<template>
  <div class="category-tabs-section">
    <div
      ref="tabsContainerRef"
      class="category-tabs"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
    >
      <div
        v-for="tab in tabs"
        :key="tab.id"
        class="tab-item"
        :class="{ active: activeTab === tab.id }"
        @click="switchTab(tab.id)"
        :ref="el => setTabRef(el, tab.id)"
      >
        <!-- 选中时显示图标 -->
        <template v-if="activeTab === tab.id">
          <img v-if="tab.iconUrl" :src="tab.iconUrl" class="tab-icon-img" :alt="tab.name" />
          <el-icon v-else-if="tab.icon" class="tab-icon">
            <component :is="tab.icon" />
          </el-icon>
        </template>
        <span class="tab-name">{{ tab.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { HomeSection, TabNavItem } from '@/types/home'

const props = defineProps<{
  section: HomeSection
  activeTab?: string
}>()

const emit = defineEmits<{
  (e: 'tabChange', tabId: string): void
}>()

const router = useRouter()

const activeTab = ref(props.activeTab || 'recommend')
const tabsContainerRef = ref<HTMLElement>()
const tabRefs = ref<Map<string, HTMLElement>>(new Map())

// 触摸滑动相关
const touchStartX = ref(0)
const touchEndX = ref(0)
const touchStartY = ref(0)
const isSwiping = ref(false)
const minSwipeDistance = 50

// 定义tabs数据 - 美团买药风格
const tabs = ref<TabNavItem[]>([
  { id: 'recommend', name: '推荐', icon: 'Star', iconUrl: '' },
  { id: 'doctor', name: '问医生', icon: 'User', iconUrl: '' },
  { id: 'test', name: '做检测', icon: 'FirstAidKit', iconUrl: '' },
  { id: 'tcm', name: '滋补保健', icon: 'FirstAidKit', iconUrl: '' },
  { id: 'chronic', name: '慢病关怀', icon: 'FirstAidKit', iconUrl: '' }
])

// 根据当前Tab计算主题色
const themeColor = computed(() => {
  return activeTab.value === 'doctor' ? '#00C9A7' : '#FFD100'
})

watch(() => props.activeTab, (newVal) => {
  if (newVal) {
    activeTab.value = newVal
    scrollToActiveTab()
  }
})

const setTabRef = (el: HTMLElement | null, tabId: string) => {
  if (el) {
    tabRefs.value.set(tabId, el)
  }
}

const scrollToActiveTab = () => {
  nextTick(() => {
    const activeTabEl = tabRefs.value.get(activeTab.value)
    if (activeTabEl && tabsContainerRef.value) {
      const container = tabsContainerRef.value
      const tabLeft = activeTabEl.offsetLeft
      const tabWidth = activeTabEl.offsetWidth
      const containerWidth = container.offsetWidth
      
      if (tabLeft < container.scrollLeft) {
        container.scrollTo({ left: tabLeft - 8, behavior: 'smooth' })
      } else if (tabLeft + tabWidth > container.scrollLeft + containerWidth) {
        container.scrollTo({ left: tabLeft + tabWidth - containerWidth + 8, behavior: 'smooth' })
      }
    }
  })
}

const getCurrentTabIndex = () => {
  return tabs.value.findIndex(tab => tab.id === activeTab.value)
}

function switchTab(id: string) {
  activeTab.value = id
  
  emit('tabChange', id)
  scrollToActiveTab()
}

const switchToPrevTab = () => {
  const currentIndex = getCurrentTabIndex()
  if (currentIndex > 0) {
    switchTab(tabs.value[currentIndex - 1].id)
  }
}

const switchToNextTab = () => {
  const currentIndex = getCurrentTabIndex()
  if (currentIndex < tabs.value.length - 1) {
    switchTab(tabs.value[currentIndex + 1].id)
  }
}

const handleTouchStart = (e: TouchEvent) => {
  touchStartX.value = e.touches[0].clientX
  touchStartY.value = e.touches[0].clientY
  isSwiping.value = false
}

const handleTouchMove = (e: TouchEvent) => {
  if (!touchStartX.value) return
  
  const currentX = e.touches[0].clientX
  const currentY = e.touches[0].clientY
  const diffX = touchStartX.value - currentX
  const diffY = touchStartY.value - currentY
  
  if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 10) {
    isSwiping.value = true
  }
}

const handleTouchEnd = (e: TouchEvent) => {
  if (!isSwiping.value) return
  
  touchEndX.value = e.changedTouches[0].clientX
  const diff = touchStartX.value - touchEndX.value
  
  if (Math.abs(diff) > minSwipeDistance) {
    if (diff > 0) {
      switchToNextTab()
    } else {
      switchToPrevTab()
    }
  }
  
  touchStartX.value = 0
  touchEndX.value = 0
  isSwiping.value = false
}

onMounted(() => {
  scrollToActiveTab()
})
</script>

<style scoped lang="scss">
$primary-yellow: #FFD100;
$primary-teal: #00C9A7;
$text-primary: #333333;
$text-secondary: #666666;

.category-tabs-section {
  background: transparent;
  padding: 0 12px 4px;
  transition: all 0.3s ease;
}

.category-tabs {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
  padding: 6px 2px;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 18px;
  font-size: 15px;
  color: rgba(51, 51, 51, 0.75);
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4px);
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.25s ease;
  user-select: none;
  flex-shrink: 0;
  position: relative;

  .tab-icon {
    font-size: 16px;
  }

  .tab-icon-img {
    width: 18px;
    height: 18px;
    object-fit: contain;
  }

  .tab-name {
    font-weight: 500;
  }

  // 选中状态：白色背景 + 图标 + 当前页面配色 + 底部小三角
  &.active {
    background: #fff;
    color: $text-primary;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .tab-icon {
      color: var(--tab-active-color, $primary-yellow);
    }

    // 底部小三角
    &::after {
      content: '';
      position: absolute;
      bottom: -4px;
      left: 50%;
      transform: translateX(-50%);
      width: 0;
      height: 0;
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-top: 5px solid #fff;
    }
  }

  &:active {
    transform: scale(0.96);
  }
}
</style>
