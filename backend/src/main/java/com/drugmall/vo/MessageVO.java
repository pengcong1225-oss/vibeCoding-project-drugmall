package com.drugmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天消息VO
 */
@Data
@Schema(description = "聊天消息")
public class MessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID")
    private String id;

    @Schema(description = "问诊ID")
    private String consultationId;

    @Schema(description = "发送者: doctor/patient/system")
    private String sender;

    @Schema(description = "消息类型: text/image/voice/prescription")
    private String type;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "发送时间")
    private String time;

    @Schema(description = "状态: sending/sent/read")
    private String status;
}
