<template>
  <div class="pre-inquiry-page">
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
    <div class="doctor-card-section">
      <div class="doctor-info-card">
        <div class="doctor-header">
          <img :src="doctorInfo.avatar || defaultAvatar" class="doctor-avatar" alt="医生头像" />
          <div class="doctor-basic">
            <div class="name-row">
              <span class="doctor-name">{{ doctorInfo.name }}</span>
              <span class="doctor-title">{{ doctorInfo.title }}</span>
              <span v-if="doctorInfo.canPrescribe" class="prescribe-badge">
                <el-icon><FirstAidKit /></el-icon>
                可开方
              </span>
            </div>
            <div class="hospital-row">
              <span class="hospital-badge">三甲</span>
              <span class="hospital-name">{{ doctorInfo.hospital }}·{{ doctorInfo.department }}</span>
            </div>
          </div>
        </div>
        <div class="doctor-specialty">
          <span class="specialty-label">擅长：</span>
          <span class="specialty-text">{{ doctorInfo.specialty }}</span>
        </div>
        <div class="service-info">
          <div class="service-item">
            <span class="service-name">图文咨询</span>
            <span class="service-price">¥{{ doctorInfo.price || 19.9 }}</span>
          </div>
          <div class="service-time">平均接诊时长{{ doctorInfo.waitTime || 12 }}分钟</div>
        </div>
        <div class="rights-tags">
          <div class="right-tag">
            <el-icon><CircleCheck /></el-icon>
            <span>24h未接诊自动退款</span>
          </div>
          <div class="right-tag">
            <el-icon><ChatDotRound /></el-icon>
            <span>接诊后24h不限次沟通</span>
          </div>
        </div>
      </div>
      <div class="service-provider">
        <span class="provider-text">互联网医院</span>
        <span class="agreement-link" @click="showAgreement">《知情同意书》</span>
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

      <div class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>详细描述患者的病情，如：症状、患病时长、用药情况等。</p>
          <p class="example">示例：半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。</p>
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
              <span class="patient-detail">{{ profile.gender === 'male' ? '男' : '女' }} {{ profile.age }}岁 {{ profile.birthday }}</span>
              <span class="patient-phone">{{ maskPhone(profile.phone) }}</span>
            </div>
            <span v-if="profile.isDefault" class="default-tag">默认就诊人</span>
          </div>
          <div class="patient-card other" @click="goToAddPatient">
            <el-icon><Plus /></el-icon>
            <span>添加新就诊人</span>
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

      <!-- AI确认消息 -->
      <div v-if="selectedPatient" class="message assistant-message">
        <div class="avatar assistant-avatar">
          <el-icon><Service /></el-icon>
        </div>
        <div class="message-content">
          <p>好的，您的情况我已了解，您完成支付后我将通知医生接诊。</p>
        </div>
      </div>

      <!-- 支付卡片 -->
      <div v-if="selectedPatient && consultationId" class="payment-card">
        <div class="payment-header">
          <div class="patient-info-row">
            <span class="patient-name">{{ selectedPatient.name }}</span>
          </div>
          <div class="service-type">图文咨询</div>
          <div class="service-price">¥{{ doctorInfo.price || 19.9 }}</div>
        </div>
        <div class="payment-desc">
          <p>打字、发图，24小时不限次沟通</p>
          <p>24小时未接诊自动退款</p>
        </div>
        <button class="pay-btn" @click="goToPayment">去支付</button>
      </div>
    </div>

    <!-- 底部输入栏 -->
    <div v-if="!selectedPatient" class="input-section">
      <div class="input-wrapper">
        <input
          v-model="inputMessage"
          type="text"
          placeholder="请详细描述您的病情"
          @keyup.enter="sendMessage"
          :disabled="!!symptomInput"
        />
        <button
          class="send-btn"
          :disabled="!inputMessage.trim() || !!symptomInput"
          @click="sendMessage"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Service,
  CircleCheck,
  ChatDotRound,
  ArrowRight,
  Plus,
  FirstAidKit
} from '@element-plus/icons-vue'
import {
  getDoctorDetail,
  createConsultation,
  type DoctorInfo,
  type PatientProfile
} from '@/api/modules/inquiry'
import { getPatients } from '@/api/modules/user'
import type { Patient } from '@/types'

const route = useRoute()
const router = useRouter()

const defaultAvatar = 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face'

const doctorInfo = ref<DoctorInfo>({
  id: '',
  name: '',
  title: '',
  hospital: '',
  department: '',
  avatar: '',
  specialty: '',
  price: 19.9,
  waitTime: 12,
  isOnline: true,
  canPrescribe: true,
  tags: [],
  inquiryCount: 0,
  rating: 0.98
})

const patientProfiles = ref<Patient[]>([])
const tempSelectedPatient = ref<Patient | null>(null)
const selectedPatient = ref<Patient | null>(null)

const symptomInput = ref('')
const inputMessage = ref('')
const consultationId = ref('')
const chatSectionRef = ref<HTMLElement>()

// 脱敏处理
const maskPhone = (phone: string) => {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}

const loadDoctorInfo = async () => {
  const doctorId = route.params.doctorId as string

  try {
    // 如果没有doctorId，使用默认医生信息（智能分诊模式）
    if (!doctorId) {
      doctorInfo.value = {
        id: '0',
        name: '智能分诊',
        title: 'AI助手',
        hospital: '互联网医院',
        department: '全科',
        avatar: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=200&h=200&fit=crop&crop=face',
        specialty: '根据您的症状，为您推荐合适的医生',
        price: 0,
        waitTime: 0,
        isOnline: true,
        canPrescribe: false,
        tags: ['AI'],
        inquiryCount: 0,
        rating: 1.0
      }
      return
    }

    // 有doctorId时，使用指定医生信息
    doctorInfo.value = {
      id: doctorId,
      name: route.query.name as string || '刘贞君',
      title: '主治医师',
      hospital: '山东青岛中西医结合医院',
      department: route.query.department as string || '皮肤科',
      avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face',
      specialty: '擅长中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病',
      price: 19.9,
      waitTime: 12,
      isOnline: true,
      canPrescribe: true,
      tags: ['三甲'],
      inquiryCount: 11000,
      rating: 0.98
    }
  } catch (error) {
    console.error('获取医生信息失败:', error)
    ElMessage.error('获取医生信息失败')
  }
}

const loadPatientProfiles = async () => {
  try {
    const res = await getPatients()
    if (Array.isArray(res) && res.length > 0) {
      patientProfiles.value = res
    } else {
      // 使用模拟数据
      patientProfiles.value = [
        { id: '1', name: '彭聪', gender: 'male', age: 41, phone: '13812341234', birthday: '1985-02-22', relationship: '本人', isDefault: true, idCard: '' },
        { id: '2', name: '李小红', gender: 'female', age: 35, phone: '13956785678', birthday: '1990-06-15', relationship: '配偶', isDefault: false, idCard: '' }
      ]
    }
  } catch (error) {
    console.error('获取患者档案失败:', error)
    // 使用模拟数据
    patientProfiles.value = [
      { id: '1', name: '彭聪', gender: 'male', age: 41, phone: '13812341234', birthday: '1985-02-22', relationship: '本人', isDefault: true, idCard: '' },
      { id: '2', name: '李小红', gender: 'female', age: 35, phone: '13956785678', birthday: '1990-06-15', relationship: '配偶', isDefault: false, idCard: '' }
    ]
  }
}

const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  symptomInput.value = inputMessage.value.trim()
  inputMessage.value = ''

  nextTick(() => {
    scrollToBottom()
  })
}

const selectPatient = (profile: Patient) => {
  tempSelectedPatient.value = profile
}

const confirmPatientSelection = async () => {
  if (!tempSelectedPatient.value) return

  selectedPatient.value = tempSelectedPatient.value

  try {
    // 创建问诊记录
    const res = await createConsultation({
      doctorId: doctorInfo.value.id,
      symptom: symptomInput.value,
      patientId: selectedPatient.value.id,
      type: 'text'
    })

    if (res && res.id) {
      consultationId.value = res.id
    } else {
      consultationId.value = 'C' + Date.now()
    }

    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('创建问诊失败:', error)
    // 仍然创建本地问诊ID用于演示
    consultationId.value = 'C' + Date.now()
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
    path: `/inquiry/pay/${consultationId.value}`,
    query: {
      doctorId: doctorInfo.value.id,
      doctorName: doctorInfo.value.name,
      doctorTitle: doctorInfo.value.title,
      hospital: doctorInfo.value.hospital,
      department: doctorInfo.value.department,
      workYears: doctorInfo.value.workYears?.toString() || '12',
      patientId: selectedPatient.value?.id,
      patientName: selectedPatient.value?.name,
      patientGender: selectedPatient.value?.gender,
      patientAge: selectedPatient.value?.age?.toString(),
      symptom: symptomInput.value,
      price: doctorInfo.value.price?.toString()
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
  router.push('/inquiry/list')
}

const showAgreement = () => {
  ElMessageBox.alert(
    '1. 互联网诊疗服务适用于部分常见病、慢性病的复诊患者。\n2. 医生将根据您提供的信息进行初步判断，必要时建议您线下就诊。\n3. 医生开具的电子处方需经药师审核后方可生效。\n4. 紧急病情请立即前往医院就诊。',
    '知情同意书',
    {
      confirmButtonText: '我知道了',
      customClass: 'agreement-dialog'
    }
  )
}

const goToAddPatient = () => {
  router.push('/patient')
}

const goToManagePatients = () => {
  router.push('/patient')
}

onMounted(() => {
  loadDoctorInfo()
  loadPatientProfiles()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;
$user-message-bg: #00C9A7;
$user-message-text: #FFFFFF;
$pay-yellow: #FFD700;
$pay-orange: #FF9500;
$price-red: #FF4D4F;

.pre-inquiry-page {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
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
        background: $primary-green;
        color: white;
      }

      .step-text {
        color: $primary-green;
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
    background: $bg-primary;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;

    .doctor-header {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;

      .doctor-avatar {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        object-fit: cover;
      }

      .doctor-basic {
        flex: 1;

        .name-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 6px;
          flex-wrap: wrap;

          .doctor-name {
            font-size: 17px;
            font-weight: 600;
            color: $text-primary;
          }

          .doctor-title {
            font-size: 13px;
            color: $text-secondary;
          }

          .prescribe-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 2px 8px;
            background: rgba($primary-green, 0.1);
            color: $primary-green;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 500;

            .el-icon {
              font-size: 12px;
            }
          }
        }

        .hospital-row {
          display: flex;
          align-items: center;
          gap: 6px;

          .hospital-badge {
            padding: 2px 6px;
            background: rgba($warning, 0.12);
            color: $warning;
            border-radius: 4px;
            font-size: 10px;
            font-weight: 600;
          }

          .hospital-name {
            font-size: 13px;
            color: $text-secondary;
          }
        }
      }
    }

    .doctor-specialty {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.5;
      margin-bottom: 12px;

      .specialty-label {
        color: $text-tertiary;
      }
    }

    .service-info {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px dashed $border-light;

      .service-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .service-name {
          font-size: 14px;
          color: $text-primary;
        }

        .service-price {
          font-size: 18px;
          font-weight: 600;
          color: $price-red;
        }
      }

      .service-time {
        font-size: 12px;
        color: $text-tertiary;
      }
    }

    .rights-tags {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .right-tag {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: $text-secondary;

        .el-icon {
          color: $primary-green;
          font-size: 14px;
        }
      }
    }
  }

  .service-provider {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    font-size: 12px;

    .provider-text {
      color: $text-tertiary;
    }

    .agreement-link {
      color: $primary-green;
      cursor: pointer;

      &:active {
        opacity: 0.8;
      }
    }
  }
}

// 聊天区域
.chat-section {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: $bg-primary;

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
        background: linear-gradient(135deg, $primary-green 0%, $primary-green-light 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        flex-shrink: 0;

        .el-icon {
          font-size: 18px;
        }
      }

      .message-content {
        background: white;
        padding: 12px 16px;
        border-radius: 12px;
        font-size: 14px;
        line-height: 1.6;
        color: $text-primary;
        max-width: 70%;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

        .example {
          color: $text-tertiary;
          font-size: 13px;
          margin-top: 8px;
        }
      }
    }
  }

  .user-message {
    justify-content: flex-end;

    .message-content {
      background: $user-message-bg;
      color: $user-message-text;
      padding: 12px 16px;
      border-radius: 12px;
      font-size: 14px;
      line-height: 1.6;
      max-width: 70%;
    }
  }
}

// 患者选择区域
.patient-selection {
  margin-bottom: 16px;

  .patient-cards {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 12px;

    .patient-card {
      background: white;
      border-radius: 12px;
      padding: 16px;
      display: flex;
      align-items: flex-start;
      gap: 12px;
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.2s;

      &:active {
        transform: scale(0.99);
      }

      &.active {
        border-color: $primary-green;
        background: rgba($primary-green, 0.02);
      }

      .radio-indicator {
        padding-top: 2px;

        .radio-circle {
          width: 20px;
          height: 20px;
          border-radius: 50%;
          border: 2px solid #ddd;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.2s;

          &.checked {
            border-color: $primary-green;
            background: $primary-green;
          }

          .radio-inner {
            width: 8px;
            height: 8px;
            background: white;
            border-radius: 50%;
          }
        }
      }

      .patient-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 4px;

        .patient-name {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
        }

        .patient-detail {
          font-size: 13px;
          color: $text-secondary;
        }

        .patient-phone {
          font-size: 13px;
          color: $text-tertiary;
        }
      }

      .default-tag {
        padding: 2px 8px;
        background: rgba($primary-green, 0.1);
        color: $primary-green;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }

      &.other {
        justify-content: center;
        align-items: center;
        color: $primary-green;
        font-size: 14px;
        border: 1px dashed $primary-green;

        .el-icon {
          font-size: 18px;
        }

        &:active {
          background: rgba($primary-green, 0.05);
        }
      }
    }
  }

  .manage-link {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    color: $text-secondary;
    font-size: 14px;
    cursor: pointer;
    padding: 8px;

    &:active {
      color: $primary-green;
    }

    .el-icon {
      font-size: 14px;
    }
  }

  .confirm-selection {
    margin-top: 16px;

    .confirm-btn {
      width: 100%;
      padding: 14px;
      background: $primary-green;
      color: white;
      border: none;
      border-radius: 24px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;

      &:active {
        opacity: 0.9;
        transform: scale(0.98);
      }
    }
  }
}

// 支付卡片
.payment-card {
  background: linear-gradient(135deg, #FFF8E7 0%, #FFF3D6 100%);
  border-radius: 16px;
  padding: 20px;
  margin-top: 16px;
  border: 1px solid rgba($pay-orange, 0.2);

  .payment-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px dashed rgba($pay-orange, 0.3);

    .patient-info-row {
      display: flex;
      align-items: center;
      gap: 8px;

      &::before {
        content: '';
        width: 32px;
        height: 32px;
        background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23FF9500"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>') center/contain no-repeat;
      }

      .patient-name {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .service-type {
      font-size: 14px;
      color: $text-secondary;
      background: white;
      padding: 4px 12px;
      border-radius: 12px;
    }

    .service-price {
      font-size: 20px;
      font-weight: 700;
      color: $price-red;
    }
  }

  .payment-desc {
    margin-bottom: 16px;

    p {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;

      &:first-child {
        color: $text-primary;
        font-weight: 500;
      }
    }
  }

  .pay-btn {
    width: 100%;
    padding: 14px;
    background: $pay-yellow;
    color: $text-primary;
    border: none;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      opacity: 0.9;
      transform: scale(0.98);
    }
  }
}

// 输入区域
.input-section {
  background: white;
  padding: 12px 16px;
  border-top: 1px solid $border-color;

  .input-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;

    input {
      flex: 1;
      padding: 10px 16px;
      border: 1px solid $border-color;
      border-radius: 20px;
      font-size: 14px;
      outline: none;

      &:focus {
        border-color: $primary-green;
      }

      &:disabled {
        background: $bg-primary;
        color: $text-tertiary;
      }
    }

    .send-btn {
      padding: 10px 20px;
      background: $primary-green;
      color: white;
      border: none;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;

      &:disabled {
        background: $text-tertiary;
        cursor: not-allowed;
      }
    }
  }
}
</style>
