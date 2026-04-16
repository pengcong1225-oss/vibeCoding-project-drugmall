package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单查询DTO
 */
@Data
@Schema(description = "订单查询请求参数")
public class OrderQueryDTO {

    @Schema(description = "订单状态：pending-待支付，paid-已支付，confirmed-已确认，shipped-配送中，delivered-已送达，completed-已完成，cancelled-已取消", example = "pending")
    private String status;

    @Schema(description = "开始时间", example = "2024-01-01")
    private String startTime;

    @Schema(description = "结束时间", example = "2024-12-31")
    private String endTime;

    @Schema(description = "搜索关键词", example = "阿莫西林")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer size = 10;
}
