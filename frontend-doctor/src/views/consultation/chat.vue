<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useConsultationStore } from '@/stores/consultation'
import { showToast, showDialog, showImagePreview } from 'vant'

const route = useRoute()
const router = useRouter()
const consultationStore = useConsultationStore()

const consultationId = route.params.id as string
const messageListRef = ref<HTMLElement | null>(null)
const inputMessage = ref('')
const isInputFocus = ref(false)
const showMoreOptions = ref(false)
const showQuickReplies = ref(false)
const isRecording = ref(false)
const recordingDuration = ref(0)
const recordingTimer = ref<NodeJS.Timeout | null>(null)
const playingVoiceId = ref<string | null>(null)

// 语音消息列表(模拟)
const voiceMessages = ref<Record<string, number>>({})

// 快捷回复语
const quickReplies = ref([
  '您好,请详细描述一下您的症状',
  '请问您这种情况持续多久了?',
  '有没有服用过什么药物?',
  '请问您有过敏史吗?',
  '建议您先做个相关检查',
  '请按时服药,注意休息',
  '如有不适请及时复诊',
  '祝您早日康复!'
])

// 是否显示结束问诊确认
const showEndConfirm = ref(false)

// 计算消息列表
const messages = computed(() => {
  return consultationStore.currentConsultation?.messages || []
})

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// 获取消息样式
const getMessageClass = (sender: string) => {
  return sender === 'doctor' ? 'message-doctor' : sender === 'patient' ? 'message-patient' : 'message-system'
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  
  const content = inputMessage.value.trim()
  inputMessage.value = ''
  showQuickReplies.value = false
  
  await consultationStore.sendMessage(consultationId, {
    type: 'text',
    content
  })
  
  // 模拟患者回复
  setTimeout(() => {
    simulatePatientReply()
  }, 2000)
  
  scrollToBottom()
}

// 模拟患者回复
const simulatePatientReply = () => {
  const replies = [
    '好的,我明白了',
    '请问这个药有什么副作用吗?',
    '服用时需要注意什么?',
    '需要吃多久才能见效?',
    '谢谢医生!'
  ]
  const randomReply = replies[Math.floor(Math.random() * replies.length)]
  
  consultationStore.addMessage(consultationId, {
    sender: 'patient',
    type: 'text',
    content: randomReply
  })
  scrollToBottom()
}

// 选择快捷回复
const selectQuickReply = (text: string) => {
  inputMessage.value = text
  showQuickReplies.value = false
  sendMessage()
}

// 选择图片
const selectImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files[0]) {
      const file = target.files[0]
      const reader = new FileReader()
      reader.onload = async (e) => {
        const imageUrl = e.target?.result as string
        await consultationStore.sendMessage(consultationId, {
          type: 'image',
          content: imageUrl
        })
        scrollToBottom()
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
  showMoreOptions.value = false
}

// 预览图片
const previewImage = (url: string) => {
  showImagePreview([url])
}

// 开始录音
const startRecording = () => {
  isRecording.value = true
  recordingDuration.value = 0
  recordingTimer.value = setInterval(() => {
    recordingDuration.value++
    if (recordingDuration.value >= 60) {
      stopRecording()
    }
  }, 1000)
}

// 停止录音
const stopRecording = () => {
  if (recordingTimer.value) {
    clearInterval(recordingTimer.value)
    recordingTimer.value = null
  }
  
  if (recordingDuration.value > 1) {
    // 模拟发送语音消息
    const messageId = 'voice_' + Date.now()
    voiceMessages.value[messageId] = recordingDuration.value
    
    consultationStore.sendMessage(consultationId, {
      type: 'voice',
      content: messageId
    })
    scrollToBottom()
  }
  
  isRecording.value = false
  recordingDuration.value = 0
  showMoreOptions.value = false
}

// 播放/暂停语音
const toggleVoice = (messageId: string) => {
  if (playingVoiceId.value === messageId) {
    playingVoiceId.value = null
  } else {
    playingVoiceId.value = messageId
    // 模拟播放完成
    setTimeout(() => {
      if (playingVoiceId.value === messageId) {
        playingVoiceId.value = null
      }
    }, (voiceMessages.value[messageId] || 5) * 1000)
  }
}

// 结束问诊
const endConsultation = async () => {
  try {
    await showDialog.confirm({
      title: '结束问诊',
      message: '确定要结束当前问诊吗?结束后将无法继续发送消息',
      confirmButtonText: '确定结束',
      cancelButtonText: '取消',
      confirmButtonColor: '#00B578'
    })
    
    await consultationStore.endConsultation(consultationId)
    showToast('问诊已结束')
    router.push('/consultation')
  } catch {
    // 用户取消
  }
}

// 开具处方
const createPrescription = () => {
  router.push({
    path: '/prescription/create',
    query: { 
      consultationId, 
      patientId: consultationStore.currentConsultation?.patientId,
      patientName: consultationStore.currentConsultation?.patientName
    }
  })
}

// 查看患者详情
const viewPatientDetail = () => {
  router.push(`/patients/detail/${consultationStore.currentConsultation?.patientId}`)
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 格式化语音时长
const formatDuration = (seconds: number) => {
  return `${seconds}"`
}

// 获取消息状态文本
const getStatusText = (status?: string) => {
  switch (status) {
    case 'read': return '已读'
    case 'sent': return '已送达'
    case 'sending': return '发送中'
    default: return ''
  }
}

onMounted(() => {
  consultationStore.fetchConsultationDetail(consultationId)
  scrollToBottom()
})

onUnmounted(() => {
  if (recordingTimer.value) {
    clearInterval(recordingTimer.value)
  }
})
</script>

<template>
  <div class="chat-page">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <div class="header-title" @click="viewPatientDetail">
        <div class="patient-name">{{ consultationStore.currentConsultation?.patientName || '患者' }}</div>
        <div class="consultation-status">
          <span class="status-dot" :class="consultationStore.currentConsultation?.status"></span>
          {{ consultationStore.currentConsultation?.status === 'processing' ? '问诊中' : '已结束' }}
        </div>
      </div>
      <div class="header-right" @click="createPrescription" v-if="consultationStore.currentConsultation?.status === 'processing'">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
        </svg>
        <span>开处方</span>
      </div>
      <div class="header-right" v-else @click="viewPatientDetail">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
      </div>
    </div>

    <!-- 患者信息卡片 -->
    <div class="patient-card" @click="viewPatientDetail" v-if="consultationStore.currentConsultation">
      <div class="patient-info">
        <div class="info-item">
          <span class="label">性别</span>
          <span class="value">{{ consultationStore.currentConsultation?.patientGender || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">年龄</span>
          <span class="value">{{ consultationStore.currentConsultation?.patientAge || '-' }}岁</span>
        </div>
        <div class="info-item">
          <span class="label">类型</span>
          <span class="value type-tag" :class="consultationStore.currentConsultation?.type">
            {{ consultationStore.currentConsultation?.type || '-' }}
          </span>
        </div>
      </div>
      <div class="symptom-info" v-if="consultationStore.currentConsultation?.symptom">
        <span class="label">症状</span>
        <span class="value">{{ consultationStore.currentConsultation?.symptom }}</span>
      </div>
      <div class="more-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </div>
    </div>

    <!-- 消息列表 -->
    <div ref="messageListRef" class="message-list">
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-item"
        :class="getMessageClass(message.sender)"
      >
        <!-- 系统消息 -->
        <template v-if="message.sender === 'system'">
          <div class="system-message">{{ message.content }}</div>
        </template>
        
        <!-- 患者消息 -->
        <template v-else-if="message.sender === 'patient'">
          <div class="message-avatar">
            {{ consultationStore.currentConsultation?.patientName?.[0] || '患' }}
          </div>
          <div class="message-content">
            <div class="message-body">
              <template v-if="message.type === 'text'">
                {{ message.content }}
              </template>
              <template v-else-if="message.type === 'image'">
                <img :src="message.content" alt="图片" class="message-image" @click="previewImage(message.content)" />
              </template>
              <template v-else-if="message.type === 'voice'">
                <div class="voice-message" @click="toggleVoice(message.content)">
                  <div class="voice-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path>
                      <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
                      <line x1="12" y1="19" x2="12" y2="23"></line>
                      <line x1="8" y1="23" x2="16" y2="23"></line>
                    </svg>
                  </div>
                  <span class="voice-duration">{{ formatDuration(voiceMessages[message.content] || 5) }}</span>
                </div>
              </template>
            </div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </template>
        
        <!-- 医生消息 -->
        <template v-else>
          <div class="message-content">
            <div class="message-body">
              <template v-if="message.type === 'text'">
                {{ message.content }}
              </template>
              <template v-else-if="message.type === 'image'">
                <img :src="message.content" alt="图片" class="message-image" @click="previewImage(message.content)" />
              </template>
              <template v-else-if="message.type === 'voice'">
                <div class="voice-message" @click="toggleVoice(message.content)" :class="{ playing: playingVoiceId === message.content }">
                  <span class="voice-duration">{{ formatDuration(voiceMessages[message.content] || 5) }}</span>
                  <div class="voice-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path>
                      <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
                      <line x1="12" y1="19" x2="12" y2="23"></line>
                      <line x1="8" y1="23" x2="16" y2="23"></line>
                    </svg>
                  </div>
                </div>
              </template>
              <template v-else-if="message.type === 'prescription'">
                <div class="prescription-card" @click="router.push(`/prescription/detail/${message.prescriptionId}`)">
                  <div class="prescription-header">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                      <polyline points="14 2 14 8 20 8"></polyline>
                    </svg>
                    <span>处方单</span>
                  </div>
                  <div class="prescription-drugs">{{ message.content }}</div>
                  <div class="prescription-footer">点击查看详情</div>
                </div>
              </template>
            </div>
            <div class="message-status">
              <span class="message-time">{{ message.time }}</span>
              <span v-if="message.status" class="read-status" :class="message.status">
                {{ getStatusText(message.status) }}
              </span>
            </div>
          </div>
          <div class="message-avatar doctor">
            医
          </div>
        </template>
      </div>
    </div>

    <!-- 快捷回复面板 -->
    <div v-if="showQuickReplies" class="quick-replies-panel">
      <div class="panel-header">
        <span>快捷回复</span>
        <svg class="close-btn" @click="showQuickReplies = false" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </div>
      <div class="quick-replies-list">
        <div 
          v-for="(reply, index) in quickReplies" 
          :key="index"
          class="quick-reply-item"
          @click="selectQuickReply(reply)"
        >
          {{ reply }}
        </div>
        <div class="quick-reply-item add" @click="showQuickReplies = false">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          添加快捷语
        </div>
      </div>
    </div>

    <!-- 录音提示 -->
    <div v-if="isRecording" class="recording-overlay">
      <div class="recording-panel">
        <div class="recording-wave">
          <span></span><span></span><span></span><span></span><span></span>
        </div>
        <div class="recording-time">{{ formatDuration(recordingDuration) }}</div>
        <div class="recording-text">松开手指结束录音</div>
      </div>
    </div>

    <!-- 结束问诊确认对话框 -->
    <van-dialog
      v-model:show="showEndConfirm"
      title="结束问诊"
      message="确定要结束当前问诊吗?结束后将无法继续发送消息"
      show-cancel-button
      confirm-button-color="#00B578"
      @confirm="confirmEndConsultation"
    />

    <!-- 底部输入区 -->
    <div class="chat-footer" :class="{ focus: isInputFocus }" v-if="consultationStore.currentConsultation?.status === 'processing'">
      <div class="input-toolbar">
        <div class="toolbar-item" @click="showQuickReplies = !showQuickReplies">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
          </svg>
          <span>快捷语</span>
        </div>
        <div class="toolbar-item" @click="selectImage">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
          <span>图片</span>
        </div>
        <div class="toolbar-item" @touchstart.prevent="startRecording" @touchend.prevent="stopRecording">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path>
            <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
            <line x1="12" y1="19" x2="12" y2="23"></line>
            <line x1="8" y1="23" x2="16" y2="23"></line>
          </svg>
          <span>语音</span>
        </div>
        <div class="toolbar-item end-consult" @click="showEndConfirm = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
          <span>结束</span>
        </div>
      </div>
      
      <div class="input-area">
        <input
          v-model="inputMessage"
          type="text"
          placeholder="请输入消息..."
          @focus="isInputFocus = true"
          @blur="isInputFocus = false"
          @keyup.enter="sendMessage"
        />
        <button class="btn-send" :disabled="!inputMessage.trim()" @click="sendMessage">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </div>
    </div>

    <!-- 问诊已结束提示 -->
    <div class="consultation-ended" v-else-if="consultationStore.currentConsultation?.status === 'completed'">
      <div class="ended-text">问诊已结束</div>
      <div class="ended-actions">
        <button class="btn-action" @click="viewPatientDetail">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          患者详情
        </button>
        <button class="btn-action" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
          返回列表
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #F8F9FA;
}

// 顶部导航
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #F0F0F0;
  
  .header-left {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 4px;
    
    svg {
      width: 24px;
      height: 24px;
      color: #333;
    }
  }
  
  .header-title {
    flex: 1;
    text-align: center;
    cursor: pointer;
    
    .patient-name {
      font-size: 17px;
      font-weight: 600;
      color: #333;
      margin-bottom: 2px;
    }
    
    .consultation-status {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      font-size: 12px;
      color: #666;
      
      .status-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        
        &.processing {
          background: #00B578;
          animation: pulse 2s infinite;
        }
        
        &.completed {
          background: #999;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    cursor: pointer;
    padding: 4px;
    color: #00B578;
    
    svg {
      width: 20px;
      height: 20px;
    }
    
    span {
      font-size: 10px;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

// 患者信息卡片
.patient-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;
  
  .patient-info {
    flex: 1;
    display: flex;
    gap: 16px;
    
    .info-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .label {
        font-size: 11px;
        color: #999;
      }
      
      .value {
        font-size: 13px;
        color: #333;
        font-weight: 500;
        
        &.type-tag {
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 11px;
          
          &.图文问诊 {
            background: #E6F7FF;
            color: #1890FF;
          }
          
          &.视频问诊 {
            background: #F6FFED;
            color: #52C41A;
          }
          
          &.复诊 {
            background: #FFF7E6;
            color: #FA8C16;
          }
        }
      }
    }
  }
  
  .symptom-info {
    max-width: 200px;
    display: flex;
    flex-direction: column;
    gap: 2px;
    
    .label {
      font-size: 11px;
      color: #999;
    }
    
    .value {
      font-size: 12px;
      color: #666;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  .more-btn {
    svg {
      width: 16px;
      height: 16px;
      color: #999;
    }
  }
}

// 消息列表
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  
  .message-item {
    display: flex;
    margin-bottom: 16px;
    
    &.message-system {
      justify-content: center;
      
      .system-message {
        padding: 6px 12px;
        background: rgba(0, 0, 0, 0.05);
        border-radius: 12px;
        font-size: 12px;
        color: #999;
      }
    }
    
    &.message-patient {
      .message-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #E6F7FF;
        color: #1890FF;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        font-weight: 500;
        margin-right: 12px;
      }
      
      .message-content {
        max-width: 70%;
        
        .message-body {
          padding: 12px 16px;
          background: #fff;
          border-radius: 16px;
          font-size: 15px;
          color: #333;
          line-height: 1.5;
          
          .message-image {
            max-width: 200px;
            border-radius: 8px;
            cursor: pointer;
          }
          
          .voice-message {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            background: #F0F0F0;
            border-radius: 20px;
            cursor: pointer;
            
            .voice-icon {
              width: 20px;
              height: 20px;
              color: #666;
            }
            
            .voice-duration {
              font-size: 14px;
              color: #666;
            }
          }
        }
        
        .message-time {
          margin-top: 4px;
          font-size: 11px;
          color: #999;
        }
      }
    }
    
    &.message-doctor {
      flex-direction: row-reverse;
      
      .message-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #00B578;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        font-weight: 500;
        margin-left: 12px;
      }
      
      .message-content {
        max-width: 70%;
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        
        .message-body {
          padding: 12px 16px;
          background: #00B578;
          color: #fff;
          border-radius: 16px;
          font-size: 15px;
          line-height: 1.5;
          
          .message-image {
            max-width: 200px;
            border-radius: 8px;
            cursor: pointer;
          }
          
          .voice-message {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            background: rgba(255,255,255,0.2);
            border-radius: 20px;
            cursor: pointer;
            
            &.playing {
              animation: voicePlaying 1s infinite;
            }
            
            .voice-icon {
              width: 20px;
              height: 20px;
              color: #fff;
            }
            
            .voice-duration {
              font-size: 14px;
              color: #fff;
            }
          }
          
          .prescription-card {
            background: rgba(255,255,255,0.95);
            border-radius: 8px;
            padding: 12px;
            color: #333;
            cursor: pointer;
            min-width: 200px;
            
            .prescription-header {
              display: flex;
              align-items: center;
              gap: 6px;
              margin-bottom: 8px;
              font-weight: 600;
              color: #00B578;
              
              svg {
                width: 16px;
                height: 16px;
              }
            }
            
            .prescription-drugs {
              font-size: 13px;
              color: #666;
              margin-bottom: 8px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            
            .prescription-footer {
              font-size: 12px;
              color: #00B578;
              text-align: center;
              padding-top: 8px;
              border-top: 1px dashed #E0E0E0;
            }
          }
        }
        
        .message-status {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-top: 4px;
          
          .message-time {
            font-size: 11px;
            color: #999;
          }
          
          .read-status {
            font-size: 11px;
            
            &.read {
              color: #00B578;
            }
            
            &.sent {
              color: #999;
            }
            
            &.sending {
              color: #999;
            }
          }
        }
      }
    }
  }
}

@keyframes voicePlaying {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

// 快捷回复面板
.quick-replies-panel {
  background: #fff;
  border-top: 1px solid #F0F0F0;
  max-height: 300px;
  overflow-y: auto;
  
  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    border-bottom: 1px solid #F0F0F0;
    
    span {
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }
    
    .close-btn {
      width: 20px;
      height: 20px;
      color: #999;
      cursor: pointer;
    }
  }
  
  .quick-replies-list {
    padding: 8px;
    
    .quick-reply-item {
      padding: 12px 16px;
      margin-bottom: 8px;
      background: #F5F5F5;
      border-radius: 8px;
      font-size: 14px;
      color: #333;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background: #E8F5E9;
        color: #00B578;
      }
      
      &.add {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
