package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("logistics_no")
    private String logisticsNo;

    @TableField("logistics_company")
    private String logisticsCompany;

    private LocalDateTime time;

    private String content;

    private String status;

    private Integer sort;

    @TableField("create_time")
    private LocalDateTime createTime;
}
