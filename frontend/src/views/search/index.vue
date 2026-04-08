<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useDebounceFn } from '@vueuse/core'
import DrugCard from '@/components/DrugCard/index.vue'
import Empty from '@/components/Empty/index.vue'
import Loading from '@/components/Loading/index.vue'
import { searchDrugs, getHotSearches } from '@/api/modules/search'
import { getSearchHistory, addSearchHistory, clearSearchHistory } from '@/utils/storage'
import type { Drug, DrugSearchParams } from '@/types'

const router = useRouter()
const route = useRoute()

// 搜索关键词
const keyword = ref('')
const searchInputRef = ref<HTMLInputElement | null>(null)

// 搜索状态
const isSearching = ref(false)
const hasSearched = ref(false)

// 搜索结果
const searchResults = ref<Drug[]>([])
const totalResults = ref(0)

// 搜索历史和热门搜索
const searchHistory = ref<string[]>([])
const hotSearches = ref<{ keyword: string; heat: number }[]>([{
  keyword: '感冒发烧',
  heat: 100
}, {
  keyword: '维生素C',
  heat: 90
}, {
  keyword: '口罩',
  heat: 80
}, {
  keyword: '降压药',
  heat: 70
}, {
  keyword: '降糖药',
  heat: 60
}])

// 排序选项
const sortOptions = [
  { label: '综合', value: 'default' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' },
  { label: '销量优先', value: 'sales' },
  { label: '最新上架', value: 'new' }
]
const currentSort = ref('default')

// 筛选条件
const filters = ref({
  isRx: null as boolean | null,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
})

// 价格区间选项
const priceRanges = [
  { label: '全部', min: undefined, max: undefined },
  { label: '0-50元', min: 0, max: 50 },
  { label: '50-100元', min: 50, max: 100 },
  { label: '100-200元', min: 100, max: 200 },
  { label: '200元以上', min: 200, max: undefined }
]
const selectedPriceRange = ref(0)

// 页面初始化
onMounted(() => {
  // 获取URL参数中的关键词
  const queryKeyword = route.query.keyword as string
  if (queryKeyword) {
    keyword.value = queryKeyword
    handleSearch()
  }

  // 加载搜索历史
  loadSearchHistory()

  // 加载热门搜索
  loadHotSearches()

  // 聚焦搜索框
  setTimeout(() => {
    searchInputRef.value?.focus()
  }, 100)
})

// 加载搜索历史
const loadSearchHistory = () => {
  searchHistory.value = getSearchHistory()
}

// 加载热门搜索
const loadHotSearches = async () => {
  try {
    const res = await getHotSearches(10)
    if (res && Array.isArray(res)) {
      hotSearches.value = res
    }
  } catch (error) {
    // 使用默认数据
  }
}

// 搜索防抖
const debouncedSearch = useDebounceFn(() => {
  handleSearch()
}, 500)

// 执行搜索
const handleSearch = async () => {
  if (!keyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  isSearching.value = true
  hasSearched.value = true

  // 保存搜索历史
  addSearchHistory(keyword.value.trim())
  loadSearchHistory()

  try {
    const params: DrugSearchParams = {
      keyword: keyword.value.trim(),
      sort: currentSort.value as any,
      page: 1,
      size: 20,
      isRx: filters.value.isRx ?? undefined,
      minPrice: filters.value.minPrice,
      maxPrice: filters.value.maxPrice
    }

    const res = await searchDrugs(params)
    if (res && res.list) {
      searchResults.value = res.list
      totalResults.value = res.total
    }
  } catch (error) {
    ElMessage.error('搜索失败，请稍后重试')
    searchResults.value = []
  } finally {
    isSearching.value = false
  }
}

// 清空搜索
const clearSearch = () => {
  keyword.value = ''
  hasSearched.value = false
  searchResults.value = []
  searchInputRef.value?.focus()
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 点击历史或热门搜索
const handleQuickSearch = (word: string) => {
  keyword.value = word
  handleSearch()
}

// 删除单条搜索历史
const deleteHistory = (word: string, event: Event) => {
  event.stopPropagation()
  // 这里需要实现删除单个历史记录的方法
  loadSearchHistory()
}

// 清空所有搜索历史
const clearAllHistory = () => {
  clearSearchHistory()
  searchHistory.value = []
  ElMessage.success('已清空搜索历史')
}

// 处理排序变化
const handleSortChange = (sort: string) => {
  currentSort.value = sort
  if (hasSearched.value) {
    handleSearch()
  }
}

// 处理价格区间选择
const handlePriceRangeChange = (index: number) => {
  selectedPriceRange.value = index
  const range = priceRanges[index]
  filters.value.minPrice = range.min
  filters.value.maxPrice = range.max
  if (hasSearched.value) {
    handleSearch()
  }
}

// 处理处方药筛选
const handleRxFilter = (isRx: boolean | null) => {
  filters.value.isRx = isRx
  if (hasSearched.value) {
    handleSearch()
  }
}

// 添加到购物车
const handleAddToCart = (drug: any) => {
  ElMessage.success(`已将 ${drug.name} 加入购物车`)
}

// 扫码
const handleScan = () => {
  ElMessage.info('扫码功能开发中')
}
</script>

<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="search-input-wrapper">
        <el-icon class="search-icon"><Search /></el-icon>
        <input
          ref="searchInputRef"
          v-model="keyword"
          type="text"
          class="search-input"
          placeholder="搜索药品、症状"
          @keyup.enter="handleSearch"
        />
        <el-icon v-if="keyword" class="clear-icon" @click="clearSearch"><CircleClose /></el-icon>
        <div class="scan-btn" @click="handleScan">
          <el-icon><FullScreen /></el-icon>
        </div>
      </div>
      <button class="search-btn" @click="handleSearch">搜索</button>
    </div>

    <!-- 未搜索状态 - 显示历史和热门 -->
    <div v-if="!hasSearched" class="search-suggestions">
      <!-- 搜索历史 -->
      <div v-if="searchHistory.length > 0" class="section">
        <div class="section-header">
          <h3>搜索历史</h3>
          <el-icon class="delete-icon" @click="clearAllHistory"><Delete /></el-icon>
        </div>
        <div class="tag-list">
          <span
            v-for="word in searchHistory"
            :key="word"
            class="tag"
            @click="handleQuickSearch(word)"
          >
            {{ word }}
          </span>
        </div>
      </div>

      <!-- 热门搜索 -->
      <div class="section">
        <div class="section-header">
          <h3>热门搜索</h3>
        </div>
        <div class="tag-list hot">
          <span
            v-for="(item, index) in hotSearches"
            :key="item.keyword"
            class="tag"
            :class="{ top: index < 3 }"
            @click="handleQuickSearch(item.keyword)"
          >
            <span class="rank">{{ index + 1 }}</span>
            {{ item.keyword }}
          </span>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div v-else class="search-results">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="sort-options">
          <span
            v-for="opt in sortOptions"
            :key="opt.value"
            class="sort-item"
            :class="{ active: currentSort === opt.value }"
            @click="handleSortChange(opt.value)"
          >
            {{ opt.label }}
          </span>
        </div>
        <div class="filter-options">
          <el-dropdown>
            <span class="filter-item">
              筛选 <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleRxFilter(null)">全部</el-dropdown-item>
                <el-dropdown-item @click="handleRxFilter(false)">非处方药</el-dropdown-item>
                <el-dropdown-item @click="handleRxFilter(true)">处方药</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 价格区间 -->
      <div class="price-filter">
        <span
          v-for="(range, index) in priceRanges"
          :key="range.label"
          class="price-tag"
          :class="{ active: selectedPriceRange === index }"
          @click="handlePriceRangeChange(index)"
        >
          {{ range.label }}
        </span>
      </div>

      <!-- 加载中 -->
      <div v-if="isSearching" class="loading-wrapper">
        <Loading />
      </div>

      <!-- 搜索结果列表 -->
      <div v-else-if="searchResults.length > 0" class="results-list">
        <div class="results-grid">
          <DrugCard
            v-for="drug in searchResults"
            :key="drug.id"
            :drug="drug"
            layout="vertical"
            @add-to-cart="handleAddToCart"
          />
        </div>
        <div class="results-count">共找到 {{ totalResults }} 件商品</div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-wrapper">
        <Empty
          description="未找到相关商品"
          :image-size="120"
        />
        <p class="empty-hint">换个关键词试试吧</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.search-page {
  min-height: 100vh;
  background: $bg-primary;
}

// 搜索头部
.search-header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-sm $spacing-md;
  padding-top: calc($safe-area-top + $spacing-sm);
  background: $bg-white;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-primary;
    font-size: 20px;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: $bg-gray;
    }
  }

  .search-input-wrapper {
    flex: 1;
    display: flex;
    align-items: center;
    background: $bg-gray;
    border-radius: $radius-xl;
    padding: $spacing-sm $spacing-md;
    gap: $spacing-sm;

    .search-icon {
      color: $text-tertiary;
      font-size: 18px;
    }

    .search-input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: $font-md;
      color: $text-primary;
      outline: none;

      &::placeholder {
        color: $text-tertiary;
      }
    }

    .clear-icon {
      color: $text-tertiary;
      font-size: 18px;
      cursor: pointer;

      &:hover {
        color: $text-secondary;
      }
    }

    .scan-btn {
      padding-left: $spacing-sm;
      border-left: 1px solid $border-light;
      color: $text-tertiary;
      cursor: pointer;

      &:hover {
        color: $primary;
      }
    }
  }

  .search-btn {
    padding: $spacing-sm $spacing-md;
    background: $primary;
    color: $text-white;
    border: none;
    border-radius: $radius-lg;
    font-size: $font-md;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: $primary-dark;
    }
  }
}

// 搜索建议区域
.search-suggestions {
  padding: $spacing-md;

  .section {
    margin-bottom: $spacing-lg;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-md;

      h3 {
        font-size: $font-md;
        font-weight: 600;
        color: $text-primary;
      }

      .delete-icon {
        color: $text-tertiary;
        font-size: 18px;
        cursor: pointer;

        &:hover {
          color: $error;
        }
      }
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-sm;

      .tag {
        padding: $spacing-sm $spacing-md;
        background: $bg-white;
        border: 1px solid $border-light;
        border-radius: $radius-xl;
        font-size: $font-sm;
        color: $text-secondary;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          border-color: $primary;
          color: $primary;
        }

        &.top {
          .rank {
            color: $error;
            font-weight: bold;
          }
        }

        .rank {
          margin-right: $spacing-xs;
          color: $text-tertiary;
        }
      }

      &.hot {
        .tag {
          background: rgba($primary, 0.05);
          border-color: transparent;

          &:hover {
            background: rgba($primary, 0.1);
          }
        }
      }
    }
  }
}

// 搜索结果区域
.search-results {
  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-sm $spacing-md;
    background: $bg-white;
    border-bottom: 1px solid $border-light;

    .sort-options {
      display: flex;
      gap: $spacing-md;

      .sort-item {
        font-size: $font-sm;
        color: $text-secondary;
        cursor: pointer;
        transition: color 0.2s;
        white-space: nowrap;

        &:hover,
        &.active {
          color: $primary;
        }
      }
    }

    .filter-options {
      .filter-item {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        font-size: $font-sm;
        color: $text-secondary;
        cursor: pointer;

        &:hover {
          color: $primary;
        }
      }
    }
  }

  .price-filter {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;
    padding: $spacing-md;
    background: $bg-white;
    border-bottom: 1px solid $border-light;

    .price-tag {
      padding: $spacing-xs $spacing-md;
      background: $bg-gray;
      border-radius: $radius-md;
      font-size: $font-sm;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      &:hover,
      &.active {
        background: $primary;
        color: $text-white;
      }
    }
  }

  .loading-wrapper {
    padding: $spacing-xl;
  }

  .results-list {
    padding: $spacing-md;

    .results-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: $spacing-md;
    }

    .results-count {
      text-align: center;
      padding: $spacing-lg;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }

  .empty-wrapper {
    padding: $spacing-xxl $spacing-md;

    .empty-hint {
      text-align: center;
      margin-top: $spacing-md;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
}
</style>
