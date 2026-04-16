package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 问诊列表项VO
 */
@Data
@Schema(description = "问诊列表项")
public class ConsultationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "问诊ID")
    private String id;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者年龄")
    private Integer patientAge;

    @Schema(description = "患者性别")
    private String patientGender;

    @Schema(description = "患者头像")
    private String patientAvatar;

    @Schema(description = "问诊类型: 图文问诊/视频问诊/复诊")
    private String type;

    @Schema(description = "状态: pending/processing/completed/closed")
    private String status;

    @Schema(description = "症状描述")
    private String symptom;

    @Schema(description = "等待时间")
    private String waitTime;

    @Schema(description = "剩余时间")
    private String remainingTime;

    @Schema(description = "是否紧急")
    private Boolean isUrgent;

    @Schema(description = "是否需要处方")
    private Boolean isRx;

    @Schema(description = "创建时间")
    private String createTime;
}
