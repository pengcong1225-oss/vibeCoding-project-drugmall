package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 药品FAQ VO
 */
@Data
@Schema(description = "药品FAQ")
public class DrugFAQVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "FAQ ID")
    private String id;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "答案")
    private String answer;

    @Schema(description = "排序")
    private Integer sort;
}
