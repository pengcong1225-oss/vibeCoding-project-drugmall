package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dm_hot_search")
public class HotSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String keyword;

    @TableField("keyword_pinyin")
    private String keywordPinyin;

    @TableField("search_count")
    private Integer searchCount;

    @TableField("daily_count")
    private Integer dailyCount;

    @TableField("weekly_count")
    private Integer weeklyCount;

    @TableField("is_hot")
    private Integer isHot;

    @TableField("is_new")
    private Integer isNew;

    @TableField("sort_order")
    private Integer sortOrder;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
