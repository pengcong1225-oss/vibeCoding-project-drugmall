package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收入构成VO
 */
@Data
@Schema(description = "收入构成")
public class IncomeCompositionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收入类型")
    private String type;

    @Schema(description = "金额")
    private Integer amount;

    @Schema(description = "占比(%)")
    private Double percentage;
}
