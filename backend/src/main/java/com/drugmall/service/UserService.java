package com.drugmall.service;

import com.drugmall.dto.*;
import com.drugmall.vo.*;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    LoginResultVO login(LoginDTO loginDTO);

    /**
     * 发送验证码
     */
    void sendCode(SendCodeDTO sendCodeDTO);

    /**
     * 用户登出
     */
    void logout(String userId);

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(String userId);

    /**
     * 更新用户信息
     */
    UserInfoVO updateUserInfo(String userId, UpdateUserInfoDTO updateDTO);

    /**
     * 上传头像
     */
    String uploadAvatar(String userId, org.springframework.web.multipart.MultipartFile file);

    /**
     * 实名认证
     */
    void realNameAuth(String userId, RealNameAuthDTO authDTO);

    /**
     * 检查手机号是否已注册
     */
    Boolean checkPhone(String phone);

    /**
     * 获取就诊人列表
     */
    List<PatientVO> getPatientList(String userId);

    /**
     * 获取默认就诊人
     */
    PatientVO getDefaultPatient(String userId);

    /**
     * 添加就诊人
     */
    PatientVO addPatient(String userId, PatientDTO patientDTO);

    /**
     * 更新就诊人
     */
    PatientVO updatePatient(String userId, String patientId, PatientDTO patientDTO);

    /**
     * 删除就诊人
     */
    void deletePatient(String userId, String patientId);

    /**
     * 设置默认就诊人
     */
    void setDefaultPatient(String userId, String patientId);

    /**
     * 获取地址列表
     */
    List<AddressVO> getAddressList(String userId);

    /**
     * 获取地址详情
     */
    AddressVO getAddressDetail(String userId, String addressId);

    /**
     * 获取默认地址
     */
    AddressVO getDefaultAddress(String userId);

    /**
     * 添加地址
     */
    AddressVO addAddress(String userId, AddressDTO addressDTO);

    /**
     * 更新地址
     */
    AddressVO updateAddress(String userId, String addressId, AddressDTO addressDTO);

    /**
     * 删除地址
     */
    void deleteAddress(String userId, String addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(String userId, String addressId);

    /**
     * 智能解析地址
     */
    AddressVO parseAddress(String addressText);

    /**
     * 获取优惠券列表
     */
    List<CouponVO> getCouponList(String userId, String status);

    /**
     * 领取优惠券
     */
    void receiveCoupon(String userId, String couponId);

    /**
     * 获取可用优惠券
     */
    List<CouponVO> getAvailableCoupons(String userId, String amount);

    /**
     * 获取浏览历史
     */
    List<BrowseHistoryVO> getBrowseHistory(String userId, Integer page, Integer size);

    /**
     * 获取浏览历史总数
     */
    Long getBrowseHistoryCount(String userId);

    /**
     * 添加到浏览历史
     */
    void addBrowseHistory(String userId, BrowseHistoryDTO historyDTO);

    /**
     * 清空浏览历史
     */
    void clearBrowseHistory(String userId);
}
