<template>
  <div class="waterfall-section">
    <div class="waterfall-grid">
      <template v-for="(item, index) in waterfallItems" :key="index">
        <!-- 广告卡片 -->
        <div
          v-if="item.type === 'ad'"
          class="waterfall-card ad-card"
          @click="handleAdClick(item)"
        >
          <img 
            v-if="item.imageUrl" 
            :src="item.imageUrl" 
            class="ad-bg-image"
            :alt="item.title"
          />
          <div v-else class="ad-bg" :style="{ background: item.bgGradient }"></div>
          <div class="ad-content">
            <h4 class="ad-title">{{ item.title }}</h4>
            <p class="ad-subtitle">{{ item.subtitle }}</p>
            <div class="ad-btn">{{ item.btnText }}</div>
          </div>
        </div>
        <!-- 商品卡片 -->
        <div
          v-else
          class="waterfall-card product-card"
          @click="handleProductClick(item)"
        >
          <div class="product-image-wrapper">
            <img 
              v-if="item.imageUrl" 
              :src="item.imageUrl" 
              class="product-image"
              :alt="item.name"
            />
            <div v-else class="product-full-bg" :style="{ background: item.imageColor }">
              <span class="product-placeholder-full">{{ item.imageText }}</span>
            </div>
            <span v-if="item.isRx" class="rx-badge">Rx</span>
            <span v-if="item.discount" class="discount-tag">-{{ item.discount }}%</span>
          </div>
          <div class="product-info">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-spec">{{ item.specification }}</div>
            <div class="product-sales">月售{{ item.sales }}万+</div>
            <div class="product-footer">
              <span class="product-price">¥{{ item.price }}</span>
              <span class="product-time">{{ item.deliveryTime }}分钟</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { HomeSection, WaterfallItemData } from '@/types/home'

const props = defineProps<{ section: HomeSection }>()
const emit = defineEmits<{
  (e: 'adClick', item: WaterfallItemData): void
  (e: 'productClick', item: WaterfallItemData): void
}>()

const router = useRouter()

const waterfallItems = computed<WaterfallItemData[]>(() => {
  const component = props.section.components[0]
  return component?.data || []
})

function handleAdClick(item: WaterfallItemData) {
  emit('adClick', item)
  if (item.link) {
    try {
      router.push(item.link)
    } catch (error) {
      console.warn('广告跳转失败:', error)
      ElMessage.info(item.title || '活动即将上线')
    }
  } else {
    ElMessage.info(`${item.title} - 活动即将上线`)
  }
}

function handleProductClick(item: WaterfallItemData) {
  emit('productClick', item)
  if (item.id) {
    try {
      router.push(`/drug/${item.id}`)
    } catch (error) {
      console.warn('跳转到商品详情失败:', error)
      ElMessage.info(`即将查看${item.name}`)
    }
  } else {
    ElMessage.warning('商品信息不完整')
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$price-red: #FF4D4F;
$accent-green: #00C9A7;
$text-primary: #1A1A1A;
$text-tertiary: #999999;

.waterfall-section {
  padding: 0 16px;

  .waterfall-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    .waterfall-card {
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;

      &.ad-card {
        position: relative;
        min-height: 140px;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;

        .ad-bg-image {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .ad-bg {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
        }

        .ad-content {
          position: relative;
          z-index: 1;
          padding: 16px;
          color: #fff;
          text-shadow: 0 1px 3px rgba(0,0,0,0.3);

          .ad-title {
            font-size: 16px;
            font-weight: bold;
            margin: 0 0 6px;
          }

          .ad-subtitle {
            font-size: 12px;
            opacity: 0.95;
            margin-bottom: 12px;
          }

          .ad-btn {
            display: inline-block;
            background: rgba(255, 255, 255, 0.9);
            color: #333;
            padding: 4px 12px;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 500;
          }
        }
      }

      &.product-card {
        background: #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        display: flex;
        flex-direction: column;

        .product-image-wrapper {
          position: relative;
          width: 100%;
          height: 160px;
          border-radius: 12px 12px 0 0;
          overflow: hidden;
          background: #f5f5f5;

          .product-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .product-full-bg {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;

            .product-placeholder-full {
              font-size: 32px;
              color: $text-primary;
              font-weight: bold;
              text-shadow: 0 1px 3px rgba(255,255,255,0.9);
            }
          }

          .rx-badge {
            position: absolute;
            top: 8px;
            left: 8px;
            background: $price-red;
            color: #fff;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 10px;
          }

          .discount-tag {
            position: absolute;
            top: 8px;
            right: 8px;
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            color: #fff;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
          }
        }

        .product-info {
          padding: 12px;

          .product-name {
            font-size: 13px;
            font-weight: 500;
            color: $text-primary;
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .product-spec {
            font-size: 11px;
            color: $text-tertiary;
            margin-bottom: 4px;
          }

          .product-sales {
            font-size: 11px;
            color: $text-tertiary;
            margin-bottom: 8px;
          }

          .product-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .product-price {
              font-size: 16px;
              font-weight: bold;
              color: $price-red;
            }

            .product-time {
              font-size: 11px;
              color: $accent-green;
              background: rgba(0, 201, 167, 0.1);
              padding: 2px 6px;
              border-radius: 4px;
            }
          }
        }
      }
    }
  }
}
</style>
