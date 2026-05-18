<template>
  <div class="error-404-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-title">页面未找到</div>
      <div class="error-desc">抱歉，您访问的页面不存在或已被移除</div>
      <button class="back-btn" @click="goHome">返回首页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ROUTES } from '@/constants/routes'

const router = useRouter()

const goHome = () => {
  router.push(ROUTES.HOME)
}
</script>

<style scoped lang="scss">
.error-404-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-content {
  text-align: center;
  padding: 40px;

  .error-code {
    font-size: 80px;
    font-weight: 700;
    color: #00B578;
    margin-bottom: 16px;
  }

  .error-title {
    font-size: 20px;
    font-weight: 500;
    color: #333;
    margin-bottom: 8px;
  }

  .error-desc {
    font-size: 14px;
    color: #999;
    margin-bottom: 32px;
  }

  .back-btn {
    background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
    color: #fff;
    border: none;
    padding: 12px 40px;
    border-radius: 24px;
    font-size: 15px;
    cursor: pointer;
  }
}
</style>
