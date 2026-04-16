/**
 * IM即时通讯API接口
 */
import request from '@/utils/request'
import type { UserSigVO, IMConversationVO, MessageVO, SendMessageParams } from '@/types/im'

/**
 * 获取IM登录凭证
 */
export function getUserSig(userId: string, userType: 'patient' | 'doctor') {
  return request<UserSigVO>({
    url: '/v1/im/usersig',
    method: 'get',
    params: { userId, userType }
  })
}

/**
 * 获取会话列表
 */
export function getConversations(userId: string, userType: 'patient' | 'doctor') {
  return request<IMConversationVO[]>({
    url: '/v1/im/conversations',
    method: 'get',
    params: { userId, userType }
  })
}

/**
 * 进入会话
 */
export function enterConversation(userId: string, userType: 'patient' | 'doctor', conversationId: string) {
  return request<IMConversationVO>({
    url: `/v1/im/conversations/${conversationId}/enter`,
    method: 'post',
    params: { userId, userType }
  })
}

/**
 * 获取消息历史
 */
export function getMessages(userId: string, conversationId: string) {
  return request<MessageVO[]>({
    url: `/v1/im/messages/${conversationId}`,
    method: 'get',
    params: { userId }
  })
}

/**
 * 发送消息
 */
export function sendMessage(params: SendMessageParams) {
  return request<MessageVO>({
    url: '/v1/im/messages/send',
    method: 'post',
    params: params
  })
}

/**
 * 获取未读消息总数
 */
export function getUnreadCount(userId: string, userType: 'patient' | 'doctor') {
  return request<number>({
    url: '/v1/im/unread-count',
    method: 'get',
    params: { userId, userType }
  })
}
