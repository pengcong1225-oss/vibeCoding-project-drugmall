package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DrugDetailVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private DrugVO drug;
    
    private List<DrugVO> relatedDrugs;
    
    private List<DrugReviewVO> reviews;
    
    private List<DrugFAQVO> faqs;
    
    private List<DrugVO> recommendedDrugs;
}
