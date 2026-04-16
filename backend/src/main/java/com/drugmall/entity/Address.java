package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址实体
 */
@Data
@TableName("dm_address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    private String postalCode;

    private String tag;

    private Boolean isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
