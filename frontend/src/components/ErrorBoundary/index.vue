<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'
import { RefreshRight, WarningFilled } from '@element-plus/icons-vue'

const hasError = ref(false)
const errorInfo = ref<Error | null>(null)

onErrorCaptured((err) => {
  hasError.value = true
  errorInfo.value = err
  console.error('组件错误捕获:', err)
  return false
})

const reload = () => {
  window.location.reload()
}
</script>

<template>
  <div v-if="hasError" class="error-boundary">
    <div class="error-content">
      <el-icon class="error-icon"><WarningFilled /></el-icon>
      <h3 class="error-title">页面出错了</h3>
      <p class="error-desc">抱歉，页面加载出现问题</p>
      <button class="reload-btn" @click="reload">
        <el-icon><RefreshRight /></el-icon>
        <span>重新加载</span>
      </button>
    </div>
  </div>
  <slot v-else />
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.error-boundary {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  padding: $spacing-xl;

  .error-content {
    text-align: center;

    .error-icon {
      font-size: 64px;
      color: $warning;
      margin-bottom: $spacing-lg;
    }

    .error-title {
      font-size: $font-xl;
      color: $text-primary;
      margin-bottom: $spacing-sm;
      font-weight: 600;
    }

    .error-desc {
      font-size: $font-md;
      color: $text-secondary;
      margin-bottom: $spacing-xl;
    }

    .reload-btn {
      display: inline-flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm $spacing-xl;
      background: $primary;
      color: $text-white;
      border: none;
      border-radius: $radius-lg;
      font-size: $font-md;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        opacity: 0.9;
        transform: translateY(-1px);
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }
}
</style>
