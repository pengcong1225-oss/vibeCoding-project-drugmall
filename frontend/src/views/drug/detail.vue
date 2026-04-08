<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { mockDrugs } from '@/api/mock'
import type { Drug } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 药品数据
const drug = ref<Drug | null>(null)
const loading = ref(true)

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
  if (drug.value?.images?.length) {
    return drug.value.images
  }
  return drug.value?.image ? [drug.value.image] : []
})

// 规格列表
const specs = computed(() => {
  return [
    { label: drug.value?.specification || '默认规格', price: drug.value?.price || 0 }
  ]
})

// 加载药品数据
const loadDrugData = async () => {
  loading.value = true
  const drugId = route.params.id as string
  
  // 模拟API调用
  setTimeout(() => {
    const found = mockDrugs.find(d => d.id === drugId.split('-')[0])
    if (found) {
      drug.value = {
        ...found,
        id: drugId,
        images: [found.image, found.image, found.image],
        description: `${found.name}是一款常用的${found.category}药品，由${found.manufacturer}生产。`,
        detail: `【药品名称】${found.name}\n【成分】详见说明书\n【适应症】${found.disease}\n【用法用量】${found.usage}\n【不良反应】偶见胃肠道不适\n【禁忌】对本品过敏者禁用\n【注意事项】请遵医嘱使用`,
        approvalNumber: '国药准字Z' + Math.floor(Math.random() * 100000000),
        storage: '密封，置阴凉干燥处',
        contraindications: '对本品过敏者禁用',
        precautions: '请遵医嘱使用，孕妇慎用',
        adverseReactions: '偶见胃肠道不适、皮疹'
      }
    }
    loading.value = false
  }, 500)
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
  
  cartStore.addItem({
    drugId: drug.value.id,
    name: drug.value.name,
    specification: drug.value.specification,
    manufacturer: drug.value.manufacturer,
    price: drug.value.price,
    quantity: quantity.value,
    image: drug.value.image,
    disease: drug.value.disease || '',
    usage: drug.value.usage || '',
    isRx: drug.value.isRx
  })
  
  ElMessage.success('已加入购物车')
}

// 立即购买
const buyNow = () => {
  if (!drug.value) return
  
  // 先加入购物车
  cartStore.addItem({
    drugId: drug.value.id,
    name: drug.value.name,
    specification: drug.value.specification,
    manufacturer: drug.value.manufacturer,
    price: drug.value.price,
    quantity: quantity.value,
    image: drug.value.image,
    disease: drug.value.disease || '',
    usage: drug.value.usage || '',
    isRx: drug.value.isRx
  })
  
  // 跳转到订单确认页
  router.push('/order/confirm')
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 查看购物车
const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  loadDrugData()
})
</script>

<template>
  <div class="drug-detail-page">
    <!-- 顶部导航栏 -->
    <div class="detail-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="header-title">药品详情</span>
      <div class="cart-btn" @click="goToCart">
        <el-icon><ShoppingCart /></el-icon>
        <span v-if="cartStore.totalCount > 0" class="cart-badge">{{ cartStore.totalCount }}</span>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <template v-else-if="drug">
      <!-- 图片轮播 -->
      <div class="image-section">
        <div class="image-swiper">
          <img :src="images[currentSwiperIndex]" :alt="drug.name" />
          <div v-if="drug.isRx" class="rx-tag">处方药</div>
        </div>
        <div class="swiper-dots">
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
          <span class="price-value">{{ drug.price.toFixed(2) }}</span>
          <span v-if="drug.originalPrice" class="original-price">¥{{ drug.originalPrice.toFixed(2) }}</span>
        </div>
        <div class="sales-info">
          <span class="sales">已售 {{ drug.sales > 10000 ? (drug.sales / 10000).toFixed(1) + '万' : drug.sales }}</span>
          <span v-if="drug.stock > 0" class="stock">库存充足</span>
        </div>
      </div>

      <!-- 药品基本信息 -->
      <div class="info-section">
        <h1 class="drug-name">{{ drug.name }}</h1>
        <p class="drug-spec">规格：{{ drug.specification }}</p>
        <p class="drug-manufacturer">生产厂家：{{ drug.manufacturer }}</p>
        <div v-if="drug.tags?.length" class="drug-tags">
          <span v-for="tag in drug.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

      <!-- 规格选择 -->
      <div class="spec-section">
        <div class="section-title">规格选择</div>
        <div class="spec-list">
          <div
            v-for="(spec, index) in specs"
            :key="index"
            class="spec-item"
            :class="{ active: selectedSpec === index }"
            @click="selectedSpec = index"
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
            <span class="value">{{ drug.disease }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【用法用量】</span>
            <span class="value">{{ drug.usage }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【批准文号】</span>
            <span class="value">{{ drug.approvalNumber }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【生产厂家】</span>
            <span class="value">{{ drug.manufacturer }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【贮藏】</span>
            <span class="value">{{ drug.storage }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【禁忌】</span>
            <span class="value">{{ drug.contraindications }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【注意事项】</span>
            <span class="value">{{ drug.precautions }}</span>
          </div>
          <div class="instruction-item">
            <span class="label">【不良反应】</span>
            <span class="value">{{ drug.adverseReactions }}</span>
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

    <!-- 底部操作栏 -->
    <div v-if="drug" class="bottom-actions">
      <div class="action-btns">
        <div class="icon-btn" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </div>
        <div class="icon-btn" @click="goToCart">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
          <span v-if="cartStore.totalCount > 0" class="badge">{{ cartStore.totalCount }}</span>
        </div>
      </div>
      <div class="buy-btns">
        <button class="btn-cart" @click="addToCart">加入购物车</button>
        <button class="btn-buy" @click="buyNow">立即购买</button>
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
  background: transparent;
  transition: background 0.3s ease;

  &.scrolled {
    background: $bg-white;
    box-shadow: $shadow-sm;
  }

  .back-btn,
  .cart-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 50%;
    color: $text-white;
    cursor: pointer;
    position: relative;

    &:hover {
      background: rgba(0, 0, 0, 0.5);
    }
  }

  .header-title {
    font-size: $font-lg;
    font-weight: 500;
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

// 加载状态
.loading-container {
  padding: $spacing-lg;
  padding-top: 100px;
}

// 图片轮播
.image-section {
  position: relative;
  background: $bg-white;

  .image-swiper {
    position: relative;
    width: 100%;
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $bg-gray;

    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
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
      background: rgba(0, 0, 0, 0.3);
      cursor: pointer;
      transition: all 0.3s ease;

      &.active {
        width: 16px;
        border-radius: 4px;
        background: $primary;
      }
    }
  }
}

// 价格区域
.price-section {
  padding: $spacing-md;
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
      font-weight: 500;
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
    }
  }

  .sales-info {
    display: flex;
    gap: $spacing-md;
    font-size: $font-sm;
    color: $text-secondary;

    .stock {
      color: $success;
    }
  }
}

// 信息区域
.info-section {
  padding: $spacing-md;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .drug-name {
    font-size: $font-xl;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-sm;
    line-height: 1.4;
  }

  .drug-spec,
  .drug-manufacturer {
    font-size: $font-sm;
    color: $text-secondary;
    margin-bottom: $spacing-xs;
  }

  .drug-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-xs;
    margin-top: $spacing-sm;

    .tag {
      padding: 4px 8px;
      background: rgba($primary, 0.1);
      color: $primary;
      font-size: $font-xs;
      border-radius: $radius-sm;
    }
  }
}

// 规格选择
.spec-section,
.quantity-section {
  padding: $spacing-md;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
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

      &:hover {
        border-color: $primary;
      }

      &.active {
        border-color: $primary;
        background: rgba($primary, 0.1);
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
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid $border-light;
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
      min-width: 40px;
      text-align: center;
      font-size: $font-md;
      font-weight: 500;
      color: $text-primary;
    }
  }
}

// 说明书
.instruction-section {
  padding: $spacing-md;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
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
      margin-bottom: $spacing-sm;
      font-size: $font-sm;
      line-height: 1.6;

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
    padding: $spacing-sm;
    margin-top: $spacing-sm;
    color: $primary;
    font-size: $font-sm;
    cursor: pointer;
    border-top: 1px solid $border-light;
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
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .action-btns {
    display: flex;
    gap: $spacing-md;

    .icon-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      padding: $spacing-xs $spacing-sm;
      color: $text-secondary;
      cursor: pointer;
      position: relative;

      &:hover {
        color: $primary;
      }

      .el-icon {
        font-size: 20px;
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
      height: 40px;
      border: none;
      border-radius: $radius-lg;
      font-size: $font-md;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .btn-cart {
      background: rgba($primary, 0.1);
      color: $primary;

      &:hover {
        background: rgba($primary, 0.2);
      }
    }

    .btn-buy {
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: $text-white;

      &:hover {
        opacity: 0.9;
      }
    }
  }
}
</style>
