package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 医生今日统计VO
 */
@Data
@Schema(description = "医生今日统计")
public class DoctorStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "待接诊数量")
    private Integer pending;

    @Schema(description = "进行中数量")
    private Integer processing;

    @Schema(description = "已完成数量")
    private Integer completed;

    @Schema(description = "今日收入")
    private Double income;
}
