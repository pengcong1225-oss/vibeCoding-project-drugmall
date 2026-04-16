package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单状态数量VO
 */
@Data
@Schema(description = "订单状态数量")
public class OrderStatusCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "数量")
    private Integer count;
}
