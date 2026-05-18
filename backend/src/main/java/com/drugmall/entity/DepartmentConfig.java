package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_department_config")
public class DepartmentConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("department_code")
    private String departmentCode;
    
    private BigDecimal price;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    private BigDecimal subsidy;
    
    private String symptoms;
    
    @TableField("response_time")
    private Integer responseTime;
    
    @TableField("answer_time")
    private Integer answerTime;
    
    private String example;
    
    @TableField("quick_symptoms")
    private String quickSymptoms;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
