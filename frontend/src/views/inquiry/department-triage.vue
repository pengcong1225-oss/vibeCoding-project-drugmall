<template>
  <div class="department-triage-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">{{ departmentName }} - 导诊台</span>
      <div class="header-actions">
        <span class="record-link" @click="goToRecords">咨询记录</span>
      </div>
    </div>

    <!-- 流程指示器 -->
    <div class="process-indicator">
      <template v-for="(step, index) in consultationSteps" :key="step.step">
        <div
          class="process-step"
          :class="{ active: conversationStep < 5 || (index === 1 && conversationStep >= 5) }"
        >
          <div class="step-circle">{{ step.step }}</div>
          <span class="step-text">{{ step.name }}</span>
        </div>
        <div v-if="index < consultationSteps.length - 1" class="process-line"></div>
      </template>
    </div>

    <!-- 科室信息卡片 -->
    <div v-if="conversationStep < 1" class="department-card-section">
      <div class="department-info-card">
        <div class="dept-header">
          <div class="dept-basic">
            <span class="dept-name">{{ departmentName }}</span>
            <span class="dept-level">三甲</span>
            <span class="dept-price">
              <span class="price-current">¥{{ departmentInfo.price }}</span>
              <span class="price-original">¥{{ departmentInfo.originalPrice }}</span>
            </span>
            <span v-if="departmentInfo.subsidy" class="subsidy-tag">
              <el-icon><Present /></el-icon>
              已补贴{{ departmentInfo.subsidy }}元
            </span>
          </div>
        </div>
        <div class="dept-symptoms">
          <span class="symptoms-text">{{ departmentInfo.symptoms }}</span>
        </div>
        <div class="dept-tags">
          <div class="dept-tag">
            <el-icon><CircleCheck /></el-icon>
            <span>公立医院资深医生，卫健委审核实名认证</span>
          </div>
          <div class="dept-tag">
            <el-icon><Clock /></el-icon>
            <span>平均{{ departmentInfo.responseTime }}秒快速响应，{{ departmentInfo.answerTime }}分钟即问即答</span>
          </div>
          <div class="dept-tag">
            <el-icon><Document /></el-icon>
            <span>问诊-开方-购药，一站式解决健康问题</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-section" ref="chatSectionRef">
      <!-- 欢迎消息 -->
      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>详细描述患者的病情，如：症状、患病时长、用药情况等。</p>
          <p class="example">示例：{{ departmentInfo.example }}</p>
        </div>
      </div>

      <!-- 用户输入的症状 -->
      <div v-if="patientInfo.symptom" class="message user-message">
        <div class="message-content">
          <p>{{ patientInfo.symptom }}</p>
        </div>
      </div>

      <!-- AI询问就诊人 -->
      <div v-if="patientInfo.symptom && conversationStep === 1" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>了解到您的情况了，请问您是为以下哪位患者咨询呢？</p>
        </div>
      </div>

      <!-- 患者档案选择 -->
      <div v-if="patientInfo.symptom && conversationStep === 1 && !selectedPatient" class="patient-selection">
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
            <span v-if="profile.isDefault" class="default-tag">默认就诊人</span>
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
        <div v-if="tempSelectedPatient" class="confirm-selection">
          <button class="confirm-btn" @click="confirmPatientSelection">确认选择</button>
        </div>
      </div>

      <!-- 用户选择的就诊人 -->
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
      <div v-if="patientInfo.duration" class="message user-message">
        <div class="message-content">
          <p>{{ patientInfo.duration }}</p>
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
      <div v-if="patientInfo.allergies" class="message user-message">
        <div class="message-content">
          <p>{{ patientInfo.allergies }}</p>
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
      <div v-if="patientInfo.medication" class="message user-message">
        <div class="message-content">
          <p>{{ patientInfo.medication }}</p>
        </div>
      </div>

      <!-- AI确认并分配医生 -->
      <div v-if="conversationStep === 5" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>好的，我已了解您的情况。正在为您安排最合适的医生接诊...</p>
        </div>
      </div>

      <!-- 医生分配结果 -->
      <div v-if="conversationStep >= 6 && assignedDoctor" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>已为您安排 <strong>{{ assignedDoctor.name }}</strong> {{ assignedDoctor.title }} 接诊</p>
          <p class="doctor-assign-info">
            {{ assignedDoctor.hospital }} | {{ assignedDoctor.department }}<br/>
            擅长：{{ assignedDoctor.specialty }}
          </p>
        </div>
      </div>

      <!-- 支付卡片 -->
      <div v-if="conversationStep >= 6 && assignedDoctor && consultationId" class="payment-card">
        <div class="payment-header">
          <div class="patient-info-row">
            <span class="patient-name">{{ assignedDoctor.name }}</span>
          </div>
          <div class="service-type">图文咨询</div>
          <div class="service-price">¥{{ assignedDoctor.price }}</div>
        </div>
        <div class="payment-desc">
          <p>打字、发图，24小时不限次沟通</p>
          <p>24小时未接诊自动退款</p>
        </div>
        <button class="pay-btn" @click="goToPayment">去支付</button>
      </div>
    </div>

    <!-- 症状快捷标签 -->
    <div v-if="conversationStep === 0" class="quick-symptoms">
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

    <!-- 底部输入栏 -->
    <div v-if="conversationStep < 5" class="input-section">
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
import { ROUTES } from '@/constants/routes'
import { createConsultation } from '@/api/modules/inquiry'
import { getPatients } from '@/api/modules/user'
import { businessApi } from '@/api/modules/business'
import type { Patient } from '@/types'

const route = useRoute()
const router = useRouter()

// 页面状态
const departmentCode = ref('')
const departmentName = ref('')
const departmentInfo = ref<any>({})
const currentSymptoms = ref<string[]>([])
const selectedSymptom = ref('')

const inputMessage = ref('')
const consultationId = ref('')
const chatSectionRef = ref<HTMLElement>()

// 多轮问诊状态
const conversationStep = ref(0) // 0: 初始 1: 症状已输入 2: 时长已输入 3: 过敏史已输入 4: 用药史已输入 5: 完成 6: 已分配医生
const patientInfo = ref({
  symptom: '',
  duration: '',
  allergies: '',
  medication: ''
})

// 患者相关
const patientProfiles = ref<Patient[]>([])
const tempSelectedPatient = ref<Patient | null>(null)
const selectedPatient = ref<Patient | null>(null)

// 分配的医生
const assignedDoctor = ref<any>(null)

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

// 脱敏处理
const maskPhone = (phone: string) => {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}

const consultationSteps = ref<{ step: number; name: string }[]>([])

// 加载科室信息
const loadDepartmentInfo = async () => {
  departmentCode.value = (route.params.departmentCode as string) || (route.query.department as string) || 'general'
  
  try {
    const configRes = await businessApi.getDepartmentConfig(departmentCode.value)
    if (configRes.data) {
      departmentName.value = configRes.data.departmentCode === 'bone' ? '骨科' : 
        configRes.data.departmentCode === 'neurology' ? '神经内科' : 
        configRes.data.departmentCode === 'general' ? '全科' : 
        configRes.data.departmentCode === 'dermatology' ? '皮肤科' : 
        configRes.data.departmentCode === 'respiratory' ? '呼吸内科' : 
        configRes.data.departmentCode === 'pediatrics' ? '儿科' : 
        configRes.data.departmentCode === 'gynecology' ? '妇产科' : 
        configRes.data.departmentCode === 'gastroenterology' ? '消化内科' : 
        configRes.data.departmentCode === 'psychology' ? '心理咨询' : 
        configRes.data.departmentCode === 'tcm' ? '中医科' : '全科'
      departmentInfo.value = configRes.data
      currentSymptoms.value = (configRes.data.quickSymptoms || []).slice(0, 9)
    }
  } catch (error) {
    console.error('加载科室配置失败:', error)
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
  const allSymptoms = departmentInfo.value.quickSymptoms || []
  const shuffled = [...allSymptoms].sort(() => Math.random() - 0.5)
  currentSymptoms.value = shuffled.slice(0, 9)
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
      patientInfo.value.symptom = message
      conversationStep.value = 1
      break
    case 2:
      // 输入患病时长
      patientInfo.value.duration = message
      conversationStep.value = 3
      break
    case 3:
      // 输入过敏史
      patientInfo.value.allergies = message
      conversationStep.value = 4
      break
    case 4:
      // 输入用药情况
      patientInfo.value.medication = message
      conversationStep.value = 5
      // 开始分配医生
      assignDoctor()
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
const confirmPatientSelection = () => {
  if (!tempSelectedPatient.value) return
  selectedPatient.value = tempSelectedPatient.value
  conversationStep.value = 2
  nextTick(() => {
    scrollToBottom()
  })
}

// 自动分配医生
const assignDoctor = async () => {
  await new Promise(resolve => setTimeout(resolve, 1500))

  const doctorsMap: Record<string, any[]> = {
    bone: [
      { id: 'D001', name: '张建国', title: '主任医师', hospital: '北京积水潭医院', department: '骨科', specialty: '擅长颈椎病、腰椎间盘突出、关节炎等骨科常见病', rating: 4.9, price: 19.9, isOnline: true, canPrescribe: true, waitTime: 5, consultCount: '1.2万' },
      { id: 'D002', name: '李明华', title: '副主任医师', hospital: '上海长征医院', department: '骨科', specialty: '擅长关节外科、运动医学、骨折创伤', rating: 4.8, price: 14.9, isOnline: true, canPrescribe: true, waitTime: 8, consultCount: '8562' }
    ],
    neurology: [
      { id: 'D003', name: '王芳', title: '主任医师', hospital: '北京宣武医院', department: '神经内科', specialty: '擅长失眠、头痛、脑血管病、帕金森病', rating: 4.9, price: 19.9, isOnline: true, canPrescribe: true, waitTime: 3, consultCount: '2.1万' },
      { id: 'D004', name: '赵明', title: '副主任医师', hospital: '上海华山医院', department: '神经内科', specialty: '擅长头晕、眩晕、周围神经病', rating: 4.7, price: 14.9, isOnline: true, canPrescribe: true, waitTime: 10, consultCount: '6543' }
    ],
    dermatology: [
      { id: 'D005', name: '周峰', title: '主治医师', hospital: '武汉市黄陂区人民医院', department: '皮肤科', specialty: '擅长过敏性疾病（特应性皮炎、湿疹、荨麻疹）', rating: 4.9, price: 19.9, isOnline: true, canPrescribe: true, waitTime: 4, consultCount: '7542' },
      { id: 'D006', name: '陈琼', title: '主治医师', hospital: '辽宁中医药大学附属第二医院', department: '皮肤科', specialty: '擅长中医药治疗痤疮、湿疹、银屑病', rating: 4.8, price: 19.9, isOnline: true, canPrescribe: true, waitTime: 37, consultCount: '4.2万' }
    ],
    general: [
      { id: 'D007', name: '刘伟', title: '副主任医师', hospital: '北京协和医院', department: '全科', specialty: '擅长常见病、多发病、慢性病管理', rating: 4.8, price: 9.9, isOnline: true, canPrescribe: true, waitTime: 6, consultCount: '3.5万' },
      { id: 'D008', name: '孙丽', title: '主治医师', hospital: '上海瑞金医院', department: '全科', specialty: '擅长健康管理、体检报告解读', rating: 4.7, price: 4.9, isOnline: true, canPrescribe: true, waitTime: 12, consultCount: '1.8万' }
    ]
  }

  const doctors = doctorsMap[departmentCode.value] || doctorsMap.general
  const symptomKeywords = patientInfo.value.symptom
  let matchedDoctor = doctors[0]

  if (symptomKeywords.includes('失眠') || symptomKeywords.includes('头痛')) {
    matchedDoctor = doctors.find(d => d.specialty.includes('失眠') || d.specialty.includes('头痛')) || doctors[0]
  }

  assignedDoctor.value = matchedDoctor
  conversationStep.value = 6

  // 创建问诊记录
  try {
    const res = await createConsultation({
      doctorId: matchedDoctor.id,
      symptom: JSON.stringify(patientInfo.value),
      patientId: selectedPatient.value?.id || '',
      type: 'text',
      departmentCode: departmentCode.value
    })

    if (res && res.id) {
      consultationId.value = res.id
    } else {
      consultationId.value = 'C' + Date.now()
    }
  } catch (error) {
    console.error('创建问诊失败:', error)
    consultationId.value = 'C' + Date.now()
  }

  nextTick(() => {
    scrollToBottom()
  })
}

// 跳转到支付
const goToPayment = () => {
  if (!consultationId.value || !assignedDoctor.value) {
    ElMessage.error('问诊信息不存在')
    return
  }

  router.push({
    path: `/inquiry/pay/${consultationId.value}`,
    query: {
      doctorId: assignedDoctor.value.id,
      doctorName: assignedDoctor.value.name,
      doctorTitle: assignedDoctor.value.title,
      hospital: assignedDoctor.value.hospital,
      department: assignedDoctor.value.department,
      patientId: selectedPatient.value?.id,
      patientName: selectedPatient.value?.name,
      patientGender: selectedPatient.value?.gender,
      patientAge: selectedPatient.value?.age?.toString(),
      symptom: patientInfo.value.symptom,
      price: assignedDoctor.value.price?.toString(),
      departmentCode: departmentCode.value
    }
  })
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatSectionRef.value) {
    chatSectionRef.value.scrollTop = chatSectionRef.value.scrollHeight
  }
}

// 返回
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

onMounted(async () => {
  await loadDepartmentInfo()
  loadPatientProfiles()
  try {
    const stepsRes = await businessApi.getConsultationSteps()
    consultationSteps.value = stepsRes.data
  } catch (error) {
    console.error('加载问诊步骤失败:', error)
  }
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

.department-triage-page {
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

// 科室信息卡片
.department-card-section {
  background: white;
  padding: 16px;
  margin-bottom: 12px;

  .department-info-card {
    background: $inquiry-bg-light;
    border-radius: 12px;
    padding: 16px;

    .dept-header {
      margin-bottom: 12px;

      .dept-basic {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;

        .dept-name {
          font-size: 18px;
          font-weight: 600;
          color: $inquiry-text-primary;
        }

        .dept-level {
          padding: 2px 8px;
          background: rgba($warning, 0.12);
          color: $warning;
          border-radius: 4px;
          font-size: 11px;
          font-weight: 600;
        }

        .dept-price {
          display: flex;
          align-items: baseline;
          gap: 6px;

          .price-current {
            font-size: 20px;
            font-weight: 700;
            color: $price-red;
          }

          .price-original {
            font-size: 13px;
            color: $text-tertiary;
            text-decoration: line-through;
          }
        }

        .subsidy-tag {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          padding: 2px 8px;
          background: rgba($subsidy-orange, 0.1);
          color: $subsidy-orange;
          border-radius: 4px;
          font-size: 11px;
          font-weight: 500;

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }

    .dept-symptoms {
      font-size: 13px;
      color: $inquiry-text-secondary;
      line-height: 1.5;
      margin-bottom: 12px;
    }

    .dept-tags {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .dept-tag {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: $inquiry-text-secondary;

        .el-icon {
          color: $inquiry-primary;
          font-size: 14px;
          flex-shrink: 0;
        }
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
        background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-light 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        .el-icon {
          font-size: 18px;
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

        .default-tag {
          padding: 2px 8px;
          background: rgba($inquiry-primary, 0.1);
          color: $inquiry-primary;
          border-radius: 4px;
          font-size: 11px;
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
    }
  }
}
</style>
