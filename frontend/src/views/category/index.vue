<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import DrugCard from '@/components/DrugCard/index.vue'
import { mockCategories, mockDrugs } from '@/api/mock'
import type { DrugCategory } from '@/types'

const route = useRoute()
const router = useRouter()

// 分类数据
const categories = ref<DrugCategory[]>([
  {
    id: '1',
    name: '感冒发烧',
    level: 1,
    sort: 1,
    status: 1,
    children: [
      { id: '1-1', name: '感冒用药', level: 2, sort: 1, status: 1 },
      { id: '1-2', name: '退烧止痛', level: 2, sort: 2, status: 1 },
      { id: '1-3', name: '清热解毒', level: 2, sort: 3, status: 1 },
      { id: '1-4', name: '咳嗽用药', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '2',
    name: '呼吸系统',
    level: 1,
    sort: 2,
    status: 1,
    children: [
      { id: '2-1', name: '支气管炎', level: 2, sort: 1, status: 1 },
      { id: '2-2', name: '哮喘用药', level: 2, sort: 2, status: 1 },
      { id: '2-3', name: '润肺止咳', level: 2, sort: 3, status: 1 }
    ]
  },
  {
    id: '3',
    name: '消化系统',
    level: 1,
    sort: 3,
    status: 1,
    children: [
      { id: '3-1', name: '胃炎用药', level: 2, sort: 1, status: 1 },
      { id: '3-2', name: '腹泻用药', level: 2, sort: 2, status: 1 },
      { id: '3-3', name: '消化不良', level: 2, sort: 3, status: 1 },
      { id: '3-4', name: '肠胃调理', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '4',
    name: '皮肤用药',
    level: 1,
    sort: 4,
    status: 1,
    children: [
      { id: '4-1', name: '皮炎湿疹', level: 2, sort: 1, status: 1 },
      { id: '4-2', name: '痤疮粉刺', level: 2, sort: 2, status: 1 },
      { id: '4-3', name: '真菌感染', level: 2, sort: 3, status: 1 },
      { id: '4-4', name: '烫伤烧伤', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '5',
    name: '维生素钙',
    level: 1,
    sort: 5,
    status: 1,
    children: [
      { id: '5-1', name: '维生素', level: 2, sort: 1, status: 1 },
      { id: '5-2', name: '钙片', level: 2, sort: 2, status: 1 },
      { id: '5-3', name: '矿物质', level: 2, sort: 3, status: 1 },
      { id: '5-4', name: '蛋白粉', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '6',
    name: '解热镇痛',
    level: 1,
    sort: 6,
    status: 1,
    children: [
      { id: '6-1', name: '退烧药', level: 2, sort: 1, status: 1 },
      { id: '6-2', name: '止痛药', level: 2, sort: 2, status: 1 },
      { id: '6-3', name: '关节痛', level: 2, sort: 3, status: 1 },
      { id: '6-4', name: '痛经', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '7',
    name: '五官用药',
    level: 1,
    sort: 7,
    status: 1,
    children: [
      { id: '7-1', name: '眼药水', level: 2, sort: 1, status: 1 },
      { id: '7-2', name: '鼻用药', level: 2, sort: 2, status: 1 },
      { id: '7-3', name: '口腔咽喉', level: 2, sort: 3, status: 1 },
      { id: '7-4', name: '耳用药', level: 2, sort: 4, status: 1 }
    ]
  },
  {
    id: '8',
    name: '医疗器械',
    level: 1,
    sort: 8,
    status: 1,
    children: [
      { id: '8-1', name: '体温计', level: 2, sort: 1, status: 1 },
      { id: '8-2', name: '血压计', level: 2, sort: 2, status: 1 },
      { id: '8-2', name: '血糖仪', level: 2, sort: 3, status: 1 },
      { id: '8-4', name: '口罩', level: 2, sort: 4, status: 1 }
    ]
  }
])

// 当前选中的一级分类
const activeCategoryId = ref('1')

// 当前选中的二级分类
const activeSubCategoryId = ref('')

// 当前分类
const currentCategory = computed(() => {
  return categories.value.find(c => c.id === activeCategoryId.value)
})

// 商品列表
const drugList = computed(() => {
  // 模拟根据分类筛选商品
  const startIndex = (parseInt(activeCategoryId.value) - 1) % mockDrugs.length
  const result = []
  for (let i = 0; i < 6; i++) {
    const drugIndex = (startIndex + i) % mockDrugs.length
    result.push({
      ...mockDrugs[drugIndex],
      id: `${mockDrugs[drugIndex].id}-${activeCategoryId.value}-${i}`
    })
  }
  return result
})

// 切换一级分类
const selectCategory = (id: string) => {
  activeCategoryId.value = id
  activeSubCategoryId.value = ''
}

// 切换二级分类
const selectSubCategory = (id: string) => {
  activeSubCategoryId.value = id
  ElMessage.success(`切换到分类: ${currentCategory.value?.children?.find(c => c.id === id)?.name || ''}`)
}

// 查看药品详情
const goToDrugDetail = (id: string) => {
  router.push(`/drug/${id}`)
}

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 初始化
onMounted(() => {
  const categoryId = route.query.id as string
  if (categoryId) {
    activeCategoryId.value = categoryId
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

    <!-- 分类内容区 -->
    <div class="category-content">
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
        <div class="sub-categories">
          <div
            v-for="sub in currentCategory?.children"
            :key="sub.id"
            class="sub-item"
            :class="{ active: activeSubCategoryId === sub.id }"
            @click="selectSubCategory(sub.id)"
          >
            {{ sub.name }}
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="drug-grid">
          <DrugCard
            v-for="drug in drugList"
            :key="drug.id"
            :drug="drug"
            layout="vertical"
            @add-to-cart="$message.success('已加入购物车')"
          />
        </div>
      </div>
    </div>
  </div>
</template>

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

    &:hover {
      opacity: 0.8;
    }
  }

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    background: rgba(255, 255, 255, 0.9);
    border-radius: $radius-xl;
    padding: $spacing-sm $spacing-md;
    color: $text-tertiary;
    cursor: pointer;

    span {
      font-size: $font-md;
    }
  }
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
    }

    &.active {
      background: $bg-white;
      color: $primary;
      font-weight: 500;

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
}

// 二级分类
.sub-categories {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;

  .sub-item {
    padding: $spacing-sm $spacing-md;
    background: $bg-gray;
    border-radius: $radius-md;
    font-size: $font-sm;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba($primary, 0.1);
      color: $primary;
    }

    &.active {
      background: $primary;
      color: $text-white;
    }
  }
}

// 商品网格
.drug-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;
}
</style>
