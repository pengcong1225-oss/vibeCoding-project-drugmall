<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Search } from '@element-plus/icons-vue'
import DrugCard from '@/components/DrugCard/index.vue'
import { getDrugCategories, getDrugList } from '@/api/modules/drug'
import type { DrugCategory, Drug } from '@/types'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(false)
const drugsLoading = ref(false)

// 分类数据
const categories = ref<DrugCategory[]>([])

// 药品列表
const drugList = ref<Drug[]>([])

// 当前选中的一级分类
const activeCategoryId = ref('')

// 当前选中的二级分类
const activeSubCategoryId = ref('')

// 当前分类
const currentCategory = computed(() => {
  return categories.value.find(c => c.id === activeCategoryId.value)
})

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getDrugCategories()
    // 兼容不同的返回格式
    if (Array.isArray(res)) {
      categories.value = res
    } else if (res && Array.isArray(res.list)) {
      categories.value = res.list
    } else if (res && res.data) {
      categories.value = Array.isArray(res.data) ? res.data : []
    }

    // 如果没有选中分类，默认选中第一个
    if (!activeCategoryId.value && categories.value.length > 0) {
      activeCategoryId.value = categories.value[0].id
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
    // 使用模拟数据作为fallback
    categories.value = [
      { id: '1', name: '感冒用药', children: [{ id: '11', name: '退烧药' }, { id: '12', name: '止咳药' }] },
      { id: '2', name: '消化系统', children: [{ id: '21', name: '胃药' }, { id: '22', name: '肠道用药' }] },
      { id: '3', name: '心脑血管', children: [] },
      { id: '4', name: '营养保健', children: [] },
      { id: '5', name: '医疗器械', children: [] }
    ]
    if (categories.value.length > 0 && !activeCategoryId.value) {
      activeCategoryId.value = categories.value[0].id
    }
  } finally {
    loading.value = false
  }
}

// 获取药品列表
const fetchDrugs = async () => {
  drugsLoading.value = true
  try {
    const params: any = {
      page: 1,
      size: 12
    }

    // 如果有选中的二级分类，使用二级分类ID
    if (activeSubCategoryId.value) {
      params.categoryId = activeSubCategoryId.value
    } else if (activeCategoryId.value) {
      // 否则使用一级分类ID
      params.categoryId = activeCategoryId.value
    }

    const res = await getDrugList(params)
    // 兼容不同的返回格式
    if (res && Array.isArray(res)) {
      drugList.value = res
    } else if (res && res.list && Array.isArray(res.list)) {
      drugList.value = res.list
    } else if (res && res.data && Array.isArray(res.data)) {
      drugList.value = res.data
    } else {
      drugList.value = []
    }
  } catch (error) {
    console.error('获取药品列表失败:', error)
    ElMessage.error('获取药品列表失败')
    drugList.value = []
  } finally {
    drugsLoading.value = false
  }
}

// 切换一级分类
const selectCategory = (id: string) => {
  activeCategoryId.value = id
  activeSubCategoryId.value = ''
}

// 切换二级分类
const selectSubCategory = (id: string) => {
  activeSubCategoryId.value = id
  const subName = currentCategory.value?.children?.find(c => c.id === id)?.name || ''
  ElMessage.success(`切换到分类: ${subName}`)
}

// 查看药品详情
const goToDrugDetail = (id: string) => {
  router.push(`/drug/${id}`)
}

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 监听分类变化，重新获取药品
watch([activeCategoryId, activeSubCategoryId], () => {
  fetchDrugs()
})

// 初始化
onMounted(async () => {
  const categoryId = route.query.id as string
  if (categoryId) {
    activeCategoryId.value = categoryId
  }
  await fetchCategories()
  // 如果有选中的分类，自动加载药品
  if (activeCategoryId.value) {
    await fetchDrugs()
  }
})
</script>

<template>
  <div class="category-page">
    <!-- 头部搜索栏 -->
    <div class="header">
      <div class="back-btn" @click="goHome">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="search-box" @click="router.push('/search')">
        <el-icon><Search /></el-icon>
        <span>搜索药品、症状</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 分类内容区 -->
    <div v-else class="category-content">
      <!-- 左侧一级分类 -->
      <div class="category-sidebar">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          :class="{ active: activeCategoryId === category.id }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="category-main">
        <!-- 二级分类 -->
        <div v-if="currentCategory?.children?.length" class="sub-categories">
          <div
            v-for="sub in currentCategory.children"
            :key="sub.id"
            class="sub-item"
            :class="{ active: activeSubCategoryId === sub.id }"
            @click="selectSubCategory(sub.id)"
          >
            {{ sub.name }}
          </div>
        </div>

        <!-- 药品列表 -->
        <div v-if="drugsLoading" class="drug-grid">
          <el-skeleton v-for="i in 6" :key="i" animated>
            <template #template>
              <div style="padding: 10px;">
                <el-skeleton-item variant="image" style="width: 100%; height: 120px; border-radius: 8px;" />
                <el-skeleton-item variant="text" style="margin-top: 10px;" />
                <el-skeleton-item variant="text" style="width: 60%; margin-top: 5px;" />
              </div>
            </template>
          </el-skeleton>
        </div>

        <div v-else-if="drugList.length === 0" class="empty-state">
          <el-empty description="暂无药品" :image-size="120">
            <el-button type="primary" size="small" @click="$router.push('/home')">去首页看看</el-button>
          </el-empty>
        </div>

        <div v-else class="drug-grid">
          <DrugCard
            v-for="drug in drugList"
            :key="drug.id"
            :drug="drug"
            layout="vertical"
            @addToCart="handleAddToCart"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
// 添加购物车处理函数
function handleAddToCart(drug: any) {
  ElMessage.success(`${drug.name}已加入购物车`)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.category-page {
  min-height: 100vh;
  background: $bg-white;
  display: flex;
  flex-direction: column;
}

// 头部
.header {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);

  .back-btn {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-white;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    background: rgba(255, 255, 255, 0.95);
    border-radius: $radius-xl;
    padding: $spacing-sm $spacing-md;
    color: $text-tertiary;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: $bg-white;
      box-shadow: $shadow-sm;
    }

    span {
      font-size: $font-md;
    }
  }
}

// 加载状态
.loading-container {
  flex: 1;
  padding: $spacing-md;
}

// 分类内容区
.category-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

// 左侧分类栏
.category-sidebar {
  width: 100px;
  background: $bg-gray;
  overflow-y: auto;
  flex-shrink: 0;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }

  .category-item {
    padding: $spacing-md $spacing-sm;
    text-align: center;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;

    &:hover {
      color: $primary;
      background: rgba($primary, 0.05);
    }

    &.active {
      background: $bg-white;
      color: $primary;
      font-weight: 600;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 20px;
        background: $primary;
        border-radius: 0 2px 2px 0;
      }
    }
  }
}

// 右侧主内容区
.category-main {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-md;
  background: $bg-white;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }
}

// 二级分类
.sub-categories {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $border-light;

  .sub-item {
    padding: $spacing-sm $spacing-md;
    background: $bg-gray;
    border-radius: $radius-full;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 1px solid transparent;

    &:hover {
      background: rgba($primary, 0.08);
      color: $primary;
      border-color: rgba($primary, 0.2);
    }

    &.active {
      background: $primary;
      color: $text-white;
      border-color: $primary;
      box-shadow: 0 2px 8px rgba($primary, 0.3);
    }
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

// 商品网格
.drug-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;
}
</style>
