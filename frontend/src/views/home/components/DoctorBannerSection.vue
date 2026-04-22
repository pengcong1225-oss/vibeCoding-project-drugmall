<template>
  <div class="doctor-banner" @click="$emit('click')">
    <div class="card-content">
      <div class="left-section">
        <div class="avatar-wrapper">
          <img 
            src="https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=100&h=100&fit=crop&crop=face" 
            class="doctor-avatar"
            alt="医生"
          />
          <span class="hospital-badge">三甲</span>
        </div>
        <div class="text-info">
          <div class="title-row">
            <span class="title">秒问医生</span>
            <span class="hot-badge">
              <el-icon><Lightning /></el-icon>
              9秒接诊
            </span>
          </div>
          <div class="desc-text">三甲医生 · 24小时 · 问诊开药</div>
        </div>
      </div>
      <button class="consult-btn" @click.stop="$emit('click')">
        去问诊
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { Lightning, ArrowRight } from '@element-plus/icons-vue'
import type { HomeSection } from '@/types/home'

defineProps<{ section: HomeSection }>()
defineEmits<{
  (e: 'click'): void
}>()
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.doctor-banner {
  margin: 0 12px 10px;
  padding: 14px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.98);
  }

  .card-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .left-section {
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar-wrapper {
      position: relative;
      width: 56px;
      height: 56px;
      flex-shrink: 0;

      .doctor-avatar {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #E8F5F0;
      }

      .hospital-badge {
        position: absolute;
        bottom: -2px;
        left: 50%;
        transform: translateX(-50%);
        background: $primary;
        color: #fff;
        padding: 1px 6px;
        border-radius: 8px;
        font-size: 10px;
        font-weight: 600;
        white-space: nowrap;
      }
    }

    .text-info {
      .title-row {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 6px;

        .title {
          font-size: 18px;
          font-weight: 700;
          color: $text-primary;
        }

        .hot-badge {
          display: inline-flex;
          align-items: center;
          gap: 2px;
          padding: 2px 8px;
          background: linear-gradient(135deg, $warning 0%, lighten($warning, 10%) 100%);
          border-radius: 10px;
          font-size: 11px;
          font-weight: 600;
          color: #fff;

          .el-icon {
            font-size: 12px;
          }
        }
      }

      .desc-text {
        font-size: 13px;
        color: $text-tertiary;
      }
    }
  }

  .consult-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 10px 18px;
    background: $primary;
    color: white;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;
    flex-shrink: 0;

    .el-icon {
      font-size: 14px;
    }

    &:active {
      background: $primary-dark;
      transform: scale(0.95);
    }
  }

  // 症状提示文字
  .hint-bar {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px dashed rgba($primary, 0.2);

    .quote {
      font-size: 14px;
      color: $primary;
      opacity: 0.5;
    }

    .hint-text {
      font-size: 13px;
      color: $primary;
      line-height: 1.5;
    }
  }
}
</style>
