package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建处方DTO
 */
@Data
@Schema(description = "创建处方请求参数")
public class CreatePrescriptionDTO {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者年龄")
    private Integer patientAge;

    @Schema(description = "患者性别")
    private String patientGender;

    @Schema(description = "问诊ID")
    private String consultationId;

    @NotBlank(message = "诊断不能为空")
    @Schema(description = "诊断", required = true)
    private String diagnosis;

    @NotEmpty(message = "处方药品不能为空")
    @Schema(description = "药品列表", required = true)
    private List<PrescriptionDrugDTO> drugs;

    @Data
    @Schema(description = "处方药品")
    public static class PrescriptionDrugDTO {
        @Schema(description = "药品ID")
        private String id;

        @NotBlank(message = "药品名称不能为空")
        @Schema(description = "药品名称", required = true)
        private String name;

        @Schema(description = "规格", example = "0.3g*20粒")
        private String spec;

        @Schema(description = "单位", example = "盒")
        private String unit;

        @Schema(description = "单价")
        private BigDecimal price;

        @Schema(description = "数量")
        private Integer quantity;

        @Schema(description = "用量", example = "1粒")
        private String dosage;

        @Schema(description = "频次", example = "每日2次")
        private String frequency;

        @Schema(description = "疗程", example = "3天")
        private String duration;

        @Schema(description = "备注")
        private String remark;
    }
}
