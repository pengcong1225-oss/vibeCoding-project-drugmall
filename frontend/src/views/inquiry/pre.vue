<template>
  <div class="expert-consultation-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">专家问诊</span>
      <div class="header-actions">
        <span class="record-link" @click="goToRecords">咨询记录</span>
      </div>
    </div>

    <!-- 流程指示器 -->
    <div class="process-indicator">
      <div class="process-step active">
        <div class="step-circle">1</div>
        <span class="step-text">导诊助手</span>
      </div>
      <div class="process-line"></div>
      <div class="process-step">
        <div class="step-circle">2</div>
        <span class="step-text">支付诊费</span>
      </div>
      <div class="process-line"></div>
      <div class="process-step">
        <div class="step-circle">3</div>
        <span class="step-text">医生接诊</span>
      </div>
      <div class="process-line"></div>
      <div class="process-step">
        <div class="step-circle">4</div>
        <span class="step-text">问诊咨询</span>
      </div>
    </div>

    <!-- 医生信息卡片 -->
    <div v-if="doctorInfo" class="doctor-card-section">
      <div class="doctor-info-card">
        <div class="doctor-basic">
          <div class="doctor-avatar">
            <img v-if="doctorInfo.avatar" :src="doctorInfo.avatar" alt="" />
            <span v-else class="avatar-text">{{ doctorInfo.name?.charAt(0) || '?' }}</span>
          </div>
          <div class="doctor-details">
            <div class="doctor-name-row">
              <span class="doctor-name">{{ doctorInfo.name }}</span>
              <span class="doctor-title">{{ doctorInfo.title }}</span>
              <span class="prescription-badge">可开方</span>
            </div>
            <div class="doctor-hospital">
              <span class="hospital-level">三甲</span>
              <span class="hospital-name">{{ doctorInfo.hospital }}</span>
              <span class="hospital-dept">| {{ doctorInfo.department }}</span>
            </div>
          </div>
        </div>
        <div class="doctor-specialty">
          <span class="specialty-label">擅长：</span>
          <span class="specialty-text">{{ doctorInfo.specialty || '常见病、多发病诊治' }}</span>
        </div>
        <div class="doctor-price-row">
          <span class="price-label">图文咨询</span>
          <span class="price-current">¥{{ doctorInfo.price || doctorInfo.consultationFee || '9.9' }}</span>
          <span class="price-time">平均接诊时长 {{ doctorInfo.avgResponseTime || '4' }}分钟</span>
        </div>
        <div class="doctor-guarantee">
          <span class="guarantee-label">权益保障</span>
          <span class="guarantee-text">24h未接诊自动退款</span>
          <span class="guarantee-text">接诊后24h不限次沟通</span>
        </div>
        <div class="service-provider">
          美团旗下互联网医院提供服务，继续咨询表明您已知悉并同意
          <span class="agreement-link">《知情同意书》</span>
        </div>
      </div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-section" ref="chatSectionRef">
      <!-- AI助手欢迎消息 -->
      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>您好，我是您的医生助手，将协助医生了解您的情况。</p>
        </div>
      </div>

      <!-- AI助手第二句 -->
      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>详细描述患者的病情，如：症状、患病时长、用药情况等。</p>
          <p class="example">示例：{{ doctorInfo?.example || '半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。' }}</p>
        </div>
      </div>

      <!-- 用户症状输入 -->
      <div v-if="symptomInput" class="message user-message">
        <div class="message-content">
          <p>{{ symptomInput }}</p>
        </div>
      </div>

      <!-- AI询问患者信息 -->
      <div v-if="symptomInput && !selectedPatient" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>了解到您的情况了，请问您是为以下哪位患者咨询呢？</p>
        </div>
      </div>

      <!-- 患者档案选择 -->
      <div v-if="symptomInput && !selectedPatient" class="patient-selection">
        <div class="patient-cards">
          <div
            v-for="profile in patientProfiles"
            :key="profile.id"
            :class="['patient-card', { active: tempSelectedPatient?.id === profile.id }]"
            @click="selectPatient(profile)"
          >
            <div class="radio-indicator">
              <div :class="['radio-circle', { checked: tempSelectedPatient?.id === profile.id }]">
                <div v-if="tempSelectedPatient?.id === profile.id" class="radio-inner"></div>
              </div>
            </div>
            <div class="patient-info">
              <span class="patient-name">{{ profile.name }}</span>
              <span class="patient-detail">{{ profile.gender === 'male' ? '男' : '女' }} {{ profile.age }}岁</span>
            </div>
          </div>
          <div class="patient-card other" @click="goToAddPatient">
            <el-icon><Plus /></el-icon>
            <span>其他</span>
          </div>
        </div>
        <div class="manage-link" @click="goToManagePatients">
          <span>管理档案</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
        <!-- 确认选择按钮 -->
        <div v-if="tempSelectedPatient" class="confirm-selection">
          <button class="confirm-btn" @click="confirmPatientSelection">确认选择</button>
        </div>
      </div>

      <!-- 用户选择患者后 -->
      <div v-if="selectedPatient" class="message user-message">
        <div class="message-content">
          <p>{{ selectedPatient.name }} {{ selectedPatient.gender === 'male' ? '男' : '女' }} {{ selectedPatient.age }}岁</p>
        </div>
      </div>

      <!-- AI询问患病时长 -->
      <div v-if="conversationStep === 2" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>请问这些症状持续多长时间了？</p>
          <p class="example">示例：1周、1个月、半年等</p>
        </div>
      </div>

      <!-- 用户输入的患病时长 -->
      <div v-if="patientMedicalInfo.duration" class="message user-message">
        <div class="message-content">
          <p>{{ patientMedicalInfo.duration }}</p>
        </div>
      </div>

      <!-- AI询问过敏史 -->
      <div v-if="conversationStep === 3" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>请问患者是否有药物过敏史或食物过敏史？</p>
          <p class="example">示例：无过敏史、青霉素过敏、海鲜过敏等</p>
        </div>
      </div>

      <!-- 用户输入的过敏史 -->
      <div v-if="patientMedicalInfo.allergies" class="message user-message">
        <div class="message-content">
          <p>{{ patientMedicalInfo.allergies }}</p>
        </div>
      </div>

      <!-- AI询问用药情况 -->
      <div v-if="conversationStep === 4" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>请问患者目前是否正在使用其他药物？或近期是否用过药？</p>
          <p class="example">示例：未用药、正在服用降压药、用过布洛芬等</p>
        </div>
      </div>

      <!-- 用户输入的用药情况 -->
      <div v-if="patientMedicalInfo.medication" class="message user-message">
        <div class="message-content">
          <p>{{ patientMedicalInfo.medication }}</p>
        </div>
      </div>

      <!-- AI确认消息 -->
      <div v-if="conversationStep === 5" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>好的，我已了解您的情况。正在为您安排医生接诊...</p>
        </div>
      </div>

      <!-- 医生分配结果 -->
      <div v-if="conversationStep >= 5 && doctorInfo" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>已为您安排 <strong>{{ doctorInfo.name }}</strong> {{ doctorInfo.title }} 接诊</p>
          <p class="doctor-assign-info">
            {{ doctorInfo.hospital }} | {{ doctorInfo.department }}<br/>
            擅长：{{ doctorInfo.specialty }}
          </p>
        </div>
      </div>

      <!-- 支付卡片 -->
      <div v-if="conversationStep >= 5 && doctorInfo && consultationId" class="payment-card">
        <div class="payment-header">
          <div class="patient-info-row">
            <span class="patient-name">{{ doctorInfo.name }}</span>
          </div>
          <div class="service-type">图文咨询</div>
          <div class="service-price">¥{{ doctorInfo?.price || doctorInfo?.consultationFee || '9.9' }}</div>
        </div>
        <div class="payment-desc">
          <p>打字、发图，24小时不限次沟通</p>
          <p>24小时未接诊自动退款</p>
        </div>
        <button class="pay-btn" @click="goToPayment">去支付</button>
      </div>
    </div>

    <!-- 症状快捷标签 - 只在输入症状阶段显示 -->
    <div v-if="!symptomInput && conversationStep === 0" class="quick-symptoms">
      <div class="quick-symptoms-header">
        <span class="header-text">点击症状词可快捷输入：</span>
        <span class="refresh-btn" @click="refreshSymptoms">
          <el-icon><Refresh /></el-icon>
          换一换
        </span>
      </div>
      <div class="symptom-tags">
        <span
          v-for="(symptom, index) in currentSymptoms"
          :key="index"
          :class="['symptom-tag', { active: selectedSymptom === symptom }]"
          @click="selectSymptom(symptom)"
        >
          {{ symptom }}
        </span>
      </div>
    </div>

    <!-- 底部输入栏 - 多轮对话期间显示 -->
    <div v-if="!selectedPatient || (selectedPatient && conversationStep >= 2 && conversationStep <= 4)" class="input-section">
      <div class="input-wrapper">
        <input
          v-model="inputMessage"
          type="text"
          :placeholder="getInputPlaceholder()"
          @keyup.enter="sendMessage"
        />
        <button
          class="send-btn"
          :disabled="!inputMessage.trim()"
          @click="sendMessage"
        >
          发送
        </button>
      </div>
    </div>

    <!-- 完成问诊按钮 -->
    <div v-if="conversationStep === 5 && !consultationId" class="input-section">
      <div class="input-wrapper">
        <button
          class="send-btn complete-btn"
          @click="completeConsultation"
        >
          确认并创建问诊
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Service,
  CircleCheck,
  Clock,
  Document,
  ArrowRight,
  Plus,
  Refresh,
  Present
} from '@element-plus/icons-vue'
import { ROUTES, getInquiryPayRoute } from '@/constants/routes'
import { createConsultation } from '@/api/modules/inquiry'
import { getPatients } from '@/api/modules/user'
import type { Patient } from '@/types'

const route = useRoute()
const router = useRouter()

// 医生信息
interface DoctorInfo {
  id: string
  name: string
  title: string
  hospital: string
  department: string
  specialty: string
  avatar: string
  price?: string
  consultationFee?: string
  avgResponseTime?: string
  example?: string
  quickSymptoms?: string[]
}

const doctorInfo = ref<DoctorInfo | null>(null)
const assistantAvatarLoaded = ref(false)

// 页面状态
const currentSymptoms = ref<string[]>([
  '咳嗽', '咳痰', '气喘', '胸闷', '发热', '感冒', '咽痛', '流鼻涕', '打喷嚏'
])
const selectedSymptom = ref('')
const symptomInput = ref('')
const inputMessage = ref('')
const consultationId = ref('')
const chatSectionRef = ref<HTMLElement>()

// 多轮问诊状态
const conversationStep = ref(0) // 0: 初始 1: 症状已输入 2: 已选择患者 3: 时长已输入 4: 过敏史已输入 5: 用药史已输入 6: 完成
const patientMedicalInfo = ref({
  duration: '',
  allergies: '',
  medication: ''
})

// 患者相关
const patientProfiles = ref<Patient[]>([])
const tempSelectedPatient = ref<Patient | null>(null)
const selectedPatient = ref<Patient | null>(null)

// 常见症状词库
const symptomPool = [
  ['咳嗽', '咳痰', '气喘', '胸闷', '发热', '感冒', '咽痛', '流鼻涕', '打喷嚏'],
  ['头痛', '头晕', '失眠', '乏力', '恶心', '呕吐', '腹泻', '腹痛', '腰痛'],
  ['皮肤瘙痒', '皮疹', '红肿', '疼痛', '麻木', '酸痛', '刺痛', '胀痛', '隐痛'],
  ['食欲差', '便秘', '反酸', '打嗝', '腹胀', '口干', '口苦', '口臭', '咽干']
]
let symptomPoolIndex = 0

// 获取输入框占位符
const getInputPlaceholder = () => {
  switch (conversationStep.value) {
    case 0:
      return '请详细描述您的病情'
    case 2:
      return '请输入患病时长，如：1周、1个月'
    case 3:
      return '请输入过敏史，如：无、青霉素过敏'
    case 4:
      return '请输入用药情况，如：未用药、正在服用XX药'
    default:
      return '请输入...'
  }
}

// 加载医生信息
const loadDoctorInfo = () => {
  const doctorId = route.params.doctorId as string
  const queryDoctorName = route.query.doctorName as string
  const queryDoctorTitle = route.query.doctorTitle as string
  const queryHospital = route.query.hospital as string
  const queryDepartment = route.query.department as string
  const querySpecialty = route.query.specialty as string
  const queryAvatar = route.query.avatar as string
  const queryPrice = route.query.price as string

  if (doctorId || queryDoctorName) {
    doctorInfo.value = {
      id: doctorId || '1',
      name: queryDoctorName || '邓健楠',
      title: queryDoctorTitle || '主治医师',
      hospital: queryHospital || '首都医科大学附属北京朝阳医院',
      department: queryDepartment || '呼吸内科',
      specialty: querySpecialty || '肺炎、哮喘、肺结核、支气管肺癌、呼吸衰竭、肺部阴影、上呼吸道感染',
      avatar: queryAvatar || '',
      price: queryPrice || '39.9',
      avgResponseTime: '4',
      example: '半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。',
      quickSymptoms: ['咳嗽', '咳痰', '气喘', '胸闷', '发热', '感冒', '咽痛', '流鼻涕', '打喷嚏']
    }
    if (doctorInfo.value.quickSymptoms) {
      currentSymptoms.value = doctorInfo.value.quickSymptoms
    }
  }
}

// 加载患者档案
const loadPatientProfiles = async () => {
  try {
    const res = await getPatients()
    if (Array.isArray(res) && res.length > 0) {
      patientProfiles.value = res
    } else {
      patientProfiles.value = [
        { id: '1', name: '彭聪', gender: 'male', age: 41, phone: '13812341234', birthday: '1985-02-22', relationship: '本人', isDefault: true, idCard: '' },
        { id: '2', name: '李小红', gender: 'female', age: 35, phone: '13956785678', birthday: '1990-06-15', relationship: '配偶', isDefault: false, idCard: '' }
      ]
    }
  } catch (error) {
    console.error('获取患者档案失败:', error)
    patientProfiles.value = [
      { id: '1', name: '彭聪', gender: 'male', age: 41, phone: '13812341234', birthday: '1985-02-22', relationship: '本人', isDefault: true, idCard: '' },
      { id: '2', name: '李小红', gender: 'female', age: 35, phone: '13956785678', birthday: '1990-06-15', relationship: '配偶', isDefault: false, idCard: '' }
    ]
  }
}

// 选择症状标签
const selectSymptom = (symptom: string) => {
  selectedSymptom.value = symptom
  inputMessage.value = symptom
}

// 刷新症状标签
const refreshSymptoms = () => {
  symptomPoolIndex = (symptomPoolIndex + 1) % symptomPool.length
  currentSymptoms.value = symptomPool[symptomPoolIndex]
  selectedSymptom.value = ''
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  const message = inputMessage.value.trim()
  inputMessage.value = ''

  switch (conversationStep.value) {
    case 0:
      // 输入症状
      if (!symptomInput.value) {
        symptomInput.value = message
        conversationStep.value = 1
      }
      break
    case 2:
      // 输入患病时长
      patientMedicalInfo.value.duration = message
      conversationStep.value = 3
      break
    case 3:
      // 输入过敏史
      patientMedicalInfo.value.allergies = message
      conversationStep.value = 4
      break
    case 4:
      // 输入用药情况
      patientMedicalInfo.value.medication = message
      conversationStep.value = 5
      break
  }

  nextTick(() => {
    scrollToBottom()
  })
}

// 选择患者
const selectPatient = (profile: Patient) => {
  tempSelectedPatient.value = profile
}

// 确认患者选择
const confirmPatientSelection = async () => {
  if (!tempSelectedPatient.value) return

  selectedPatient.value = tempSelectedPatient.value
  conversationStep.value = 2 // 进入多轮问诊流程

  nextTick(() => {
    scrollToBottom()
  })
}

// 完成多轮问诊并创建问诊记录
const completeConsultation = async () => {
  if (!selectedPatient.value) return

  try {
    const res = await createConsultation({
      doctorId: doctorInfo.value?.id || '0',
      symptom: symptomInput.value,
      patientId: selectedPatient.value.id,
      type: 'text',
      duration: patientMedicalInfo.value.duration,
      allergies: patientMedicalInfo.value.allergies,
      medication: patientMedicalInfo.value.medication
    })

    if (res && res.id) {
      consultationId.value = res.id
    } else {
      consultationId.value = 'C' + Date.now()
    }
    conversationStep.value = 6 // 完成

    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('创建问诊失败:', error)
    consultationId.value = 'C' + Date.now()
    conversationStep.value = 6
    nextTick(() => {
      scrollToBottom()
    })
  }
}

const goToPayment = () => {
  if (!consultationId.value) {
    ElMessage.error('问诊信息不存在')
    return
  }

  router.push({
    path: getInquiryPayRoute(consultationId.value),
    query: {
      doctorId: doctorInfo.value?.id,
      doctorName: doctorInfo.value?.name,
      doctorTitle: doctorInfo.value?.title,
      hospital: doctorInfo.value?.hospital,
      department: doctorInfo.value?.department,
      patientId: selectedPatient.value?.id,
      patientName: selectedPatient.value?.name,
      patientGender: selectedPatient.value?.gender,
      patientAge: selectedPatient.value?.age?.toString(),
      symptom: symptomInput.value,
      price: doctorInfo.value?.price || doctorInfo.value?.consultationFee
    }
  })
}

const scrollToBottom = () => {
  if (chatSectionRef.value) {
    chatSectionRef.value.scrollTop = chatSectionRef.value.scrollHeight
  }
}

const goBack = () => {
  router.back()
}

const goToRecords = () => {
  router.push(ROUTES.INQUIRY_LIST)
}

const goToAddPatient = () => {
  router.push(ROUTES.PATIENT)
}

const goToManagePatients = () => {
  router.push(ROUTES.PATIENT)
}

onMounted(() => {
  loadDoctorInfo()
  loadPatientProfiles()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/inquiry-theme' as *;

$primary-green: #00C9A7;
$primary-green-light: #00B894;
$user-message-bg: #00C9A7;
$user-message-text: #FFFFFF;
$pay-yellow: #F59E0B;
$price-red: #EF4444;
$subsidy-orange: #FF6B35;

.expert-consultation-page {
  min-height: 100vh;
  background: $inquiry-bg;
  display: flex;
  flex-direction: column;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  color: white;
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

    &:active {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: 17px;
    font-weight: 600;
    flex: 1;
    text-align: center;
  }

  .header-actions {
    width: 60px;
    display: flex;
    justify-content: flex-end;

    .record-link {
      font-size: 14px;
      cursor: pointer;

      &:active {
        opacity: 0.8;
      }
    }
  }
}

// 流程指示器
.process-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: white;
  border-bottom: 1px solid $border-light;
  gap: 0;

  .process-step {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;

    .step-circle {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: #e0e0e0;
      color: #999;
      font-size: 13px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .step-text {
      font-size: 11px;
      color: $text-tertiary;
    }

    &.active {
      .step-circle {
        background: $inquiry-primary;
        color: white;
      }

      .step-text {
        color: $inquiry-primary;
        font-weight: 500;
      }
    }
  }

  .process-line {
    width: 30px;
    height: 2px;
    background: #e0e0e0;
    margin: 0 4px;
    margin-bottom: 20px;
  }
}

// 医生信息卡片
.doctor-card-section {
  background: white;
  padding: 16px;
  margin-bottom: 12px;

  .doctor-info-card {
    background: $inquiry-bg-light;
    border-radius: 12px;
    padding: 16px;

    .doctor-basic {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;

      .doctor-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .avatar-text {
          font-size: 24px;
          font-weight: 600;
          color: white;
        }
      }

      .doctor-details {
        flex: 1;

        .doctor-name-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 6px;

          .doctor-name {
            font-size: 17px;
            font-weight: 600;
            color: $inquiry-text-primary;
          }

          .doctor-title {
            font-size: 12px;
            color: $inquiry-text-secondary;
          }

          .prescription-badge {
            padding: 2px 6px;
            background: rgba($inquiry-primary, 0.1);
            color: $inquiry-primary;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 500;
          }
        }

        .doctor-hospital {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          color: $inquiry-text-secondary;

          .hospital-level {
            padding: 1px 6px;
            background: rgba($warning, 0.12);
            color: $warning;
            border-radius: 3px;
            font-size: 10px;
            font-weight: 600;
          }
        }
      }
    }

    .doctor-specialty {
      font-size: 13px;
      color: $inquiry-text-secondary;
      line-height: 1.5;
      margin-bottom: 12px;

      .specialty-label {
        color: $inquiry-text-tertiary;
      }
    }

    .doctor-price-row {
      display: flex;
      align-items: baseline;
      gap: 8px;
      margin-bottom: 12px;

      .price-label {
        font-size: 13px;
        color: $inquiry-text-secondary;
      }

      .price-current {
        font-size: 22px;
        font-weight: 700;
        color: $price-red;
      }

      .price-time {
        font-size: 12px;
        color: $inquiry-text-tertiary;
      }
    }

    .doctor-guarantee {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: rgba($inquiry-primary, 0.05);
      border-radius: 8px;
      margin-bottom: 12px;

      .guarantee-label {
        font-size: 12px;
        color: $inquiry-primary;
        font-weight: 500;
      }

      .guarantee-text {
        font-size: 11px;
        color: $inquiry-text-secondary;
      }
    }

    .service-provider {
      font-size: 11px;
      color: $inquiry-text-tertiary;
      text-align: center;
      line-height: 1.5;

      .agreement-link {
        color: $inquiry-primary;
        text-decoration: underline;
      }
    }
  }
}

// 聊天区域
.chat-section {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: $inquiry-bg;

  .message {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;

    &.assistant-message {
      align-items: flex-start;

      .assistant-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        overflow: hidden;
        font-size: 10px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .message-content {
        background: white;
        border-radius: 12px;
        padding: 12px 16px;
        max-width: 80%;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

        p {
          font-size: 14px;
          color: $inquiry-text-primary;
          line-height: 1.6;
          margin: 0;

          &.example {
            color: $inquiry-text-tertiary;
            font-size: 13px;
            margin-top: 8px;
          }
        }

        .doctor-assign-info {
          margin-top: 8px;
          padding-top: 8px;
          border-top: 1px dashed $border-light;
          font-size: 12px;
          color: $inquiry-text-secondary;
          line-height: 1.6;
        }

        strong {
          color: $inquiry-primary;
          font-weight: 600;
        }
      }
    }

    &.user-message {
      justify-content: flex-end;

      .message-content {
        background: $user-message-bg;
        border-radius: 12px;
        padding: 12px 16px;
        max-width: 70%;

        p {
          font-size: 14px;
          color: $user-message-text;
          line-height: 1.6;
          margin: 0;
        }
      }
    }
  }

  // 患者选择
  .patient-selection {
    margin: 16px 0;

    .patient-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 12px;

      .patient-card {
        display: flex;
        align-items: center;
        gap: 12px;
        background: white;
        border-radius: 12px;
        padding: 14px 16px;
        cursor: pointer;
        border: 2px solid transparent;
        transition: all 0.2s;

        &.active {
          border-color: $inquiry-primary;
          background: rgba($inquiry-primary, 0.05);
        }

        &.other {
          justify-content: center;
          color: $inquiry-primary;
          border: 2px dashed rgba($inquiry-primary, 0.3);

          .el-icon {
            font-size: 20px;
          }

          span {
            font-size: 14px;
          }
        }

        .radio-indicator {
          flex-shrink: 0;

          .radio-circle {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            border: 2px solid #d0d0d0;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s;

            &.checked {
              border-color: $inquiry-primary;
              background: $inquiry-primary;

              .radio-inner {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: white;
              }
            }
          }
        }

        .patient-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 4px;

          .patient-name {
            font-size: 15px;
            font-weight: 500;
            color: $inquiry-text-primary;
          }

          .patient-detail {
            font-size: 12px;
            color: $inquiry-text-secondary;
          }
        }
      }
    }

    .manage-link {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      padding: 12px;
      color: $inquiry-primary;
      font-size: 13px;
      cursor: pointer;

      .el-icon {
        font-size: 14px;
      }
    }

    .confirm-selection {
      margin-top: 16px;

      .confirm-btn {
        width: 100%;
        padding: 14px;
        background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
        color: white;
        border: none;
        border-radius: 24px;
        font-size: 16px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;

        &:active {
          opacity: 0.9;
          transform: scale(0.98);
        }
      }
    }
  }

  // 支付卡片
  .payment-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    margin-top: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .payment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px dashed $border-light;

      .patient-name {
        font-size: 15px;
        font-weight: 500;
        color: $inquiry-text-primary;
      }

      .service-type {
        font-size: 13px;
        color: $inquiry-text-secondary;
      }

      .service-price {
        font-size: 18px;
        font-weight: 600;
        color: $price-red;
      }
    }

    .payment-desc {
      margin-bottom: 16px;

      p {
        font-size: 12px;
        color: $inquiry-text-tertiary;
        line-height: 1.6;
        margin: 0;
      }
    }

    .pay-btn {
      width: 100%;
      padding: 14px;
      background: linear-gradient(135deg, $pay-yellow 0%, #F97316 100%);
      color: white;
      border: none;
      border-radius: 24px;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;

      &:active {
        opacity: 0.9;
        transform: scale(0.98);
      }
    }
  }
}

// 症状快捷标签
.quick-symptoms {
  background: white;
  padding: 16px;
  border-top: 1px solid $border-light;

  .quick-symptoms-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    .header-text {
      font-size: 13px;
      color: $inquiry-text-tertiary;
    }

    .refresh-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: $inquiry-primary;
      cursor: pointer;

      .el-icon {
        font-size: 14px;
      }

      &:active {
        opacity: 0.8;
      }
    }
  }

  .symptom-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .symptom-tag {
      padding: 8px 16px;
      background: $inquiry-bg-light;
      border-radius: 20px;
      font-size: 13px;
      color: $inquiry-text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      &.active {
        background: rgba($inquiry-primary, 0.1);
        color: $inquiry-primary;
        border: 1px solid $inquiry-primary;
      }

      &:active {
        transform: scale(0.95);
      }
    }
  }
}

// 底部输入栏
.input-section {
  background: white;
  padding: 12px 16px;
  padding-bottom: calc($safe-area-bottom + 12px);
  border-top: 1px solid $border-light;

  .input-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;

    input {
      flex: 1;
      height: 40px;
      padding: 0 16px;
      background: $inquiry-bg-light;
      border: 1px solid $border-light;
      border-radius: 20px;
      font-size: 14px;
      color: $inquiry-text-primary;
      outline: none;
      transition: all 0.2s;

      &::placeholder {
        color: $inquiry-text-tertiary;
      }

      &:focus {
        border-color: $inquiry-primary;
        background: white;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }

    .send-btn {
      padding: 10px 24px;
      background: $inquiry-primary;
      color: white;
      border: none;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;

      &:disabled {
        background: #d0d0d0;
        cursor: not-allowed;
      }

      &:active:not(:disabled) {
        opacity: 0.9;
        transform: scale(0.95);
      }

      &.complete-btn {
        flex: 1;
        background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
        font-size: 15px;
        padding: 12px 24px;
      }
    }
  }
}
</style>
