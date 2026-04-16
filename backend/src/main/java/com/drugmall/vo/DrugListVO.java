package com.drugmall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DrugListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<DrugVO> list;
    
    private Long total;
    
    private Integer page;
    
    private Integer size;
}
