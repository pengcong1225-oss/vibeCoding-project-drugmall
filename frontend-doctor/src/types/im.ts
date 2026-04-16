/**
 * IM即时通讯类型定义
 */

// 用户签名信息
export interface UserSigVO {
  userId: string
  userSig: string
  sdkAppId: number
  expireTime: number
}

// 会话信息
export interface IMConversationVO {
  conversationId: string
  type: 'C2C' | 'GROUP' // 单聊 | 群聊
  peerAccount?: string // 对方账号
  peerName?: string // 对方昵称
  peerAvatar?: string // 对方头像
  lastMessage?: MessageVO
  unreadCount: number
  lastTime?: number
}

// 消息信息
export interface MessageVO {
  msgId?: string
  conversationId: string
  fromAccount: string
  toAccount: string
  msgType: 'TIMTextElem' | 'TIMImageElem' | 'TIMCustomElem' | 'TIMSoundElem'
  content: string
  time: number
  isRead?: boolean
  isSelf?: boolean
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
  | 'onMessageReceived' // 收到新消息
  | 'onConversationListUpdate' // 会话列表更新
  | 'onConversationRead' // 会话已读
  | 'onGroupListUpdate' // 群组列表更新
  | 'onSdkReady' // SDK Ready
  | 'onKickedOut' // 被踢下线
  | 'onNetStateChange' // 网络状态变化

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
