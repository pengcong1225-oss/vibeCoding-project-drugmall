package com.drugmall.im.service;

/**
 * IM UserSig生成服务接口
 */
public interface IMUserSigService {

    /**
     * 生成UserSig
     * @param userId 用户ID（业务ID，如患者ID、医生ID）
     * @param userType 用户类型（patient/doctor）
     * @return UserSig签名
     */
    String generateUserSig(String userId, String userType);

    /**
     * 生成IM用户ID
     * @param userType 用户类型（patient/doctor）
     * @param businessId 业务用户ID
     * @return IM用户ID（格式：{userType}_{businessId}）
     */
    String generateIMUserId(String userType, String businessId);
}
