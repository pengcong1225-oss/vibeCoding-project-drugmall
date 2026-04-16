package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 合并购物车DTO
 */
@Data
@Schema(description = "合并购物车请求参数")
public class MergeCartDTO {

    @NotEmpty(message = "本地购物车数据不能为空")
    @Schema(description = "本地购物车数据列表", required = true)
    private List<LocalCartItemDTO> items;

    @Data
    @Schema(description = "本地购物车项")
    public static class LocalCartItemDTO {

        @Schema(description = "药品ID", required = true, example = "1")
        private String drugId;

        @Schema(description = "数量", required = true, example = "1")
        private Integer quantity;

        @Schema(description = "是否选中", example = "true")
        private Boolean isSelected;

        @Schema(description = "病症", example = "感冒")
        private String disease;

        @Schema(description = "用法用量", example = "一日三次，一次一粒")
        private String usage;
    }
}
