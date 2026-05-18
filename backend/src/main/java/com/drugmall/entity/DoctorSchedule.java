package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生排班实体类
 */
@Data
@TableName("dm_doctor_schedule")
public class DoctorSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String doctorId;
    private Integer dayOfWeek;
    private Integer morning;
    private Integer afternoon;
    private Integer evening;
    private Integer maxConsultations;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
