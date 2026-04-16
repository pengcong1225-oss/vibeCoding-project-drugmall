<template>
  <div class="tab-content">
    <div class="chronic-banner" :style="{ background: section.config?.bannerBg || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
      <div class="chronic-content">
        <h3>{{ section.title || '慢病管理中心' }}</h3>
        <p>{{ section.subtitle || '高血压、糖尿病等慢性病用药专区' }}</p>
        <div class="chronic-btn" @click="$emit('bannerClick')">{{ section.config?.btnText || '立即查看' }}</div>
      </div>
    </div>
    <div class="chronic-categories">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="chronic-item"
        @click="handleCategoryClick(cat)"
      >
        <div class="chronic-icon" :style="{ background: cat.bgColor }">
          <img v-if="cat.iconUrl" :src="cat.iconUrl" class="chronic-icon-img" />
          <span v-else>{{ cat.icon }}</span>
        </div>
        <span class="chronic-name">{{ cat.name }}</span>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { HomeSection, CategoryItemData } from '@/types/home'

const props = defineProps<{ section: HomeSection }>()
const emit = defineEmits<{
  (e: 'bannerClick'): void
  (e: 'categoryClick', item: CategoryItemData): void
}>()

const categories = computed<CategoryItemData[]>(() => {
  return props.section.components.map(c => c.data)
})

function handleCategoryClick(item: CategoryItemData) {
  emit('categoryClick', item)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$text-secondary: #666666;
$text-primary: #333333;

.tab-content {
  padding: 0 16px;

  .chronic-banner, .tcm-banner {
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 16px;
    color: #fff;

    h3 {
      font-size: 18px;
      font-weight: bold;
      margin: 0 0 8px;
    }

    p {
      font-size: 13px;
      opacity: 0.9;
      margin: 0 0 12px;
    }

    .chronic-btn, .tcm-btn {
      display: inline-block;
      background: rgba(255, 255, 255, 0.25);
      padding: 6px 16px;
      border-radius: 16px;
      font-size: 13px;
      cursor: pointer;
    }
  }

  .chronic-categories, .tcm-categories {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;

    .chronic-item, .tcm-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      background: #fff;
      border-radius: 12px;
      padding: 16px 12px;
      cursor: pointer;
      transition: transform 0.2s;

      &:active {
        transform: scale(0.96);
      }

      .chronic-icon, .tcm-icon {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 8px;
        font-size: 18px;
        font-weight: bold;
        color: $text-primary;
        overflow: hidden;

        .chronic-icon-img {
          width: 36px;
          height: 36px;
          object-fit: contain;
        }
      }

      .chronic-name {
        font-size: 12px;
        color: $text-secondary;
        text-align: center;
        line-height: 1.3;
      }
    }
  }
}
</style>
