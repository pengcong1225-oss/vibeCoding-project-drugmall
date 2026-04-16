package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索建议VO
 */
@Data
@Schema(description = "搜索建议")
public class SearchSuggestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "类型：drug-药品，disease-疾病，symptom-症状")
    private String type;

    @Schema(description = "数量")
    private Integer count;
}
