package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 提交评价DTO
 */
@Data
@Schema(description = "提交评价请求参数")
public class SubmitReviewDTO {

    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true, example = "ORD20241201001")
    private String orderId;

    @NotBlank(message = "商品项ID不能为空")
    @Schema(description = "商品项ID", required = true, example = "1")
    private String itemId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    @Schema(description = "评分：1-5星", required = true, example = "5")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Schema(description = "评价内容", required = true, example = "药品效果很好，物流也很快")
    private String content;

    @Schema(description = "评价图片列表")
    private List<String> images;

    @Schema(description = "评价标签列表")
    private List<String> tags;

    @Schema(description = "是否匿名评价", example = "false")
    private Boolean isAnonymous;

    @Schema(description = "是否推荐", example = "true")
    private Boolean isRecommended;
}
