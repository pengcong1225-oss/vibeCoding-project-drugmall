/**
 * 腾讯IM SDK管理器
 * 封装TIM SDK的初始化、登录、事件监听等功能
 * 支持Mock模式和真实SDK模式
 */
import TIM from 'tim-js-sdk';
import type {
  IMMode,
  IMUserInfo,
  IMConversation,
  IMMessage
} from '@/types/im';
import {
  getUserSig,
  getConversations as apiGetConversations,
  enterConversation as apiEnterConversation,
  getMessages as apiGetMessages,
  sendMessage as apiSendMessage,
  getUnreadCount as apiGetUnreadCount
} from '@/api/modules/im';

/**
 * IM SDK Manager单例
 */
class IMSDKManager {
  private static instance: IMSDKManager;
  private tim: any = null;
  private isInitialized = false;
  private isLoggedIn = false;
  private currentUser: IMUserInfo | null = null;
  private mode: IMMode = (import.meta.env.VITE_IM_MODE as IMMode) || 'mock';
  private eventListeners: Map<string, Function[]> = new Map();
  private sdkReady = false;

  private constructor() {}

  public static getInstance(): IMSDKManager {
    if (!IMSDKManager.instance) {
      IMSDKManager.instance = new IMSDKManager();
    }
    return IMSDKManager.instance;
  }

  /**
   * 获取当前模式
   */
  public getMode(): IMMode {
    return this.mode;
  }

  /**
   * 是否为真实模式
   */
  public isRealMode(): boolean {
    return this.mode === 'real';
  }

  /**
   * 获取TIM实例（仅real模式）
   */
  public getTIM(): any {
    if (this.mode !== 'real') {
      console.warn('[IM SDK] Mock模式下无法获取TIM实例');
      return null;
    }
    return this.tim;
  }

  /**
   * 检查是否已登录
   */
  public checkLogin(): boolean {
    return this.isLoggedIn;
  }

  /**
   * SDK是否就绪
   */
  public isSdkReady(): boolean {
    return this.sdkReady;
  }

  /**
   * 获取当前用户信息
   */
  public getCurrentUser(): IMUserInfo | null {
    return this.currentUser;
  }

  /**
   * 初始化SDK
   */
  public init(sdkAppId: number): void {
    if (this.isInitialized) {
      console.warn('[IM SDK] SDK已初始化');
      return;
    }

    if (this.mode === 'real') {
      // 真实模式：初始化TIM SDK
      this.tim = TIM.create({
        SDKAppID: sdkAppId
      });
      this.tim.setLogLevel(1);
      this.bindEventListeners();
      console.log('[IM SDK] 真实模式初始化成功, SDKAppID:', sdkAppId);
    } else {
      console.log('[IM SDK] Mock模式初始化成功');
    }

    this.isInitialized = true;
  }

  /**
   * 登录IM
   */
  public async login(userId: string, userType: 'patient' | 'doctor'): Promise<IMUserInfo> {
    try {
      // 从后端获取UserSig
      const { data } = await getUserSig({ userId, userType });

      this.currentUser = data;

      if (this.mode === 'real' && this.tim) {
        // 真实模式：调用TIM SDK登录
        const loginRes = await this.tim.login({
          userID: data.userId,
          userSig: data.userSig
        });
        this.isLoggedIn = true;
        console.log('[IM SDK] 真实模式登录成功:', data.userId, loginRes);
      } else {
        // Mock模式：模拟登录成功
        this.isLoggedIn = true;
        console.log('[IM SDK] Mock模式登录成功:', data.userId);
      }

      return data;
    } catch (error) {
      console.error('[IM SDK] 登录失败:', error);
      throw error;
    }
  }

  /**
   * 登出IM
   */
  public async logout(): Promise<void> {
    if (!this.isLoggedIn) {
      return;
    }

    try {
      if (this.mode === 'real' && this.tim) {
        await this.tim.logout();
      }
      this.isLoggedIn = false;
      this.currentUser = null;
      this.sdkReady = false;
      console.log('[IM SDK] 登出成功');
    } catch (error) {
      console.error('[IM SDK] 登出失败:', error);
      throw error;
    }
  }

  // ============ 真实模式下的TIM SDK操作 ============

  /**
   * 获取会话列表（真实模式通过TIM SDK）
   */
  public async getConversationList(): Promise<IMConversation[]> {
    if (this.mode !== 'real' || !this.tim) {
      throw new Error('仅支持真实模式');
    }

    try {
      const res = await this.tim.getConversationList();
      const convList = res.data.conversationList || [];

      return convList.map((conv: any) => ({
        conversationId: conv.conversationID,
        type: conv.type === TIM.TYPES.CONV_C2C ? 'C2C' : 'GROUP',
        targetUserId: conv.type === TIM.TYPES.CONV_C2C ? conv.userProfile?.userID || '' : conv.groupProfile?.groupID || '',
        targetUserName: conv.type === TIM.TYPES.CONV_C2C ? (conv.userProfile?.nick || conv.userProfile?.userID || '') : (conv.groupProfile?.name || ''),
        targetUserAvatar: conv.type === TIM.TYPES.CONV_C2C ? (conv.userProfile?.avatar || '') : (conv.groupProfile?.avatar || ''),
        lastMessage: conv.lastMessage ? {
          id: conv.lastMessage.ID || '',
          from: conv.lastMessage.from || '',
          type: this.convertMsgType(conv.lastMessage.type),
          content: this.extractMsgContent(conv.lastMessage),
          time: this.formatTimestamp(conv.lastMessage.lastTime),
          status: 'sent',
          isSelf: conv.lastMessage.flow === 'out'
        } : null,
        unreadCount: conv.unreadCount || 0,
        lastMessageTime: conv.lastMessage ? this.formatTimestamp(conv.lastMessage.lastTime) : '',
        consultationId: ''
      })) as IMConversation[];
    } catch (error) {
      console.error('[IM SDK] 获取会话列表失败:', error);
      throw error;
    }
  }

  /**
   * 获取消息列表（真实模式通过TIM SDK）
   */
  public async getMessageList(conversationId: string, nextReqMessageID?: string): Promise<{ messages: IMMessage[]; isCompleted: boolean }> {
    if (this.mode !== 'real' || !this.tim) {
      throw new Error('仅支持真实模式');
    }

    try {
      const options: any = {
        conversationID: conversationId,
        count: 20
      };
      if (nextReqMessageID) {
        options.nextReqMessageID = nextReqMessageID;
      }

      const res = await this.tim.getMessageList(options);
      const msgList = res.data.messageList || [];

      const messages: IMMessage[] = msgList.map((msg: any) => ({
        id: msg.ID,
        conversationId: conversationId,
        from: msg.from,
        type: this.convertMsgType(msg.type),
        content: this.extractMsgContent(msg),
        time: this.formatTimestamp(msg.time),
        status: msg.status === 'unSend' ? 'sending' : 'sent',
        isSelf: msg.flow === 'out'
      }));

      return {
        messages,
        isCompleted: res.data.isCompleted || false
      };
    } catch (error) {
      console.error('[IM SDK] 获取消息列表失败:', error);
      throw error;
    }
  }

  /**
   * 发送文本消息（真实模式通过TIM SDK）
   */
  public async sendTextMessage(conversationId: string, text: string): Promise<IMMessage> {
    if (this.mode !== 'real' || !this.tim) {
      throw new Error('仅支持真实模式');
    }

    try {
      const message = this.tim.createTextMessage({
        to: this.extractTargetId(conversationId),
        conversationType: conversationId.startsWith('C2C') ? TIM.TYPES.CONV_C2C : TIM.TYPES.CONV_GROUP,
        payload: { text }
      });

      const res = await this.tim.sendMessage(message);
      const sentMsg = res.data.message;

      return {
        id: sentMsg.ID,
        conversationId: conversationId,
        from: sentMsg.from,
        type: 'text',
        content: text,
        time: this.formatTimestamp(sentMsg.time),
        status: 'sent',
        isSelf: true
      };
    } catch (error) {
      console.error('[IM SDK] 发送消息失败:', error);
      throw error;
    }
  }

  /**
   * 标记会话已读（真实模式通过TIM SDK）
   */
  public async setMessageRead(conversationId: string): Promise<void> {
    if (this.mode !== 'real' || !this.tim) {
      return;
    }

    try {
      await this.tim.setMessageRead({ conversationID: conversationId });
      console.log('[IM SDK] 标记已读成功:', conversationId);
    } catch (error) {
      console.error('[IM SDK] 标记已读失败:', error);
    }
  }

  /**
   * 获取总未读数（真实模式通过TIM SDK）
   */
  public async getTotalUnreadCount(): Promise<number> {
    if (this.mode !== 'real' || !this.tim) {
      return 0;
    }

    try {
      const res = await this.tim.getTotalUnreadMessageCount();
      return res.data || 0;
    } catch (error) {
      console.error('[IM SDK] 获取未读数失败:', error);
      return 0;
    }
  }

  // ============ 事件绑定 ============

  /**
   * 绑定事件监听（仅real模式）
   */
  private bindEventListeners(): void {
    if (!this.tim || this.mode !== 'real') return;

    // SDK就绪
    this.tim.on(TIM.EVENT.SDK_READY, (event: any) => {
      console.log('[IM SDK] SDK Ready');
      this.sdkReady = true;
      this.emitEvent('SDK_READY', event);
    });

    // SDK未就绪
    this.tim.on(TIM.EVENT.SDK_NOT_READY, (event: any) => {
      console.warn('[IM SDK] SDK Not Ready');
      this.sdkReady = false;
      this.emitEvent('SDK_NOT_READY', event);
    });

    // SDK出错
    this.tim.on(TIM.EVENT.ERROR, (event: any) => {
      console.error('[IM SDK] SDK Error:', event);
      this.emitEvent('SDK_ERROR', event);
    });

    // 收到新消息
    this.tim.on(TIM.EVENT.MESSAGE_RECEIVED, (event: any) => {
      console.log('[IM SDK] 收到新消息:', event.data);
      this.emitEvent('MESSAGE_RECEIVED', event.data);
    });

    // 消息已修改（撤回等）
    this.tim.on(TIM.EVENT.MESSAGE_MODIFIED, (event: any) => {
      console.log('[IM SDK] 消息已修改:', event.data);
      this.emitEvent('MESSAGE_MODIFIED', event.data);
    });

    // 会话列表更新
    this.tim.on(TIM.EVENT.CONVERSATION_LIST_UPDATED, (event: any) => {
      console.log('[IM SDK] 会话列表更新:', event.data);
      this.emitEvent('CONVERSATION_UPDATED', event.data);
    });

    // 未读数变化
    this.tim.on(TIM.EVENT.TOTAL_UNREAD_MESSAGE_COUNT_UPDATED, (event: any) => {
      console.log('[IM SDK] 未读数变化:', event.data);
      this.emitEvent('UNREAD_COUNT_UPDATED', event.data);
    });

    // 被踢下线
    this.tim.on(TIM.EVENT.KICKED_OUT, (event: any) => {
      console.warn('[IM SDK] 被踢下线:', event.data);
      this.emitEvent('KICKED_OUT', event.data);
      this.logout();
    });

    // 网络状态变化
    this.tim.on(TIM.EVENT.NET_STATE_CHANGE, (event: any) => {
      console.log('[IM SDK] 网络状态变化:', event.data);
      this.emitEvent('NETWORK_CHANGE', event.data);
    });
  }

  /**
   * 添加事件监听
   */
  public on(event: string, handler: Function): void {
    if (!this.eventListeners.has(event)) {
      this.eventListeners.set(event, []);
    }
    this.eventListeners.get(event)!.push(handler);
  }

  /**
   * 移除事件监听
   */
  public off(event: string, handler: Function): void {
    const handlers = this.eventListeners.get(event);
    if (handlers) {
      const index = handlers.indexOf(handler);
      if (index > -1) {
        handlers.splice(index, 1);
      }
    }
  }

  /**
   * 触发事件
   */
  private emitEvent(event: string, data: any): void {
    const handlers = this.eventListeners.get(event);
    if (handlers) {
      handlers.forEach(handler => handler(data));
    }
  }

  // ============ 工具方法 ============

  /**
   * 转换TIM SDK消息类型到业务类型
   */
  private convertMsgType(timType: string): string {
    switch (timType) {
      case TIM.TYPES.MSG_TEXT: return 'text';
      case TIM.TYPES.MSG_IMAGE: return 'image';
      case TIM.TYPES.MSG_AUDIO: return 'voice';
      case TIM.TYPES.MSG_CUSTOM: return 'custom';
      default: return 'text';
    }
  }

  /**
   * 提取消息文本内容
   */
  private extractMsgContent(msg: any): string {
    if (!msg || !msg.payload) return '';
    if (msg.type === TIM.TYPES.MSG_TEXT) {
      return msg.payload.text || '';
    }
    if (msg.type === TIM.TYPES.MSG_IMAGE) {
      return '[图片]';
    }
    if (msg.type === TIM.TYPES.MSG_AUDIO) {
      return '[语音]';
    }
    if (msg.type === TIM.TYPES.MSG_CUSTOM) {
      try {
        const data = JSON.parse(msg.payload.data || '{}');
        return data.description || '[自定义消息]';
      } catch {
        return '[自定义消息]';
      }
    }
    return '[消息]';
  }

  /**
   * 格式化时间戳为 HH:mm
   */
  private formatTimestamp(timestamp: number): string {
    if (!timestamp) return '';
    const date = new Date(timestamp * 1000);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  /**
   * 从会话ID中提取目标用户ID
   */
  private extractTargetId(conversationId: string): string {
    if (conversationId.startsWith('C2C')) {
      return conversationId.replace(/^C2C_?/, '');
    }
    if (conversationId.startsWith('GROUP')) {
      return conversationId.replace(/^GROUP_?/, '');
    }
    return conversationId;
  }

  /**
   * 销毁SDK实例
   */
  public destroy(): void {
    if (this.tim && this.mode === 'real') {
      this.tim.destroy();
    }
    this.isInitialized = false;
    this.isLoggedIn = false;
    this.currentUser = null;
    this.sdkReady = false;
    this.eventListeners.clear();
    console.log('[IM SDK] SDK已销毁');
  }
}

// 导出单例
export const imSDK = IMSDKManager.getInstance();
export default imSDK;
