<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Check } from '@element-plus/icons-vue'
import { formatPrice } from '@/utils'
import { getDrugDetailRoute } from '@/constants/routes'
import type { Drug } from '@/types'

const props = withDefaults(defineProps<{
  drug: Drug
  layout?: 'vertical' | 'horizontal'
  showTag?: boolean
  showSales?: boolean
}>(), {
  layout: 'vertical',
  showTag: false,
  showSales: false
})

const emit = defineEmits<{
  (e: 'addToCart', drug: Drug): void
}>()

const router = useRouter()

const drugImage = computed(() => props.drug.imageUrl || props.drug.image || '')

const goToDetail = () => {
  router.push(getDrugDetailRoute(props.drug.id))
}

const isAdding = ref(false)

const handleAddToCart = (event: Event) => {
  event.stopPropagation()
  isAdding.value = true
  emit('addToCart', props.drug)
  
  // 1.5秒后重置状态
  setTimeout(() => {
    isAdding.value = false
  }, 1500)
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
        v-lazy="drugImage"
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
      
      <!-- 标签和销量合并一行 -->
      <div class="drug-meta">
        <!-- Rx标识前置 -->
        <span v-if="drug.isRx" class="rx-tag">Rx</span>
        <span v-else class="otc-tag">OTC</span>
        
        <!-- 标签 -->
        <div v-if="showTag && drug.tags?.length" class="drug-tags">
          <span v-for="tag in drug.tags.slice(0, 2)" :key="tag" class="tag">{{ tag }}</span>
        </div>
        
        <!-- 销量 -->
        <span v-if="showSales && drug.sales > 0" class="drug-sales">
          已售{{ drug.sales > 10000 ? (drug.sales / 10000).toFixed(1) + '万' : drug.sales }}
        </span>
      </div>
      
      <!-- 底部信息 -->
      <div class="drug-footer">
        <div class="price-section">
          <span class="price-symbol">¥</span>
          <span class="price-value">
            <span class="integer">{{ Math.floor(drug.price) }}</span>
            <span class="decimal">.{{ (drug.price % 1).toFixed(2).slice(2) }}</span>
          </span>
          <span v-if="drug.originalPrice && drug.originalPrice > drug.price" class="original-price">
            ¥{{ formatPrice(drug.originalPrice) }}
          </span>
        </div>
        
        <button class="add-btn" :class="{ 'is-adding': isAdding }" @click="handleAddToCart">
          <el-icon v-if="!isAdding"><Plus /></el-icon>
          <el-icon v-else class="check-icon"><Check /></el-icon>
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
  
  // Rx标识（图片左上角）
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
    animation: rxPulse 2s infinite;
  }

  @keyframes rxPulse {
    0%, 100% {
      box-shadow: 0 0 0 0 rgba($error, 0.4);
    }
    50% {
      box-shadow: 0 0 0 4px rgba($error, 0);
    }
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
  
  .drug-meta {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    margin-bottom: $spacing-sm;
    flex-wrap: wrap;

    .rx-tag {
      font-size: $font-xs;
      color: $error;
      background: rgba($error, 0.1);
      padding: 2px 6px;
      border-radius: $radius-sm;
      font-weight: 600;
    }

    .otc-tag {
      font-size: $font-xs;
      color: $success;
      background: rgba($success, 0.1);
      padding: 2px 6px;
      border-radius: $radius-sm;
      font-weight: 600;
    }

    .drug-tags {
      display: flex;
      gap: $spacing-xs;
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
      font-size: $font-xs;
      color: $text-tertiary;
    }
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
    gap: 2px;
    
    .price-symbol {
      font-size: $font-sm;
      color: $error;
      font-weight: 600;
    }
    
    .price-value {
      font-size: 22px;
      font-weight: bold;
      color: $error;
      
      .decimal {
        font-size: $font-md;
      }
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
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &:hover {
      background: $primary-dark;
      transform: scale(1.05);
    }
    
    &:active {
      transform: scale(0.95);
    }
    
    &.is-adding {
      background: $success;
      animation: addSuccess 0.5s ease;
      
      .check-icon {
        animation: checkPop 0.3s ease;
      }
    }
  }
}

@keyframes addSuccess {
  0% {
    transform: scale(1);
  }
  30% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes checkPop {
  0% {
    transform: scale(0) rotate(-45deg);
    opacity: 0;
  }
  70% {
    transform: scale(1.2) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
  }
}
</style>
