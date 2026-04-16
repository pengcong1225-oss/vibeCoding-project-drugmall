package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收入趋势VO
 */
@Data
@Schema(description = "收入趋势")
public class IncomeTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日期")
    private String date;

    @Schema(description = "收入金额")
    private Integer income;

    @Schema(description = "问诊数量")
    private Integer quantity;
}
