package com.example.aitodo.service.impl;

import com.example.aitodo.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 大模型服务实现类
 * <p>
 * 支持对接阿里云通义千问 / DeepSeek / OpenAI 等兼容 OpenAI Chat Completions 格式的 API。
 * 默认对接通义千问（DashScope）的 OpenAI 兼容接口。
 * </p>
 *
 * <h3>配置方式（application.properties）：</h3>
 * <pre>
 * # 大模型 API 密钥（必填，替换为你自己的 Key）
 * llm.api-key=sk-xxxxxxxxxxxxxxxxxxxxxxxx
 *
 * # 大模型 API 地址（可选，默认为通义千问）
 * llm.api-url=https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
 *
 * # 使用的模型名称（可选，默认 qwen-turbo）
 * llm.model=qwen-turbo
 * </pre>
 *
 * @author AI Todo Team
 */
@Service
public class LlmServiceImpl implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmServiceImpl.class);

    /**
     * 大模型 API 地址
     * 支持通义千问、DeepSeek 等兼容 OpenAI 格式的接口：
     * - 通义千问：https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
     * - DeepSeek：https://api.deepseek.com/v1/chat/completions
     * - OpenAI：https://api.openai.com/v1/chat/completions
     */
    @Value("${llm.api-url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;

    /**
     * API 密钥（从配置文件读取）
     */
    @Value("${llm.api-key:}")
    private String apiKey;

    /**
     * 模型名称
     * - 通义千问：qwen-turbo / qwen-plus / qwen-max
     * - DeepSeek：deepseek-chat
     * - OpenAI：gpt-3.5-turbo / gpt-4
     */
    @Value("${llm.model:qwen-turbo}")
    private String model;

    /**
     * 调用大模型 API
     * <p>
     * 使用 OpenAI Chat Completions 兼容格式发送请求。
     * 请求体格式：
     * <pre>
     * {
     *   "model": "qwen-turbo",
     *   "messages": [
     *     {"role": "user", "content": "用户的Prompt内容"}
     *   ],
     *   "temperature": 0.3
     * }
     * </pre>
     * </p>
     *
     * @param prompt 完整的 Prompt 文本（已包含 System 指令和用户输入）
     * @return 大模型返回的文本内容（从 choices[0].message.content 中提取）
     * @throws RuntimeException 当 API 调用失败时抛出异常
     */
    @Override
    public String callAi(String prompt) {
        // ======================== 参数校验 ========================
        if (apiKey == null || apiKey.isBlank()) {
            log.error("【LLM】API Key 未配置，请在 application.properties 中设置 llm.api-key");
            throw new RuntimeException("大模型 API Key 未配置，请联系管理员");
        }

        log.info("【LLM】开始调用大模型，模型：{}，API地址：{}", model, apiUrl);
        log.debug("【LLM】Prompt 内容：{}", prompt);

        try {
            // ======================== 1. 构建请求体 ========================
            // 对 prompt 内容进行 JSON 转义（处理换行符、引号等特殊字符）
            String escapedPrompt = escapeJsonString(prompt);

            String requestBody = "{"
                    + "\"model\": \"" + model + "\","
                    + "\"messages\": ["
                    + "  {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}"
                    + "],"
                    + "\"temperature\": 0.3"  // 低温度，确保输出更稳定、更结构化
                    + "}";

            // ======================== 2. 发送 HTTP 请求 ========================
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setConnectTimeout(15000);  // 连接超时 15 秒
            conn.setReadTimeout(60000);     // 读取超时 60 秒（大模型生成较慢）
            conn.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // ======================== 3. 读取响应 ========================
            int responseCode = conn.getResponseCode();
            log.info("【LLM】API 响应状态码：{}", responseCode);

            // 读取响应内容（成功或失败都要读取）
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            conn.disconnect();

            String responseJson = responseBuilder.toString();
            log.debug("【LLM】API 原始响应：{}", responseJson);

            // ======================== 4. 处理错误响应 ========================
            if (responseCode < 200 || responseCode >= 300) {
                log.error("【LLM】API 调用失败，状态码：{}，响应：{}", responseCode, responseJson);
                throw new RuntimeException("大模型 API 调用失败（状态码：" + responseCode + "）");
            }

            // ======================== 5. 解析响应，提取 content ========================
            // 简单解析：从 OpenAI 兼容格式中提取 choices[0].message.content
            String content = extractContentFromResponse(responseJson);
            log.info("【LLM】大模型返回内容：{}", content);

            return content;

        } catch (RuntimeException e) {
            // 直接重新抛出已知的 RuntimeException
            throw e;
        } catch (Exception e) {
            log.error("【LLM】调用大模型时发生异常：{}", e.getMessage(), e);
            throw new RuntimeException("调用大模型服务失败：" + e.getMessage(), e);
        }
    }

    /**
     * 从 OpenAI 兼容格式的 JSON 响应中提取 content 字段
     * <p>
     * 响应格式示例：
     * <pre>
     * {
     *   "choices": [{
     *     "message": {
     *       "content": "这里是大模型返回的文本"
     *     }
     *   }]
     * }
     * </pre>
     * </p>
     *
     * @param responseJson 原始 JSON 响应字符串
     * @return 提取出的 content 文本
     */
    private String extractContentFromResponse(String responseJson) {
        try {
            // 使用简单字符串查找方式提取 content（避免引入额外 JSON 库依赖）
            // 查找 "content" 字段
            String contentKey = "\"content\"";
            int contentIndex = responseJson.lastIndexOf(contentKey);
            if (contentIndex == -1) {
                log.warn("【LLM】响应中未找到 content 字段");
                throw new RuntimeException("大模型返回格式异常：未找到 content 字段");
            }

            // 定位到 content 值的起始引号
            int colonIndex = responseJson.indexOf(':', contentIndex + contentKey.length());
            int startQuote = responseJson.indexOf('"', colonIndex + 1);
            if (startQuote == -1) {
                throw new RuntimeException("大模型返回格式异常：content 值解析失败");
            }

            // 查找结束引号（需要处理转义的引号）
            StringBuilder content = new StringBuilder();
            int i = startQuote + 1;
            while (i < responseJson.length()) {
                char c = responseJson.charAt(i);
                if (c == '\\' && i + 1 < responseJson.length()) {
                    // 处理转义字符
                    char next = responseJson.charAt(i + 1);
                    switch (next) {
                        case '"': content.append('"'); break;
                        case '\\': content.append('\\'); break;
                        case 'n': content.append('\n'); break;
                        case 'r': content.append('\r'); break;
                        case 't': content.append('\t'); break;
                        default: content.append('\\').append(next); break;
                    }
                    i += 2;
                } else if (c == '"') {
                    // 未转义的引号，表示字符串结束
                    break;
                } else {
                    content.append(c);
                    i++;
                }
            }

            return content.toString().trim();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("【LLM】解析响应内容失败：{}", e.getMessage());
            throw new RuntimeException("解析大模型响应失败", e);
        }
    }

    /**
     * 对字符串进行 JSON 转义
     * 处理换行符、制表符、引号、反斜杠等特殊字符
     *
     * @param input 原始字符串
     * @return 转义后的字符串（可安全嵌入 JSON 值中）
     */
    private String escapeJsonString(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:   sb.append(c); break;
            }
        }
        return sb.toString();
    }
}
