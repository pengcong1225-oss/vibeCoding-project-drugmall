package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品项实体
 */
@Data
@TableName("dm_order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String orderId;

    private String drugId;

    private String name;

    private String specification;

    private String manufacturer;

    private String image;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer quantity;

    private Boolean isRx;

    private String disease;

    private String usage;

    private BigDecimal subtotal;

    private String reviewStatus;

    private String reviewId;

    private LocalDateTime createTime;
}
