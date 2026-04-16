package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
@Schema(description = "创建订单请求参数")
public class CreateOrderDTO {

    @NotEmpty(message = "购物车项ID列表不能为空")
    @Schema(description = "购物车项ID列表", required = true)
    private List<String> cartItemIds;

    @NotBlank(message = "地址ID不能为空")
    @Schema(description = "地址ID", required = true, example = "1")
    private String addressId;

    @NotBlank(message = "配送方式不能为空")
    @Schema(description = "配送方式：delivery-快递配送，self_pickup-到店自提，same_day-当日达", required = true, example = "delivery")
    private String deliveryType;

    @Schema(description = "备注", example = "请尽快发货")
    private String remark;

    @Schema(description = "优惠券ID", example = "1")
    private String couponId;

    @Schema(description = "处方ID", example = "1")
    private String prescriptionId;
}
