package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付结果VO
 */
@Data
@Schema(description = "支付结果")
public class PayResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否支付成功")
    private Boolean success;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "第三方支付流水号")
    private String transactionId;
}
