<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ShoppingCart, HomeFilled, Plus, Minus, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { getDrugDetail } from '@/api/modules/drug'
import type { Drug } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 药品数据
const drug = ref<Drug | null>(null)
const loading = ref(true)
const error = ref(false)

// 当前选中的规格
const selectedSpec = ref(0)

// 购买数量
const quantity = ref(1)

// 说明书展开状态
const instructionExpanded = ref(false)

// 当前轮播索引
const currentSwiperIndex = ref(0)

// 图片列表
const images = computed(() => {
  if (!drug.value) return []
  if (drug.value.images?.length) {
    return drug.value.images
  }
  return drug.value.image ? [drug.value.image] : []
})

// 规格列表
const specs = computed(() => {
  if (!drug.value) return []
  return [
    {
      label: drug.value.specification || '默认规格',
      price: drug.value.price || 0,
      stock: drug.value.stock || 0
    }
  ]
})

// 加载药品数据
const loadDrugData = async () => {
  loading.value = true
  error.value = false
  const drugId = route.params.id as string

  if (!drugId) {
    ElMessage.error('药品ID不能为空')
    error.value = true
    loading.value = false
    return
  }

  try {
    const data = await getDrugDetail(drugId)
    // 兼容不同的返回格式
    if (data) {
      drug.value = data as Drug
    } else {
      throw new Error('数据为空')
    }
  } catch (err) {
    console.error('获取药品详情失败:', err)
    error.value = true
    ElMessage.error('获取药品信息失败')
  } finally {
    loading.value = false
  }
}

// 增加数量
const increaseQuantity = () => {
  if (quantity.value < 99) {
    quantity.value++
  }
}

// 减少数量
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

// 加入购物车
const addToCart = () => {
  if (!drug.value) return

  try {
    cartStore.addItem({
      drugId: drug.value.id,
      name: drug.value.name || '未知药品',
      specification: drug.value.specification || '',
      manufacturer: drug.value.manufacturer || '',
      price: drug.value.price || 0,
      quantity: quantity.value,
      image: drug.value.image || '',
      disease: drug.value.disease || '',
      usage: drug.value.usage || '',
      isRx: drug.value.isRx || false
    })

    ElMessage.success('已加入购物车')
  } catch (err) {
    console.error('加入购物车失败:', err)
    ElMessage.error('加入购物车失败')
  }
}

// 立即购买
const buyNow = () => {
  if (!drug.value) return

  try {
    // 先加入购物车
    cartStore.addItem({
      drugId: drug.value.id,
      name: drug.value.name || '未知药品',
      specification: drug.value.specification || '',
      manufacturer: drug.value.manufacturer || '',
      price: drug.value.price || 0,
      quantity: quantity.value,
      image: drug.value.image || '',
      disease: drug.value.disease || '',
      usage: drug.value.usage || '',
      isRx: drug.value.isRx || false
    })

    // 跳转到订单确认页
    router.push('/order/confirm')
  } catch (err) {
    console.error('购买失败:', err)
    ElMessage.error('操作失败')
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 返回首页
const goHome = () => {
  router.push('/')
}

// 查看购物车
const goToCart = () => {
  router.push('/cart')
}

// 重试加载
const retryLoad = () => {
  loadDrugData()
}

onMounted(() => {
  loadDrugData()
})
</script>

<template>
  <div class="drug-detail-page">
    <!-- 顶部导航栏 -->
    <div class="detail-header" :class="{ scrolled: false }">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="header-title">药品详情</span>
      <div class="cart-btn" @click="goToCart">
        <el-icon><ShoppingCart /></el-icon>
        <span v-if="cartStore.totalCount > 0" class="cart-badge">{{ cartStore.totalCount > 99 ? '99+' : cartStore.totalCount }}</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton animated>
        <template #template>
          <!-- 图片骨架 -->
          <div style="width: 100%; aspect-ratio: 1; background: #f5f5f5; margin-bottom: 12px;" />
          <!-- 价格骨架 -->
          <div style="padding: 16px; background: #fff;">
            <el-skeleton-item variant="text" style="width: 40%; height: 32px; margin-bottom: 8px;" />
            <el-skeleton-item variant="text" style="width: 30%;" />
          </div>
          <!-- 信息骨架 -->
          <div style="padding: 16px; background: #fff; margin-top: 12px;">
            <el-skeleton-item variant="h1" style="width: 70%; margin-bottom: 12px;" />
            <el-skeleton-item variant="text" style="width: 50%; margin-bottom: 8px;" />
            <el-skeleton-item variant="text" style="width: 60%;" />
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error || !drug" class="error-container">
      <el-empty description="加载失败" :image-size="120">
        <el-button type="primary" @click="retryLoad">重新加载</el-button>
      </el-empty>
    </div>

    <!-- 药品详情内容 -->
    <template v-else>
      <!-- 图片轮播 -->
      <div class="image-section">
        <div class="image-swiper" :style="{ backgroundColor: drug.imageColor || '#3B8CFF' }">
          <img
            v-if="drug.imageUrl"
            :src="drug.imageUrl"
            class="drug-image"
            :alt="drug.name"
          />
          <div v-else class="image-placeholder">
            <span class="image-text">{{ drug.imageText || drug.name?.slice(0, 2) || '药' }}</span>
          </div>
          <span v-if="drug.isRx" class="rx-tag">处方药</span>
        </div>
        <div v-if="images.length > 1" class="swiper-dots">
          <span
            v-for="(_, index) in images"
            :key="index"
            class="dot"
            :class="{ active: currentSwiperIndex === index }"
            @click="currentSwiperIndex = index"
          />
        </div>
      </div>

      <!-- 价格信息 -->
      <div class="price-section">
        <div class="price-row">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ (drug.price || 0).toFixed(2) }}</span>
          <span v-if="drug.originalPrice && drug.originalPrice > drug.price" class="original-price">¥{{ drug.originalPrice.toFixed(2) }}</span>
        </div>
        <div class="sales-info">
          <span class="sales">已售 {{ drug.sales > 10000 ? (drug.sales / 10000).toFixed(1) + '万' : (drug.sales || 0) }}</span>
          <span v-if="drug.stock > 0" class="stock">库存充足</span>
          <span v-else class="stock out-of-stock">暂无库存</span>
        </div>
      </div>

      <!-- 药品基本信息 -->
      <div class="info-section">
        <h1 class="drug-name">{{ drug.name || '未知药品' }}</h1>
        <p v-if="drug.specification" class="drug-spec">规格：{{ drug.specification }}</p>
        <p v-if="drug.manufacturer" class="drug-manufacturer">生产厂家：{{ drug.manufacturer }}</p>
        <div v-if="drug.tags?.length" class="drug-tags">
          <span v-for="tag in drug.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

      <!-- 规格选择 -->
      <div v-if="specs.length > 0" class="spec-section">
        <div class="section-title">规格选择</div>
        <div class="spec-list">
          <div
            v-for="(spec, index) in specs"
            :key="index"
            class="spec-item"
            :class="{ active: selectedSpec === index, disabled: spec.stock <= 0 }"
            @click="spec.stock > 0 && (selectedSpec = index)"
          >
            <span class="spec-label">{{ spec.label }}</span>
            <span class="spec-price">¥{{ spec.price.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 数量选择 -->
      <div class="quantity-section">
        <div class="section-title">购买数量</div>
        <div class="quantity-selector">
          <button class="btn-minus" :disabled="quantity <= 1" @click="decreaseQuantity">
            <el-icon><Minus /></el-icon>
          </button>
          <span class="quantity-value">{{ quantity }}</span>
          <button class="btn-plus" :disabled="quantity >= 99" @click="increaseQuantity">
            <el-icon><Plus /></el-icon>
          </button>
        </div>
      </div>

      <!-- 药品说明 -->
      <div class="instruction-section">
        <div class="section-title">药品说明书</div>
        <div class="instruction-content" :class="{ expanded: instructionExpanded }">
          <div class="instruction-item">
            <span class="label">【适应症】</span>
            <span class="value">{{ drug.disease || '详见说明书' }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【用法用量】</span>
            <span class="value">{{ drug.usage || '详见说明书' }}</span>
          </div>
          <div v-if="drug.approvalNumber" class="instruction-item">
            <span class="label">【批准文号】</span>
            <span class="value">{{ drug.approvalNumber }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【生产厂家】</span>
            <span class="value">{{ drug.manufacturer || '详见说明书' }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【贮藏】</span>
            <span class="value">{{ drug.storage || '密封，置阴凉干燥处' }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【禁忌】</span>
            <span class="value">{{ drug.contraindications || '详见说明书' }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【注意事项】</span>
            <span class="value">{{ drug.precautions || '请遵医嘱使用' }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【不良反应】</span>
            <span class="value">{{ drug.adverseReactions || '详见说明书' }}</span>
          </div>
        </div>
        <div class="expand-btn" @click="instructionExpanded = !instructionExpanded">
          <span>{{ instructionExpanded ? '收起' : '展开全部' }}</span>
          <el-icon>
            <ArrowUp v-if="instructionExpanded" />
            <ArrowDown v-else />
          </el-icon>
        </div>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder" />
    </template>

    <!-- 底部操作栏（仅在非loading和非error时显示）-->
    <div v-if="drug && !loading && !error" class="bottom-actions">
      <div class="action-btns">
        <div class="icon-btn" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </div>
        <div class="icon-btn" @click="goToCart">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
          <span v-if="cartStore.totalCount > 0" class="badge">{{ cartStore.totalCount > 99 ? '99+' : cartStore.totalCount }}</span>
        </div>
      </div>
      <div class="buy-btns">
        <button class="btn-cart" @click="addToCart">加入购物车</button>
        <button class="btn-buy" @click="buyNow" :disabled="drug.stock <= 0">
          {{ drug.stock <= 0 ? '暂无库存' : '立即购买' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.drug-detail-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 80px;
}

// 顶部导航
.detail-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: rgba(59, 140, 255, 0.95);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;

  &.scrolled {
    background: $bg-white;
    box-shadow: $shadow-sm;

    .back-btn,
    .cart-btn {
      color: $text-primary;
      background: rgba(0, 0, 0, 0.05);
    }

    .header-title {
      color: $text-primary;
    }

    .cart-badge {
      background: $error;
    }
  }

  .back-btn,
  .cart-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    color: $text-white;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.05);
    }

    &:active {
      transform: scale(0.95);
    }
  }

  .header-title {
    font-size: $font-lg;
    font-weight: 600;
    color: $text-white;
  }

  .cart-badge {
    position: absolute;
    top: -4px;
    right: -4px;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    background: $error;
    color: $text-white;
    font-size: 10px;
    font-weight: bold;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

// 加载和错误状态
.loading-container,
.error-container {
  padding: $spacing-lg;
  padding-top: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

// 图片轮播
.image-section {
  position: relative;
  background: $bg-white;
  margin-top: 60px;

  .image-swiper {
    position: relative;
    width: 100%;
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .drug-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .image-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      .image-text {
        color: #fff;
        font-size: 48px;
        font-weight: bold;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      }
    }

    .rx-tag {
      position: absolute;
      top: 60px;
      left: $spacing-md;
      background: $error;
      color: $text-white;
      font-size: $font-sm;
      padding: 4px 8px;
      border-radius: $radius-sm;
      box-shadow: 0 2px 6px rgba($error, 0.3);
    }
  }

  .swiper-dots {
    position: absolute;
    bottom: $spacing-md;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: $spacing-xs;

    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.5);
      cursor: pointer;
      transition: all 0.3s ease;

      &.active {
        width: 16px;
        border-radius: 4px;
        background: #fff;
      }
    }
  }
}

// 价格区域
.price-section {
  padding: $spacing-lg;
  background: $bg-white;
  border-bottom: 1px solid $border-light;

  .price-row {
    display: flex;
    align-items: baseline;
    gap: $spacing-sm;
    margin-bottom: $spacing-sm;

    .price-symbol {
      font-size: $font-md;
      color: $error;
      font-weight: 600;
    }

    .price-value {
      font-size: 32px;
      font-weight: bold;
      color: $error;
    }

    .original-price {
      font-size: $font-md;
      color: $text-tertiary;
      text-decoration: line-through;
      margin-left: $spacing-sm;
    }
  }

  .sales-info {
    display: flex;
    gap: $spacing-md;
    font-size: $font-sm;
    color: $text-secondary;

    .stock {
      color: $success;

      &.out-of-stock {
        color: $error;
      }
    }
  }
}

// 信息区域
.info-section {
  padding: $spacing-lg;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .drug-name {
    font-size: $font-xl;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 $spacing-sm 0;
    line-height: 1.4;
  }

  .drug-spec,
  .drug-manufacturer {
    font-size: $font-sm;
    color: $text-secondary;
    margin: 0 0 $spacing-xs 0;
  }

  .drug-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-xs;
    margin-top: $spacing-sm;

    .tag {
      padding: 4px 8px;
      background: rgba($primary, 0.08);
      color: $primary;
      font-size: $font-xs;
      border-radius: $radius-sm;
    }
  }
}

// 规格选择、数量选择
.spec-section,
.quantity-section {
  padding: $spacing-lg;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .section-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .spec-list {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;

    .spec-item {
      padding: $spacing-sm $spacing-md;
      border: 1px solid $border-light;
      border-radius: $radius-md;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover:not(.disabled) {
        border-color: $primary;
      }

      &.active {
        border-color: $primary;
        background: rgba($primary, 0.06);

        .spec-label,
        .spec-price {
          color: $primary;
        }
      }

      &.disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }

      .spec-label {
        font-size: $font-sm;
        color: $text-primary;
        margin-right: $spacing-sm;
      }

      .spec-price {
        font-size: $font-sm;
        color: $error;
        font-weight: 500;
      }
    }
  }

  .quantity-selector {
    display: flex;
    align-items: center;
    gap: $spacing-md;

    .btn-minus,
    .btn-plus {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid $border-color;
      background: $bg-white;
      border-radius: $radius-sm;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover:not(:disabled) {
        border-color: $primary;
        color: $primary;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }

    .quantity-value {
      min-width: 48px;
      text-align: center;
      font-size: $font-lg;
      font-weight: 600;
      color: $text-primary;
    }
  }
}

// 说明书
.instruction-section {
  padding: $spacing-lg;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .section-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .instruction-content {
    max-height: 200px;
    overflow: hidden;
    transition: max-height 0.3s ease;

    &.expanded {
      max-height: none;
    }

    .instruction-item {
      margin-bottom: $spacing-md;
      font-size: $font-sm;
      line-height: 1.7;

      .label {
        color: $text-secondary;
        font-weight: 500;
      }

      .value {
        color: $text-primary;
      }
    }
  }

  .expand-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    padding: $spacing-md 0;
    margin-top: $spacing-sm;
    color: $primary;
    font-size: $font-sm;
    cursor: pointer;
    border-top: 1px solid $border-light;
    transition: opacity 0.2s;

    &:hover {
      opacity: 0.8;
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 80px;
}

// 底部操作栏
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: $spacing-sm $spacing-md;
  padding-bottom: calc($safe-area-bottom + $spacing-sm);
  background: $bg-white;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;

  .action-btns {
    display: flex;
    gap: $spacing-xs;

    .icon-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      padding: $spacing-xs $spacing-sm;
      color: $text-secondary;
      cursor: pointer;
      position: relative;
      transition: all 0.2s;

      &:hover {
        color: $primary;
      }

      .el-icon {
        font-size: 22px;
      }

      span {
        font-size: 10px;
      }

      .badge {
        position: absolute;
        top: -2px;
        right: 0;
        min-width: 16px;
        height: 16px;
        padding: 0 4px;
        background: $error;
        color: $text-white;
        font-size: 10px;
        font-weight: bold;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .buy-btns {
    flex: 1;
    display: flex;
    gap: $spacing-sm;
    margin-left: $spacing-md;

    .btn-cart,
    .btn-buy {
      flex: 1;
      height: 44px;
      border: none;
      border-radius: $radius-lg;
      font-size: $font-md;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s ease;

      &:active {
        transform: scale(0.98);
      }
    }

    .btn-cart {
      background: linear-gradient(135deg, #FFF4E6 0%, #FFE7BA 100%);
      color: $warning;
      border: 1px solid rgba($warning, 0.3);

      &:hover {
        background: linear-gradient(135deg, #FFE7BA 0%, #FFD9A0 100%);
      }
    }

    .btn-buy {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: $text-white;
      box-shadow: 0 4px 12px rgba($primary, 0.35);

      &:hover:not(:disabled) {
        box-shadow: 0 6px 16px rgba($primary, 0.45);
        transform: translateY(-1px);
      }

      &:disabled {
        background: #ccc;
        box-shadow: none;
        cursor: not-allowed;
      }
    }
  }
}
</style>
