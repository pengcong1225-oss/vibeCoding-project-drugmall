<template>
  <div class="chronic-tab-section">
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
      <div class="service-card blue-card">
        <div class="card-header">
          <h4>慢病专区</h4>
          <p>正品保障 长期用药</p>
        </div>
        <div class="card-products">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=80&h=80&fit=crop" alt="产品1" />
            <span class="product-tag">降压药</span>
          </div>
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1585435557343-3b092031a831?w=80&h=80&fit=crop" alt="产品2" />
            <span class="product-tag">降糖药</span>
          </div>
        </div>
        <div class="rider-icon">💊</div>
      </div>

      <div class="service-card green-card">
        <div class="card-header">
          <h4>定期送药</h4>
          <p>自动续方 按时送达</p>
        </div>
        <div class="card-products single">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=80&h=80&fit=crop" alt="产品" />
            <span class="product-tag">省心服务</span>
          </div>
        </div>
      </div>

      <div class="service-card orange-card">
        <div class="card-header">
          <h4>用药指导</h4>
          <p>专业药师 在线答疑</p>
        </div>
        <div class="card-products single">
          <div class="product-thumb">
            <img src="https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=80&h=80&fit=crop" alt="产品" />
            <span class="product-tag">免费咨询</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 慢病分类快捷入口 -->
    <div class="chronic-categories">
      <div class="category-item" @click="goToCategory('hypertension')">
        <div class="category-icon" style="background: linear-gradient(135deg, #FF6B6B 0%, #EE5A6F 100%)">
          <span>🫀</span>
        </div>
        <span class="category-name">高血压</span>
      </div>
      <div class="category-item" @click="goToCategory('diabetes')">
        <div class="category-icon" style="background: linear-gradient(135deg, #4ECDC4 0%, #44A08D 100%)">
          <span>🩸</span>
        </div>
        <span class="category-name">糖尿病</span>
      </div>
      <div class="category-item" @click="goToCategory('heart')">
        <div class="category-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span>❤️</span>
        </div>
        <span class="category-name">心脏病</span>
      </div>
      <div class="category-item" @click="goToCategory('respiratory')">
        <div class="category-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span>🫁</span>
        </div>
        <span class="category-name">呼吸系统</span>
      </div>
    </div>

    <!-- 活动Banner -->
    <div class="activity-banner">
      <img src="https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=150&fit=crop" alt="慢病管理" />
      <div class="activity-overlay">
        <h4>慢病管理 从规范用药开始</h4>
        <p>专业药师一对一用药指导</p>
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
          <div v-if="product.isRx" class="rx-badge">处方药</div>
        </div>
        <div class="product-info">
          <h4 class="product-name">{{ product.name }}</h4>
          <p class="product-spec">{{ product.spec }}</p>
          <div class="product-tags">
            <span v-for="tag in product.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
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

// 金刚位数据 - 慢病关怀分类真实图片
const kingkongItems = ref([
  { id: '1', name: '高血压', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#FFEBEE', isActive: true },
  { id: '2', name: '糖尿病', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#E8F5E9' },
  { id: '3', name: '心脏病', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#FFF3E0' },
  { id: '4', name: '高血脂', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#E3F2FD' },
  { id: '5', name: '哮喘/呼吸', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#F3E5F5' },
  { id: '6', name: '痛风', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FBE9E7' },
  { id: '7', name: '肝病', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i1/O1CN01XY8X0i1GxPFS0YlFW_!!6000000000683-55-tps-83-82.svg', bgColor: '#FFF8E1' },
  { id: '8', name: '肾病', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i3/O1CN01MNj3nA1q3Hq0q0ZqL_!!6000000005438-55-tps-83-82.svg', bgColor: '#E0F7FA' },
  { id: '9', name: '甲状腺', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg', bgColor: '#E8EAF6' },
  { id: '10', name: '骨质疏松', icon: '', iconUrl: 'https://img.alicdn.com/imgextra/i2/O1CN01O0et1z1Jog3zZ1Dgn_!!6000000001069-55-tps-83-82.svg', bgColor: '#FCE4EC' }
])

// 商品数据 - 慢病用药
const products = ref([
  {
    id: '1',
    name: '[拜新同]硝苯地平控释片',
    spec: '30mg*7片/盒',
    price: '52.8',
    sales: '1.2万',
    deliveryTime: '28',
    isRx: true,
    tags: ['降压药', '进口'],
    image: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=200&h=200&fit=crop'
  },
  {
    id: '2',
    name: '[格华止]盐酸二甲双胍片',
    spec: '0.5g*20片/盒',
    price: '28.5',
    sales: '8560',
    deliveryTime: '25',
    isRx: true,
    tags: ['降糖药'],
    image: 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=200&h=200&fit=crop'
  },
  {
    id: '3',
    name: '[立普妥]阿托伐他汀钙片',
    spec: '20mg*7片/盒',
    price: '45.0',
    sales: '6230',
    deliveryTime: '30',
    isRx: true,
    tags: ['降脂药', '进口'],
    image: 'https://images.unsplash.com/photo-1626285861696-9f0bf5a49c6d?w=200&h=200&fit=crop'
  },
  {
    id: '4',
    name: '[舒利迭]沙美特罗替卡松粉吸入剂',
    spec: '50/250μg*60泡/盒',
    price: '198.0',
    sales: '3420',
    deliveryTime: '35',
    isRx: true,
    tags: ['哮喘', '进口'],
    image: 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=200&h=200&fit=crop'
  },
  {
    id: '5',
    name: '[拜阿司匹灵]阿司匹林肠溶片',
    spec: '100mg*30片/盒',
    price: '18.5',
    sales: '2.5万',
    deliveryTime: '20',
    isRx: true,
    tags: ['抗血栓'],
    image: 'https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=200&h=200&fit=crop'
  },
  {
    id: '6',
    name: '[优甲乐]左甲状腺素钠片',
    spec: '50μg*100片/盒',
    price: '32.0',
    sales: '9800',
    deliveryTime: '25',
    isRx: true,
    tags: ['甲状腺', '进口'],
    image: 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=200&h=200&fit=crop'
  }
])

// 金刚位点击
const handleKingkongClick = (item: any) => {
  kingkongItems.value.forEach(k => k.isActive = false)
  item.isActive = true
}

// 去分类
const goToCategory = (category: string) => {
  router.push(`/category?type=chronic&sub=${category}`)
}

// 去商品详情
const goToProduct = (id: string) => {
  router.push(`/drug/${id}`)
}
</script>

<style scoped lang="scss">
// 慢病关怀主题色 - 医疗青绿色系
$theme-teal: #00A896;
$theme-teal-light: #00C9B7;
$theme-teal-bg: #E0F7F5;
$accent-orange: #FF6B35;
$accent-yellow: #FFB800;
$price-red: #FF4D4F;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;

.chronic-tab-section {
  padding: 0 0 12px;
  background: linear-gradient(180deg, $theme-teal-bg 0%, #F5F5F5 100%);
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
      color: $theme-teal;
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

    &.blue-card {
      background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);

      .card-header h4 {
        color: #1976D2;
      }
    }

    &.green-card {
      background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);

      .card-header h4 {
        color: #388E3C;
      }
    }

    &.orange-card {
      background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);

      .card-header h4 {
        color: #F57C00;
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

// 慢病分类快捷入口
.chronic-categories {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  .category-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;

    .category-icon {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 8px;
      font-size: 28px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .category-name {
      font-size: 12px;
      color: $text-secondary;
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
      position: relative;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .rx-badge {
        position: absolute;
        top: 8px;
        left: 8px;
        background: rgba(255, 77, 79, 0.9);
        color: #fff;
        font-size: 10px;
        padding: 2px 6px;
        border-radius: 4px;
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

      .product-tags {
        display: flex;
        gap: 4px;
        margin-bottom: 6px;
        flex-wrap: wrap;

        .tag {
          font-size: 10px;
          color: $theme-teal;
          background: rgba(0, 168, 150, 0.1);
          padding: 2px 6px;
          border-radius: 4px;
        }
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
