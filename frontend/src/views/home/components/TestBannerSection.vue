<template>
  <div class="test-banner-section">
    <div class="banner-background">
      <img src="/images/jc01.png" alt="检测服务" class="banner-image" />
      <div class="banner-overlay"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 单张图片背景展示，无需额外逻辑
</script>

<style scoped lang="scss">
.test-banner-section {
  width: 100%;
  position: relative;
  overflow: hidden;
  height: 160px;

  .banner-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;

    .banner-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      object-position: center top;
      display: block;
    }

    .banner-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(180deg, 
        rgba(255, 217, 61, 0.3) 0%, 
        rgba(255, 217, 61, 0.1) 50%,
        transparent 100%
      );
    }
  }
}
</style>