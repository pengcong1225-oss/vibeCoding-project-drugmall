<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, SuccessFilled } from '@element-plus/icons-vue'

const isOnline = ref(navigator.onLine)
const showBanner = ref(false)
let hideTimer: ReturnType<typeof setTimeout> | null = null

const updateOnlineStatus = () => {
  const wasOffline = !isOnline.value
  isOnline.value = navigator.onLine

  if (isOnline.value && wasOffline) {
    // 网络恢复
    showBanner.value = true
    ElMessage.success('网络已恢复')
    if (hideTimer) clearTimeout(hideTimer)
    hideTimer = setTimeout(() => {
      showBanner.value = false
    }, 3000)
  } else if (!isOnline.value) {
    // 网络断开
    showBanner.value = true
    ElMessage.warning('网络已断开，请检查网络连接')
  }
}

onMounted(() => {
  window.addEventListener('online', updateOnlineStatus)
  window.addEventListener('offline', updateOnlineStatus)
})

onUnmounted(() => {
  window.removeEventListener('online', updateOnlineStatus)
  window.removeEventListener('offline', updateOnlineStatus)
  if (hideTimer) clearTimeout(hideTimer)
})
</script>

<template>
  <transition name="slide-down">
    <div v-show="showBanner" class="network-status-banner" :class="{ online: isOnline, offline: !isOnline }">
      <el-icon class="status-icon">
        <SuccessFilled v-if="isOnline" />
        <Warning v-else />
      </el-icon>
      <span class="status-text">
        {{ isOnline ? '网络已恢复' : '网络已断开，请检查网络连接' }}
      </span>
    </div>
  </transition>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.network-status-banner {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  padding: $spacing-sm;
  z-index: 9999;
  transition: all 0.3s ease;

  &.offline {
    background: rgba($error, 0.95);
    color: $text-white;
  }

  &.online {
    background: rgba($success, 0.95);
    color: $text-white;
  }

  .status-icon {
    font-size: 16px;
  }

  .status-text {
    font-size: $font-sm;
    font-weight: 500;
  }
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}
</style>
