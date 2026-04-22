<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Search, ArrowRight, ArrowDown, FirstAidKit, Timer } from '@element-plus/icons-vue'
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

// 左侧症状分类数据
const symptomCategories = ref([
  { id: 'all', name: '全部' },
  { id: 'fever', name: '发烧/头痛' },
  { id: 'nose', name: '鼻塞/流涕' },
  { id: 'throat', name: '咽痛/喉痒' },
  { id: 'cough', name: '咳嗽/咳痰' },
  { id: 'covid', name: '新冠用药' },
  { id: 'flu', name: '甲流用药' },
  { id: 'pneumonia', name: '支原体肺炎' },
  { id: 'anti', name: '消炎药' },
  { id: 'child', name: '儿童感冒' },
  { id: 'feverpatch', name: '退热贴' },
  { id: 'thermometer', name: '体温计' },
  { id: 'mask', name: '口罩' },
  { id: 'disinfect', name: '消毒灭菌' },
  { id: 'immunity', name: '提高免疫力' },
  { id: 'test', name: '检测用品' }
])

// 当前选中的症状分类
const activeSymptomId = ref('all')

// 二级分类标签
const subCategories = ref([
  { id: 'all', name: '全部' },
  { id: 'cold', name: '感冒药' },
  { id: 'ibu', name: '布洛芬' },
  { id: 'anti', name: '消炎药' },
  { id: 'cough', name: '止咳药' },
  { id: 'fever', name: '退烧药' }
])

// 模拟药品数据
const mockDrugList = ref([
  {
    id: '1',
    name: '[京都念慈菴]京都念慈菴蜜炼川贝枇杷膏150ml/瓶/盒',
    image: 'https://via.placeholder.com/120x120/f5f5f5/666?text=枇杷膏',
    tags: ['进口', '止咳糖浆', '咳嗽干咳', '润肺止咳化痰'],
    price: 28.8,
    deliveryTime: '20分钟',
    monthlySales: '60万+'
  },
  {
    id: '2',
    name: '[新康泰克]氨酚咖那敏片10片*2板/盒',
    image: 'https://via.placeholder.com/120x120/e8f4ff/666?text=新康泰克',
    tags: ['感冒', '发热', '头痛', '四肢酸痛'],
    price: 20.61,
    deliveryTime: '26分钟',
    monthlySales: '10万+'
  },
  {
    id: '3',
    name: '[仁和]阿莫西林胶囊0.25g*10粒*4板/盒',
    image: 'https://via.placeholder.com/120x120/fff8e7/666?text=阿莫西林',
    tags: ['处方药', '国家基药'],
    price: 18,
    deliveryTime: '15分钟',
    monthlySales: '50万+',
    isRx: true
  },
  {
    id: '4',
    name: '[云丰]蒲地蓝消炎片(薄膜衣片)0.3g*24片*2板/盒',
    image: 'https://via.placeholder.com/120x120/e8f8f0/666?text=蒲地蓝',
    tags: ['疖肿', '咽炎', '扁桃体炎'],
    price: 19.8,
    deliveryTime: '37分钟',
    monthlySales: '50万+'
  },
  {
    id: '5',
    name: '[仁和]复方鲜竹沥液10ml*6瓶/盒',
    image: 'https://via.placeholder.com/120x120/f0f8e8/666?text=鲜竹沥',
    tags: ['痰热咳嗽', '痰黄粘稠'],
    price: 19.2,
    deliveryTime: '26分钟',
    monthlySales: '2万+'
  },
  {
    id: '6',
    name: '[感康]复方氨酚烷胺片6片*2板/盒',
    image: 'https://via.placeholder.com/120x120/ffe8e8/666?text=感康',
    tags: ['感冒流感发热', '头痛咽痛', '鼻塞流涕'],
    price: 16.5,
    deliveryTime: '18分钟',
    monthlySales: '30万+'
  }
])

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getDrugCategories()
    if (Array.isArray(res)) {
      categories.value = res
    } else if (res && Array.isArray(res.list)) {
      categories.value = res.list
    } else if (res && res.data) {
      categories.value = Array.isArray(res.data) ? res.data : []
    }

    if (!activeCategoryId.value && categories.value.length > 0) {
      activeCategoryId.value = categories.value[0].id
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
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

    if (activeSubCategoryId.value) {
      params.categoryId = activeSubCategoryId.value
    } else if (activeCategoryId.value) {
      params.categoryId = activeCategoryId.value
    }

    const res = await getDrugList(params)
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
    drugList.value = mockDrugList.value as any
  } finally {
    drugsLoading.value = false
  }
}

// 切换症状分类
const selectSymptom = (id: string) => {
  activeSymptomId.value = id
  fetchDrugs()
}

// 切换二级分类
const selectSubCategory = (id: string) => {
  activeSubCategoryId.value = id
}

// 查看药品详情
const goToDrugDetail = (id: string) => {
  router.push(`/drug/${id}`)
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 去搜索
const goSearch = () => {
  router.push('/search')
}

// 去咨询医生
const goConsult = () => {
  router.push('/inquiry')
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
  drugList.value = mockDrugList.value as any
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
      <!-- 左侧症状分类 -->
      <div class="symptom-sidebar">
        <div class="symptom-list">
          <div
            v-for="item in symptomCategories"
            :key="item.id"
            class="symptom-item"
            :class="{ active: activeSymptomId === item.id }"
            @click="selectSymptom(item.id)"
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
            <div class="more-btn">
              <el-icon><ArrowDown /></el-icon>
            </div>
          </div>
        </div>

        <!-- 药品列表 -->
        <div class="drug-list">
          <div
            v-for="drug in mockDrugList"
            :key="drug.id"
            class="drug-item"
            @click="goToDrugDetail(drug.id)"
          >
            <div class="drug-image">
              <img :src="drug.image" :alt="drug.name" />
              <div v-if="drug.isRx" class="rx-badge">处方药</div>
              <div class="sales-badge">月售{{ drug.monthlySales }}</div>
            </div>
            <div class="drug-info">
              <div class="drug-name">{{ drug.name }}</div>
              <div class="drug-tags">
                <span
                  v-for="(tag, index) in drug.tags"
                  :key="index"
                  class="tag"
                  :class="{ 'tag-primary': index === 0 && drug.isRx, 'tag-blue': index === 0 && !drug.isRx }"
                >
                  {{ tag }}
                </span>
              </div>
              <div class="drug-bottom">
                <div class="price-section">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ drug.price }}</span>
                </div>
                <div class="delivery-time">
                  <el-icon><Timer /></el-icon>
                  <span>{{ drug.deliveryTime }}</span>
                </div>
              </div>
            </div>
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
        color: $error;

        .price-symbol {
          font-size: $font-sm;
          font-weight: 600;
        }

        .price-value {
          font-size: $font-xl;
          font-weight: 700;
        }
      }

      .delivery-time {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: $font-xs;
        color: $primary;
        background: rgba($primary, 0.1);
        padding: 6px 12px;
        border-radius: $radius-full;
        font-weight: 500;

        .el-icon {
          font-size: $font-xs;
        }
      }
    }
  }
}
</style>
