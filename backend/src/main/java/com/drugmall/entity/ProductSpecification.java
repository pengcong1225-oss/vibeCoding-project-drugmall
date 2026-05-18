package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 药品规格实体
 */
@Data
@TableName("dm_product_specification")
public class ProductSpecification implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private String specName;
    
    private String specCode;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer stock;
    
    private String barCode;
    
    private Boolean isDefault;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
