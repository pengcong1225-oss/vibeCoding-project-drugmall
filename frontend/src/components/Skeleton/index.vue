<script setup lang="ts">
interface Props {
  // 骨架屏类型
  type?: 'text' | 'card' | 'list' | 'image' | 'avatar' | 'paragraph'
  // 行数（用于 paragraph 类型）
  rows?: number
  // 是否显示动画
  animated?: boolean
  // 宽度
  width?: string
  // 高度
  height?: string
}

withDefaults(defineProps<Props>(), {
  type: 'text',
  rows: 3,
  animated: true,
  width: '100%',
  height: 'auto'
})
</script>

<template>
  <div 
    class="skeleton"
    :class="{ 
      [`skeleton--${type}`]: type,
      'is-animated': animated 
    }"
    :style="{ width, height }"
  >
    <!-- 文本骨架 -->
    <template v-if="type === 'text'">
      <div class="skeleton__text"></div>
    </template>

    <!-- 卡片骨架 -->
    <template v-if="type === 'card'">
      <div class="skeleton__image"></div>
      <div class="skeleton__content">
        <div class="skeleton__title"></div>
        <div class="skeleton__subtitle"></div>
      </div>
    </template>

    <!-- 列表骨架 -->
    <template v-if="type === 'list'">
      <div v-for="i in 3" :key="i" class="skeleton__list-item">
        <div class="skeleton__avatar"></div>
        <div class="skeleton__lines">
          <div class="skeleton__line"></div>
          <div class="skeleton__line-short"></div>
        </div>
      </div>
    </template>

    <!-- 图片骨架 -->
    <template v-if="type === 'image'">
      <div class="skeleton__image-box">
        <el-icon class="skeleton__image-icon"><Picture /></el-icon>
      </div>
    </template>

    <!-- 头像骨架 -->
    <template v-if="type === 'avatar'">
      <div class="skeleton__avatar-circle"></div>
    </template>

    <!-- 段落骨架 -->
    <template v-if="type === 'paragraph'">
      <div v-for="i in rows" :key="i" class="skeleton__paragraph-line"></div>
    </template>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

// 动画
@keyframes skeleton-loading {
  0% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}

.skeleton {
  // 基础样式
  background: $bg-gray;
  
  // 动画效果
  &.is-animated {
    background: linear-gradient(
      90deg,
      $bg-gray 25%,
      #e8e8e8 50%,
      $bg-gray 75%
    );
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s ease-in-out infinite;
  }

  // 文本骨架
  &--text {
    height: 16px;
    border-radius: $radius-sm;
  }

  // 卡片骨架
  &--card {
    background: $bg-white;
    border-radius: $radius-lg;
    overflow: hidden;

    .skeleton__image {
      width: 100%;
      height: 200px;
      background: $bg-gray;
    }

    .skeleton__content {
      padding: $spacing-md;
    }

    .skeleton__title {
      height: 20px;
      background: $bg-gray;
      border-radius: $radius-sm;
      margin-bottom: $spacing-sm;
    }

    .skeleton__subtitle {
      height: 14px;
      width: 60%;
      background: $bg-gray;
      border-radius: $radius-sm;
    }
  }

  // 列表骨架
  &--list {
    background: transparent;

    .skeleton__list-item {
      display: flex;
      align-items: center;
      padding: $spacing-md;
      background: $bg-white;
      margin-bottom: $spacing-sm;
      border-radius: $radius-md;
    }

    .skeleton__avatar {
      width: 48px;
      height: 48px;
      background: $bg-gray;
      border-radius: 50%;
      margin-right: $spacing-md;
      flex-shrink: 0;
    }

    .skeleton__lines {
      flex: 1;
    }

    .skeleton__line {
      height: 16px;
      background: $bg-gray;
      border-radius: $radius-sm;
      margin-bottom: $spacing-xs;
    }

    .skeleton__line-short {
      height: 14px;
      width: 60%;
      background: $bg-gray;
      border-radius: $radius-sm;
    }
  }

  // 图片骨架
  &--image {
    background: $bg-gray;
    border-radius: $radius-md;

    .skeleton__image-box {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 150px;
      color: $text-tertiary;
    }

    .skeleton__image-icon {
      font-size: 48px;
    }
  }

  // 头像骨架
  &--avatar {
    background: transparent;

    .skeleton__avatar-circle {
      width: 48px;
      height: 48px;
      background: $bg-gray;
      border-radius: 50%;
    }
  }

  // 段落骨架
  &--paragraph {
    background: transparent;

    .skeleton__paragraph-line {
      height: 16px;
      background: $bg-gray;
      border-radius: $radius-sm;
      margin-bottom: $spacing-sm;

      &:last-child {
        width: 60%;
        margin-bottom: 0;
      }
    }
  }
}
</style>
