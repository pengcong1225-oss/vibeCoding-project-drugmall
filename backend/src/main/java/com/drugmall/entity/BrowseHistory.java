package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 浏览历史实体
 */
@Data
@TableName("dm_browse_history")
public class BrowseHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String drugId;

    private String name;

    private String image;

    private BigDecimal price;

    private LocalDateTime browseTime;
}
