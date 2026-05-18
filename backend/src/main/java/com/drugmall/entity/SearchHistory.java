package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dm_search_history")
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String keyword;

    @TableField("search_type")
    private String searchType;

    @TableField("result_count")
    private Integer resultCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
