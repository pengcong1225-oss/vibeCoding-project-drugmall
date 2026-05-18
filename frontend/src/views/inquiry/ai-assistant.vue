<template>
  <div class="ai-assistant-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">AI问诊助手</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-section" ref="chatSectionRef">
      <!-- AI助手欢迎消息 -->
      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>您好，我是您的AI问诊助手 🤖</p>
          <p>请描述您的症状、不适部位、持续时间等信息，我将为您推荐合适的科室和医生。</p>
        </div>
      </div>

      <!-- 症状示例提示 -->
      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p class="hint-title">您可以这样描述：</p>
          <div class="example-list">
            <div class="example-item" @click="useExample('我最近头痛，伴有恶心，已经持续3天了')">
              <el-icon><Document /></el-icon>
              <span>我最近头痛，伴有恶心，已经持续3天了</span>
            </div>
            <div class="example-item" @click="useExample('孩子发烧38.5度，咳嗽有痰，精神不太好')">
              <el-icon><Document /></el-icon>
              <span>孩子发烧38.5度，咳嗽有痰，精神不太好</span>
            </div>
            <div class="example-item" @click="useExample('皮肤瘙痒，出现红疹，越抓越痒')">
              <el-icon><Document /></el-icon>
              <span>皮肤瘙痒，出现红疹，越抓越痒</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户消息 -->
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.type + '-message']">
        <div v-if="msg.type === 'assistant'" class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p v-if="msg.type === 'user'">{{ msg.content }}</p>
          <div v-else-if="msg.type === 'assistant'">
            <p>{{ msg.content }}</p>
            <!-- 推荐科室 -->
            <div v-if="msg.departments" class="recommend-section">
              <p class="recommend-title">🏥 推荐科室</p>
              <div class="dept-tags">
                <span
                  v-for="dept in msg.departments"
                  :key="dept.code"
                  class="dept-tag"
                  :class="{ active: selectedDept === dept.code }"
                  @click="selectDept(dept)"
                >
                  {{ dept.name }}
                </span>
              </div>
            </div>
            <!-- 推荐医生 -->
            <div v-if="msg.doctors" class="recommend-section">
              <p class="recommend-title">👨‍⚕️ 推荐医生</p>
              <div class="doctor-list">
                <div
                  v-for="doctor in msg.doctors"
                  :key="doctor.id"
                  class="doctor-card"
                  @click="selectDoctor(doctor)"
                >
                  <img :src="doctor.avatar" class="doctor-avatar" alt="医生头像" />
                  <div class="doctor-info">
                    <div class="doctor-name">{{ doctor.name }} <span class="doctor-title">{{ doctor.title }}</span></div>
                    <div class="doctor-hospital">{{ doctor.hospital }}</div>
                    <div class="doctor-specialty">擅长：{{ doctor.specialty }}</div>
                    <div class="doctor-meta">
                      <span class="price">¥{{ doctor.price }}</span>
                      <span class="wait-time">{{ doctor.waitTime }}分钟接诊</span>
                    </div>
                  </div>
                  <button class="consult-btn-small" @click.stop="goToConsult(doctor)">咨询</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content loading">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="input-section">
      <div class="input-wrapper">
        <input
          v-model="inputMessage"
          type="text"
          placeholder="请描述您的症状..."
          @keyup.enter="sendMessage"
          :disabled="loading"
        />
        <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim() || loading">
          <el-icon><Position /></el-icon>
        </button>
      </div>
      <div class="quick-actions">
        <span class="action-tag" @click="useExample('感冒发烧')">感冒发烧</span>
        <span class="action-tag" @click="useExample('皮肤问题')">皮肤问题</span>
        <span class="action-tag" @click="useExample('肠胃不适')">肠胃不适</span>
        <span class="action-tag" @click="useExample('睡眠问题')">睡眠问题</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Service, Position, Document } from '@element-plus/icons-vue'
import { aiAssistantApi } from '@/api/modules/ai-assistant'
import { getDoctorList } from '@/api/modules/inquiry'
import { ElMessage } from 'element-plus'

const router = useRouter()
const chatSectionRef = ref<HTMLElement>()
const inputMessage = ref('')
const loading = ref(false)
const selectedDept = ref('')
const currentSessionId = ref('')

interface Message {
  type: 'user' | 'assistant'
  content: string
  departments?: Department[]
  doctors?: Doctor[]
}

interface Department {
  code: string
  name: string
}

interface Doctor {
  id: string
  name: string
  title: string
  hospital: string
  department: string
  avatar: string
  specialty: string
  price: number
  waitTime: number
}

const messages = ref<Message[]>([])

// 模拟科室数据
const mockDepartments: Department[] = [
  { code: 'internal', name: '内科' },
  { code: 'surgery', name: '外科' },
  { code: 'pediatrics', name: '儿科' },
  { code: 'dermatology', name: '皮肤科' },
  { code: 'tcm', name: '中医科' },
  { code: 'psychology', name: '心理咨询' },
  { code: 'gynecology', name: '妇科' },
  { code: 'andrology', name: '男科' },
]

// 模拟医生数据
const mockDoctors: Doctor[] = [
  {
    id: '1',
    name: '刘贞君',
    title: '主治医师',
    hospital: '山东青岛中西医结合医院',
    department: '皮肤科',
    avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face',
    specialty: '中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病',
    price: 19.9,
    waitTime: 9
  },
  {
    id: '2',
    name: '张晓明',
    title: '副主任医师',
    hospital: '首都儿科研究所',
    department: '儿科',
    avatar: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=200&h=200&fit=crop&crop=face',
    specialty: '小儿发热、呼吸道感染、消化系统疾病',
    price: 29.9,
    waitTime: 15
  },
  {
    id: '3',
    name: '王雪梅',
    title: '主任医师',
    hospital: '北京大学第六医院',
    department: '心理咨询',
    avatar: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=200&h=200&fit=crop&crop=face',
    specialty: '焦虑症、抑郁症、睡眠障碍、情绪管理',
    price: 199,
    waitTime: 5
  }
]

function goBack() {
  router.back()
}

function useExample(text: string) {
  inputMessage.value = text
  sendMessage()
}

function sendMessage() {
  const content = inputMessage.value.trim()
  if (!content || loading.value) return

  messages.value.push({ type: 'user', content })
  inputMessage.value = ''
  loading.value = true

  scrollToBottom()

  aiAssistantApi.chat({
    message: content,
    sessionId: currentSessionId.value || undefined
  }).then(res => {
    const response = res as any
    if (response.sessionId) {
      currentSessionId.value = response.sessionId
    }
    const aiMessage: Message = {
      type: 'assistant',
      content: response.content || '抱歉，我暂时无法回答这个问题，请稍后再试。',
      departments: [],
      doctors: []
    }
    if (response.drugs && response.drugs.length > 0) {
      aiMessage.departments = [{ code: 'internal', name: '内科' }]
      fetchDoctorsByDepartment('internal')
    }
    messages.value.push(aiMessage)
    loading.value = false
    scrollToBottom()
  }).catch(error => {
    console.error('AI对话失败:', error)
    ElMessage.error('AI服务暂时不可用，请稍后再试')
    loading.value = false
    messages.value.push({
      type: 'assistant',
      content: '抱歉，AI服务暂时不可用。请尝试描述更详细的症状，或选择科室直接咨询。',
      departments: [],
      doctors: []
    })
    scrollToBottom()
  })
}

async function fetchDoctorsByDepartment(department: string) {
  try {
    const doctors = await getDoctorList({ department })
    if (Array.isArray(doctors) && doctors.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg && lastMsg.type === 'assistant') {
        lastMsg.doctors = doctors.slice(0, 3).map((d: any) => ({
          id: d.id,
          name: d.name,
          title: d.title,
          hospital: d.hospital,
          department: d.department,
          avatar: d.avatar || '',
          specialty: d.specialty || '',
          price: d.price || 0,
          waitTime: d.waitTime || 0
        }))
      }
      scrollToBottom()
    }
  } catch (error) {
    console.error('获取医生列表失败:', error)
  }
}

function selectDept(dept: Department) {
  selectedDept.value = dept.code
  fetchDoctorsByDepartment(dept.code)
}

function selectDoctor(doctor: Doctor) {
  // 选中医生，可以显示更多信息
}

function goToConsult(doctor: Doctor) {
  router.push({
    path: `/inquiry/pre/${doctor.id}`,
    query: {
      name: doctor.name,
      department: doctor.department
    }
  })
}

function scrollToBottom() {
  nextTick(() => {
    if (chatSectionRef.value) {
      chatSectionRef.value.scrollTop = chatSectionRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.ai-assistant-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: white;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: background-color 0.2s;

    &:active {
      background-color: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: 17px;
    font-weight: 600;
  }

  .nav-placeholder {
    width: 36px;
  }
}

// 聊天区域
.chat-section {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  padding-bottom: 180px;
}

.message {
  display: flex;
  margin-bottom: 16px;

  &.user-message {
    flex-direction: row-reverse;

    .message-content {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: white;
      margin-right: 0;
      margin-left: 40px;
      border-radius: 16px 4px 16px 16px;
    }
  }

  &.assistant-message {
    .message-content {
      background-color: white;
      margin-left: 0;
      margin-right: 40px;
      border-radius: 4px 16px 16px 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
  }
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.assistant-avatar {
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: white;
    font-size: 18px;
  }
}

.message-content {
  padding: 12px 16px;
  max-width: calc(100% - 60px);
  font-size: 14px;
  line-height: 1.6;

  p {
    margin: 0 0 8px 0;

    &:last-child {
      margin-bottom: 0;
    }
  }

  &.loading {
    display: flex;
    gap: 4px;
    padding: 16px;

    .dot {
      width: 8px;
      height: 8px;
      background-color: $primary;
      border-radius: 50%;
      animation: bounce 1.4s infinite ease-in-out both;

      &:nth-child(1) { animation-delay: -0.32s; }
      &:nth-child(2) { animation-delay: -0.16s; }
    }
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

// 示例列表
.hint-title {
  font-size: 13px;
  color: $text-secondary;
  margin-bottom: 8px;
}

.example-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.example-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
  font-size: 13px;
  color: $text-primary;
  cursor: pointer;
  transition: all 0.2s;

  .el-icon {
    color: $primary;
    font-size: 14px;
  }

  &:active {
    background-color: #e8f5e9;
  }
}

// 推荐区域
.recommend-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e0e0e0;
}

.recommend-title {
  font-size: 13px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8px;
}

.dept-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.dept-tag {
  padding: 6px 14px;
  background-color: #f0f7ff;
  color: $primary;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;

  &.active,
  &:active {
    background-color: $primary;
    color: white;
  }
}

// 医生列表
.doctor-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    background-color: #e8f5e9;
  }

  .doctor-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }

  .doctor-info {
    flex: 1;
    min-width: 0;

    .doctor-name {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;

      .doctor-title {
        font-size: 12px;
        font-weight: normal;
        color: $text-secondary;
        margin-left: 4px;
      }
    }

    .doctor-hospital {
      font-size: 12px;
      color: $text-secondary;
      margin-top: 2px;
    }

    .doctor-specialty {
      font-size: 11px;
      color: $text-tertiary;
      margin-top: 2px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .doctor-meta {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 4px;

      .price {
        font-size: 14px;
        font-weight: 600;
        color: $error;
      }

      .wait-time {
        font-size: 11px;
        color: $text-tertiary;
      }
    }
  }

  .consult-btn-small {
    padding: 6px 14px;
    background-color: $primary;
    color: white;
    border: none;
    border-radius: 14px;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    flex-shrink: 0;

    &:active {
      background-color: $primary-light;
    }
  }
}

// 底部输入区域
.input-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #f5f5f5;
  border-radius: 24px;
  padding: 4px 4px 4px 16px;

  input {
    flex: 1;
    border: none;
    background: transparent;
    font-size: 14px;
    color: $text-primary;
    outline: none;
    padding: 10px 0;

    &::placeholder {
      color: $text-tertiary;
    }
  }

  .send-btn {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: white;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      background: #ccc;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      transform: scale(0.95);
    }
  }
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  overflow-x: auto;
  padding-bottom: 4px;

  &::-webkit-scrollbar {
    display: none;
  }

  .action-tag {
    padding: 6px 12px;
    background-color: #f0f7ff;
    color: $primary;
    border-radius: 14px;
    font-size: 12px;
    white-space: nowrap;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      background-color: $primary;
      color: white;
    }
  }
}
</style>
