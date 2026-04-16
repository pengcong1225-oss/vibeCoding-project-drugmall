package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流信息实体
 */
@Data
@TableName("dm_logistics")
public class Logistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String orderId;

    private LocalDateTime time;

    private String content;

    private String status;

    private Integer sort;
}
