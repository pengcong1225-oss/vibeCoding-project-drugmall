package com.drugmall.im.service.impl;

import com.drugmall.config.MockDataService;
import com.drugmall.im.config.TencentIMConfig;
import com.drugmall.im.service.IMService;
import com.drugmall.im.service.TencentIMRestService;
import com.drugmall.im.vo.IMConversationVO;
import com.drugmall.vo.MessageVO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * IM业务服务实现
 * 支持Mock模式（MockDataService）和真实模式（腾讯云IM REST API）
 */
@Slf4j
@Service
public class IMServiceImpl implements IMService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private TencentIMConfig imConfig;

    @Autowired
    private TencentIMRestService tencentIMRestService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<IMConversationVO> getConversations(String userId, String userType) {
        if (!imConfig.getMockMode()) {
            return getRealConversations(userId, userType);
        }
        return getMockConversations(userId, userType);
    }

    @Override
    public List<MessageVO> getMessages(String userId, String conversationId) {
        if (!imConfig.getMockMode()) {
            return getRealMessages(userId, conversationId);
        }
        return getMockMessages(userId, conversationId);
    }

    @Override
    public MessageVO sendMessage(String userId, String userType, String conversationId, String type, String content) {
        if (!imConfig.getMockMode()) {
            return sendRealMessage(userId, userType, conversationId, type, content);
        }
        return sendMockMessage(userId, userType, conversationId, type, content);
    }

    @Override
    public Integer getUnreadCount(String userId, String userType) {
        if (!imConfig.getMockMode()) {
            // 真实模式下，前端TIM SDK自行管理未读数
            // 后端返回0，实际未读数由前端SDK实时维护
            return 0;
        }
        return getMockUnreadCount(userId, userType);
    }

    @Override
    public void markRead(String userId, String userType, String conversationId) {
        if (!imConfig.getMockMode()) {
            markRealRead(userId, userType, conversationId);
            return;
        }
        log.info("Mock模式标记会话已读: userId={}, userType={}, conversationId={}",
                userId, userType, conversationId);
    }

    // ============ 真实模式方法 ============

    /**
     * 真实模式：通过REST API获取最近联系人（会话列表）
     */
    private List<IMConversationVO> getRealConversations(String userId, String userType) {
        List<IMConversationVO> conversations = new ArrayList<>();
        String imUserId = userType.toLowerCase() + "_" + userId;

        try {
            JsonNode result = tencentIMRestService.getRecentContacts(imUserId);
            int errorCode = result.path("ErrorCode").asInt(-1);

            if (errorCode != 0) {
                log.warn("获取真实会话列表失败: errorCode={}, errorInfo={}",
                        errorCode, result.path("ErrorInfo").asText());
                return conversations;
            }

            JsonNode sessionList = result.path("SessionItem");
            if (sessionList.isArray()) {
                for (JsonNode session : sessionList) {
                    IMConversationVO vo = new IMConversationVO();
                    String toAccount = session.path("To_Account").asText();
                    vo.setConversationId("C2C_" + toAccount);
                    vo.setType("C2C");
                    vo.setTargetUserId(toAccount);
                    vo.setTargetUserName(toAccount);
                    vo.setTargetUserAvatar("");
                    vo.setUnreadCount(session.path("UnreadMsgCount").asInt(0));

                    // 解析最后消息时间
                    long msgTime = session.path("MsgTime").asLong(0);
                    if (msgTime > 0) {
                        LocalDateTime dt = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(msgTime), ZoneId.systemDefault());
                        vo.setLastMessageTime(dt.format(TIME_FORMATTER));
                    }

                    conversations.add(vo);
                }
            }

            log.info("获取用户 {} 的真实会话列表成功，共 {} 个会话", imUserId, conversations.size());
        } catch (Exception e) {
            log.error("获取真实会话列表异常: userId={}", imUserId, e);
        }

        return conversations;
    }

    /**
     * 真实模式：通过REST API获取漫游消息
     */
    private List<MessageVO> getRealMessages(String userId, String conversationId) {
        List<MessageVO> messages = new ArrayList<>();

        try {
            // 解析会话ID获取对方账号（格式：C2C_{targetUserId}）
            String targetUserId = conversationId;
            if (conversationId.startsWith("C2C_")) {
                targetUserId = conversationId.substring(4);
            }

            // 查询最近7天的消息
            long maxTime = System.currentTimeMillis() / 1000;
            long minTime = maxTime - 7 * 24 * 3600;

            JsonNode result = tencentIMRestService.getRoamMsg(userId, targetUserId, 50, minTime, maxTime);
            int errorCode = result.path("ErrorCode").asInt(-1);

            if (errorCode != 0) {
                log.warn("获取真实消息历史失败: errorCode={}, errorInfo={}",
                        errorCode, result.path("ErrorInfo").asText());
                return messages;
            }

            JsonNode msgList = result.path("MsgList");
            if (msgList.isArray()) {
                for (JsonNode msg : msgList) {
                    MessageVO vo = new MessageVO();
                    vo.setId("MSG_" + msg.path("MsgRandom").asLong());
                    vo.setConsultationId(conversationId);
                    vo.setSender(msg.path("From_Account").asText());

                    // 解析消息内容
                    JsonNode msgBody = msg.path("MsgBody");
                    if (msgBody.isArray() && msgBody.size() > 0) {
                        JsonNode firstElem = msgBody.get(0);
                        String msgType = firstElem.path("MsgType").asText();
                        if ("TIMTextElem".equals(msgType)) {
                            vo.setType("text");
                            vo.setContent(firstElem.path("MsgContent").path("Text").asText());
                        } else {
                            vo.setType(msgType);
                            vo.setContent(firstElem.path("MsgContent").toString());
                        }
                    }

                    // 解析时间
                    long msgTime = msg.path("MsgTimeStamp").asLong(0);
                    if (msgTime > 0) {
                        LocalDateTime dt = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(msgTime), ZoneId.systemDefault());
                        vo.setTime(dt.format(TIME_FORMATTER));
                    }

                    vo.setStatus("sent");
                    messages.add(vo);
                }
            }

            log.info("获取会话 {} 的真实消息历史成功，共 {} 条消息", conversationId, messages.size());
        } catch (Exception e) {
            log.error("获取真实消息历史异常: conversationId={}", conversationId, e);
        }

        return messages;
    }

    /**
     * 真实模式：通过REST API发送消息
     */
    private MessageVO sendRealMessage(String userId, String userType, String conversationId,
                                       String type, String content) {
        String fromAccount = userType.toLowerCase() + "_" + userId;
        String toAccount = conversationId;
        if (conversationId.startsWith("C2C_")) {
            toAccount = conversationId.substring(4);
        }

        try {
            JsonNode result = tencentIMRestService.sendC2CMessage(fromAccount, toAccount, type, content);
            int errorCode = result.path("ErrorCode").asInt(-1);

            MessageVO vo = new MessageVO();
            vo.setConsultationId(conversationId);
            vo.setSender(fromAccount);
            vo.setType(type != null ? type : "text");
            vo.setContent(content);
            vo.setTime(LocalDateTime.now().format(TIME_FORMATTER));

            if (errorCode == 0) {
                vo.setId("MSG_" + result.path("MsgKey").asText());
                vo.setStatus("sent");
                log.info("真实模式发送消息成功: from={}, to={}", fromAccount, toAccount);
            } else {
                vo.setId("MSG_" + System.currentTimeMillis());
                vo.setStatus("failed");
                log.error("真实模式发送消息失败: errorCode={}, errorInfo={}",
                        errorCode, result.path("ErrorInfo").asText());
            }

            return vo;
        } catch (Exception e) {
            log.error("真实模式发送消息异常: from={}, to={}", fromAccount, toAccount, e);

            MessageVO vo = new MessageVO();
            vo.setId("MSG_" + System.currentTimeMillis());
            vo.setConsultationId(conversationId);
            vo.setSender(fromAccount);
            vo.setType(type != null ? type : "text");
            vo.setContent(content);
            vo.setTime(LocalDateTime.now().format(TIME_FORMATTER));
            vo.setStatus("failed");
            return vo;
        }
    }

    /**
     * 真实模式：标记消息已读
     */
    private void markRealRead(String userId, String userType, String conversationId) {
        String imUserId = userType.toLowerCase() + "_" + userId;
        String targetUserId = conversationId;
        if (conversationId.startsWith("C2C_")) {
            targetUserId = conversationId.substring(4);
        }

        try {
            boolean success = tencentIMRestService.setMsgRead(imUserId, targetUserId);
            if (success) {
                log.info("真实模式标记已读成功: userId={}, conversationId={}", imUserId, conversationId);
            } else {
                log.warn("真实模式标记已读失败: userId={}, conversationId={}", imUserId, conversationId);
            }
        } catch (Exception e) {
            log.error("真实模式标记已读异常: userId={}, conversationId={}", imUserId, conversationId, e);
        }
    }

    // ============ Mock模式方法 ============

    private List<IMConversationVO> getMockConversations(String userId, String userType) {
        List<IMConversationVO> conversations = new ArrayList<>();
        String imUserId = userType.toLowerCase() + "_" + userId;

        JsonNode conversationsNode = mockDataService.getIMConversations(imUserId);
        if (conversationsNode == null || !conversationsNode.isArray()) {
            log.warn("未找到用户 {} 的会话数据", imUserId);
            return conversations;
        }

        for (JsonNode convNode : conversationsNode) {
            IMConversationVO vo = new IMConversationVO();
            vo.setConversationId(convNode.path("conversationId").asText());
            vo.setType(convNode.path("type").asText());
            vo.setTargetUserId(convNode.path("targetUserId").asText());
            vo.setTargetUserName(convNode.path("targetUserName").asText());
            vo.setTargetUserAvatar(convNode.path("targetUserAvatar").asText());
            vo.setUnreadCount(convNode.path("unreadCount").asInt(0));
            vo.setLastMessageTime(convNode.path("lastMessageTime").asText());
            vo.setConsultationId(convNode.path("consultationId").asText());

            JsonNode lastMsgNode = convNode.get("lastMessage");
            if (lastMsgNode != null && !lastMsgNode.isNull()) {
                MessageVO msgVO = new MessageVO();
                msgVO.setId(lastMsgNode.path("id").asText());
                msgVO.setSender(lastMsgNode.path("from").asText());
                msgVO.setType(lastMsgNode.path("type").asText());
                msgVO.setContent(lastMsgNode.path("content").asText());
                msgVO.setTime(lastMsgNode.path("time").asText());
                msgVO.setStatus(lastMsgNode.path("status").asText());
                vo.setLastMessage(msgVO);
            }

            conversations.add(vo);
        }

        log.info("获取用户 {} 的会话列表成功，共 {} 个会话", imUserId, conversations.size());
        return conversations;
    }

    private List<MessageVO> getMockMessages(String userId, String conversationId) {
        List<MessageVO> messages = new ArrayList<>();

        JsonNode messagesNode = mockDataService.getIMMessages(conversationId);
        if (messagesNode == null || !messagesNode.isArray()) {
            log.warn("未找到会话 {} 的消息数据", conversationId);
            return messages;
        }

        for (JsonNode msgNode : messagesNode) {
            MessageVO vo = new MessageVO();
            vo.setId(msgNode.path("id").asText());
            vo.setConsultationId(conversationId);
            vo.setSender(msgNode.path("from").asText());
            vo.setType(msgNode.path("type").asText());
            vo.setContent(msgNode.path("content").asText());
            vo.setTime(msgNode.path("time").asText());
            vo.setStatus(msgNode.path("status").asText());
            messages.add(vo);
        }

        log.info("获取会话 {} 的消息列表成功，共 {} 条消息", conversationId, messages.size());
        return messages;
    }

    private MessageVO sendMockMessage(String userId, String userType, String conversationId,
                                       String type, String content) {
        MessageVO vo = new MessageVO();
        vo.setId("MSG_" + System.currentTimeMillis());
        vo.setConsultationId(conversationId);
        vo.setSender(userType.toLowerCase() + "_" + userId);
        vo.setType(type != null ? type : "text");
        vo.setContent(content);
        vo.setTime(LocalDateTime.now().format(TIME_FORMATTER));
        vo.setStatus("sent");

        log.info("Mock模式模拟发送消息: userId={}, conversationId={}, content={}", userId, conversationId, content);
        return vo;
    }

    private Integer getMockUnreadCount(String userId, String userType) {
        int totalUnread = 0;
        String imUserId = userType.toLowerCase() + "_" + userId;

        JsonNode conversationsNode = mockDataService.getIMConversations(imUserId);
        if (conversationsNode == null || !conversationsNode.isArray()) {
            return totalUnread;
        }

        for (JsonNode convNode : conversationsNode) {
            totalUnread += convNode.path("unreadCount").asInt(0);
        }

        log.info("获取用户 {} 的未读总数: {}", imUserId, totalUnread);
        return totalUnread;
    }
}
