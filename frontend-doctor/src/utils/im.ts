/**
 * 腾讯IM SDK工具类
 * 集成腾讯云即时通信SDK，实现实时聊天功能
 */
import TIM from 'tim-js-sdk'
import type { IMConfig, IMEventListener, IMEventType, MessageVO, ChatMessage } from '@/types/im'
import { getUserSig } from '@/api/im'

// 简单的消息提示函数
const showMessage = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'info') => {
  console.log(`[IM ${type.toUpperCase()}] ${message}`)
}

/**
 * IM服务管理类
 */
class IMServiceManager {
  private tim: any = null
  private config: IMConfig = {
    sdkAppId: 1600043565
  }
  private listeners: Map<string, Array<Function>> = new Map()
  private initialized: boolean = false

  /**
   * 初始化IM服务
   */
  async init(userId: string, userType: string): Promise<boolean> {
    try {
      if (this.initialized && this.tim) {
        return true
      }

      console.log('[IM] 开始初始化TIM SDK...')
      
      // 创建SDK实例
      this.tim = TIM.create({
        SDKAppID: this.config.sdkAppId
      })
      
      this.tim.setLogLevel(0) // 关闭日志

      // 获取UserSig并登录
      const userSigData = await getUserSig(userId, userType)
      console.log('[IM] UserSig获取成功:', userSigData.userId)

      const loginRes = await this.tim.login({
        userID: userSigData.userId,
        userSig: userSigData.userSig
      })

      console.log('[IM] 登录成功:', loginRes)

      // 注册事件监听
      this.registerEvents()

      this.initialized = true
      this.emit('onSdkReady')

      return true

    } catch (error: any) {
      console.error('[IM] 初始化失败:', error)
      showMessage('IM初始化失败: ' + (error.message || '未知错误'), 'error')
      return false
    }
  }

  get TIM() {
    return TIM || {}
  }

  /**
   * 注册事件监听
   */
  private registerEvents() {
    if (!this.tim) return
    
    // 新消息通知
    this.tim.on(TIM.EVENT.MESSAGE_RECEIVED, (event: any) => {
      const msgList = event.data || []
      for (const msg of msgList) {
        this.emit('onMessageReceived', msg)
      }
    })
    
    // 会话列表更新
    this.tim.on(TIM.EVENT.CONVERSATION_LIST_UPDATED, (event: any) => {
      this.emit('onConversationListUpdate', event)
    })
    
    // 被踢下线
    this.tim.on(TIM.EVENT.KICKED_OUT, (event: any) => {
      this.emit('onKickedOut', event)
      showMessage('您已在其他设备登录', 'warning')
    })
    
    // 网络状态变化
    this.tim.on(TIM.EVENT.NET_STATE_CHANGE, (event: any) => {
      this.emit('onNetStateChange', event)
    })
  }

  /**
   * 发送文本消息
   */
  async sendTextMessage(conversationId: string, text: string): Promise<any> {
    try {
      const message = this.tim.createTextMessage({
        to: conversationId.replace('C2C_', ''),
        conversationType: this.TIM.TYPES.CONV_C2C,
        payload: { text }
      })

      const res = await this.tim.sendMessage(message)
      console.log('[IM] 消息发送成功:', res)
      return res

    } catch (error: any) {
      console.error('[IM] 消息发送失败:', error)
      throw error
    }
  }

  /**
   * 获取历史消息列表
   */
  async getMessageList(conversationId: string, count: number = 20): Promise<any[]> {
    try {
      const res = await this.tim.getMessageList({
        conversationID: conversationId,
        count: count
      })
      return res.data?.messageList || []

    } catch (error: any) {
      console.error('[IM] 获取消息列表失败:', error)
      throw error
    }
  }

  /**
   * 标记消息已读
   */
  async setMessageRead(conversationId: string): Promise<void> {
    try {
      await this.tim.setMessageRead({ conversationID: conversationId })
    } catch (error) {
      console.warn('[IM] 标记已读失败:', error)
    }
  }

  /**
   * 将TIM消息转换为VO
   */
  convertMessage(msg: any): MessageVO {
    let content = '[消息]'
    let type = 'text'

    if (msg.type === this.TIM.TYPES.MSG_TEXT) {
      content = msg.payload?.text || ''
      type = 'text'
    } else if (msg.type === this.TIM.TYPES.MSG_IMAGE) {
      content = msg.payload?.imageUrl || '[图片]'
      type = 'image'
    } else if (msg.type === this.TIM.TYPES.MSG_CUSTOM) {
      content = '[自定义消息]'
      type = 'custom'
    }

    return {
      id: msg.ID,
      from: msg.from,
      to: msg.to,
      content,
      type,
      time: msg.time * 1000,
      flow: msg.flow
    }
  }

  /**
   * 将MessageVO转换为ChatMessage
   */
  convertToChatMessage(msg: MessageVO, patientAvatar?: string, doctorAvatar?: string): ChatMessage {
    const isSelf = msg.flow === 'out'

    return {
      id: msg.id,
      content: msg.content,
      avatar: isSelf ? doctorAvatar : patientAvatar,
      time: new Date(msg.time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      type: isSelf ? 'doctor' : 'patient'
    }
  }

  /**
   * 事件监听
   */
  on(event: IMEventType | string, callback: Function) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event)?.push(callback)
  }

  /**
   * 移除事件监听
   */
  off(event: IMEventType | string, callback: Function) {
    const callbacks = this.listeners.get(event)
    if (callbacks) {
      const index = callbacks.indexOf(callback)
      if (index > -1) callbacks.splice(index, 1)
    }
  }

  /**
   * 触发事件
   */
  private emit(event: string, data?: any) {
    const callbacks = this.listeners.get(event)
    if (callbacks) {
      callbacks.forEach(cb => cb(data))
    }
  }

  /**
   * 销毁实例
   */
  destroy() {
    if (this.tim) {
      this.tim.logout()
      this.tim = null
    }
    this.listeners.clear()
    this.initialized = false
  }
}

export const imService = new IMServiceManager()
