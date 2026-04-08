<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="location" @click="showLocationPicker = true">
        <el-icon><Location /></el-icon>
        <span class="location-text">{{ currentLocation }}</span>
        <el-icon><ArrowDown /></el-icon>
      </div>
      <div class="search-box" @click="goToSearch">
        <el-icon><Search /></el-icon>
        <span class="placeholder">搜索药品、症状</span>
        <div class="scan-btn" @click.stop="scanCode">
          <el-icon><FullScreen /></el-icon>
        </div>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="banner-section">
      <el-carousel :interval="4000" type="card" height="120px">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h3>{{ banner.title }}</h3>
              <p>{{ banner.subtitle }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="action-item" @click="goToAIAssistant">
        <div class="icon ai-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <span>AI助手</span>
      </div>
      <div class="action-item" @click="goToInquiry">
        <div class="icon inquiry-icon">
          <el-icon><FirstAidKit /></el-icon>
        </div>
        <span>在线问诊</span>
      </div>
      <div class="action-item" @click="goToPrescription">
        <div class="icon prescription-icon">
          <el-icon><Document /></el-icon>
        </div>
        <span>我的处方</span>
      </div>
      <div class="action-item" @click="goToOrder">
        <div class="icon order-icon">
          <el-icon><ShoppingBag /></el-icon>
        </div>
        <span>我的订单</span>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="category-section">
      <div class="section-header">
        <h3>药品分类</h3>
        <span class="more" @click="goToCategory">查看更多 <el-icon><ArrowRight /></el-icon></span>
      </div>
      <div class="category-grid">
        <div 
          v-for="category in categories.slice(0, 8)" 
          :key="category.id" 
          class="category-item"
          @click="goToCategoryList(category.id)"
        >
          <img :src="category.icon" :alt="category.name" />
          <span>{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 热门推荐 -->
    <div class="hot-section">
      <div class="section-header">
        <h3>热门推荐</h3>
        <span class="more" @click="goToMore">查看更多 <el-icon><ArrowRight /></el-icon></span>
      </div>
      <div class="drug-list">
        <div 
          v-for="drug in hotDrugs" 
          :key="drug.id" 
          class="drug-card"
          @click="goToDrugDetail(drug.id)"
        >
          <div class="drug-image">
            <img :src="drug.image" :alt="drug.name" />
            <div v-if="drug.isRx" class="rx-tag">处方药</div>
          </div>
          <div class="drug-info">
            <h4 class="drug-name">{{ drug.name }}</h4>
            <p class="specification">{{ drug.specification }}</p>
            <div class="price-section">
              <span class="price">¥{{ drug.price }}</span>
              <span v-if="drug.originalPrice" class="original-price">¥{{ drug.originalPrice }}</span>
            </div>
            <div class="sales">已售 {{ drug.sales }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 附近药店 -->
    <div class="nearby-section">
      <div class="section-header">
        <h3>附近药店</h3>
        <span class="more" @click="goToMoreStores">查看全部 <el-icon><ArrowRight /></el-icon></span>
      </div>
      <div class="store-list">
        <div 
          v-for="store in nearbyStores" 
          :key="store.id" 
          class="store-card"
          @click="goToStore(store.id)"
        >
          <div class="store-header">
            <h4>{{ store.name }}</h4>
            <div class="rating">
              <el-icon><StarFilled /></el-icon>
              <span>{{ store.rating }}</span>
            </div>
          </div>
          <div class="store-info">
            <span class="distance">{{ store.distance }}</span>
            <span class="time">{{ store.deliveryTime }}分钟</span>
          </div>
          <div class="store-tags">
            <span v-for="tag in store.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部安全区域 -->
    <div class="safe-area-bottom"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { mockDrugs, mockCategories } from '@/api/mock'

const router = useRouter()

// 当前位置
const currentLocation = ref('北京市朝阳区')
const showLocationPicker = ref(false)

// 轮播图数据
const banners = ref([
  { title: '新人大礼包', subtitle: '新人专享满39减20', bg: 'linear-gradient(135deg, #00b578 0%, #00c78a 100%)' },
  { title: '24小时送药', subtitle: '夜间急用药最快30分钟达', bg: 'linear-gradient(135deg, #1890ff 0%, #40a9ff 100%)' },
  { title: '正品保障', subtitle: '国家药监局认证正规药店', bg: 'linear-gradient(135deg, #722ed1 0%, #b37feb 100%)' }
])

// 分类数据
const categories = ref(mockCategories)

// 热门药品
const hotDrugs = computed(() => mockDrugs.slice(0, 4))

// 附近药店
const nearbyStores = ref([
  {
    id: '1',
    name: '同仁堂大药房(朝阳店)',
    rating: 4.9,
    distance: '1.2km',
    deliveryTime: 25,
    tags: ['24小时营业', '医保定点', '急速达']
  },
  {
    id: '2',
    name: '老百姓大药房',
    rating: 4.8,
    distance: '0.8km',
    deliveryTime: 20,
    tags: ['正品保障', '满减优惠']
  },
  {
    id: '3',
    name: '海王星辰健康药房',
    rating: 4.7,
    distance: '1.5km',
    deliveryTime: 30,
    tags: ['夜间送药', '专业服务']
  }
])

// 页面跳转方法
const goToSearch = () => router.push('/search')
const goToCategory = () => router.push('/category')
const goToDrugDetail = (id: string) => router.push(`/drug/${id}`)
const goToAIAssistant = () => router.push('/ai-assistant')
const goToInquiry = () => router.push('/inquiry')
const goToPrescription = () => router.push('/prescription')
const goToOrder = () => router.push('/order/list')
const goToMore = () => router.push('/category')
const goToMoreStores = () => ElMessage.info('附近药店列表功能开发中')
const goToStore = (id: string) => ElMessage.info(`药店详情功能开发中，ID: ${id}`)
const goToCategoryList = (id: string) => router.push(`/category?id=${id}`)

const scanCode = () => {
  ElMessage.success('扫码功能：扫描药品条形码快速加购')
}

onMounted(() => {
  // 页面加载完成后的初始化
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.home-page {
  min-height: 100vh;
  background-color: $bg-primary;
  padding-bottom: 20px;
}

// 搜索栏
.search-header {
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  
  .location {
    display: flex;
    align-items: center;
    color: $text-white;
    margin-bottom: $spacing-md;
    font-size: $font-md;
    
    .location-text {
      margin: 0 $spacing-xs;
      font-weight: 500;
    }
  }
  
  .search-box {
    display: flex;
    align-items: center;
    background: $bg-white;
    border-radius: $radius-xl;
    padding: $spacing-sm $spacing-md;
    color: $text-tertiary;
    
    .placeholder {
      flex: 1;
      margin-left: $spacing-sm;
      font-size: $font-md;
    }
    
    .scan-btn {
      padding: $spacing-xs $spacing-sm;
      border-left: 1px solid $border-light;
      margin-left: $spacing-sm;
      color: $text-secondary;
    }
  }
}

// 轮播图
.banner-section {
  margin: $spacing-md;
  
  .banner-item {
    height: 100%;
    border-radius: $radius-lg;
    display: flex;
    align-items: center;
    padding: $spacing-lg;
    color: $text-white;
    
    .banner-content {
      h3 {
        font-size: $font-xl;
        font-weight: 600;
        margin-bottom: $spacing-xs;
      }
      
      p {
        font-size: $font-sm;
        opacity: 0.9;
      }
    }
  }
}

// 快捷入口
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-md;
  margin: $spacing-md;
  padding: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  
  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
    
    .icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: $spacing-sm;
      font-size: 24px;
      color: $text-white;
      
      &.ai-icon {
        background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%);
      }
      
      &.inquiry-icon {
        background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      }
      
      &.prescription-icon {
        background: linear-gradient(135deg, $info 0%, #69c0ff 100%);
      }
      
      &.order-icon {
        background: linear-gradient(135deg, $warning 0%, #ffc53d 100%);
      }
    }
    
    span {
      font-size: $font-sm;
      color: $text-secondary;
    }
  }
}

// 分类导航
.category-section {
  margin: $spacing-md;
  padding: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    
    h3 {
      font-size: $font-lg;
      font-weight: 600;
      color: $text-primary;
    }
    
    .more {
      display: flex;
      align-items: center;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
  
  .category-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-md;
    
    .category-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      
      img {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        margin-bottom: $spacing-xs;
      }
      
      span {
        font-size: $font-xs;
        color: $text-secondary;
      }
    }
  }
}

// 热门推荐
.hot-section {
  margin: $spacing-md;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    
    h3 {
      font-size: $font-lg;
      font-weight: 600;
      color: $text-primary;
    }
    
    .more {
      display: flex;
      align-items: center;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
  
  .drug-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-md;
    
    .drug-card {
      background: $bg-white;
      border-radius: $radius-lg;
      overflow: hidden;
      box-shadow: $shadow-sm;
      cursor: pointer;
      
      .drug-image {
        position: relative;
        width: 100%;
        padding-top: 100%;
        
        img {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .rx-tag {
          position: absolute;
          top: $spacing-xs;
          left: $spacing-xs;
          background: rgba(255, 77, 79, 0.9);
          color: $text-white;
          font-size: $font-xs;
          padding: 2px 6px;
          border-radius: $radius-sm;
        }
      }
      
      .drug-info {
        padding: $spacing-sm;
        
        .drug-name {
          font-size: $font-sm;
          font-weight: 500;
          color: $text-primary;
          margin-bottom: 4px;
          @extend .text-ellipsis;
        }
        
        .specification {
          font-size: $font-xs;
          color: $text-tertiary;
          margin-bottom: $spacing-xs;
          @extend .text-ellipsis;
        }
        
        .price-section {
          display: flex;
          align-items: baseline;
          gap: $spacing-xs;
          margin-bottom: 4px;
          
          .price {
            font-size: $font-md;
            font-weight: 600;
            color: $error;
          }
          
          .original-price {
            font-size: $font-xs;
            color: $text-tertiary;
            text-decoration: line-through;
          }
        }
        
        .sales {
          font-size: $font-xs;
          color: $text-tertiary;
        }
      }
    }
  }
}

// 附近药店
.nearby-section {
  margin: $spacing-md;
  margin-bottom: $spacing-xxl;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    
    h3 {
      font-size: $font-lg;
      font-weight: 600;
      color: $text-primary;
    }
    
    .more {
      display: flex;
      align-items: center;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
  
  .store-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
    
    .store-card {
      background: $bg-white;
      border-radius: $radius-lg;
      padding: $spacing-md;
      box-shadow: $shadow-sm;
      cursor: pointer;
      
      .store-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: $spacing-xs;
        
        h4 {
          font-size: $font-md;
          font-weight: 500;
          color: $text-primary;
        }
        
        .rating {
          display: flex;
          align-items: center;
          gap: 4px;
          color: $warning;
          font-size: $font-sm;
          font-weight: 500;
        }
      }
      
      .store-info {
        display: flex;
        gap: $spacing-md;
        margin-bottom: $spacing-sm;
        font-size: $font-sm;
        color: $text-secondary;
      }
      
      .store-tags {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-xs;
        
        .tag {
          padding: 2px 8px;
          background: rgba($primary, 0.1);
          color: $primary;
          font-size: $font-xs;
          border-radius: $radius-sm;
        }
      }
    }
  }
}

// 安全区域
.safe-area-bottom {
  height: calc($tabbar-height + $safe-area-bottom + 20px);
}
</style>
