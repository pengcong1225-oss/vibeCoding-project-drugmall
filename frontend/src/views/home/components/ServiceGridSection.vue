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
import type { ServiceItemData } from '@/types/home'

const router = useRouter()
const sliderRef = ref<HTMLElement>()
const currentPage = ref(0)

// 服务数据 - 美团买药风格（2页，每页10个）
const services = ref<ServiceItemData[]>([
  // 第一页
  { id: '1', name: '肠炎宁', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#E8F5E9', category: '肠胃消化' },
  { id: '2', name: '枇杷膏', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FFF3E0', category: '咳嗽咽痛' },
  { id: '3', name: '阿莫西林', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E3F2FD', category: '消炎药' },
  { id: '4', name: '莫匹罗星', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#FFEBEE', category: '皮肤红疹' },
  { id: '5', name: '云南白药', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FCE4EC', category: '筋骨跌打' },
  { id: '6', name: '中医/养生', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FFF8E1', category: '中医养生' },
  { id: '7', name: '家用护理', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E0F2F1', category: '家用护理' },
  { id: '8', name: '慢病用药', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#F3E5F5', category: '慢病用药' },
  { id: '9', name: '止痛药', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FFEBEE', category: '止痛药' },
  { id: '10', name: '西瓜霜', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#E8F5E9', category: '清热去火' },
  // 第二页
  { id: '11', name: '感冒灵', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E3F2FD', category: '感冒发烧' },
  { id: '12', name: '布洛芬', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#FFF3E0', category: '儿童用药' },
  { id: '13', name: '万艾可', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FFEBEE', category: '男科用药' },
  { id: '14', name: '超薄', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FCE4EC', category: '避孕测孕' },
  { id: '15', name: '雷诺考特', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E8F5E9', category: '过敏用药' },
  { id: '16', name: '为TA买药', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#FFF8E1', category: '为TA买药' },
  { id: '17', name: '原研药', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#E8EAF6', category: '原研药' },
  { id: '18', name: '自营大药房', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FFF3E0', category: '自营大药房' },
  { id: '19', name: '口腔/看牙', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E1F5FE', category: '口腔/看牙' },
  { id: '20', name: '眼科/配镜', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#E3F2FD', category: '眼科/配镜' },
])

// 点击全部服务进入分类页
function handleViewAll() {
  router.push('/category')
}

function handleServiceClick(item: ServiceItemData) {
  router.push(`/category?active=${item.id}`)
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
$primary-yellow: #FFD100;
$text-primary: #333333;
$text-secondary: #666666;

.service-section {
  background: #fff;
  margin: -20px 12px 10px;
  border-radius: 16px;
  padding: 14px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
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
          background: $primary-yellow;
        }
      }
    }
  }
}
</style>
