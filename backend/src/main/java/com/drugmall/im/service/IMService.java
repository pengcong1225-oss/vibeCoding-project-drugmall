package com.drugmall.im.service;

import com.drugmall.im.vo.IMConversationVO;
import com.drugmall.vo.MessageVO;

import java.util.List;

/**
 * IM业务服务接口
 */
public interface IMService {

    /**
     * 获取会话列表
     * @param userId 业务用户ID
     * @param userType 用户类型（patient/doctor）
     * @return 会话列表
     */
    List<IMConversationVO> getConversations(String userId, String userType);

    /**
     * 获取消息历史
     * @param userId 业务用户ID
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<MessageVO> getMessages(String userId, String conversationId);

    /**
     * 发送消息（Mock模式下模拟发送）
     * @param userId 业务用户ID
     * @param userType 用户类型
     * @param conversationId 会话ID
     * @param type 消息类型
     * @param content 消息内容
     * @return 发送后的消息
     */
    MessageVO sendMessage(String userId, String userType, String conversationId, String type, String content);

    /**
     * 获取总未读数
     * @param userId 业务用户ID
     * @param userType 用户类型
     * @return 未读总数
     */
    Integer getUnreadCount(String userId, String userType);

    /**
     * 标记会话为已读
     * @param userId 业务用户ID
     * @param userType 用户类型
     * @param conversationId 会话ID
     */
    void markRead(String userId, String userType, String conversationId);
}
