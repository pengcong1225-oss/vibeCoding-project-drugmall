package com.drugmall.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统错误"),
    
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PHONE_ALREADY_REGISTERED(1003, "手机号已注册"),
    LOGIN_FAILED(1004, "登录失败"),
    VERIFY_CODE_ERROR(1005, "验证码错误"),
    
    DRUG_NOT_FOUND(2001, "药品不存在"),
    DRUG_OFF_SHELF(2002, "药品已下架"),
    DRUG_STOCK_NOT_ENOUGH(2003, "药品库存不足"),
    
    CART_ITEM_NOT_FOUND(3001, "购物车商品不存在"),
    CART_EMPTY(3002, "购物车为空"),
    
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态错误"),
    ORDER_ALREADY_PAID(4003, "订单已支付"),
    ORDER_ALREADY_CANCELLED(4004, "订单已取消"),
    ORDER_CANNOT_CANCEL(4005, "订单无法取消"),
    
    ADDRESS_NOT_FOUND(5001, "地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(5002, "地址数量已达上限"),
    
    PRESCRIPTION_NOT_FOUND(6001, "处方不存在"),
    PRESCRIPTION_EXPIRED(6002, "处方已过期"),
    PRESCRIPTION_USED(6003, "处方已使用"),

    DOCTOR_NOT_FOUND(7001, "医生不存在"),
    DOCTOR_LOGIN_FAILED(7002, "医生登录失败"),
    DOCTOR_NOT_CERTIFIED(7003, "医生未认证"),
    CONSULTATION_NOT_FOUND(7004, "问诊不存在"),
    CONSULTATION_STATUS_ERROR(7005, "问诊状态错误"),
    CONSULTATION_ALREADY_CLOSED(7006, "问诊已结束"),
    PATIENT_NOT_FOUND(7007, "患者不存在"),
    PRESCRIPTION_STATUS_ERROR(7008, "处方状态错误"),
    PRESCRIPTION_DRUG_EMPTY(7009, "处方药品不能为空"),
    INSUFFICIENT_BALANCE(7010, "余额不足"),
    WITHDRAW_AMOUNT_ERROR(7011, "提现金额错误"),
    SCHEDULE_NOT_FOUND(7012, "排班信息不存在"),
    LICENSE_NOT_FOUND(7013, "执业信息不存在"),

    IM_USER_SIG_ERROR(7014, "UserSig生成失败"),
    IM_CONVERSATION_NOT_FOUND(7015, "会话不存在"),
    IM_MESSAGE_SEND_FAILED(7016, "消息发送失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
