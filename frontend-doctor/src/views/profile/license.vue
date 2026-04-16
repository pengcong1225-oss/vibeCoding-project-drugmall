<template>
  <div class="license-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">执业信息</h1>
      <div class="header-right"></div>
    </header>

    <!-- 认证状态 -->
    <div class="status-banner">
      <div class="status-icon">✓</div>
      <div class="status-text">已通过实名认证</div>
      <div class="status-sub">您的执业信息已审核通过</div>
    </div>

    <!-- 执业信息 -->
    <div class="info-section">
      <div class="section-title">基本信息</div>
      <div class="info-list">
        <div class="info-item">
          <span class="label">真实姓名</span>
          <span class="value">张三</span>
        </div>
        <div class="info-item">
          <span class="label">身份证号</span>
          <span class="value">110101********1234</span>
        </div>
        <div class="info-item">
          <span class="label">医师资格证号</span>
          <span class="value">201911010101234</span>
        </div>
        <div class="info-item">
          <span class="label">执业证书编号</span>
          <span class="value">110101123456789</span>
        </div>
      </div>
    </div>

    <div class="info-section">
      <div class="section-title">执业信息</div>
      <div class="info-list">
        <div class="info-item">
          <span class="label">执业医院</span>
          <span class="value">北京协和医院</span>
        </div>
        <div class="info-item">
          <span class="label">执业科室</span>
          <span class="value">心内科</span>
        </div>
        <div class="info-item">
          <span class="label">职称</span>
          <span class="value">副主任医师</span>
        </div>
        <div class="info-item">
          <span class="label">执业范围</span>
          <span class="value">内科专业</span>
        </div>
        <div class="info-item">
          <span class="label">执业地点</span>
          <span class="value">北京市东城区</span>
        </div>
      </div>
    </div>

    <!-- 证件照片 -->
    <div class="photo-section">
      <div class="section-title">证件照片</div>
      <div class="photo-grid">
        <div class="photo-item">
          <div class="photo-placeholder">
            <span>身份证正面</span>
          </div>
        </div>
        <div class="photo-item">
          <div class="photo-placeholder">
            <span>身份证反面</span>
          </div>
        </div>
        <div class="photo-item">
          <div class="photo-placeholder">
            <span>医师资格证</span>
          </div>
        </div>
        <div class="photo-item">
          <div class="photo-placeholder">
            <span>执业证书</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="tips-section">
      <div class="tips-title">温馨提示</div>
      <ul class="tips-list">
        <li>执业信息已通过审核，如需修改请联系客服</li>
        <li>请确保执业信息真实有效，虚假信息将被封号</li>
        <li>执业证书到期前30天会收到提醒</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.license-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  margin-bottom: 12px;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: #333;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: #f5f5f5;
    }

    svg {
      width: 24px;
      height: 24px;
    }
  }

  .page-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }

  .header-right {
    width: 40px;
  }
}

.status-banner {
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: #fff;

  .status-icon {
    width: 48px;
    height: 48px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin: 0 auto 12px;
  }

  .status-text {
    font-size: 18px;
    font-weight: 500;
    margin-bottom: 4px;
  }

  .status-sub {
    font-size: 13px;
    opacity: 0.9;
  }
}

.info-section {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 16px;

  .section-title {
    font-size: 15px;
    font-weight: 500;
    color: #333;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f5f5f5;
  }

  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .label {
        font-size: 14px;
        color: #666;
      }

      .value {
        font-size: 14px;
        color: #333;
      }
    }
  }
}

.photo-section {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 16px;

  .section-title {
    font-size: 15px;
    font-weight: 500;
    color: #333;
    margin-bottom: 16px;
  }

  .photo-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;

    .photo-item {
      .photo-placeholder {
        aspect-ratio: 3/2;
        background: #f5f5f5;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px dashed #ddd;

        span {
          font-size: 13px;
          color: #999;
        }
      }
    }
  }
}

.tips-section {
  background: #fff;
  margin: 0 12px;
  border-radius: 12px;
  padding: 16px;

  .tips-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-bottom: 12px;
  }

  .tips-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: 13px;
      color: #666;
      margin-bottom: 8px;
      padding-left: 16px;
      position: relative;
      line-height: 1.5;

      &::before {
        content: '•';
        position: absolute;
        left: 0;
        color: #00B578;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
