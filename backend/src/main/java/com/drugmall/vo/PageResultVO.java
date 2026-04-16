package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果VO
 */
@Data
@Schema(description = "分页结果")
public class PageResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer page;

    @Schema(description = "每页数量")
    private Integer size;

    @Schema(description = "总页数")
    private Integer pages;

    public static <T> PageResultVO<T> of(List<T> list, Long total, Integer page, Integer size) {
        PageResultVO<T> result = new PageResultVO<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        return result;
    }
}
