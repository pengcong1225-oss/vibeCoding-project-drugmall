package com.drugmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 药品评价实体
 */
@Data
@TableName("dm_drug_review")
public class DrugReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String userName;

    private String userAvatar;

    private String drugId;

    private String orderId;

    private Integer rating;

    private String content;

    private String images;

    private String tags;

    private Boolean isAnonymous;

    private Boolean isRecommended;

    private Integer helpfulCount;

    private LocalDateTime createTime;

    private String replyContent;

    private LocalDateTime replyTime;
}
