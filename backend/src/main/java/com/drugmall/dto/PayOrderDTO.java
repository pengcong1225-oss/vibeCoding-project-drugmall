package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 支付订单DTO
 */
@Data
@Schema(description = "支付订单请求参数")
public class PayOrderDTO {

    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true, example = "ORD20241201001")
    private String orderId;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式：wechat-微信支付，alipay-支付宝，balance-余额支付", required = true, example = "wechat")
    private String payType;

    @Schema(description = "支付成功后跳转URL", example = "https://example.com/pay/success")
    private String returnUrl;
}
