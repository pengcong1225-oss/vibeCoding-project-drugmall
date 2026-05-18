import request from '@/utils/request'
import type { UserSigVO, IMConversationVO, MessageVO, SendMessageParams } from '@/types/im'

export function getUserSig(userId: string, userType: 'patient' | 'doctor') {
  return request<UserSigVO>({
    url: '/im/usersig',
    method: 'get',
    params: { userId, userType }
  })
}

export function getConversations(userId: string, userType: 'patient' | 'doctor') {
  return request<IMConversationVO[]>({
    url: '/im/conversations',
    method: 'get',
    params: { userId, userType }
  })
}

export function enterConversation(userId: string, userType: 'patient' | 'doctor', conversationId: string) {
  return request<IMConversationVO>({
    url: `/im/conversations/${conversationId}/enter`,
    method: 'post',
    params: { userId, userType }
  })
}

export function getMessages(userId: string, userType: 'patient' | 'doctor', conversationId: string) {
  return request<MessageVO[]>({
    url: `/im/messages/${conversationId}`,
    method: 'get',
    params: { userId, userType }
  })
}

export function sendMessage(params: SendMessageParams) {
  return request<MessageVO>({
    url: '/im/messages/send',
    method: 'post',
    params: params
  })
}

export function getUnreadCount(userId: string, userType: 'patient' | 'doctor') {
  return request<number>({
    url: '/im/unread-count',
    method: 'get',
    params: { userId, userType }
  })
}
