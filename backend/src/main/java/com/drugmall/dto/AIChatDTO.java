package com.drugmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * AI对话请求DTO
 *
 * @author DrugMall Team
 */
@Data
@Schema(description = "AI对话请求")
public class AIChatDTO {

    @Schema(description = "用户消息", required = true, example = "感冒发烧怎么办")
    @NotBlank(message = "消息内容不能为空")
    private String message;

    @Schema(description = "会话ID，用于保持对话上下文", example = "session-123456")
    private String sessionId;

    @Schema(description = "对话历史")
    private List<ChatMessage> history;

    @Schema(description = "是否使用流式响应", example = "false")
    private Boolean stream = false;

    /**
     * 对话消息
     */
    @Data
    @Schema(description = "对话消息")
    public static class ChatMessage {

        @Schema(description = "角色：user/assistant", example = "user")
        private String role;

        @Schema(description = "消息内容", example = "头痛发热怎么办")
        private String content;
    }
}
