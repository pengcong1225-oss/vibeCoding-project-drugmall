<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import type { CartItem } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 页面加载时获取购物车数据
onMounted(() => {
  cartStore.fetchCartList()
})

// 选中的商品ID
const selectedIds = ref<string[]>([])

// 是否全选
const isAllSelected = computed(() => {
  if (cartStore.items.length === 0) return false
  return selectedIds.value.length === cartStore.items.length
})

// 选中商品列表
const selectedItems = computed(() => {
  return cartStore.items.filter(item => selectedIds.value.includes(item.id))
})

// 选中商品总价
const selectedTotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

// 选中商品数量
const selectedCount = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 全选/取消全选
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedIds.value = []
  } else {
    selectedIds.value = cartStore.items.map(item => item.id)
  }
}

// 选中/取消选中单个商品
const toggleSelectItem = (id: string) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

// 更新数量
const updateQuantity = (item: CartItem, delta: number) => {
  const newQuantity = item.quantity + delta
  if (newQuantity < 1) {
    ElMessage.warning('数量不能少于1')
    return
  }
  if (newQuantity > 99) {
    ElMessage.warning('数量不能多于99')
    return
  }
  cartStore.updateQuantity(item.id, newQuantity)
}

// 删除商品
const removeItem = (item: CartItem) => {
  ElMessageBox.confirm(
    `确定要删除 "${item.name}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    cartStore.removeItem(item.id)
    // 从选中列表中移除
    const index = selectedIds.value.indexOf(item.id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    }
    ElMessage.success('已删除')
  }).catch(() => {
    // 取消删除
  })
}

// 清空购物车
const clearCart = () => {
  if (cartStore.items.length === 0) {
    ElMessage.info('购物车已经是空的')
    return
  }
  
  ElMessageBox.confirm(
    '确定要清空购物车吗？',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    cartStore.clearCart()
    selectedIds.value = []
    ElMessage.success('购物车已清空')
  }).catch(() => {
    // 取消
  })
}

// 去结算
const goToCheckout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm(
      '请先登录后再结算',
      '需要登录',
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      router.push('/login')
    }).catch(() => {
      // 取消
    })
    return
  }
  
  router.push('/order/confirm')
}

// 继续购物
const continueShopping = () => {
  router.push('/home')
}

// 去药品详情
const goToDrugDetail = (item: CartItem) => {
  router.push(`/drug/${item.drugId}`)
}
</script>

<template>
  <div class="cart-page">
    <!-- 头部 -->
    <div class="cart-header">
      <h1 class="title">购物车({{ cartStore.totalCount }})</h1>
      <div v-if="cartStore.items.length > 0" class="clear-btn" @click="clearCart">
        <el-icon><Delete /></el-icon>
        <span>清空</span>
      </div>
    </div>

    <!-- 购物车内容 -->
    <div class="cart-content">
      <!-- 空购物车 -->
      <div v-if="cartStore.items.length === 0" class="empty-cart">
        <el-empty description="购物车是空的">
          <template #image>
            <div class="empty-icon">
              <el-icon :size="80" color="#ccc"><ShoppingCart /></el-icon>
            </div>
          </template>
          <el-button type="primary" @click="continueShopping">去逛逛</el-button>
        </el-empty>
      </div>

      <!-- 商品列表 -->
      <div v-else class="cart-list">
        <!-- 全选栏 -->
        <div class="select-all-bar">
          <div class="checkbox-wrapper" @click="toggleSelectAll">
            <el-icon v-if="isAllSelected" class="checked"><CircleCheckFilled /></el-icon>
            <el-icon v-else class="unchecked"><CircleCheck /></el-icon>
            <span>全选</span>
          </div>
          <span class="total-count">共 {{ cartStore.totalCount }} 件商品</span>
        </div>

        <!-- 商品项 -->
        <div
          v-for="item in cartStore.items"
          :key="item.id"
          class="cart-item"
          :class="{ selected: selectedIds.includes(item.id) }"
        >
          <!-- 选择框 -->
          <div class="item-checkbox" @click="toggleSelectItem(item.id)">
            <el-icon v-if="selectedIds.includes(item.id)" class="checked"><CircleCheckFilled /></el-icon>
            <el-icon v-else class="unchecked"><CircleCheck /></el-icon>
          </div>

          <!-- 商品图片 -->
          <div class="item-image" @click="goToDrugDetail(item)">
            <img :src="item.image" :alt="item.name" />
            <span v-if="item.isRx" class="rx-tag">Rx</span>
          </div>

          <!-- 商品信息 -->
          <div class="item-info">
            <h3 class="item-name" @click="goToDrugDetail(item)">{{ item.name }}</h3>
            <p class="item-spec">{{ item.specification }}</p>
            <p class="item-manufacturer">{{ item.manufacturer }}</p>

            <!-- 价格和数量 -->
            <div class="item-bottom">
              <span class="item-price">¥{{ item.price.toFixed(2) }}</span>
              <div class="quantity-control">
                <button
                  class="btn-minus"
                  :disabled="item.quantity <= 1"
                  @click="updateQuantity(item, -1)"
                >
                  <el-icon><Minus /></el-icon>
                </button>
                <span class="quantity-value">{{ item.quantity }}</span>
                <button
                  class="btn-plus"
                  :disabled="item.quantity >= 99"
                  @click="updateQuantity(item, 1)"
                >
                  <el-icon><Plus /></el-icon>
                </button>
              </div>
            </div>
          </div>

          <!-- 删除按钮 -->
          <div class="item-delete" @click="removeItem(item)">
            <el-icon><Delete /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div v-if="cartStore.items.length > 0" class="cart-footer">
      <div class="select-all" @click="toggleSelectAll">
        <el-icon v-if="isAllSelected" class="checked"><CircleCheckFilled /></el-icon>
        <el-icon v-else class="unchecked"><CircleCheck /></el-icon>
        <span>全选</span>
      </div>
      <div class="footer-info">
        <div class="total-row">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ selectedTotal.toFixed(2) }}</span>
        </div>
        <div class="total-desc">
          共 {{ selectedCount }} 件商品
        </div>
      </div>
      <button
        class="checkout-btn"
        :disabled="selectedItems.length === 0"
        @click="goToCheckout"
      >
        去结算
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.cart-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(80px + $safe-area-bottom);
}

// 头部
.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: $bg-white;
  border-bottom: 1px solid $border-light;

  .title {
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
  }

  .clear-btn {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-xs $spacing-sm;
    color: $text-tertiary;
    font-size: $font-sm;
    cursor: pointer;
    border-radius: $radius-sm;
    transition: all 0.2s ease;

    &:hover {
      color: $error;
      background: rgba($error, 0.1);
    }
  }
}

// 购物车内容
.cart-content {
  padding: $spacing-md;
}

// 空购物车
.empty-cart {
  padding: $spacing-xxl 0;

  .empty-icon {
    display: flex;
    justify-content: center;
    margin-bottom: $spacing-md;
  }

  :deep(.el-empty__description) {
    color: $text-tertiary;
  }
}

// 购物车列表
.cart-list {
  .select-all-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md;
    background: $bg-white;
    border-radius: $radius-lg $radius-lg 0 0;
    margin-bottom: 1px;

    .checkbox-wrapper {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      cursor: pointer;

      .checked {
        font-size: 20px;
        color: $primary;
      }

      .unchecked {
        font-size: 20px;
        color: $text-tertiary;
      }

      span {
        font-size: $font-sm;
        color: $text-primary;
      }
    }

    .total-count {
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
}

// 购物车项
.cart-item {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-md;
  background: $bg-white;
  margin-bottom: 1px;
  transition: background 0.2s ease;

  &:last-child {
    margin-bottom: 0;
    border-radius: 0 0 $radius-lg $radius-lg;
  }

  &.selected {
    background: rgba($primary, 0.02);
  }

  .item-checkbox {
    cursor: pointer;

    .checked {
      font-size: 20px;
      color: $primary;
    }

    .unchecked {
      font-size: 20px;
      color: $text-tertiary;
    }
  }

  .item-image {
    position: relative;
    width: 80px;
    height: 80px;
    flex-shrink: 0;
    border-radius: $radius-md;
    overflow: hidden;
    background: $bg-gray;
    cursor: pointer;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .rx-tag {
      position: absolute;
      top: 0;
      left: 0;
      background: $error;
      color: $text-white;
      font-size: 10px;
      padding: 2px 4px;
      border-bottom-right-radius: $radius-sm;
    }
  }

  .item-info {
    flex: 1;
    min-width: 0;

    .item-name {
      font-size: $font-md;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: $spacing-xs;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;

      &:hover {
        color: $primary;
      }
    }

    .item-spec,
    .item-manufacturer {
      font-size: $font-xs;
      color: $text-tertiary;
      margin-bottom: 2px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: $spacing-xs;

      .item-price {
        font-size: $font-md;
        font-weight: 600;
        color: $error;
      }

      .quantity-control {
        display: flex;
        align-items: center;
        gap: $spacing-xs;

        .btn-minus,
        .btn-plus {
          width: 24px;
          height: 24px;
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

          .el-icon {
            font-size: 12px;
          }
        }

        .quantity-value {
          min-width: 30px;
          text-align: center;
          font-size: $font-sm;
          font-weight: 500;
          color: $text-primary;
        }
      }
    }
  }

  .item-delete {
    padding: $spacing-sm;
    color: $text-tertiary;
    cursor: pointer;
    transition: color 0.2s ease;

    &:hover {
      color: $error;
    }

    .el-icon {
      font-size: 18px;
    }
  }
}

// 底部结算栏
.cart-footer {
  position: fixed;
  bottom: calc($tabbar-height + $safe-area-bottom);
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: $spacing-sm $spacing-md;
  background: $bg-white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 99;

  .select-all {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    cursor: pointer;
    margin-right: $spacing-md;

    .checked {
      font-size: 20px;
      color: $primary;
    }

    .unchecked {
      font-size: 20px;
      color: $text-tertiary;
    }

    span {
      font-size: $font-sm;
      color: $text-primary;
    }
  }

  .footer-info {
    flex: 1;
    text-align: right;
    margin-right: $spacing-md;

    .total-row {
      display: flex;
      align-items: baseline;
      justify-content: flex-end;
      gap: $spacing-xs;

      .total-label {
        font-size: $font-sm;
        color: $text-primary;
      }

      .total-price {
        font-size: 20px;
        font-weight: bold;
        color: $error;
      }
    }

    .total-desc {
      font-size: $font-xs;
      color: $text-tertiary;
      margin-top: 2px;
    }
  }

  .checkout-btn {
    min-width: 100px;
    height: 40px;
    padding: 0 $spacing-lg;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: $text-white;
    font-size: $font-md;
    font-weight: 500;
    border: none;
    border-radius: $radius-xl;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover:not(:disabled) {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    &:disabled {
      background: $text-tertiary;
      cursor: not-allowed;
      opacity: 0.6;
    }
  }
}
</style>
