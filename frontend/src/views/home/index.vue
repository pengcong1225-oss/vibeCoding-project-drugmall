<template>
  <div :class="['home-page', `theme-${activeTab}`]" ref="homePageRef">
    <!-- 头部渐变区域 - 包含搜索栏、Tab导航和各Tab的第一个区域 -->
    <div class="header-wrapper">
      <div class="header-gradient">
        <!-- 搜索栏和Tab导航 -->
        <component
          v-for="section in headerSections"
          :key="section.sectionId"
          :is="getSectionComponent(section.sectionType)"
          :section="section"
          :active-tab="activeTab"
          @tab-change="handleTabChange"
          @location-click="showLocationPicker = true"
          @cart-click="goToCart"
          @search-click="goToSearch"
          @scan-code="handleScanCode"
        />
        <!-- 各Tab的第一个区域 - 统一包含在渐变区域内 -->
        <!-- 推荐页促销横幅 -->
        <PromoBannerSection
          v-if="activeTab === 'recommend'"
          @left-click="handlePromoLeftClick"
          @center-click="handlePromoCenterClick"
          @right-click="handlePromoRightClick"
        />
        <!-- 问医生Tab - 秒问医生卡片 -->
        <QuickConsultCard
          v-if="activeTab === 'doctor'"
          @consult="handleQuickConsult"
        />
        <!-- 做检测Tab - 轮播图 -->
        <TestBannerSection
          v-if="activeTab === 'test'"
        />
        <!-- 滋补保健Tab - 轮播图 -->
        <TcmBannerSection
          v-if="activeTab === 'tcm'"
        />
        <!-- 慢病关怀Tab - 轮播图 -->
        <ChronicBannerSection
          v-if="activeTab === 'chronic'"
        />
      </div>
      <!-- 渐变过渡区域 -->
      <div class="header-fade"></div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 其他内容区域 -->
      <template v-if="activeTab === 'tcm'">
        <TonicTabSection />
      </template>
      <template v-else-if="activeTab === 'chronic'">
        <ChronicTabSection />
      </template>
      <template v-else>
        <component
          v-for="section in contentSections"
          :key="section.sectionId"
          :is="getSectionComponent(section.sectionType)"
          :section="section"
          :active-tab="activeTab"
          @tab-change="handleTabChange"
          @location-click="showLocationPicker = true"
          @cart-click="goToCart"
          @search-click="goToSearch"
          @scan-code="handleScanCode"
          @click="handleSectionClick(section.sectionType)"
        />
      </template>
    </div>

    <!-- 加载状态 -->
    <div v-if="homeStore.loading" class="loading-container">
      <Loading />
    </div>

    <!-- 错误状态 -->
    <div v-if="homeStore.error && !homeStore.loading" class="error-container">
      <Empty description="加载失败，点击重试" @click="homeStore.fetchHomePageConfig()" />
    </div>

    <!-- 位置选择弹窗 -->
    <el-dialog
      v-model="showLocationPicker"
      title="选择位置"
      width="90%"
      class="location-dialog"
    >
      <div class="location-list">
        <div
          v-for="loc in locations"
          :key="loc"
          class="location-item"
          :class="{ active: currentLocation === loc }"
          @click="selectLocation(loc)"
        >
          <span>{{ loc }}</span>
          <el-icon v-if="currentLocation === loc"><Check /></el-icon>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { useHomeStore } from '@/stores/home'

// 导入所有 Section 组件
import SearchBarSection from './components/SearchBarSection.vue'
import TabNavigationSection from './components/TabNavigationSection.vue'
import PromoBannerSection from './components/PromoBannerSection.vue'
import ServiceGridSection from './components/ServiceGridSection.vue'
import BannerSubsidySection from './components/BannerSubsidySection.vue'
import DoctorBannerSection from './components/DoctorBannerSection.vue'
import NearbyPharmacySection from './components/NearbyPharmacySection.vue'
import WaterfallLayoutSection from './components/WaterfallLayoutSection.vue'
import TestBannerSection from './components/TestBannerSection.vue'
import DoctorDepartmentSection from './components/DoctorDepartmentSection.vue'
import TestItemsSection from './components/TestItemsSection.vue'
import ChronicCategorySection from './components/ChronicCategorySection.vue'
import TcmCategorySection from './components/TcmCategorySection.vue'
// 导入新的Tab内容组件
import TonicTabSection from './components/TonicTabSection.vue'
import ChronicTabSection from './components/ChronicTabSection.vue'
// 导入Banner组件
import TcmBannerSection from './components/TcmBannerSection.vue'
import ChronicBannerSection from './components/ChronicBannerSection.vue'
// 导入问诊组件
import QuickConsultCard from '@/components/consultation/QuickConsultCard.vue'

// 导入公共组件
import Loading from '@/components/Loading/index.vue'
import Empty from '@/components/Empty/index.vue'

// 类型导入
import type { SectionType } from '@/types/home'

const router = useRouter()
const homeStore = useHomeStore()

// Refs
const homePageRef = ref<HTMLElement>()
const showLocationPicker = ref(false)
const currentLocation = ref('葛洲坝·世纪花园')
const locations = ['葛洲坝·世纪花园', '北京市朝阳区', '北京市海淀区', '北京市东城区']

// Tab状态管理
const activeTab = ref('recommend')

// 头部区域组件（搜索栏、Tab导航）
const headerSections = computed(() => {
  const sections = homeStore.sections
  const headerTypes = ['search_bar', 'tab_navigation']
  return sections.filter(s => headerTypes.includes(s.sectionType))
})

// 内容区域组件（根据Tab过滤）
const contentSections = computed(() => {
  const sections = homeStore.sections

  switch (activeTab.value) {
    case 'recommend':
      const recommendTypes = ['service_grid', 'banner_subsidy', 'doctor_banner', 'nearby_pharmacy', 'waterfall_layout']
      return sections.filter(s => recommendTypes.includes(s.sectionType))
    case 'doctor':
      return sections.filter(s => s.sectionType === 'doctor_department')
    case 'test':
      return sections.filter(s => s.sectionType === 'test_items')
    case 'chronic':
      return sections.filter(s => s.sectionType === 'chronic_category')
    case 'tcm':
      return sections.filter(s => s.sectionType === 'tcm_category')
    default:
      return []
  }
})

// 组件映射表
const componentMap: Record<SectionType, any> = {
  search_bar: SearchBarSection,
  tab_navigation: TabNavigationSection,
  promo_banner: PromoBannerSection,
  service_grid: ServiceGridSection,
  banner_subsidy: BannerSubsidySection,
  doctor_banner: DoctorBannerSection,
  nearby_pharmacy: NearbyPharmacySection,
  waterfall_layout: WaterfallLayoutSection,
  doctor_department: DoctorDepartmentSection,
  test_items: TestItemsSection,
  chronic_category: ChronicCategorySection,
  tcm_category: TcmCategorySection
}

function getSectionComponent(type: SectionType): any {
  return componentMap[type] || null
}

function handleTabChange(tabId: string) {
  activeTab.value = tabId
  console.log('Tab switched to:', tabId)
}

const goToSearch = () => router.push('/search')
const goToCart = () => router.push('/cart')
const handleScanCode = () => ElMessage.info('扫码功能开发中')

function selectLocation(loc: string) {
  currentLocation.value = loc
  showLocationPicker.value = false
  ElMessage.success(`已切换到${loc}`)
}

const handleScroll = () => {
  if (homePageRef.value) {
    const scrollTop = homePageRef.value.scrollTop
  }
}

function handleQuickConsult() {
  router.push('/inquiry/ai-triage')
}

// 处理区块点击
const handleSectionClick = (sectionType: string) => {
  if (sectionType === 'doctor_banner') {
    router.push('/inquiry/pre')
  }
}

// 促销横幅点击处理
const handlePromoLeftClick = () => {
  ElMessage.info('过敏报告功能开发中')
}
const handlePromoCenterClick = () => {
  router.push('/promotion/slimming')
}
const handlePromoRightClick = () => {
  router.push('/category/allergy')
}

onMounted(async () => {
  await homeStore.fetchHomePageConfig()
  homePageRef.value?.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  homePageRef.value?.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss">
@use '@/styles/variables' as *;

$primary-yellow: #FFD100;
$primary-teal: #00C9A7;
$bg-gray: #F5F5F5;
$bg-teal: #F0F9F6;
$bg-warm: #FFF9E6;

.home-page {
  min-height: 100vh;
  background: $bg-gray;
  padding-bottom: 80px;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;

  // 头部渐变包装器 - 固定高度结构
  .header-wrapper {
    position: relative;
    overflow: hidden;

    // 头部渐变区域 - 搜索栏和Tab导航
    .header-gradient {
      padding: 8px 0 12px;
      transition: background 0.3s ease;
      --tab-active-color: #FFD100;
      // 默认推荐Tab - 黄色渐变
      background: linear-gradient(180deg, #FFD100 0%, #FFE066 100%);
    }

    // 渐变过渡区域 - 平滑过渡到内容区
    .header-fade {
      height: 24px;
      background: linear-gradient(180deg, 
        #FFE066 0%, 
        rgba(255, 224, 102, 0.5) 30%, 
        rgba(255, 224, 102, 0.1) 70%, 
        transparent 100%
      );
      pointer-events: none;
    }
  }

  // 问医生Tab - 青绿色系
  &.theme-doctor {
    background: $bg-teal;

    .header-wrapper .header-gradient {
      background: linear-gradient(180deg, #00C9A7 0%, #00E5BF 100%);
      --tab-active-color: #00C9A7;
    }

    .header-wrapper .header-fade {
      background: linear-gradient(180deg, 
        #00E5BF 0%, 
        rgba(0, 229, 191, 0.5) 30%, 
        rgba(0, 229, 191, 0.1) 70%, 
        transparent 100%
      );
    }
  }

  // 滋补保健Tab - 天蓝色系
  &.theme-tcm {
    background: #E8F4FC;

    .header-wrapper .header-gradient {
      background: linear-gradient(180deg, #4A90E2 0%, #5BA3F5 100%);
      --tab-active-color: #4A90E2;
    }

    .header-wrapper .header-fade {
      background: linear-gradient(180deg, 
        #5BA3F5 0%, 
        rgba(91, 163, 245, 0.5) 30%, 
        rgba(91, 163, 245, 0.1) 70%, 
        transparent 100%
      );
    }
  }

  // 慢病关怀Tab - 医疗青绿色系
  &.theme-chronic {
    background: #E0F7F5;

    .header-wrapper .header-gradient {
      background: linear-gradient(180deg, #00A896 0%, #00C9B7 100%);
      --tab-active-color: #00A896;
    }

    .header-wrapper .header-fade {
      background: linear-gradient(180deg, 
        #00C9B7 0%, 
        rgba(0, 201, 183, 0.5) 30%, 
        rgba(0, 201, 183, 0.1) 70%, 
        transparent 100%
      );
    }
  }

  // 做检测Tab - 暖黄色渐变背景
  &.theme-test {
    background: $bg-warm;

    .header-wrapper .header-gradient {
      background: linear-gradient(180deg, #FFD93D 0%, #FFE066 100%);
      --tab-active-color: #FFD93D;
    }

    .header-wrapper .header-fade {
      background: linear-gradient(180deg, 
        #FFE066 0%, 
        rgba(255, 224, 102, 0.5) 30%, 
        rgba(255, 224, 102, 0.1) 70%, 
        transparent 100%
      );
    }
  }

  .content-area {
    position: relative;
    z-index: 1;
    padding: 0;
    min-height: calc(100vh - 200px);
  }
}

.loading-container,
.error-container {
  padding: 40px 16px;
  text-align: center;
}

.location-dialog {
  :deep(.el-dialog__header) {
    text-align: center;
    font-weight: bold;
  }

  .location-list {
    .location-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f5f5f5;
      cursor: pointer;

      &.active {
        color: #FFD100;
        font-weight: 500;
      }

      &:last-child {
        border-bottom: none;
      }
    }
  }
}
</style>
