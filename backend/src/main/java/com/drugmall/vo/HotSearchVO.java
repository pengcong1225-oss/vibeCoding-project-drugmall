package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 热门搜索VO
 */
@Data
@Schema(description = "热门搜索")
public class HotSearchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "热度")
    private Integer heat;

    @Schema(description = "是否新品")
    private Boolean isNew;

    @Schema(description = "是否热门")
    private Boolean isHot;
}
