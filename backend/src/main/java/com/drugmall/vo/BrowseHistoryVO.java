package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 浏览历史VO
 */
@Data
@Schema(description = "浏览历史")
public class BrowseHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "药品名称")
    private String name;

    @Schema(description = "药品图片")
    private String image;

    @Schema(description = "药品价格")
    private BigDecimal price;

    @Schema(description = "浏览时间")
    private LocalDateTime browseTime;
}
