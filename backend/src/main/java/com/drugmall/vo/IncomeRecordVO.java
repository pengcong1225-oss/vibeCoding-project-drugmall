package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收入明细VO
 */
@Data
@Schema(description = "收入明细")
public class IncomeRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "类型图标")
    private String typeIcon;

    @Schema(description = "金额")
    private Double amount;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "状态: 待结算/已结算/已提现")
    private String status;

    @Schema(description = "问诊ID")
    private String inquiryId;
}
