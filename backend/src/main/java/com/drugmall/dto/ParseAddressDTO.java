package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 智能解析地址DTO
 */
@Data
@Schema(description = "智能解析地址请求参数")
public class ParseAddressDTO {

    @NotBlank(message = "地址文本不能为空")
    @Schema(description = "地址文本", required = true, example = "张三 13800138000 北京市朝阳区某某小区1号楼1单元101室")
    private String addressText;
}
