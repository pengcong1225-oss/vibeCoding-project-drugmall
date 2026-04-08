<script setup lang="ts">
import { computed } from 'vue'

type EmptyType = 'default' | 'search' | 'cart' | 'order' | 'address' | 'coupon' | 'message' | 'network'

interface Props {
  type?: EmptyType
  title?: string
  description?: string
  showButton?: boolean
  buttonText?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  title: '',
  description: '',
  showButton: true,
  buttonText: ''
})

const emit = defineEmits<{
  (e: 'click'): void
}>()

// 默认配置
const defaultConfigs: Record<EmptyType, { icon: string; title: string; desc: string; btn: string }> = {
  default: {
    icon: 'Box',
    title: '暂无数据',
    desc: '暂时没有相关内容',
    btn: '刷新'
  },
  search: {
    icon: 'Search',
    title: '没有找到相关商品',
    desc: '换个关键词试试吧',
    btn: '清除搜索'
  },
  cart: {
    icon: 'ShoppingCart',
    title: '购物车是空的',
    desc: '快去选购心仪的商品吧',
    btn: '去逛逛'
  },
  order: {
    icon: 'Document',
    title: '暂无订单',
    desc: '您还没有相关订单',
    btn: '去购买'
  },
  address: {
    icon: 'Location',
    title: '暂无收货地址',
    desc: '请添加收货地址',
    btn: '添加地址'
  },
  coupon: {
    icon: 'Ticket',
    title: '暂无优惠券',
    desc: '您还没有可用的优惠券',
    btn: '去领券'
  },
  message: {
    icon: 'ChatDotRound',
    title: '暂无消息',
    desc: '您还没有收到新消息',
    btn: '刷新'
  },
  network: {
    icon: 'Connection',
    title: '网络异常',
    desc: '请检查网络连接后重试',
    btn: '重试'
  }
}

const displayTitle = computed(() => props.title || defaultConfigs[props.type].title)
const displayDesc = computed(() => props.description || defaultConfigs[props.type].desc)
const displayBtn = computed(() => props.buttonText || defaultConfigs[props.type].btn)
const displayIcon = computed(() => defaultConfigs[props.type].icon)

const handleClick = () => {
  emit('click')
}
</script>

<template>
  <div class="empty-state" :class="`type-${type}`">
    <div class="empty-icon">
      <el-icon :size="64">
        <component :is="displayIcon" />
      </el-icon>
    </div>
    
    <h3 class="empty-title">{{ displayTitle }}</h3>
    
    <p v-if="displayDesc" class="empty-description">
      {{ displayDesc }}
    </p>
    
    <div v-if="showButton" class="empty-action">
      <el-button type="primary" @click="handleClick">
        {{ displayBtn }}
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xxl $spacing-lg;
  text-align: center;
  min-height: 300px;
  
  .empty-icon {
    margin-bottom: $spacing-lg;
    
    .el-icon {
      color: $text-tertiary;
      opacity: 0.5;
    }
  }
  
  .empty-title {
    font-size: $font-lg;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-sm;
  }
  
  .empty-description {
    font-size: $font-md;
    color: $text-tertiary;
    margin-bottom: $spacing-lg;
    line-height: 1.5;
  }
  
  .empty-action {
    .el-button {
      min-width: 120px;
    }
  }
  
  // 不同类型样式
  &.type-search {
    .empty-icon .el-icon {
      color: $warning;
    }
  }
  
  &.type-network {
    .empty-icon .el-icon {
      color: $error;
    }
  }
}
</style>
