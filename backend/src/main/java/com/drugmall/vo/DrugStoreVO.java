package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 药品在售门店VO
 */
@Data
public class DrugStoreVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private String name;
    
    private String logo;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String distance;
    
    private String delivery;
    
    private Double rating;
    
    private Integer sales;
    
    private List<String> tags;
    
    private Integer stock;
    
    private Boolean isAvailable;
}
