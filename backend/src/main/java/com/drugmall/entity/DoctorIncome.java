package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生收入实体类
 */
@Data
@TableName("dm_doctor_income")
public class DoctorIncome {
    @TableId(type = IdType.INPUT)
    private String id;
    private String doctorId;
    private String type;
    private BigDecimal amount;
    private Long patientId;
    private String consultationId;
    private String status;
    private LocalDateTime settleTime;
    private LocalDateTime createTime;
}
