<script setup lang="ts">
interface Props {
  position: 'top' | 'bottom' | 'both'
  extraHeight?: number
  bgColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  position: 'bottom',
  extraHeight: 0,
  bgColor: 'transparent'
})
</script>

<template>
  <div 
    v-if="position === 'top' || position === 'both'"
    class="safe-area-top"
    :style="{ 
      backgroundColor: bgColor,
      height: `calc(env(safe-area-inset-top) + ${extraHeight}px)`
    }"
  />
  
  <div 
    v-if="position === 'bottom' || position === 'both'"
    class="safe-area-bottom"
    :style="{ 
      backgroundColor: bgColor,
      height: `calc(env(safe-area-inset-bottom) + ${extraHeight}px)`
    }"
  />
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.safe-area-top,
.safe-area-bottom {
  flex-shrink: 0;
  width: 100%;
}

.safe-area-top {
  position: sticky;
  top: 0;
  z-index: 100;
}

.safe-area-bottom {
  // 用于占位，防止内容被底部导航栏遮挡
}

// 兼容不支持 env() 的浏览器
@supports not (height: env(safe-area-inset-top)) {
  .safe-area-top {
    height: v-bind("position === 'top' || position === 'both' ? '20px' : '0'") !important;
  }
  
  .safe-area-bottom {
    height: v-bind("position === 'bottom' || position === 'both' ? '34px' : '0'") !important;
  }
}
</style>
