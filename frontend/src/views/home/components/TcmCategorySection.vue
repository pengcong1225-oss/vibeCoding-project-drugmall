<template>
  <div class="tab-content">
    <div class="tcm-banner" :style="{ background: section.config?.bannerBg || 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' }">
      <div class="tcm-content">
        <h3>{{ section.title || '中医养生馆' }}</h3>
        <p>{{ section.subtitle || '中药材、养生茶饮、理疗保健' }}</p>
        <div class="tcm-btn" @click="$emit('bannerClick')">{{ section.config?.btnText || '探索更多' }}</div>
      </div>
    </div>
    <div class="tcm-categories">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="tcm-item"
        @click="handleCategoryClick(cat)"
      >
        <div class="tcm-icon" :style="{ background: cat.bgColor }">{{ cat.icon }}</div>
        <span>{{ cat.name }}</span>
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
      padding: 16px;
      cursor: pointer;

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
      }

      span {
        font-size: 12px;
        color: $text-secondary;
      }
    }
  }
}
</style>
