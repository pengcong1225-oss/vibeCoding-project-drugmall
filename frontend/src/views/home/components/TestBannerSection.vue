<template>
  <div class="test-banner-section">
    <div class="banner-image-wrapper">
      <img src="/images/jc01.png" alt="检测服务" class="banner-image" />
    </div>
  </div>
</template>

<script setup lang="ts">
// 单张图片展示，无需额外逻辑
</script>

<style scoped lang="scss">
.test-banner-section {
  margin: 12px 0 0;
  width: 100%;
  overflow: hidden;
  position: relative;

  .banner-image-wrapper {
    width: 100%;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    .banner-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }
}
</style>
