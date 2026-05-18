<template>
  <div class="search-section">
    <!-- 顶部导航栏 - 完全复刻图片：返回按钮 + 标题/地址横向排列 + 购物车 -->
    <div class="top-nav">
      <div class="nav-left">
        <div class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <div class="nav-title">
          <span class="title-text">宜格健康</span>
          <div class="location" @click="goToAddress">
            <el-icon class="location-icon"><Location /></el-icon>
            <span class="location-text">{{ currentLocation }}</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
      <div class="cart-btn" @click="$emit('cartClick')">
        <el-icon><ShoppingCart /></el-icon>
      </div>
    </div>

    <!-- 搜索框和购物车 -->
    <div class="search-box-wrapper">
      <div class="search-box" @click="$emit('searchClick')">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="placeholder">搜索药品、症状或品牌</span>
        <div class="voice-btn" @click.stop="$emit('voiceSearch')">
          <el-icon><Microphone /></el-icon>
        </div>
        <div class="camera-btn" @click.stop="$emit('scanCode')">
          <el-icon><Camera /></el-icon>
        </div>
        <div class="search-btn">搜索</div>
      </div>
      <!-- 吸顶时显示的购物车 -->
      <div class="sticky-cart-btn" @click="$emit('cartClick')">
        <el-icon><ShoppingCart /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Location, ArrowRight, ShoppingCart, Search, Camera, Microphone } from '@element-plus/icons-vue'
import { ROUTES } from '@/constants/routes'
import type { HomeSection } from '@/types/home'

const props = defineProps<{
  section: HomeSection
  activeTab?: string
}>()

const emit = defineEmits<{
  (e: 'locationClick'): void
  (e: 'cartClick'): void
  (e: 'searchClick'): void
  (e: 'scanCode'): void
  (e: 'voiceSearch'): void
}>()

const router = useRouter()
const currentLocation = ref('星光天地')

const goToAddress = () => {
  router.push(ROUTES.ADDRESS)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-yellow: #FFD100;
$primary-teal: #00C9A7;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;

.search-section {
  padding: 6px 12px 4px;
  background: transparent;
  transition: all 0.3s ease;

  .top-nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;

    .nav-left {
      display: flex;
      align-items: center;
      gap: 8px;

      .back-btn {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-primary;
        font-size: 20px;
        cursor: pointer;
      }

      .nav-title {
        display: flex;
        align-items: center;
        gap: 6px;

        .title-text {
          font-size: 17px;
          font-weight: 600;
          color: $text-primary;
        }

        .location {
          display: flex;
          align-items: center;
          gap: 2px;
          font-size: 13px;
          color: $text-secondary;
          cursor: pointer;

          .location-icon {
            font-size: 13px;
          }

          .location-text {
            max-width: 100px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .arrow-icon {
            font-size: 11px;
          }
        }
      }
    }

    .cart-btn {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-primary;
      font-size: 22px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
  }

  .search-box-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;

    .search-box {
      flex: 1;
      display: flex;
      align-items: center;
      background: #fff;
      border-radius: 20px;
      padding: 8px 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

      .search-icon {
        color: $text-tertiary;
        font-size: 18px;
        margin-right: 8px;
      }

      .placeholder {
        flex: 1;
        color: $text-tertiary;
        font-size: 14px;
      }

      .voice-btn {
        padding: 0 8px;
        color: $text-secondary;
        cursor: pointer;
        display: flex;
        align-items: center;
        font-size: 18px;
        transition: color 0.2s ease;

        &:active {
          color: $primary-teal;
        }
      }

      .camera-btn {
        padding: 0 10px;
        border-right: 1px solid #EEEEEE;
        color: $text-secondary;
        cursor: pointer;
        display: flex;
        align-items: center;
        font-size: 18px;
      }

      .search-btn {
        color: $text-primary;
        padding: 5px 8px;
        font-size: 14px;
        font-weight: 500;
        margin-left: 4px;
        cursor: pointer;
        transition: color 0.3s ease;
        background: transparent;
      }
    }

    // 吸顶时显示的购物车按钮
    .sticky-cart-btn {
      display: none;
      width: 36px;
      height: 36px;
      align-items: center;
      justify-content: center;
      color: #333;
      font-size: 22px;
      cursor: pointer;
      flex-shrink: 0;
    }
  }
}
</style>
