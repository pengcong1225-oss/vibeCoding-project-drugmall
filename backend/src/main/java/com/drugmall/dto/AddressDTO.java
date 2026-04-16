package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 地址DTO
 */
@Data
@Schema(description = "地址请求参数")
public class AddressDTO {

    @Schema(description = "地址ID（更新时必填）", example = "1")
    private String id;

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", required = true, example = "张三")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份", required = true, example = "北京市")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市", required = true, example = "北京市")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Schema(description = "区县", required = true, example = "朝阳区")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", required = true, example = "某某小区1号楼1单元101室")
    private String detail;

    @Schema(description = "邮政编码", example = "100000")
    private String postalCode;

    @Schema(description = "标签：家、公司、学校等", example = "家")
    private String tag;

    @Schema(description = "是否设为默认地址", example = "true")
    private Boolean isDefault;
}
