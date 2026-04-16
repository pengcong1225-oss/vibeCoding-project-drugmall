package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_drug")
public class Drug implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    private String specification;
    
    private String manufacturer;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String image;
    
    private String imageColor;
    
    private String imageText;
    
    private Boolean isRx;
    
    private String category;
    
    private String categoryId;
    
    private String disease;
    
    private String usage;
    
    private Integer stock;
    
    private Integer sales;
    
    private String description;
    
    private Integer status;
    
    private Integer sortOrder;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
