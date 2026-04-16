package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

/**
 * 退款申请DTO
 */
@Data
@Schema(description = "退款申请请求参数")
public class RefundApplyDTO {

    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true, example = "ORD20241201001")
    private String orderId;

    @Schema(description = "申请退款的商品项ID列表")
    private List<String> itemIds;

    @NotBlank(message = "退款原因不能为空")
    @Schema(description = "退款原因", required = true, example = "商品质量问题")
    private String reason;

    @Schema(description = "退款说明", example = "商品包装破损")
    private String description;

    @Schema(description = "退款凭证图片列表")
    private List<String> images;

    @Schema(description = "退款金额", example = "15.80")
    private BigDecimal refundAmount;
}
