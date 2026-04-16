<template>
  <div class="inquiry-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">问医生</span>
      <div class="header-actions">
        <button class="action-btn" @click="showSearch = true">
          <el-icon><Search /></el-icon>
        </button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div v-if="showSearch" class="search-section">
      <div class="search-bar">
        <el-icon class="search-icon"><Search /></el-icon>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索医生姓名、科室、症状..."
          @keyup.enter="handleSearch"
        />
        <el-icon v-if="searchKeyword" class="clear-icon" @click="searchKeyword = ''"><CircleClose /></el-icon>
      </div>
    </div>

    <!-- 优惠券卡片 -->
    <div class="coupon-section">
      <div class="coupon-card">
        <div class="coupon-left">
          <span class="coupon-price">¥2</span>
          <span class="coupon-title">秒问医生-首诊优惠</span>
        </div>
        <button class="coupon-btn" @click="useCoupon">去使用</button>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-entry-section">
      <div class="quick-entry-list">
        <div class="quick-entry-item" @click="goToSafeMedicine">
          <div class="entry-icon safe">
            <el-icon><FirstAidKit /></el-icon>
          </div>
          <span class="entry-name">安全用药</span>
        </div>
        <div class="quick-entry-item" @click="goToTcmTea">
          <div class="entry-icon tea">
            <el-icon><Mug /></el-icon>
          </div>
          <span class="entry-name">养生茶饮</span>
        </div>
        <div class="quick-entry-item" @click="goToPsychology">
          <div class="entry-icon psychology">
            <el-icon><ChatLineRound /></el-icon>
          </div>
          <div class="entry-info">
            <span class="entry-name">心理咨询</span>
            <span class="entry-price">199元</span>
          </div>
        </div>
        <div class="quick-entry-item" @click="goToMore">
          <div class="entry-icon more">
            <el-icon><MoreFilled /></el-icon>
          </div>
          <span class="entry-name">更多</span>
        </div>
      </div>
    </div>

    <!-- 找专家区域 -->
    <div class="expert-section">
      <div class="expert-header">
        <span class="expert-title">找专家</span>
        <span class="expert-subtitle">知名专家 权威诊疗</span>
      </div>

      <!-- 科室筛选 -->
      <div class="department-tabs">
        <button
          v-for="dept in departmentList"
          :key="dept.value"
          :class="['dept-tab', { active: selectedDepartment === dept.value }]"
          @click="selectDepartment(dept.value)"
        >
          {{ dept.label }}
        </button>
        <button class="dept-tab more" @click="showAllDepartments = true">
          全部<el-icon><ArrowDown /></el-icon>
        </button>
      </div>

      <!-- 排序筛选栏 -->
      <div class="filter-bar">
        <div class="filter-item" @click="showSortDropdown = !showSortDropdown">
          <span>{{ currentSortLabel }}</span>
          <el-icon :class="{ rotate: showSortDropdown }"><ArrowDown /></el-icon>
        </div>
        <div class="filter-item" @click="showSpecialtyDropdown = !showSpecialtyDropdown">
          <span>医生擅长</span>
          <el-icon :class="{ rotate: showSpecialtyDropdown }"><ArrowDown /></el-icon>
        </div>
        <div class="filter-item" @click="showFilterPanel = true">
          <el-icon><Filter /></el-icon>
          <span>筛选</span>
        </div>
      </div>

      <!-- 排序下拉菜单 -->
      <div v-if="showSortDropdown" class="dropdown-menu">
        <div
          v-for="sort in sortOptions"
          :key="sort.value"
          :class="['dropdown-item', { active: sortType === sort.value }]"
          @click="selectSort(sort.value)"
        >
          {{ sort.label }}
        </div>
      </div>
    </div>

    <!-- 医生列表 -->
    <div class="doctor-list-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated style="margin-top: 12px" />
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredDoctorList.length === 0" class="empty-state">
        <el-empty description="暂无符合条件的医生" :image-size="120" />
      </div>

      <!-- 医生卡片列表 -->
      <template v-else>
        <DoctorCard
          v-for="doctor in filteredDoctorList"
          :key="doctor.id"
          :doctor="doctor"
          @click="goToDoctorDetail(doctor)"
          @consult="handleConsult(doctor)"
        />

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <button class="load-more-btn" @click="loadMore" :disabled="loadingMore">
            <span v-if="loadingMore">
              <el-icon class="loading-icon"><Loading /></el-icon>
              加载中...
            </span>
            <span v-else>加载更多</span>
          </button>
        </div>
      </template>
    </div>

    <!-- 全部科室弹窗 -->
    <el-dialog
      v-model="showAllDepartments"
      title="选择科室"
      width="90%"
      class="department-dialog"
    >
      <div class="all-departments">
        <button
          v-for="dept in allDepartments"
          :key="dept.value"
          :class="['dept-option', { active: selectedDepartment === dept.value }]"
          @click="selectDepartment(dept.value); showAllDepartments = false"
        >
          {{ dept.label }}
        </button>
      </div>
    </el-dialog>

    <!-- 筛选面板（抽屉） -->
    <el-drawer
      v-model="showFilterPanel"
      title="筛选条件"
      size="80%"
      :with-header="true"
    >
      <div class="filter-panel">
        <div class="filter-group">
          <h4>科室</h4>
          <div class="filter-tags">
            <button
              v-for="dept in allDepartments"
              :key="dept.value"
              :class="['tag-btn', { active: selectedDepartment === dept.value }]"
              @click="selectedDepartment = dept.value"
            >
              {{ dept.label }}
            </button>
          </div>
        </div>
        <div class="filter-group">
          <h4>职称</h4>
          <div class="filter-tags">
            <button
              v-for="title in titleOptions"
              :key="title"
              :class="['tag-btn', { active: selectedTitle === title }]"
              @click="selectedTitle = title"
            >
              {{ title }}
            </button>
          </div>
        </div>
        <div class="filter-group">
          <h4>服务类型</h4>
          <div class="filter-tags">
            <button
              v-for="service in serviceOptions"
              :key="service.value"
              :class="['tag-btn', { active: selectedService === service.value }]"
              @click="selectedService = service.value"
            >
              {{ service.label }}
            </button>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="filter-actions">
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="applyFilter">确定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Search,
  CircleClose,
  ArrowDown,
  Filter,
  Loading,
  FirstAidKit,
  Mug,
  ChatLineRound,
  MoreFilled
} from '@element-plus/icons-vue'
import DoctorCard from '@/components/consultation/DoctorCard.vue'
import type { DoctorInfo } from '@/api/modules/inquiry'

const router = useRouter()
const route = useRoute()

// 页面状态
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const showSearch = ref(false)
const showFilterPanel = ref(false)
const showAllDepartments = ref(false)
const showSortDropdown = ref(false)
const showSpecialtyDropdown = ref(false)
const searchKeyword = ref('')

// 筛选状态
const selectedDepartment = ref('all')
const selectedTitle = ref('全部')
const selectedService = ref('all')
const sortType = ref('comprehensive')

// 默认头像
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor'

// 科室列表（横向滚动显示）
const departmentList = ref([
  { value: 'dermatology', label: '皮肤科' },
  { value: 'respiratory', label: '呼吸内科' },
  { value: 'gynecology', label: '妇产科' },
  { value: 'digestive', label: '消化内科' },
])

// 全部科室
const allDepartments = ref([
  { value: 'all', label: '全部科室' },
  { value: 'dermatology', label: '皮肤科' },
  { value: 'respiratory', label: '呼吸内科' },
  { value: 'gynecology', label: '妇产科' },
  { value: 'digestive', label: '消化内科' },
  { value: 'pediatrics', label: '儿科' },
  { value: 'psychology', label: '心理咨询' },
  { value: 'andrology', label: '男科' },
  { value: 'internal', label: '内科' },
  { value: 'surgery', label: '外科' },
  { value: 'tcm', label: '中医科' },
  { value: 'ent', label: '耳鼻喉' },
  { value: 'ophthalmology', label: '眼科' },
  { value: 'stomatology', label: '口腔科' },
])

// 排序选项
const sortOptions = ref([
  { value: 'comprehensive', label: '综合排序' },
  { value: 'rating', label: '好评率' },
  { value: 'consultationCount', label: '问诊量' },
  { value: 'responseTime', label: '响应速度' },
])

// 当前排序标签
const currentSortLabel = computed(() => {
  const sort = sortOptions.value.find(s => s.value === sortType.value)
  return sort?.label || '综合排序'
})

// 职称选项
const titleOptions = ref(['全部', '主任医师', '副主任医师', '主治医师'])

// 服务选项
const serviceOptions = ref([
  { value: 'all', label: '全部' },
  { value: 'prescription', label: '可开方' },
  { value: 'online', label: '在线' },
])

// 医生列表
const doctorList = ref<DoctorInfo[]>([])

// 模拟医生数据（带更多字段）
const mockDoctors: DoctorInfo[] = [
  {
    id: '1',
    name: '刘贞君',
    title: '主治医师',
    department: '皮肤科',
    hospital: '山东青岛中西医结合医院',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor1',
    specialty: '擅长中西医结合诊疗银屑病，痤疮，湿疹，荨麻疹等常见皮肤病',
    rating: 0.98,
    isOnline: true,
    consultationCount: 11000,
    waitTime: 12,
    price: 19.9,
    tags: ['三甲']
  },
  {
    id: '2',
    name: '张晓明',
    title: '副主任医师',
    department: '呼吸内科',
    hospital: '北京协和医院',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor2',
    specialty: '擅长慢性咳嗽、哮喘、慢阻肺、肺部感染等呼吸系统疾病的诊治',
    rating: 0.99,
    isOnline: true,
    consultationCount: 8500,
    waitTime: 8,
    price: 29.9,
    tags: ['三甲']
  },
  {
    id: '3',
    name: '李雪梅',
    title: '主任医师',
    department: '妇产科',
    hospital: '上海红房子妇产科医院',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor3',
    specialty: '擅长妇科肿瘤、子宫内膜异位症、月经不调等妇科疾病的诊治',
    rating: 0.97,
    isOnline: false,
    consultationCount: 15200,
    waitTime: 15,
    price: 39.9,
    tags: ['三甲']
  },
  {
    id: '4',
    name: '王建国',
    title: '副主任医师',
    department: '消化内科',
    hospital: '四川大学华西医院',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor4',
    specialty: '擅长胃炎、胃溃疡、肠易激综合征等消化系统疾病的诊治',
    rating: 0.96,
    isOnline: true,
    consultationCount: 6800,
    waitTime: 10,
    price: 25.9,
    tags: ['三甲']
  },
  {
    id: '5',
    name: '陈小红',
    title: '主治医师',
    department: '儿科',
    hospital: '广州市妇女儿童医疗中心',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor5',
    specialty: '擅长小儿发热、呼吸道感染、腹泻等常见儿科疾病的诊治',
    rating: 0.98,
    isOnline: true,
    consultationCount: 9200,
    waitTime: 6,
    price: 19.9,
    tags: ['三甲']
  },
  {
    id: '6',
    name: '赵文静',
    title: '主任医师',
    department: '心理咨询',
    hospital: '北京大学第六医院',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=doctor6',
    specialty: '擅长焦虑症、抑郁症、睡眠障碍、情绪管理等心理问题的咨询与治疗',
    rating: 0.99,
    isOnline: true,
    consultationCount: 5600,
    waitTime: 5,
    price: 199,
    tags: ['三甲']
  },
]

// 过滤后的医生列表
const filteredDoctorList = computed(() => {
  let result = [...doctorList.value]

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(doc =>
      doc.name.toLowerCase().includes(keyword) ||
      doc.department.toLowerCase().includes(keyword) ||
      doc.specialty.toLowerCase().includes(keyword)
    )
  }

  // 科室过滤
  if (selectedDepartment.value !== 'all') {
    const deptMap: Record<string, string> = {
      dermatology: '皮肤科',
      respiratory: '呼吸内科',
      gynecology: '妇产科',
      digestive: '消化内科',
      pediatrics: '儿科',
      psychology: '心理咨询',
      andrology: '男科',
      internal: '内科',
      surgery: '外科',
      tcm: '中医科'
    }
    const deptName = deptMap[selectedDepartment.value]
    if (deptName) {
      result = result.filter(doc => doc.department === deptName)
    }
  }

  // 职称过滤
  if (selectedTitle.value !== '全部') {
    result = result.filter(doc => doc.title === selectedTitle.value)
  }

  // 服务过滤
  if (selectedService.value === 'online') {
    result = result.filter(doc => doc.isOnline)
  }

  // 排序
  switch (sortType.value) {
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    case 'consultationCount':
      result.sort((a, b) => (b.consultationCount || 0) - (a.consultationCount || 0))
      break
    case 'responseTime':
      result.sort((a, b) => (a.waitTime || 999) - (b.waitTime || 999))
      break
    default:
      // 综合排序，在线优先
      result.sort((a, b) => (b.isOnline ? 1 : 0) - (a.isOnline ? 1 : 0))
  }

  return result
})

// 初始化
onMounted(() => {
  // 从路由参数获取筛选条件
  const { keyword, department } = route.query
  if (keyword) {
    searchKeyword.value = keyword as string
    showSearch.value = true
  }
  if (department) {
    selectedDepartment.value = department as string
  }

  // 加载医生列表
  loadDoctors()
})

// 加载医生列表
const loadDoctors = async () => {
  loading.value = true
  try {
    // 实际项目中调用API
    // const res = await getDoctorList({ department: selectedDepartment.value })

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    doctorList.value = mockDoctors
    hasMore.value = false
  } catch (error) {
    console.error('获取医生列表失败:', error)
    ElMessage.error('获取医生列表失败')
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 搜索
const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
  loadDoctors()
}

// 选择科室
const selectDepartment = (value: string) => {
  selectedDepartment.value = value
  loadDoctors()
}

// 选择排序
const selectSort = (value: string) => {
  sortType.value = value
  showSortDropdown.value = false
}

// 跳转到医生详情
const goToDoctorDetail = (doctor: DoctorInfo) => {
  router.push({
    path: `/doctor/${doctor.id}`,
    query: {
      name: doctor.name,
      department: doctor.department
    }
  })
}

// 处理咨询
const handleConsult = (doctor: DoctorInfo) => {
  console.log('咨询医生:', doctor.name)
}

// 使用优惠券
const useCoupon = () => {
  ElMessage.success('优惠券已领取')
}

// 快捷入口跳转
const goToSafeMedicine = () => {
  router.push('/category')
}

const goToTcmTea = () => {
  router.push('/category')
}

const goToPsychology = () => {
  selectedDepartment.value = 'psychology'
  loadDoctors()
}

const goToMore = () => {
  showAllDepartments.value = true
}

// 加载更多
const loadMore = async () => {
  if (loadingMore.value) return
  loadingMore.value = true

  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    hasMore.value = false
  } finally {
    loadingMore.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  selectedDepartment.value = 'all'
  selectedTitle.value = '全部'
  selectedService.value = 'all'
}

// 应用筛选
const applyFilter = () => {
  showFilterPanel.value = false
  loadDoctors()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/inquiry-theme' as *;

.inquiry-page {
  min-height: 100vh;
  background: $inquiry-bg;
  padding-bottom: 20px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  color: $text-white;
  position: sticky;
  top: 0;
  z-index: 100;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    flex: 1;
    text-align: center;
  }

  .header-actions {
    width: 36px;
    display: flex;
    justify-content: flex-end;

    .action-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: transparent;
      border: none;
      color: $text-white;
      cursor: pointer;
      border-radius: 50%;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 搜索栏
.search-section {
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  padding: 0 $spacing-md $spacing-md;

  .search-bar {
    display: flex;
    align-items: center;
    background: $inquiry-card-bg;
    border-radius: 20px;
    padding: 8px 12px;

    .search-icon {
      color: $inquiry-text-tertiary;
      margin-right: 8px;
    }

    input {
      flex: 1;
      border: none;
      outline: none;
      font-size: $font-md;
      background: transparent;
      color: $inquiry-text-primary;

      &::placeholder {
        color: $inquiry-text-tertiary;
      }
    }

    .clear-icon {
      color: $inquiry-text-tertiary;
      cursor: pointer;
    }
  }
}

// 优惠券区域
.coupon-section {
  background: linear-gradient(135deg, $inquiry-primary 0%, $inquiry-primary-dark 100%);
  padding: $spacing-md;
  padding-top: 0;

  .coupon-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #FFF7E6;
    border-radius: 12px;
    padding: 12px 16px;

    .coupon-left {
      display: flex;
      align-items: center;
      gap: 8px;

      .coupon-price {
        font-size: 24px;
        font-weight: 700;
        color: $inquiry-tag-fever;
      }

      .coupon-title {
        font-size: $font-md;
        color: $inquiry-text-primary;
        font-weight: 500;
      }
    }

    .coupon-btn {
      padding: 6px 16px;
      background: $inquiry-tag-fever;
      color: $text-white;
      border: none;
      border-radius: 16px;
      font-size: $font-sm;
      font-weight: 500;
      cursor: pointer;

      &:active {
        opacity: 0.9;
      }
    }
  }
}

// 快捷入口
.quick-entry-section {
  background: $inquiry-card-bg;
  padding: $spacing-md;
  border-bottom: 1px solid rgba($inquiry-primary, 0.08);

  .quick-entry-list {
    display: flex;
    gap: 16px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .quick-entry-item {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    flex-shrink: 0;

    .entry-icon {
      width: 40px;
      height: 40px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.safe {
        background: rgba($inquiry-primary, 0.1);
        color: $inquiry-primary;
      }

      &.tea {
        background: rgba($inquiry-tag-fever, 0.1);
        color: $inquiry-tag-fever;
      }

      &.psychology {
        background: rgba($primary, 0.1);
        color: $primary;
      }

      &.more {
        background: $inquiry-bg;
        color: $inquiry-text-tertiary;
      }

      .el-icon {
        font-size: 20px;
      }
    }

    .entry-name {
      font-size: $font-sm;
      color: $inquiry-text-primary;
      white-space: nowrap;
    }

    .entry-info {
      display: flex;
      flex-direction: column;

      .entry-price {
        font-size: $font-xs;
        color: $inquiry-tag-price;
      }
    }
  }
}

// 找专家区域
.expert-section {
  background: $inquiry-card-bg;
  padding: $spacing-md;
  border-bottom: 1px solid rgba($inquiry-primary, 0.08);
  position: relative;

  .expert-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .expert-title {
      font-size: $font-lg;
      font-weight: 600;
      color: $inquiry-text-primary;
    }

    .expert-subtitle {
      font-size: $font-sm;
      color: $inquiry-text-tertiary;
    }
  }

  .department-tabs {
    display: flex;
    gap: 10px;
    margin-bottom: 12px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      display: none;
    }

    .dept-tab {
      padding: 6px 14px;
      background: $inquiry-bg;
      border: none;
      border-radius: 16px;
      font-size: 13px;
      color: $inquiry-text-secondary;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      gap: 4px;

      &.active {
        background: rgba($inquiry-primary, 0.1);
        color: $inquiry-primary;
        font-weight: 500;
      }

      &.more {
        background: transparent;
        border: 1px solid rgba($inquiry-primary, 0.2);
      }

      &:active {
        transform: scale(0.95);
      }
    }
  }

  .filter-bar {
    display: flex;
    align-items: center;
    gap: 20px;

    .filter-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: $inquiry-text-secondary;
      cursor: pointer;

      .el-icon {
        font-size: 12px;
        transition: transform 0.2s;

        &.rotate {
          transform: rotate(180deg);
        }
      }

      &:active {
        color: $inquiry-primary;
      }
    }
  }

  .dropdown-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: $inquiry-card-bg;
    border-bottom: 1px solid rgba($inquiry-primary, 0.08);
    z-index: 50;
    padding: 8px 0;

    .dropdown-item {
      padding: 10px $spacing-md;
      font-size: $font-md;
      color: $inquiry-text-secondary;
      cursor: pointer;

      &.active {
        color: $inquiry-primary;
        background: rgba($inquiry-primary, 0.05);
      }

      &:active {
        background: $inquiry-bg;
      }
    }
  }
}

// 医生列表区域
.doctor-list-section {
  padding: $spacing-md;

  .loading-container {
    padding: $spacing-lg 0;
  }

  .empty-state {
    margin-top: $spacing-xxl;
  }
}

// 加载更多
.load-more {
  padding: $spacing-lg;
  text-align: center;

  .load-more-btn {
    padding: 10px 24px;
    background: $inquiry-card-bg;
    border: 1px solid rgba($inquiry-primary, 0.2);
    border-radius: 20px;
    font-size: 14px;
    color: $inquiry-text-secondary;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .loading-icon {
      animation: rotate 1s linear infinite;
    }

    &:active:not(:disabled) {
      background: $inquiry-bg;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 全部科室弹窗
.all-departments {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: $spacing-md;

  .dept-option {
    padding: 8px 16px;
    background: $inquiry-bg;
    border: 1px solid transparent;
    border-radius: 16px;
    font-size: 13px;
    color: $inquiry-text-secondary;
    cursor: pointer;
    transition: all 0.2s;

    &.active {
      background: rgba($inquiry-primary, 0.1);
      border-color: $inquiry-primary;
      color: $inquiry-primary;
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

// 筛选面板
.filter-panel {
  padding: $spacing-md;

  .filter-group {
    margin-bottom: $spacing-xl;

    h4 {
      font-size: $font-md;
      font-weight: 600;
      color: $inquiry-text-primary;
      margin-bottom: $spacing-md;
    }

    .filter-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .tag-btn {
        padding: 8px 16px;
        background: $inquiry-bg;
        border: 1px solid transparent;
        border-radius: 16px;
        font-size: 13px;
        color: $inquiry-text-secondary;
        cursor: pointer;
        transition: all 0.2s;

        &.active {
          background: rgba($inquiry-primary, 0.1);
          border-color: $inquiry-primary;
          color: $inquiry-primary;
        }

        &:active {
          transform: scale(0.95);
        }
      }
    }
  }
}

.filter-actions {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md;
  border-top: 1px solid rgba($inquiry-primary, 0.08);

  .el-button {
    flex: 1;

    &--primary {
      background: $inquiry-primary;
      border-color: $inquiry-primary;

      &:hover {
        background: $inquiry-primary-dark;
        border-color: $inquiry-primary-dark;
      }
    }
  }
}
</style>
