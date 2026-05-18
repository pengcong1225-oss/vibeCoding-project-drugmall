<template>
  <div class="nearby-section">
    <div class="section-header">
      <div class="title-group">
        <h3 class="title">{{ section.title || '附近急送' }}</h3>
        <span class="tag yellow">{{ section.config?.deliveryTag || '平均30分钟' }}</span>
      </div>
      <div class="delivery-time">
        <span>现在下单</span>
        <span class="time">{{ estimatedTime }}</span>
        <span>送达</span>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <span
        v-for="filter in filters"
        :key="filter"
        class="filter-item"
        :class="{ active: activeFilter === filter }"
        @click="activeFilter = filter"
      >
        {{ filter }}
      </span>
    </div>

    <!-- 药店列表 -->
    <div class="pharmacy-list">
      <div
        v-for="pharmacy in pharmacies"
        :key="pharmacy.id"
        class="pharmacy-card"
        @click="handlePharmacyClick(pharmacy)"
      >
        <div class="pharmacy-header">
          <div class="pharmacy-info">
            <div class="pharmacy-logo" :style="{ background: pharmacy.logoColor }">
              <img v-if="pharmacy.logoUrl" :src="pharmacy.logoUrl" class="logo-img" :alt="pharmacy.name" />
              <span v-else class="logo-text">{{ pharmacy.logoText }}</span>
            </div>
            <div class="pharmacy-meta">
              <h4 class="pharmacy-name">{{ pharmacy.name }}</h4>
              <div class="pharmacy-rating">
                <el-icon class="star-icon"><StarFilled /></el-icon>
                <span class="rating-score">{{ pharmacy.rating }}分</span>
                <span class="rating-count">月售{{ pharmacy.monthlySales }}+</span>
              </div>
            </div>
          </div>
          <div class="pharmacy-distance">
            <span class="distance">{{ pharmacy.distance }}km</span>
            <span class="time">{{ pharmacy.deliveryTime }}分钟</span>
          </div>
        </div>
        <div class="pharmacy-tags">
          <span
            v-for="(tag, idx) in (pharmacy.tags || []).filter(t => t)"
            :key="idx"
            class="pharmacy-tag"
            :class="tag.type || 'primary'"
          >
            {{ tag.text }}
          </span>
        </div>
      </div>
    </div>

    <!-- 更多商家 -->
    <div class="more-pharmacies" @click="$emit('moreClick')">
      <span>更多附近商家</span>
      <el-icon><ArrowRight /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, StarFilled } from '@element-plus/icons-vue'
import type { HomeSection, PharmacyData } from '@/types/home'

const props = defineProps<{ section: HomeSection }>()
const emit = defineEmits<{
  (e: 'moreClick'): void
  (e: 'pharmacyClick', pharmacy: PharmacyData): void
}>()

const router = useRouter()

// ✅ 修复：正确解析药店数据结构
// 后端返回的 data 是一个对象：{stores: [...]}
const pharmacyData = computed(() => {
  const component = props.section.components?.[0]
  return component?.data || {}
})

const activeFilter = ref(pharmacyData.value.activeFilter || '附近药店')
const filters = ref<string[]>(pharmacyData.value.filters || ['附近药店', '成人用品', '医疗器械', '隐形眼镜', '营养保健'])

const pharmacies = computed<PharmacyData[]>(() => {
  return pharmacyData.value.stores || []
})

const estimatedTime = computed(() => {
  const now = new Date()
  now.setMinutes(now.getMinutes() + 30)
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
})

function handlePharmacyClick(pharmacy: PharmacyData) {
  emit('pharmacyClick', pharmacy)
  // ✅ 修复：路由从 /pharmacy/ 改为 /store/
  try {
    router.push(`/store/${pharmacy.id}`)
  } catch (error) {
    console.warn('跳转到药店详情失败:', error)
    ElMessage.info(`即将进入${pharmacy.name}`)
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-blue: #3B8CFF;
$accent-green: #00C9A7;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-gray: #F5F7FA;
$border-color: #EEEEEE;

.nearby-section {
  background: #fff;
  margin: 0 12px 10px;
  border-radius: 16px;
  padding: 14px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;

    .title-group {
      display: flex;
      align-items: center;
      gap: 8px;

      .title {
        font-size: 16px;
        font-weight: bold;
        color: $text-primary;
        margin: 0;
      }

      .tag {
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;

        &.yellow {
          background: #ffd700;
          color: $text-primary;
        }
      }
    }

    .delivery-time {
      font-size: 12px;
      color: $text-secondary;

      .time {
        color: $primary-blue;
        font-weight: bold;
        margin: 0 4px;
      }
    }
  }

  .filter-tabs {
    display: flex;
    gap: 6px;
    margin-bottom: 10px;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;

    &::-webkit-scrollbar {
      display: none;
    }

    .filter-item {
      padding: 6px 12px;
      border-radius: 16px;
      font-size: 12px;
      color: $text-secondary;
      background: $bg-gray;
      white-space: nowrap;
      cursor: pointer;

      &.active {
        background: $primary-blue;
        color: #fff;
      }
    }
  }

  .pharmacy-list {
    margin-bottom: 10px;

    .pharmacy-card {
      background: $bg-gray;
      border-radius: 12px;
      padding: 8px 10px;
      margin-bottom: 6px;
      cursor: pointer;

      &:last-child {
        margin-bottom: 0;
      }

      .pharmacy-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 8px;

        .pharmacy-info {
          display: flex;
          gap: 10px;

          .pharmacy-logo {
            width: 44px;
            height: 44px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;

            .logo-img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .logo-text {
              font-size: 12px;
              font-weight: bold;
              color: #fff;
            }
          }

          .pharmacy-meta {
            .pharmacy-name {
              font-size: 14px;
              font-weight: bold;
              color: $text-primary;
              margin: 0 0 4px;
            }

            .pharmacy-rating {
              display: flex;
              align-items: center;
              gap: 4px;

              .star-icon {
                color: #ffc107;
                font-size: 12px;
              }

              .rating-score {
                font-size: 12px;
                font-weight: bold;
                color: $text-primary;
              }

              .rating-count {
                font-size: 11px;
                color: $text-tertiary;
              }
            }
          }
        }

        .pharmacy-distance {
          text-align: right;

          .distance {
            display: block;
            font-size: 12px;
            color: $text-secondary;
          }

          .time {
            font-size: 12px;
            color: $accent-green;
            font-weight: 500;
          }
        }
      }

      .pharmacy-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;

        .pharmacy-tag {
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 10px;

          &.primary {
            background: #E3F2FD;
            color: $primary-blue;
          }

          &.success {
            background: #E8F5E9;
            color: #4CAF50;
          }

          &.warning {
            background: #FFF3E0;
            color: #FF9800;
          }
        }
      }
    }
  }

  .more-pharmacies {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 10px;
    color: $text-secondary;
    font-size: 13px;
    cursor: pointer;
    border-top: 1px solid $border-color;

    .el-icon {
      font-size: 12px;
    }
  }
}
</style>
