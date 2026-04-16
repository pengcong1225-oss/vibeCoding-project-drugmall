package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("dm_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String phone;

    private String nickname;

    private String avatar;

    private String email;

    private String birthday;

    private Integer gender;

    private String realName;

    private String idCard;

    private Boolean isRealNameAuth;

    private BigDecimal balance;

    private Integer points;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
