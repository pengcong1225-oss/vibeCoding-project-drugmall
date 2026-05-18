<template>
  <div class="service-section">
    <!-- 24h服务标题 -->
    <div class="section-header">
      <div class="title-wrapper">
        <span class="title-highlight">24h</span>
        <span class="title-text">服务</span>
      </div>
      <div class="subtitle-wrapper" @click="handleViewAll">
        <span class="subtitle-text">春季过敏别硬扛</span>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 服务网格 - 支持左右滑动 -->
    <div class="service-slider">
      <div 
        class="service-grid" 
        ref="sliderRef"
        @scroll="handleScroll"
      >
        <div
          v-for="item in services"
          :key="item.id"
          class="service-item"
          @click="handleServiceClick(item)"
        >
          <div class="icon-wrapper" :style="{ background: item.bgColor }">
            <img 
              v-if="item.iconUrl" 
              :src="item.iconUrl" 
              class="icon-image"
              :alt="item.name"
            />
            <span v-else class="icon-text">{{ item.icon }}</span>
          </div>
          <span class="service-name">{{ item.name }}</span>
        </div>
      </div>
      <!-- 滑动指示器 -->
      <div class="slider-indicator">
        <span 
          v-for="(_, index) in 2" 
          :key="index"
          :class="['indicator-dot', { active: currentPage === index }]"
        ></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { ROUTES } from '@/constants/routes'
import { CATEGORY_ICONS } from '@/constants/images'
import type { ServiceItemData } from '@/types/home'

const router = useRouter()
const sliderRef = ref<HTMLElement>()
const currentPage = ref(0)

const services = ref<ServiceItemData[]>([
  { id: '1', name: '感冒发烧', icon: '', iconUrl: CATEGORY_ICONS['感冒发烧'], bgColor: 'rgba(0, 201, 167, 0.12)', category: '感冒发烧' },
  { id: '2', name: '咳嗽化痰', icon: '', iconUrl: CATEGORY_ICONS['咳嗽化痰'], bgColor: 'rgba(0, 229, 191, 0.12)', category: '咳嗽化痰' },
  { id: '3', name: '肠胃用药', icon: '', iconUrl: CATEGORY_ICONS['肠胃用药'], bgColor: 'rgba(16, 185, 129, 0.12)', category: '肠胃用药' },
  { id: '4', name: '皮肤用药', icon: '', iconUrl: CATEGORY_ICONS['皮肤用药'], bgColor: 'rgba(0, 201, 167, 0.08)', category: '皮肤用药' },
  { id: '5', name: '维生素钙', icon: '', iconUrl: CATEGORY_ICONS['维生素钙'], bgColor: 'rgba(0, 229, 191, 0.08)', category: '维生素钙' },
  { id: '6', name: '止痛消炎', icon: '', iconUrl: CATEGORY_ICONS['止痛消炎'], bgColor: 'rgba(0, 184, 148, 0.12)', category: '止痛消炎' },
  { id: '7', name: '五官用药', icon: '', iconUrl: CATEGORY_ICONS['五官用药'], bgColor: 'rgba(0, 201, 167, 0.1)', category: '五官用药' },
  { id: '8', name: '儿童用药', icon: '', iconUrl: CATEGORY_ICONS['儿童用药'], bgColor: 'rgba(0, 229, 191, 0.1)', category: '儿童用药' },
  { id: '9', name: '慢病用药', icon: '', iconUrl: CATEGORY_ICONS['慢病用药'], bgColor: 'rgba(16, 185, 129, 0.1)', category: '慢病用药' },
  { id: '10', name: '医疗器械', icon: '', iconUrl: CATEGORY_ICONS['医疗器械'], bgColor: 'rgba(0, 201, 167, 0.08)', category: '医疗器械' },
  { id: '11', name: '中药饮片', icon: '', iconUrl: CATEGORY_ICONS['中药饮片'], bgColor: 'rgba(0, 229, 191, 0.12)', category: '中药饮片' },
  { id: '12', name: '男科用药', icon: '', iconUrl: CATEGORY_ICONS['男科用药'], bgColor: 'rgba(0, 184, 148, 0.12)', category: '男科用药' },
  { id: '13', name: '妇科用药', icon: '', iconUrl: CATEGORY_ICONS['妇科用药'], bgColor: 'rgba(0, 201, 167, 0.1)', category: '妇科用药' },
  { id: '14', name: '避孕测孕', icon: '', iconUrl: CATEGORY_ICONS['避孕测孕'], bgColor: 'rgba(0, 229, 191, 0.1)', category: '避孕测孕' },
  { id: '15', name: '过敏用药', icon: '', iconUrl: CATEGORY_ICONS['过敏用药'], bgColor: 'rgba(16, 185, 129, 0.12)', category: '过敏用药' },
  { id: '16', name: '肝胆用药', icon: '', iconUrl: CATEGORY_ICONS['肝胆用药'], bgColor: 'rgba(0, 201, 167, 0.12)', category: '肝胆用药' },
  { id: '17', name: '心脑血管', icon: '', iconUrl: CATEGORY_ICONS['心脑血管'], bgColor: 'rgba(0, 229, 191, 0.08)', category: '心脑血管' },
  { id: '18', name: '滋补养生', icon: '', iconUrl: CATEGORY_ICONS['滋补养生'], bgColor: 'rgba(0, 184, 148, 0.1)', category: '滋补养生' },
  { id: '19', name: '口腔护理', icon: '', iconUrl: CATEGORY_ICONS['口腔护理'], bgColor: 'rgba(0, 201, 167, 0.1)', category: '口腔护理' },
  { id: '20', name: '眼科用药', icon: '', iconUrl: CATEGORY_ICONS['眼科用药'], bgColor: 'rgba(0, 229, 191, 0.12)', category: '眼科用药' },
])

function handleViewAll() {
  router.push(ROUTES.CATEGORY)
}

function handleServiceClick(item: ServiceItemData) {
  router.push(`${ROUTES.CATEGORY}?active=${item.id}`)
}

// 处理滚动事件，更新指示器
function handleScroll() {
  if (sliderRef.value) {
    const scrollLeft = sliderRef.value.scrollLeft
    const containerWidth = sliderRef.value.offsetWidth
    const newPage = Math.round(scrollLeft / containerWidth)
    currentPage.value = Math.min(newPage, 1)
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.service-section {
  background: #fff;
  margin: -28px 12px 10px;
  border-radius: 16px;
  padding: 12px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .title-wrapper {
      display: flex;
      align-items: baseline;
      gap: 4px;

      .title-highlight {
        font-size: 20px;
        font-weight: 800;
        color: $text-primary;
        font-style: italic;
      }

      .title-text {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .subtitle-wrapper {
      display: flex;
      align-items: center;
      gap: 4px;
      cursor: pointer;

      .subtitle-text {
        font-size: 12px;
        color: $text-secondary;
      }

      .arrow-icon {
        font-size: 12px;
        color: $text-secondary;
      }

      &:active {
        opacity: 0.7;
      }
    }
  }

  .service-slider {
    overflow: hidden;
    
    .service-grid {
      display: grid;
      grid-template-columns: repeat(10, 1fr);
      grid-template-rows: repeat(2, auto);
      grid-auto-flow: column;
      gap: 12px 16px;
      overflow-x: auto;
      scrollbar-width: none;
      -ms-overflow-style: none;
      -webkit-overflow-scrolling: touch;
      scroll-snap-type: x mandatory;
      padding-bottom: 8px;

      &::-webkit-scrollbar {
        display: none;
      }

      .service-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;
        scroll-snap-align: start;
        width: 60px;

        .icon-wrapper {
          width: 52px;
          height: 52px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 6px;
          overflow: hidden;
          transition: transform 0.2s;

          .icon-image {
            width: 40px;
            height: 40px;
            object-fit: contain;
          }

          .icon-text {
            font-size: 14px;
            color: $text-primary;
            font-weight: 500;
          }
        }

        .service-name {
          font-size: 11px;
          color: $text-secondary;
          text-align: center;
          white-space: nowrap;
        }

        &:active {
          .icon-wrapper {
            transform: scale(0.95);
          }
        }
      }
    }

    .slider-indicator {
      display: flex;
      justify-content: center;
      gap: 6px;
      margin-top: 12px;

      .indicator-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #ddd;
        transition: all 0.3s;

        &.active {
          width: 12px;
          border-radius: 3px;
          background: $primary;
        }
      }
    }
  }
}
</style>
