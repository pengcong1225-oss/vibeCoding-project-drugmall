<template>
  <div class="store-detail-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">药店详情</span>
      <div class="share-btn" @click="handleShare">
        <el-icon><Share /></el-icon>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <template v-else-if="store">
      <!-- 药店信息卡片 -->
      <div class="store-card">
        <div class="store-header">
          <div class="store-info-main">
            <h1 class="store-name">{{ store.name }}</h1>
            <div class="store-rating">
              <el-rate v-model="store.rating" disabled :colors="['#FF9500', '#FF9500', '#FF9500']" />
              <span class="rating-text">{{ store.rating }}分</span>
            </div>
          </div>
          <div class="store-status" :class="{ open: store.isOpen }">
            {{ store.isOpen ? '营业中' : '已打烊' }}
          </div>
        </div>

        <div class="store-tags">
          <span v-for="tag in store.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>

        <div class="store-details">
          <div class="detail-item">
            <el-icon><Location /></el-icon>
            <span>{{ store.address }}</span>
          </div>
          <div class="detail-item">
            <el-icon><Clock /></el-icon>
            <span>配送约{{ store.deliveryTime }}分钟 · 起送¥{{ store.minDelivery }}</span>
          </div>
          <div class="detail-item">
            <el-icon><Position /></el-icon>
            <span>距离您{{ store.distance }}</span>
          </div>
          <div class="detail-item">
            <el-icon><Phone /></el-icon>
            <span>{{ store.phone }}</span>
          </div>
        </div>
      </div>

      <!-- 优惠券区域 -->
      <div v-if="coupons.length > 0" class="coupon-section">
        <div class="section-header">
          <h3 class="section-title">店铺优惠</h3>
          <span class="more-btn">查看全部</span>
        </div>
        <div class="coupon-list">
          <div v-for="coupon in coupons.slice(0, 3)" :key="coupon.id" class="coupon-card">
            <div class="coupon-left">
              <div class="coupon-value">
                <span class="symbol">¥</span>
                <span class="amount">{{ coupon.value }}</span>
              </div>
              <div class="coupon-condition">满{{ coupon.minSpend }}可用</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-name">{{ coupon.name }}</div>
              <button class="coupon-btn" @click="claimCoupon(coupon)">领取</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分类和商品列表 -->
      <div class="products-section">
        <!-- 分类标签 -->
        <div class="category-tabs">
          <div
            v-for="cat in categories"
            :key="cat.id"
            :class="['tab-item', { active: activeCategory === cat.id }]"
            @click="handleCategoryChange(cat.id)"
          >
            {{ cat.name }}
          </div>
        </div>

        <!-- 商品加载状态 -->
        <div v-if="drugsLoading" class="loading-container">
          <el-skeleton v-for="i in 4" :key="i" animated>
            <template #template>
              <div style="padding: 12px; display: flex; gap: 12px;">
                <el-skeleton-item variant="image" style="width: 80px; height: 80px; border-radius: 8px;" />
                <div style="flex: 1; display: flex; flex-direction: column; gap: 8px;">
                  <el-skeleton-item variant="text" style="width: 60%;" />
                  <el-skeleton-item variant="text" style="width: 40%;" />
                  <el-skeleton-item variant="text" style="width: 30%;" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>

        <!-- 空状态 -->
        <div v-else-if="filteredDrugs.length === 0" class="empty-state">
          <el-empty description="暂无药品" :image-size="120" />
        </div>

        <!-- 商品列表 -->
        <div v-else class="product-list">
          <div
            v-for="drug in filteredDrugs"
            :key="drug.id"
            class="product-item"
            @click="goToDrugDetail(drug.id)"
          >
            <div class="product-image" :style="{ backgroundColor: drug.imageColor || '#3B8CFF' }">
              <div class="image-placeholder">
                <span class="image-text">{{ drug.imageText || drug.name?.slice(0, 2) }}</span>
              </div>
              <span v-if="drug.isRx" class="rx-tag">处方药</span>
            </div>
            <div class="product-info">
              <div class="product-name">{{ drug.name }}</div>
              <div class="product-spec">{{ drug.specification }}</div>
              <div class="product-sales">月售 {{ drug.sales || 0 }}</div>
              <div class="product-footer">
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ drug.price?.toFixed(2) }}</span>
                </div>
                <button class="add-cart-btn" @click.stop="addToCart(drug)">
                  <el-icon><Plus /></el-icon>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <el-empty description="加载失败" :image-size="120">
        <el-button type="primary" @click="loadStoreDetail">重新加载</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Share, Location, Clock, Position, Phone, Plus } from '@element-plus/icons-vue'
import { getStoreDetail, getStoreDrugs, type StoreInfo, type StoreDrug, type StoreCoupon } from '@/api/modules/store'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(false)
const drugsLoading = ref(false)

// 药店数据
const store = ref<StoreInfo | null>(null)

const drugsList = ref<StoreDrug[]>([])

const coupons = ref<StoreCoupon[]>([])

const cartStore = useCartStore()

// 分类
const categories = ref([
  { id: 'all', name: '全部' },
  { id: 'rx', name: '处方药' },
  { id: 'otc', name: '非处方药' },
  { id: 'health', name: '保健品' },
  { id: 'device', name: '医疗器械' }
])

const activeCategory = ref('all')

// 过滤后的药品列表
const filteredDrugs = computed(() => {
  if (activeCategory.value === 'all') {
    return drugsList.value
  } else if (activeCategory.value === 'rx') {
    return drugsList.value.filter(d => d.isRx)
  } else if (activeCategory.value === 'otc') {
    return drugsList.value.filter(d => !d.isRx)
  }
  return drugsList.value
})

// 加载药店详情
const loadStoreDetail = async () => {
  loading.value = true
  const storeId = route.params.id as string

  try {
    const res = await getStoreDetail(storeId)
    store.value = res || {
      id: storeId,
      name: '同仁堂大药房(朝阳店)',
      rating: 4.9,
      isOpen: true,
      deliveryTime: 25,
      minDelivery: 20,
      distance: '1.2km',
      address: '北京市朝阳区建国路88号SOHO现代城1层',
      phone: '010-12345678',
      tags: ['24小时营业', '医保定点', '正品保障', '急速达']
    }

    coupons.value = [
      { id: '1', name: '满减券', value: 10, minSpend: 50 },
      { id: '2', name: '新客券', value: 15, minSpend: 30 },
      { id: '3', name: '会员券', value: 20, minSpend: 100 }
    ]

    await fetchDrugs()
  } catch (error) {
    console.error('获取药店信息失败:', error)
    store.value = {
      id: storeId,
      name: '同仁堂大药房(朝阳店)',
      rating: 4.9,
      isOpen: true,
      deliveryTime: 25,
      minDelivery: 20,
      distance: '1.2km',
      address: '北京市朝阳区建国路88号SOHO现代城1层',
      phone: '010-12345678',
      tags: ['24小时营业', '医保定点', '正品保障', '急速达']
    }
    coupons.value = [
      { id: '1', name: '满减券', value: 10, minSpend: 50 },
      { id: '2', name: '新客券', value: 15, minSpend: 30 },
      { id: '3', name: '会员券', value: 20, minSpend: 100 }
    ]
    await fetchDrugs()
  } finally {
    loading.value = false
  }
}

// 获取药品列表
const fetchDrugs = async () => {
  drugsLoading.value = true
  try {
    const storeId = route.params.id as string
    const res = await getStoreDrugs(storeId)
    drugsList.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取药品列表失败:', error)
    drugsList.value = []
  } finally {
    drugsLoading.value = false
  }
}

// 切换分类
const handleCategoryChange = (categoryId: string) => {
  activeCategory.value = categoryId
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到药品详情
const goToDrugDetail = (id: string) => {
  router.push(`/drug/${id}`)
}

// 分享
const handleShare = () => {
  ElMessage.info('分享功能开发中')
}

// 领取优惠券
const claimCoupon = async (coupon: StoreCoupon) => {
  try {
    ElMessage.success(`成功领取${coupon.name}¥${coupon.value}`)
  } catch (error) {
    ElMessage.error('领取失败')
  }
}

const addToCart = async (drug: StoreDrug) => {
  try {
    await cartStore.addItem({
      drugId: drug.id,
      name: drug.name,
      price: drug.price,
      quantity: 1,
      specification: drug.specification,
      manufacturer: '',
      image: drug.image || '',
      disease: '',
      usage: '',
      isRx: drug.isRx
    })
    ElMessage.success(`${drug.name}已加入购物车`)
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

// 初始化
onMounted(() => {
  loadStoreDetail()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.store-detail-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 20px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: $text-white;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    flex: 1;
    text-align: center;
    margin-right: 36px;
  }

  .share-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

// 加载和错误状态
.loading-container,
.error-state {
  padding: $spacing-lg;
  margin-top: $spacing-xl;
}

// 药店信息卡片
.store-card {
  background: $bg-white;
  margin: $spacing-md;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: $shadow-sm;

  .store-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $spacing-md;

    .store-info-main {
      flex: 1;

      .store-name {
        font-size: $font-xl;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 $spacing-sm 0;
      }

      .store-rating {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .rating-text {
          font-size: $font-sm;
          color: $warning;
          font-weight: 500;
        }
      }
    }

    .store-status {
      padding: $spacing-xs $spacing-md;
      border-radius: $radius-full;
      font-size: $font-xs;
      font-weight: 500;

      &.open {
        background: rgba($success, 0.1);
        color: $success;
      }

      &:not(.open) {
        background: rgba($text-tertiary, 0.1);
        color: $text-tertiary;
      }
    }
  }

  .store-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;
    margin-bottom: $spacing-md;

    .tag {
      font-size: $font-xs;
      color: $primary;
      background: rgba($primary, 0.08);
      padding: $spacing-xs $spacing-sm;
      border-radius: $radius-sm;
    }
  }

  .store-details {
    .detail-item {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-sm;
      font-size: $font-sm;
      color: $text-secondary;

      &:last-child {
        margin-bottom: 0;
      }

      .el-icon {
        color: $primary;
        font-size: 16px;
      }
    }
  }
}

// 优惠券区域
.coupon-section {
  background: $bg-white;
  margin: 0 $spacing-md $spacing-md;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: $shadow-sm;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;

    .section-title {
      font-size: $font-md;
      font-weight: 600;
      color: $text-primary;
      margin: 0;
    }

    .more-btn {
      font-size: $font-sm;
      color: $primary;
      cursor: pointer;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .coupon-list {
    display: flex;
    gap: $spacing-md;
    overflow-x: auto;

    &::-webkit-scrollbar {
      display: none;
    }

    .coupon-card {
      display: flex;
      align-items: center;
      background: linear-gradient(135deg, #fff5f0 0%, #fff0f0 100%);
      border: 1px solid #ffd4d4;
      border-radius: $radius-md;
      padding: $spacing-md;
      min-width: 200px;
      flex-shrink: 0;

      .coupon-left {
        margin-right: $spacing-md;

        .coupon-value {
          color: $error;

          .symbol {
            font-size: $font-sm;
          }

          .amount {
            font-size: 28px;
            font-weight: bold;
          }
        }

        .coupon-condition {
          font-size: $font-xs;
          color: $text-tertiary;
          margin-top: $spacing-xs;
        }
      }

      .coupon-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: $spacing-sm;

        .coupon-name {
          font-size: $font-sm;
          color: $text-primary;
          font-weight: 500;
        }

        .coupon-btn {
          background: $error;
          color: $text-white;
          border: none;
          padding: $spacing-xs $spacing-md;
          border-radius: $radius-full;
          font-size: $font-xs;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            opacity: 0.9;
          }
        }
      }
    }
  }
}

// 商品区域
.products-section {
  background: $bg-white;
  margin: 0 $spacing-md;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;

  .category-tabs {
    display: flex;
    border-bottom: 1px solid $border-light;
    overflow-x: auto;
    padding: 0 $spacing-sm;

    &::-webkit-scrollbar {
      display: none;
    }

    .tab-item {
      padding: $spacing-md $spacing-lg;
      font-size: $font-md;
      color: $text-secondary;
      white-space: nowrap;
      cursor: pointer;
      position: relative;
      transition: all 0.2s;

      &:hover {
        color: $primary;
      }

      &.active {
        color: $primary;
        font-weight: 600;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 24px;
          height: 3px;
          background: $primary;
          border-radius: 2px;
        }
      }
    }
  }

  .loading-container {
    padding: $spacing-md;
  }

  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
  }

  .product-list {
    padding: $spacing-md;

    .product-item {
      display: flex;
      padding: $spacing-md 0;
      border-bottom: 1px solid $border-light;
      cursor: pointer;
      transition: background 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: $bg-gray;
        margin: 0 (-$spacing-md);
        padding: $spacing-md;
        border-radius: $radius-md;
      }

      .product-image {
        width: 80px;
        height: 80px;
        border-radius: $radius-md;
        overflow: hidden;
        margin-right: $spacing-md;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        .image-placeholder {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;

          .image-text {
            color: $text-white;
            font-size: 16px;
            font-weight: bold;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
          }
        }

        .rx-tag {
          position: absolute;
          top: 0;
          left: 0;
          background: $error;
          color: $text-white;
          font-size: $font-xs;
          padding: 2px 6px;
          border-radius: 0 0 $radius-sm 0;
        }
      }

      .product-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .product-name {
          font-size: $font-md;
          color: $text-primary;
          font-weight: 500;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .product-spec {
          font-size: $font-sm;
          color: $text-tertiary;
          margin-top: $spacing-xs;
        }

        .product-sales {
          font-size: $font-sm;
          color: $text-tertiary;
          margin-top: $spacing-xs;
        }

        .product-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: $spacing-sm;

          .product-price {
            color: $error;

            .price-symbol {
              font-size: $font-sm;
            }

            .price-value {
              font-size: $font-xl;
              font-weight: bold;
            }
          }

          .add-cart-btn {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
            color: $text-white;
            border: none;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s;
            box-shadow: 0 2px 8px rgba($primary, 0.3);

            &:hover {
              transform: scale(1.1);
            }

            &:active {
              transform: scale(0.95);
            }
          }
        }
      }
    }
  }
}
</style>
