package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项实体
 */
@Data
@TableName("dm_cart_item")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String drugId;

    private String drugName;

    private String specification;

    private String manufacturer;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer quantity;

    private String image;

    private String imageColor;

    private String imageText;

    private String disease;

    private String usage;

    private Boolean isRx;

    private Boolean isSelected;

    private Integer stock;

    private String categoryId;

    private String categoryName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
