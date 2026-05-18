package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生扩展信息实体类
 */
@Data
@TableName("dm_doctor_ext")
public class DoctorExt {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String doctorId;

    private String departmentCode;

    private Integer isOnline;

    private Integer canPrescribe;

    private Integer waitTime;

    private String consultCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
