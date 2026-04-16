<template>
  <div class="expert-filter-bar">
    <div class="filter-tags">
      <button
        v-for="dept in departmentTags"
        :key="dept.value"
        :class="['filter-tag', { active: selectedDepartment === dept.value }]"
        @click="$emit('departmentChange', dept.value)"
      >
        {{ dept.label }}
      </button>
      <button class="filter-tag more-btn" @click="$emit('showAllDepartments')">
        全部
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
          <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>
    <div class="sort-filter-row">
      <div class="sort-selector" @click="$emit('toggleSort')">
        <span>{{ currentSortLabel }}</span>
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
          <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <button class="specialty-filter" @click="$emit('toggleSpecialty')">
        医生擅长
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
          <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <button class="filter-btn" @click="$emit('openFilter')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        筛选
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface FilterTag {
  value: string
  label: string
}

type SortType = 'comprehensive' | 'rating' | 'consultationCount' | 'responseTime'

const props = withDefaults(defineProps<{
  departmentTags: FilterTag[]
  selectedDepartment: string
  sortType?: SortType
}>(), {
  selectedDepartment: '',
  sortType: 'comprehensive'
})

defineEmits<{
  (e: 'departmentChange', value: string): void
  (e: 'showAllDepartments'): void
  (e: 'toggleSort'): void
  (e: 'toggleSpecialty'): void
  (e: 'openFilter'): void
}>()

const currentSortLabel = computed(() => {
  const map: Record<SortType, string> = {
    comprehensive: '综合排序',
    rating: '好评率',
    consultationCount: '问诊量',
    responseTime: '响应速度'
  }
  return map[props.sortType] || '综合排序'
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.expert-filter-bar {
  padding: 16px;
  background-color: $bg-primary;

  .filter-tags {
    display: flex;
    gap: 10px;
    margin-bottom: 14px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      display: none;
    }

    .filter-tag {
      padding: 7px 16px;
      background-color: $bg-white;
      border: 1px solid $border-light;
      border-radius: 18px;
      font-size: 13px;
      color: $text-secondary;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;
      gap: 4px;

      &.active {
        background-color: rgba($primary, 0.1);
        border-color: $primary;
        color: $primary;
        font-weight: 500;
      }

      &:active {
        transform: scale(0.96);
      }

      &.more-btn {
        svg {
          color: inherit;
        }
      }
    }
  }

  .sort-filter-row {
    display: flex;
    align-items: center;
    gap: 16px;

    .sort-selector,
    .specialty-filter {
      display: flex;
      align-items: center;
      gap: 4px;
      background: transparent;
      border: none;
      font-size: 13px;
      color: $text-secondary;
      cursor: pointer;
      padding: 4px 0;

      svg {
        color: $text-tertiary;
      }

      &:active {
        color: $primary;

        svg {
          color: $primary;
        }
      }
    }

    .specialty-filter {
      position: relative;

      &::after {
        content: '';
        position: absolute;
        right: -8px;
        top: 50%;
        transform: translateY(-50%);
        width: 1px;
        height: 12px;
        background-color: $border-light;
      }
    }

    .filter-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-left: auto;
      padding: 6px 14px;
      background-color: $bg-white;
      border: 1px solid $border-light;
      border-radius: 16px;
      font-size: 13px;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      svg {
        color: $primary;
      }

      &:active {
        background-color: $bg-primary;
        border-color: $border-color;
      }
    }
  }
}
</style>
