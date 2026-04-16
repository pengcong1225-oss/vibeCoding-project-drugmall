package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方详情VO
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetailVO {

    /** 处方ID */
    private String id;

    /** 处方编号 */
    private String prescriptionNo;

    /** 患者姓名 */
    private String patientName;

    /** 患者年龄 */
    private Integer patientAge;

    /** 患者性别 */
    private String patientGender;

    /** 诊断结果 */
    private String diagnosis;

    /** 医生姓名 */
    private String doctorName;

    /** 医生职称 */
    private String doctorTitle;

    /** 科室 */
    private String department;

    /** 就诊医院 */
    private String hospital;

    /** 处方状态 */
    private String status;

    /** 状态文本 */
    private String statusText;

    /** 开具时间 */
    private LocalDateTime createTime;

    /** 有效期至 */
    private LocalDateTime expireTime;

    /** 药品数量 */
    private Integer drugCount;

    /** 主诉症状 */
    private String chiefComplaint;

    /** 病史 */
    private String medicalHistory;

    /** 过敏史 */
    private String allergyHistory;

    /** 用法用量说明 */
    private String usageInstruction;

    /** 注意事项 */
    private String precautions;

    /** 医生建议 */
    private String doctorAdvice;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 审核药师 */
    private String reviewerName;

    /** 药品明细 */
    List<PrescriptionDrugItem> drugs;

    /** 总金额 */
    private BigDecimal totalAmount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionDrugItem {
        /** 药品ID */
        private String drugId;

        /** 药品名称 */
        private String drugName;

        /** 规格 */
        private String specification;

        /** 数量 */
        private Integer quantity;

        /** 单位 */
        private String unit;

        /** 单价 */
        private BigDecimal price;

        /** 小计金额 */
        private BigDecimal subtotal;

        /** 用法用量 */
        private String usage;

        /** 是否处方药 */
        private Boolean isRx;

        /** 生产厂家 */
        private String manufacturer;
    }
}
