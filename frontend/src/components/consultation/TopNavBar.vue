<template>
  <div class="top-nav-bar">
    <div class="nav-left">
      <span class="brand-text">看病买药</span>
      <div class="location-selector" @click="$emit('selectLocation')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="12" cy="9" r="2.5" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span class="location-text">{{ location }}</span>
        <svg class="dropdown-icon" width="10" height="10" viewBox="0 0 24 24" fill="none">
          <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
    <div class="nav-right">
      <button class="cart-btn" @click="$emit('goToCart')">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M9 2L7 6M17 2l-2 4M3 6h18M5 6v14a2 2 0 002 2h10a2 2 0 002-2V6M9 11v6M15 11v6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>购物车</span>
        <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  location?: string
  cartCount?: number
}>()

defineEmits<{
  (e: 'selectLocation'): void
  (e: 'goToCart'): void
}>()
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.top-nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: $bg-white;
  position: sticky;
  top: 0;
  z-index: 100;

  .nav-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .brand-text {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
    }

    .location-selector {
      display: flex;
      align-items: center;
      gap: 4px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 16px;
      transition: background-color 0.2s;

      &:active {
        background-color: $bg-primary;
      }

      svg:first-child {
        color: $primary;
      }

      .location-text {
        font-size: 13px;
        color: $text-secondary;
        max-width: 80px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .dropdown-icon {
        color: $text-tertiary;
      }
    }
  }

  .nav-right {
    .cart-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 6px 12px;
      background-color: transparent;
      border: none;
      border-radius: 18px;
      font-size: 13px;
      color: $text-primary;
      cursor: pointer;
      position: relative;
      transition: background-color 0.2s;

      &:active {
        background-color: $bg-primary;
      }

      svg {
        color: $primary;
      }

      span:last-of-type:not(.cart-badge) {
        color: $text-secondary;
      }

      .cart-badge {
        position: absolute;
        top: -2px;
        right: -2px;
        min-width: 16px;
        height: 16px;
        padding: 0 4px;
        background-color: $error;
        color: white;
        font-size: 10px;
        font-weight: 600;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}
</style>
