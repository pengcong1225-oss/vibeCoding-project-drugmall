package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量删除购物车DTO
 */
@Data
@Schema(description = "批量删除购物车请求参数")
public class BatchRemoveCartDTO {

    @NotEmpty(message = "购物车项ID列表不能为空")
    @Schema(description = "购物车项ID列表", required = true)
    private List<String> itemIds;
}
