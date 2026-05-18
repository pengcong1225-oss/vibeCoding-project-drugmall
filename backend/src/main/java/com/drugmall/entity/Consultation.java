package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问诊实体类
 */
@Data
@TableName("dm_consultation")
public class Consultation {
    @TableId(type = IdType.INPUT)
    private String id;
    private String doctorId;
    private Long patientId;
    private Long userId;
    private String type;
    private String symptom;
    private String images;
    private String requestedDrugIds; // 患者申请的药品ID列表(JSON数组)
    private String status;
    private Integer isUrgent;
    private Integer isRx;
    private BigDecimal fee;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
