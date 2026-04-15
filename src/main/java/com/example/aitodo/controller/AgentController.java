package com.example.aitodo.controller;

import com.example.aitodo.common.Result;
import com.example.aitodo.dto.AgentRequestDTO;
import com.example.aitodo.dto.TaskDTO;
import com.example.aitodo.entity.Task;
import com.example.aitodo.service.LlmService;
import com.example.aitodo.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Agent 控制器 —— 自然语言创建任务
 * <p>
 * 核心职责：
 * 1. 接收用户的自然语言输入
 * 2. 拼接系统时间 + 精心设计的 Prompt，发送给大模型（LLM）
 * 3. 解析大模型返回的结构化 JSON，转为任务列表
 * 4. 批量存入数据库
 * </p>
 *
 * @author AI Todo Team
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    /** 大模型服务 */
    private final LlmService llmService;

    /** 任务服务 */
    private final TaskService taskService;

    /** JSON 解析器（Jackson） */
    private final ObjectMapper objectMapper;

    // ==================== 日期格式化器（支持多种格式） ====================
    private static final DateTimeFormatter FORMATTER_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_NO_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ==================== 构造函数注入 ====================
    public AgentController(LlmService llmService, TaskService taskService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.taskService = taskService;
        this.objectMapper = objectMapper;
    }

    /**
     * AI Agent 自然语言创建任务接口
     * <p>
     * 用户输入一句话，例如："今天的todo是 八点前洗完澡，九点前写完作业"
     * 后端调用大模型提取结构化任务列表，并自动存入数据库。
     * </p>
     *
     * @param requestDTO 前端请求体，包含用户的自然语言输入
     * @param session    HttpSession，用于获取当前登录用户 ID
     * @return Result<List<Task>> 成功时返回创建的任务列表
     */
    @PostMapping("/create-tasks")
    public Result<?> createTasksByAi(@RequestBody AgentRequestDTO requestDTO, HttpSession session) {

        // ======================== 1. 校验用户登录状态 ========================
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("【AI Agent】未登录用户尝试调用 AI 创建任务");
            return Result.error(401, "请先登录后再使用 AI 助手");
        }

        // ======================== 2. 校验用户输入 ========================
        String userInput = requestDTO.getUserInput();
        if (userInput == null || userInput.trim().isEmpty()) {
            return Result.error("请输入任务内容，例如：'明天下午三点前完成报告'");
        }

        // 输入长度限制（防止恶意超长输入）
        if (userInput.length() > 500) {
            return Result.error("输入内容过长，请控制在 500 字以内");
        }

        log.info("【AI Agent】用户 {} 发起 AI 创建任务请求，输入：{}", userId, userInput);

        try {
            // ======================== 3. 构建 Prompt ========================
            String prompt = buildPrompt(userInput);
            log.debug("【AI Agent】构建的 Prompt：\n{}", prompt);

            // ======================== 4. 调用大模型 ========================
            String aiResponse = llmService.callAi(prompt);
            log.info("【AI Agent】大模型返回原始内容：{}", aiResponse);

            // ======================== 5. 清洗 AI 返回内容 ========================
            String cleanedJson = cleanAiResponse(aiResponse);
            log.info("【AI Agent】清洗后的 JSON：{}", cleanedJson);

            // ======================== 6. 解析 JSON 为 TaskDTO 列表 ========================
            List<TaskDTO> taskDTOList = parseTaskDTOList(cleanedJson);

            // 校验：检查是否提取到了任务
            if (taskDTOList == null || taskDTOList.isEmpty()) {
                log.warn("【AI Agent】大模型未提取到任何任务，用户输入：{}", userInput);
                return Result.error("AI 未从你的输入中识别到任务，请描述更具体一些。例如：'明天下午两点前写完报告'");
            }

            // ======================== 7. 转换并存入数据库 ========================
            List<Task> createdTasks = new ArrayList<>();
            for (TaskDTO dto : taskDTOList) {
                // 校验每个任务的标题不能为空
                if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                    log.warn("【AI Agent】跳过空标题任务：{}", dto);
                    continue;
                }

                Task task = convertToTask(dto, userId);
                Task savedTask = taskService.createTask(task);
                createdTasks.add(savedTask);
                log.info("【AI Agent】成功创建任务：{}", savedTask.getTitle());
            }

            // 校验：是否有任务成功入库
            if (createdTasks.isEmpty()) {
                return Result.error("AI 提取到的任务数据异常，请重新描述你的任务");
            }

            // ======================== 8. 返回成功结果 ========================
            String successMsg = String.format("AI 助手成功创建了 %d 个任务！", createdTasks.size());
            log.info("【AI Agent】用户 {} 通过 AI 成功创建 {} 个任务", userId, createdTasks.size());
            return Result.success(successMsg, createdTasks);

        } catch (JsonProcessingException e) {
            // JSON 解析异常（大模型返回了非 JSON 格式的内容）
            log.error("【AI Agent】JSON 解析失败：{}", e.getMessage(), e);
            return Result.error("AI 返回的内容格式异常，请稍后重试或换一种方式描述任务");

        } catch (RuntimeException e) {
            // 大模型调用超时、网络异常等
            log.error("【AI Agent】任务创建失败：{}", e.getMessage(), e);
            return Result.error("AI 服务暂时不可用，请稍后重试（" + e.getMessage() + "）");

        } catch (Exception e) {
            // 兜底：捕获所有未预期的异常
            log.error("【AI Agent】未预期的异常：{}", e.getMessage(), e);
            return Result.error("系统内部错误，请稍后重试");
        }
    }

    // ==================================================================================
    //                              私有辅助方法
    // ==================================================================================

    /**
     * 构建发送给大模型的 Prompt
     * <p>
     * 设计要点：
     * 1. 注入当前服务器时间，让大模型理解"今天"、"明天"等相对时间
     * 2. 严格规定输出格式：纯 JSON 数组，不带 markdown 标记
     * 3. 给出示例，引导大模型输出规范的结构化数据
     * 4. 处理 Edge Case：输入不含任务时返回空数组
     * </p>
     *
     * @param userInput 用户的自然语言输入
     * @return 完整的 Prompt 字符串
     */
    private String buildPrompt(String userInput) {
        // 获取当前服务器时间（包含日期、星期，帮助大模型理解时间语境）
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));
        String dayOfWeek = now.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINESE);

        return """
                你是一个专业的任务提取助手。你的职责是从用户的自然语言输入中提取出结构化的任务列表。
                
                【重要时间信息】
                当前时间：%s（%s）
                
                【你的任务】
                请从用户输入中提取所有任务，并以 JSON 数组格式返回。每个任务包含以下字段：
                - title（字符串）：任务标题，简洁明确
                - deadline（字符串）：截止时间，格式必须为 "yyyy-MM-dd HH:mm:ss"
                - status（整数）：固定为 0（表示进行中）
                
                【输出规则 - 极其重要，必须严格遵守】
                1. 只输出纯 JSON 数组，不要输出任何其他文字
                2. 不要使用 markdown 代码块标记（不要输出 ```json 或 ```）
                3. 如果用户输入中没有任何可识别的任务，返回空数组 []
                4. 时间推断规则：
                   - "今天八点" → 当天 20:00:00
                   - "明天下午三点" → 明天 15:00:00
                   - "下周一" → 下一个周一 23:59:59
                   - 如果没有明确时间，默认设为当天 23:59:59
                5. 区分上午和下午：中文语境中 "八点" 通常指上午 08:00 或晚上 20:00，请根据任务内容合理推断
                
                【示例】
                用户输入：今天的todo是 八点前洗完澡，九点前写完作业
                输出：
                [{"title":"洗完澡","deadline":"%s 20:00:00","status":0},{"title":"写完作业","deadline":"%s 21:00:00","status":0}]
                
                【用户输入】
                %s
                """.formatted(
                currentTime,
                dayOfWeek,
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                userInput
        );
    }

    /**
     * 清洗大模型返回的内容
     * <p>
     * 大模型有时会在 JSON 外面包裹 markdown 代码块标记或多余文字，
     * 此方法负责提取出纯净的 JSON 数组。
     * </p>
     *
     * @param aiResponse 大模型的原始返回内容
     * @return 清洗后的纯 JSON 字符串
     */
    private String cleanAiResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.isBlank()) {
            return "[]";
        }

        String cleaned = aiResponse.trim();

        // 处理 markdown 代码块：```json ... ``` 或 ``` ... ```
        if (cleaned.contains("```")) {
            // 提取代码块内的内容
            int startMark = cleaned.indexOf("```");
            int contentStart = cleaned.indexOf('\n', startMark);
            if (contentStart == -1) {
                contentStart = startMark + 3;
            } else {
                contentStart += 1;
            }
            int endMark = cleaned.lastIndexOf("```");
            if (endMark > startMark && endMark > contentStart) {
                cleaned = cleaned.substring(contentStart, endMark).trim();
            }
        }

        // 尝试提取 JSON 数组部分（从第一个 [ 到最后一个 ]）
        int arrayStart = cleaned.indexOf('[');
        int arrayEnd = cleaned.lastIndexOf(']');
        if (arrayStart != -1 && arrayEnd > arrayStart) {
            cleaned = cleaned.substring(arrayStart, arrayEnd + 1);
        }

        return cleaned;
    }

    /**
     * 将清洗后的 JSON 字符串解析为 TaskDTO 列表
     *
     * @param json 纯净的 JSON 数组字符串
     * @return TaskDTO 列表
     * @throws JsonProcessingException 当 JSON 格式非法时抛出
     */
    private List<TaskDTO> parseTaskDTOList(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<List<TaskDTO>>() {});
    }

    /**
     * 将 TaskDTO 转换为 Task 实体，并设置用户 ID 和默认值
     *
     * @param dto    大模型返回的任务 DTO
     * @param userId 当前登录用户 ID
     * @return Task 实体对象
     */
    private Task convertToTask(TaskDTO dto, Long userId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(dto.getTitle().trim());
        task.setStatus(0);  // 默认：进行中
        task.setCreatedAt(LocalDateTime.now());

        // 解析截止时间（支持多种格式，做好容错处理）
        if (dto.getDeadline() != null && !dto.getDeadline().trim().isEmpty()) {
            task.setDeadline(parseDeadline(dto.getDeadline().trim()));
        } else {
            // 未提供截止时间，默认设为 3 天后
            task.setDeadline(LocalDateTime.now().plusDays(3));
            log.info("【AI Agent】任务 '{}' 未提供截止时间，默认设为 3 天后", dto.getTitle());
        }

        return task;
    }

    /**
     * 解析截止时间字符串，支持多种日期格式
     * <p>
     * 尝试顺序：
     * 1. yyyy-MM-dd HH:mm:ss
     * 2. yyyy-MM-dd HH:mm
     * 3. ISO 8601 格式（yyyy-MM-ddTHH:mm:ss）
     * 4. 全部失败时返回 3 天后作为兜底
     * </p>
     *
     * @param deadlineStr 截止时间字符串
     * @return 解析后的 LocalDateTime
     */
    private LocalDateTime parseDeadline(String deadlineStr) {
        // 尝试格式 1：yyyy-MM-dd HH:mm:ss
        try {
            return LocalDateTime.parse(deadlineStr, FORMATTER_FULL);
        } catch (DateTimeParseException ignored) {
        }

        // 尝试格式 2：yyyy-MM-dd HH:mm
        try {
            return LocalDateTime.parse(deadlineStr, FORMATTER_NO_SEC);
        } catch (DateTimeParseException ignored) {
        }

        // 尝试格式 3：ISO 格式
        try {
            return LocalDateTime.parse(deadlineStr, FORMATTER_ISO);
        } catch (DateTimeParseException ignored) {
        }

        // 全部失败，使用默认值
        log.warn("【AI Agent】无法解析截止时间：'{}'，使用默认值（3天后）", deadlineStr);
        return LocalDateTime.now().plusDays(3);
    }
}
