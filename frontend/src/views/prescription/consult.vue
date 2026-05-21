<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Phone, VideoCamera, Warning, Loading, UserFilled, CircleCheckFilled, Position } from '@element-plus/icons-vue'
import { usePrescriptionStore } from '@/stores/prescription'
import { useIMStore } from '@/stores/im'
import { useUserStore } from '@/stores/user'
import { getDoctorDetail, getConsultationMessages, sendConsultationMessage } from '@/api/modules/inquiry'
import StepBar from './components/StepBar.vue'
import { ROUTES } from '@/constants/routes'
import type { ChatMessage, DoctorInfo } from '@/stores/prescription'

const router = useRouter()
const route = useRoute()
const prescriptionStore = usePrescriptionStore()
const imStore = useIMStore()
const userStore = useUserStore()

const consultationId = ref((route.query.consultationId as string) || (route.query.id as string) || prescriptionStore.consultationId)
const doctorId = ref((route.query.doctorId as string) || prescriptionStore.assignedDoctorId || '')

const currentStep = ref(2)
const doctorInfo = ref<DoctorInfo>({
  id: doctorId.value,
  name: '在线医生',
  title: '',
  hospital: '',
  department: '',
  avatar: '',
  isCertified: true
})

const messageList = ref<ChatMessage[]>([])
const inputMessage = ref('')
const isSending = ref(false)
const isGeneratingPrescription = ref(false)
const chatContainerRef = ref<HTMLElement>()
const imConnected = ref(false)
const isLoading = ref(true)
const error = ref('')
const pollingTimer = ref<ReturnType<typeof setInterval> | null>(null)

const goBack = () => router.back()

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  try {
    const date = new Date(timeStr)
    if (isNaN(date.getTime())) {
      if (timeStr.includes(':')) return timeStr.slice(-8, -3) || timeStr.slice(-5)
      return ''
    }
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } catch { return '' }
}

const handleNewMessage = (msgList: any[]) => {
  if (!Array.isArray(msgList)) return
  for (const timMsg of msgList) {
    const msgId = timMsg.id || timMsg.ID || ('msg_' + Date.now())
    if (messageList.value.some(m => m.id === msgId)) continue

    const content = timMsg.content || timMsg.payload?.text || '[消息]'
    const isPrescription = content.includes('【电子处方】')

    if (isPrescription) {
      isGeneratingPrescription.value = true
      prescriptionStore.loadLatestPrescription().then(() => {
        setTimeout(() => router.push(ROUTES.PRESCRIPTION_SUCCESS), 2000)
      })
    }

    messageList.value.push({
      id: msgId,
      type: timMsg.from === 'patient' ? 'user' : 'doctor',
      content,
      time: timMsg.time || new Date().toISOString()
    })
    scrollToBottom()
  }
}

const sendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content || isSending.value) return

  isSending.value = true
  messageList.value.push({
    id: 'MSG' + Date.now(),
    type: 'user',
    content,
    time: new Date().toISOString()
  })
  inputMessage.value = ''
  scrollToBottom()

  try {
    await sendConsultationMessage(consultationId.value, { type: 'text', content })
    if (imConnected.value) {
      try { await imStore.sendTextMessage(content) } catch { /* TIM静默失败 */ }
    }
  } catch (error: any) {
    console.error('[处方咨询] 发送失败:', error)
    ElMessage.error('发送失败，请重试')
  } finally {
    isSending.value = false
  }
}

const quickReplies = ['症状已经持续3天了', '有轻微发烧', '没有过敏史', '之前服用过类似药物']
const sendQuickReply = (reply: string) => { inputMessage.value = reply; sendMessage() }

const initChat = async () => {
  try {
    isLoading.value = true
    error.value = ''

    // 加载医生信息
    try {
      if (doctorId.value) {
        const doc = await getDoctorDetail(doctorId.value)
        doctorInfo.value = {
          id: doc.id || doctorId.value,
          name: doc.name || '在线医生',
          title: doc.title || '',
          hospital: doc.hospital || '',
          department: doc.department || '',
          avatar: doc.avatar || '',
          isCertified: true
        }
      }
    } catch { /* 使用默认信息 */ }

    prescriptionStore.setDoctorInfo(doctorInfo.value)
    if (consultationId.value) prescriptionStore.setConsultationId(consultationId.value)

    // 初始化 IM
    if (userStore.isLoggedIn) {
      try {
        if (!imStore.isInitialized) await imStore.initialize()
        await imStore.login(userStore.userInfo?.id?.toString() || '1', 'patient')
        imConnected.value = true
        imStore.on('MESSAGE_RECEIVED', handleNewMessage)
      } catch (imError: any) {
        console.warn('[处方咨询] IM初始化失败，使用API轮询:', imError.message)
      }
    }

    // 加载历史消息
    if (consultationId.value) {
      try {
        const apiMessages = await getConsultationMessages(consultationId.value)
        if (apiMessages && apiMessages.length > 0) {
          messageList.value = apiMessages.map((m: any) => ({
            id: m.id,
            type: m.sender === 'patient' ? 'user' : 'doctor',
            content: m.content,
            time: m.time || new Date().toISOString()
          } as ChatMessage))

          if (messageList.value.some(m => m.content?.includes('【电子处方】'))) {
            isGeneratingPrescription.value = true
          }
        }
      } catch { /* 无历史消息 */ }
    }

    if (messageList.value.length === 0) {
      messageList.value.push({
        id: 'welcome',
        type: 'doctor',
        content: `您好，我是${doctorInfo.value.name}，很高兴为您服务。请详细描述一下您的症状和病史。`,
        time: new Date().toISOString()
      })
    }
    scrollToBottom()
  } catch (e: any) {
    error.value = e.message || '连接失败'
  } finally {
    isLoading.value = false
  }
}

async function pollNewMessages() {
  if (!consultationId.value) return
  try {
    const apiMessages = await getConsultationMessages(consultationId.value)
    if (!apiMessages || !apiMessages.length) return
    for (const m of apiMessages) {
      if (messageList.value.some(local => local.id === m.id)) continue
      const isPrescription = m.content?.includes('【电子处方】')
      messageList.value.push({
        id: m.id, type: m.sender === 'patient' ? 'user' : 'doctor',
        content: m.content, time: m.time || new Date().toISOString()
      } as ChatMessage)
      if (isPrescription) {
        isGeneratingPrescription.value = true
        prescriptionStore.loadLatestPrescription().then(() => {
          setTimeout(() => router.push(ROUTES.PRESCRIPTION_SUCCESS), 2000)
        })
      }
    }
    scrollToBottom()
  } catch { /* 静默 */ }
}

function startPolling() { stopPolling(); pollingTimer.value = setInterval(pollNewMessages, 5000) }
function stopPolling() { if (pollingTimer.value) { clearInterval(pollingTimer.value); pollingTimer.value = null } }

const callDoctor = () => ElMessage.info('语音通话功能开发中')
const videoCall = () => ElMessage.info('视频通话功能开发中')

onMounted(() => { initChat().then(() => startPolling()) })
onUnmounted(() => {
  stopPolling()
  if (imConnected.value) imStore.off('MESSAGE_RECEIVED', handleNewMessage)
})
</script>

<template>
  <div class="prescription-consult-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="doctor-info">
        <div class="doctor-avatar">
          <img v-if="doctorInfo.avatar" :src="doctorInfo.avatar" :alt="doctorInfo.name">
          <el-icon v-else><UserFilled /></el-icon>
        </div>
        <div class="doctor-detail">
          <div class="doctor-name">
            {{ doctorInfo.name }}
            <span class="certified-badge" v-if="doctorInfo.isCertified">
              <el-icon><CircleCheckFilled /></el-icon>
            </span>
          </div>
          <div class="doctor-title">{{ doctorInfo.hospital }} · {{ doctorInfo.title }}</div>
        </div>
      </div>
      <div class="header-actions">
        <div class="action-btn" @click="callDoctor">
          <el-icon><Phone /></el-icon>
        </div>
        <div class="action-btn" @click="videoCall">
          <el-icon><VideoCamera /></el-icon>
        </div>
      </div>
    </div>

    <!-- 步骤条 -->
    <StepBar :current-step="currentStep" />

    <!-- 安全提示 -->
    <div class="safety-notice">
      <el-icon><Warning /></el-icon>
      <span>互联网诊疗仅限复诊患者，医生正在根据您提供的信息进行诊疗</span>
    </div>

    <!-- 聊天区域 -->
    <div ref="chatContainerRef" class="chat-container">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>正在连接医生...</span>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="retry-btn" @click="initChat">重试</button>
      </div>

      <!-- 正常聊天 -->
      <template v-else>
      <!-- 时间分隔线 -->
      <div class="time-divider">
        <span>{{ new Date().toLocaleDateString('zh-CN') }}</span>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="message in messageList"
          :key="message.id"
          :class="['message-item', message.type]"
        >
          <!-- 医生头像 -->
          <div v-if="message.type === 'doctor'" class="avatar doctor-avatar-small">
            <img v-if="doctorInfo.avatar" :src="doctorInfo.avatar" :alt="doctorInfo.name">
            <el-icon v-else><UserFilled /></el-icon>
          </div>

          <!-- 消息内容 -->
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text">{{ message.content }}</div>
            </div>
            <div class="message-time">{{ formatTime(message.time) }}</div>
          </div>

          <!-- 用户头像 -->
          <div v-if="message.type === 'user'" class="avatar user-avatar">
            <el-icon><User /></el-icon>
          </div>
        </div>

        <!-- 处方开具中提示 -->
        <div v-if="isGeneratingPrescription" class="prescription-loading">
          <div class="loading-content">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <span>医生正在为您开具处方...</span>
          </div>
        </div>
      </div>
      </template>
    </div>

    <!-- 快捷回复 -->
    <div v-if="!isGeneratingPrescription" class="quick-replies">
      <div class="quick-reply-list">
        <span
          v-for="(reply, index) in quickReplies"
          :key="index"
          class="quick-reply-item"
          @click="sendQuickReply(reply)"
        >
          {{ reply }}
        </span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <input
          v-model="inputMessage"
          type="text"
          placeholder="请输入您的问题..."
          :disabled="isSending || isGeneratingPrescription"
          @keyup.enter="sendMessage"
        />
        <button
          :class="['send-btn', { active: inputMessage.trim() && !isSending }]"
          :disabled="!inputMessage.trim() || isSending || isGeneratingPrescription"
          @click="sendMessage"
        >
          <el-icon v-if="isSending"><Loading /></el-icon>
          <el-icon v-else><Position /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.prescription-consult-page {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;
    color: #fff;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
    }
  }

  .doctor-info {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;
    justify-content: center;

    .doctor-avatar {
      width: 40px;
      height: 40px;
      background: $bg-white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-tertiary;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .el-icon {
        font-size: 20px;
      }
    }

    .doctor-detail {
      text-align: left;

      .doctor-name {
        font-size: $font-md;
        font-weight: 600;
        color: #fff;
        display: flex;
        align-items: center;
        gap: 4px;

        .certified-badge {
          color: $success;
          font-size: 14px;
        }
      }

      .doctor-title {
        font-size: $font-sm;
        color: rgba(255, 255, 255, 0.8);
        margin-top: 2px;
      }
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;

    .action-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: 50%;
      transition: all 0.2s;
      color: #fff;
      background: rgba(255, 255, 255, 0.2);

      &:hover {
        background: rgba(255, 255, 255, 0.35);
      }
    }
  }
}

// 安全提示
.safety-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: $spacing-sm $spacing-md;
  background: rgba($warning, 0.1);
  color: $warning;
  font-size: $font-sm;

  .el-icon {
    font-size: 14px;
  }
}

// 聊天容器
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-md;
  -webkit-overflow-scrolling: touch;
}

// 加载/错误状态
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xxl;
  color: $text-tertiary;
  gap: 12px;

  .loading-icon {
    font-size: 32px;
    animation: rotate 1s linear infinite;
  }

  p { font-size: $font-sm; }

  .retry-btn {
    padding: 8px 24px;
    background: $primary;
    color: #fff;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: $font-md;
  }
}

// 时间分隔线
.time-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;

  span {
    font-size: $font-sm;
    color: $text-tertiary;
    background: $bg-primary;
    padding: 4px 12px;
    border-radius: 12px;
  }
}

// 消息列表
.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

// 消息项
.message-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;

  &.doctor {
    .message-content {
      align-items: flex-start;
    }

    .message-bubble {
      background: $bg-white;
      border-bottom-left-radius: 4px;
    }
  }

  &.user {
    flex-direction: row-reverse;

    .message-content {
      align-items: flex-end;
    }

    .message-bubble {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: #fff;
      border-bottom-right-radius: 4px;
    }
  }

  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    overflow: hidden;

    &.doctor-avatar-small {
      background: $bg-white;
      color: $text-tertiary;
      border: 1px solid $border-light;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .el-icon {
        font-size: 20px;
      }
    }

    &.user-avatar {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: #fff;

      .el-icon {
        font-size: 20px;
      }
    }
  }

  .message-content {
    display: flex;
    flex-direction: column;
    max-width: 70%;
  }

  .message-bubble {
    padding: 12px 16px;
    border-radius: 16px;
    font-size: $font-md;
    line-height: 1.5;
    word-break: break-word;
    box-shadow: $shadow-sm;
  }

  .message-time {
    font-size: 11px;
    color: $text-tertiary;
    margin-top: 4px;
  }
}

// 处方开具中
.prescription-loading {
  display: flex;
  justify-content: center;
  margin: 20px 0;

  .loading-content {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    background: $bg-white;
    border-radius: 20px;
    box-shadow: $shadow-sm;
    font-size: $font-sm;
    color: $text-secondary;

    .loading-icon {
      font-size: 16px;
      animation: rotate 1s linear infinite;
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 快捷回复
.quick-replies {
  padding: $spacing-md;
  background: $bg-white;
  border-top: 1px solid $border-light;

  .quick-reply-list {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    padding-bottom: 4px;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .quick-reply-item {
    flex-shrink: 0;
    padding: 8px 14px;
    background: $bg-primary;
    border-radius: 16px;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;

    &:hover {
      background: rgba($primary, 0.15);
      color: $primary;
    }
  }
}

// 输入区域
.input-area {
  padding: $spacing-md;
  padding-bottom: calc($safe-area-bottom + $spacing-md);
  background: $bg-white;
  border-top: 1px solid $border-light;

  .input-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    background: $bg-primary;
    border-radius: 24px;
    padding: 4px 4px 4px 16px;

    input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: $font-md;
      color: $text-primary;
      outline: none;
      padding: 10px 0;

      &::placeholder {
        color: $text-tertiary;
      }

      &:disabled {
        color: $text-tertiary;
      }
    }

    .send-btn {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 50%;
      background: $text-tertiary;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;
      flex-shrink: 0;

      &.active {
        background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
        color: #fff;
        box-shadow: 0 2px 8px rgba($primary, 0.3);

        &:hover {
          transform: scale(1.05);
        }
      }

      &:disabled {
        cursor: not-allowed;
        opacity: 0.6;
      }

      .el-icon {
        font-size: 18px;
      }
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .nav-header {
    .doctor-info {
      .doctor-detail {
        .doctor-name {
          font-size: 14px;
        }

        .doctor-title {
          font-size: 11px;
        }
      }
    }
  }

  .message-item {
    .message-content {
      max-width: 75%;
    }

    .message-bubble {
      padding: 10px 14px;
      font-size: 13px;
    }
  }

  .quick-reply-item {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>
