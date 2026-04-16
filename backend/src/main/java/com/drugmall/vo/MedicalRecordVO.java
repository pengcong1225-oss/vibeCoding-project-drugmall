package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 病历记录VO
 */
@Data
@Schema(description = "病历记录")
public class MedicalRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "病历ID")
    private String id;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "就诊日期")
    private String date;

    @Schema(description = "类型: 初诊/复诊")
    private String type;

    @Schema(description = "诊断")
    private String diagnosis;

    @Schema(description = "处方")
    private String prescription;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "医生")
    private String doctor;
}
