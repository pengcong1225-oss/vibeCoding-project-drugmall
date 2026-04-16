<template>
  <div class="department-grid-section">
    <div class="section-header">
      <h3 class="section-title">按科室问医生</h3>
      <span class="doctor-count">{{ onlineCount }}+医生在线值班</span>
    </div>
    
    <!-- 科室网格 - 每页2排x5列，超过10个显示到下一页 -->
    <div class="dept-slider">
      <div class="grid-container" ref="sliderRef" @scroll="handleScroll">
        <!-- 分页显示，每页最多10个（2排 x 5列） -->
        <div
          v-for="(pageDepts, pageIndex) in paginatedDepartments"
          :key="pageIndex"
          class="page"
          :class="{ 'page-first': pageIndex === 0 }"
        >
          <div
            v-for="dept in pageDepts"
            :key="dept.code"
            class="dept-card"
            @click="$emit('select', dept)"
          >
            <div class="dept-icon-wrapper">
              <img v-if="dept.iconUrl" :src="dept.iconUrl" class="dept-icon-img" :alt="dept.name" />
              <el-icon v-else :size="22">
                <component :is="getIconComponent(dept.icon)" />
              </el-icon>
            </div>
            <span class="dept-name">{{ dept.name }}</span>
            <span v-if="dept.tag" :class="['dept-tag', `tag-${dept.tagType}`]">
              {{ dept.tag }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- 滑动指示器 -->
      <div class="slider-indicator">
        <span 
          v-for="(_, index) in totalPages" 
          :key="index"
          :class="['indicator-dot', { active: currentPage === index }]"
        ></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Sunny,
  FirstAidKit,
  User,
  Food,
  Female,
  Headset,
  CircleCheck,
  View,
  ChatLineRound,
  Moon,
  Scissor,
  Star
} from '@element-plus/icons-vue'

export interface Department {
  code: string
  name: string
  icon: string
  iconUrl?: string
  tag?: string
  tagType?: 'hot' | 'price' | 'info' | 'fever' | 'supplement'
}

const props = withDefaults(defineProps<{
  departments: Department[]
  onlineCount?: number
}>(), {
  onlineCount: 2700
})

const emit = defineEmits<{
  (e: 'select', dept: Department): void
}>()

const sliderRef = ref<HTMLElement>()
const currentPage = ref(0)

// 每页显示10个（2排 x 5列）
const ITEMS_PER_PAGE = 10

// 分页后的科室数据
const paginatedDepartments = computed(() => {
  const pages: Department[][] = []
  for (let i = 0; i < props.departments.length; i += ITEMS_PER_PAGE) {
    pages.push(props.departments.slice(i, i + ITEMS_PER_PAGE))
  }
  return pages
})

// 总页数
const totalPages = computed(() => paginatedDepartments.value.length)

// 图标映射
const iconMap: Record<string, any> = {
  skin: Sunny,
  lung: FirstAidKit,
  child: User,
  stomach: Food,
  female: Female,
  ear: Headset,
  kidney: CircleCheck,
  eye: View,
  psychology: ChatLineRound,
  moon: Moon,
  bone: Scissor,
  star: Star
}

const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || FirstAidKit
}

// 处理滚动事件，更新指示器
const handleScroll = () => {
  if (sliderRef.value) {
    const scrollLeft = sliderRef.value.scrollLeft
    const containerWidth = sliderRef.value.offsetWidth
    const newPage = Math.round(scrollLeft / containerWidth)
    currentPage.value = Math.min(newPage, totalPages.value - 1)
  }
}
</script>

<style scoped lang="scss">
$primary-teal: #00C9A7;
$text-primary: #1A1A1A;
$text-secondary: #666666;

.department-grid-section {
  padding: 16px;
  background-color: #fff;
  margin: 0 12px 10px;
  border-radius: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);

  .section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      flex-shrink: 0;
    }

    .doctor-count {
      font-size: 13px;
      color: $text-secondary;
    }
  }

  .dept-slider {
    overflow: hidden;

    .grid-container {
      display: flex;
      overflow-x: auto;
      scrollbar-width: none;
      -ms-overflow-style: none;
      -webkit-overflow-scrolling: touch;
      scroll-snap-type: x mandatory;
      padding-bottom: 8px;

      &::-webkit-scrollbar {
        display: none;
      }

      .page {
        display: grid;
        gap: 8px 4px;
        flex-shrink: 0;
        width: 100%;
        scroll-snap-align: start;
        align-content: start; // 高度自适应
        grid-template-columns: repeat(5, 1fr); // 5列
        grid-auto-rows: auto; // 自适应高度
      }

      .dept-card {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 4px 2px;
        cursor: pointer;
        transition: all 0.2s ease;
        position: relative;

        &:active {
          transform: scale(0.95);
        }

        .dept-icon-wrapper {
          width: 40px;
          height: 40px;
          background-color: rgba($primary-teal, 0.08);
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 4px;

          .dept-icon-img {
            width: 24px;
            height: 24px;
            object-fit: contain;
          }

          .el-icon {
            font-size: 20px;
            color: $primary-teal;
          }
        }

        .dept-name {
          font-size: 11px;
          font-weight: 500;
          color: $text-primary;
          text-align: center;
          line-height: 1.2;
          white-space: nowrap;
        }

        .dept-tag {
          position: absolute;
          top: 4px;
          right: 4px;
          padding: 1px 4px;
          border-radius: 4px;
          font-size: 9px;
          font-weight: 600;
          line-height: 1.2;

          &.tag-hot {
            background-color: #FF4D4F;
            color: #fff;
          }

          &.tag-fever {
            background-color: #FF6B6B;
            color: #fff;
          }

          &.tag-price {
            background-color: #FF4D4F;
            color: #fff;
          }

          &.tag-supplement {
            background-color: #FF6B6B;
            color: #fff;
          }

          &.tag-info {
            background-color: rgba($primary-teal, 0.12);
            color: $primary-teal;
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
          background: $primary-teal;
        }
      }
    }
  }
}
</style>
