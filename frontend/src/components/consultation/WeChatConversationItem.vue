<template>
  <div class="wechat-conversation-item" @click="$emit('click', consultation)">
    <img :src="consultation.doctorAvatar || defaultAvatar" :alt="consultation.doctorName" class="avatar" />
    <div class="conversation-content">
      <div class="content-header">
        <span class="doctor-name">{{ consultation.doctorName }}</span>
        <span class="message-time">{{ formatTime(consultation.createTime) }}</span>
      </div>
      <div class="meta-row">
        <span class="doctor-meta">{{ consultation.doctorTitle }} · {{ consultation.department }}</span>
      </div>
      <div class="last-message">
        {{ getLastMessagePreview(consultation) }}
      </div>
    </div>
    <div :class="['status-badge', getStatusClass(consultation.status)]">
      {{ getStatusText(consultation.status) }}
    </div>
  </div>
</template>

<script setup lang="ts">
type ConsultationStatus = 'pending' | 'processing' | 'completed' | 'closed'

interface Consultation {
  id: string
  doctorName: string
  doctorAvatar?: string
  doctorTitle: string
  department: string
  symptom?: string
  status: ConsultationStatus
  createTime: string
}

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjQiIGN5PSIyNCIgcj0iMjQiIGZpbGw9IiNGNUY1RjUiLz4KPHBhdGggZD0iTTI0IDI0YzUuMyAwIDkuNi00LjMgOS42LTkuNlMzMC4zIDE0LjQgMjQgMTQuNHMtOS42IDQuMy05LjYgOS42UzE4LjcgMjQgMjQgMjR6IiBmaWxsPSIjREREIi8+CjxjaXJjbGUgY3g9IjI0IiBjeT0iMzQiIHI9IjciIGZpbGw9IiNEREIvPgo8L3N2Zz4='

defineProps<{
  consultation: Consultation
}>()

defineEmits<{
  (e: 'click', consultation: Consultation): void
}>()

function formatTime(timeStr: string): string {
  if (!timeStr) return ''

  try {
    const date = new Date(timeStr)
    const now = new Date()
    const diff = now.getTime() - date.getTime()

    if (diff < 60 * 1000) return '刚刚'
    if (diff < 60 * 60 * 1000) return `${Math.floor(diff / (60 * 1000))}分钟前`
    if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / (60 * 60 * 1000))}小时前`

    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}-${day}`
  } catch {
    return timeStr
  }
}

function getLastMessagePreview(item: Consultation): string {
  if (item.symptom) {
    return item.symptom.length > 30 ? item.symptom.substring(0, 30) + '...' : item.symptom
  }
  return '暂无消息'
}

function getStatusText(status: ConsultationStatus): string {
  const map: Record<ConsultationStatus, string> = {
    pending: '待接诊',
    processing: '进行中',
    completed: '已完成',
    closed: '已取消'
  }
  return map[status] || status
}

function getStatusClass(status: ConsultationStatus): string {
  const map: Record<ConsultationStatus, string> = {
    pending: 'status-pending',
    processing: 'status-processing',
    completed: 'status-completed',
    closed: 'status-closed'
  }
  return map[status] || ''
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.wechat-conversation-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background-color: $bg-white;
  cursor: pointer;
  transition: background-color 0.15s ease;
  position: relative;

  &:active {
    background-color: #F5F5F5;
  }

  .avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    background-color: #F5F5F5;
    flex-shrink: 0;
  }

  .conversation-content {
    flex: 1;
    min-width: 0;
    margin-left: 12px;

    .content-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 4px;

      .doctor-name {
        font-size: 16px;
        font-weight: 500;
        color: $text-primary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 180px;
      }

      .message-time {
        font-size: 12px;
        color: $text-tertiary;
        flex-shrink: 0;
        margin-left: 12px;
      }
    }

    .meta-row {
      margin-bottom: 4px;

      .doctor-meta {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .last-message {
      font-size: 14px;
      color: $text-tertiary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .status-badge {
    margin-left: 12px;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 500;
    white-space: nowrap;
    flex-shrink: 0;

    &.status-processing {
      background-color: rgba(24, 144, 255, 0.1);
      color: #1890FF;
    }

    &.status-pending {
      background-color: rgba($warning, 0.1);
      color: $warning;
    }

    &.status-completed,
    &.status-closed {
      background-color: rgba($text-tertiary, 0.1);
      color: $text-tertiary;
    }
  }
}
</style>
