# DrugMall 腾讯IM在线问诊技术方案

## 1. 方案概述

### 1.1 背景
DrugMall药品电商平台需要支持患者端与医生端的在线问诊功能，包括图文问诊、实时聊天、处方开具等场景。

### 1.2 目标
- 实现患者与医生的实时在线沟通
- 支持文字、图片、语音、处方等多种消息类型
- 确保消息可靠投递与历史记录保存
- 提供良好的用户体验与系统稳定性

### 1.3 技术选型
- **IM SDK**: 腾讯云即时通信 IM (Tencent Cloud IM)
- **SDK版本**: Web SDK v3.x
- **AppID**: 1600043565

---

## 2. 系统架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端层                                 │
├─────────────────┬─────────────────┬─────────────────────────────┤
│   患者端(App)   │   医生端(App)   │      管理后台(Web)          │
│   (uni-app)     │   (uni-app)     │      (Vue3)                 │
└────────┬────────┴────────┬────────┴──────────────┬──────────────┘
         │                 │                       │
         └─────────────────┼───────────────────────┘
                           │
              ┌─────────────▼──────────────┐
              │    腾讯云 IM 服务          │
              │  - 即时消息                │
              │  - 群组管理                │
              │  - 用户签名                │
              └─────────────┬──────────────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
┌────────▼────────┐ ┌───────▼───────┐ ┌───────▼───────┐
│  DrugMall后端    │ │   文件存储    │ │   消息队列    │
│  (Spring Boot)   │ │  (COS/MinIO)  │ │  (RabbitMQ)   │
│                 │ │               │ │               │
│ - 用户系统      │ │ - 图片存储    │ │ - 异步通知    │
│ - 问诊订单      │ │ - 文件转存    │ │ - 消息持久化  │
│ - 处方管理      │ │               │ │               │
│ - 回调处理      │ │               │ │               │
└─────────────────┘ └───────────────┘ └───────────────┘
```

### 2.2 模块划分

| 模块 | 功能描述 | 技术实现 |
|------|----------|----------|
| IM基础模块 | 登录、登出、连接管理 | Tencent IM SDK |
| 消息模块 | 消息收发、历史记录 | IM SDK + 后端API |
| 会话模块 | 会话列表、会话管理 | IM SDK + 本地存储 |
| 群组模块 | 问诊群创建、成员管理 | IM SDK + 后端API |
| 用户模块 | 用户资料、医生/患者信息 | 后端User API |
| 文件模块 | 图片发送、文件上传 | COS/MinIO + IM SDK |
| 通知模块 | 推送通知、未读提醒 | 推送服务 + IM回调 |

---

## 3. 详细设计方案

### 3.1 IM用户体系设计

#### 3.1.1 用户ID映射

```
┌────────────────────────────────────────────────────────┐
│                    用户ID映射规则                       │
├────────────────────────────────────────────────────────┤
│  DrugMall用户ID    │    IM用户ID (UserID)              │
├───────────────────┼───────────────────────────────────┤
│  患者: P_12345    │  patient_12345                    │
│  医生: D_67890    │  doctor_67890                     │
│  系统: SYSTEM     │  system_admin                     │
└───────────────────┴───────────────────────────────────┘
```

#### 3.1.2 用户资料同步

```typescript
// IM用户资料结构
interface IMUserProfile {
  userID: string;           // IM用户ID
  nick: string;             // 昵称（医生姓名/患者姓名）
  avatar: string;           // 头像URL
  role: 'patient' | 'doctor' | 'system';  // 角色
  customData: {
    userType: string;       // 用户类型
    hospital?: string;      // 所属医院（医生）
    department?: string;    // 科室（医生）
    title?: string;         // 职称（医生）
  }
}
```

### 3.2 会话与群组设计

#### 3.2.1 问诊会话结构

```
┌──────────────────────────────────────────────────────────────┐
│                     问诊会话(Consultation)                     │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│   问诊订单创建                                                  │
│         │                                                       │
│         ▼                                                       │
│   ┌─────────────────────────┐                                  │
│   │    IM群组创建(2人)       │                                  │
│   │  ┌─────┐    ┌─────┐     │                                  │
│   │  │患者 │◄──►│医生 │     │                                  │
│   │  └──┬──┘    └──┬──┘     │                                  │
│   │     └────┬────┘        │                                  │
│   │    问诊聊天群组          │                                  │
│   └───────────┬──────────────┘                                  │
│               │                                                 │
│   ┌───────────┼───────────┐                                    │
│   ▼           ▼           ▼                                    │
│ 文字消息    图片消息    处方消息                                  │
│  ┌───┐     ┌───┐     ┌─────┐                                   │
│  │Txt│     │Img│     │ Rx  │                                   │
│  └───┘     └───┘     └─────┘                                   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

#### 3.2.2 群组管理策略

| 场景 | 群组类型 | 成员 | 说明 |
|------|----------|------|------|
| 图文问诊 | 私有群(AVChatRoom) | 患者+医生 | 一对一问诊，订单结束后解散 |
| 复诊咨询 | 私有群 | 患者+医生 | 历史群组可复用 |
| 系统通知 | 系统消息 | 单聊 | 系统向用户发送通知 |
| 客服咨询 | 私有群 | 用户+客服 | 平台客服支持 |

### 3.3 消息类型设计

#### 3.3.1 消息类型定义

```typescript
// 基础消息类型
enum MessageType {
  TEXT = 'TIMTextElem',           // 文本消息
  IMAGE = 'TIMImageElem',         // 图片消息
  FILE = 'TIMFileElem',           // 文件消息
  CUSTOM = 'TIMCustomElem',       // 自定义消息
}

// 业务消息子类型
enum CustomMessageType {
  PRESCRIPTION = 'PRESCRIPTION',   // 处方消息
  CONSULTATION_START = 'CONSULT_START',  // 问诊开始
  CONSULTATION_END = 'CONSULT_END',      // 问诊结束
  SYSTEM_NOTICE = 'SYSTEM_NOTICE',       // 系统通知
  MEDICAL_RECORD = 'MEDICAL_RECORD',     // 病历消息
}

// 自定义消息数据结构
interface CustomMessageData {
  type: CustomMessageType;
  version: string;
  data: any;
  timestamp: number;
}

// 处方消息数据
interface PrescriptionMessageData {
  prescriptionId: string;
  prescriptionNo: string;
  medicines: {
    name: string;
    dosage: string;
    frequency: string;
    quantity: number;
  }[];
  totalAmount: number;
  status: 'pending' | 'confirmed' | 'dispatched';
}
```

#### 3.3.2 消息格式示例

```json
// 文本消息
{
  "id": "msg_123456",
  "conversationID": "C2C_doctor_123",
  "from": "patient_456",
  "to": "doctor_123",
  "time": 1704067200,
  "type": "TIMTextElem",
  "payload": {
    "text": "医生您好，我最近头疼得厉害"
  }
}

// 处方自定义消息
{
  "id": "msg_789012",
  "conversationID": "C2C_doctor_123",
  "from": "doctor_123",
  "to": "patient_456",
  "time": 1704067500,
  "type": "TIMCustomElem",
  "payload": {
    "data": "{\"type\":\"PRESCRIPTION\",\"version\":\"1.0\",\"data\":{\"prescriptionId\":\"RX202401010001\",\"medicines\":[{\"name\":\"阿莫西林\",\"dosage\":\"0.5g\",\"quantity\":2}]},\"timestamp\":1704067500}",
    "description": "处方消息",
    "extension": "PRESCRIPTION"
  }
}
```

### 3.4 消息可靠性与存储

#### 3.4.1 消息可靠性保障

```
┌─────────────────────────────────────────────────────────────┐
│                    消息可靠性保障机制                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐              │
│  │ 消息发送 │────►│ IM服务器  │────►│ 消息存储  │              │
│  └──────────┘     └────┬─────┘     └──────────┘              │
│                        │                                     │
│        ┌───────────────┼───────────────┐                     │
│        ▼               ▼               ▼                     │
│   ┌──────────┐   ┌──────────┐   ┌──────────┐               │
│   │ 在线推送  │   │ 离线存储  │   │ 历史记录  │               │
│   └──────────┘   └──────────┘   └──────────┘               │
│                                                              │
│  保障机制:                                                   │
│  ✓ 消息确认机制 (ACK)                                       │
│  ✓ 消息去重处理                                              │
│  ✓ 离线消息同步                                              │
│  ✓ 历史消息拉取                                              │
│  ✓ 消息顺序保证                                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### 3.4.2 本地存储策略

```typescript
// 本地存储键值定义
const STORAGE_KEYS = {
  IM_USER_INFO: 'im_user_info',           // IM用户信息
  IM_CONVERSATIONS: 'im_conversations', // 会话列表
  IM_MESSAGES: 'im_messages_',           // 消息历史(前缀+conversationID)
  IM_UNREAD_COUNT: 'im_unread_count',    // 未读数
  IM_LAST_SYNC: 'im_last_sync',           // 最后同步时间
};

// IndexedDB 存储方案
interface IMMessageStore {
  dbName: string;
  version: number;
  stores: {
    messages: {
      keyPath: 'id';
      indexes: [
        { name: 'conversationID'; keyPath: 'conversationID' },
        { name: 'time'; keyPath: 'time' },
        { name: 'from'; keyPath: 'from' }
      ];
    };
    conversations: {
      keyPath: 'conversationID';
      indexes: [
        { name: 'lastMessageTime'; keyPath: 'lastMessage.time' }
      ];
    };
  };
}
```

---

## 4. 客户端SDK集成方案

### 4.1 SDK选型与版本

| 平台 | SDK类型 | 版本 | 说明 |
|------|---------|------|------|
| 患者端(App) | tim-wx-sdk | v3.x | 微信小程序SDK |
| 医生端(App) | tim-wx-sdk | v3.x | 微信小程序SDK |
| 管理后台(Web) | tim-js-sdk | v3.x | Web端SDK |

### 4.2 SDK初始化流程

```typescript
// IM SDK 初始化与登录管理
import TIM from 'tim-wx-sdk';
import COS from 'cos-wx-sdk-v5';

class IMSDKManager {
  private static instance: IMSDKManager;
  private tim: any = null;
  private isLogin: boolean = false;
  private currentUser: IMUserProfile | null = null;

  // SDK配置
  private readonly SDK_CONFIG = {
    SDKAppID: 1600043565,  // 腾讯云IM应用的SDKAppID
    level: 0,  // 日志级别
  };

  private constructor() {}

  public static getInstance(): IMSDKManager {
    if (!IMSDKManager.instance) {
      IMSDKManager.instance = new IMSDKManager();
    }
    return IMSDKManager.instance;
  }

  /**
   * 初始化SDK
   */
  public init(): void {
    if (this.tim) {
      console.warn('[IM] SDK already initialized');
      return;
    }

    // 创建SDK实例
    this.tim = TIM.create(this.SDK_CONFIG);

    // 注册COS插件（用于图片/文件上传）
    this.tim.registerPlugin({ 'cos-wx-sdk': COS });

    // 设置日志级别
    this.tim.setLogLevel(1);

    // 绑定事件监听
    this.bindEventListeners();

    console.log('[IM] SDK initialized successfully');
  }

  /**
   * 登录IM
   */
  public async login(userID: string, userSig: string): Promise<void> {
    if (!this.tim) {
      throw new Error('[IM] SDK not initialized');
    }

    if (this.isLogin) {
      console.warn('[IM] Already logged in');
      return;
    }

    try {
      const response = await this.tim.login({
        userID,
        userSig,
      });

      this.isLogin = true;
      this.currentUser = {
        userID,
        ...response.data,
      };

      console.log('[IM] Login successful:', userID);
    } catch (error) {
      console.error('[IM] Login failed:', error);
      throw error;
    }
  }

  /**
   * 登出IM
   */
  public async logout(): Promise<void> {
    if (!this.tim || !this.isLogin) {
      return;
    }

    try {
      await this.tim.logout();
      this.isLogin = false;
      this.currentUser = null;
      console.log('[IM] Logout successful');
    } catch (error) {
      console.error('[IM] Logout failed:', error);
      throw error;
    }
  }

  /**
   * 获取SDK实例
   */
  public getTIM(): any {
    return this.tim;
  }

  /**
   * 获取当前登录用户
   */
  public getCurrentUser(): IMUserProfile | null {
    return this.currentUser;
  }

  /**
   * 检查登录状态
   */
  public checkLogin(): boolean {
    return this.isLogin;
  }

  /**
   * 绑定事件监听
   */
  private bindEventListeners(): void {
    if (!this.tim) return;

    // SDK准备就绪
    this.tim.on(TIM.EVENT.SDK_READY, (event: any) => {
      console.log('[IM] SDK Ready');
      // 触发全局事件
      uni.$emit('IM_SDK_READY', event);
    });

    // SDK出错
    this.tim.on(TIM.EVENT.ERROR, (event: any) => {
      console.error('[IM] SDK Error:', event);
      uni.$emit('IM_SDK_ERROR', event);
    });

    // 收到新消息
    this.tim.on(TIM.EVENT.MESSAGE_RECEIVED, (event: any) => {
      console.log('[IM] Message received:', event.data);
      uni.$emit('IM_MESSAGE_RECEIVED', event.data);
    });

    // 会话列表更新
    this.tim.on(TIM.EVENT.CONVERSATION_LIST_UPDATED, (event: any) => {
      console.log('[IM] Conversation list updated:', event.data);
      uni.$emit('IM_CONVERSATION_UPDATED', event.data);
    });

    // 被踢下线
    this.tim.on(TIM.EVENT.KICKED_OUT, (event: any) => {
      console.warn('[IM] Kicked out:', event.data);
      uni.$emit('IM_KICKED_OUT', event.data);
      // 执行登出清理
      this.logout();
    });

    // 网络状态变化
    this.tim.on(TIM.EVENT.NET_STATE_CHANGE, (event: any) => {
      console.log('[IM] Network state changed:', event.data);
      uni.$emit('IM_NETWORK_CHANGE', event.data);
    });
  }
}

// 导出单例
export const imSDK = IMSDKManager.getInstance();
export default imSDK;
```

### 4.3 用户签名生成（后端实现）

```java
/**
 * 腾讯云IM UserSig生成服务
 */
@Service
@Slf4j
public class IMUserSigService {

    @Value("${tencent.im.sdkappid:1600043565}")
    private Long sdkAppId;
    
    @Value("${tencent.im.private-key}")
    private String privateKey;
    
    @Value("${tencent.im.expire-time:86400}")
    private Long expireTime; // 默认24小时

    /**
     * 生成UserSig
     * @param userId 用户ID
     * @return UserSig
     */
    public String generateUserSig(String userId) {
        try {
            // 构造UserSig参数
            long currentTime = System.currentTimeMillis() / 1000;
            long expireTime = currentTime + this.expireTime;
            
            // 构造JSON对象
            JSONObject sigDoc = new JSONObject();
            sigDoc.put("TLS.identifier", userId);
            sigDoc.put("TLS.appid", sdkAppId);
            sigDoc.put("TLS.expire", this.expireTime);
            sigDoc.put("TLS.expire_time", expireTime);
            sigDoc.put("TLS.create_time", currentTime);
            
            // 签名数据
            String sigData = sigDoc.toJSONString();
            
            // 使用ECDSA签名
            Signature signature = Signature.getInstance("SHA256withECDSA");
            PrivateKey key = loadPrivateKey(privateKey);
            signature.initSign(key);
            signature.update(sigData.getBytes(StandardCharsets.UTF_8));
            byte[] sigBytes = signature.sign();
            
            // Base64编码
            String sigBase64 = Base64.getEncoder().encodeToString(sigBytes);
            
            // 构造最终的UserSig
            String userSig = sigData + "." + sigBase64;
            userSig = Base64.getEncoder().encodeToString(userSig.getBytes(StandardCharsets.UTF_8));
            
            // URL安全的Base64
            userSig = userSig.replace("+", "*")
                           .replace("/", "-")
                           .replace("=", "_");
            
            log.info("Generated UserSig for user: {}", userId);
            return userSig;
            
        } catch (Exception e) {
            log.error("Failed to generate UserSig for user: {}", userId, e);
            throw new BusinessException("生成UserSig失败");
        }
    }
    
    /**
     * 加载私钥
     */
    private PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        // 移除PEM格式标记
        String key = privateKeyStr
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN EC PRIVATE KEY-----", "")
            .replace("-----END EC PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(spec);
    }
    
    /**
     * 生成临时UserSig（用于测试）
     */
    public String generateTempUserSig(String userId, Long expireSeconds) {
        long originalExpire = this.expireTime;
        this.expireTime = expireSeconds;
        try {
            return generateUserSig(userId);
        } finally {
            this.expireTime = originalExpire;
        }
    }
}
```

---

## 5. 客户端集成实现

### 5.1 IM模块封装

```typescript
// stores/modules/im.ts - Pinia状态管理
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { imSDK } from '@/utils/im-sdk';
import type { Message, Conversation } from '@/types/im';

export const useIMStore = defineStore('im', () => {
  // ============ State ============
  const isInitialized = ref(false);
  const isLoggedIn = ref(false);
  const currentUser = ref<any>(null);
  const conversationList = ref<Conversation[]>([]);
  const currentConversation = ref<Conversation | null>(null);
  const messageList = ref<Message[]>([]);
  const unreadTotalCount = ref(0);
  const isLoading = ref(false);
  const hasMoreMessages = ref(true);

  // ============ Getters ============
  const sortedConversationList = computed(() => {
    return [...conversationList.value].sort((a, b) => {
      return (b.lastMessage?.time || 0) - (a.lastMessage?.time || 0);
    });
  });

  const getUnreadCountByConversation = (conversationID: string) => {
    const conversation = conversationList.value.find(
      c => c.conversationID === conversationID
    );
    return conversation?.unreadCount || 0;
  };

  // ============ Actions ============

  /**
   * 初始化IM SDK
   */
  async function initialize() {
    if (isInitialized.value) return;

    try {
      imSDK.init();
      
      // 监听全局事件
      setupEventListeners();
      
      isInitialized.value = true;
      console.log('[IM Store] SDK initialized');
    } catch (error) {
      console.error('[IM Store] SDK initialization failed:', error);
      throw error;
    }
  }

  /**
   * 登录IM
   */
  async function login(userID: string, userSig: string) {
    if (!isInitialized.value) {
      await initialize();
    }

    try {
      await imSDK.login(userID, userSig);
      isLoggedIn.value = true;
      currentUser.value = imSDK.getCurrentUser();
      
      // 获取会话列表
      await getConversationList();
      
      console.log('[IM Store] Login successful:', userID);
    } catch (error) {
      console.error('[IM Store] Login failed:', error);
      throw error;
    }
  }

  /**
   * 登出IM
   */
  async function logout() {
    try {
      await imSDK.logout();
      
      // 清理状态
      isLoggedIn.value = false;
      currentUser.value = null;
      conversationList.value = [];
      currentConversation.value = null;
      messageList.value = [];
      unreadTotalCount.value = 0;
      
      console.log('[IM Store] Logout successful');
    } catch (error) {
      console.error('[IM Store] Logout failed:', error);
      throw error;
    }
  }

  /**
   * 获取会话列表
   */
  async function getConversationList() {
    if (!imSDK.checkLogin()) return;

    try {
      const tim = imSDK.getTIM();
      const response = await tim.getConversationList();
      
      conversationList.value = response.data.conversationList.map(
        (conv: any) => formatConversation(conv)
      );
      
      // 计算总未读数
      calculateTotalUnread();
      
      return conversationList.value;
    } catch (error) {
      console.error('[IM Store] Get conversation list failed:', error);
      throw error;
    }
  }

  /**
   * 进入会话
   */
  async function enterConversation(conversationID: string) {
    try {
      // 查找会话
      const conversation = conversationList.value.find(
        c => c.conversationID === conversationID
      );
      
      if (!conversation) {
        // 如果本地没有，尝试从SDK获取
        const tim = imSDK.getTIM();
        const response = await tim.getConversationProfile(conversationID);
        currentConversation.value = formatConversation(response.data.conversation);
      } else {
        currentConversation.value = conversation;
      }

      // 加载消息历史
      await loadMessageHistory();
      
      // 清空该会话未读数
      await resetUnreadCount(conversationID);
      
      return currentConversation.value;
    } catch (error) {
      console.error('[IM Store] Enter conversation failed:', error);
      throw error;
    }
  }

  /**
   * 加载消息历史
   */
  async function loadMessageHistory(nextReqMessageID?: string) {
    if (!currentConversation.value) return;

    isLoading.value = true;
    
    try {
      const tim = imSDK.getTIM();
      const options: any = {
        conversationID: currentConversation.value.conversationID,
        count: 15, // 每次拉取15条
      };
      
      if (nextReqMessageID) {
        options.nextReqMessageID = nextReqMessageID;
      }
      
      const response = await tim.getMessageList(options);
      
      const messages = response.data.messageList.map((msg: any) => 
        formatMessage(msg)
      );
      
      if (nextReqMessageID) {
        // 加载更多，添加到末尾
        messageList.value = [...messageList.value, ...messages];
      } else {
        // 首次加载
        messageList.value = messages;
      }
      
      hasMoreMessages.value = response.data.isCompleted === false;
      
      return messages;
    } catch (error) {
      console.error('[IM Store] Load message history failed:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * 发送文本消息
   */
  async function sendTextMessage(text: string): Promise<Message> {
    if (!currentConversation.value) {
      throw new Error('No active conversation');
    }

    try {
      const tim = imSDK.getTIM();
      const message = tim.createTextMessage({
        to: getConversationTargetID(currentConversation.value),
        conversationType: currentConversation.value.type,
        payload: { text },
      });

      const response = await tim.sendMessage(message);
      const sentMessage = formatMessage(response.data.message);
      
      // 添加到本地消息列表
      messageList.value.unshift(sentMessage);
      
      return sentMessage;
    } catch (error) {
      console.error('[IM Store] Send text message failed:', error);
      throw error;
    }
  }

  /**
   * 发送图片消息
   */
  async function sendImageMessage(file: File): Promise<Message> {
    if (!currentConversation.value) {
      throw new Error('No active conversation');
    }

    try {
      const tim = imSDK.getTIM();
      const message = tim.createImageMessage({
        to: getConversationTargetID(currentConversation.value),
        conversationType: currentConversation.value.type,
        payload: { file },
        onProgress: (percent: number) => {
          console.log(`[IM] Image upload progress: ${percent}%`);
        },
      });

      const response = await tim.sendMessage(message);
      const sentMessage = formatMessage(response.data.message);
      
      messageList.value.unshift(sentMessage);
      
      return sentMessage;
    } catch (error) {
      console.error('[IM Store] Send image message failed:', error);
      throw error;
    }
  }

  /**
   * 发送自定义消息（处方、问诊开始/结束等）
   */
  async function sendCustomMessage(
    customType: CustomMessageType,
    data: any
  ): Promise<Message> {
    if (!currentConversation.value) {
      throw new Error('No active conversation');
    }

    try {
      const tim = imSDK.getTIM();
      
      const customData: CustomMessageData = {
        type: customType,
        version: '1.0',
        data,
        timestamp: Date.now(),
      };

      const message = tim.createCustomMessage({
        to: getConversationTargetID(currentConversation.value),
        conversationType: currentConversation.value.type,
        payload: {
          data: JSON.stringify(customData),
          description: this.getCustomMessageDescription(customType),
          extension: customType,
        },
      });

      const response = await tim.sendMessage(message);
      const sentMessage = formatMessage(response.data.message);
      
      messageList.value.unshift(sentMessage);
      
      return sentMessage;
    } catch (error) {
      console.error('[IM Store] Send custom message failed:', error);
      throw error;
    }
  }

  /**
   * 重置会话未读数
   */
  async function resetUnreadCount(conversationID: string): Promise<void> {
    try {
      const tim = imSDK.getTIM();
      await tim.setMessageRead({ conversationID });
      
      // 更新本地未读数
      const conversation = conversationList.value.find(
        c => c.conversationID === conversationID
      );
      if (conversation) {
        conversation.unreadCount = 0;
      }
      
      calculateTotalUnread();
    } catch (error) {
      console.error('[IM Store] Reset unread count failed:', error);
    }
  }

  // ============ Helper Methods ============

  private setupEventListeners(): void {
    // 监听SDK事件，更新状态
    uni.$on('IM_SDK_READY', () => {
      console.log('[IM Store] SDK Ready');
    });

    uni.$on('IM_MESSAGE_RECEIVED', (messages: Message[]) => {
      // 处理新消息
      this.handleNewMessages(messages);
    });

    uni.$on('IM_CONVERSATION_UPDATED', (conversations: Conversation[]) => {
      // 更新会话列表
      conversationList.value = conversations.map(c => formatConversation(c));
      calculateTotalUnread();
    });

    uni.$on('IM_KICKED_OUT', (data: any) => {
      // 被踢下线处理
      console.warn('[IM Store] Kicked out:', data);
      logout();
      uni.showModal({
        title: '提示',
        content: '您的账号已在其他设备登录',
        showCancel: false,
        success: () => {
          uni.navigateTo({ url: '/pages/login/index' });
        },
      });
    });
  }

  private handleNewMessages(messages: Message[]): void {
    messages.forEach(message => {
      const formattedMsg = formatMessage(message);
      
      // 如果是当前会话的消息，添加到列表
      if (
        currentConversation.value &&
        message.conversationID === currentConversation.value.conversationID
      ) {
        messageList.value.unshift(formattedMsg);
      }
      
      // 触发新消息事件
      uni.$emit('IM_NEW_MESSAGE', formattedMsg);
    });
    
    // 更新总未读数
    calculateTotalUnread();
  }

  private calculateTotalUnread(): void {
    const total = conversationList.value.reduce((sum, conv) => sum + (conv.unreadCount || 0), 0);
    unreadTotalCount.value = total;
  }

  private getConversationTargetID(conversation: Conversation): string {
    // 从conversationID中提取目标用户ID
    // C2C_patient_12345 -> patient_12345
    return conversation.conversationID.replace(/^C2C_/, '');
  }

  private getCustomMessageDescription(type: CustomMessageType): string {
    const descriptions: Record<CustomMessageType, string> = {
      [CustomMessageType.PRESCRIPTION]: '处方消息',
      [CustomMessageType.CONSULTATION_START]: '问诊开始',
      [CustomMessageType.CONSULTATION_END]: '问诊结束',
      [CustomMessageType.SYSTEM_NOTICE]: '系统通知',
      [CustomMessageType.MEDICAL_RECORD]: '病历消息',
    };
    return descriptions[type] || '自定义消息';
  }

  return {
    // State
    isInitialized,
    isLoggedIn,
    currentUser,
    conversationList,
    sortedConversationList,
    currentConversation,
    messageList,
    unreadTotalCount,
    isLoading,
    hasMoreMessages,
    // Actions
    initialize,
    login,
    logout,
    getConversationList,
    enterConversation,
    loadMessageHistory,
    sendTextMessage,
    sendImageMessage,
    sendCustomMessage,
    resetUnreadCount,
    getUnreadCountByConversation,
  };
});

// ============ Helper Functions ============

function formatConversation(conv: any): Conversation {
  return {
    conversationID: conv.conversationID,
    type: conv.type,
    name: conv.name || '',
    avatar: conv.avatar || '',
    unreadCount: conv.unreadCount || 0,
    lastMessage: conv.lastMessage ? formatMessage(conv.lastMessage) : null,
    lastMessageTime: conv.lastMessage?.time || 0,
  };
}

function formatMessage(msg: any): Message {
  return {
    id: msg.ID,
    conversationID: msg.conversationID,
    from: msg.from,
    to: msg.to,
    time: msg.time,
    type: msg.type,
    payload: msg.payload,
    isSelf: msg.flow === 'out',
    status: msg.status,
  };
}

function getConversationTargetID(conversation: Conversation): string {
  return conversation.conversationID.replace(/^C2C_/, '');
}
```

### 5.2 聊天组件实现

```vue
<!-- components/im/ChatContainer.vue -->
<template>
  <div class="chat-container">
    <!-- 聊天头部 -->
    <ChatHeader
      :conversation="currentConversation"
      :user-info="chatUserInfo"
      @back="handleBack"
      @view-detail="handleViewDetail"
    />

    <!-- 消息列表 -->
    <MessageList
      ref="messageListRef"
      :messages="messageList"
      :loading="isLoading"
      :has-more="hasMoreMessages"
      :current-user="currentUser"
      @load-more="handleLoadMore"
      @image-preview="handleImagePreview"
      @prescription-click="handlePrescriptionClick"
    />

    <!-- 输入区域 -->
    <ChatInput
      :disabled="inputDisabled"
      :placeholder="inputPlaceholder"
      @send-text="handleSendText"
      @send-image="handleSendImage"
      @send-prescription="handleSendPrescription"
      @input-focus="handleInputFocus"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useIMStore } from '@/stores/modules/im';
import { useUserStore } from '@/stores/modules/user';
import ChatHeader from './ChatHeader.vue';
import MessageList from './MessageList.vue';
import ChatInput from './ChatInput.vue';

const props = defineProps<{
  conversationId: string;
  userType: 'patient' | 'doctor';
}>();

const emit = defineEmits<{
  back: [];
  viewDetail: [conversationId: string];
}>();

// Store
const imStore = useIMStore();
const userStore = useUserStore();

// Refs
const messageListRef = ref<InstanceType<typeof MessageList>>();

// Computed
const currentUser = computed(() => userStore.userInfo);
const currentConversation = computed(() => imStore.currentConversation);
const messageList = computed(() => imStore.messageList);
const isLoading = computed(() => imStore.isLoading);
const hasMoreMessages = computed(() => imStore.hasMoreMessages);
const isLoggedIn = computed(() => imStore.isLoggedIn);

const chatUserInfo = computed(() => {
  if (!currentConversation.value) return null;
  // 根据会话信息获取对方用户信息
  return {
    name: currentConversation.value.name,
    avatar: currentConversation.value.avatar,
  };
});

const inputDisabled = computed(() => {
  // 根据问诊状态判断是否可输入
  return false;
});

const inputPlaceholder = computed(() => {
  return '请输入消息...';
});

// Methods
async function initChat() {
  try {
    // 检查IM登录状态
    if (!isLoggedIn.value) {
      // 获取UserSig并登录
      await loginIM();
    }

    // 进入会话
    await imStore.enterConversation(props.conversationId);
  } catch (error) {
    console.error('[Chat] Init chat failed:', error);
    uni.showToast({
      title: '加载聊天失败',
      icon: 'error',
    });
  }
}

async function loginIM() {
  // 从后端获取UserSig
  const { data } = await uni.request({
    url: '/api/im/usersig',
    method: 'GET',
  });

  const { userID, userSig } = data;
  await imStore.login(userID, userSig);
}

async function handleSendText(text: string) {
  try {
    await imStore.sendTextMessage(text);
    scrollToBottom();
  } catch (error) {
    console.error('[Chat] Send text failed:', error);
    uni.showToast({ title: '发送失败', icon: 'error' });
  }
}

async function handleSendImage(file: File) {
  try {
    await imStore.sendImageMessage(file);
    scrollToBottom();
  } catch (error) {
    console.error('[Chat] Send image failed:', error);
    uni.showToast({ title: '发送失败', icon: 'error' });
  }
}

function handleSendPrescription() {
  // 跳转到开处方页面
  uni.navigateTo({
    url: `/pages/prescription/create?conversationId=${props.conversationId}`,
  });
}

function handleLoadMore() {
  imStore.loadMessageHistory();
}

function scrollToBottom() {
  nextTick(() => {
    messageListRef.value?.scrollToBottom();
  });
}

function handleImagePreview(url: string) {
  uni.previewImage({
    urls: [url],
    current: url,
  });
}

function handlePrescriptionClick(prescriptionId: string) {
  uni.navigateTo({
    url: `/pages/prescription/detail?id=${prescriptionId}`,
  });
}

function handleBack() {
  emit('back');
}

function handleViewDetail() {
  if (currentConversation.value) {
    emit('viewDetail', currentConversation.value.conversationID);
  }
}

function handleInputFocus() {
  scrollToBottom();
}

// Lifecycle
onMounted(() => {
  initChat();
});

onUnmounted(() => {
  // 清理工作
});
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}
</style>
```

## 6. 后端API设计

### 6.1 IM相关接口

```java
/**
 * IM服务控制器
 */
@RestController
@RequestMapping("/api/im")
@Tag(name = "IM服务", description = "即时通讯相关接口")
public class IMController {

    @Autowired
    private IMUserSigService userSigService;
    
    @Autowired
    private IMGroupService groupService;
    
    @Autowired
    private ConsultationService consultationService;

    /**
     * 获取UserSig
     */
    @GetMapping("/usersig")
    @Operation(summary = "获取IM登录凭证")
    public Result<UserSigVO> getUserSig(@AuthenticationPrincipal UserDetails user) {
        String userID = generateIMUserID(user.getUserType(), user.getUserId());
        String userSig = userSigService.generateUserSig(userID);
        
        UserSigVO vo = new UserSigVO();
        vo.setUserID(userID);
        vo.setUserSig(userSig);
        vo.setExpireTime(86400); // 24小时
        
        return Result.success(vo);
    }

    /**
     * 创建问诊群组
     */
    @PostMapping("/groups/consultation")
    @Operation(summary = "创建问诊群组")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public Result<GroupInfoVO> createConsultationGroup(
            @RequestBody @Valid CreateGroupDTO dto) {
        // 创建问诊订单
        ConsultationOrder order = consultationService.createOrder(dto);
        
        // 创建IM群组
        String groupID = groupService.createConsultationGroup(
            order.getPatientId(),
            order.getDoctorId(),
            order.getId()
        );
        
        GroupInfoVO vo = new GroupInfoVO();
        vo.setGroupID(groupID);
        vo.setGroupName("问诊-" + order.getId());
        vo.setConsultationId(order.getId());
        
        return Result.success(vo);
    }

    /**
     * 解散群组
     */
    @DeleteMapping("/groups/{groupId}")
    @Operation(summary = "解散群组")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public Result<Void> dismissGroup(@PathVariable String groupId) {
        groupService.dismissGroup(groupId);
        return Result.success();
    }

    /**
     * 生成IM用户ID
     */
    private String generateIMUserID(String userType, Long userId) {
        return userType.toLowerCase() + "_" + userId;
    }
}
```

### 6.2 回调处理服务

```java
/**
 * IM回调处理器
 * 处理腾讯IM服务器的回调通知
 */
@Service
@Slf4j
public class IMCallbackHandler {

    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private MessagePersistenceService messageService;
    
    @Autowired
    private NotificationService notificationService;

    /**
     * 处理单聊消息回调
     */
    public void handleC2CMessageCallback(IMCallbackRequest request) {
        log.info("[IM Callback] C2C message: {}", request.getMsgId());
        
        // 解析消息
        IMMessage message = parseMessage(request);
        
        // 保存消息到数据库
        messageService.saveMessage(message);
        
        // 更新问诊最后消息时间
        consultationService.updateLastMessageTime(
            message.getConsultationId(),
            message.getTimestamp()
        );
        
        // 发送推送通知（如果接收者不在线）
        if (!request.getOnline()) {
            notificationService.sendPushNotification(
                message.getToUser(),
                message.getSummary()
            );
        }
    }

    /**
     * 处理群组消息回调
     */
    public void handleGroupMessageCallback(IMCallbackRequest request) {
        log.info("[IM Callback] Group message: {}", request.getMsgId());
        
        IMMessage message = parseMessage(request);
        message.setGroupId(request.getGroupId());
        
        // 保存消息
        messageService.saveMessage(message);
        
        // 通知群成员
        notifyGroupMembers(request.getGroupId(), message);
    }

    /**
     * 处理状态变更回调
     */
    public void handleStateChangeCallback(IMCallbackRequest request) {
        String callbackCommand = request.getCallbackCommand();
        
        switch (callbackCommand) {
            case "State.StateChange":
                handleUserStateChange(request);
                break;
            case "Group.CallbackAfterNewMemberJoin":
                handleMemberJoin(request);
                break;
            case "Group.CallbackAfterMemberExit":
                handleMemberExit(request);
                break;
            default:
                log.warn("[IM Callback] Unknown command: {}", callbackCommand);
        }
    }

    /**
     * 解析消息
     */
    private IMMessage parseMessage(IMCallbackRequest request) {
        IMMessage message = new IMMessage();
        message.setMsgId(request.getMsgId());
        message.setFromUser(request.getFromAccount());
        message.setToUser(request.getToAccount());
        message.setMsgType(request.getMsgType());
        message.setContent(request.getMsgBody());
        message.setTimestamp(new Date(request.getMsgTime()));
        
        // 从自定义数据中提取问诊ID
        if (request.getCloudCustomData() != null) {
            message.setConsultationId(request.getCloudCustomData().getConsultationId());
        }
        
        return message;
    }

    /**
     * 处理用户状态变更
     */
    private void handleUserStateChange(IMCallbackRequest request) {
        String userId = request.getFromAccount();
        String state = request.getState();
        
        log.info("[IM State] User {} state changed to {}", userId, state);
        
        // 更新用户在线状态到Redis
        if ("Online".equals(state)) {
            consultationService.setUserOnline(userId, true);
        } else {
            consultationService.setUserOnline(userId, false);
        }
    }

    /**
     * 处理成员加入群组
     */
    private void handleMemberJoin(IMCallbackRequest request) {
        String groupId = request.getGroupId();
        String userId = request.getOperatorAccount();
        
        log.info("[IM Group] User {} joined group {}", userId, groupId);
        
        // 发送系统消息通知
        consultationService.sendSystemMessage(
            groupId,
            userId + " 加入了问诊"
        );
    }

    /**
     * 处理成员退出群组
     */
    private void handleMemberExit(IMCallbackRequest request) {
        String groupId = request.getGroupId();
        String userId = request.getOperatorAccount();
        
        log.info("[IM Group] User {} exited group {}", userId, groupId);
        
        // 如果是医生退出，结束问诊
        if (userId.startsWith("doctor_")) {
            consultationService.endConsultationByGroupId(groupId, "医生退出");
        }
    }

    /**
     * 通知群成员
     */
    private void notifyGroupMembers(String groupId, IMMessage message) {
        // 获取群成员列表
        List<String> members = consultationService.getGroupMembers(groupId);
        
        // 排除发送者
        members.remove(message.getFromUser());
        
        // 发送推送通知
        for (String member : members) {
            if (!consultationService.isUserOnline(member)) {
                notificationService.sendPushNotification(
                    member,
                    "您有一条新消息: " + message.getSummary()
                );
            }
        }
    }
}
```

## 7. 安全与性能优化

### 7.1 安全措施

| 措施 | 说明 | 实现方式 |
|------|------|----------|
| UserSig加密 | 防止身份伪造 | 后端生成，ECDSA签名 |
| 回调验证 | 确保回调来自腾讯云 | 校验请求IP和签名 |
| 消息加密 | 保护敏感内容 | HTTPS + 端到端加密 |
| 敏感词过滤 | 防止违规内容 | 内容审核API |
| 频率限制 | 防止滥用 | 接口限流 |

### 7.2 性能优化策略

```
┌─────────────────────────────────────────────────────────────┐
│                    性能优化策略                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. 消息加载优化                                              │
│     ├─ 分页加载（每次15-20条）                                │
│     ├─ 虚拟滚动（长列表优化）                                  │
│     └─ 消息预加载（下滑时提前加载）                            │
│                                                              │
│  2. 图片优化                                                  │
│     ├─ 缩略图压缩                                            │
│     ├─ 懒加载                                                │
│     └─ 渐进式加载                                            │
│                                                              │
│  3. 网络优化                                                  │
│     ├─ 连接池复用                                            │
│     ├─ 心跳保活（30秒）                                       │
│     ├─ 断线重连（指数退避）                                    │
│     └─ 弱网优化（消息队列缓存）                                 │
│                                                              │
│  4. 存储优化                                                  │
│     ├─ IndexedDB本地缓存                                     │
│     ├─ 消息本地存储                                          │
│     ├─ 会话列表缓存                                          │
│     └─ 智能清理（保留最近30天）                               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## 8. 部署与运维

### 8.1 环境配置

```yaml
# application-im.yml
im:
  tencent:
    sdk-app-id: 1600043565
    secret-key: ${IM_SECRET_KEY}
    private-key: ${IM_PRIVATE_KEY}
    admin-identifier: administrator
    # 回调配置
    callback:
      enabled: true
      token: ${IM_CALLBACK_TOKEN}
      # 需要配置的回调命令
      commands:
        - C2C.CallbackAfterSendMsg
        - Group.CallbackAfterSendMsg
        - State.StateChange
        - Group.CallbackAfterNewMemberJoin
        - Group.CallbackAfterMemberExit

# 用户签名配置
usersig:
  expire-time: 86400  # 24小时
  
# 消息存储配置
message:
  storage:
    enabled: true
    retention-days: 90
  # 敏感词过滤
  content-moderation:
    enabled: true
```

### 8.2 监控告警

| 监控项 | 告警阈值 | 通知方式 |
|--------|----------|----------|
| IM连接数 | > 10000 | 邮件+短信 |
| 消息延迟 | > 3秒 | 邮件+短信 |
| 回调失败率 | > 5% | 邮件+短信 |
| 登录失败率 | > 10% | 邮件 |
| 存储使用率 | > 80% | 邮件 |

### 8.3 运维检查清单

- [ ] 每日检查IM服务状态
- [ ] 每周分析消息量统计
- [ ] 每月审查存储使用情况
- [ ] 定期更新UserSig密钥
- [ ] 定期测试灾备恢复流程

## 9. 风险评估与应对

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| 腾讯云IM服务故障 | 高 | 低 | 降级为留言模式，保存离线消息 |
| 消息丢失 | 高 | 低 | 多重确认机制，本地+远程存储 |
| 敏感信息泄露 | 高 | 中 | 端到端加密，敏感词过滤 |
| 性能瓶颈 | 中 | 中 | 水平扩展，缓存优化 |
| 合规问题 | 高 | 中 | 医疗数据合规审查，数据本地化 |

## 10. 总结

本技术方案详细设计了DrugMall平台基于腾讯云IM的在线问诊系统，涵盖：

1. **架构设计**: 完整的系统架构，包括客户端、IM服务、后端服务的交互
2. **SDK集成**: 完整的SDK初始化、登录、消息收发实现
3. **用户体系**: UserSig生成、用户资料同步、ID映射策略
4. **消息系统**: 多种消息类型、自定义消息、历史记录管理
5. **回调处理**: 完整的回调处理机制，包括消息、状态、群组事件
6. **安全与性能**: 安全措施和性能优化策略
7. **部署运维**: 环境配置、监控告警、运维清单

**下一步行动建议**:
1. 注册腾讯云IM服务，创建应用获取SDKAppID
2. 配置开发环境，集成SDK进行POC验证
3. 开发后端UserSig服务和回调接口
4. 实现基础的聊天功能并进行联调测试
5. 逐步完善消息类型和业务功能
    // 更新未读数
    calculateTotalUnread();
  }

  private calculateTotalUnread(): void {
    unreadTotalCount.value = conversationList.value.reduce(
      (total, conv) => total + (conv.unreadCount || 0),
      0
    );
  }

  private getConversationTargetID(conversation: Conversation): string {
    // 从会话ID中提取目标用户ID
    // C2C_userId 格式
    if (conversation.type === 'C2C') {
      return conversation.conversationID.replace('C2C_', '');
    }
    return '';
  }

  private getCustomMessageDescription(type: CustomMessageType): string {
    const descriptions: Record<CustomMessageType, string> = {
      [CustomMessageType.PRESCRIPTION]: '处方消息',
      [CustomMessageType.CONSULTATION_START]: '问诊开始',
      [CustomMessageType.CONSULTATION_END]: '问诊结束',
      [CustomMessageType.SYSTEM_NOTICE]: '系统通知',
      [CustomMessageType.MEDICAL_RECORD]: '病历消息',
    };
    return descriptions[type] || '自定义消息';
  }

  // ============ 格式化方法 ============

  function formatConversation(rawConv: any): Conversation {
    return {
      conversationID: rawConv.conversationID,
      type: rawConv.type,
      userProfile: rawConv.userProfile,
      groupProfile: rawConv.groupProfile,
      lastMessage: rawConv.lastMessage,
      unreadCount: rawConv.unreadCount || 0,
      lastMessageTime: rawConv.lastMessage?.time || 0,
    };
  }

  function formatMessage(rawMsg: any): Message {
    return {
      id: rawMsg.ID,
      conversationID: rawMsg.conversationID,
      from: rawMsg.from,
      to: rawMsg.to,
      time: rawMsg.time,
      type: rawMsg.type,
      payload: rawMsg.payload,
      isSelf: rawMsg.flow === 'out',
      status: rawMsg.status,
    };
  }

  return {
    // State
    isInitialized,
    isLoggedIn,
    currentUser,
    conversationList,
    currentConversation,
    messageList,
    unreadTotalCount,
    isLoading,
    hasMoreMessages,
    
    // Getters
    sortedConversationList,
    getUnreadCountByConversation,
    
    // Actions
    initialize,
    login,
    logout,
    getConversationList,
    enterConversation,
    loadMessageHistory,
    sendTextMessage,
    sendImageMessage,
    sendCustomMessage,
    resetUnreadCount,
  };
});
```

---

## 6. 后端服务设计

### 6.1 问诊业务流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      在线问诊业务流程                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐                                                    │
│  │ 患者下单  │                                                   │
│  └────┬─────┘                                                    │
│       │ 1.创建问诊订单                                            │
│       ▼                                                          │
│  ┌──────────────────┐                                            │
│  │ 系统分配医生      │                                            │
│  │ (或患者选择医生)  │                                            │
│  └────┬─────────────┘                                            │
│       │ 2.创建IM群组                                              │
│       ▼                                                          │
│  ┌──────────────────┐                                            │
│  │ IM群组初始化      │                                            │
│  │ - 创建C2C会话      │                                            │
│  │ - 同步用户资料     │                                            │
│  └────┬─────────────┘                                            │
│       │ 3.发送系统消息                                             │
│       ▼                                                          │
│  ┌──────────────────┐                                            │
│  │ 患者发送首条消息    │◄─────────────────────────────┐           │
│  └────┬─────────────┘                              │           │
│       │ 4.消息投递                                  │           │
│       ▼                                            │           │
│  ┌──────────────────┐                              │           │
│  │ 医生收到消息提醒  │                              │           │
│  │ (推送+应用内)    │                              │           │
│  └────┬─────────────┘                              │           │
│       │ 5.医生回复                                  │           │
│       ▼                                            │           │
│  ┌──────────────────┐                              │           │
│  │ 医生发送回复/处方  │──────────────────────────────┘           │
│  └────┬─────────────┘                                            │
│       │                                                            │
│       │ 6.问诊结束条件                                              │
│       ▼                                                            │
│  ┌──────────────────────────────────────────┐                     │
│  │ • 患者主动结束                           │                     │
│  │ • 医生结束问诊                           │                     │
│  │ • 超时自动结束(24小时无消息)              │                     │
│  │ • 处方开具完成                           │                     │
│  └────┬─────────────────────────────────────┘                     │
│       │ 7.结束处理                                                    │
│       ▼                                                            │
│  ┌──────────────────┐                                            │
│  │ • 发送结束通知    │                                            │
│  │ • 保存问诊记录    │                                            │
│  │ • 关闭IM群组      │                                            │
│  │ • 生成病历摘要    │                                            │
│  └──────────────────┘                                            │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### 6.2 状态流转图

```
┌─────────────┐     创建订单      ┌─────────────┐
│   待开始    │ ────────────────► │   进行中    │
└─────────────┘                   └──────┬──────┘
      ▲                                  │
      │                                  │
      │         ┌─────────────┐          │
      └──────── │   已结束    │ ◄────────┘
                └─────────────┘
                      ▲
                      │
                ┌─────┴─────┐
                │   已取消   │
                └───────────┘
```

### 6.3 核心业务接口

```java
/**
 * 问诊服务接口
 */
public interface ConsultationService {

    /**
     * 创建问诊订单
     */
    ConsultationOrder createOrder(CreateConsultationDTO dto);

    /**
     * 分配医生
     */
    void assignDoctor(Long orderId, Long doctorId);

    /**
     * 患者发送消息
     */
    void sendPatientMessage(Long orderId, MessageDTO message);

    /**
     * 医生回复消息
     */
    void sendDoctorMessage(Long orderId, MessageDTO message);

    /**
     * 开具处方
     */
    Prescription createPrescription(Long orderId, PrescriptionDTO dto);

    /**
     * 结束问诊
     */
    void endConsultation(Long orderId, EndReason reason);

    /**
     * 超时自动结束
     */
    void autoTimeoutEnd(Long orderId);

    /**
     * 获取问诊详情
     */
    ConsultationDetail getConsultationDetail(Long orderId);

    /**
     * 获取问诊列表
     */
    PageResult<ConsultationOrder> getConsultationList(QueryConsultationDTO dto);

    /**
     * 获取消息历史
     */
    List<Message> getMessageHistory(Long orderId, Long lastMessageId, int limit);
}
```

### 6.4 数据库设计

```sql
-- 问诊订单表
CREATE TABLE dm_consultation_order (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no            VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    patient_id          BIGINT NOT NULL COMMENT '患者ID',
    doctor_id           BIGINT COMMENT '医生ID',
    patient_name        VARCHAR(50) COMMENT '患者姓名',
    doctor_name         VARCHAR(50) COMMENT '医生姓名',
    type                TINYINT NOT NULL DEFAULT 1 COMMENT '问诊类型:1-图文 2-视频 3-复诊',
    status              TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待开始 1-进行中 2-已结束 3-已取消',
    symptom             TEXT COMMENT '症状描述',
    images              JSON COMMENT '症状图片',
    price               DECIMAL(10,2) NOT NULL COMMENT '问诊价格',
    group_id            VARCHAR(100) COMMENT 'IM群组ID',
    start_time          DATETIME COMMENT '开始时间',
    end_time            DATETIME COMMENT '结束时间',
    end_reason          TINYINT COMMENT '结束原因:1-正常结束 2-超时结束 3-患者取消 4-医生取消',
    prescription_id     BIGINT COMMENT '关联处方ID',
    is_deleted          TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_status (status),
    INDEX idx_group (group_id)
) ENGINE=InnoDB COMMENT='问诊订单表';

-- 问诊消息表
CREATE TABLE dm_consultation_message (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    msg_id              VARCHAR(100) NOT NULL UNIQUE COMMENT 'IM消息ID',
    order_id            BIGINT NOT NULL COMMENT '问诊订单ID',
    group_id            VARCHAR(100) COMMENT '群组ID',
    from_user           VARCHAR(100) NOT NULL COMMENT '发送者ID',
    to_user             VARCHAR(100) COMMENT '接收者ID',
    msg_type            TINYINT NOT NULL COMMENT '消息类型:1-文本 2-图片 3-语音 4-自定义',
    sub_type            VARCHAR(50) COMMENT '子类型:PRESCRIPTION/SYSTEM/etc',
    content             TEXT COMMENT '消息内容',
    extra_data          JSON COMMENT '扩展数据',
    is_read             TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读',
    read_time           DATETIME COMMENT '已读时间',
    status              TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-发送中 1-已发送 2-发送失败',
    send_time           DATETIME NOT NULL COMMENT '发送时间',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order (order_id),
    INDEX idx_msg (msg_id),
    INDEX idx_from (from_user),
    INDEX idx_time (send_time)
) ENGINE=InnoDB COMMENT='问诊消息表';

-- 问诊处方表
CREATE TABLE dm_consultation_prescription (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    prescription_no     VARCHAR(50) NOT NULL UNIQUE COMMENT '处方编号',
    order_id            BIGINT NOT NULL COMMENT '问诊订单ID',
    patient_id          BIGINT NOT NULL COMMENT '患者ID',
    doctor_id           BIGINT NOT NULL COMMENT '医生ID',
    diagnosis           VARCHAR(500) COMMENT '诊断结果',
    advice              TEXT COMMENT '医嘱建议',
    medicines           JSON NOT NULL COMMENT '药品清单',
    total_amount        DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status              TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待确认 1-已确认 2-已购药 3-已过期',
    valid_time          DATETIME COMMENT '处方有效期',
    sign_status         TINYINT DEFAULT 0 COMMENT '签名状态:0-未签名 1-已签名',
    is_deleted          TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='问诊处方表';
```

---

## 10. 总结与建议

### 10.1 核心功能实现清单

| 功能模块 | 实现状态 | 说明 |
|---------|---------|------|
| IM SDK集成 | ✅ | SDK初始化、登录、事件监听 |
| 用户认证体系 | ✅ | UserSig生成、用户映射 |
| 消息收发 | ✅ | 文本、图片、自定义消息 |
| 会话管理 | ✅ | 会话列表、未读数、历史记录 |
| 问诊业务流程 | ✅ | 创建、进行、结束全流程 |
| 处方功能 | ✅ | 开处方、展示、流转 |
| 回调处理 | ✅ | 消息、状态、群组事件 |
| 离线推送 | ✅ | 消息推送、未读提醒 |

### 10.2 后续优化建议

1. **性能优化**
   - 实现消息本地存储（IndexedDB）
   - 添加消息预加载机制
   - 优化长列表渲染性能

2. **功能增强**
   - 支持语音消息
   - 添加视频通话功能
   - 实现消息撤回功能

3. **安全加固**
   - 实现端到端加密
   - 添加敏感内容自动过滤
   - 完善消息审计日志

4. **监控完善**
   - 接入APM性能监控
   - 实现实时业务大盘
   - 添加智能告警规则

### 10.3 技术实施路线图

```
Phase 1 (第1-2周): 基础框架
├── SDK集成与登录
├── 基础消息收发
└── 简单UI界面

Phase 2 (第3-4周): 业务功能
├── 问诊流程集成
├── 处方功能开发
└── 消息类型完善

Phase 3 (第5-6周): 优化上线
├── 性能优化
├── 安全加固
└── 生产部署
```

---

**文档完成日期**: 2024年

**文档版本**: v1.0

**编写者**: AI Technical Assistant