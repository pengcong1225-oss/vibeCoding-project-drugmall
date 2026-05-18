package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门店药品库存实体
 */
@Data
@TableName("dm_store_inventory")
public class StoreInventory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long storeId;
    
    private Long productId;
    
    private Integer stock;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer discount;
    
    private Boolean isAvailable;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
