<template>
  <div class="service-shortcuts">
    <!-- 滑动长方形卡片列表 -->
    <div class="shortcuts-slider" ref="sliderRef">
      <div
        v-for="(item, index) in shortcuts"
        :key="index"
        class="shortcut-card"
        @click="$emit('click', item)"
      >
        <div class="card-content">
          <div class="text-content">
            <div class="card-title">{{ item.name }}</div>
            <div class="card-subtitle">{{ item.subtitle }}</div>
          </div>
          <div class="avatar-wrapper" v-if="item.doctorAvatar">
            <img :src="item.doctorAvatar" class="doctor-avatar" :alt="item.name" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

export interface ShortcutItem {
  name: string
  subtitle: string
  doctorAvatar?: string
}

const props = defineProps<{
  shortcuts: ShortcutItem[]
}>()

const emit = defineEmits<{
  (e: 'click', item: ShortcutItem): void
}>()

const sliderRef = ref<HTMLElement>()
</script>

<style scoped lang="scss">
$primary-teal: #00C9A7;
$bg-teal: #F0F9F6;
$text-primary: #1A1A1A;
$text-secondary: #666666;

.service-shortcuts {
  padding: 0 12px 10px;
  background-color: $bg-teal;

  .shortcuts-slider {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    -webkit-overflow-scrolling: touch;
    padding-bottom: 4px;

    &::-webkit-scrollbar {
      display: none;
    }

    .shortcut-card {
      flex-shrink: 0;
      width: 140px;
      height: 70px;
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      cursor: pointer;
      transition: all 0.2s ease;
      overflow: hidden;

      &:active {
        transform: scale(0.98);
      }

      .card-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px;
        height: 100%;

        .text-content {
          flex: 1;
          min-width: 0;

          .card-title {
            font-size: 14px;
            font-weight: 600;
            color: $text-primary;
            margin-bottom: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .card-subtitle {
            font-size: 11px;
            color: $text-secondary;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .avatar-wrapper {
          flex-shrink: 0;
          margin-left: 8px;

          .doctor-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #f0f0f0;
          }
        }
      }
    }
  }
}
</style>
