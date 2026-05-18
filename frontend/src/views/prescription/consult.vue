<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Phone, VideoCamera, MoreFilled, Warning } from '@element-plus/icons-vue'
import { usePrescriptionStore } from '@/stores/prescription'
import StepBar from './components/StepBar.vue'
import { ROUTES } from '@/constants/routes'
import type { ChatMessage, DoctorInfo } from '@/stores/prescription'

const router = useRouter()
const route = useRoute()
const prescriptionStore = usePrescriptionStore()

// 当前步骤
const currentStep = ref(2)

// 医生信息
const doctorInfo = ref<DoctorInfo>({
  id: 'DOC001',
  name: '王医生',
  title: '副主任医师',
  hospital: '北京协和医院',
  department: '内科',
  avatar: '',
  isCertified: true
})

// 聊天消息列表
const messageList = ref<ChatMessage[]>([
  {
    id: '1',
    type: 'doctor',
    content: '您好，我是王医生，很高兴为您服务。请详细描述一下您的症状和病史。',
    time: new Date(Date.now() - 1000 * 60 * 5).toISOString()
  }
])

// 输入的消息
const inputMessage = ref('')

// 是否正在发送
const isSending = ref(false)

// 是否显示处方开具中
const isGeneratingPrescription = ref(false)

// 聊天容器引用
const chatContainerRef = ref<HTMLElement>()

// 返回上一页
const goBack = () => {
  router.back()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 发送消息
const sendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content || isSending.value) return

  isSending.value = true

  // 添加用户消息
  const userMessage: ChatMessage = {
    id: 'MSG' + Date.now(),
    type: 'user',
    content,
    time: new Date().toISOString()
  }
  messageList.value.push(userMessage)
  inputMessage.value = ''
  scrollToBottom()

  try {
    // 模拟医生回复
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const doctorReply: ChatMessage = {
      id: 'MSG' + (Date.now() + 1),
      type: 'doctor',
      content: '收到，根据您描述的症状，我需要为您开具处方。请稍等片刻。',
      time: new Date().toISOString(),
      avatar: doctorInfo.value.avatar
    }
    messageList.value.push(doctorReply)
    scrollToBottom()

    // 模拟处方开具中
    setTimeout(() => {
      isGeneratingPrescription.value = true
      scrollToBottom()
      
      setTimeout(async () => {
        await prescriptionStore.setDoctorInfo(doctorInfo.value)
        await prescriptionStore.completePrescription()
        router.push(ROUTES.PRESCRIPTION_SUCCESS)
      }, 3000)
    }, 1500)

  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请重试')
  } finally {
    isSending.value = false
  }
}

// 快捷回复
const quickReplies = [
  '症状已经持续3天了',
  '有轻微发烧',
  '没有过敏史',
  '之前服用过类似药物'
]

const sendQuickReply = (reply: string) => {
  inputMessage.value = reply
  sendMessage()
}

// 拨打电话
const callDoctor = () => {
  ElMessage.info('正在为您接通医生电话...')
}

// 视频通话
const videoCall = () => {
  ElMessage.info('正在为您发起视频通话...')
}

onMounted(() => {
  // 设置医生信息到store
  prescriptionStore.setDoctorInfo(doctorInfo.value)
  
  // 如果有咨询ID，加载聊天记录
  const consultationId = route.query.id as string
  if (consultationId) {
    prescriptionStore.setConsultationId(consultationId)
  }
  
  scrollToBottom()
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
