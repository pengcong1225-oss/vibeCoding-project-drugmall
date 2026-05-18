<template>
  <div :class="['home-page', `theme-${activeTab}`]" ref="homePageRef"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
    @touchend="handleTouchEnd"
  >
    <!-- 下拉刷新指示器 -->
    <div v-if="isPulling || isRefreshing" class="pull-refresh-indicator" :style="{ transform: `translateY(${refreshProgress * 60}px)` }">
      <div class="refresh-spinner" :class="{ spinning: isRefreshing }">
        <el-icon><Refresh /></el-icon>
      </div>
      <span class="refresh-text">{{ isRefreshing ? '刷新中...' : '下拉刷新' }}</span>
    </div>
    <!-- 头部渐变区域 - 包含搜索栏、Tab导航和各Tab的第一个区域 -->
    <div class="header-wrapper" :class="{ 'is-sticky': isSticky }">
      <!-- 吸顶时的占位元素，保持文档流稳定 -->
      <div v-if="isSticky" class="sticky-spacer"></div>
      <div class="header-gradient" :class="{ 'sticky': isSticky }">
        <!-- 搜索栏和Tab导航 -->
        <component
          v-for="section in headerSections"
          v-show="getSectionComponent(section.sectionType)"
          :key="section.sectionId"
          :is="getSectionComponent(section.sectionType)"
          :section="section"
          :active-tab="activeTab"
          @tab-change="handleTabChange"
          @location-click="showLocationPicker = true"
          @cart-click="goToCart"
          @search-click="goToSearch"
          @scan-code="handleScanCode"
          @voice-search="handleVoiceSearch"
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

        <!-- 吸顶时的金刚位区域 - 滋补保健和慢病关怀 -->
        <div v-if="isSticky && (activeTab === 'tcm' || activeTab === 'chronic')" class="sticky-kingkong">
          <div class="kingkong-scroll">
            <div
              v-for="item in kingkongItems"
              :key="item.id"
              class="kingkong-item"
              @click="handleKingkongClick(item)"
            >
              <img :src="item.iconUrl" class="kingkong-img" />
              <span class="kingkong-name">{{ item.name }}</span>
            </div>
          </div>
        </div>
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
          v-show="getSectionComponent(section.sectionType)"
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

    <!-- 回到顶部按钮 -->
    <ScrollToTop />

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
import { ROUTES } from '@/constants/routes'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Search, ShoppingCart, Refresh } from '@element-plus/icons-vue'
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
import ScrollToTop from '@/components/ScrollToTop/index.vue'

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

// Tab数据
const tabs = [
  { id: 'recommend', name: '推荐', icon: '' },
  { id: 'doctor', name: '问医生', icon: '' },
  { id: 'test', name: '做检测', icon: '' },
  { id: 'adult', name: '成人情趣', icon: '' },
  { id: 'tcm', name: '滋补保健', icon: '/images/icons/tonic.png' },
  { id: 'chronic', name: '慢病关怀', icon: '' }
]

// 吸顶金刚位数据 - 滋补保健
const tcmKingkongItems = [
  { id: 'tcm1', name: '大牌精选', iconUrl: '/images/categories/tonic-selected.png' },
  { id: 'tcm2', name: '礼赠佳品', iconUrl: '/images/categories/tonic-gift.png' },
  { id: 'tcm3', name: '益生菌', iconUrl: '/images/categories/tonic-probiotic.png' },
  { id: 'tcm4', name: '钙铁锌', iconUrl: '/images/categories/tonic-calcium.png' },
  { id: 'tcm5', name: '维生素', iconUrl: '/images/categories/tonic-vitamin.png' },
  { id: 'tcm6', name: '滋补养', iconUrl: '/images/categories/tonic-nourish.png' }
]

// 吸顶金刚位数据 - 慢病关怀
const chronicKingkongItems = [
  { id: 'chr1', name: '高血压', iconUrl: '/images/categories/chronic-hypertension.png' },
  { id: 'chr2', name: '糖尿病', iconUrl: '/images/categories/chronic-diabetes.png' },
  { id: 'chr3', name: '心脏病', iconUrl: '/images/categories/chronic-heart.png' },
  { id: 'chr4', name: '哮喘', iconUrl: '/images/categories/chronic-asthma.png' },
  { id: 'chr5', name: '痛风', iconUrl: '/images/categories/chronic-gout.png' },
  { id: 'chr6', name: '肝病', iconUrl: '/images/categories/chronic-liver.png' }
]

// 根据当前Tab获取金刚位数据
const kingkongItems = computed(() => {
  if (activeTab.value === 'tcm') return tcmKingkongItems
  if (activeTab.value === 'chronic') return chronicKingkongItems
  return []
})

// 处理金刚位点击
const handleKingkongClick = (item: { id: string; name: string; iconUrl: string }) => {
  console.log('Kingkong clicked:', item)
  // 可以添加跳转逻辑
}

// 头部区域组件（搜索栏、Tab导航）
const headerSections = computed(() => {
  const sections = homeStore.sections
  const headerTypes = ['search_bar', 'tab_navigation']
  return sections.filter(s => headerTypes.includes(s.sectionType))
})

// 内容区域组件（根据Tab过滤）
const contentSections = computed(() => {
  return homeStore.getSectionsByTab(activeTab.value)
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

const goToSearch = () => router.push(ROUTES.SEARCH)
const goToCart = () => router.push(ROUTES.CART)
const handleScanCode = () => ElMessage.info('扫码功能开发中')
const handleVoiceSearch = () => {
  // 检查浏览器是否支持语音识别
  const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition
  if (!SpeechRecognition) {
    ElMessage.warning('您的浏览器不支持语音搜索，请使用文字搜索')
    return
  }
  
  const recognition = new SpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.continuous = false
  recognition.interimResults = false
  
  recognition.onstart = () => {
    ElMessage.info('请说出您要搜索的药品、症状或品牌')
  }
  
  recognition.onresult = (event: any) => {
    const transcript = event.results[0][0].transcript
    router.push({
      path: ROUTES.SEARCH,
      query: { keyword: transcript }
    })
  }
  
  recognition.onerror = () => {
    ElMessage.error('语音识别失败，请重试')
  }
  
  recognition.start()
}

function selectLocation(loc: string) {
  currentLocation.value = loc
  showLocationPicker.value = false
  ElMessage.success(`已切换到${loc}`)
}

// 吸顶状态
const isSticky = ref(false)

const handleScroll = () => {
  // 使用 window 的滚动位置
  const scrollTop = window.scrollY || window.pageYOffset || document.documentElement.scrollTop
  // 滚动超过80px时触发吸顶
  isSticky.value = scrollTop > 80
}

function handleQuickConsult() {
  router.push(ROUTES.INQUIRY_AI_TRIAGE)
}

const handleSectionClick = (sectionType: string) => {
  if (sectionType === 'doctor_banner') {
    router.push(ROUTES.INQUIRY_PRE)
  }
}

const handlePromoLeftClick = () => {
  ElMessage.info('过敏报告功能开发中')
}
const handlePromoCenterClick = () => {
  router.push(ROUTES.PROMOTION_SLIMMING)
}
const handlePromoRightClick = () => {
  router.push(ROUTES.CATEGORY_ALLERGY)
}

// 下拉刷新状态
const isRefreshing = ref(false)
const refreshProgress = ref(0)
let refreshStartY = 0
let isPulling = false

const handleTouchStart = (e: TouchEvent) => {
  if (window.scrollY === 0) {
    refreshStartY = e.touches[0].clientY
    isPulling = true
  }
}

const handleTouchMove = (e: TouchEvent) => {
  if (!isPulling || isRefreshing.value) return
  const diff = e.touches[0].clientY - refreshStartY
  if (diff > 0 && diff < 150) {
    refreshProgress.value = diff / 150
  }
}

const handleTouchEnd = () => {
  if (!isPulling) return
  isPulling = false
  if (refreshProgress.value >= 1) {
    isRefreshing.value = true
    homeStore.fetchHomePageConfig().finally(() => {
      isRefreshing.value = false
      refreshProgress.value = 0
    })
  } else {
    refreshProgress.value = 0
  }
}

onMounted(async () => {
  await homeStore.fetchHomePageConfig()
  window.addEventListener('scroll', handleScroll, { passive: true })
  // 初始检查一次
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss">
@use '@/styles/variables' as *;

$primary-cyan: #0891B2;
$primary-light: #22D3EE;
$bg-gray: #ECFEFF;
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
    
    // 吸顶时的占位元素 - 保持文档流稳定
    .sticky-spacer {
      height: 180px;  // 足够容纳所有tab的第一个区域
      width: 100%;
    }

    // 吸顶状态 - 使用原有元素
    &.is-sticky {
      .header-gradient {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
        background: #fff !important;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
        padding: 8px 0 12px;
        transition: all 0.3s ease;

        // 隐藏所有第一个区域组件 - 使用display none，由sticky-spacer保持空间
        :deep(.promo-banner-section),
        :deep(.quick-consult-card),
        :deep(.test-banner-section),
        :deep(.tcm-banner-section),
        :deep(.chronic-banner-section) {
          display: none;
        }

        // 调整搜索栏样式 - 搜索框和购物车并排
        :deep(.search-section) {
          padding: 4px 12px;
        }

        // 隐藏顶部导航栏（返回按钮、标题、地址、购物车）
        :deep(.search-section .top-nav) {
          display: none !important;
        }

        // 搜索框和购物车并排
        :deep(.search-section .search-box-wrapper) {
          display: flex;
          align-items: center;
          gap: 12px;
        }

        :deep(.search-section .search-box) {
          flex: 1;
          background: #f5f5f5;
          border: 1px solid #e8e8e8;
          box-shadow: none;
          border-radius: 16px;
          padding: 8px 12px;
        }

        :deep(.search-section .search-box .search-icon) {
          color: #999;
        }

        :deep(.search-section .search-box .placeholder) {
          color: #999;
        }

        // 隐藏相机和搜索按钮
        :deep(.search-section .search-box .camera-btn),
        :deep(.search-section .search-box .search-btn) {
          display: none !important;
        }

        // 显示吸顶购物车按钮
        :deep(.search-section .sticky-cart-btn) {
          display: flex !important;
        }

        // 调整Tab导航样式
        :deep(.tab-navigation-section) {
          border-bottom: 1px solid #f0f0f0;
          padding: 0 12px;

          .category-tabs {
            padding: 4px 2px;
          }

          .tab-item {
            background: transparent;
            backdrop-filter: none;
            color: #666;
            padding: 6px 12px;
            font-size: 14px;

            &.active {
              background: transparent;
              color: var(--tab-active-color);
              box-shadow: none;
              font-weight: 600;

              // 吸顶后改为下划线指示器
              &::after {
                content: '';
                position: absolute;
                bottom: -4px;
                left: 50%;
                transform: translateX(-50%);
                width: 20px;
                height: 3px;
                background: var(--tab-active-color);
                border-radius: 2px;
                border: none;
              }
            }
          }
        }
      }

      .header-fade {
        display: none;
      }
    }

    // 头部渐变区域 - 搜索栏和Tab导航
    .header-gradient {
      padding: 8px 0 12px;
      transition: all 0.3s ease;
      --tab-active-color: #0891B2;
      // 默认推荐Tab - 青蓝科技感渐变
      background: linear-gradient(180deg, #0891B2 0%, #22D3EE 100%);
    }

    // 渐变过渡区域 - 平滑过渡到内容区
    .header-fade {
      height: 12px;
      background: linear-gradient(180deg,
        #22D3EE 0%,
        rgba(34, 211, 238, 0.3) 50%,
        transparent 100%
      );
      pointer-events: none;
      transition: all 0.3s ease;
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
        rgba(0, 229, 191, 0.3) 50%,
        transparent 100%
      );
    }
  }

  // 滋补保健Tab - 独立头部设计
  &.theme-tcm {
    background: #F5F5F5;

    .header-wrapper {
      background: url('/images/tcm-header-bg.svg') no-repeat center top;
      background-size: cover;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .header-gradient {
        background: transparent;
        padding: 8px 0 0;
        --tab-active-color: #D4A574;
      }

      .header-fade {
        display: none;
      }
    }
  }

  // 慢病关怀Tab - 独立头部设计
  &.theme-chronic {
    background: #F5F5F5;

    .header-wrapper {
      background: url('/images/chronic-header-bg.svg') no-repeat center top;
      background-size: cover;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .header-gradient {
        background: transparent;
        padding: 8px 0 0;
        --tab-active-color: #00A896;
      }

      .header-fade {
        display: none;
      }
    }
  }

  // 做检测Tab - 独立头部设计
  &.theme-test {
    background: #F5F5F5;

    .header-wrapper {
      background: #FFD93D;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .header-gradient {
        background: transparent;
        padding: 8px 0 0;
        --tab-active-color: #FFD93D;
      }

      .header-fade {
        display: none;
      }
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

<!-- 全局样式 - 用于覆盖 scoped 组件样式 -->
<style lang="scss">
// 吸顶时隐藏顶部导航栏
.home-page .header-wrapper.is-sticky .header-gradient .search-section .top-nav {
  display: none !important;
}

// 吸顶时搜索框和购物车并排
.home-page .header-wrapper.is-sticky .header-gradient .search-section .search-box-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.home-page .header-wrapper.is-sticky .header-gradient .search-section .search-box-wrapper .search-box {
  flex: 1;
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
  box-shadow: none;
  border-radius: 16px;
  padding: 8px 12px;
}

// 隐藏相机和搜索按钮
.home-page .header-wrapper.is-sticky .header-gradient .search-section .search-box .camera-btn,
.home-page .header-wrapper.is-sticky .header-gradient .search-section .search-box .search-btn {
  display: none !important;
}

// 显示吸顶购物车按钮
.home-page .header-wrapper.is-sticky .header-gradient .search-section .sticky-cart-btn {
  display: flex !important;
}

// 吸顶时隐藏促销横幅
.home-page .header-wrapper.is-sticky .header-gradient .promo-banner-section,
.home-page .header-wrapper.is-sticky .header-gradient .quick-consult-card,
.home-page .header-wrapper.is-sticky .header-gradient .test-banner-section,
.home-page .header-wrapper.is-sticky .header-gradient .tcm-banner-section,
.home-page .header-wrapper.is-sticky .header-gradient .chronic-banner-section {
  display: none !important;
}

// 吸顶金刚位样式
.home-page .header-wrapper.is-sticky .header-gradient .sticky-kingkong {
  padding: 8px 12px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;

  .kingkong-scroll {
    display: flex;
    gap: 16px;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    -webkit-overflow-scrolling: touch;
    padding: 4px 0;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .kingkong-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
    cursor: pointer;
    transition: transform 0.2s ease;

    &:active {
      transform: scale(0.95);
    }

    .kingkong-img {
      width: 56px;
      height: 56px;
      object-fit: contain;
      border-radius: 12px;
    }

    .kingkong-name {
      font-size: 12px;
      color: #333;
      white-space: nowrap;
    }
  }
}

// 下拉刷新指示器
.pull-refresh-indicator {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  z-index: 100;
  transition: transform 0.2s ease;

  .refresh-spinner {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $primary;
    font-size: 20px;

    &.spinning {
      animation: spin 1s linear infinite;
    }
  }

  .refresh-text {
    font-size: 12px;
    color: $text-tertiary;
    margin-top: 4px;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
