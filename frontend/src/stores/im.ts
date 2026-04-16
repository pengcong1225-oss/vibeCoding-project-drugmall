/**
 * IM即时通讯 Pinia Store
 * 管理IM相关状态、会话、消息等
 * 真实模式：使用TIM SDK直接与腾讯IM服务器通信
 * Mock模式：通过后端API获取模拟数据
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { imSDK } from '@/utils/im-sdk-manager';
import {
  getConversations as apiGetConversations,
  enterConversation as apiEnterConversation,
  getMessages as apiGetMessages,
  sendMessage as apiSendMessage
} from '@/api/modules/im';
import type {
  IMUserInfo,
  IMConversation,
  IMMessage
} from '@/types/im';

export const useIMStore = defineStore('im', () => {
  // ============ State ============
  const isInitialized = ref(false);
  const isLoggedIn = ref(false);
  const currentUser = ref<IMUserInfo | null>(null);
  const conversations = ref<IMConversation[]>([]);
  const currentConversation = ref<IMConversation | null>(null);
  const messages = ref<IMMessage[]>([]);
  const unreadTotalCount = ref(0);
  const isLoading = ref(false);
  const hasMoreMessages = ref(true);

  // ============ Getters ============
  const sortedConversations = computed(() => {
    return [...conversations.value].sort((a, b) => {
      return new Date(b.lastMessageTime).getTime() - new Date(a.lastMessageTime).getTime();
    });
  });

  const currentConversationMessages = computed(() => {
    return messages.value;
  });

  const currentConversationUnreadCount = computed(() => {
    return currentConversation.value?.unreadCount || 0;
  });

  // ============ Actions ============

  /**
   * 初始化IM SDK
   */
  async function initialize() {
    if (isInitialized.value) return;

    try {
      const sdkAppId = Number(import.meta.env.VITE_IM_SDK_APP_ID) || 1600043565;
      imSDK.init(sdkAppId);

      // 真实模式下注册SDK事件回调
      if (imSDK.isRealMode()) {
        setupSDKEventHandlers();
      }

      isInitialized.value = true;
      console.log('[IM Store] SDK初始化成功, 模式:', imSDK.getMode());
    } catch (error) {
      console.error('[IM Store] SDK初始化失败:', error);
      throw error;
    }
  }

  /**
   * 注册TIM SDK事件处理器（真实模式）
   */
  function setupSDKEventHandlers() {
    // 收到新消息
    imSDK.on('MESSAGE_RECEIVED', (msgList: any[]) => {
      if (!Array.isArray(msgList)) return;
      for (const timMsg of msgList) {
        const msg: IMMessage = {
          id: timMsg.ID,
          conversationId: timMsg.conversationID,
          from: timMsg.from,
          type: timMsg.type === 'TIMTextElem' ? 'text' : timMsg.type,
          content: timMsg.payload?.text || '[消息]',
          time: formatTimestamp(timMsg.time),
          status: 'sent',
          isSelf: timMsg.flow === 'out'
        };
        handleNewMessage(msg);
      }
    });

    // 会话列表更新
    imSDK.on('CONVERSATION_UPDATED', async () => {
      await loadConversations();
    });

    // 未读数变化
    imSDK.on('UNREAD_COUNT_UPDATED', (count: number) => {
      unreadTotalCount.value = count;
    });
  }

  /**
   * 登录IM
   */
  async function login(userId: string, userType: 'patient' | 'doctor') {
    if (!isInitialized.value) {
      await initialize();
    }

    try {
      const userInfo = await imSDK.login(userId, userType);
      currentUser.value = userInfo;
      isLoggedIn.value = true;

      // 加载会话列表
      await loadConversations();

      console.log('[IM Store] 登录成功:', userInfo.userId);
    } catch (error) {
      console.error('[IM Store] 登录失败:', error);
      throw error;
    }
  }

  /**
   * 登出IM
   */
  async function logout() {
    try {
      await imSDK.logout();
      isLoggedIn.value = false;
      currentUser.value = null;
      conversations.value = [];
      currentConversation.value = null;
      messages.value = [];
      unreadTotalCount.value = 0;
      console.log('[IM Store] 登出成功');
    } catch (error) {
      console.error('[IM Store] 登出失败:', error);
      throw error;
    }
  }

  /**
   * 加载会话列表
   */
  async function loadConversations(userId?: string, userType?: 'patient' | 'doctor') {
    if (!isLoggedIn.value) return;

    try {
      isLoading.value = true;

      if (imSDK.isRealMode()) {
        // 真实模式：通过TIM SDK获取
        conversations.value = await imSDK.getConversationList();
        // 更新未读数
        unreadTotalCount.value = await imSDK.getTotalUnreadCount();
      } else {
        // Mock模式：通过后端API获取
        const uid = userId || currentUser.value?.userId?.split('_')[1];
        const uType = userType || currentUser.value?.userId?.split('_')[0] as 'patient' | 'doctor';

        if (!uid || !uType) {
          console.warn('[IM Store] 用户信息不完整，无法加载会话列表');
          return;
        }

        const { data } = await apiGetConversations({ userId: uid, userType: uType });
        conversations.value = data || [];
        calculateTotalUnread();
      }

      console.log('[IM Store] 会话列表加载成功，共', conversations.value.length, '个会话');
    } catch (error) {
      console.error('[IM Store] 加载会话列表失败:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * 进入会话
   */
  async function enterConversation(conversationId: string) {
    if (!isLoggedIn.value) return;

    try {
      isLoading.value = true;

      if (imSDK.isRealMode()) {
        // 真实模式：通过TIM SDK标记已读
        await imSDK.setMessageRead(conversationId);
      } else {
        // Mock模式：通过后端API
        const uid = currentUser.value?.userId?.split('_')[1];
        const uType = currentUser.value?.userId?.split('_')[0] as 'patient' | 'doctor';
        if (uid && uType) {
          await apiEnterConversation({
            userId: uid,
            userType: uType,
            conversationId
          });
        }
      }

      // 查找会话
      const conversation = conversations.value.find(c => c.conversationId === conversationId);
      if (conversation) {
        currentConversation.value = conversation;
        conversation.unreadCount = 0;
      }

      // 加载消息历史
      await loadMessageHistory(conversationId);

      // 更新总未读数
      calculateTotalUnread();

      console.log('[IM Store] 进入会话:', conversationId);
    } catch (error) {
      console.error('[IM Store] 进入会话失败:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * 加载消息历史
   */
  async function loadMessageHistory(conversationId?: string) {
    const convId = conversationId || currentConversation.value?.conversationId;
    if (!convId || !isLoggedIn.value) return;

    try {
      isLoading.value = true;

      if (imSDK.isRealMode()) {
        // 真实模式：通过TIM SDK获取消息列表
        const result = await imSDK.getMessageList(convId);
        messages.value = result.messages;
        hasMoreMessages.value = !result.isCompleted;
      } else {
        // Mock模式：通过后端API
        const uid = currentUser.value?.userId?.split('_')[1];
        if (!uid) return;

        const { data } = await apiGetMessages({
          userId: uid,
          conversationId: convId
        });

        messages.value = data || [];
        hasMoreMessages.value = false;
      }

      console.log('[IM Store] 消息历史加载成功，共', messages.value.length, '条消息');
    } catch (error) {
      console.error('[IM Store] 加载消息历史失败:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * 发送文本消息
   */
  async function sendTextMessage(content: string) {
    if (!currentConversation.value || !isLoggedIn.value) return;

    try {
      if (imSDK.isRealMode()) {
        // 真实模式：通过TIM SDK发送
        const msg = await imSDK.sendTextMessage(
          currentConversation.value.conversationId,
          content
        );
        messages.value.push(msg);
      } else {
        // Mock模式：通过后端API
        const uid = currentUser.value?.userId?.split('_')[1];
        const uType = currentUser.value?.userId?.split('_')[0] as 'patient' | 'doctor';

        if (!uid || !uType) return;

        const { data } = await apiSendMessage({
          userId: uid,
          userType: uType,
          conversationId: currentConversation.value.conversationId,
          type: 'text',
          content
        });

        if (data) {
          messages.value.push({
            id: data.id,
            from: uType + '_' + uid,
            type: 'text',
            content: data.content,
            time: data.time,
            status: 'sent',
            isSelf: true
          });
        }
      }

      console.log('[IM Store] 消息发送成功');
    } catch (error) {
      console.error('[IM Store] 发送消息失败:', error);
      throw error;
    }
  }

  /**
   * 标记会话已读
   */
  async function markRead(conversationId: string) {
    try {
      if (imSDK.isRealMode()) {
        // 真实模式：通过TIM SDK
        await imSDK.setMessageRead(conversationId);
      } else {
        // Mock模式：通过后端API
        const uid = currentUser.value?.userId?.split('_')[1];
        const uType = currentUser.value?.userId?.split('_')[0] as 'patient' | 'doctor';

        if (!uid || !uType) return;

        await apiEnterConversation({
          userId: uid,
          userType: uType,
          conversationId
        });
      }

      // 更新本地未读数
      const conversation = conversations.value.find(c => c.conversationId === conversationId);
      if (conversation) {
        conversation.unreadCount = 0;
      }

      calculateTotalUnread();
    } catch (error) {
      console.error('[IM Store] 标记已读失败:', error);
    }
  }

  /**
   * 处理新消息
   */
  function handleNewMessage(newMessage: IMMessage) {
    // 如果是当前会话的消息，添加到列表
    if (
      currentConversation.value &&
      newMessage.conversationId === currentConversation.value.conversationId
    ) {
      messages.value.push(newMessage);
    }

    // 更新会话列表中的最后一条消息
    const conversation = conversations.value.find(
      c => c.conversationId === newMessage.conversationId
    );
    if (conversation) {
      conversation.lastMessage = newMessage;
      conversation.lastMessageTime = newMessage.time;

      // 如果不是当前会话，增加未读数
      if (currentConversation.value?.conversationId !== newMessage.conversationId) {
        conversation.unreadCount = (conversation.unreadCount || 0) + 1;
      }
    }

    calculateTotalUnread();
  }

  /**
   * 计算总未读数
   */
  function calculateTotalUnread() {
    unreadTotalCount.value = conversations.value.reduce(
      (total, conv) => total + (conv.unreadCount || 0),
      0
    );
  }

  /**
   * 格式化时间戳
   */
  function formatTimestamp(timestamp: number): string {
    if (!timestamp) return '';
    const date = new Date(timestamp * 1000);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  return {
    // State
    isInitialized,
    isLoggedIn,
    currentUser,
    conversations,
    currentConversation,
    messages,
    unreadTotalCount,
    isLoading,
    hasMoreMessages,
    // Getters
    sortedConversations,
    currentConversationMessages,
    currentConversationUnreadCount,
    // Actions
    initialize,
    login,
    logout,
    loadConversations,
    enterConversation,
    loadMessageHistory,
    sendTextMessage,
    markRead,
    handleNewMessage
  };
});
