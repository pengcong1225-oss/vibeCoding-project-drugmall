<template>
  <div class="ai-assistant-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h2 class="page-title">宜格健康管家</h2>
      <div class="header-actions">
        <div class="action-btn" @click="showHistory = true">
          <el-icon><Clock /></el-icon>
        </div>
      </div>
    </div>

    <!-- 空状态首页 -->
    <div v-if="!messages.length" class="home-section">
      <!-- 吉祥物区域 -->
      <div class="mascot-section">
        <div class="mascot-wrapper">
          <div class="mascot-container">
            <div class="mascot-body">
              <div class="mascot-face">
                <div class="eyes">
                  <div class="eye left"></div>
                  <div class="eye right"></div>
                </div>
                <div class="cheeks">
                  <div class="cheek left"></div>
                  <div class="cheek right"></div>
                </div>
                <div class="mouth"></div>
              </div>
              <div class="stethoscope"></div>
              <div class="coat"></div>
            </div>
          </div>
          <div class="mascot-shadow"></div>
        </div>
        <h1 class="main-title">描述症状，为您找药</h1>
        <p class="sub-title">我是宜格健康管家，时刻待命，等待您的召唤和提问~</p>
        <div class="wechat-link" @click="showWechatTip">
          <el-icon><ChatDotRound /></el-icon>
          <span>加我微信好友</span>
          <span class="link-highlight">找我更方便</span>
        </div>
      </div>

      <!-- Tab切换区 -->
      <div class="tab-section">
        <div class="tab-bar">
          <div
            v-for="tab in tabs"
            :key="tab.id"
            class="tab-item"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            {{ tab.name }}
          </div>
        </div>

        <!-- 热门问题 -->
        <div v-if="activeTab === 'hot'" class="tab-content">
          <div
            v-for="(question, index) in hotQuestions.slice(0, 3)"
            :key="index"
            class="question-item"
            @click="quickQuery(question)"
          >
            <div class="question-num">{{ index + 1 }}</div>
            <span class="question-text">{{ question }}</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 对症找药 -->
        <div v-if="activeTab === 'symptom'" class="tab-content">
          <div
            v-for="(symptom, index) in symptomList.slice(0, 3)"
            :key="symptom.id"
            class="question-item"
            @click="quickQuery(symptom.query)"
          >
            <div class="question-num">{{ index + 1 }}</div>
            <span class="question-text">{{ symptom.name }}吃什么药？</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 用药科普 -->
        <div v-if="activeTab === 'knowledge'" class="tab-content">
          <div
            v-for="(item, index) in knowledgeList.slice(0, 3)"
            :key="index"
            class="question-item"
            @click="quickQuery(item.query)"
          >
            <div class="question-num">{{ index + 1 }}</div>
            <span class="question-text">{{ item.title }}</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 健康问答 -->
        <div v-if="activeTab === 'qa'" class="tab-content">
          <div
            v-for="(qa, index) in qaList.slice(0, 3)"
            :key="index"
            class="question-item"
            @click="quickQuery(qa.query)"
          >
            <div class="question-num">{{ index + 1 }}</div>
            <span class="question-text">{{ qa.text }}</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 免责声明 -->
      <div class="disclaimer-bar">
        <el-icon><InfoFilled /></el-icon>
        <span>内容由AI生成，仅供参考。购药请考虑个体差异及药品品牌/批次，咨询医生或商家确认</span>
      </div>
    </div>

    <!-- 对话区域 -->
    <div v-else ref="chatContainer" class="chat-section">
      <div class="chat-list">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message-item"
          :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
        >
          <!-- AI头像 -->
          <div v-if="message.type === 'ai'" class="message-avatar ai-avatar">
            <div class="mini-mascot">
              <div class="mini-face">
                <div class="mini-eyes">
                  <div class="mini-eye"></div>
                  <div class="mini-eye"></div>
                </div>
                <div class="mini-mouth"></div>
              </div>
            </div>
          </div>
          <!-- 用户头像 -->
          <img v-else :src="userAvatar" class="message-avatar user-avatar" alt="User">

          <div class="message-content">
            <!-- 消息气泡 -->
            <div class="message-bubble" :class="message.type">
              <!-- 结构化内容展示 -->
              <div v-if="message.structured" class="structured-content">
                <!-- 原因分析 -->
                <div v-if="message.structured.causeAnalysis" class="content-section">
                  <div class="section-title">
                    <span class="title-icon">🔍</span>
                    原因分析
                  </div>
                  <div class="section-body" v-html="highlightKeywords(message.structured.causeAnalysis)"></div>
                </div>

                <!-- 治疗方案 -->
                <div v-if="message.structured.treatment" class="content-section">
                  <div class="section-title">
                    <span class="title-icon">💊</span>
                    治疗方案
                  </div>
                  <div class="section-body" v-html="highlightKeywords(message.structured.treatment)"></div>
                </div>

                <!-- 注意事项 -->
                <div v-if="message.structured.precautions" class="content-section">
                  <div class="section-title">
                    <span class="title-icon">⚠️</span>
                    注意事项
                  </div>
                  <div class="section-body">{{ message.structured.precautions }}</div>
                </div>

                <!-- 展开/收起按钮 -->
                <div v-if="message.structured.hasMore" class="expand-btn" @click="toggleExpand(index)">
                  <span>{{ message.expanded ? '收起' : '展开更多' }}</span>
                  <el-icon :class="{ expanded: message.expanded }"><ArrowDown /></el-icon>
                </div>
              </div>
              <!-- 普通文本 -->
              <div v-else class="message-text" v-html="formatMessage(message.content)"></div>
            </div>

            <!-- 推荐药品卡片 -->
            <div v-if="message.drugs?.length" class="drug-recommend-section">
              <div class="section-header">
                <div class="header-icon">
                  <el-icon><FirstAidKit /></el-icon>
                </div>
                <span class="header-title">为您推荐</span>
                <span class="header-subtitle">{{ message.drugs.length }}款相关药品</span>
              </div>

              <div class="drug-list">
                <div
                  v-for="drug in message.drugs"
                  :key="drug.id"
                  class="drug-card"
                  @click="goToDrugDetail(drug.id)"
                >
                  <div class="drug-image">
                    <img :src="drug.image" :alt="drug.name">
                    <div v-if="drug.isRx" class="rx-badge">处方药</div>
                  </div>
                  <div class="drug-info">
                    <div class="drug-name">{{ drug.name }}</div>
                    <div class="drug-indication">{{ drug.indication || '治疗' + drug.name.replace(/[^\u4e00-\u9fa5]/g, '').slice(0, 4) }}</div>
                    <div class="drug-footer">
                      <span class="drug-price">¥{{ drug.price.toFixed(2) }}</span>
                      <button class="buy-btn" @click.stop="handleAddToCart(drug)">
                        去购买
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 快捷操作 -->
              <div class="action-bar">
                <button class="action-btn consult" @click="goToInquiry">
                  <el-icon><User /></el-icon>
                  咨询医生
                </button>
                <button class="action-btn add-all" @click="addToCart(message.drugs)">
                  <el-icon><ShoppingCart /></el-icon>
                  全部加购
                </button>
              </div>
            </div>

            <!-- 推荐医生 -->
            <div v-if="message.doctors?.length" class="doctor-recommend-section">
              <div class="section-header">
                <div class="header-icon doctor">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <span class="header-title">推荐医生</span>
                <span class="header-subtitle">专业医师在线接诊</span>
              </div>
              <div class="doctor-list">
                <div
                  v-for="doctor in message.doctors"
                  :key="doctor.id"
                  class="doctor-card-mini"
                  @click="goToConsult(doctor)"
                >
                  <img :src="doctor.avatar" class="doctor-avatar-mini" :alt="doctor.name">
                  <div class="doctor-mini-info">
                    <div class="doctor-mini-name">{{ doctor.name }} <span class="title">{{ doctor.title }}</span></div>
                    <div class="doctor-mini-hospital">{{ doctor.hospital }}</div>
                    <div class="doctor-mini-price">¥{{ doctor.price || 19.9 }}起</div>
                  </div>
                  <button class="consult-mini-btn">咨询</button>
                </div>
              </div>
            </div>

            <!-- AI消息免责声明 -->
            <div v-if="message.type === 'ai'" class="message-disclaimer">
              <el-icon><InfoFilled /></el-icon>
              <span>以上建议仅供参考，不能替代医生诊断</span>
            </div>
          </div>
        </div>

        <!-- 加载中 -->
        <div v-if="isSending" class="message-item ai-message loading">
          <div class="message-avatar ai-avatar">
            <div class="mini-mascot">
              <div class="mini-face">
                <div class="mini-eyes">
                  <div class="mini-eye"></div>
                  <div class="mini-eye"></div>
                </div>
                <div class="mini-mouth"></div>
              </div>
            </div>
          </div>
          <div class="message-content">
            <div class="loading-bubble">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="bottom-section">
      <!-- 快捷操作栏（仅在对话状态显示） -->
      <div v-if="messages.length" class="quick-bar">
        <span
          v-for="action in quickActions"
          :key="action"
          class="quick-tag"
          @click="quickQuery(action)"
        >
          {{ action }}
        </span>
      </div>

      <!-- 输入框 -->
      <div class="input-bar">
        <button
          class="icon-btn voice-btn"
          :class="{ recording: isRecording }"
          @touchstart="startVoice"
          @touchend="stopVoice"
          @mousedown="startVoice"
          @mouseup="stopVoice"
        >
          <el-icon><Microphone /></el-icon>
        </button>
        <div class="input-wrapper">
          <input
            v-model="inputMessage"
            type="text"
            :placeholder="isRecording ? '正在录音...' : '请输入您的问题...'"
            @keyup.enter="sendMessage"
            :disabled="isRecording"
          >
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleFileUpload"
          >
        </div>
        <button class="icon-btn camera-btn" @click="triggerUpload">
          <el-icon><Camera /></el-icon>
        </button>
        <button
          class="send-btn"
          :disabled="!inputMessage.trim() || isSending"
          @click="sendMessage"
        >
          <span v-if="!isSending">发送</span>
          <el-icon v-else class="loading-icon"><Loading /></el-icon>
        </button>
      </div>

      <!-- 医疗免责声明 -->
      <div class="medical-disclaimer">
        <span>本服务仅供参考，不构成医疗诊断和治疗建议。如有严重症状，请及时就医。</span>
      </div>
    </div>

    <!-- 历史记录侧边栏 -->
    <el-drawer
      v-model="showHistory"
      title="对话历史"
      direction="rtl"
      size="300px"
    >
      <div class="history-list">
        <div v-if="historyList.length === 0" class="empty-history">
          <el-icon class="empty-icon"><ChatDotRound /></el-icon>
          <p>暂无对话历史</p>
        </div>
        <div
          v-for="(item, index) in historyList"
          :key="item.id"
          class="history-item"
          @click="loadHistory(item)"
        >
          <el-icon class="history-icon"><ChatLineRound /></el-icon>
          <div class="history-info">
            <div class="history-summary">{{ item.summary }}</div>
            <div class="history-time">{{ item.time }}</div>
          </div>
          <el-icon class="delete-icon" @click.stop="deleteHistory(index)">
            <Delete />
          </el-icon>
        </div>
      </div>
      <template #footer>
        <el-button type="danger" text @click="handleClearChat">清空历史</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { aiAssistantApi } from '@/api/modules/ai-assistant'
import type { RecommendedDrug } from '@/api/modules/ai-assistant'
import {
  ArrowLeft, Clock, ChatDotRound, ArrowRight,
  FirstAidKit, User, ShoppingCart, UserFilled,
  InfoFilled, QuestionFilled, Microphone, Camera,
  Loading, Delete, ChatLineRound, ChatDotRound as ChatIcon
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userAvatar = computed(() => userStore.userInfo?.avatar || 'https://via.placeholder.com/40x40/00C853/ffffff?text=U')

// 会话状态
const sessionId = ref<string>('')
const messages = ref<Array<{
  type: 'user' | 'ai'
  content: string
  structured?: {
    causeAnalysis?: string
    treatment?: string
    precautions?: string
    hasMore?: boolean
  }
  expanded?: boolean
  drugs?: RecommendedDrug[]
  doctors?: RecommendedDoctor[]
  showActions?: boolean
}>>([])

const inputMessage = ref('')
const isSending = ref(false)
const isRecording = ref(false)
const showHistory = ref(false)
const chatContainer = ref<HTMLElement>()
const fileInput = ref<HTMLInputElement>()

// Tab状态
const activeTab = ref('hot')
const tabs = [
  { id: 'hot', name: '热门问题' },
  { id: 'symptom', name: '对症找药' },
  { id: 'knowledge', name: '用药科普' },
  { id: 'qa', name: '健康问答' }
]

// 热门问题
const hotQuestions = [
  '孩子呼吸重鼻子不通气是过敏吗',
  '一直打喷嚏可以吃氯雷他定吗',
  '三高常见原研药',
  '感冒发烧吃什么药好得快？',
  '孕妇可以服用哪些感冒药？',
  '失眠怎么办？'
]

// 症状列表
const symptomList = [
  { id: 1, name: '感冒发烧', query: '感冒发烧吃什么药？', icon: 'FirstAidKit', bgColor: '#FFF5F5' },
  { id: 2, name: '头痛', query: '头痛应该吃什么药？', icon: 'User', bgColor: '#E6F7FF' },
  { id: 3, name: '肠胃不适', query: '肠胃不适怎么办？', icon: 'FirstAidKit', bgColor: '#F6FFED' },
  { id: 4, name: '皮肤过敏', query: '皮肤过敏用什么药？', icon: 'User', bgColor: '#FFFBE6' },
  { id: 5, name: '咳嗽', query: '咳嗽多痰吃什么药？', icon: 'FirstAidKit', bgColor: '#FFF0F6' },
  { id: 6, name: '失眠', query: '失眠怎么办？', icon: 'User', bgColor: '#F9F0FF' },
  { id: 7, name: '腹泻', query: '腹痛腹泻怎么治疗？', icon: 'FirstAidKit', bgColor: '#E6FFFB' },
  { id: 8, name: '过敏', query: '过敏吃什么药？', icon: 'User', bgColor: '#FFF7E6' }
]

// 用药科普
const knowledgeList = [
  { tag: '常识', title: '处方药和非处方药的区别', query: '处方药和非处方药有什么区别？' },
  { tag: '注意', title: '孕妇用药安全指南', query: '孕妇用药需要注意什么？' },
  { tag: '儿童', title: '儿童用药剂量计算方法', query: '儿童用药剂量怎么计算？' },
  { tag: '储存', title: '家庭常备药品储存方法', query: '药品应该怎么储存？' }
]

// 健康问答
const qaList = [
  { text: '这个药有什么副作用？', query: '药品副作用有哪些？' },
  { text: '儿童用量是多少？', query: '儿童用药剂量是多少？' },
  { text: '降压药怎么吃？', query: '降压药服用方法' },
  { text: '胃疼应该吃什么药？', query: '胃疼吃什么药？' }
]

// 快捷操作
const quickActions = ['感冒发烧吃什么药？', '孕妇可以吃这个药吗？', '这个药有什么副作用', '儿童用量是多少？']

// 历史记录
const historyList = ref<Array<{
  id: string
  summary: string
  time: string
  messages: any[]
}>>([])

// 医生推荐类型
interface RecommendedDoctor {
  id: string
  name: string
  title: string
  hospital: string
  department: string
  avatar: string
  specialty: string
  rating?: number
  consultCount?: string
  price?: number
  isOnline?: boolean
}

// 模拟药品数据
const mockDrugs: RecommendedDrug[] = [
  {
    id: '1',
    name: '京都念慈菴蜜炼川贝枇杷膏',
    image: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop',
    price: 28.5,
    spec: '150ml',
    manufacturer: '京都念慈菴',
    isRx: false,
    indication: '治疗咳嗽'
  },
  {
    id: '2',
    name: '咳特灵胶囊',
    image: 'https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=200&h=200&fit=crop',
    price: 15.8,
    spec: '0.36g*30粒',
    manufacturer: '白云山',
    isRx: false,
    indication: '镇咳祛痰'
  },
  {
    id: '3',
    name: '布洛芬缓释胶囊',
    image: 'https://images.unsplash.com/photo-1585435557343-3b092031a831?w=200&h=200&fit=crop',
    price: 22.0,
    spec: '0.3g*24粒',
    manufacturer: '中美史克',
    isRx: false,
    indication: '缓解疼痛'
  },
  {
    id: '4',
    name: '阿莫西林胶囊',
    image: 'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=200&h=200&fit=crop',
    price: 35.0,
    spec: '0.25g*24粒',
    manufacturer: '华北制药',
    isRx: true,
    indication: '抗菌消炎'
  },
  {
    id: '5',
    name: '氯雷他定片',
    image: 'https://images.unsplash.com/photo-1631549916768-4119b2e5f926?w=200&h=200&fit=crop',
    price: 18.5,
    spec: '10mg*12片',
    manufacturer: '拜耳医药',
    isRx: false,
    indication: '抗过敏'
  },
  {
    id: '6',
    name: '蒙脱石散',
    image: 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=200&h=200&fit=crop',
    price: 25.0,
    spec: '3g*10袋',
    manufacturer: '博福-益普生',
    isRx: false,
    indication: '止泻'
  }
]

// 关键词高亮
const highlightKeywords = (content: string) => {
  const keywords = ['咳嗽', '过敏原', '普通感冒', '支气管炎', '病毒', '细菌', '感染', '发烧', '头痛', '过敏']
  let result = content
  keywords.forEach(keyword => {
    result = result.replace(
      new RegExp(keyword, 'g'),
      `<span class="highlight-keyword">${keyword}</span>`
    )
  })
  return result
}

// 格式化消息
const formatMessage = (content: string) => {
  return content.replace(/\n/g, '<br>')
}

// 切换展开
const toggleExpand = (index: number) => {
  messages.value[index].expanded = !messages.value[index].expanded
}

// 根据用户输入智能推荐药品
const getRecommendedDrugs = (userMessage: string): RecommendedDrug[] => {
  const message = userMessage.toLowerCase()
  const keywords: Record<string, string[]> = {
    '咳嗽': ['京都念慈菴', '咳特灵'],
    '感冒': ['感冒灵', '布洛芬'],
    '头痛': ['布洛芬'],
    '疼痛': ['布洛芬'],
    '过敏': ['氯雷他定'],
    '皮肤': ['氯雷他定'],
    '腹泻': ['蒙脱石散'],
    '拉肚子': ['蒙脱石散'],
    '消炎': ['阿莫西林'],
    '抗生素': ['阿莫西林']
  }

  const matchedDrugs: RecommendedDrug[] = []
  const addedIds = new Set<string>()

  for (const [keyword, drugNames] of Object.entries(keywords)) {
    if (message.includes(keyword)) {
      for (const drugName of drugNames) {
        const drug = mockDrugs.find(d => d.name.includes(drugName))
        if (drug && !addedIds.has(drug.id)) {
          matchedDrugs.push(drug)
          addedIds.add(drug.id)
        }
      }
    }
  }

  return matchedDrugs.length > 0 ? matchedDrugs.slice(0, 3) : mockDrugs.slice(0, 2)
}

// 判断是否显示药品推荐
const shouldShowDrugRecommendation = (userMessage: string): boolean => {
  const message = userMessage.toLowerCase()
  const drugKeywords = ['药', '吃什么', '用什么', '治疗', '缓解', '发烧', '感冒', '头痛', '过敏', '腹泻', '消炎', '咳嗽']
  return drugKeywords.some(keyword => message.includes(keyword))
}

// 解析结构化内容
const parseStructuredContent = (content: string) => {
  const structured: {
    causeAnalysis?: string
    treatment?: string
    precautions?: string
    hasMore?: boolean
  } = {}

  // 提取原因分析
  const causeMatch = content.match(/(?:原因分析|病因|可能原因)[：:]?\s*([^]*?)(?=\n\s*(?:治疗|方案|注意|建议)|$)/i)
  if (causeMatch) {
    structured.causeAnalysis = causeMatch[1].trim()
  }

  // 提取治疗方案
  const treatmentMatch = content.match(/(?:治疗方案|治疗|用药建议)[：:]?\s*([^]*?)(?=\n\s*(?:注意|建议|提醒)|$)/i)
  if (treatmentMatch) {
    structured.treatment = treatmentMatch[1].trim()
  }

  // 提取注意事项
  const precautionsMatch = content.match(/(?:注意事项|注意|提醒)[：:]?\s*([^]*?)$/i)
  if (precautionsMatch) {
    structured.precautions = precautionsMatch[1].trim()
  }

  structured.hasMore = !!(structured.causeAnalysis || structured.treatment)

  return structured
}

// 发送消息
const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text || isSending.value) return

  messages.value.push({
    type: 'user',
    content: text
  })

  inputMessage.value = ''
  isSending.value = true

  await nextTick()
  scrollToBottom()

  try {
    const response = await aiAssistantApi.chat({
      message: text,
      sessionId: sessionId.value,
      history: messages.value.slice(0, -1).map(msg => ({
        role: msg.type === 'user' ? 'user' : 'assistant',
        content: msg.content
      }))
    })

    if (response.data?.data) {
      const data = response.data.data

      if (data.sessionId) {
        sessionId.value = data.sessionId
        localStorage.setItem('ai_session_id', data.sessionId)
      }

      // 解析结构化内容
      const structured = parseStructuredContent(data.content)

      // 如果后端没有返回药品，但用户询问药品相关，则前端智能推荐
      let recommendedDrugs = data.drugs
      if ((!recommendedDrugs || recommendedDrugs.length === 0) && shouldShowDrugRecommendation(text)) {
        recommendedDrugs = getRecommendedDrugs(text)
      }

      messages.value.push({
        type: 'ai',
        content: data.content,
        structured: structured.hasMore ? structured : undefined,
        expanded: false,
        drugs: recommendedDrugs,
        showActions: data.showActions || (recommendedDrugs && recommendedDrugs.length > 0)
      })

      saveHistory()
    }
  } catch (error: any) {
    console.error('AI对话失败:', error)

    const recommendedDrugs = shouldShowDrugRecommendation(text) ? getRecommendedDrugs(text) : []

    messages.value.push({
      type: 'ai',
      content: '抱歉，AI助手暂时无法响应，请稍后再试。',
      drugs: recommendedDrugs,
      showActions: recommendedDrugs.length > 0
    })

    ElMessage.error('AI助手响应失败，请稍后再试')
  } finally {
    isSending.value = false
    nextTick(() => scrollToBottom())
  }
}

// 快捷查询
const quickQuery = (text: string) => {
  inputMessage.value = text
  sendMessage()
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// 语音输入
const startVoice = () => {
  isRecording.value = true
  ElMessage.info('开始录音，请说话...')
}

const stopVoice = () => {
  if (isRecording.value) {
    isRecording.value = false
    ElMessage.success('录音结束')
    // 模拟语音识别结果
    setTimeout(() => {
      inputMessage.value = '咳嗽3天了，我可以吃什么药？'
    }, 500)
  }
}

// 显示微信提示
const showWechatTip = () => {
  ElMessageBox.alert(
    '请添加微信号：DrugMall_Health\n或扫描二维码添加小团健康管家',
    '添加微信好友',
    { confirmButtonText: '知道了' }
  )
}

// 清空对话
const handleClearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有对话历史吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    messages.value = []
    sessionId.value = ''
    historyList.value = []
    localStorage.removeItem('ai_session_id')
    localStorage.removeItem('ai_history')

    ElMessage.success('对话历史已清空')
    showHistory.value = false
  } catch (error) {
    // 用户取消
  }
}

// 保存历史
const saveHistory = () => {
  if (messages.value.length === 0) return

  const summary = messages.value[0].content.substring(0, 20) + '...'
  const time = new Date().toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })

  const existingIndex = historyList.value.findIndex(h => h.id === sessionId.value)

  if (existingIndex >= 0) {
    historyList.value[existingIndex] = {
      id: sessionId.value,
      summary,
      time,
      messages: [...messages.value]
    }
  } else {
    historyList.value.unshift({
      id: sessionId.value || Date.now().toString(),
      summary,
      time,
      messages: [...messages.value]
    })
  }

  if (historyList.value.length > 20) {
    historyList.value = historyList.value.slice(0, 20)
  }

  localStorage.setItem('ai_history', JSON.stringify(historyList.value))
}

// 加载历史
const loadHistory = (item: any) => {
  messages.value = [...item.messages]
  sessionId.value = item.id
  showHistory.value = false
  nextTick(() => scrollToBottom())
}

// 删除历史
const deleteHistory = async (index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条对话记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    historyList.value.splice(index, 1)
    localStorage.setItem('ai_history', JSON.stringify(historyList.value))
    ElMessage.success('删除成功')
  } catch (error) {
    // 用户取消
  }
}

// 图片上传
const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件')
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过10MB')
    return
  }

  try {
    const uploadResponse = await aiAssistantApi.uploadFile(file, 'medical')

    if (!uploadResponse.data?.data?.id) {
      throw new Error('文件上传失败')
    }

    const fileId = uploadResponse.data.data.id
    const contentResponse = await aiAssistantApi.getFileContent(fileId)

    if (contentResponse.data?.data) {
      const fileData = contentResponse.data.data

      messages.value.push({
        type: 'user',
        content: `[图片] ${file.name}`
      })

      let aiContent = ''
      if (fileData.recognizeType === 'prescription') {
        aiContent = `已识别到处方信息：\n\n${fileData.content || '处方内容已识别'}\n\n如需购买处方药，请先上传处方或咨询医生。`
      } else if (fileData.recognizeType === 'drug') {
        aiContent = `已识别到药品信息：\n\n${fileData.content || '药品信息已识别'}\n\n您可以查看药品详情或加入购物车。`
      } else {
        aiContent = `图片内容已识别：\n\n${fileData.content || '图片内容已识别'}\n\n请问有什么可以帮助您的？`
      }

      messages.value.push({
        type: 'ai',
        content: aiContent,
        showActions: false
      })

      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请稍后再试')
  } finally {
    if (target) target.value = ''
  }
}

// 导航
const goBack = () => router.back()
const goToDrugDetail = (id: string) => router.push(`/drug/${id}`)
const goToInquiry = () => router.push('/inquiry')
const goToConsult = (doctor: any) => {
  router.push({
    path: `/inquiry/pre/${doctor.id}`,
    query: { name: doctor.name, department: doctor.department }
  })
}

// 购物车
const handleAddToCart = (drug: RecommendedDrug) => {
  ElMessage.success(`${drug.name} 已加入购物车`)
}

const addToCart = (drugs: RecommendedDrug[] | undefined) => {
  if (!drugs?.length) return
  drugs.forEach(drug => ElMessage.success(`${drug.name} 已加入购物车`))
  setTimeout(() => router.push('/cart'), 1500)
}

// 初始化
onMounted(() => {
  const savedSessionId = localStorage.getItem('ai_session_id')
  if (savedSessionId) sessionId.value = savedSessionId

  const savedHistory = localStorage.getItem('ai_history')
  if (savedHistory) {
    try {
      historyList.value = JSON.parse(savedHistory)
    } catch (error) {
      console.error('加载历史记录失败:', error)
    }
  }
})
</script>

<style scoped lang="scss">
// 颜色变量
$primary: #00C853;
$primary-light: #69F0AE;
$primary-dark: #00A344;
$health-blue: #E3F2FD;
$accent-yellow: #FFD54F;
$text-primary: #1A1A1A;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-gradient-start: #E8F5E9;
$bg-gradient-end: #E3F2FD;
$rx-badge: #FF5252;

// 间距
$space-xs: 4px;
$space-sm: 8px;
$space-md: 12px;
$space-lg: 16px;
$space-xl: 20px;
$space-xxl: 24px;

// 圆角
$radius-sm: 6px;
$radius-md: 12px;
$radius-lg: 16px;
$radius-xl: 20px;
$radius-full: 9999px;

.ai-assistant-page {
  min-height: 100vh;
  background: linear-gradient(180deg, $bg-gradient-start 0%, $bg-gradient-end 30%, #FFFFFF 100%);
  display: flex;
  flex-direction: column;
}

// 顶部导航
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-md $space-lg;
  padding-top: calc(env(safe-area-inset-top) + $space-md);
  background: transparent;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: $text-primary;
    font-size: 20px;
  }

  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
  }

  .header-actions {
    display: flex;
    gap: $space-sm;

    .action-btn {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: $text-primary;
      font-size: 20px;
    }
  }
}

// 首页区域
.home-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

// 吉祥物区域
.mascot-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $space-lg $space-lg $space-md;
  animation: fadeInUp 0.6s ease-out;

  .mascot-wrapper {
    position: relative;
    margin-bottom: $space-lg;

    .mascot-container {
      width: 120px;
      height: 120px;
      position: relative;
      animation: float 3s ease-in-out infinite;
    }

    // CSS绘制吉祥物
    .mascot-body {
      width: 90px;
      height: 90px;
      background: linear-gradient(135deg, $accent-yellow 0%, #FFB300 100%);
      border-radius: 50%;
      position: absolute;
      top: 15px;
      left: 15px;
      box-shadow: 0 6px 24px rgba(255, 213, 79, 0.4);

      .mascot-face {
        position: absolute;
        top: 26px;
        left: 50%;
        transform: translateX(-50%);

        .eyes {
          display: flex;
          gap: 22px;

          .eye {
            width: 10px;
            height: 10px;
            background: $text-primary;
            border-radius: 50%;
            position: relative;

            &::after {
              content: '';
              position: absolute;
              top: 2px;
              right: 2px;
              width: 4px;
              height: 4px;
              background: white;
              border-radius: 50%;
            }
          }
        }

        .cheeks {
          position: absolute;
          top: 14px;
          left: 50%;
          transform: translateX(-50%);
          display: flex;
          gap: 38px;

          .cheek {
            width: 9px;
            height: 6px;
            background: rgba(255, 107, 107, 0.3);
            border-radius: 50%;
          }
        }

        .mouth {
          position: absolute;
          top: 22px;
          left: 50%;
          transform: translateX(-50%);
          width: 18px;
          height: 9px;
          border-bottom: 2px solid $text-primary;
          border-radius: 0 0 18px 18px;
        }
      }

      // 听诊器
      .stethoscope {
        position: absolute;
        top: 38px;
        left: 50%;
        transform: translateX(-50%);
        width: 45px;
        height: 30px;
        border: 3px solid $primary;
        border-bottom: none;
        border-radius: 22px 22px 0 0;

        &::after {
          content: '';
          position: absolute;
          bottom: -6px;
          left: 50%;
          transform: translateX(-50%);
          width: 12px;
          height: 12px;
          background: $primary;
          border-radius: 50%;
          border: 2px solid white;
        }
      }

      // 白大褂
      .coat {
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60px;
        height: 34px;
        background: white;
        border-radius: 30px 30px 0 0;

        &::before {
          content: '';
          position: absolute;
          top: 8px;
          left: 50%;
          transform: translateX(-50%);
          width: 15px;
          height: 15px;
          background: $primary;
          border-radius: 3px;
        }
      }
    }

    .mascot-shadow {
      position: absolute;
      bottom: -4px;
      left: 50%;
      transform: translateX(-50%);
      width: 75px;
      height: 15px;
      background: rgba(0, 0, 0, 0.1);
      border-radius: 50%;
      animation: shadow 3s ease-in-out infinite;
    }
  }

  .main-title {
    font-size: 24px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: $space-sm;
  }

  .sub-title {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: $space-md;
  }

  .wechat-link {
    display: flex;
    align-items: center;
    gap: $space-xs;
    color: $primary;
    font-size: 14px;
    cursor: pointer;
    padding: $space-sm $space-md;
    background: rgba($primary, 0.1);
    border-radius: $radius-full;
    transition: all 0.3s;

    &:hover {
      background: rgba($primary, 0.2);
    }

    .link-highlight {
      text-decoration: underline;
    }
  }
}

// Tab区域
.tab-section {
  flex: 1;
  background: white;
  border-radius: 24px 24px 0 0;
  padding: $space-lg;
  margin-top: $space-md;

  .tab-bar {
    display: flex;
    background: #F5F5F5;
    border-radius: $radius-full;
    padding: 4px;
    margin-bottom: $space-lg;

    .tab-item {
      flex: 1;
      padding: 10px 0;
      text-align: center;
      font-size: 14px;
      color: $text-secondary;
      border-radius: $radius-full;
      transition: all 0.3s;
      cursor: pointer;

      &.active {
        background: $primary;
        color: white;
        font-weight: 500;
      }
    }
  }

  .tab-content {
    animation: fadeIn 0.3s ease-out;
  }
}

// 热门问题
.question-item {
  display: flex;
  align-items: center;
  gap: $space-md;
  padding: $space-md 0;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;
  transition: all 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    opacity: 0.7;
  }

  .question-num {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: #E8F5E9;
    color: $primary;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .question-text {
    flex: 1;
    font-size: 15px;
    color: $text-primary;
  }

  .arrow-icon {
    color: $text-tertiary;
    font-size: 16px;
  }
}

// 症状网格
.symptom-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-md;

  .symptom-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $space-sm;
    padding: $space-md;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      transform: scale(0.95);
    }

    .symptom-icon {
      width: 56px;
      height: 56px;
      border-radius: $radius-lg;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: $primary;
    }

    .symptom-name {
      font-size: 13px;
      color: $text-primary;
    }
  }
}

// 用药科普
.knowledge-item {
  display: flex;
  align-items: center;
  gap: $space-md;
  padding: $space-md 0;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }

  .knowledge-tag {
    padding: 4px 8px;
    background: #E8F5E9;
    color: $primary;
    font-size: 12px;
    border-radius: 4px;
    flex-shrink: 0;
  }

  .knowledge-title {
    flex: 1;
    font-size: 15px;
    color: $text-primary;
  }

  .arrow-icon {
    color: $text-tertiary;
  }
}

// 健康问答
.qa-item {
  display: flex;
  align-items: center;
  gap: $space-md;
  padding: $space-md 0;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }

  .qa-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #E3F2FD;
    color: #2196F3;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    flex-shrink: 0;
  }

  .qa-text {
    flex: 1;
    font-size: 15px;
    color: $text-primary;
  }

  .arrow-icon {
    color: $text-tertiary;
  }
}

// 免责声明条
.disclaimer-bar {
  background: white;
  padding: $space-md $space-lg;
  display: flex;
  align-items: flex-start;
  gap: $space-sm;
  font-size: 12px;
  color: $text-tertiary;
  border-top: 1px solid #F0F0F0;

  .el-icon {
    flex-shrink: 0;
    margin-top: 2px;
  }
}

// 对话区域
.chat-section {
  flex: 1;
  overflow-y: auto;
  padding: $space-md;

  .chat-list {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
  }
}

// 消息项
.message-item {
  display: flex;
  gap: $space-sm;

  &.user-message {
    flex-direction: row-reverse;
  }

  &.loading {
    .loading-bubble {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: $space-md $space-lg;
      background: white;
      border-radius: $radius-lg;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .dot {
        width: 8px;
        height: 8px;
        background: $primary;
        border-radius: 50%;
        animation: bounce 1.4s infinite ease-in-out both;

        &:nth-child(1) { animation-delay: -0.32s; }
        &:nth-child(2) { animation-delay: -0.16s; }
      }
    }
  }
}

// 头像
.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;

  &.ai-avatar {
    background: white;
    padding: 2px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  &.user-avatar {
    object-fit: cover;
  }
}

// 迷你吉祥物
.mini-mascot {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, $accent-yellow 0%, #FFB300 100%);
  border-radius: 50%;
  position: relative;

  .mini-face {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    .mini-eyes {
      display: flex;
      gap: 10px;
      margin-bottom: 6px;

      .mini-eye {
        width: 6px;
        height: 6px;
        background: $text-primary;
        border-radius: 50%;
      }
    }

    .mini-mouth {
      width: 10px;
      height: 5px;
      border-bottom: 2px solid $text-primary;
      border-radius: 0 0 10px 10px;
      margin: 0 auto;
    }
  }
}

// 消息内容
.message-content {
  max-width: 280px;
}

// 消息气泡
.message-bubble {
  padding: $space-md;
  border-radius: $radius-lg;
  font-size: 15px;
  line-height: 1.6;

  &.user {
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: white;
    border-radius: $radius-lg $radius-lg 4px $radius-lg;
  }

  &.ai {
    background: white;
    color: $text-primary;
    border-radius: 4px $radius-lg $radius-lg $radius-lg;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

// 结构化内容
.structured-content {
  .content-section {
    margin-bottom: $space-md;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      display: flex;
      align-items: center;
      gap: $space-xs;
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $space-sm;

      .title-icon {
        font-size: 16px;
      }
    }

    .section-body {
      font-size: 14px;
      color: $text-secondary;
      line-height: 1.7;

      :deep(.highlight-keyword) {
        color: $primary;
        font-weight: 500;
      }
    }
  }

  .expand-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $space-xs;
    padding-top: $space-sm;
    color: $text-tertiary;
    font-size: 13px;
    cursor: pointer;
    border-top: 1px dashed #E0E0E0;
    margin-top: $space-sm;

    .el-icon {
      transition: transform 0.3s;

      &.expanded {
        transform: rotate(180deg);
      }
    }
  }
}

// 药品推荐区域
.drug-recommend-section {
  margin-top: $space-md;
  background: white;
  border-radius: $radius-lg;
  padding: $space-md;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .section-header {
    display: flex;
    align-items: center;
    gap: $space-sm;
    margin-bottom: $space-md;

    .header-icon {
      width: 32px;
      height: 32px;
      border-radius: $radius-md;
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 16px;

      &.doctor {
        background: linear-gradient(135deg, #52C41A 0%, #73D13D 100%);
      }
    }

    .header-title {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
    }

    .header-subtitle {
      font-size: 12px;
      color: $text-tertiary;
      margin-left: auto;
      padding: 2px 8px;
      background: #F5F5F5;
      border-radius: $radius-full;
    }
  }
}

// 药品列表
.drug-list {
  display: flex;
  flex-direction: column;
  gap: $space-md;
}

// 药品卡片
.drug-card {
  display: flex;
  gap: $space-md;
  padding: $space-md;
  background: #FAFAFA;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all 0.3s;

  &:active {
    transform: scale(0.98);
  }

  .drug-image {
    position: relative;
    width: 88px;
    height: 88px;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      border-radius: $radius-sm;
      object-fit: cover;
    }

    .rx-badge {
      position: absolute;
      top: -4px;
      left: -4px;
      padding: 2px 6px;
      background: $rx-badge;
      color: white;
      font-size: 10px;
      border-radius: 4px;
    }
  }

  .drug-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .drug-name {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      line-height: 1.4;
    }

    .drug-indication {
      font-size: 13px;
      color: $text-tertiary;
    }

    .drug-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .drug-price {
        font-size: 18px;
        font-weight: 700;
        color: #FF5252;

        &::before {
          content: '¥';
          font-size: 12px;
        }
      }

      .buy-btn {
        padding: 8px 16px;
        background: $primary;
        color: white;
        border: none;
        border-radius: $radius-full;
        font-size: 13px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;

        &:active {
          transform: scale(0.95);
        }
      }
    }
  }
}

// 操作栏
.action-bar {
  display: flex;
  gap: $space-md;
  margin-top: $space-md;
  padding-top: $space-md;
  border-top: 1px dashed #E0E0E0;

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $space-xs;
    padding: 12px;
    border-radius: $radius-full;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    border: none;

    &.consult {
      background: #F5F5F5;
      color: $text-primary;
    }

    &.add-all {
      background: $primary;
      color: white;
    }

    &:active {
      transform: scale(0.98);
    }
  }
}

// 医生推荐
.doctor-recommend-section {
  margin-top: $space-md;
  background: white;
  border-radius: $radius-lg;
  padding: $space-md;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.doctor-list {
  display: flex;
  flex-direction: column;
  gap: $space-md;
}

.doctor-card-mini {
  display: flex;
  align-items: center;
  gap: $space-md;
  padding: $space-md;
  background: #F6FFED;
  border-radius: $radius-md;
  cursor: pointer;

  .doctor-avatar-mini {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
  }

  .doctor-mini-info {
    flex: 1;

    .doctor-mini-name {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;

      .title {
        font-size: 12px;
        color: $text-secondary;
        font-weight: normal;
      }
    }

    .doctor-mini-hospital {
      font-size: 13px;
      color: $text-secondary;
    }

    .doctor-mini-price {
      font-size: 14px;
      color: #FF5252;
      font-weight: 600;
    }
  }

  .consult-mini-btn {
    padding: 8px 16px;
    background: #52C41A;
    color: white;
    border: none;
    border-radius: $radius-full;
    font-size: 13px;
    cursor: pointer;

    &:active {
      transform: scale(0.95);
    }
  }
}

// 消息免责声明
.message-disclaimer {
  display: flex;
  align-items: flex-start;
  gap: $space-xs;
  margin-top: $space-sm;
  padding: $space-sm $space-md;
  background: #FFFBE6;
  border-radius: $radius-sm;
  font-size: 12px;
  color: #D48806;

  .el-icon {
    flex-shrink: 0;
    margin-top: 2px;
  }
}

// 底部区域
.bottom-section {
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

// 快捷操作栏
.quick-bar {
  display: flex;
  gap: $space-sm;
  padding: $space-sm $space-md;
  overflow-x: auto;

  &::-webkit-scrollbar {
    display: none;
  }

  .quick-tag {
    flex-shrink: 0;
    padding: 8px 16px;
    background: #F0F7FF;
    color: #2196F3;
    font-size: 13px;
    border-radius: $radius-full;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      background: #2196F3;
      color: white;
    }
  }
}

// 输入栏
.input-bar {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: $space-md;

  .icon-btn {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: #F5F5F5;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-secondary;
    font-size: 20px;
    cursor: pointer;
    transition: all 0.2s;

    &.voice-btn {
      &.recording {
        background: #FF5252;
        color: white;
        animation: pulse 1.5s infinite;
      }
    }

    &:active {
      transform: scale(0.95);
    }
  }

  .input-wrapper {
    flex: 1;

    input {
      width: 100%;
      height: 44px;
      padding: 0 $space-md;
      background: #F5F5F5;
      border: none;
      border-radius: $radius-full;
      font-size: 15px;
      color: $text-primary;
      outline: none;

      &::placeholder {
        color: $text-tertiary;
      }
    }
  }

  .send-btn {
    padding: 0 20px;
    height: 44px;
    background: $primary;
    color: white;
    border: none;
    border-radius: $radius-full;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      background: #E0E0E0;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      transform: scale(0.95);
    }

    .loading-icon {
      animation: spin 1s linear infinite;
    }
  }
}

// 医疗免责声明
.medical-disclaimer {
  padding: $space-sm $space-md;
  padding-bottom: calc(env(safe-area-inset-bottom) + $space-sm);
  text-align: center;
  font-size: 11px;
  color: $text-tertiary;
}

// 历史记录
.history-list {
  height: 100%;
  overflow-y: auto;

  .empty-history {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: $space-xxl;
    color: $text-tertiary;

    .empty-icon {
      font-size: 48px;
      margin-bottom: $space-md;
    }

    p {
      font-size: 14px;
    }
  }

  .history-item {
    display: flex;
    align-items: center;
    padding: $space-md;
    border-bottom: 1px solid #F0F0F0;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #F5F5F5;
    }

    .history-icon {
      font-size: 20px;
      color: $text-tertiary;
      margin-right: $space-sm;
    }

    .history-info {
      flex: 1;

      .history-summary {
        font-size: 14px;
        color: $text-primary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 4px;
      }

      .history-time {
        font-size: 12px;
        color: $text-tertiary;
      }
    }

    .delete-icon {
      font-size: 16px;
      color: $text-tertiary;
      cursor: pointer;

      &:hover {
        color: #FF5252;
      }
    }
  }
}

// 动画
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes shadow {
  0%, 100% { transform: translateX(-50%) scale(1); opacity: 0.1; }
  50% { transform: translateX(-50%) scale(0.8); opacity: 0.05; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(255, 82, 82, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(255, 82, 82, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255, 82, 82, 0); }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
