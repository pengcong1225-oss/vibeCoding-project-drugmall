package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("refund_no")
    private String refundNo;

    private String status;

    private String reason;

    private String description;

    private String images;

    @TableField("refund_amount")
    private BigDecimal refundAmount;

    @TableField("actual_refund_amount")
    private BigDecimal actualRefundAmount;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
