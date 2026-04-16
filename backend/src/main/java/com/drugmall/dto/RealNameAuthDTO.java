package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 实名认证DTO
 */
@Data
@Schema(description = "实名认证请求参数")
public class RealNameAuthDTO {

    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名", required = true, example = "张三")
    private String realName;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Schema(description = "身份证号", required = true, example = "110101199001011234")
    private String idCard;
}
