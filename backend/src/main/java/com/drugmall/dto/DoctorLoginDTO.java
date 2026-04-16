package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 医生登录DTO
 */
@Data
@Schema(description = "医生登录请求参数")
public class DoctorLoginDTO {

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", required = true, example = "13900001234")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;
}
