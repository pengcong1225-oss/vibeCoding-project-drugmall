<template>
  <div class="tonic-tab-section">
    <!-- 金刚位网格 - 2行5列 -->
    <div class="kingkong-section">
      <div class="kingkong-scroll" ref="kingkongRef">
        <div 
          v-for="item in kingkongItems" 
          :key="item.id"
          class="kingkong-item"
          :class="{ 'is-active': item.isActive }"
          @click="handleKingkongClick(item)"
        >
          <div class="kingkong-icon-wrapper" :style="{ background: item.bgColor }">
            <img v-if="item.iconUrl" :src="item.iconUrl" class="kingkong-icon" />
            <span v-else class="kingkong-icon-text">{{ item.icon }}</span>
          </div>
          <span class="kingkong-name">{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 服务卡片区域 -->
    <div class="service-cards">
      <div class="service-card yellow-card">
        <div class="card-header">
          <h4>附近急送</h4>
          <p>官方正品 大牌好价</p>
        </div>
        <div class="card-products">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=80&h=80&fit=crop" alt="产品1" />
            <span class="product-tag">平台优选</span>
          </div>
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1585435557343-3b092031a831?w=80&h=80&fit=crop" alt="产品2" />
            <span class="product-tag">药店直送</span>
          </div>
        </div>
        <div class="rider-icon">🛵</div>
      </div>

      <div class="service-card orange-card">
        <div class="card-header">
          <h4>美团自营</h4>
          <p>品质检测</p>
        </div>
        <div class="card-products single">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=80&h=80&fit=crop" alt="产品" />
            <span class="product-tag">正品好价</span>
          </div>
        </div>
      </div>

      <div class="service-card purple-card">
        <div class="card-header">
          <h4>原装进口</h4>
          <p>正品保障</p>
        </div>
        <div class="card-products single">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=80&h=80&fit=crop" alt="产品" />
            <span class="product-tag">包邮包税</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动Banner -->
    <div class="activity-banner">
      <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=150&fit=crop" alt="春季活动" />
      <div class="activity-overlay">
        <h4>初春防晒 全年美白</h4>
        <p>你买防晒霜了吗</p>
      </div>
    </div>

    <!-- 商品瀑布流 -->
    <div class="product-waterfall">
      <div 
        v-for="product in products" 
        :key="product.id"
        class="product-card"
        @click="goToProduct(product.id)"
      >
        <div class="product-image">
          <img :src="product.image" :alt="product.name" />
        </div>
        <div class="product-info">
          <h4 class="product-name">{{ product.name }}</h4>
          <p class="product-spec">{{ product.spec }}</p>
          <div class="product-sales">月售{{ product.sales }}</div>
          <div class="product-bottom">
            <div class="product-price">
              <span class="price-symbol">¥</span>
              <span class="price-num">{{ product.price }}</span>
            </div>
            <div class="delivery-time">
              <el-icon><Timer /></el-icon>
              <span>{{ product.deliveryTime }}分钟</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Timer } from '@element-plus/icons-vue'

const router = useRouter()

// 金刚位数据 - 滋补保健分类真实图片
const kingkongItems = ref([
  { id: '1', name: '东阿阿胶', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FFF3E0', isActive: true },
  { id: '2', name: '蛋白粉', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#E8F5E9' },
  { id: '3', name: '益生菌', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#E3F2FD' },
  { id: '4', name: '钙铁锌', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#FFF8E1' },
  { id: '5', name: '维生素', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FFEBEE' },
  { id: '6', name: '鱼油/DHA', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#E0F2F1' },
  { id: '7', name: '褪黑素', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#F3E5F5' },
  { id: '8', name: '枸杞/黄芪', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#FCE4EC' },
  { id: '9', name: '燕窝', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#E8EAF6' },
  { id: '10', name: '蜂蜜/蜂胶', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FFF3E0' }
])

// 商品数据
const products = ref([
  {
    id: '1',
    name: '[海王金樽]海王金樽牌牡蛎大豆肽肉碱口服液...',
    spec: '50ml/瓶',
    price: '20',
    sales: '10',
    deliveryTime: '15',
    image: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop'
  },
  {
    id: '2',
    name: '[汤臣倍健]褪黑素片',
    spec: '24g(400mg*60片)/瓶',
    price: '36.62',
    sales: '11',
    deliveryTime: '15',
    image: 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=200&h=200&fit=crop'
  },
  {
    id: '3',
    name: '[京都念慈菴]枇杷糖',
    spec: '45g(2.5g*18粒)/盒',
    price: '16.8',
    sales: '3',
    deliveryTime: '20',
    image: 'https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=200&h=200&fit=crop'
  },
  {
    id: '4',
    name: '[碧生源]常菁茶',
    spec: '100g(2.5g*40袋)/盒',
    price: '78.21',
    sales: '13',
    deliveryTime: '15',
    image: 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=200&h=200&fit=crop'
  },
  {
    id: '5',
    name: '[京都念慈菴]枇杷糖',
    spec: '45g(2.5g*18粒)/盒',
    price: '18.31',
    sales: '2',
    deliveryTime: '28',
    image: 'https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=200&h=200&fit=crop'
  },
  {
    id: '6',
    name: '高原安胶囊',
    spec: '0.3g*20粒/盒',
    price: '45',
    sales: '8',
    deliveryTime: '18',
    image: 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=200&h=200&fit=crop'
  }
])

// 金刚位点击
const handleKingkongClick = (item: any) => {
  kingkongItems.value.forEach(k => k.isActive = false)
  item.isActive = true
}

// 去商品详情
const goToProduct = (id: string) => {
  router.push(`/drug/${id}`)
}
</script>

<style scoped lang="scss">
// 滋补保健主题色 - 完全复刻图片
$theme-blue: #4A90E2;
$theme-blue-light: #5BA3F5;
$theme-blue-bg: #E8F4FC;
$accent-orange: #FF9500;
$accent-yellow: #FFB800;
$price-red: #FF4D4F;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;

.tonic-tab-section {
  padding: 0 0 12px;
  background: linear-gradient(180deg, $theme-blue-bg 0%, #F5F5F5 100%);
  min-height: calc(100vh - 200px);
}

// 金刚位
.kingkong-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.kingkong-scroll {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px 8px;
}

.kingkong-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;

  &.is-active {
    .kingkong-name {
      color: $theme-blue;
      font-weight: 600;
    }
  }

  .kingkong-icon-wrapper {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;
    overflow: hidden;

    .kingkong-icon {
      width: 36px;
      height: 36px;
      object-fit: contain;
    }

    .kingkong-icon-text {
      font-size: 18px;
      font-weight: bold;
      color: $text-primary;
    }
  }

  .kingkong-name {
    font-size: 12px;
    color: $text-secondary;
    text-align: center;
    line-height: 1.3;
  }
}

// 服务卡片
.service-cards {
  display: grid;
  grid-template-columns: 1.2fr 0.9fr 0.9fr;
  gap: 8px;
  margin-bottom: 12px;

  .service-card {
    background: #fff;
    border-radius: 12px;
    padding: 12px;
    position: relative;
    overflow: hidden;

    &.yellow-card {
      background: linear-gradient(135deg, #FFF8E7 0%, #FFEFD5 100%);

      .card-header h4 {
        color: #D4A574;
      }
    }

    &.orange-card {
      background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);

      .card-header h4 {
        color: #E65100;
      }
    }

    &.purple-card {
      background: linear-gradient(135deg, #F3E5F5 0%, #E1BEE7 100%);

      .card-header h4 {
        color: #7B1FA2;
      }
    }

    .card-header {
      margin-bottom: 8px;

      h4 {
        font-size: 14px;
        font-weight: bold;
        margin-bottom: 2px;
      }

      p {
        font-size: 11px;
        color: $text-tertiary;
      }
    }

    .card-products {
      display: flex;
      gap: 8px;

      &.single {
        justify-content: center;
      }

      .product-thumb {
        text-align: center;

        img {
          width: 60px;
          height: 60px;
          object-fit: cover;
          border-radius: 8px;
          background: #fff;
        }

        .product-tag {
          display: block;
          font-size: 10px;
          color: $text-tertiary;
          margin-top: 4px;
        }
      }
    }

    .rider-icon {
      position: absolute;
      top: 8px;
      right: 8px;
      font-size: 24px;
    }
  }
}

// 活动Banner
.activity-banner {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;

  img {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }

  .activity-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 16px;
    background: linear-gradient(transparent 0%, rgba(0, 0, 0, 0.6) 100%);
    color: #fff;

    h4 {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 4px;
    }

    p {
      font-size: 12px;
      opacity: 0.9;
    }
  }
}

// 商品瀑布流
.product-waterfall {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;

  .product-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    .product-image {
      width: 100%;
      height: 160px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .product-info {
      padding: 12px;

      .product-name {
        font-size: 13px;
        color: $text-primary;
        line-height: 1.4;
        margin-bottom: 4px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .product-spec {
        font-size: 11px;
        color: $text-tertiary;
        margin-bottom: 6px;
      }

      .product-sales {
        font-size: 11px;
        color: $text-tertiary;
        margin-bottom: 8px;
      }

      .product-bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .product-price {
          color: $price-red;

          .price-symbol {
            font-size: 12px;
          }

          .price-num {
            font-size: 18px;
            font-weight: bold;
          }
        }

        .delivery-time {
          display: flex;
          align-items: center;
          gap: 2px;
          font-size: 11px;
          color: $accent-orange;

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style>
