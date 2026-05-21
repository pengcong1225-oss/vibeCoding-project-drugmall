<template>
  <div class="waiting-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">等待接诊</span>
      <div class="header-actions"></div>
    </div>

    <!-- 流程指示器 -->
    <div class="process-indicator">
      <div class="process-step completed">
        <span class="step-num">①</span>
        <span class="step-text">导诊助手</span>
      </div>
      <div class="process-arrow">></div>
      <div class="process-step completed">
        <span class="step-num">②</span>
        <span class="step-text">支付诊费</span>
      </div>
      <div class="process-arrow">></div>
      <div class="process-step active">
        <span class="step-num">③</span>
        <span class="step-text">医生接诊</span>
      </div>
      <div class="process-arrow">></div>
      <div class="process-step">
        <span class="step-num">④</span>
        <span class="step-text">问诊咨询</span>
      </div>
    </div>

    <!-- 等待状态 -->
    <div class="waiting-content">
      <div class="waiting-card">
        <div class="waiting-icon">
          <el-icon class="loading-icon"><Loading /></el-icon>
        </div>
        <div class="waiting-title">正在等待医生接诊</div>
        <div class="waiting-desc">医生将在24小时内接诊，请耐心等待</div>
        <div class="waiting-queue" v-if="queueInfo.position > 0">
          当前排队位置：<span class="queue-num">第{{ queueInfo.position }}位</span>
        </div>
      </div>

      <!-- 医生信息 -->
      <div class="doctor-card">
        <div class="doctor-header">
          <img :src="doctorInfo.avatar || defaultAvatar" class="doctor-avatar" alt="医生头像" />
          <div class="doctor-basic">
            <div class="name-row">
              <span class="doctor-name">{{ doctorInfo.name }}</span>
              <span class="doctor-title">{{ doctorInfo.title }}</span>
            </div>
            <div class="hospital-row">
              <span class="hospital-badge">三甲</span>
              <span class="hospital-name">{{ doctorInfo.hospital }}·{{ doctorInfo.department }}</span>
            </div>
          </div>
        </div>
        <div class="doctor-stats" v-if="doctorInfo.consultationCount">
          <div class="stat-item">
            <span class="stat-value">{{ formatCount(doctorInfo.consultationCount) }}</span>
            <span class="stat-label">接诊量</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ (doctorInfo.rating * 100).toFixed(0) }}%</span>
            <span class="stat-label">好评率</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ doctorInfo.workYears || 12 }}年</span>
            <span class="stat-label">从业年限</span>
          </div>
        </div>
      </div>

      <!-- 提示信息 -->
      <div class="tips-section">
        <div class="tips-title">
          <el-icon><InfoFilled /></el-icon>
          <span>温馨提示</span>
        </div>
        <div class="tips-list">
          <div class="tip-item">1. 医生接诊后，您将收到消息通知</div>
          <div class="tip-item">2. 接诊后24小时内可不限次沟通</div>
          <div class="tip-item">3. 如医生24小时内未接诊，费用将自动退回</div>
          <div class="tip-item">4. 紧急病情请立即前往医院就诊</div>
        </div>
      </div>
    </div>

    <!-- 底部操作 -->
    <div class="waiting-footer">
      <button class="action-btn secondary" @click="remindDoctorAction" :disabled="reminding">
        <span v-if="reminding">提醒中...</span>
        <span v-else>提醒医生</span>
      </button>
      <button class="action-btn primary" @click="cancelConsultationAction" :disabled="cancelling">
        <span v-if="cancelling">取消中...</span>
        <span v-else>取消问诊</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Loading, InfoFilled } from '@element-plus/icons-vue'
import { getConsultationDetail, getDoctorDetail, checkDoctorAcceptance, cancelConsultation, remindDoctor, type DoctorInfo, type ConsultationDetail } from '@/api/modules/inquiry'
import { ROUTES } from '@/constants/routes'

const route = useRoute()
const router = useRouter()

// 默认头像
const defaultAvatar = 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face'

// 问诊ID
const consultationId = ref(route.params.consultationId as string || '')

// 医生信息
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
  workYears: 12,
  isOnline: true,
  tags: [],
  inquiryCount: 0,
  rating: 0
})

// 排队信息
const queueInfo = ref({
  position: 0,
  total: 0
})

// 状态
const reminding = ref(false)
const cancelling = ref(false)
const checkTimer = ref<number | null>(null)

// 加载问诊和医生信息
const loadConsultationInfo = async () => {
  if (!consultationId.value) {
    ElMessage.error('问诊信息不存在')
    router.push(ROUTES.INQUIRY)
    return
  }

  try {
    console.log('加载问诊详情:', consultationId.value)
    
    // 获取问诊详情
    const detail: ConsultationDetail = await getConsultationDetail(consultationId.value)
    console.log('问诊详情:', detail)
    
    // 如果有医生信息，加载医生详情
    if (detail.doctorId) {
      try {
        const doctor: DoctorInfo = await getDoctorDetail(detail.doctorId)
        doctorInfo.value = {
          ...doctor,
          inquiryCount: doctor.consultationCount || 0,
          rating: doctor.rating || 0
        }
        console.log('医生信息:', doctorInfo.value)
      } catch (error) {
        console.error('获取医生信息失败:', error)
        // 使用问诊中的医生信息
        doctorInfo.value = {
          id: detail.doctorId || '',
          name: detail.doctorName || '在线医生',
          title: detail.doctorTitle || '主治医师',
          hospital: detail.hospital || '',
          department: detail.department || '',
          avatar: detail.doctorAvatar || '',
          specialty: '',
          price: detail.price || 19.9,
          waitTime: 12,
          workYears: 12,
          isOnline: true,
          tags: [],
          inquiryCount: 0,
          rating: 0
        }
      }
    }
    
    // 计算排队位置（根据pending状态的问诊数量）
    // 这里简化处理，实际应该根据创建时间排序
    queueInfo.value = {
      position: Math.floor(Math.random() * 5) + 1, // TODO: 从后端获取真实排队位置
      total: Math.floor(Math.random() * 10) + 5
    }
    
  } catch (error) {
    console.error('获取问诊信息失败:', error)
    ElMessage.error('获取问诊信息失败')
    return
  }

  // 加载完信息后立即检查状态（如果医生已接诊，跳过等待轮询）
  checkAcceptanceStatus()
}

// 检查医生接诊状态
const checkAcceptanceStatus = async () => {
  if (!consultationId.value) return

  try {
    const res = await checkDoctorAcceptance(consultationId.value)

    // 如果医生已接诊，跳转到聊天页面
    if (res.accepted || res.status === 'processing') {
      stopChecking()
      const isPrescriptionFlow = route.query.type === 'prescription'
      ElMessage.success(isPrescriptionFlow ? '医生已接诊，即将进入处方咨询' : '医生已接诊，即将进入问诊')
      setTimeout(() => {
        router.push({
          path: isPrescriptionFlow ? ROUTES.PRESCRIPTION_CONSULT : ROUTES.INQUIRY_CHAT,
          query: {
            consultationId: consultationId.value,
            doctorId: doctorInfo.value.id,
            doctorName: doctorInfo.value.name
          }
        })
      }, 1500)
    }
  } catch (error) {
    console.error('检查接诊状态失败:', error)
  }
}

// 开始轮询检查
const startChecking = () => {
  checkTimer.value = window.setInterval(() => {
    checkAcceptanceStatus()
  }, 5000) // 每5秒检查一次
}

// 停止轮询
const stopChecking = () => {
  if (checkTimer.value) {
    clearInterval(checkTimer.value)
    checkTimer.value = null
  }
}

// 提醒医生
const remindDoctorAction = async () => {
  reminding.value = true
  try {
    await remindDoctor(consultationId.value)
    ElMessage.success('已提醒医生，请耐心等待')
  } catch (error) {
    console.error('提醒医生失败:', error)
    ElMessage.error('提醒失败，请重试')
  } finally {
    reminding.value = false
  }
}

// 取消问诊
const cancelConsultationAction = async () => {
  try {
    await ElMessageBox.confirm(
      '取消问诊后费用将原路退回，确定要取消吗？',
      '确认取消',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '继续等待',
        type: 'warning'
      }
    )

    cancelling.value = true
    
    await cancelConsultation(consultationId.value)

    ElMessage.success('问诊已取消，费用将原路退回')
    router.push(ROUTES.INQUIRY)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消问诊失败:', error)
      ElMessage.error('取消失败，请重试')
    }
  } finally {
    cancelling.value = false
  }
}

const formatCount = (count?: number): string => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count.toString()
}

const goBack = () => {
  router.push(ROUTES.INQUIRY)
}

onMounted(() => {
  loadConsultationInfo()
  startChecking()
})

onUnmounted(() => {
  stopChecking()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00B894;

.waiting-page {
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
    width: 36px;
  }
}

// 流程指示器
.process-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid $border-light;
  gap: 4px;

  .process-step {
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 12px;
    color: $text-tertiary;

    &.completed {
      color: $primary-green;
    }

    &.active {
      color: $primary-green;
      font-weight: 600;
    }

    .step-num {
      font-size: 13px;
    }

    .step-text {
      font-size: 12px;
    }
  }

  .process-arrow {
    color: $text-tertiary;
    font-size: 10px;
    margin: 0 2px;
  }
}

// 等待内容
.waiting-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;

  .waiting-card {
    background: white;
    border-radius: 16px;
    padding: 32px 24px;
    text-align: center;
    margin-bottom: 16px;

    .waiting-icon {
      width: 80px;
      height: 80px;
      margin: 0 auto 20px;
      background: linear-gradient(135deg, rgba($primary-green, 0.1) 0%, rgba($primary-green-light, 0.1) 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      .loading-icon {
        font-size: 40px;
        color: $primary-green;
        animation: rotate 2s linear infinite;
      }
    }

    .waiting-title {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 8px;
    }

    .waiting-desc {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 16px;
    }

    .waiting-queue {
      font-size: 14px;
      color: $text-secondary;

      .queue-num {
        color: $primary-green;
        font-weight: 600;
      }
    }
  }

  // 医生卡片
  .doctor-card {
    background: white;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 16px;

    .doctor-header {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid $border-light;

      .doctor-avatar {
        width: 60px;
        height: 60px;
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

          .doctor-name {
            font-size: 17px;
            font-weight: 600;
            color: $text-primary;
          }

          .doctor-title {
            font-size: 13px;
            color: $text-secondary;
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

    .doctor-stats {
      display: flex;
      justify-content: space-around;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: $text-primary;
        }

        .stat-label {
          font-size: 12px;
          color: $text-tertiary;
        }
      }
    }
  }

  // 提示信息
  .tips-section {
    background: white;
    border-radius: 16px;
    padding: 20px;

    .tips-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 16px;

      .el-icon {
        color: $primary-green;
      }
    }

    .tips-list {
      .tip-item {
        font-size: 13px;
        color: $text-secondary;
        line-height: 1.8;
        padding: 8px 0;
        border-bottom: 1px solid $border-light;

        &:last-child {
          border-bottom: none;
        }
      }
    }
  }
}

// 底部操作
.waiting-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  padding-bottom: calc(16px + $safe-area-bottom);
  background: white;
  border-top: 1px solid $border-light;

  .action-btn {
    flex: 1;
    padding: 14px;
    border-radius: 24px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      transform: scale(0.98);
    }

    &.secondary {
      background: $bg-primary;
      color: $text-primary;
      border: 1px solid $border-light;
    }

    &.primary {
      background: $primary-green;
      color: white;
      border: none;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
