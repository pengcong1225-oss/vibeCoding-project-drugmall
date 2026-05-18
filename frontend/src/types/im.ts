// IM即时通讯相关类型定义

// IM运行模式
export type IMMode = 'mock' | 'real'

// 消息类型枚举
export enum MessageType {
  TEXT = 'text',
  IMAGE = 'image',
  VOICE = 'voice',
  PRESCRIPTION = 'prescription',
  SYSTEM = 'system'
}

// IM用户信息（登录后返回）
export interface IMUserInfo {
  userId: string
  userSig: string
  sdkAppId: number
  expireTime: number
}

// IM会话
export interface IMConversation {
  conversationId: string
  type: 'C2C' | 'GROUP'
  targetUserId: string
  targetUserName: string
  targetUserAvatar: string
  lastMessage: IMMessage | null
  unreadCount: number
  lastMessageTime: string
  consultationId?: string
}

// IM消息
export interface IMMessage {
  id: string
  conversationId?: string
  from: string
  type: string
  content: string
  time: string
  status: 'sending' | 'sent' | 'read'
  isSelf: boolean
}

// 获取UserSig请求参数
export interface GetUserSigParams {
  userId: string
  userType: 'patient' | 'doctor'
}

// 获取会话列表请求参数
export interface GetConversationsParams {
  userId: string
  userType: 'patient' | 'doctor'
}

// 进入会话请求参数
export interface EnterConversationParams {
  userId: string
  userType: 'patient' | 'doctor'
  conversationId: string
}

// 获取消息请求参数
export interface GetMessagesParams {
  userId: string
  userType: 'patient' | 'doctor'
  conversationId: string
}

// 发送消息请求参数
export interface SendMessageParams {
  userId: string
  userType: 'patient' | 'doctor'
  conversationId: string
  type: string
  content: string
}

// 获取未读数请求参数
export interface GetUnreadCountParams {
  userId: string
  userType: 'patient' | 'doctor'
}
