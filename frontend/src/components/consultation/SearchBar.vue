<template>
  <div class="search-bar">
    <div class="search-input-wrapper">
      <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none">
        <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
        <path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <input
        ref="inputRef"
        v-model="searchText"
        type="text"
        class="search-input"
        :placeholder="placeholder"
        @focus="$emit('focus')"
        @blur="$emit('blur')"
        @input="handleInput"
        @keyup.enter="handleSearch"
      />
      <button v-if="searchText" class="clear-btn" @click="clearSearch">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="9" fill="#E8E8E8"/>
          <path d="M8 8l8 8M16 8l-8 8" stroke="#666666" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
    <button class="camera-btn" @click="$emit('cameraClick')">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2zM15 13a4 4 0 11-8 0 4 4 0 018 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
    <button class="search-btn" @click="handleSearch">搜索</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = withDefaults(defineProps<{
  placeholder?: string
  modelValue?: string
}>(), {
  placeholder: '搜索医生/科室/症状...',
  modelValue: ''
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
  (e: 'cameraClick'): void
  (e: 'focus'): void
  (e: 'blur'): void
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const searchText = ref(props.modelValue)

function handleInput() {
  emit('update:modelValue', searchText.value)
}

function handleSearch() {
  if (searchText.value.trim()) {
    emit('search', searchText.value.trim())
  }
}

function clearSearch() {
  searchText.value = ''
  emit('update:modelValue', '')
  inputRef.value?.focus()
}

defineExpose({
  focus: () => inputRef.value?.focus(),
  clear: clearSearch
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background-color: $bg-white;

  .search-input-wrapper {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    background-color: $bg-primary;
    border-radius: 22px;
    border: none;

    .search-icon {
      color: $text-tertiary;
      flex-shrink: 0;
    }

    .search-input {
      flex: 1;
      background: transparent;
      border: none;
      outline: none;
      font-size: 14px;
      color: $text-primary;
      line-height: 1.4;

      &::placeholder {
        color: $text-tertiary;
      }
    }

    .clear-btn {
      background: transparent;
      border: none;
      padding: 2px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      &:active {
        opacity: 0.7;
      }
    }
  }

  .camera-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: transparent;
    border: 1px solid $border-light;
    border-radius: 10px;
    cursor: pointer;
    color: $text-secondary;
    flex-shrink: 0;
    transition: all 0.2s;

    &:active {
      background-color: $bg-primary;
      border-color: $border-color;
    }
  }

  .search-btn {
    padding: 10px 18px;
    background-color: $primary;
    color: white;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;
    flex-shrink: 0;

    &:active {
      background-color: $primary-light;
      transform: scale(0.96);
    }
  }
}
</style>
