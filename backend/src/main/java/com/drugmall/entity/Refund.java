package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款实体
 */
@Data
@TableName("dm_refund")
public class Refund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String orderId;

    private String userId;

    private String status;

    private String reason;

    private String description;

    private String images;

    private BigDecimal refundAmount;

    private BigDecimal actualRefundAmount;

    private LocalDateTime createTime;

    private LocalDateTime completeTime;

    private String rejectReason;
}
