package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 药品规格VO
 */
@Data
public class DrugSpecificationVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private String specName;
    
    private String specCode;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer stock;
    
    private String barCode;
    
    private Boolean isDefault;
}
