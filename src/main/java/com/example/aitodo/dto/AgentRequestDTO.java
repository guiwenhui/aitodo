package com.example.aitodo.dto;

/**
 * AI Agent 请求数据传输对象
 * <p>
 * 用于接收前端发送的自然语言任务创建请求。
 * 用户在前端输入一段自然语言文字（如"今天八点前洗完澡，九点前写完作业"），
 * 前端将其封装为该 DTO 发送到后端。
 * </p>
 *
 * @author AI Todo Team
 */
public class AgentRequestDTO {

    /**
     * 用户输入的自然语言文本
     * 例如："今天的todo是 八点前洗完澡，九点前写完作业"
     */
    private String userInput;

    // ==================== 构造函数 ====================

    public AgentRequestDTO() {
    }

    public AgentRequestDTO(String userInput) {
        this.userInput = userInput;
    }

    // ==================== Getter & Setter ====================

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    @Override
    public String toString() {
        return "AgentRequestDTO{" +
                "userInput='" + userInput + '\'' +
                '}';
    }
}
