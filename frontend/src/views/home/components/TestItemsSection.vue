<template>
  <div class="test-service-content">
    <!-- 第一个区域：看一看 + 第一个话题合并 -->
    <div class="section-card first-section">
      <div class="section-header">
        <div class="section-title">
          <span class="title-text">看一看</span>
          <span class="title-tag">精选检测项目</span>
        </div>
      </div>
      
      <!-- 第一个话题 -->
      <div class="topic-item" v-if="topicList.length > 0">
        <div class="topic-header">
          <div class="topic-name">
            <span class="topic-hash">#</span>
            <span class="topic-title-text">{{ topicList[0].name }}</span>
          </div>
          <div class="topic-hot" v-if="topicList[0].hotTag">
            <span class="hot-icon">🔥</span>
            <span class="hot-text">{{ topicList[0].hotTag }}</span>
          </div>
        </div>
        
        <!-- 商品2排横向滑动布局 -->
        <div class="product-scroll-container">
          <div class="product-grid two-rows">
            <!-- 第一排 -->
            <div class="product-row">
              <div
                v-for="(product, index) in topicList[0].products.slice(0, 2)"
                :key="product.id"
                class="product-card"
                @click="handleProductClick(product)"
              >
                <div class="product-image">
                  <svg class="product-svg" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
                    <rect width="80" height="80" :fill="getBgColor(index)"/>
                    <rect x="20" y="15" width="40" height="50" rx="4" fill="#e0e0e0"/>
                    <rect x="25" y="20" width="30" height="35" rx="2" fill="#fff"/>
                    <circle cx="40" cy="55" r="8" fill="#FFD93D"/>
                    <rect x="36" y="51" width="8" height="8" rx="1" fill="#fff"/>
                    <rect x="30" y="25" width="20" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="30" width="15" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="35" width="18" height="2" rx="1" fill="#ccc"/>
                  </svg>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-sales">月售{{ product.sales }}</div>
                  <div class="product-bottom">
                    <div class="product-price">
                      <span class="price-symbol">¥</span>
                      <span class="price-value">{{ product.price }}</span>
                    </div>
                    <div class="delivery-time">{{ product.deliveryTime }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 第二排 -->
            <div class="product-row">
              <div
                v-for="(product, index) in topicList[0].products.slice(2, 4)"
                :key="product.id"
                class="product-card"
                @click="handleProductClick(product)"
              >
                <div class="product-image">
                  <svg class="product-svg" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
                    <rect width="80" height="80" :fill="getBgColor(index + 2)"/>
                    <rect x="20" y="15" width="40" height="50" rx="4" fill="#e0e0e0"/>
                    <rect x="25" y="20" width="30" height="35" rx="2" fill="#fff"/>
                    <circle cx="40" cy="55" r="8" fill="#FFD93D"/>
                    <rect x="36" y="51" width="8" height="8" rx="1" fill="#fff"/>
                    <rect x="30" y="25" width="20" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="30" width="15" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="35" width="18" height="2" rx="1" fill="#ccc"/>
                  </svg>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-sales">月售{{ product.sales }}</div>
                  <div class="product-bottom">
                    <div class="product-price">
                      <span class="price-symbol">¥</span>
                      <span class="price-value">{{ product.price }}</span>
                    </div>
                    <div class="delivery-time">{{ product.deliveryTime }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 其他话题区域 -->
    <div class="section-card" v-for="topic in otherTopics" :key="topic.id">
      <div class="topic-item">
        <div class="topic-header">
          <div class="topic-name">
            <span class="topic-hash">#</span>
            <span class="topic-title-text">{{ topic.name }}</span>
          </div>
          <div class="topic-hot" v-if="topic.hotTag">
            <span class="hot-icon">🔥</span>
            <span class="hot-text">{{ topic.hotTag }}</span>
          </div>
        </div>
        
        <!-- 商品2排横向滑动布局 -->
        <div class="product-scroll-container">
          <div class="product-grid two-rows">
            <!-- 第一排 -->
            <div class="product-row">
              <div 
                v-for="product in topic.products.slice(0, 2)" 
                :key="product.id"
                class="product-card"
                @click="handleProductClick(product)"
              >
                <div class="product-image">
                  <svg class="product-svg" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
                    <rect width="80" height="80" fill="#f0f0f0"/>
                    <rect x="20" y="15" width="40" height="50" rx="4" fill="#e0e0e0"/>
                    <rect x="25" y="20" width="30" height="35" rx="2" fill="#fff"/>
                    <circle cx="40" cy="55" r="8" fill="#FFD93D"/>
                    <rect x="36" y="51" width="8" height="8" rx="1" fill="#fff"/>
                    <rect x="30" y="25" width="20" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="30" width="15" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="35" width="18" height="2" rx="1" fill="#ccc"/>
                  </svg>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-sales">月售{{ product.sales }}</div>
                  <div class="product-bottom">
                    <div class="product-price">
                      <span class="price-symbol">¥</span>
                      <span class="price-value">{{ product.price }}</span>
                    </div>
                    <div class="delivery-time">{{ product.deliveryTime }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 第二排 -->
            <div class="product-row">
              <div
                v-for="product in topic.products.slice(2, 4)"
                :key="product.id"
                class="product-card"
                @click="handleProductClick(product)"
              >
                <div class="product-image">
                  <svg class="product-svg" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
                    <rect width="80" height="80" fill="#f0f0f0"/>
                    <rect x="20" y="15" width="40" height="50" rx="4" fill="#e0e0e0"/>
                    <rect x="25" y="20" width="30" height="35" rx="2" fill="#fff"/>
                    <circle cx="40" cy="55" r="8" fill="#FFD93D"/>
                    <rect x="36" y="51" width="8" height="8" rx="1" fill="#fff"/>
                    <rect x="30" y="25" width="20" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="30" width="15" height="2" rx="1" fill="#ccc"/>
                    <rect x="30" y="35" width="18" height="2" rx="1" fill="#ccc"/>
                  </svg>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ product.title }}</div>
                  <div class="product-sales">月售{{ product.sales }}</div>
                  <div class="product-bottom">
                    <div class="product-price">
                      <span class="price-symbol">¥</span>
                      <span class="price-value">{{ product.price }}</span>
                    </div>
                    <div class="delivery-time">{{ product.deliveryTime }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部安全区域占位 -->
    <div class="safe-area-bottom"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ROUTES, getDrugDetailRoute } from '@/constants/routes'
import type { HomeSection } from '@/types/home'

const props = defineProps<{
  section: HomeSection
  activeTab?: string
}>()

const emit = defineEmits<{
  (e: 'bannerClick', slide: any): void
  (e: 'productClick', product: any): void
}>()

const router = useRouter()

// Banner背景图
const bannerBackground = ref('/images/jc01.png')

// 默认商品图片
const defaultProductImage = ref('/images/default-product.png')

// SVG背景色数组
const bgColors = ['#E3F2FD', '#F3E5F5', '#E8F5E9', '#FFF3E0', '#FBE9E7', '#E0F7FA']

// 根据索引获取背景色
const getBgColor = (index: number) => {
  return bgColors[index % bgColors.length]
}

const handleBannerClickFn = () => {
  emit('bannerClick', {})
  router.push(ROUTES.TEST_HOME)
}

// 话题列表数据
const topicList = ref([
  {
    id: 1,
    name: '呼吸道感染早知道',
    hotTag: '温差大易感冒',
    products: [
      {
        id: 'p1',
        title: '[申基医药]肺炎支原体抗原检测试剂盒(胶体金法)1人份/盒',
        sales: '12',
        price: '28.9',
        deliveryTime: '预计后天送达',
        image: '/images/product-01.png'
      },
      {
        id: 'p2',
        title: '[博迪泰]甲型/乙型流感病毒抗原检测试剂盒(卡型)(胶体...',
        sales: '100+',
        price: '6.5',
        deliveryTime: '预计3天送达',
        image: '/images/product-02.png'
      },
      {
        id: 'p1-2',
        title: '[申基医药]肺炎支原体抗原检测试剂盒(胶体金法)1人份/盒',
        sales: '12',
        price: '28.9',
        deliveryTime: '预计后天送达',
        image: '/images/product-01.png'
      },
      {
        id: 'p2-2',
        title: '[博迪泰]甲型/乙型流感病毒抗原检测试剂盒(卡型)(胶体...',
        sales: '100+',
        price: '6.5',
        deliveryTime: '预计3天送达',
        image: '/images/product-02.png'
      }
    ]
  },
  {
    id: 2,
    name: '妇科隐患，早测早安心',
    hotTag: '关注"她健康"',
    products: [
      {
        id: 'p3',
        title: '南京同仁堂生物科技HPV探针检测试剂盒原位杂交法尿...',
        sales: '600+',
        price: '68',
        deliveryTime: '预计后天送达',
        image: '/images/product-03.png'
      },
      {
        id: 'p4',
        title: '[SYNTHCNE]阴道炎联合检测试剂盒',
        sales: '200+',
        price: '45',
        deliveryTime: '预计明天送达',
        image: '/images/product-04.png'
      },
      {
        id: 'p3-2',
        title: '南京同仁堂生物科技HPV探针检测试剂盒原位杂交法尿...',
        sales: '600+',
        price: '68',
        deliveryTime: '预计后天送达',
        image: '/images/product-03.png'
      },
      {
        id: 'p4-2',
        title: '[SYNTHCNE]阴道炎联合检测试剂盒',
        sales: '200+',
        price: '45',
        deliveryTime: '预计明天送达',
        image: '/images/product-04.png'
      }
    ]
  },
  {
    id: 3,
    name: '肠胃健康早筛查',
    hotTag: '居家自测更方便',
    products: [
      {
        id: 'p5',
        title: '幽门螺杆菌检测试纸',
        sales: '1000+',
        price: '19.9',
        deliveryTime: '预计后天送达',
        image: '/images/product-05.png'
      },
      {
        id: 'p6',
        title: '便隐血检测试剂盒',
        sales: '500+',
        price: '35',
        deliveryTime: '预计3天送达',
        image: '/images/product-06.png'
      },
      {
        id: 'p5-2',
        title: '幽门螺杆菌检测试纸',
        sales: '1000+',
        price: '19.9',
        deliveryTime: '预计后天送达',
        image: '/images/product-05.png'
      },
      {
        id: 'p6-2',
        title: '便隐血检测试剂盒',
        sales: '500+',
        price: '35',
        deliveryTime: '预计3天送达',
        image: '/images/product-06.png'
      }
    ]
  }
])

// 其他话题（排除第一个）
const otherTopics = computed(() => topicList.value.slice(1))

const handleProductClick = (product: any) => {
  emit('productClick', product)
  router.push(getDrugDetailRoute(product.id))
}
</script>

<style scoped lang="scss">
// 配色方案 - 与图片一致
$primary-yellow: #FFD93D;
$bg-warm: #FFF9E6;
$price-red: #FF6B6B;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$card-bg: #FFFFFF;

.test-service-content {
  min-height: 100%;
  background: transparent;
}

// Banner区域 - 使用jc01.png作为背景，完整展示图片
.banner-section {
  margin: 0 0 12px;
  border-radius: 0;
  overflow: hidden;
  position: relative;
  height: 180px;
  background-size: 100% auto;
  background-position: center top;
  background-repeat: no-repeat;

  // 遮罩层，确保文字可读
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, rgba(255, 217, 61, 0.85) 0%, rgba(255, 217, 61, 0.5) 40%, transparent 70%);
    pointer-events: none;
  }

  .banner-content {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    padding: 20px 16px;
    height: 100%;
    box-sizing: border-box;
  }

  .banner-left {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .banner-tag {
      display: inline-block;
      background: rgba(255, 255, 255, 0.9);
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      color: $text-primary;
      width: fit-content;
      font-weight: 500;
    }

    .banner-title {
      font-size: 18px;
      font-weight: 700;
      color: $text-primary;
      text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
    }

    .banner-btn {
      background: $text-primary;
      color: #fff;
      border: none;
      border-radius: 16px;
      padding: 6px 14px;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      width: fit-content;
      transition: all 0.2s ease;

      &:active {
        transform: scale(0.95);
      }
    }
  }
}

// 区域卡片 - 白色底色圆角
.section-card {
  background: $card-bg;
  border-radius: 0;
  padding: 12px 0;
  margin-bottom: 0;
  box-shadow: none;

  &.first-section {
    .section-header {
      padding: 0 12px 8px;
      border-bottom: 1px solid #f5f5f5;
      margin-bottom: 8px;

      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;

        .title-text {
          font-size: 18px;
          font-weight: 700;
          color: $text-primary;
        }

        .title-tag {
          font-size: 12px;
          color: $text-secondary;
          background: rgba(255, 217, 61, 0.3);
          padding: 2px 8px;
          border-radius: 10px;
        }
      }
    }
  }
}

// 话题样式
.topic-item {
  padding: 0 12px;

  .topic-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .topic-name {
      display: flex;
      align-items: center;

      .topic-hash {
        font-size: 16px;
        font-weight: 700;
        color: $primary-yellow;
      }

      .topic-title-text {
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .topic-hot {
      display: flex;
      align-items: center;
      gap: 2px;
      background: rgba(255, 107, 107, 0.1);
      padding: 2px 8px;
      border-radius: 10px;

      .hot-icon {
        font-size: 12px;
      }

      .hot-text {
        font-size: 11px;
        color: $price-red;
      }
    }
  }

  // 商品滑动容器 - 支持左右滑动
  .product-scroll-container {
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    margin: 0 -12px;
    padding: 0 12px;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  // 商品网格布局 - 2排横向滑动
  .product-grid {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: max-content;

    &.two-rows {
      .product-row {
        display: flex;
        gap: 8px;
      }
    }
  }

  .product-card {
    background: #f8f8f8;
    border-radius: 8px;
    padding: 10px;
    display: flex;
    flex-direction: row;
    gap: 10px;
    cursor: pointer;
    transition: all 0.2s ease;
    width: calc(60vw - 16px);
    max-width: 260px;
    flex-shrink: 0;

    &:active {
      transform: scale(0.98);
    }

    .product-image {
      width: 72px;
      height: 72px;
      flex-shrink: 0;
      border-radius: 8px;
      overflow: hidden;

      .product-svg {
        width: 100%;
        height: 100%;
        display: block;
      }
    }

    .product-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      min-width: 0;
      overflow: hidden;

      .product-title {
        font-size: 14px;
        font-weight: 500;
        color: $text-primary;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        word-break: break-all;
      }

      .product-sales {
        font-size: 12px;
        color: $text-tertiary;
        margin-top: 4px;
      }

      .product-bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 6px;

        .product-price {
          color: $price-red;

          .price-symbol {
            font-size: 12px;
            font-weight: 600;
          }

          .price-value {
            font-size: 18px;
            font-weight: 700;
          }
        }

        .delivery-time {
          font-size: 11px;
          color: $text-tertiary;
          white-space: nowrap;
        }
      }
    }
  }
}

// 安全区域
.safe-area-bottom {
  height: env(safe-area-inset-bottom, 0);
}
</style>
