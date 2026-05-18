import TIM from 'tim-js-sdk'
import type { IMConfig, IMEventListener, IMEventType, MessageVO, ChatMessage, TIMRawMessage } from '@/types/im'
import { getUserSig } from '@/api/im'

const showMessage = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'info') => {
  console.log(`[IM ${type.toUpperCase()}] ${message}`)
}

class IMServiceManager {
  private tim: any = null
  private config: IMConfig = {
    sdkAppId: 1600043565
  }
  private listeners: Map<string, Array<Function>> = new Map()
  private initialized: boolean = false
  private sdkReady: boolean = false

  async init(userId: string, userType: string): Promise<boolean> {
    try {
      if (this.initialized && this.tim && this.sdkReady) {
        return true
      }

      console.log('[IM] 开始初始化TIM SDK...')

      this.tim = TIM.create({
        SDKAppID: this.config.sdkAppId
      })

      this.tim.setLogLevel(1)

      this.registerEvents()

      const userSigData = await getUserSig(userId, userType as 'patient' | 'doctor')
      console.log('[IM] UserSig获取成功:', userSigData.userId)

      const loginRes = await this.tim.login({
        userID: userSigData.userId,
        userSig: userSigData.userSig
      })

      console.log('[IM] 登录成功:', loginRes)

      this.initialized = true

      await this.waitSdkReady()

      this.emit('onSdkReady')

      return true

    } catch (error: any) {
      console.error('[IM] 初始化失败:', error)
      showMessage('IM初始化失败: ' + (error.message || '未知错误'), 'error')
      return false
    }
  }

  private waitSdkReady(): Promise<void> {
    if (this.sdkReady) {
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        reject(new Error('SDK Ready超时'))
      }, 10000)

      const onReady = () => {
        clearTimeout(timeout)
        this.tim.off(TIM.EVENT.SDK_READY, onReady)
        resolve()
      }

      this.tim.on(TIM.EVENT.SDK_READY, onReady)
    })
  }

  get TIM() {
    return TIM || {}
  }

  get isInitialized() {
    return this.initialized && this.sdkReady
  }

  private registerEvents() {
    if (!this.tim) return

    this.tim.on(TIM.EVENT.SDK_READY, () => {
      console.log('[IM] SDK Ready')
      this.sdkReady = true
      this.emit('onSdkReady')
    })

    this.tim.on(TIM.EVENT.SDK_NOT_READY, () => {
      console.warn('[IM] SDK Not Ready')
      this.sdkReady = false
      this.emit('onSdkNotReady')
    })

    this.tim.on(TIM.EVENT.MESSAGE_RECEIVED, (event: any) => {
      const msgList = event.data || []
      for (const msg of msgList) {
        this.emit('onMessageReceived', msg)
      }
    })

    this.tim.on(TIM.EVENT.CONVERSATION_LIST_UPDATED, (event: any) => {
      this.emit('onConversationListUpdate', event)
    })

    this.tim.on(TIM.EVENT.KICKED_OUT, (event: any) => {
      this.emit('onKickedOut', event)
      this.sdkReady = false
      showMessage('您已在其他设备登录', 'warning')
    })

    this.tim.on(TIM.EVENT.NET_STATE_CHANGE, (event: any) => {
      this.emit('onNetStateChange', event)
    })

    this.tim.on(TIM.EVENT.ERROR, (event: any) => {
      console.error('[IM] SDK Error:', event)
    })
  }

  async sendTextMessage(conversationId: string, text: string): Promise<any> {
    try {
      if (!this.sdkReady) {
        throw new Error('SDK未就绪，无法发送消息')
      }

      const targetId = this.extractTargetId(conversationId)
      const isC2C = conversationId.startsWith('C2C')

      const message = this.tim.createTextMessage({
        to: targetId,
        conversationType: isC2C ? TIM.TYPES.CONV_C2C : TIM.TYPES.CONV_GROUP,
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

  async getMessageList(conversationId: string, count: number = 20): Promise<any[]> {
    try {
      if (!this.sdkReady) {
        console.warn('[IM] SDK未就绪，返回空消息列表')
        return []
      }

      const res = await this.tim.getMessageList({
        conversationID: conversationId,
        count: count
      })
      return res.data?.messageList || []

    } catch (error: any) {
      console.error('[IM] 获取消息列表失败:', error)
      return []
    }
  }

  async setMessageRead(conversationId: string): Promise<void> {
    try {
      if (!this.sdkReady) return
      await this.tim.setMessageRead({ conversationID: conversationId })
    } catch (error) {
      console.warn('[IM] 标记已读失败:', error)
    }
  }

  convertMessage(msg: any): TIMRawMessage {
    let content = '[消息]'
    let type = 'text'

    if (msg.type === TIM.TYPES.MSG_TEXT) {
      content = msg.payload?.text || ''
      type = 'text'
    } else if (msg.type === TIM.TYPES.MSG_IMAGE) {
      content = msg.payload?.imageInfoArray?.[0]?.url || '[图片]'
      type = 'image'
    } else if (msg.type === TIM.TYPES.MSG_CUSTOM) {
      content = '[自定义消息]'
      type = 'custom'
    } else if (msg.type === TIM.TYPES.MSG_AUDIO) {
      content = '[语音]'
      type = 'voice'
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

  private extractTargetId(conversationId: string): string {
    if (conversationId.startsWith('C2C')) {
      return conversationId.replace(/^C2C_?/, '')
    }
    if (conversationId.startsWith('GROUP')) {
      return conversationId.replace(/^GROUP_?/, '')
    }
    return conversationId
  }

  on(event: IMEventType | string, callback: Function) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event)?.push(callback)
  }

  off(event: IMEventType | string, callback: Function) {
    const callbacks = this.listeners.get(event)
    if (callbacks) {
      const index = callbacks.indexOf(callback)
      if (index > -1) callbacks.splice(index, 1)
    }
  }

  private emit(event: string, data?: any) {
    const callbacks = this.listeners.get(event)
    if (callbacks) {
      callbacks.forEach(cb => cb(data))
    }
  }

  destroy() {
    if (this.tim) {
      this.tim.logout()
      this.tim = null
    }
    this.listeners.clear()
    this.initialized = false
    this.sdkReady = false
  }
}

export const imService = new IMServiceManager()
