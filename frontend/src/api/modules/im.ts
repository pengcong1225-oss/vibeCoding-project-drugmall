import request, { http } from '../request'
import type {
  IMUserInfo,
  IMConversation,
  IMMessage,
  GetUserSigParams,
  GetConversationsParams,
  EnterConversationParams,
  GetMessagesParams,
  SendMessageParams,
  GetUnreadCountParams
} from '@/types/im'

// 获取IM登录凭证（UserSig）
export function getUserSig(params: GetUserSigParams) {
  return http.get<IMUserInfo>('/im/usersig', params).then(data => ({ data }))
}

// 获取会话列表
export function getConversations(params: GetConversationsParams) {
  return http.get<IMConversation[]>('/im/conversations', params).then(data => ({ data: data || [] }))
}

// 进入会话（标记已读）- 后端使用 @RequestParam
export function enterConversation(params: EnterConversationParams) {
  const url = `/im/conversations/${params.conversationId}/enter?userId=${encodeURIComponent(params.userId)}&userType=${encodeURIComponent(params.userType)}`
  return http.post<IMConversation>(url).then(data => ({ data }))
}

// 获取消息历史
export function getMessages(params: GetMessagesParams) {
  return http.get<IMMessage[]>(`/im/messages/${params.conversationId}`, {
    userId: params.userId
  }).then(data => ({ data: data || [] }))
}

// 发送消息 - 后端使用 @RequestParam
export function sendMessage(params: SendMessageParams) {
  const searchParams = new URLSearchParams({
    userId: params.userId,
    userType: params.userType,
    conversationId: params.conversationId,
    type: params.type,
    content: params.content
  })
  return http.post<IMMessage>(`/im/messages/send?${searchParams.toString()}`).then(data => ({ data }))
}

// 获取未读总数
export function getUnreadCount(params: GetUnreadCountParams) {
  return http.get<number>('/im/unread-count', params).then(data => ({ data: data || 0 }))
}
