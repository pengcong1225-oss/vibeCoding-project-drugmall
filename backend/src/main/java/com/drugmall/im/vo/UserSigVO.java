package com.drugmall.im.vo;

import lombok.Data;

/**
 * UserSig响应VO
 */
@Data
public class UserSigVO {

    /**
     * IM用户ID
     */
    private String userId;

    /**
     * UserSig签名
     */
    private String userSig;

    /**
     * SDK应用ID
     */
    private Long sdkAppId;

    /**
     * 过期时间（秒）
     */
    private Long expireTime;
}
