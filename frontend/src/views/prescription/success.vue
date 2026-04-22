<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Check, Clock, Warning, Share, Download } from '@element-plus/icons-vue'
import { usePrescriptionStore } from '@/stores/prescription'
import StepBar from './components/StepBar.vue'
import PrescriptionCard from './components/PrescriptionCard.vue'
import type { ElectronicPrescription } from '@/stores/prescription'

const router = useRouter()
const prescriptionStore = usePrescriptionStore()

// 当前步骤
const currentStep = ref(2)

// 倒计时（15分钟支付有效期）
const countdown = ref(15 * 60) // 15分钟，单位秒
const countdownText = computed(() => {
  const minutes = Math.floor(countdown.value / 60)
  const seconds = countdown.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 电子处方数据
const prescription = computed<ElectronicPrescription | null>(() => {
  return prescriptionStore.electronicPrescription
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 去购药（跳转到收银台支付页面）
const goToPay = () => {
  if (!prescription.value) return
  
  // 获取咨询ID（从store中获取）
  const consultationId = prescriptionStore.consultationId || 'CONS' + Date.now()
  
  // 跳转到收银台页面
  router.push(`/inquiry/checkout/${consultationId}`)
}

// 下载处方
const downloadPrescription = () => {
  ElMessage.success('处方下载中...')
}

// 分享处方
const sharePrescription = () => {
  ElMessage.success('分享功能开发中')
}

// 查看药品详情
const viewDrugDetail = (drugId: string) => {
  router.push(`/drug/detail?id=${drugId}`)
}

// 倒计时逻辑
let countdownTimer: number | null = null
const startCountdown = () => {
  countdownTimer = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      // 倒计时结束，清除定时器
      if (countdownTimer) {
        clearInterval(countdownTimer)
      }
      ElMessage.warning('处方支付时间已过期，请重新申请')
    }
  }, 1000)
}

// 医生信息
const doctorInfo = computed(() => prescriptionStore.doctorInfo)

onMounted(() => {
  // 如果没有处方数据，可能是直接访问该页面，需要返回
  if (!prescription.value) {
    ElMessage.warning('请先完成处方申请流程')
    router.replace('/prescription/apply')
    return
  }
  
  // 启动倒计时
  startCountdown()
})
</script>

<template>
  <div class="prescription-success-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">处方开具成功</span>
      <div class="placeholder"></div>
    </div>

    <!-- 步骤条 -->
    <StepBar :current-step="currentStep" />

    <!-- 成功提示 -->
    <div class="success-banner">
      <div class="success-icon">
        <el-icon><Check /></el-icon>
      </div>
      <div class="success-title">处方开具成功</div>
      <div class="success-desc">医生已为您开具电子处方，请及时购药</div>
      
      <!-- 倒计时 -->
      <div class="countdown-box">
        <el-icon><Clock /></el-icon>
        <span>支付有效期剩余</span>
        <span class="countdown-time">{{ countdownText }}</span>
      </div>
    </div>

    <!-- 医生信息卡片 -->
    <div v-if="doctorInfo" class="doctor-card">
      <div class="doctor-header">
        <div class="doctor-avatar">
          <img v-if="doctorInfo.avatar" :src="doctorInfo.avatar" :alt="doctorInfo.name">
          <el-icon v-else><UserFilled /></el-icon>
        </div>
        <div class="doctor-info">
          <div class="doctor-name">
            {{ doctorInfo.name }}
            <span class="title-tag">{{ doctorInfo.title }}</span>
          </div>
          <div class="doctor-hospital">{{ doctorInfo.hospital }} · {{ doctorInfo.department }}</div>
        </div>
      </div>
      <div class="doctor-actions">
        <button class="action-btn" @click="sharePrescription">
          <el-icon><Share /></el-icon>
          <span>分享</span>
        </button>
        <button class="action-btn" @click="downloadPrescription">
          <el-icon><Download /></el-icon>
          <span>下载</span>
        </button>
      </div>
    </div>

    <!-- 电子处方卡片 -->
    <div v-if="prescription" class="prescription-section">
      <PrescriptionCard
        :prescription="prescription"
        @buy="goToPay"
        @download="downloadPrescription"
      />
    </div>

    <!-- 用药提醒 -->
    <div class="reminder-section">
      <div class="reminder-title">
        <el-icon><Warning /></el-icon>
        <span>用药提醒</span>
      </div>
      <ul class="reminder-list">
        <li>请严格按照医嘱用药，不要自行增减剂量</li>
        <li>处方药需凭处方购买，请妥善保管电子处方</li>
        <li>如出现不良反应，请立即停药并就医</li>
        <li>药品一经售出，非质量问题不予退换</li>
      </ul>
    </div>

    <!-- 底部占位 -->
    <div class="bottom-placeholder"></div>

    <!-- 底部支付栏 -->
    <div class="bottom-bar">
      <div class="price-info">
        <span class="price-label">合计金额</span>
        <span class="price-value">¥{{ prescription?.totalAmount.toFixed(2) || '0.00' }}</span>
      </div>
      <button class="pay-btn" @click="goToPay">
        <span>立即支付</span>
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.prescription-success-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 100px;
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

  .title {
    font-size: $font-lg;
    font-weight: 600;
    color: #fff;
  }

  .placeholder {
    width: 36px;
  }
}

// 成功提示横幅
.success-banner {
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  padding: 24px 16px 32px;
  text-align: center;
  color: #fff;

  .success-icon {
    width: 64px;
    height: 64px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

    .el-icon {
      font-size: 32px;
      color: $success;
    }
  }

  .success-title {
    font-size: $font-xl;
    font-weight: 700;
    margin-bottom: 8px;
  }

  .success-desc {
    font-size: $font-md;
    opacity: 0.9;
    margin-bottom: 20px;
  }

  .countdown-box {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: rgba(255, 255, 255, 0.3);
    padding: 8px 16px;
    border-radius: 20px;
    font-size: $font-sm;

    .el-icon {
      font-size: 14px;
    }

    .countdown-time {
      font-weight: 700;
      font-size: $font-lg;
      color: #fff;
    }
  }
}

// 医生信息卡片
.doctor-card {
  margin: -16px 16px 16px;
  background: $bg-white;
  border-radius: $radius-lg;
  padding: $spacing-md;
  box-shadow: $shadow-lg;
  position: relative;
  z-index: 10;

  .doctor-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .doctor-avatar {
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      overflow: hidden;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .el-icon {
        font-size: 28px;
      }
    }

    .doctor-info {
      flex: 1;

      .doctor-name {
        font-size: $font-lg;
        font-weight: 600;
        color: #333;
        margin-bottom: 6px;
        display: flex;
        align-items: center;
        gap: 8px;

        .title-tag {
          font-size: 12px;
          color: $primary;
          background: rgba($primary, 0.15);
          padding: 2px 8px;
          border-radius: 4px;
          font-weight: 500;
        }
      }

      .doctor-hospital {
        font-size: $font-sm;
        color: $text-secondary;
      }
    }
  }

  .doctor-actions {
    display: flex;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid $border-light;

    .action-btn {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      padding: 10px;
      background: $bg-primary;
      border: none;
      border-radius: 8px;
      font-size: $font-md;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: rgba($primary, 0.1);
        color: $primary;
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }
}

// 处方区域
.prescription-section {
  padding: 0 16px;
  margin-bottom: 16px;
}

// 用药提醒
.reminder-section {
  margin: 0 16px;
  background: rgba($warning, 0.1);
  border-radius: $radius-lg;
  padding: $spacing-md;
  border: 1px solid rgba($warning, 0.2);

  .reminder-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: $font-md;
    font-weight: 600;
    color: $warning;
    margin-bottom: 12px;

    .el-icon {
      font-size: 18px;
    }
  }

  .reminder-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: $font-sm;
      color: $text-secondary;
      padding: 6px 0;
      padding-left: 16px;
      position: relative;
      line-height: 1.5;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 12px;
        width: 6px;
        height: 6px;
        background: $warning;
        border-radius: 50%;
      }

      &:not(:last-child) {
        border-bottom: 1px dashed rgba($warning, 0.2);
      }
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 20px;
}

// 底部支付栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-bottom: calc($safe-area-bottom + $spacing-md);
  background: $bg-white;
  box-shadow: $shadow-lg;
  z-index: 100;

  .price-info {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .price-label {
      font-size: $font-md;
      color: $text-secondary;
    }

    .price-value {
      font-size: $font-title;
      font-weight: 700;
      color: $price-red;
    }
  }

  .pay-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 12px 28px;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    border: none;
    border-radius: 24px;
    font-size: $font-lg;
    font-weight: 600;
    color: #fff;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba($primary, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba($primary, 0.4);
    }

    .el-icon {
      font-size: 18px;
    }
  }
}

// 响应式适配
@media (max-width: 375px) {
  .success-banner {
    padding: 20px 16px 28px;

    .success-icon {
      width: 56px;
      height: 56px;

      .el-icon {
        font-size: 28px;
      }
    }

    .success-title {
      font-size: 18px;
    }

    .success-desc {
      font-size: 13px;
    }
  }

  .doctor-card {
    margin: -12px 12px 12px;
    padding: 14px;

    .doctor-header {
      .doctor-avatar {
        width: 48px;
        height: 48px;

        .el-icon {
          font-size: 24px;
        }
      }

      .doctor-info {
        .doctor-name {
          font-size: 15px;
        }
      }
    }
  }

  .prescription-section {
    padding: 0 12px;
  }

  .reminder-section {
    margin: 0 12px;
    padding: 14px;
  }

  .bottom-bar {
    .price-info {
      .price-value {
        font-size: 20px;
      }
    }

    .pay-btn {
      padding: 10px 24px;
      font-size: 15px;
    }
  }
}
</style>
