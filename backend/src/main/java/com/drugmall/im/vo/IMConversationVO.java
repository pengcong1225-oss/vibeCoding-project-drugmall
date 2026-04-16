package com.drugmall.im.vo;

import lombok.Data;

/**
 * IM会话VO
 */
@Data
public class IMConversationVO {

    /**
     * 会话ID（格式：C2C_{userId}）
     */
    private String conversationId;

    /**
     * 会话类型：C2C（单聊）/ GROUP（群聊）
     */
    private String type;

    /**
     * 对方用户ID
     */
    private String targetUserId;

    /**
     * 对方用户名称
     */
    private String targetUserName;

    /**
     * 对方用户头像
     */
    private String targetUserAvatar;

    /**
     * 最后一条消息
     */
    private Object lastMessage;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 最后消息时间
     */
    private String lastMessageTime;

    /**
     * 关联问诊ID
     */
    private String consultationId;
}
