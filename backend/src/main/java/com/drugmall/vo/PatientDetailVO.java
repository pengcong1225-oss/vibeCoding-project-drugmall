package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 患者详情VO
 */
@Data
@Schema(description = "患者详情")
public class PatientDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "患者ID")
    private String id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "标签")
    private List<String> tags;

    @Schema(description = "诊断")
    private List<String> diagnosis;

    @Schema(description = "最后就诊时间")
    private String lastVisit;

    @Schema(description = "就诊次数")
    private Integer visitCount;

    @Schema(description = "是否VIP")
    private Boolean isVip;

    @Schema(description = "过敏史")
    private String allergies;

    @Schema(description = "病史")
    private String medicalHistory;
}
