package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 就诊人DTO
 */
@Data
@Schema(description = "就诊人请求参数")
public class PatientDTO {

    @Schema(description = "就诊人ID（更新时必填）", example = "1")
    private String id;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", required = true, example = "张三")
    private String name;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别：1-男，2-女", required = true, example = "1")
    private Integer gender;

    @NotNull(message = "年龄不能为空")
    @Schema(description = "年龄", required = true, example = "30")
    private Integer age;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Schema(description = "身份证号", required = true, example = "110101199001011234")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @NotBlank(message = "关系不能为空")
    @Schema(description = "关系：本人、配偶、父母、子女、其他", required = true, example = "本人")
    private String relationship;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "地址", example = "北京市朝阳区某某小区")
    private String address;

    @Schema(description = "过敏史", example = "青霉素过敏")
    private String allergyHistory;

    @Schema(description = "病史", example = "高血压")
    private String medicalHistory;

    @Schema(description = "是否设为默认", example = "true")
    private Boolean isDefault;
}
