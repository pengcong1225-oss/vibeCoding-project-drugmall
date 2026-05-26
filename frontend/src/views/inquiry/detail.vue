<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ChatDotRound, User, Clock } from '@element-plus/icons-vue'
import { getConsultationDetail } from '@/api/modules/inquiry'
import type { ConsultationDetail } from '@/api/modules/inquiry'
import { ROUTES } from '@/constants/routes'

const route = useRoute()
const router = useRouter()

const consultationId = route.query.consultationId as string
const loading = ref(false)
const consultation = ref<ConsultationDetail | null>(null)

// 获取咨询详情
const fetchDetail = async () => {
  if (!consultationId) {
    ElMessage.error('咨询ID不存在')
    return
  }

  loading.value = true
  try {
    const res = await getConsultationDetail(consultationId)
    consultation.value = res
  } catch (error) {
    console.error('获取咨询详情失败:', error)
    ElMessage.error('获取咨询详情失败')
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 进入聊天
const goToChat = () => {
  if (!consultation.value) return
  
  router.push({
    path: ROUTES.INQUIRY_CHAT,
    query: {
      consultationId: consultation.value.id,
      doctorId: consultation.value.doctorId,
      doctorName: consultation.value.doctorName
    }
  })
}

// 查看患者详情
const goToPatient = () => {
  // TODO: 跳转到患者详情页
  ElMessage.info('患者详情功能开发中')
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待接诊',
    processing: '问诊中',
    completed: '已完成',
    closed: '已关闭',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

// 获取状态样式类
const getStatusClass = (status: string) => {
  return `status-${status}`
}

onMounted(() => {
  fetchDetail()
})
</script>

<template>
  <div class="consultation-detail-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </div>
      <h1 class="page-title">咨询详情</h1>
      <div class="header-right"></div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 内容区域 -->
    <div v-else-if="consultation" class="content-area">
      <!-- 医生信息卡片 -->
      <div class="info-card doctor-card">
        <div class="card-header">
          <div class="header-icon doctor">
            <el-icon :size="18"><User /></el-icon>
          </div>
          <h3 class="header-title">医生信息</h3>
        </div>
        <div class="card-body">
          <div class="doctor-info-row">
            <img 
              :src="consultation.doctorAvatar || 'https://img.icons8.com/color/96/doctor-male.png'" 
              class="doctor-avatar" 
              alt="医生头像"
            />
            <div class="doctor-detail">
              <div class="doctor-name-row">
                <span class="doctor-name">{{ consultation.doctorName }}</span>
                <span class="doctor-title">{{ consultation.doctorTitle }}</span>
              </div>
              <div class="hospital-name">{{ consultation.hospital }} · {{ consultation.department }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 患者信息卡片 -->
      <div class="info-card patient-card" @click="goToPatient">
        <div class="card-header">
          <div class="header-icon patient">
            <el-icon :size="18"><User /></el-icon>
          </div>
          <h3 class="header-title">就诊人信息</h3>
          <el-icon class="arrow-icon"><ArrowLeft /></el-icon>
        </div>
        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <span class="label">姓名</span>
              <span class="value">{{ consultation.patientName }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <span class="value">{{ consultation.patientGender === 'male' ? '男' : '女' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄</span>
              <span class="value">{{ consultation.patientAge }}岁</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 咨询信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <div class="header-icon consultation">
            <el-icon :size="18"><ChatDotRound /></el-icon>
          </div>
          <h3 class="header-title">咨询信息</h3>
        </div>
        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <span class="label">咨询类型</span>
              <span class="value">{{ consultation.type }}</span>
            </div>
            <div class="info-item">
              <span class="label">咨询状态</span>
              <span class="value status" :class="getStatusClass(consultation.status)">
                {{ getStatusText(consultation.status) }}
              </span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item full">
              <span class="label">症状描述</span>
              <span class="value symptom">{{ consultation.symptom }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <span class="label">创建时间</span>
              <span class="value">
                <el-icon :size="14" style="margin-right: 4px"><Clock /></el-icon>
                {{ consultation.createTime }}
              </span>
            </div>
            <div class="info-item" v-if="consultation.price">
              <span class="label">咨询费用</span>
              <span class="value price">¥{{ consultation.price }}</span>
            </div>
          </div>
          <div class="info-row" v-if="consultation.isRx">
            <div class="info-item full">
              <span class="label">处方药申请</span>
              <span class="value">是</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button 
          v-if="consultation.status === 'pending'"
          class="btn btn-primary"
          @click="goToChat"
        >
          去支付
        </button>
        <button 
          v-else-if="consultation.status === 'processing'"
          class="btn btn-primary"
          @click="goToChat"
        >
          继续咨询
        </button>
        <button 
          v-else-if="consultation.status === 'completed'"
          class="btn btn-secondary"
          @click="goToChat"
        >
          查看聊天记录
        </button>
        <button 
          v-else
          class="btn btn-secondary"
          @click="goBack"
        >
          返回列表
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="咨询信息不存在" />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.consultation-detail-page {
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: #fff;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;

  .header-left {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 4px;
    color: $text-primary;
  }

  .page-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }

  .header-right {
    width: 28px;
  }
}

.loading-container {
  padding: 20px 16px;
}

.content-area {
  padding: 12px 16px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  &.patient-card {
    cursor: pointer;

    &:active {
      opacity: 0.8;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 16px 12px;

  .header-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;

    &.doctor {
      background: #E6F7FF;
      color: #1890FF;
    }

    &.patient {
      background: #FFF7E6;
      color: #FA8C16;
    }

    &.consultation {
      background: #F6FFED;
      color: #52C41A;
    }
  }

  .header-title {
    flex: 1;
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
  }

  .arrow-icon {
    width: 16px;
    height: 16px;
    color: $text-tertiary;
    transform: rotate(180deg);
  }
}

.card-body {
  padding: 0 16px 16px;
}

.doctor-info-row {
  display: flex;
  gap: 12px;
  align-items: center;

  .doctor-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
  }

  .doctor-detail {
    flex: 1;

    .doctor-name-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;

      .doctor-name {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }

      .doctor-title {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .hospital-name {
      font-size: 13px;
      color: $text-tertiary;
    }
  }
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-item {
  flex: 1;
  min-width: 100px;

  &.full {
    flex: 0 0 100%;
  }

  .label {
    display: block;
    font-size: 12px;
    color: $text-tertiary;
    margin-bottom: 4px;
  }

  .value {
    font-size: 14px;
    color: $text-primary;
    font-weight: 500;

    &.status {
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      display: inline-block;

      &.status-pending {
        background: #FFF7E6;
        color: #FA8C16;
      }

      &.status-processing {
        background: #E6F7FF;
        color: #1890FF;
      }

      &.status-completed {
        background: #F6FFED;
        color: #52C41A;
      }

      &.status-closed,
      &.status-cancelled {
        background: #F5F5F5;
        color: #999;
      }
    }

    &.symptom {
      font-weight: 400;
      color: $text-secondary;
      line-height: 1.6;
    }

    &.price {
      color: #ff4d4f;
      font-weight: 600;
      font-size: 16px;
    }
  }
}

.action-buttons {
  padding: 16px 0;
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
    padding: 12px 24px;
    border-radius: 24px;
    font-size: 15px;
    font-weight: 500;
    border: none;
    cursor: pointer;
    transition: all 0.2s;

    &.btn-primary {
      background: linear-gradient(135deg, #00C9A7 0%, #00b894 100%);
      color: #fff;

      &:active {
        opacity: 0.9;
        transform: scale(0.98);
      }
    }

    &.btn-secondary {
      background: #f5f5f5;
      color: $text-primary;

      &:active {
        background: #e8e8e8;
      }
    }
  }
}

.empty-state {
  padding: 60px 16px;
}
</style>
