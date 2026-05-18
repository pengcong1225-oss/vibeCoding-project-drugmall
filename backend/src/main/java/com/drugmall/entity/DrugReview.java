package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 药品评价实体
 */
@Data
@TableName("dm_product_review")
public class DrugReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("user_name")
    private String userName;

    @TableField("user_avatar")
    private String userAvatar;

    @TableField("product_id")
    private Long productId;

    @TableField("order_id")
    private Long orderId;

    private Integer rating;

    private String content;

    private String images;

    private String tags;

    @TableField("is_anonymous")
    private Boolean isAnonymous;

    @TableField("is_recommended")
    private Boolean isRecommended;

    @TableField("helpful_count")
    private Integer helpfulCount;

    @TableField("reply_content")
    private String replyContent;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
