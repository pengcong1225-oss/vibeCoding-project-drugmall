package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 处方药申请DTO
 */
@Data
@Schema(description = "处方药申请请求参数")
public class PrescriptionApplyDTO {

    @NotBlank(message = "药品ID不能为空")
    @Schema(description = "药品ID", required = true)
    private String drugId;

    @Schema(description = "规格ID")
    private Long specificationId;

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private Integer patientId;

    @NotBlank(message = "疾病症状不能为空")
    @Schema(description = "疾病标签（多个用逗号分隔）", required = true, example = "感冒,发烧")
    private String diseases;

    @Schema(description = "症状描述")
    private String symptoms;
}
