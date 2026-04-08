<script setup lang="ts">
import { computed } from 'vue'
import { formatPrice } from '@/utils'

interface Props {
  price: number | string
  originalPrice?: number | string
  size?: 'small' | 'medium' | 'large'
  showSymbol?: boolean
  showOriginal?: boolean
  originalPosition?: 'right' | 'bottom'
  isFen?: boolean
  highlight?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  showSymbol: true,
  showOriginal: true,
  originalPosition: 'right',
  isFen: false,
  highlight: false
})

const formattedPrice = computed(() => formatPrice(props.price, props.isFen))

const formattedOriginalPrice = computed(() => {
  if (!props.originalPrice) return ''
  return formatPrice(props.originalPrice, props.isFen)
})

const hasDiscount = computed(() => {
  if (!props.originalPrice) return false
  const original = typeof props.originalPrice === 'string' 
    ? parseFloat(props.originalPrice) 
    : props.originalPrice
  const current = typeof props.price === 'string' 
    ? parseFloat(props.price) 
    : props.price
  return original > current
})

const discountPercent = computed(() => {
  if (!hasDiscount.value) return 0
  const original = typeof props.originalPrice === 'string' 
    ? parseFloat(props.originalPrice) 
    : props.originalPrice!
  const current = typeof props.price === 'string' 
    ? parseFloat(props.price) 
    : props.price
  return Math.round((current / original) * 10)
})
</script>

<template>
  <span 
    class="price-component" 
    :class="[
      `size-${size}`,
      { 'has-original': showOriginal && originalPrice, 'highlight': highlight }
    ]"
  >
    <!-- 当前价格 -->
    <span class="current-price">
      <span v-if="showSymbol" class="symbol">¥</span>
      <span class="value">{{ formattedPrice }}</span>
    </span>
    
    <!-- 原价 - 右侧位置 -->
    <span 
      v-if="showOriginal && originalPrice && originalPosition === 'right'" 
      class="original-price right"
    >
      <span class="symbol">¥</span>
      <span class="value">{{ formattedOriginalPrice }}</span>
    </span>
    
    <!-- 折扣标签 -->
    <span v-if="hasDiscount && discountPercent < 10" class="discount-badge">
      {{ discountPercent }}折
    </span>
    
    <!-- 原价 - 底部位置 -->
    <span 
      v-if="showOriginal && originalPrice && originalPosition === 'bottom'" 
      class="original-price bottom"
    >
      <span class="symbol">¥</span>
      <span class="value">{{ formattedOriginalPrice }}</span>
    </span>
  </span>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.price-component {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: $spacing-xs;
  
  // 尺寸样式
  &.size-small {
    .current-price {
      .symbol {
        font-size: $font-xs;
      }
      .value {
        font-size: $font-sm;
      }
    }
    
    .original-price {
      font-size: $font-xs;
    }
  }
  
  &.size-medium {
    .current-price {
      .symbol {
        font-size: $font-sm;
      }
      .value {
        font-size: $font-lg;
      }
    }
    
    .original-price {
      font-size: $font-sm;
    }
  }
  
  &.size-large {
    .current-price {
      .symbol {
        font-size: $font-md;
      }
      .value {
        font-size: $font-xxl;
      }
    }
    
    .original-price {
      font-size: $font-md;
    }
  }
  
  // 高亮样式
  &.highlight {
    .current-price .value {
      color: $error;
    }
  }
  
  // 当前价格
  .current-price {
    font-weight: bold;
    color: $error;
    
    .symbol {
      font-weight: 500;
      margin-right: 1px;
    }
    
    .value {
      font-family: 'DIN Alternate', 'Helvetica Neue', Arial, sans-serif;
    }
  }
  
  // 原价
  .original-price {
    color: $text-tertiary;
    text-decoration: line-through;
    font-weight: normal;
    
    &.bottom {
      width: 100%;
      margin-top: 2px;
    }
    
    .symbol {
      font-size: 0.9em;
    }
  }
  
  // 折扣标签
  .discount-badge {
    display: inline-flex;
    align-items: center;
    background: $warning;
    color: $text-white;
    font-size: $font-xs;
    font-weight: bold;
    padding: 2px 6px;
    border-radius: $radius-sm;
    margin-left: 4px;
  }
}
</style>
