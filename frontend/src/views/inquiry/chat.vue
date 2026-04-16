<template>
  <div class="chat-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">专家问诊</span>
      <div class="header-actions">
        <button class="action-btn" @click="viewRecords">
          <span class="action-text">咨询记录</span>
        </button>
      </div>
    </div>

    <!-- 流程指示器 -->
    <div class="process-indicator">
      <div class="process-step" :class="{ active: currentStep >= 1, current: currentStep === 1 }">
        <div class="step-number">①</div>
        <div class="step-name">导诊助手</div>
      </div>
      <div class="step-arrow">></div>
      <div class="process-step" :class="{ active: currentStep >= 2, current: currentStep === 2 }">
        <div class="step-number">②</div>
        <div class="step-name">支付诊费</div>
      </div>
      <div class="step-arrow">></div>
      <div class="process-step" :class="{ active: currentStep >= 3, current: currentStep === 3 }">
        <div class="step-number">③</div>
        <div class="step-name">医生接诊</div>
      </div>
      <div class="step-arrow">></div>
      <div class="process-step" :class="{ active: currentStep >= 4, current: currentStep === 4 }">
        <div class="step-number">④</div>
        <div class="step-name">问诊咨询</div>
      </div>
    </div>

    <!-- 医生信息卡片 -->
    <div class="doctor-info-card">
      <div class="doctor-header">
        <img :src="doctorAvatar" class="doctor-avatar" />
        <div class="doctor-basic">
          <div class="name-row">
            <span class="doctor-name">{{ doctorName }}</span>
            <span class="doctor-title">{{ doctorTitle }}</span>
          </div>
          <div class="hospital-row">
            <span class="hospital-badge">三甲</span>
            <span class="hospital-name">{{ hospital }}·{{ department }}</span>
          </div>
          <div class="specialty-row">
            <span class="specialty-label">擅长：</span>
            <span class="specialty-text">{{ specialty }}</span>
          </div>
        </div>
      </div>
      <div class="service-info">
        <div class="service-type">图文咨询</div>
        <div class="service-price">¥{{ price }}</div>
        <div class="wait-time">平均接诊时长{{ waitTime }}分钟</div>
      </div>
      <div class="guarantee-tags">
        <div class="guarantee-tag">
          <el-icon><CircleCheck /></el-icon>
          <span>权益保障</span>
        </div>
        <div class="guarantee-tag">
          <el-icon><Timer /></el-icon>
          <span>24h未接诊自动退款</span>
        </div>
      </div>
      <div class="service-note">
        <el-icon><ChatDotRound /></el-icon>
        <span>接诊后24h不限次沟通</span>
      </div>
    </div>

    <!-- 服务声明 -->
    <div class="service-declaration">
      <span class="declaration-text">美团旗下互联网医院提供服务</span>
      <a class="agreement-link" @click="showAgreement">《知情同意书》</a>
    </div>

    <!-- 消息列表 -->
    <div ref="messageListRef" class="message-list">
      <!-- 医生助手欢迎消息 -->
      <div class="assistant-message">
        <div class="message-bubble">
          <div class="message-title">👤 您好，我是您的医生助手</div>
          <div class="message-content">
            详细描述患者的病情，如：症状、持续时间、用药情况、过敏史等，以便医生更准确地判断
          </div>
        </div>
      </div>

      <!-- 示例消息 -->
      <div class="example-message">
        <div class="message-bubble">
          <div class="message-title">示例：</div>
          <div class="message-content">
            半夜咽痒咳嗽，持续1周，无发热，有过敏性鼻炎史，目前服用氯雷他定片
          </div>
        </div>
      </div>

      <!-- 时间分割线 -->
      <div v-if="messages.length > 0" class="time-divider">
        <span>{{ formatDate(new Date()) }}</span>
      </div>

      <!-- 消息气泡 -->
      <WeChatBubble
        v-for="(msg, index) in messages"
        :key="msg.id || index"
        :message="msg"
        :direction="msg.isSelf ? 'right' : 'left'"
        :avatar="msg.isSelf ? patientAvatar : doctorAvatar"
        :sender-name="msg.isSelf ? '我' : doctorName"
        @preview-image="handlePreviewImage"
        @view-prescription="handleViewPrescription"
      />

      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <!-- 空状态 -->
      <div v-if="!isLoading && messages.length === 0 && !error" class="empty-state">
        <div class="empty-icon">💬</div>
        <p>请描述您的病情，医生将尽快回复</p>
      </div>

      <!-- 错误状态 -->
      <div v-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="retryInit">重试</button>
      </div>
    </div>

    <!-- 快捷回复 -->
    <div v-if="showQuickReplies" class="quick-replies">
      <div class="quick-reply-title">快捷回复</div>
      <div class="quick-reply-list">
        <button
          v-for="reply in quickReplies"
          :key="reply"
          class="quick-reply-btn"
          @click="sendQuickReply(reply)"
        >
          {{ reply }}
        </button>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <!-- 输入框 -->
      <div class="input-box">
        <textarea
          v-model="inputMessage"
          class="text-input"
          placeholder="请详细描述您的病情"
          :disabled="!imConnected || isLoading"
          rows="1"
          @input="autoResize"
          @keyup.enter.prevent="handleSendMessage"
        />
        <button
          class="send-btn"
          :disabled="!canSend"
          @click="handleSendMessage"
        >
          <span v-if="isSending">
            <el-icon class="loading-icon"><Loading /></el-icon>
          </span>
          <span v-else>发送</span>
        </button>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewImages"
      :initial-index="previewIndex"
      @close="previewVisible = false"
    />

    <!-- 知情同意书弹窗 -->
    <el-dialog
      v-model="agreementVisible"
      title="知情同意书"
      width="90%"
      class="agreement-dialog"
    >
      <div class="agreement-content">
        <h4>互联网诊疗服务知情同意书</h4>
        <p>1. 互联网诊疗服务适用于常见病、慢性病的复诊，不适用于急危重症患者。</p>
        <p>2. 患者应如实提供病情信息，配合医生完成诊疗。</p>
        <p>3. 医生根据患者提供的信息进行诊疗建议，患者应理解互联网诊疗的局限性。</p>
        <p>4. 如病情变化或出现紧急情况，应及时到实体医院就诊。</p>
        <p>5. 本服务遵循医疗保密原则，保护患者隐私。</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="agreementVisible = false">我已阅读并同意</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, CircleCheck, Timer, ChatDotRound, Loading } from '@element-plus/icons-vue'
import WeChatBubble from '@/components/chat/WeChatBubble.vue'

const router = useRouter()
const route = useRoute()

// 路由参数
const consultationId = ref((route.query.id as string) || (route.query.consultationId as string) || '1')
const doctorId = ref((route.query.doctorId as string) || 'DOC001')
const doctorName = ref((route.query.doctorName as string) || '刘贞君')
const doctorTitle = ref((route.query.doctorTitle as string) || '主治医师')
const department = ref((route.query.department as string) || '皮肤科')
const hospital = ref('山东青岛中西医结合医院')
const specialty = ref('擅长中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病')
const price = ref((route.query.price as string) || '19.9')
const waitTime = ref(12)

// 当前步骤
const currentStep = ref(1)

// 头像
const patientAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=patient'
const doctorAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor1'

// 状态
const inputMessage = ref('')
const messageListRef = ref<HTMLElement>()
const messages = ref<Array<{
  id: string
  content: string
  type: 'text' | 'image' | 'prescription'
  url?: string
  from: string
  isSelf: boolean
  time: string
  status?: 'sent' | 'read' | 'failed'
  cardData?: any
}>>([])
const isLoading = ref(false)
const isSending = ref(false)
const error = ref('')
const imConnected = ref(true)
const showQuickReplies = ref(false)
const agreementVisible = ref(false)

// 图片预览
const previewVisible = ref(false)
const previewImages = ref<string[]>([])
const previewIndex = ref(0)

// 快捷回复
const quickReplies = ref([
  '半夜咽痒咳嗽，持续1周',
  '皮肤瘙痒，有红疹',
  '胃疼，饭后加重',
  '头疼，伴有恶心',
  '失眠，难以入睡',
  '其他症状'
])

// 计算属性
const canSend = computed(() => {
  return inputMessage.value.trim().length > 0 && !isSending.value && imConnected.value
})

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 格式化日期
const formatDate = (date: Date): string => {
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 查看咨询记录
const viewRecords = () => {
  ElMessage.info('咨询记录功能开发中')
}

// 显示知情同意书
const showAgreement = () => {
  agreementVisible.value = true
}

// 发送快捷回复
const sendQuickReply = (reply: string) => {
  inputMessage.value = reply
  showQuickReplies.value = false
  handleSendMessage()
}

// 自动调整输入框高度
const autoResize = (e: Event) => {
  const textarea = e.target as HTMLTextAreaElement
  textarea.style.height = 'auto'
  textarea.style.height = Math.min(textarea.scrollHeight, 100) + 'px'
}

// 发送消息
const handleSendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content || !canSend.value) return

  inputMessage.value = ''
  isSending.value = true

  // 添加消息到列表
  const message = {
    id: 'MSG_' + Date.now(),
    content,
    type: 'text' as const,
    from: 'patient',
    isSelf: true,
    time: formatDate(new Date()),
    status: 'sent' as const
  }
  messages.value.push(message)
  scrollToBottom()

  // 更新步骤
  if (currentStep.value < 4) {
    currentStep.value = 4
  }

  try {
    // 模拟发送成功
    await new Promise(resolve => setTimeout(resolve, 300))
    message.status = 'read'

    // 模拟医生回复
    setTimeout(() => {
      simulateDoctorReply()
    }, 1500)
  } catch (e: any) {
    console.error('发送消息失败:', e)
    message.status = 'failed'
    ElMessage.error('发送失败，请重试')
  } finally {
    isSending.value = false
  }
}

// 模拟医生回复
const simulateDoctorReply = () => {
  const replies = [
    '您好，请详细描述一下您的症状。',
    '了解了，这种情况持续多久了？',
    '建议您可以先做一些基础检查。',
    '这个症状需要进一步观察，建议您及时就医。',
    '我给您开一些药，您按说明服用。',
    '平时要注意休息，避免熬夜。'
  ]
  const randomReply = replies[Math.floor(Math.random() * replies.length)]

  const reply = {
    id: 'MSG_' + Date.now(),
    content: randomReply,
    type: 'text' as const,
    from: 'doctor',
    isSelf: false,
    time: formatDate(new Date()),
    status: 'read' as const
  }
  messages.value.push(reply)
  scrollToBottom()
}

// 预览图片
const handlePreviewImage = (url: string) => {
  previewImages.value = [url]
  previewIndex.value = 0
  previewVisible.value = true
}

// 查看处方
const handleViewPrescription = (data?: any) => {
  console.log('查看处方:', data)
  ElMessage.info('查看处方详情')
}

// 初始化
const initIM = async () => {
  try {
    isLoading.value = true
    error.value = ''

    // 模拟IM初始化
    await new Promise(resolve => setTimeout(resolve, 500))
    imConnected.value = true
    isLoading.value = false

  } catch (e: any) {
    console.error('[Chat] IM初始化失败:', e)
    error.value = e.message || '连接失败'
    imConnected.value = false
    isLoading.value = false
  }
}

// 重试初始化
const retryInit = () => {
  initIM()
}

onMounted(() => {
  initIM()
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/inquiry-theme' as *;

.chat-page {
  min-height: 100vh;
  background: $inquiry-bg;
  display: flex;
  flex-direction: column;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  color: $text-white;
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

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    flex: 1;
    text-align: center;
  }

  .header-actions {
    display: flex;
    justify-content: flex-end;

    .action-btn {
      padding: 6px 12px;
      background: transparent;
      border: none;
      color: $text-white;
      cursor: pointer;
      border-radius: 14px;
      font-size: 13px;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 流程指示器
.process-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 12px $spacing-md;
  background: $inquiry-card-bg;
  border-bottom: 1px solid rgba($inquiry-primary, 0.08);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    display: none;
  }

  .process-step {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;

    .step-number {
      font-size: 14px;
      color: $inquiry-offline;
      font-weight: 500;
    }

    .step-name {
      font-size: 11px;
      color: $inquiry-text-tertiary;
      white-space: nowrap;
    }

    &.active {
      .step-number {
        color: $inquiry-primary;
      }

      .step-name {
        color: $inquiry-text-secondary;
      }
    }

    &.current {
      .step-number {
        color: $inquiry-primary;
        font-weight: 700;
      }

      .step-name {
        color: $inquiry-primary;
        font-weight: 500;
      }
    }
  }

  .step-arrow {
    font-size: 12px;
    color: rgba($inquiry-primary, 0.3);
    flex-shrink: 0;
  }
}

// 医生信息卡片
.doctor-info-card {
  background: $inquiry-card-bg;
  margin: $spacing-md;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.06);

  .doctor-header {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;

    .doctor-avatar {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      object-fit: cover;
      background: $inquiry-bg;
    }

    .doctor-basic {
      flex: 1;
      min-width: 0;

      .name-row {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .doctor-name {
          font-size: 16px;
          font-weight: 600;
          color: $inquiry-text-primary;
        }

        .doctor-title {
          font-size: 12px;
          color: $inquiry-text-secondary;
        }
      }

      .hospital-row {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 4px;

        .hospital-badge {
          padding: 1px 6px;
          background: rgba($inquiry-primary, 0.1);
          color: $inquiry-primary;
          border-radius: 4px;
          font-size: 10px;
          font-weight: 600;
        }

        .hospital-name {
          font-size: 12px;
          color: $inquiry-text-secondary;
        }
      }

      .specialty-row {
        display: flex;
        gap: 4px;
        font-size: 12px;

        .specialty-label {
          color: $inquiry-text-tertiary;
          flex-shrink: 0;
        }

        .specialty-text {
          color: $inquiry-text-secondary;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }

  .service-info {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 0;
    border-top: 1px solid rgba($inquiry-primary, 0.08);
    border-bottom: 1px solid rgba($inquiry-primary, 0.08);
    margin-bottom: 12px;

    .service-type {
      font-size: 14px;
      color: $inquiry-text-primary;
      font-weight: 500;
    }

    .service-price {
      font-size: 18px;
      font-weight: 700;
      color: $inquiry-tag-price;
    }

    .wait-time {
      font-size: 12px;
      color: $inquiry-text-tertiary;
      margin-left: auto;
    }
  }

  .guarantee-tags {
    display: flex;
    gap: 12px;
    margin-bottom: 8px;

    .guarantee-tag {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: $inquiry-primary;

      .el-icon {
        font-size: 14px;
      }
    }
  }

  .service-note {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: $inquiry-text-tertiary;

    .el-icon {
      font-size: 14px;
    }
  }
}

// 服务声明
.service-declaration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px $spacing-md;
  font-size: 11px;
  color: $inquiry-text-tertiary;

  .declaration-text {
    color: $inquiry-text-tertiary;
  }

  .agreement-link {
    color: $inquiry-primary;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

// 消息列表
.message-list {
  flex: 1;
  padding: $spacing-md;
  overflow-y: auto;
  display: flex;
  flex-direction: column;

  .assistant-message,
  .example-message {
    margin-bottom: 16px;

    .message-bubble {
      background: $inquiry-card-bg;
      border-radius: 12px;
      padding: 12px 16px;
      box-shadow: 0 2px 8px rgba(0, 201, 167, 0.06);

      .message-title {
        font-size: 14px;
        font-weight: 500;
        color: $inquiry-text-primary;
        margin-bottom: 8px;
      }

      .message-content {
        font-size: 13px;
        color: $inquiry-text-secondary;
        line-height: 1.5;
      }
    }
  }

  .example-message {
    .message-bubble {
      background: rgba($inquiry-primary, 0.05);
      border: 1px dashed rgba($inquiry-primary, 0.3);

      .message-title {
        color: $inquiry-primary;
      }
    }
  }

  .time-divider {
    text-align: center;
    margin: $spacing-md 0;

    span {
      font-size: 12px;
      color: $inquiry-text-tertiary;
      background: rgba($inquiry-primary, 0.08);
      padding: 4px 12px;
      border-radius: 10px;
    }
  }

  .loading-state {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: $spacing-xl;
    color: $inquiry-text-tertiary;
    font-size: $font-sm;

    .loading-icon {
      animation: rotate 1s linear infinite;
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $spacing-xxl;
    color: $inquiry-text-tertiary;

    .empty-icon {
      font-size: 48px;
      margin-bottom: $spacing-md;
    }

    p {
      font-size: $font-sm;
    }
  }

  .error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $spacing-xxl;
    color: $inquiry-text-tertiary;

    p {
      margin-bottom: $spacing-md;
    }

    button {
      padding: $spacing-sm $spacing-lg;
      background: $inquiry-primary;
      color: $text-white;
      border: none;
      border-radius: $radius-md;
      cursor: pointer;

      &:hover {
        background: $inquiry-primary-dark;
      }
    }
  }
}

// 快捷回复
.quick-replies {
  background: $inquiry-card-bg;
  border-top: 1px solid rgba($inquiry-primary, 0.08);
  padding: $spacing-md;

  .quick-reply-title {
    font-size: $font-xs;
    color: $inquiry-text-tertiary;
    margin-bottom: $spacing-sm;
  }

  .quick-reply-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .quick-reply-btn {
      padding: 6px 12px;
      background: $inquiry-bg;
      border: 1px solid rgba($inquiry-primary, 0.15);
      border-radius: 14px;
      font-size: 13px;
      color: $inquiry-text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: rgba($inquiry-primary, 0.1);
        border-color: $inquiry-primary;
        color: $inquiry-primary;
      }

      &:active {
        transform: scale(0.95);
      }
    }
  }
}

// 输入区域
.input-section {
  background: $inquiry-card-bg;
  border-top: 1px solid rgba($inquiry-primary, 0.08);
  padding: $spacing-sm $spacing-md;
  padding-bottom: calc($spacing-md + $safe-area-bottom);

  .input-box {
    display: flex;
    align-items: flex-end;
    gap: $spacing-sm;

    .text-input {
      flex: 1;
      min-height: 44px;
      max-height: 100px;
      border: 1px solid rgba($inquiry-primary, 0.2);
      border-radius: 22px;
      padding: 10px $spacing-md;
      font-size: $font-md;
      line-height: 1.5;
      resize: none;
      outline: none;
      transition: border-color 0.2s;
      background: $inquiry-bg;
      color: $inquiry-text-primary;

      &:focus {
        border-color: $inquiry-primary;
        background: $inquiry-card-bg;
      }

      &:disabled {
        background: $bg-gray;
        cursor: not-allowed;
      }

      &::placeholder {
        color: $inquiry-text-tertiary;
      }
    }

    .send-btn {
      height: 44px;
      padding: 0 $spacing-lg;
      background: $inquiry-primary;
      color: $text-white;
      border: none;
      border-radius: 22px;
      font-size: $font-md;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      min-width: 60px;

      &:hover:not(:disabled) {
        background: $inquiry-primary-dark;
      }

      &:disabled {
        background: $inquiry-text-tertiary;
        cursor: not-allowed;
      }

      .loading-icon {
        animation: rotate 1s linear infinite;
      }
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 知情同意书弹窗
.agreement-content {
  padding: $spacing-md;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: $inquiry-text-primary;
    margin-bottom: 16px;
    text-align: center;
  }

  p {
    font-size: 14px;
    color: $inquiry-text-secondary;
    line-height: 1.8;
    margin-bottom: 12px;
  }
}

:deep(.agreement-dialog) {
  .el-button--primary {
    background: $inquiry-primary;
    border-color: $inquiry-primary;

    &:hover {
      background: $inquiry-primary-dark;
      border-color: $inquiry-primary-dark;
    }
  }
}
</style>
