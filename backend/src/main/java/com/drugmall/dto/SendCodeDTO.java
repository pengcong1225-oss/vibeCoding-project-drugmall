package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 发送验证码DTO
 */
@Data
@Schema(description = "发送验证码请求参数")
public class SendCodeDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @Schema(description = "验证码类型：login-登录，register-注册，reset-重置密码", example = "login")
    private String type;
}
