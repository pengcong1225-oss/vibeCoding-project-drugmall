package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方实体类
 */
@Data
@TableName("dm_prescription")
public class Prescription {
    @TableId(type = IdType.INPUT)
    private String id;
    private String consultationId;
    private String doctorId;
    private Long patientId;
    private Long userId;
    private String diagnosis;
    private String status;
    private String rejectReason;
    private BigDecimal totalAmount;
    private Integer validDays;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
