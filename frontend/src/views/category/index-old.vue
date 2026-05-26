<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Search, ArrowRight, ArrowDown, FirstAidKit, Timer } from '@element-plus/icons-vue'
import { getDrugCategories, getDrugList } from '@/api/modules/drug'
import { ROUTES } from '@/constants/routes'
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

// 一级分类列表（左侧边栏）
const mainCategories = computed(() => {
  // 筛选出顶级分类（parentId为null的）
  return categories.value.filter(c => !c.parentId || c.parentId === null)
})

// 当前一级分类的二级分类列表（顶部标签栏）
const subCategories = computed(() => {
  if (!activeCategoryId.value) return [{ id: 'all', name: '全部' }]
  
  // 筛选出当前一级分类下的所有子分类
  const subs = categories.value.filter(c => c.parentId === activeCategoryId.value)
  
  // 添加"全部"选项
  return [{ id: 'all', name: '全部' }, ...subs]
})

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getDrugCategories()
    console.log('分类API响应:', res)
    
    // 处理不同的响应格式
    if (Array.isArray(res)) {
      categories.value = res
    } else if (res && Array.isArray(res.list)) {
      categories.value = res.list
    } else if (res && res.data && Array.isArray(res.data)) {
      categories.value = res.data
    } else {
      categories.value = []
    }

    console.log('分类数据:', categories.value)

    // 默认选中第一个分类
    if (!activeCategoryId.value && mainCategories.value.length > 0) {
      activeCategoryId.value = mainCategories.value[0].id
    }
  } catch (error: any) {
    // 如果是请求被取消，静默处理
    if (error.message === '请求已取消' || error.name === 'CanceledError' || error.code === 'ERR_CANCELED') {
      console.log('分类列表请求已取消（正常现象）')
      return
    }
    
    console.error('获取分类列表失败:', error)
    categories.value = []
    ElMessage.error('获取分类列表失败，请稍后重试')
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

    // 优先使用二级分类ID，否则使用一级分类ID
    if (activeSubCategoryId.value) {
      params.categoryId = activeSubCategoryId.value
    } else if (activeCategoryId.value) {
      params.categoryId = activeCategoryId.value
    }

    console.log('请求药品列表参数:', params)
    const res = await getDrugList(params)
    console.log('药品API响应:', res)
    
    // 处理不同的响应格式
    if (res && Array.isArray(res)) {
      drugList.value = res
    } else if (res && res.list && Array.isArray(res.list)) {
      drugList.value = res.list
    } else if (res && res.data && Array.isArray(res.data)) {
      drugList.value = res.data
    } else {
      drugList.value = []
    }
    
    console.log('药品列表数据:', drugList.value.length, '条')
  } catch (error: any) {
    // 如果是请求被取消（快速切换分类时），静默处理，不显示错误
    if (error.message === '请求已取消' || error.name === 'CanceledError' || error.code === 'ERR_CANCELED') {
      console.log('药品列表请求已取消（正常现象）')
      return
    }
    
    console.error('获取药品列表失败:', error)
    drugList.value = []
    ElMessage.warning('获取药品列表失败，请稍后重试')
  } finally {
    drugsLoading.value = false
  }
}

// 切换一级分类
const selectCategory = (id: string) => {
  activeCategoryId.value = id
  activeSubCategoryId.value = '' // 重置二级分类
}

// 切换二级分类
const selectSubCategory = (id: string) => {
  activeSubCategoryId.value = id === 'all' ? '' : id
}

// 查看药品详情
const goToDrugDetail = (id: string) => {
  router.push(`/drug/${id}`)
}

// 返回上一页
const goBack = () => {
  router.back()
}

const goSearch = () => {
  router.push(ROUTES.SEARCH)
}

const goConsult = () => {
  router.push(ROUTES.INQUIRY)
}

// 监听分类变化
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
  if (activeCategoryId.value) {
    await fetchDrugs()
  }
})
</script>

<template>
  <div class="category-page">
    <!-- 头部搜索栏 -->
    <div class="header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="search-box" @click="goSearch">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="search-placeholder">搜索药品、疾病、症状、品牌</span>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧分类列表 -->
      <div class="symptom-sidebar">
        <div class="symptom-list">
          <div
            v-for="item in mainCategories"
            :key="item.id"
            class="symptom-item"
            :class="{ active: activeCategoryId === item.id }"
            @click="selectCategory(item.id)"
          >
            {{ item.name }}
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 二级分类标签 -->
        <div class="sub-category-bar-wrapper">
          <div class="sub-category-bar">
            <div
              v-for="item in subCategories"
              :key="item.id"
              class="sub-category-item"
              :class="{ active: activeSubCategoryId === item.id }"
              @click="selectSubCategory(item.id)"
            >
              {{ item.name }}
            </div>
          </div>
        </div>

        <!-- 药品列表 -->
        <div class="drug-list">
          <div
            v-for="drug in drugList"
            :key="drug.id"
            class="drug-item"
            @click="goToDrugDetail(drug.id)"
          >
            <div class="drug-image">
              <img :src="drug.image || 'https://via.placeholder.com/120x120/f5f5f5/666?text=药品'" :alt="drug.name" />
              <div v-if="drug.isRx" class="rx-badge">处方药</div>
              <div class="sales-badge">月售{{ drug.sales || 0 }}</div>
            </div>
            <div class="drug-info">
              <div class="drug-name">{{ drug.name }}</div>
              <div class="drug-spec">{{ drug.specification || '' }}</div>
              <div class="drug-bottom">
                <div class="price-section">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ drug.price }}</span>
                  <span v-if="drug.originalPrice && drug.originalPrice > drug.price" class="original-price">¥{{ drug.originalPrice }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="!drugsLoading && drugList.length === 0" class="empty-state">
            <el-icon class="empty-icon"><FirstAidKit /></el-icon>
            <p class="empty-text">暂无药品数据</p>
          </div>
          
          <!-- 加载状态 -->
          <div v-if="drugsLoading" class="loading-state">
            <el-skeleton :rows="3" animated />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.category-page {
  min-height: 100vh;
  background: $bg-primary;
}

// 头部
.header {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-sm $spacing-md;
  padding-top: calc($safe-area-top + $spacing-sm);
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-white;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
    }
  }

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    background: rgba(255, 255, 255, 0.95);
    border-radius: $radius-full;
    padding: $spacing-sm $spacing-md;
    cursor: pointer;
    box-shadow: $shadow-sm;

    .search-icon {
      color: $text-tertiary;
      font-size: $font-md;
    }

    .search-placeholder {
      font-size: $font-md;
      color: $text-tertiary;
    }
  }
}

// 主内容区
.main-content {
  display: flex;
  height: calc(100vh - 56px);
  background: $bg-primary;
  padding-top: 56px;
}

// 左侧症状分类
.symptom-sidebar {
  width: 85px;
  background: $bg-white;
  flex-shrink: 0;
  overflow-y: auto;
  height: calc(100vh - 56px);
  position: fixed;
  left: 0;
  top: 56px;
  border-right: 1px solid $border-light;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }

  .symptom-list {
    padding: $spacing-sm 0;

    .symptom-item {
      padding: 14px $spacing-sm;
      text-align: center;
      font-size: $font-sm;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;
      border-left: 3px solid transparent;
      margin: $spacing-xs 0;

      &:hover {
        color: $text-primary;
        background: rgba($primary, 0.05);
      }

      &.active {
        background: rgba($primary, 0.1);
        color: $primary;
        border-left-color: $primary;
        font-weight: 600;
      }
    }
  }
}

// 右侧内容区
.content-area {
  flex: 1;
  background: $bg-primary;
  height: calc(100vh - 56px);
  margin-left: 85px;
  overflow-y: auto;
  position: relative;
  padding-top: 56px;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }
}

// 二级分类标签容器
.sub-category-bar-wrapper {
  position: fixed;
  top: 56px;
  left: 85px;
  right: 0;
  background: $bg-primary;
  z-index: 10;
  padding: $spacing-md;
}

// 二级分类标签
.sub-category-bar {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-sm;
  overflow-x: auto;
  box-shadow: $shadow-sm;

  &::-webkit-scrollbar {
    height: 0;
    display: none;
  }

  .sub-category-item {
    flex-shrink: 0;
    padding: $spacing-sm $spacing-lg;
    font-size: $font-sm;
    color: $text-secondary;
    background: $bg-gray;
    border-radius: $radius-full;
    cursor: pointer;
    transition: all 0.25s;
    font-weight: 500;

    &:hover {
      background: rgba($primary, 0.1);
      color: $primary;
    }

    &.active {
      background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
      color: $text-white;
      box-shadow: 0 4px 12px rgba($primary, 0.3);
    }
  }

  .more-btn {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-tertiary;
    cursor: pointer;
    background: $bg-gray;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: rgba($primary, 0.1);
      color: $primary;
    }
  }
}

// 药品列表
.drug-list {
  padding: $spacing-md;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px $spacing-md;
  text-align: center;

  .empty-icon {
    font-size: 64px;
    color: $text-tertiary;
    margin-bottom: $spacing-md;
  }

  .empty-text {
    font-size: $font-md;
    color: $text-tertiary;
  }
}

// 加载状态
.loading-state {
  padding: $spacing-md;
}

.drug-item {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
  background: $bg-white;
  border-radius: $radius-lg;
  cursor: pointer;
  box-shadow: $shadow-sm;
  transition: all 0.25s;

  &:hover {
    box-shadow: $shadow-md;
    transform: translateY(-2px);
  }

  &:last-child {
    margin-bottom: 0;
  }

  .drug-image {
    position: relative;
    width: 100px;
    height: 100px;
    flex-shrink: 0;
    border-radius: $radius-md;
    overflow: hidden;
    background: $bg-gray;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .rx-badge {
      position: absolute;
      top: 6px;
      left: 6px;
      background: linear-gradient(135deg, $error 0%, lighten($error, 10%) 100%);
      color: $text-white;
      font-size: $font-xs;
      padding: 3px 8px;
      border-radius: $radius-sm;
      font-weight: 600;
    }

    .sales-badge {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
      color: $text-white;
      font-size: $font-xs;
      padding: 8px 6px 6px;
      text-align: center;
      font-weight: 500;
    }
  }

  .drug-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 100px;

    .drug-name {
      font-size: $font-md;
      color: $text-primary;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      font-weight: 600;
    }

    .drug-spec {
      font-size: $font-sm;
      color: $text-secondary;
      margin-top: $spacing-xs;
    }

    .drug-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-xs;
      margin-top: $spacing-xs;

      .tag {
        font-size: $font-xs;
        color: $text-tertiary;
        padding: 4px 10px;
        background: $bg-gray;
        border-radius: $radius-sm;
        font-weight: 500;

        &.tag-primary {
          background: linear-gradient(135deg, lighten($error, 45%) 0%, lighten($error, 40%) 100%);
          color: $error;
        }

        &.tag-blue {
          background: linear-gradient(135deg, lighten($info, 45%) 0%, lighten($info, 40%) 100%);
          color: $info;
        }
      }
    }

    .drug-bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: $spacing-sm;

      .price-section {
        display: flex;
        align-items: baseline;
        gap: $spacing-xs;

        .price-symbol {
          font-size: $font-sm;
          font-weight: 600;
          color: $error;
        }

        .price-value {
          font-size: $font-xl;
          font-weight: 700;
          color: $error;
        }

        .original-price {
          font-size: $font-xs;
          color: $text-tertiary;
          text-decoration: line-through;
        }
      }
    }
  }
}
</style>
