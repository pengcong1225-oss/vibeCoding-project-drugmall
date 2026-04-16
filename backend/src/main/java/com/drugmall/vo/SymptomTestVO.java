package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 症状自测VO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "症状自测响应")
public class SymptomTestVO {

    @Schema(description = "可能情况")
    private List<String> possibleConditions;

    @Schema(description = "建议说明")
    private String advice;

    @Schema(description = "推荐药品")
    private List<AIChatVO.RecommendedDrug> recommendedDrugs;

    @Schema(description = "注意事项")
    private List<String> precautions;

    @Schema(description = "是否需要就医")
    private Boolean needDoctor;

    @Schema(description = "紧急程度：低、中、高")
    private String urgencyLevel;

    @Schema(description = "下一步建议")
    private List<String> nextSteps;
}
