package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 就诊人实体
 */
@Data
@TableName("dm_patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String name;

    private Integer gender;

    private LocalDate birthday;

    @TableField("id_card")
    private String idCard;

    private String phone;

    private String relationship;

    @TableField("allergy_history")
    private String allergyHistory;

    @TableField("medical_history")
    private String medicalHistory;

    @TableField("is_default")
    private Boolean isDefault;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Boolean isDeleted;
}
