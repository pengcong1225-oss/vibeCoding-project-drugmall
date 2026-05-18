package com.drugmall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentConfigVO {
    private String departmentCode;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal subsidy;
    private String symptoms;
    private Integer responseTime;
    private Integer answerTime;
    private String example;
    private List<String> quickSymptoms;
}
