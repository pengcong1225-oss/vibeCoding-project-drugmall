package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("dm_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String orderNo;

    private String userId;

    private String status;

    private String statusText;

    private Integer totalQuantity;

    private BigDecimal totalAmount;

    private BigDecimal drugAmount;

    private BigDecimal deliveryFee;

    private BigDecimal discountAmount;

    private BigDecimal couponAmount;

    private BigDecimal payableAmount;

    private BigDecimal paidAmount;

    private String deliveryType;

    private String addressId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String pickupStoreId;

    private String pickupStoreName;

    private String pickupCode;

    private String payType;

    private LocalDateTime payTime;

    private String logisticsNo;

    private String logisticsCompany;

    private LocalDateTime createTime;

    private LocalDateTime expireTime;

    private LocalDateTime confirmTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime completeTime;

    private LocalDateTime cancelTime;

    private String remark;

    private String prescriptionId;

    private Boolean needRxReview;

    private String rxReviewStatus;
}
