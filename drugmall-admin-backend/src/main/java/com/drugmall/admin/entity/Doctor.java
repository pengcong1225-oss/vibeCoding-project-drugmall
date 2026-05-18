package com.drugmall.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dm_doctor")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String phone;

    private String password;

    private String name;

    private String avatar;

    private Integer gender;

    private String title;

    private String hospital;

    private String department;

    private String licenseNo;

    private Integer isCertified;

    private BigDecimal rating;

    private Integer serviceCount;

    private Integer responseTime;

    private String specialties;

    private String introduction;

    private BigDecimal balance;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
