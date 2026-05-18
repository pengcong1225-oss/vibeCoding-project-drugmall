/**
 * IM即时通讯类型定义
 * 字段与后端 MessageVO / IMConversationVO 保持一致
 */

// 用户签名信息
export interface UserSigVO {
  userId: string
  userSig: string
  sdkAppId: number
  expireTime: number
}

// 会话信息（与后端 IMConversationVO 对齐）
export interface IMConversationVO {
  conversationId: string
  type: 'C2C' | 'GROUP'
  targetUserId: string       // 对方用户ID
  targetUserName: string     // 对方昵称
  targetUserAvatar: string   // 对方头像
  lastMessage?: MessageVO
  unreadCount: number
  lastMessageTime: string    // 最后消息时间
  consultationId?: string    // 关联的问诊ID
}

// 消息信息（与后端 MessageVO 对齐）
export interface MessageVO {
  id: string                 // 消息ID
  consultationId?: string    // 问诊ID
  sender: string             // 发送者 (doctor/patient/system)
  type: string               // 消息类型: text/image/voice/prescription
  content: string            // 消息内容
  time: string               // 发送时间 (HH:mm 或其他格式字符串)
  status: string             // 状态: sending/sent/read
}

// TIM SDK 原始消息（convertMessage 的中间格式）
export interface TIMRawMessage {
  id: string
  from: string
  to: string
  type: string
  content: string
  time: number
  flow: 'in' | 'out'
}

// 发送消息参数
export interface SendMessageParams {
  userId: string
  userType: 'patient' | 'doctor'
  conversationId: string
  type: 'text' | 'image' | 'custom' | 'audio'
  content: string
}

// IM SDK事件类型
export type IMEventType =
  | 'onMessageReceived'
  | 'onConversationListUpdate'
  | 'onConversationRead'
  | 'onGroupListUpdate'
  | 'onSdkReady'
  | 'onKickedOut'
  | 'onNetStateChange'

// IM事件监听器
export type IMEventListener = (event: any) => void

// IM初始化配置
export interface IMConfig {
  sdkAppId: number
  userId: string
  userSig: string
}

// 聊天消息显示格式
export interface ChatMessage {
  id: string
  type: 'system' | 'patient' | 'doctor'
  content: string
  time: string
  avatar?: string
  isRead?: boolean
}

// 问诊会话信息
export interface ConsultationInfo {
  id: string
  patientId: string
  patientName: string
  patientAvatar?: string
  patientAge?: number
  patientGender?: string
  status: 'pending' | 'processing' | 'completed' | 'cancelled'
  type: '图文问诊' | '电话问诊' | '视频问诊'
  symptoms?: string
  duration?: string
  createTime: string
}
