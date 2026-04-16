package com.drugmall.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DrugQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String keyword;
    
    private String categoryId;
    
    private Boolean isRx;
    
    private Integer minPrice;
    
    private Integer maxPrice;
    
    private String sortBy;
    
    private String sortOrder;
    
    private String sort;
    
    private Integer page = 1;
    
    private Integer size = 10;
}
