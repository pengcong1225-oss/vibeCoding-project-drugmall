package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 症状自测DTO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "症状自测请求")
public class SymptomTestDTO {

    @NotEmpty(message = "症状列表不能为空")
    @Schema(description = "症状列表", required = true, example = "[\"发热\", \"咳嗽\", \"头痛\"]")
    private List<String> symptoms;

    @Schema(description = "身体部位", example = "头部")
    private String bodyPart;

    @Schema(description = "持续时间", example = "3天")
    private String duration;

    @Schema(description = "严重程度：轻度、中度、重度", example = "中度")
    private String severity;

    @Schema(description = "其他描述")
    private String description;
}
