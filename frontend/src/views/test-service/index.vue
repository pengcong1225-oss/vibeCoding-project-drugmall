<template>
  <div class="test-service-page">
    <!-- 顶部导航栏 -->
    <div class="header-nav">
      <div class="nav-left" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="nav-title">看病买药</div>
      <div class="nav-right">
        <div class="location" @click="showLocationPicker = true">
          <el-icon><Location /></el-icon>
          <span>宜昌美术馆</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <div class="cart-icon" @click="goToCart">
          <el-icon><ShoppingCart /></el-icon>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="search-box">
        <el-icon class="search-icon"><Search /></el-icon>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="布洛芬"
          class="search-input"
        />
        <el-icon class="camera-icon"><Camera /></el-icon>
        <button class="search-btn">搜索</button>
      </div>
    </div>

    <!-- 分类标签栏 -->
    <div class="category-tabs">
      <div 
        v-for="tab in categoryTabs" 
        :key="tab.id"
        class="tab-item"
        :class="{ active: activeTab === tab.id }"
        @click="switchTab(tab.id)"
      >
        <span class="tab-name">{{ tab.name }}</span>
      </div>
    </div>

    <!-- Banner区域 -->
    <div class="banner-section">
      <div class="banner-content">
        <div class="banner-left">
          <div class="banner-tag">居家自检</div>
          <div class="banner-title">自测15分钟出结果</div>
          <button class="banner-btn">立即查看</button>
        </div>
        <div class="banner-right">
          <div class="test-box-image">
            <div class="test-box-placeholder">
              <el-icon><FirstAidKit /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品区块标题 -->
    <div class="section-header">
      <div class="section-title">
        <span class="title-text">看一看</span>
        <span class="title-tag">精选检测项目</span>
      </div>
    </div>

    <!-- 话题列表 -->
    <div class="topic-list">
      <div v-for="topic in topicList" :key="topic.id" class="topic-item">
        <!-- 话题标题 -->
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
        
        <!-- 商品横向滚动 -->
        <div class="product-scroll">
          <div 
            v-for="product in topic.products" 
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <div class="product-img-placeholder">
                <el-icon><FirstAidKit /></el-icon>
              </div>
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

    <!-- 底部安全区域占位 -->
    <div class="safe-area-bottom"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowLeft, 
  Location, 
  ArrowDown, 
  ShoppingCart, 
  Search, 
  Camera,
  FirstAidKit 
} from '@element-plus/icons-vue'
import { ROUTES, getDrugDetailRoute } from '@/constants/routes'

const router = useRouter()
const searchKeyword = ref('')
const activeTab = ref('test')
const showLocationPicker = ref(false)

// 分类标签
const categoryTabs = [
  { id: 'recommend', name: '推荐' },
  { id: 'doctor', name: '问医生' },
  { id: 'test', name: '做检测' },
  { id: 'adult', name: '成人情趣' },
  { id: 'tonic', name: '滋补保健' }
]

// 添加标签栏样式优化 - 选中态使用胶囊样式
const getTabStyle = (tabId: string) => {
  if (tabId === 'test') {
    return {
      background: '#333333',
      color: '#FFD93D',
      fontWeight: 600
    }
  }
  return {}
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
        title: '[申基医药]肺炎支原体抗原检测试剂盒',
        sales: '12',
        price: '28.9',
        deliveryTime: '预计后天送达'
      },
      {
        id: 'p2',
        title: '[博迪泰]甲型/乙型流感病毒抗原检测试剂盒',
        sales: '100+',
        price: '6.5',
        deliveryTime: '预计3天送达'
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
        title: '南京同仁堂生物科技HPV探针检测试剂盒',
        sales: '600+',
        price: '68',
        deliveryTime: '预计后天送达'
      },
      {
        id: 'p4',
        title: '[SYNTHCNE]阴道炎联合检测试剂盒',
        sales: '200+',
        price: '45',
        deliveryTime: '预计明天送达'
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
        deliveryTime: '预计后天送达'
      },
      {
        id: 'p6',
        title: '便隐血检测试剂盒',
        sales: '500+',
        price: '35',
        deliveryTime: '预计3天送达'
      }
    ]
  }
])

const switchTab = (tabId: string) => {
  if (tabId === 'recommend') {
    router.push(ROUTES.HOME)
  } else if (tabId === 'doctor') {
    router.push(ROUTES.INQUIRY)
  } else if (tabId === 'test') {
    // 当前页面
  } else {
    router.push(ROUTES.CATEGORY)
  }
}

const goBack = () => {
  router.back()
}

const goToCart = () => {
  router.push(ROUTES.CART)
}

const goToProduct = (productId: string) => {
  router.push(getDrugDetailRoute(productId))
}
</script>

<style scoped lang="scss">
// 配色方案
$primary-yellow: #FFD93D;
$bg-warm: #FFF9E6;
$price-red: #FF6B6B;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$card-bg: #FFFFFF;

.test-service-page {
  min-height: 100vh;
  background: $bg-warm;
  padding-bottom: 80px;
}

// 顶部导航栏
.header-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding: 0 12px;
  background: $primary-yellow;

  .nav-left {
    width: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 20px;
      color: $text-primary;
    }
  }

  .nav-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .location {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: $text-primary;

      .el-icon {
        font-size: 14px;
      }

      span {
        max-width: 80px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .cart-icon {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;

      .el-icon {
        font-size: 20px;
        color: $text-primary;
      }
    }
  }
}

// 搜索栏
.search-section {
  padding: 12px 16px;
  background: $primary-yellow;

  .search-box {
    display: flex;
    align-items: center;
    background: $card-bg;
    border-radius: 20px;
    padding: 8px 12px;
    gap: 8px;

    .search-icon {
      font-size: 18px;
      color: $text-tertiary;
    }

    .search-input {
      flex: 1;
      border: none;
      outline: none;
      font-size: 14px;
      color: $text-primary;
      background: transparent;

      &::placeholder {
        color: $text-tertiary;
      }
    }

    .camera-icon {
      font-size: 18px;
      color: $text-tertiary;
    }

    .search-btn {
      background: $primary-yellow;
      border: none;
      border-radius: 14px;
      padding: 4px 12px;
      font-size: 13px;
      font-weight: 500;
      color: $text-primary;
      cursor: pointer;
    }
  }
}

// 分类标签栏
.category-tabs {
  display: flex;
  gap: 8px;
  padding: 8px 16px 16px;
  background: $primary-yellow;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .tab-item {
    flex-shrink: 0;
    padding: 6px 16px;
    border-radius: 16px;
    font-size: 14px;
    color: $text-secondary;
    background: rgba(255, 255, 255, 0.5);
    cursor: pointer;
    transition: all 0.2s ease;

    &.active {
      background: #333333;
      color: #FFD93D;
      font-weight: 600;
    }
  }
}

// Banner区域
.banner-section {
  margin: 0 16px 16px;
  background: linear-gradient(135deg, #FFE066 0%, #FFD93D 100%);
  border-radius: 12px;
  padding: 16px;

  .banner-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .banner-left {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .banner-tag {
      display: inline-block;
      background: rgba(255, 255, 255, 0.8);
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      color: $text-primary;
      width: fit-content;
    }

    .banner-title {
      font-size: 18px;
      font-weight: 700;
      color: $text-primary;
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
    }
  }

  .banner-right {
    .test-box-image {
      width: 80px;
      height: 80px;
      background: rgba(255, 255, 255, 0.6);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;

      .test-box-placeholder {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

        .el-icon {
          font-size: 28px;
          color: $primary-yellow;
        }
      }
    }
  }
}

// 区块标题
.section-header {
  padding: 0 16px 12px;

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

// 话题列表
.topic-list {
  padding: 0 16px;

  .topic-item {
    margin-bottom: 20px;

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

    .product-scroll {
      display: flex;
      gap: 12px;
      overflow-x: auto;
      scrollbar-width: none;
      -ms-overflow-style: none;
      padding-bottom: 4px;

      &::-webkit-scrollbar {
        display: none;
      }
    }

    .product-card {
      flex-shrink: 0;
      width: 280px;
      background: $card-bg;
      border-radius: 12px;
      padding: 12px;
      display: flex;
      gap: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

      .product-image {
        width: 80px;
        height: 80px;
        flex-shrink: 0;

        .product-img-placeholder {
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;

          .el-icon {
            font-size: 32px;
            color: #ccc;
          }
        }
      }

      .product-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-width: 0;

        .product-title {
          font-size: 14px;
          font-weight: 500;
          color: $text-primary;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .product-sales {
          font-size: 12px;
          color: $text-tertiary;
        }

        .product-bottom {
          display: flex;
          justify-content: space-between;
          align-items: center;

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
          }
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
