package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 就诊人VO
 */
@Data
@Schema(description = "就诊人信息")
public class PatientVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "就诊人ID")
    private String id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别：1-男，2-女")
    private Integer gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "生日")
    private String birthday;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "关系")
    private String relationship;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "病史")
    private String medicalHistory;

    @Schema(description = "是否默认")
    private Boolean isDefault;
}
