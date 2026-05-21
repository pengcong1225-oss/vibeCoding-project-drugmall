<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowUp } from '@element-plus/icons-vue'

const visible = ref(false)
let scrollContainer: HTMLElement | Window = window

const checkScroll = () => {
  const scrollTop = scrollContainer instanceof Window 
    ? scrollContainer.scrollY 
    : scrollContainer.scrollTop
  visible.value = scrollTop > 300
}

const scrollToTop = () => {
  if (scrollContainer instanceof Window) {
    scrollContainer.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    scrollContainer.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

onMounted(() => {
  // 尝试找到可滚动的父容器
  const mainContent = document.querySelector('.main-content')
  if (mainContent) {
    scrollContainer = mainContent as HTMLElement
  }
  scrollContainer.addEventListener('scroll', checkScroll)
  checkScroll()
})

onUnmounted(() => {
  scrollContainer.removeEventListener('scroll', checkScroll)
})
</script>

<template>
  <transition name="fade-scale">
    <button
      v-show="visible"
      class="scroll-to-top"
      @click="scrollToTop"
      aria-label="回到顶部"
    >
      <el-icon><ArrowUp /></el-icon>
    </button>
  </transition>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.scroll-to-top {
  position: fixed;
  right: 20px;
  bottom: calc(80px + $safe-area-bottom);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: $bg-white;
  border: 1px solid $border-light;
  box-shadow: $shadow-md;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 999;
  transition: all 0.3s ease;
  color: $text-secondary;

  &:hover {
    background: $primary;
    color: $text-white;
    border-color: $primary;
    transform: translateY(-2px);
  }

  &:active {
    transform: scale(0.95);
  }

  .el-icon {
    font-size: 20px;
  }
}

.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.8);
}
</style>
