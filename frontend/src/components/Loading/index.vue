<script setup lang="ts">
interface Props {
  visible?: boolean
  text?: string
  fullscreen?: boolean
  background?: string
}

withDefaults(defineProps<Props>(), {
  visible: false,
  text: '加载中...',
  fullscreen: false,
  background: 'rgba(255, 255, 255, 0.9)'
})
</script>

<template>
  <teleport to="body">
    <transition name="loading-fade">
      <div
        v-show="visible"
        class="loading-wrapper"
        :class="{ 'is-fullscreen': fullscreen }"
        :style="{ background }"
      >
        <div class="loading-content">
          <div class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>
          <p v-if="text" class="loading-text">{{ text }}</p>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.loading-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
  
  &.is-fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
  }
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-md;
}

.loading-spinner {
  position: relative;
  width: 50px;
  height: 50px;
  
  .spinner-ring {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 3px solid transparent;
    border-top-color: $primary;
    animation: spin 1s linear infinite;
    
    &:nth-child(1) {
      animation-duration: 1s;
      border-top-color: $primary;
    }
    
    &:nth-child(2) {
      animation-duration: 0.8s;
      border-top-color: rgba($primary, 0.6);
      transform: scale(0.8);
    }
    
    &:nth-child(3) {
      animation-duration: 0.6s;
      border-top-color: rgba($primary, 0.3);
      transform: scale(0.6);
    }
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-md;
  color: $text-secondary;
  margin: 0;
}

// 过渡动画
.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
