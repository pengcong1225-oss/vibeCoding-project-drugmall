package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 发送消息DTO
 */
@Data
@Schema(description = "发送消息请求参数")
public class SendMessageDTO {

    @NotBlank(message = "消息类型不能为空")
    @Schema(description = "消息类型: text/image/voice/prescription", required = true, example = "text")
    private String type;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", required = true)
    private String content;
}
