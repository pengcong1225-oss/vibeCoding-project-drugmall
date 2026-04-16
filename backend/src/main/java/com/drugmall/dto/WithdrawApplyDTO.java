package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现申请DTO
 */
@Data
@Schema(description = "提现申请请求参数")
public class WithdrawApplyDTO {

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    @Schema(description = "提现金额", required = true, example = "1000.00")
    private BigDecimal amount;

    @NotBlank(message = "提现方式不能为空")
    @Schema(description = "提现方式: bank/alipay", required = true, example = "bank")
    private String method;

    @Schema(description = "提现方式名称", example = "工商银行(尾号8888)")
    private String methodName;
}
