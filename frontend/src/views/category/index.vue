<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Search, FirstAidKit } from '@element-plus/icons-vue'
import { getDrugCategories, getDrugList } from '@/api/modules/drug'
import { ROUTES, getDrugDetailRoute } from '@/constants/routes'
import type { DrugCategory, Drug } from '@/types'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(false)
const drugsLoading = ref(false)

// 所有分类数据（扁平列表）
const allCategories = ref<DrugCategory[]>([])

// 药品列表
const drugList = ref<Drug[]>([])

// 当前选中的一级分类ID（从URL参数获取）
const activeLevel1Id = ref('')

// 当前选中的二级分类ID（左侧边栏）
const activeLevel2Id = ref('')

// 当前选中的三级分类ID（顶部标签栏）
const activeLevel3Id = ref('')

// 一级分类（金刚位入口）- 从URL参数确定
const level1Category = computed(() => {
  return allCategories.value.find(c => c.id === activeLevel1Id.value)
})

// 二级分类列表（左侧边栏）- 一级分类的子分类
const level2Categories = computed(() => {
  if (!activeLevel1Id.value) return []
  return allCategories.value.filter(c => c.parentId === activeLevel1Id.value)
})

// 三级分类列表（顶部标签栏）- 二级分类的子分类
const level3Categories = computed(() => {
  if (!activeLevel2Id.value) return []
  
  const subs = allCategories.value.filter(c => c.parentId === activeLevel2Id.value)
  
  // 添加"全部"选项
  return [{ id: 'all', name: '全部', parentId: activeLevel2Id.value }, ...subs]
})

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getDrugCategories()
    console.log('分类API响应:', res)
    
    // 处理不同的响应格式
    if (Array.isArray(res)) {
      allCategories.value = res
    } else if (res && Array.isArray(res.list)) {
      allCategories.value = res.list
    } else if (res && res.data && Array.isArray(res.data)) {
      allCategories.value = res.data
    } else {
      allCategories.value = []
    }

    console.log('分类数据总数:', allCategories.value.length)

    // 从URL参数获取一级分类ID（支持id和active两种参数名）
    const categoryId = (route.query.id || route.query.active) as string
    if (categoryId) {
      activeLevel1Id.value = categoryId
      
      // 自动选中第一个二级分类
      if (level2Categories.value.length > 0) {
        activeLevel2Id.value = level2Categories.value[0].id
        
        // 自动选中第一个三级分类
        const level3Subs = allCategories.value.filter(c => c.parentId === activeLevel2Id.value)
        if (level3Subs.length > 0) {
          activeLevel3Id.value = level3Subs[0].id
        }
      }
    }
  } catch (error: any) {
    // 如果是请求被取消，静默处理
    if (error.message === '请求已取消' || error.name === 'CanceledError' || error.code === 'ERR_CANCELED') {
      console.log('分类列表请求已取消（正常现象）')
      return
    }
    
    console.error('获取分类列表失败:', error)
    allCategories.value = []
    ElMessage.error('获取分类列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取药品列表
const fetchDrugs = async () => {
  if (!activeLevel3Id.value && !activeLevel2Id.value) {
    drugList.value = []
    return
  }
  
  drugsLoading.value = true
  try {
    const params: any = {
      page: 1,
      size: 20
    }

    // 优先使用三级分类ID，否则使用二级分类ID
    if (activeLevel3Id.value && activeLevel3Id.value !== 'all') {
      params.categoryId = activeLevel3Id.value
    } else if (activeLevel2Id.value) {
      params.categoryId = activeLevel2Id.value
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

// 切换二级分类（左侧边栏）
const selectLevel2 = (id: string) => {
  activeLevel2Id.value = id
  
  // 自动选中该二级分类下的第一个三级分类
  const level3Subs = allCategories.value.filter(c => c.parentId === id)
  if (level3Subs.length > 0) {
    activeLevel3Id.value = level3Subs[0].id
  } else {
    activeLevel3Id.value = ''
  }
}

// 切换三级分类（顶部标签栏）
const selectLevel3 = (id: string) => {
  activeLevel3Id.value = id === 'all' ? '' : id
}

// 查看药品详情
const goToDrugDetail = (id: string) => {
  router.push(getDrugDetailRoute(id))
}

const goBack = () => {
  router.back()
}

const goSearch = () => {
  router.push(ROUTES.SEARCH)
}

// 监听二、三级分类变化，自动加载药品
watch([activeLevel2Id, activeLevel3Id], () => {
  fetchDrugs()
})

// 初始化
onMounted(async () => {
  await fetchCategories()
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
      <!-- 左侧二级分类列表 -->
      <div class="level2-sidebar">
        <div class="level2-list">
          <div
            v-for="item in level2Categories"
            :key="item.id"
            class="level2-item"
            :class="{ active: activeLevel2Id === item.id }"
            @click="selectLevel2(item.id)"
          >
            {{ item.name }}
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 三级分类标签 -->
        <div class="level3-bar-wrapper" v-if="level3Categories.length > 0">
          <div class="level3-bar">
            <div
              v-for="item in level3Categories"
              :key="item.id"
              class="level3-item"
              :class="{ active: activeLevel3Id === item.id }"
              @click="selectLevel3(item.id)"
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
@import '@/styles/variables.scss';

.category-page {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}

// 头部搜索栏
.header {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md;
  background: $bg-white;
  box-shadow: $shadow-sm;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: $bg-gray;
    }

    .el-icon {
      font-size: 20px;
      color: $text-primary;
    }
  }

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    padding: $spacing-sm $spacing-md;
    background: $bg-gray;
    border-radius: $radius-full;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: darken($bg-gray, 5%);
    }

    .search-icon {
      font-size: 18px;
      color: $text-tertiary;
    }

    .search-placeholder {
      flex: 1;
      font-size: $font-sm;
      color: $text-tertiary;
    }
  }
}

// 主内容区
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

// 左侧二级分类边栏
.level2-sidebar {
  width: 90px;
  background: $bg-white;
  border-right: 1px solid $border-color;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 0;
    display: none;
  }

  .level2-list {
    padding: $spacing-sm 0;

    .level2-item {
      padding: $spacing-md $spacing-sm;
      text-align: center;
      font-size: $font-sm;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;
      border-left: 3px solid transparent;

      &:hover {
        background: rgba($primary, 0.05);
        color: $primary;
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
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: $bg-primary;
}

// 三级分类标签栏
.level3-bar-wrapper {
  background: $bg-white;
  padding: $spacing-md;
  border-bottom: 1px solid $border-color;
  position: sticky;
  top: 0;
  z-index: 10;

  .level3-bar {
    display: flex;
    gap: $spacing-sm;
    overflow-x: auto;

    &::-webkit-scrollbar {
      height: 0;
      display: none;
    }

    .level3-item {
      flex-shrink: 0;
      padding: $spacing-sm $spacing-lg;
      font-size: $font-sm;
      color: $text-secondary;
      background: $bg-gray;
      border-radius: $radius-full;
      cursor: pointer;
      transition: all 0.2s;
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
  }
}

// 药品列表
.drug-list {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-md;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: $border-color;
    border-radius: 3px;
  }
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

// 药品卡片
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
