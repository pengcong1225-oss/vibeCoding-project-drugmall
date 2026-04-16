package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 添加到购物车DTO
 */
@Data
@Schema(description = "添加到购物车请求参数")
public class AddToCartDTO {

    @NotBlank(message = "药品ID不能为空")
    @Schema(description = "药品ID", required = true, example = "1")
    private String drugId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    @Schema(description = "数量", required = true, example = "1")
    private Integer quantity;

    @Schema(description = "病症", example = "感冒")
    private String disease;

    @Schema(description = "用法用量", example = "一日三次，一次一粒")
    private String usage;
}
