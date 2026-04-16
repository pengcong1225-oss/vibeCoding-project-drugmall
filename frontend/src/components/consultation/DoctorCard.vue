<template>
  <div class="doctor-card" @click="goToDetail">
    <div class="card-main">
      <div class="avatar-section">
        <img :src="doctor.avatar || defaultAvatar" :alt="doctor.name" class="avatar" />
        <span v-if="doctor.isOnline" class="online-badge">在线</span>
      </div>
      <div class="info-section">
        <div class="name-row">
          <span class="name">{{ doctor.name }}</span>
          <span class="title">{{ doctor.title }}</span>
          <span class="dept">{{ doctor.department }}</span>
        </div>
        <div class="hospital-row">
          <span class="badge">三甲</span>
          <span class="hospital">{{ doctor.hospital }}</span>
        </div>
        <div v-if="doctor.canPrescribe" class="prescribe-tag">
          <el-icon><FirstAidKit /></el-icon>
          可开方
        </div>
        <div class="specialty">擅长：{{ truncate(doctor.specialty, 40) }}</div>
        <div class="stats-row">
          <span>好评率{{ formatRating(doctor.rating) }}%</span>
          <span class="divider">|</span>
          <span>接诊量{{ doctor.consultCount || '1.1万' }}</span>
          <span class="divider">|</span>
          <span>平均等待{{ doctor.waitTime || 12 }}分钟</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { FirstAidKit } from '@element-plus/icons-vue'

interface DoctorInfo {
  id: string
  name: string
  title: string
  department: string
  hospital: string
  avatar?: string
  specialty: string
  rating: number
  isOnline: boolean
  canPrescribe?: boolean
  waitTime?: number
  price?: number
  consultCount?: string
}

const defaultAvatar = 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face'

const props = defineProps<{
  doctor: DoctorInfo
}>()

const emit = defineEmits<{
  (e: 'click', doctor: DoctorInfo): void
}>()

const router = useRouter()

function truncate(text: string, maxLen: number): string {
  if (!text) return ''
  return text.length > maxLen ? text.substring(0, maxLen) + '...' : text
}

function formatRating(rating: number): string {
  return (rating * 100).toFixed(0)
}

function goToDetail() {
  emit('click', props.doctor)
  router.push({
    path: `/doctor/${props.doctor.id}`,
    query: {
      name: props.doctor.name,
      department: props.doctor.department
    }
  })
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/inquiry-theme' as *;

.doctor-card {
  background-color: $inquiry-card-bg;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.06);

  &:active {
    transform: scale(0.985);
    background-color: #FAFAFA;
  }

  .card-main {
    display: flex;
    gap: 14px;
  }

  .avatar-section {
    position: relative;
    flex-shrink: 0;

    .avatar {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      object-fit: cover;
      background-color: $inquiry-bg;
    }

    .online-badge {
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      background: $inquiry-online;
      color: white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 8px;
      white-space: nowrap;
    }
  }

  .info-section {
    flex: 1;
    min-width: 0;

    .name-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 6px;
      flex-wrap: wrap;

      .name {
        font-size: 16px;
        font-weight: 600;
        color: $inquiry-text-primary;
      }

      .title {
        padding: 2px 8px;
        background-color: rgba($inquiry-primary, 0.1);
        color: $inquiry-primary;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }

      .dept {
        font-size: 12px;
        color: $inquiry-text-secondary;
      }
    }

    .hospital-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 6px;

      .badge {
        padding: 1px 6px;
        background-color: rgba($inquiry-primary, 0.1);
        color: $inquiry-primary;
        border-radius: 4px;
        font-size: 10px;
        font-weight: 600;
      }

      .hospital {
        font-size: 12px;
        color: $inquiry-text-secondary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .prescribe-tag {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 2px 8px;
      background-color: rgba($inquiry-primary, 0.08);
      color: $inquiry-primary-light;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 500;
      margin-bottom: 6px;

      .el-icon {
        color: $inquiry-primary;
        font-size: 12px;
      }
    }

    .specialty {
      font-size: 12px;
      color: $inquiry-text-secondary;
      line-height: 1.5;
      margin-bottom: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .stats-row {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      color: $inquiry-text-tertiary;

      .divider {
        color: #e0e0e0;
      }
    }
  }
}
</style>
