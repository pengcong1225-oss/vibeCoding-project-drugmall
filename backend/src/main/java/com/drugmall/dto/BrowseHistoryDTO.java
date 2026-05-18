package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 浏览历史DTO
 */
@Data
@Schema(description = "添加浏览历史请求参数")
public class BrowseHistoryDTO {

    @NotBlank(message = "药品ID不能为空")
    @Schema(description = "药品ID", required = true, example = "1")
    private String drugId;

    @Schema(description = "商品ID（与drugId兼容）", example = "1")
    private String productId;
}
