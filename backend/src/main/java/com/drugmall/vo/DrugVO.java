package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DrugVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
    
    private String categoryName;
    
    private Integer status;
    
    private java.time.LocalDateTime createTime;
}
