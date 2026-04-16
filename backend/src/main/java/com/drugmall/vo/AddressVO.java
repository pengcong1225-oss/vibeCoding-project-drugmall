package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 地址VO
 */
@Data
@Schema(description = "收货地址信息")
public class AddressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "地址ID")
    private String id;

    @Schema(description = "收货人姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "完整地址")
    private String fullAddress;

    @Schema(description = "邮政编码")
    private String postalCode;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "是否默认")
    private Boolean isDefault;
}
