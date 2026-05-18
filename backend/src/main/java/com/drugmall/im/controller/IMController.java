package com.drugmall.im.controller;

import com.drugmall.common.Result;
import com.drugmall.im.service.IMService;
import com.drugmall.im.service.IMUserSigService;
import com.drugmall.im.vo.IMConversationVO;
import com.drugmall.im.vo.UserSigVO;
import com.drugmall.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IM即时通讯控制器
 * 提供腾讯IM相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/v1/im")
@Tag(name = "IM即时通讯", description = "腾讯IM相关接口")
public class IMController {

    @Autowired
    private IMUserSigService userSigService;

    @Autowired
    private IMService imService;

    /**
     * 获取UserSig
     * 供前端腾讯IM SDK登录使用
     */
    @GetMapping("/usersig")
    @Operation(summary = "获取IM登录凭证", description = "生成UserSig供前端IM SDK登录")
    public Result<UserSigVO> getUserSig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "用户类型: patient/doctor") @RequestParam String userType) {
        try {
            // 生成IM用户ID
            String imUserId = userSigService.generateIMUserId(userType, userId);

            // 生成UserSig
            String userSig = userSigService.generateUserSig(userId, userType);

            UserSigVO vo = new UserSigVO();
            vo.setUserId(imUserId);
            vo.setUserSig(userSig);
            vo.setSdkAppId(1600043565L);
            vo.setExpireTime(86400L);

            log.info("生成UserSig成功: userId={}, userType={}, imUserId={}", userId, userType, imUserId);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("生成UserSig失败", e);
            return Result.error(7014, "UserSig生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    @Operation(summary = "获取会话列表", description = "获取用户的IM会话列表")
    public Result<List<IMConversationVO>> getConversations(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "用户类型: patient/doctor") @RequestParam String userType) {
        try {
            List<IMConversationVO> conversations = imService.getConversations(userId, userType);
            return Result.success(conversations);
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }

    /**
     * 进入会话
     */
    @PostMapping("/conversations/{id}/enter")
    @Operation(summary = "进入会话", description = "进入指定会话并标记为已读")
    public Result<IMConversationVO> enterConversation(
            @RequestBody java.util.Map<String, String> body,
            @Parameter(description = "会话ID") @PathVariable("id") String conversationId) {
        String userId = body.get("userId");
        String userType = body.get("userType");
        try {
            // 标记为已读
            imService.markRead(userId, userType, conversationId);

            // 返回会话信息（这里简化处理，实际应查询会话详情）
            IMConversationVO vo = new IMConversationVO();
            vo.setConversationId(conversationId);
            vo.setUnreadCount(0);

            return Result.success(vo);
        } catch (Exception e) {
            log.error("进入会话失败", e);
            return Result.error("进入会话失败: " + e.getMessage());
        }
    }

    /**
     * 获取消息历史
     */
    @GetMapping("/messages/{conversationId}")
    @Operation(summary = "获取消息历史", description = "获取指定会话的消息历史")
    public Result<List<MessageVO>> getMessages(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "会话ID") @PathVariable String conversationId) {
        try {
            List<MessageVO> messages = imService.getMessages(userId, userType, conversationId);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取消息历史失败", e);
            return Result.error("获取消息历史失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/messages/send")
    @Operation(summary = "发送消息", description = "发送IM消息")
    public Result<MessageVO> sendMessage(
            @RequestBody java.util.Map<String, String> body) {
        String userId = body.get("userId");
        String userType = body.get("userType");
        String conversationId = body.get("conversationId");
        String type = body.get("type");
        String content = body.get("content");
        try {
            MessageVO message = imService.sendMessage(userId, userType, conversationId, type, content);
            return Result.success(message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error(7016, "消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 获取未读总数
     */
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读总数", description = "获取用户所有会话的未读消息总数")
    public Result<Integer> getUnreadCount(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "用户类型: patient/doctor") @RequestParam String userType) {
        try {
            Integer unreadCount = imService.getUnreadCount(userId, userType);
            return Result.success(unreadCount);
        } catch (Exception e) {
            log.error("获取未读总数失败", e);
            return Result.error("获取未读总数失败: " + e.getMessage());
        }
    }
}
