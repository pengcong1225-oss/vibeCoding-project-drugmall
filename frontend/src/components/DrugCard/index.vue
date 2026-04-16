<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatPrice } from '@/utils'
import type { Drug } from '@/types'

const props = defineProps<{
  drug: Drug
  layout?: 'vertical' | 'horizontal'
  showTag?: boolean
  showSales?: boolean
}>()

const emit = defineEmits<{
  (e: 'addToCart', drug: Drug): void
}>()

const router = useRouter()

const drugImage = computed(() => props.drug.imageUrl || props.drug.image || '')

const goToDetail = () => {
  router.push(`/drug/${props.drug.id}`)
}

const handleAddToCart = (event: Event) => {
  event.stopPropagation()
  emit('addToCart', props.drug)
}

const discount = computed(() => {
  if (props.drug.originalPrice && props.drug.originalPrice > props.drug.price) {
    return Math.round((props.drug.price / props.drug.originalPrice) * 10)
  }
  return null
})
</script>

<template>
  <div 
    class="drug-card" 
    :class="[`layout-${layout}`, { 'is-rx': drug.isRx }]"
    @click="goToDetail"
  >
    <!-- 药品图片 -->
    <div class="drug-image" :style="{ backgroundColor: drug.imageColor || '#00b578' }">
      <img
        v-if="drugImage"
        :src="drugImage"
        :alt="drug.name"
        class="drug-img"
      />
      <div v-else class="image-placeholder">
        <span class="image-text">{{ drug.imageText || drug.name.slice(0, 2) }}</span>
      </div>
      
      <!-- 处方药标识 -->
      <span v-if="drug.isRx" class="rx-badge">Rx</span>
      
      <!-- 折扣标识 -->
      <span v-if="discount" class="discount-badge">{{ discount }}折</span>
    </div>
    
    <!-- 药品信息 -->
    <div class="drug-info">
      <!-- 药品名称 -->
      <h3 class="drug-name" :title="drug.name">{{ drug.name }}</h3>
      
      <!-- 规格 -->
      <p class="drug-spec">{{ drug.specification }}</p>
      
      <!-- 标签 -->
      <div v-if="showTag && drug.tags?.length" class="drug-tags">
        <span v-for="tag in drug.tags.slice(0, 2)" :key="tag" class="tag">{{ tag }}</span>
      </div>
      
      <!-- 销量 -->
      <p v-if="showSales && drug.sales > 0" class="drug-sales">
        已售 {{ drug.sales > 10000 ? (drug.sales / 10000).toFixed(1) + '万' : drug.sales }}
      </p>
      
      <!-- 底部信息 -->
      <div class="drug-footer">
        <div class="price-section">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ formatPrice(drug.price) }}</span>
          <span v-if="drug.originalPrice && drug.originalPrice > drug.price" class="original-price">
            ¥{{ formatPrice(drug.originalPrice) }}
          </span>
        </div>
        
        <button class="add-btn" @click="handleAddToCart">
          <el-icon><Plus /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.drug-card {
  background: $bg-white;
  border-radius: $radius-lg;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    box-shadow: $shadow-md;
    transform: translateY(-2px);
  }
  
  // 垂直布局
  &.layout-vertical {
    display: flex;
    flex-direction: column;
    
    .drug-image {
      width: 100%;
      aspect-ratio: 1;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .drug-info {
      padding: $spacing-md;
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  }
  
  // 水平布局
  &.layout-horizontal {
    display: flex;
    padding: $spacing-md;
    gap: $spacing-md;
    
    .drug-image {
      width: 100px;
      height: 100px;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: $radius-md;
      }
    }
    
    .drug-info {
      flex: 1;
      min-width: 0;
    }
  }
  
  // 图片区域
  .drug-image {
    position: relative;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;

    .drug-img {
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
        font-size: 24px;
        font-weight: bold;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }
    }
  }
  
  // Rx标识
  .rx-badge {
    position: absolute;
    top: $spacing-xs;
    left: $spacing-xs;
    background: $error;
    color: $text-white;
    font-size: $font-xs;
    font-weight: bold;
    padding: 2px 4px;
    border-radius: $radius-sm;
  }
  
  // 折扣标识
  .discount-badge {
    position: absolute;
    top: $spacing-xs;
    right: $spacing-xs;
    background: $warning;
    color: $text-white;
    font-size: $font-xs;
    font-weight: bold;
    padding: 2px 6px;
    border-radius: $radius-sm;
  }
  
  // 药品信息
  .drug-name {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    line-height: 1.4;
    margin-bottom: $spacing-xs;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  .drug-spec {
    font-size: $font-sm;
    color: $text-tertiary;
    margin-bottom: $spacing-xs;
  }
  
  .drug-tags {
    display: flex;
    gap: $spacing-xs;
    margin-bottom: $spacing-xs;
    flex-wrap: wrap;
    
    .tag {
      font-size: $font-xs;
      color: $primary;
      background: rgba($primary, 0.1);
      padding: 2px 6px;
      border-radius: $radius-sm;
    }
  }
  
  .drug-sales {
    font-size: $font-sm;
    color: $text-tertiary;
    margin-bottom: $spacing-sm;
  }
  
  // 底部区域
  .drug-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: auto;
    padding-top: $spacing-xs;
  }
  
  .price-section {
    display: flex;
    align-items: baseline;
    gap: 4px;
    
    .price-symbol {
      font-size: $font-sm;
      color: $error;
      font-weight: 500;
    }
    
    .price-value {
      font-size: $font-lg;
      font-weight: bold;
      color: $error;
    }
    
    .original-price {
      font-size: $font-sm;
      color: $text-tertiary;
      text-decoration: line-through;
      margin-left: $spacing-xs;
    }
  }
  
  .add-btn {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: $primary;
    border: none;
    color: $text-white;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      background: $primary-dark;
      transform: scale(1.05);
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
}
</style>
