package com.drugmall.controller;

import com.drugmall.common.Result;
import com.drugmall.dto.*;
import com.drugmall.service.UserService;
import com.drugmall.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/v1/user")
@Tag(name = "用户管理", description = "用户相关接口")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    private static final String CURRENT_USER_ID = "1";

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用手机号和验证码登录")
    public Result<LoginResultVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "发送手机验证码")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDTO sendCodeDTO) {
        userService.sendCode(sendCodeDTO);
        return Result.success();
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录")
    public Result<Void> logout() {
        userService.logout(CURRENT_USER_ID);
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户信息")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserInfo(CURRENT_USER_ID));
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户信息")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO updateDTO) {
        return Result.success(userService.updateUserInfo(CURRENT_USER_ID, updateDTO));
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像")
    public Result<String> uploadAvatar(@RequestBody String avatarBase64) {
        return Result.success(userService.uploadAvatar(CURRENT_USER_ID, avatarBase64));
    }

    @PostMapping("/real-name-auth")
    @Operation(summary = "实名认证", description = "提交实名认证信息")
    public Result<Void> realNameAuth(@Valid @RequestBody RealNameAuthDTO authDTO) {
        userService.realNameAuth(CURRENT_USER_ID, authDTO);
        return Result.success();
    }

    @GetMapping("/check-phone")
    @Operation(summary = "检查手机号", description = "检查手机号是否已注册")
    public Result<Boolean> checkPhone(
            @Parameter(description = "手机号") @RequestParam @NotBlank String phone) {
        return Result.success(userService.checkPhone(phone));
    }

    // ============== 就诊人管理 ==============

    @GetMapping("/patients")
    @Operation(summary = "获取就诊人列表", description = "获取当前用户的就诊人列表")
    public Result<List<PatientVO>> getPatientList() {
        return Result.success(userService.getPatientList(CURRENT_USER_ID));
    }

    @GetMapping("/patients/default")
    @Operation(summary = "获取默认就诊人", description = "获取当前用户的默认就诊人")
    public Result<PatientVO> getDefaultPatient() {
        return Result.success(userService.getDefaultPatient(CURRENT_USER_ID));
    }

    @PostMapping("/patients")
    @Operation(summary = "添加就诊人", description = "添加新的就诊人")
    public Result<PatientVO> addPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return Result.success(userService.addPatient(CURRENT_USER_ID, patientDTO));
    }

    @PutMapping("/patients/{id}")
    @Operation(summary = "更新就诊人", description = "更新就诊人信息")
    public Result<PatientVO> updatePatient(
            @Parameter(description = "就诊人ID") @PathVariable String id,
            @Valid @RequestBody PatientDTO patientDTO) {
        return Result.success(userService.updatePatient(CURRENT_USER_ID, id, patientDTO));
    }

    @DeleteMapping("/patients/{id}")
    @Operation(summary = "删除就诊人", description = "删除指定就诊人")
    public Result<Void> deletePatient(
            @Parameter(description = "就诊人ID") @PathVariable String id) {
        userService.deletePatient(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PutMapping("/patients/{id}/default")
    @Operation(summary = "设置默认就诊人", description = "设置指定就诊人为默认")
    public Result<Void> setDefaultPatient(
            @Parameter(description = "就诊人ID") @PathVariable String id) {
        userService.setDefaultPatient(CURRENT_USER_ID, id);
        return Result.success();
    }

    // ============== 地址管理 ==============

    @GetMapping("/addresses")
    @Operation(summary = "获取地址列表", description = "获取当前用户的收货地址列表")
    public Result<List<AddressVO>> getAddressList() {
        return Result.success(userService.getAddressList(CURRENT_USER_ID));
    }

    @GetMapping("/addresses/{id}")
    @Operation(summary = "获取地址详情", description = "获取指定地址详情")
    public Result<AddressVO> getAddressDetail(
            @Parameter(description = "地址ID") @PathVariable String id) {
        return Result.success(userService.getAddressDetail(CURRENT_USER_ID, id));
    }

    @GetMapping("/addresses/default")
    @Operation(summary = "获取默认地址", description = "获取当前用户的默认地址")
    public Result<AddressVO> getDefaultAddress() {
        return Result.success(userService.getDefaultAddress(CURRENT_USER_ID));
    }

    @PostMapping("/addresses")
    @Operation(summary = "添加地址", description = "添加新的收货地址")
    public Result<AddressVO> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        return Result.success(userService.addAddress(CURRENT_USER_ID, addressDTO));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "更新地址", description = "更新收货地址")
    public Result<AddressVO> updateAddress(
            @Parameter(description = "地址ID") @PathVariable String id,
            @Valid @RequestBody AddressDTO addressDTO) {
        return Result.success(userService.updateAddress(CURRENT_USER_ID, id, addressDTO));
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "删除地址", description = "删除指定地址")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable String id) {
        userService.deleteAddress(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PutMapping("/addresses/{id}/default")
    @Operation(summary = "设置默认地址", description = "设置指定地址为默认")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable String id) {
        userService.setDefaultAddress(CURRENT_USER_ID, id);
        return Result.success();
    }

    @PostMapping("/addresses/parse")
    @Operation(summary = "智能解析地址", description = "智能解析地址文本")
    public Result<AddressVO> parseAddress(@Valid @RequestBody ParseAddressDTO parseDTO) {
        return Result.success(userService.parseAddress(parseDTO.getAddressText()));
    }

    // ============== 优惠券管理 ==============

    @GetMapping("/coupons")
    @Operation(summary = "获取优惠券列表", description = "获取当前用户的优惠券列表")
    public Result<List<CouponVO>> getCouponList(
            @Parameter(description = "状态：unused-未使用，used-已使用，expired-已过期") @RequestParam(required = false) String status) {
        return Result.success(userService.getCouponList(CURRENT_USER_ID, status));
    }

    @PostMapping("/coupons/receive")
    @Operation(summary = "领取优惠券", description = "领取指定优惠券")
    public Result<Void> receiveCoupon(
            @Parameter(description = "优惠券ID") @RequestParam String couponId) {
        userService.receiveCoupon(CURRENT_USER_ID, couponId);
        return Result.success();
    }

    @GetMapping("/coupons/available")
    @Operation(summary = "获取可用优惠券", description = "获取当前订单可用的优惠券")
    public Result<List<CouponVO>> getAvailableCoupons(
            @Parameter(description = "订单金额") @RequestParam String amount) {
        return Result.success(userService.getAvailableCoupons(CURRENT_USER_ID, amount));
    }

    // ============== 浏览历史 ==============

    @GetMapping("/browse-history")
    @Operation(summary = "获取浏览历史", description = "获取药品浏览历史")
    public Result<List<BrowseHistoryVO>> getBrowseHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.getBrowseHistory(CURRENT_USER_ID, page, size));
    }

    @PostMapping("/browse-history")
    @Operation(summary = "添加浏览历史", description = "添加药品到浏览历史")
    public Result<Void> addBrowseHistory(@Valid @RequestBody BrowseHistoryDTO historyDTO) {
        userService.addBrowseHistory(CURRENT_USER_ID, historyDTO);
        return Result.success();
    }

    @DeleteMapping("/browse-history")
    @Operation(summary = "清空浏览历史", description = "清空所有浏览历史")
    public Result<Void> clearBrowseHistory() {
        userService.clearBrowseHistory(CURRENT_USER_ID);
        return Result.success();
    }
}
