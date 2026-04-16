<template>
  <div :class="['wechat-bubble-wrapper', direction]">
    <!-- 对方消息：显示头像 -->
    <img v-if="direction === 'left'" :src="avatar || defaultAvatar" :alt="senderName" class="bubble-avatar" />

    <div :class="['bubble-container', direction]">
      <!-- 气泡主体 -->
      <div :class="['bubble', `bubble-${direction}`, { 'with-card': message.type === 'prescription' }]">
        <!-- 处方卡片 -->
        <div v-if="message.type === 'prescription'" class="prescription-card">
          <div class="card-header">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M19 3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V5a2 2 0 00-2-2z" stroke="#00C98A" stroke-width="2"/>
              <path d="M12 8v8M8 12h8" stroke="#00C98A" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span class="card-title">电子处方</span>
          </div>
          <div class="card-body" v-html="formatContent(message.content)"></div>
          <button class="card-action" @click.stop="$emit('viewPrescription', message.cardData)">
            查看处方详情
          </button>
        </div>

        <!-- 图片消息 -->
        <img
          v-else-if="message.type === 'image' && message.url"
          :src="message.url"
          alt="图片"
          class="bubble-image"
          @click.stop="$emit('previewImage', message.url)"
        />

        <!-- 文本消息 -->
        <div v-else class="text-content" v-html="formatContent(message.content)"></div>
      </div>

      <!-- 状态指示器（仅自己发送的消息显示） -->
      <div v-if="direction === 'right'" class="message-status">
        <template v-if="message.status === 'read'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M20 6L9 17l-5-5" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style="margin-left: -10px;">
            <path d="M20 6L9 17l-5-5" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </template>
        <template v-else-if="message.status === 'sent'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M20 6L9 17l-5-5" stroke="#CCCCCC" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </template>
        <template v-else-if="message.status === 'failed'">
          <span class="failed-icon">!</span>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Message {
  id: string
  content: string
  type?: 'text' | 'image' | 'prescription'
  url?: string
  status?: 'sent' | 'read' | 'failed'
  cardData?: any
}

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDIiIGhlaWdodD0iNDIiIHZpZXdCb3g9IjAgMCA0MiA0MiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjEiIGN5PSIyMSIgcj0iMjEiIGZpbGw9IiNGNUY1RjUiLz4KPHBhdGggZD0iTTIxIDIxYzQuNiAwIDguOC0zLjUgOC44LTguNFMyNS42IDUuNiAyMSA1LjZTMTIuMiA5LjEgMTIuMiAxNC4xczQuMiA4LjQgOC44IDguNHoiIGZpbGw9IiNEREQiLz4KPGNpcmNsZSBjeD0iMjEiIGN5PSIzMCIgcj0iNiIgZmlsbD0iI0RERCIvPgo8L3N2Zz4='

defineProps<{
  message: Message
  direction: 'left' | 'right'
  avatar?: string
  senderName?: string
}>()

defineEmits<{
  (e: 'previewImage', url: string): void
  (e: 'viewPrescription', data?: any): void
}>()

function formatContent(content: string): string {
  if (!content) return ''
  return content.replace(/\n/g, '<br/>')
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.wechat-bubble-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 16px;
  max-width: 75%;

  &.left {
    align-self: flex-start;
  }

  &.right {
    align-self: flex-end;
    flex-direction: row-reverse;
  }

  .bubble-avatar {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    object-fit: cover;
    background-color: #F5F5F5;
    flex-shrink: 0;
  }

  .bubble-container {
    display: flex;
    flex-direction: column;
    gap: 4px;

    &.right {
      align-items: flex-end;
    }
  }

  .bubble {
    padding: 10px 14px;
    border-radius: 8px;
    word-break: break-word;
    line-height: 1.6;
    position: relative;
    box-shadow: none;
    transition: all 0.15s ease;

    // 自己发送的消息 - 微信绿色气泡
    &.bubble-right {
      background-color: #95EC69;
      color: #000000;
      border-radius: 8px 2px 8px 8px;
      max-width: 100%;
    }

    // 对方发送的消息 - 白色气泡
    &.bubble-left {
      background-color: $bg-white;
      color: #000000;
      border-radius: 2px 8px 8px 8px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      max-width: 100%;

      &.with-card {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }
    }

    .text-content {
      font-size: 15px;
      line-height: 1.6;
    }

    .bubble-image {
      max-width: 200px;
      max-height: 240px;
      border-radius: 6px;
      object-fit: cover;
      cursor: pointer;
      display: block;

      &:active {
        opacity: 0.92;
      }
    }
  }

  // 处方卡片样式
  .prescription-card {
    min-width: 220px;

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 14px;
      background-color: rgba($primary, 0.06);
      border-bottom: 1px solid rgba($primary, 0.1);

      .card-title {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .card-body {
      padding: 12px 14px;
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.7;
    }

    .card-action {
      width: 100%;
      padding: 10px 14px;
      background-color: rgba($primary, 0.08);
      color: $primary;
      font-size: 13px;
      font-weight: 600;
      border: none;
      border-top: 1px solid rgba($primary, 0.08);
      cursor: pointer;
      text-align: left;
      transition: all 0.2s;

      &:active {
        background-color: rgba($primary, 0.15);
      }
    }
  }

  // 消息状态
  .message-status {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 4px;
    height: 16px;

    svg {
      opacity: 0.7;
    }

    .failed-icon {
      width: 16px;
      height: 16px;
      background-color: $error;
      color: white;
      border-radius: 50%;
      font-size: 11px;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>
