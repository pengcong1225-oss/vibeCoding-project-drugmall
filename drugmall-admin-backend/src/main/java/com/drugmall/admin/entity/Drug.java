package com.drugmall.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_product")
public class Drug implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productCode;

    private String productName;

    private Long categoryId;

    private Long brandId;

    private String mainImage;

    private String images;

    private String specification;

    private String unit;

    private String manufacturer;

    private String approvalNumber;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer warningStock;

    private Integer sales;

    private Integer isRx;

    @TableField("`usage`")
    private String usage;

    private String description;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
