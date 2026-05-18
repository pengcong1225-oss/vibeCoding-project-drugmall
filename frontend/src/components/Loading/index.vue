<script setup lang="ts">
interface Props {
  visible?: boolean
  text?: string
  fullscreen?: boolean
  background?: string
  type?: 'spinner' | 'skeleton' | 'dots'
}

withDefaults(defineProps<Props>(), {
  visible: false,
  text: '加载中...',
  fullscreen: false,
  background: 'rgba(255, 255, 255, 0.9)',
  type: 'spinner'
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
          <!-- 环形加载器 -->
          <div v-if="type === 'spinner'" class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>

          <!-- 点状加载器 -->
          <div v-else-if="type === 'dots'" class="loading-dots">
            <div class="dot"></div>
            <div class="dot"></div>
            <div class="dot"></div>
          </div>

          <!-- 骨架屏 -->
          <div v-else-if="type === 'skeleton'" class="loading-skeleton">
            <div class="skeleton-header"></div>
            <div class="skeleton-content">
              <div class="skeleton-line"></div>
              <div class="skeleton-line short"></div>
              <div class="skeleton-line"></div>
            </div>
          </div>

          <p v-if="text && type !== 'skeleton'" class="loading-text">{{ text }}</p>
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

// 环形加载器
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

// 点状加载器
.loading-dots {
  display: flex;
  gap: 8px;

  .dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: $primary;
    animation: dotBounce 1.4s ease-in-out infinite;

    &:nth-child(1) {
      animation-delay: 0s;
    }

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

// 骨架屏
.loading-skeleton {
  width: 200px;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  .skeleton-header {
    width: 60%;
    height: 20px;
    background: linear-gradient(90deg, $bg-secondary 25%, $bg-primary 50%, $bg-secondary 75%);
    background-size: 200% 100%;
    border-radius: $radius-sm;
    animation: skeletonShimmer 1.5s infinite;
  }

  .skeleton-content {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;

    .skeleton-line {
      height: 14px;
      background: linear-gradient(90deg, $bg-secondary 25%, $bg-primary 50%, $bg-secondary 75%);
      background-size: 200% 100%;
      border-radius: $radius-sm;
      animation: skeletonShimmer 1.5s infinite;

      &.short {
        width: 70%;
      }
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

@keyframes dotBounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes skeletonShimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
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
