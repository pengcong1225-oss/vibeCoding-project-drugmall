package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 提现记录VO
 */
@Data
@Schema(description = "提现记录")
public class WithdrawRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "提现ID")
    private String withdrawId;

    @Schema(description = "提现金额")
    private Double amount;

    @Schema(description = "提现方式: bank/alipay")
    private String method;

    @Schema(description = "方式图标")
    private String methodIcon;

    @Schema(description = "方式名称")
    private String methodName;

    @Schema(description = "状态: processing/success/rejected")
    private String status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "申请时间")
    private String applyTime;

    @Schema(description = "到账时间")
    private String arrivalTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;
}
