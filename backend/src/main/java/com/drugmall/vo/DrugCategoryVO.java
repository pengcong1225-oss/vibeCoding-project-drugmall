package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 药品分类VO
 */
@Data
@Schema(description = "药品分类")
public class DrugCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    private String id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类ID")
    private String parentId;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "子分类")
    private List<DrugCategoryVO> children;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
