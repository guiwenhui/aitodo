package com.example.aitodo.service;

/**
 * 大模型（LLM）服务接口
 * <p>
 * 定义与大语言模型交互的核心方法。
 * 可对接阿里云通义千问、DeepSeek、OpenAI 等任意大模型 API。
 * </p>
 *
 * @author AI Todo Team
 */
public interface LlmService {

    /**
     * 调用大模型 API，发送 Prompt 并获取模型回复
     *
     * @param prompt 发送给大模型的完整 Prompt（包含 System Prompt + 用户输入）
     * @return 大模型返回的原始文本内容
     * @throws RuntimeException 当调用超时或网络异常时抛出
     */
    String callAi(String prompt);
}
