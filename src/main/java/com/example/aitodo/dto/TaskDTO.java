package com.example.aitodo.dto;

/**
 * 任务数据传输对象 —— 用于接收大模型返回的结构化任务信息
 * <p>
 * 大模型返回的 JSON 数组中的每个元素将被反序列化为该 DTO。
 * 字段说明：
 * - title：任务标题（必填，由 LLM 从用户自然语言中提取）
 * - deadline：截止时间（格式："yyyy-MM-dd HH:mm:ss"，由 LLM 根据上下文推断）
 * - status：任务状态（0:进行中，默认值；通常由后端自动设置为 0）
 * </p>
 *
 * @author AI Todo Team
 */
public class TaskDTO {

    /** 任务标题 */
    private String title;

    /** 截止时间，格式：yyyy-MM-dd HH:mm:ss */
    private String deadline;

    /** 任务状态，默认 0（进行中） */
    private Integer status;

    // ==================== 构造函数 ====================

    public TaskDTO() {
    }

    public TaskDTO(String title, String deadline, Integer status) {
        this.title = title;
        this.deadline = deadline;
        this.status = status;
    }

    // ==================== Getter & Setter ====================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "title='" + title + '\'' +
                ", deadline='" + deadline + '\'' +
                ", status=" + status +
                '}';
    }
}
