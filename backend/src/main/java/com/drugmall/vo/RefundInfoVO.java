package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 退款信息VO
 */
@Data
@Schema(description = "退款信息")
public class RefundInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "退款ID")
    private String id;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "状态：pending-待处理，processing-处理中，completed-已完成，rejected-已拒绝")
    private String status;

    @Schema(description = "退款原因")
    private String reason;

    @Schema(description = "退款说明")
    private String description;

    @Schema(description = "退款凭证图片")
    private List<String> images;

    @Schema(description = "申请退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "实际退款金额")
    private BigDecimal actualRefundAmount;

    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;
}
