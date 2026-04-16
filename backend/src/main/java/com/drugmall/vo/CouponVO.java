package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券VO
 */
@Data
@Schema(description = "优惠券信息")
public class CouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券ID")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型：full_reduction-满减，discount-折扣，cash-现金券")
    private String type;

    @Schema(description = "面值")
    private BigDecimal value;

    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态：unused-未使用，used-已使用，expired-已过期")
    private String status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "使用范围")
    private String scope;
}
