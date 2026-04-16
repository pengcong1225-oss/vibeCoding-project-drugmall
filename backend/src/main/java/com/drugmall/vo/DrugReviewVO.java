package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品评价VO
 */
@Data
@Schema(description = "药品评价")
public class DrugReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "评价ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "评分：1-5星")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价图片")
    private List<String> images;

    @Schema(description = "评价标签")
    private List<String> tags;

    @Schema(description = "是否匿名")
    private Boolean isAnonymous;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    @Schema(description = " helpful数量")
    private Integer helpfulCount;

    @Schema(description = "评价时间")
    private LocalDateTime createTime;

    @Schema(description = "商家回复")
    private ReplyVO reply;

    @Data
    @Schema(description = "商家回复")
    public static class ReplyVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "回复内容")
        private String content;

        @Schema(description = "回复时间")
        private LocalDateTime createTime;
    }
}
