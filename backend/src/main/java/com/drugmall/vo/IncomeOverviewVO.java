package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收入总览VO
 */
@Data
@Schema(description = "收入总览")
public class IncomeOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账户余额")
    private Double balance;

    @Schema(description = "本月收入")
    private Double monthIncome;

    @Schema(description = "月收入增长率(%)")
    private Double monthIncomeRatio;

    @Schema(description = "累计收入")
    private Double totalIncome;

    @Schema(description = "今日收入")
    private Double todayIncome;

    @Schema(description = "本周收入")
    private Double weekIncome;

    @Schema(description = "待结算")
    private Double pendingSettlement;

    @Schema(description = "累计提现")
    private Double totalWithdraw;
}
