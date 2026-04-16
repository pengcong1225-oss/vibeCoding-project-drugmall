package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * AI对话响应VO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "AI对话响应")
public class AIChatVO {

    @Schema(description = "AI回复内容")
    private String content;

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "推荐的药品列表")
    private List<RecommendedDrug> drugs;

    @Schema(description = "是否显示操作按钮")
    private Boolean showActions;

    @Schema(description = "Token使用统计")
    private TokenUsage usage;

    /**
     * 推荐药品
     */
    @Data
    @Schema(description = "推荐药品")
    public static class RecommendedDrug {

        @Schema(description = "药品ID")
        private String id;

        @Schema(description = "药品名称")
        private String name;

        @Schema(description = "药品图片")
        private String image;

        @Schema(description = "药品价格")
        private Double price;

        @Schema(description = "药品规格")
        private String spec;

        @Schema(description = "生产厂家")
        private String manufacturer;

        @Schema(description = "是否处方药")
        private Boolean isRx;
    }

    /**
     * Token使用统计
     */
    @Data
    @Schema(description = "Token使用统计")
    public static class TokenUsage {

        @Schema(description = "提示词Token数")
        private Integer promptTokens;

        @Schema(description = "生成Token数")
        private Integer completionTokens;

        @Schema(description = "总Token数")
        private Integer totalTokens;
    }
}
