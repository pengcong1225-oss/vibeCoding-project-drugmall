package com.drugmall.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String storeCode;

    private String storeName;

    private String logo;

    private String logoText;

    private String logoColor;

    private BigDecimal rating;

    private Integer monthlySales;

    private String address;

    private String phone;

    private String businessHours;

    private Integer isOpen;

    @com.baomidou.mybatisplus.annotation.TableField("is_24hours")
    private Integer is24hours;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    private String businessScope;

    private String licenseNo;

    private Integer isInsurance;

    private Integer isChain;

    private Integer isSelfOperated;

    private Integer deliveryTime;

    private BigDecimal minDeliveryAmount;

    private BigDecimal deliveryFee;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
