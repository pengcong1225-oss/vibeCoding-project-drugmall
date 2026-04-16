<template>
  <div class="pay-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="nav-title">订单结算</span>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 倒计时提示 -->
    <div class="countdown-section">
      <div class="countdown-title">
        待支付，剩余 <span class="countdown-time">{{ formatCountdown }}</span>
      </div>
      <div class="countdown-subtitle">请及时支付，以便开启服务</div>
    </div>

    <!-- 医生信息 -->
    <div class="doctor-section">
      <div class="doctor-card">
        <img :src="doctorInfo.avatar || defaultAvatar" class="doctor-avatar" alt="医生头像" />
        <div class="doctor-info">
          <div class="doctor-name">{{ doctorInfo.name }} {{ doctorInfo.title }}</div>
          <div class="doctor-hospital">
            <span class="hospital-badge">三甲</span>
            <span>{{ doctorInfo.hospital }} {{ doctorInfo.department }}</span>
          </div>
          <div class="doctor-experience" v-if="doctorInfo.workYears">从业{{ doctorInfo.workYears }}年</div>
        </div>
      </div>
    </div>

    <!-- 问诊信息 -->
    <div class="info-section">
      <div class="section-title">问诊信息</div>
      <div class="info-card">
        <div class="info-row">
          <span class="info-label">患者信息</span>
          <span class="info-value">{{ patientInfo.name }} {{ patientInfo.gender === 'male' ? '男' : '女' }} {{ patientInfo.age }}岁</span>
        </div>
        <div class="info-row">
          <span class="info-label">病情描述</span>
          <span class="info-value symptom">{{ patientInfo.symptom }}</span>
        </div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="info-section">
      <div class="section-title">订单信息</div>
      <div class="info-card">
        <div class="info-row">
          <span class="info-label">订单号码</span>
          <div class="info-value-with-action">
            <span>{{ orderInfo.orderNo }}</span>
            <span class="copy-btn" @click="copyOrderNo">复制</span>
          </div>
        </div>
        <div class="info-row">
          <span class="info-label">订单类型</span>
          <span class="info-value">专家问诊</span>
        </div>
        <div class="info-row">
          <span class="info-label">服务模式</span>
          <span class="info-value">图文问诊</span>
        </div>
        <div class="info-row">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ orderInfo.createTime }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">订单金额</span>
          <span class="info-value price">¥{{ orderInfo.amount }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">实付款</span>
          <span class="info-value price">¥{{ orderInfo.paidAmount }}</span>
        </div>
      </div>
    </div>

    <!-- 底部提示 -->
    <div class="bottom-hint">—— 已经到底了 ——</div>

    <!-- 底部支付按钮 -->
    <div class="pay-footer">
      <button class="pay-btn" @click="goToCheckout" :disabled="paying">
        <span v-if="paying">
          <el-icon class="loading-icon"><Loading /></el-icon>
          加载中...
        </span>
        <span v-else>去支付</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'
import { getConsultationDetail, type DoctorInfo } from '@/api/modules/inquiry'

const route = useRoute()
const router = useRouter()

// 默认头像
const defaultAvatar = 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face'

// 医生信息
const doctorInfo = ref<DoctorInfo>({
  id: '',
  name: '',
  title: '',
  hospital: '',
  department: '',
  avatar: '',
  workYears: 12,
  isOnline: true,
  tags: [],
  inquiryCount: 0,
  rating: 0.98
})

// 患者信息
const patientInfo = ref({
  name: '',
  gender: 'male' as 'male' | 'female',
  age: 41,
  symptom: ''
})

// 订单信息
const orderInfo = ref({
  orderNo: '',
  type: 'expert',
  serviceMode: 'text',
  createTime: '',
  amount: 19.9,
  paidAmount: 19.9
})

// 倒计时
const countdown = ref(30 * 60) // 30分钟
const countdownTimer = ref<number | null>(null)
const paying = ref(false)

// 格式化倒计时
const formatCountdown = computed(() => {
  const minutes = Math.floor(countdown.value / 60)
  const seconds = countdown.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

// 开始倒计时
const startCountdown = () => {
  countdownTimer.value = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      // 倒计时结束，取消订单
      stopCountdown()
      ElMessage.warning('支付超时，订单已取消')
      router.back()
    }
  }, 1000)
}

// 停止倒计时
const stopCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
}

// 加载订单信息
const loadOrderInfo = async () => {
  const consultationId = route.params.consultationId as string
  if (!consultationId) {
    ElMessage.error('订单ID不存在')
    return
  }

  try {
    // 实际项目中调用API
    // const res = await getConsultationDetail(consultationId)

    // 从路由参数获取信息
    doctorInfo.value = {
      id: route.query.doctorId as string || '1',
      name: route.query.doctorName as string || '刘贞君',
      title: route.query.doctorTitle as string || '主治医师',
      hospital: route.query.hospital as string || '山东青岛中西医结合医院',
      department: route.query.department as string || '皮肤科',
      avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face',
      workYears: parseInt(route.query.workYears as string) || 12,
      isOnline: true,
      tags: ['三甲'],
      inquiryCount: 11000,
      rating: 0.98
    }

    patientInfo.value = {
      name: route.query.patientName as string || '彭聪',
      gender: (route.query.patientGender as 'male' | 'female') || 'male',
      age: parseInt(route.query.patientAge as string) || 41,
      symptom: route.query.symptom as string || '头疼'
    }

    const price = parseFloat(route.query.price as string) || 19.9
    orderInfo.value = {
      orderNo: generateOrderNo(),
      type: 'expert',
      serviceMode: 'text',
      createTime: formatDateTime(new Date()),
      amount: price,
      paidAmount: price
    }
  } catch (error) {
    console.error('获取订单信息失败:', error)
    ElMessage.error('获取订单信息失败')
  }
}

// 生成订单号
const generateOrderNo = () => {
  const timestamp = Date.now().toString()
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  return timestamp + random
}

// 格式化日期时间
const formatDateTime = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 复制订单号
const copyOrderNo = () => {
  navigator.clipboard.writeText(orderInfo.value.orderNo).then(() => {
    ElMessage.success('订单号已复制')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 跳转到收银台
const goToCheckout = () => {
  const consultationId = route.params.consultationId as string
  if (!consultationId) {
    ElMessage.error('订单信息不存在')
    return
  }

  paying.value = true
  
  // 模拟加载
  setTimeout(() => {
    paying.value = false
    router.push({
      path: `/inquiry/checkout/${consultationId}`,
      query: {
        ...route.query,
        orderNo: orderInfo.value.orderNo,
        amount: orderInfo.value.amount.toString()
      }
    })
  }, 500)
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadOrderInfo()
  startCountdown()
})

onUnmounted(() => {
  stopCountdown()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$pay-yellow: #FFD700;
$pay-orange: #FF9500;
$price-red: #FF4D4F;

.pay-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 100px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: white;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;

    &:active {
      background: $bg-primary;
    }
  }
}

// 倒计时区域
.countdown-section {
  background: white;
  padding: 20px 16px;
  text-align: center;
  border-bottom: 1px solid $border-light;

  .countdown-title {
    font-size: 16px;
    color: $text-primary;
    margin-bottom: 8px;

    .countdown-time {
      color: $pay-orange;
      font-weight: 600;
      font-size: 20px;
    }
  }

  .countdown-subtitle {
    font-size: 13px;
    color: $text-tertiary;
  }
}

// 医生信息区域
.doctor-section {
  background: white;
  padding: 16px;
  margin-bottom: 12px;

  .doctor-card {
    display: flex;
    gap: 12px;
    padding: 16px;
    background: $bg-primary;
    border-radius: 12px;

    .doctor-avatar {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      object-fit: cover;
    }

    .doctor-info {
      flex: 1;

      .doctor-name {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 6px;
      }

      .doctor-hospital {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: $text-secondary;
        margin-bottom: 4px;

        .hospital-badge {
          padding: 2px 6px;
          background: rgba($warning, 0.12);
          color: $warning;
          border-radius: 4px;
          font-size: 10px;
          font-weight: 600;
        }
      }

      .doctor-experience {
        font-size: 12px;
        color: $text-tertiary;
      }
    }
  }
}

// 信息区域
.info-section {
  background: white;
  padding: 16px;
  margin-bottom: 12px;

  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .info-card {
    background: $bg-primary;
    border-radius: 12px;
    padding: 16px;

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 10px 0;
      border-bottom: 1px solid $border-light;

      &:last-child {
        border-bottom: none;
      }

      .info-label {
        font-size: 14px;
        color: $text-secondary;
        flex-shrink: 0;
      }

      .info-value {
        font-size: 14px;
        color: $text-primary;
        text-align: right;
        max-width: 60%;

        &.symptom {
          color: $text-secondary;
        }

        &.price {
          color: $price-red;
          font-weight: 600;
        }
      }

      .info-value-with-action {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        color: $text-primary;

        .copy-btn {
          padding: 2px 8px;
          background: white;
          border: 1px solid $border-light;
          border-radius: 4px;
          font-size: 12px;
          color: $text-secondary;
          cursor: pointer;

          &:active {
            background: $bg-primary;
          }
        }
      }
    }
  }
}

// 底部提示
.bottom-hint {
  text-align: center;
  padding: 12px;
  font-size: 12px;
  color: $text-tertiary;
}

// 底部支付按钮
.pay-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  padding-bottom: calc(12px + $safe-area-bottom);
  background: white;
  border-top: 1px solid $border-light;

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

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      opacity: 0.9;
      transform: scale(0.98);
    }

    .loading-icon {
      animation: rotate 1s linear infinite;
      margin-right: 4px;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
