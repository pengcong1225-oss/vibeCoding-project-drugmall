package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;

/**
 * 更新购物车DTO
 */
@Data
@Schema(description = "更新购物车请求参数")
public class UpdateCartDTO {

    @Schema(description = "数量", example = "2")
    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;

    @Schema(description = "是否选中", example = "true")
    private Boolean isSelected;

    @Schema(description = "病症", example = "感冒")
    private String disease;

    @Schema(description = "用法用量", example = "一日三次，一次一粒")
    private String usage;
}
