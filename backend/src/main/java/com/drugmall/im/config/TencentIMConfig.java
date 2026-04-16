package com.drugmall.im.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云IM配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.im")
public class TencentIMConfig {

    /**
     * SDK应用ID
     */
    private Long sdkAppId = 1600043565L;

    /**
     * 密钥（用于UserSig签名和REST API调用）
     */
    private String secretKey;

    /**
     * 管理员UserID
     */
    private String adminIdentifier = "administrator";

    /**
     * UserSig有效期（秒），默认24小时
     */
    private Long expireTime = 86400L;

    /**
     * Mock模式开关
     * true: 使用Mock数据，生成伪UserSig
     * false: 使用真实腾讯云IM服务
     */
    private Boolean mockMode = true;
}
