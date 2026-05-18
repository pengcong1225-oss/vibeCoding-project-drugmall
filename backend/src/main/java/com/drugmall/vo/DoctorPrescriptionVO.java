package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 医生端处方详情VO
 */
@Data
@Schema(description = "医生端处方详情")
public class DoctorPrescriptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "处方ID")
    private String id;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者年龄")
    private Integer patientAge;

    @Schema(description = "患者性别")
    private String patientGender;

    @Schema(description = "问诊ID")
    private String consultationId;

    @Schema(description = "诊断")
    private String diagnosis;

    @Schema(description = "药品列表")
    private List<DrugItemVO> drugs;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "状态: pending/approved/rejected/cancelled")
    private String status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "审核药师")
    private String pharmacist;

    @Schema(description = "审核时间")
    private String reviewTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "问诊状态")
    private String consultationStatus;

    @Schema(description = "问诊症状描述")
    private String consultationSymptom;

    @Schema(description = "问诊类型")
    private String consultationType;

    @Data
    @Schema(description = "处方药品")
    public static class DrugItemVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "药品ID")
        private String id;

        @Schema(description = "药品名称")
        private String name;

        @Schema(description = "规格")
        private String spec;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "单价")
        private BigDecimal price;

        @Schema(description = "数量")
        private Integer quantity;

        @Schema(description = "用量")
        private String dosage;

        @Schema(description = "频次")
        private String frequency;

        @Schema(description = "疗程")
        private String duration;

        @Schema(description = "备注")
        private String remark;
    }
}
