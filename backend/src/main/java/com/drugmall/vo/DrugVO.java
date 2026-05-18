package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DrugVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    
    private String name;
    
    private String genericName;
    
    private String brand;
    
    private String specification;
    
    private String manufacturer;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String image;
    
    private String imageColor;
    
    private String imageText;
    
    private Boolean isRx;
    
    private Boolean isNationalEssential;
    
    private String category;
    
    private String categoryId;
    
    private String disease;
    
    private String usage;
    
    private String contraindications;
    
    private String precautions;
    
    private String adverseReactions;
    
    private String storage;
    
    private String validity;
    
    private String ingredients;
    
    private String appearance;
    
    private String drugInteractions;
    
    private Integer stock;
    
    private Integer sales;
    
    private String approvalNumber;
    
    private String barCode;
    
    private String medicalInsuranceCode;
    
    private String traceabilityCode;
    
    private Boolean isLongPrescription;
    
    private String insuranceCategory;
    
    private java.util.List<DrugSpecificationVO> specifications;
    
    private String description;
    
    private String categoryName;
    
    private Integer status;
    
    private java.time.LocalDateTime createTime;
}
