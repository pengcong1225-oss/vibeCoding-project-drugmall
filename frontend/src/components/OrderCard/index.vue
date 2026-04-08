<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatPrice, formatDateTime } from '@/utils'
import type { Order, OrderStatus } from '@/types'

const props = defineProps<{
  order: Order
}>()

const emit = defineEmits<{
  (e: 'pay', order: Order): void
  (e: 'cancel', order: Order): void
  (e: 'confirm', order: Order): void
  (e: 'delete', order: Order): void
  (e: 'rebuy', order: Order): void
  (e: 'review', order: Order): void
  (e: 'refund', order: Order): void
}>()

const router = useRouter()

// 状态配置
const statusConfig: Record<OrderStatus, { text: string; color: string; type: string }> = {
  pending: { text: '待支付', color: '#ff9500', type: 'warning' },
  paid: { text: '已支付', color: '#00b578', type: 'success' },
  confirmed: { text: '已确认', color: '#1890ff', type: 'info' },
  shipped: { text: '配送中', color: '#00b578', type: 'success' },
  delivered: { text: '已送达', color: '#00b578', type: 'success' },
  completed: { text: '已完成', color: '#666', type: 'info' },
  cancelled: { text: '已取消', color: '#999', type: 'info' },
  refunding: { text: '退款中', color: '#ff9500', type: 'warning' },
  refunded: { text: '已退款', color: '#666', type: 'info' }
}

const currentStatus = computed(() => statusConfig[props.order.status])

// 操作按钮配置
const primaryAction = computed(() => {
  switch (props.order.status) {
    case 'pending':
      return { text: '立即支付', action: () => emit('pay', props.order) }
    case 'shipped':
      return { text: '确认收货', action: () => emit('confirm', props.order) }
    case 'completed':
      if (!props.order.items.every(item => item.reviewStatus === 'completed')) {
        return { text: '评价', action: () => emit('review', props.order) }
      }
      return { text: '再次购买', action: () => emit('rebuy', props.order) }
    case 'cancelled':
      return { text: '再次购买', action: () => emit('rebuy', props.order) }
    default:
      return null
  }
})

const secondaryAction = computed(() => {
  switch (props.order.status) {
    case 'pending':
      return { text: '取消订单', action: () => emit('cancel', props.order) }
    case 'completed':
    case 'cancelled':
      return { text: '删除订单', action: () => emit('delete', props.order) }
    case 'paid':
    case 'confirmed':
      return { text: '申请退款', action: () => emit('refund', props.order) }
    default:
      return null
  }
})

const goToDetail = () => {
  router.push(`/order/${props.order.id}`)
}

// 获取商品图片
const getDrugImage = (image: string | undefined) => {
  return image || 'https://via.placeholder.com/80x80/00b578/ffffff?text=Drug'
}

// 显示更多商品数量
const moreCount = computed(() => {
  const count = props.order.items.length - 3
  return count > 0 ? count : 0
})
</script>

<template>
  <div class="order-card">
    <!-- 订单头部 -->
    <div class="order-header" @click="goToDetail">
      <div class="order-no">
        <span class="label">订单号:</span>
        <span class="value">{{ order.orderNo }}</span>
      </div>
      <div class="order-status" :style="{ color: currentStatus.color }">
        {{ currentStatus.text }}
      </div>
    </div>
    
    <!-- 订单商品 -->
    <div class="order-content" @click="goToDetail">
      <!-- 多个商品时显示缩略图列表 -->
      <div class="drug-list">
        <div 
          v-for="(item, index) in order.items.slice(0, 3)" 
          :key="item.id"
          class="drug-thumb"
          :class="{ 'is-rx': item.isRx }"
        >
          <img :src="getDrugImage(item.image)" :alt="item.name" />
          <span v-if="item.isRx" class="rx-mark">Rx</span>
        </div>
        <div v-if="moreCount > 0" class="more-count">
          +{{ moreCount }}
        </div>
      </div>
      
      <!-- 订单金额信息 -->
      <div class="order-amount">
        <div class="amount-row">
          <span class="label">共{{ order.totalQuantity }}件商品</span>
          <span class="total-price">
            <span class="symbol">¥</span>
            <span class="value">{{ formatPrice(order.totalAmount) }}</span>
          </span>
        </div>
        <div v-if="order.deliveryFee > 0" class="amount-row delivery">
          <span class="label">含运费</span>
          <span class="value">¥{{ formatPrice(order.deliveryFee) }}</span>
        </div>
      </div>
    </div>
    
    <!-- 订单时间（仅待支付显示） -->
    <div v-if="order.status === 'pending' && order.expireTime" class="order-time">
      <el-icon><Clock /></el-icon>
      <span>支付截止时间: {{ formatDateTime(order.expireTime) }}</span>
    </div>
    
    <!-- 订单操作区 -->
    <div class="order-actions">
      <template v-if="primaryAction">
        <el-button
          :type="order.status === 'pending' ? 'primary' : 'default'"
          size="default"
          @click="primaryAction.action"
        >
          {{ primaryAction.text }}
        </el-button>
      </template>
      
      <template v-if="secondaryAction">
        <el-button
          type="default"
          size="default"
          plain
          @click="secondaryAction.action"
        >
          {{ secondaryAction.text }}
        </el-button>
      </template>
      
      <!-- 查看详情按钮 -->
      <el-button
        type="default"
        size="default"
        plain
        @click="goToDetail"
      >
        查看详情
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.order-card {
  background: $bg-white;
  border-radius: $radius-lg;
  margin-bottom: $spacing-md;
  overflow: hidden;
  
  // 订单头部
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-md;
    border-bottom: 1px solid $border-light;
    cursor: pointer;
    
    .order-no {
      .label {
        font-size: $font-sm;
        color: $text-tertiary;
      }
      
      .value {
        font-size: $font-sm;
        color: $text-secondary;
        margin-left: $spacing-xs;
      }
    }
    
    .order-status {
      font-size: $font-md;
      font-weight: 500;
    }
  }
  
  // 订单内容
  .order-content {
    padding: $spacing-md;
    cursor: pointer;
    
    .drug-list {
      display: flex;
      gap: $spacing-sm;
      margin-bottom: $spacing-md;
      
      .drug-thumb {
        position: relative;
        width: 60px;
        height: 60px;
        border-radius: $radius-md;
        overflow: hidden;
        background: $bg-gray;
        flex-shrink: 0;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        &.is-rx {
          border: 1px solid rgba($error, 0.3);
        }
        
        .rx-mark {
          position: absolute;
          top: 0;
          left: 0;
          background: $error;
          color: $text-white;
          font-size: 9px;
          font-weight: bold;
          padding: 1px 3px;
          border-bottom-right-radius: $radius-sm;
        }
      }
      
      .more-count {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: $bg-gray;
        border-radius: $radius-md;
        font-size: $font-sm;
        color: $text-secondary;
      }
    }
    
    .order-amount {
      .amount-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        &.delivery {
          margin-top: $spacing-xs;
          
          .label, .value {
            font-size: $font-sm;
            color: $text-tertiary;
          }
        }
        
        .label {
          font-size: $font-sm;
          color: $text-secondary;
        }
        
        .total-price {
          .symbol {
            font-size: $font-sm;
            color: $error;
          }
          
          .value {
            font-size: $font-lg;
            font-weight: bold;
            color: $error;
          }
        }
      }
    }
  }
  
  // 订单时间
  .order-time {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-sm $spacing-md;
    background: rgba($warning, 0.05);
    
    .el-icon {
      font-size: $font-md;
      color: $warning;
    }
    
    span {
      font-size: $font-sm;
      color: $warning;
    }
  }
  
  // 订单操作区
  .order-actions {
    display: flex;
    justify-content: flex-end;
    gap: $spacing-sm;
    padding: $spacing-md;
    border-top: 1px solid $border-light;
    flex-wrap: wrap;
  }
}
</style>
