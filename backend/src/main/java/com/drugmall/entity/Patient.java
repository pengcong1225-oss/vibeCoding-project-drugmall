package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 就诊人实体
 */
@Data
@TableName("dm_patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String name;

    private Integer gender;

    private Integer age;

    private String birthday;

    private String idCard;

    private String phone;

    private String relationship;

    private String address;

    private String allergyHistory;

    private String medicalHistory;

    private Boolean isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
