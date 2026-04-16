package com.drugmall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI助手配置类
 * 配置百川大模型API相关参数
 *
 * @author DrugMall Team
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.baichuan")
public class AIConfig {

    /**
     * 百川API密钥
     */
    private String apiKey;

    /**
     * 百川API地址
     */
    private String apiUrl = "https://api.baichuan-ai.com/v1/chat/completions";

    /**
     * 使用的模型名称
     */
    private String model = "Baichuan4-Turbo";

    /**
     * 温度参数，控制回答的多样性
     */
    private Double temperature = 0.3;

    /**
     * Top-P参数
     */
    private Double topP = 0.85;

    /**
     * Top-K参数
     */
    private Integer topK = 5;

    /**
     * 最大Token数
     */
    private Integer maxTokens = 2048;

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 60000;

    /**
     * 系统提示词
     */
    private String systemPrompt = "你是一位专业的医药健康助手，为用户提供药品咨询、用药指导和健康建议。" +
            "请遵循以下原则：\n" +
            "1. 提供准确、专业的医疗健康信息\n" +
            "2. 对于处方药，必须提醒用户需要医生开具处方\n" +
            "3. 建议仅供参考，不能替代医生诊断\n" +
            "4. 紧急情况建议用户及时就医\n" +
            "5. 回答要简洁明了，易于理解";
}
