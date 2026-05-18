<template>
  <div class="doctor-department-section">
    <!-- 按科室问医生 - 每排5个，最多显示2排 -->
    <DepartmentGrid
      :departments="departments"
      :online-count="onlineCount"
      @select="handleDeptSelect"
    />

    <!-- 快捷服务入口 -->
    <ServiceShortcuts
      :shortcuts="serviceShortcuts"
      @click="handleShortcutClick"
    />

    <!-- 找专家区域 -->
    <div class="expert-section">
      <div class="expert-header">
        <span class="expert-title">找专家</span>
        <span class="expert-subtitle">知名专家 权威诊疗</span>
      </div>
      
      <!-- 科室筛选标签 -->
      <div class="dept-filter-bar">
        <div 
          v-for="tag in departmentTags" 
          :key="tag.code"
          class="filter-tag"
          :class="{ active: selectedDepartment === tag.code }"
          @click="handleDepartmentChange(tag.code)"
        >
          {{ tag.label }}
        </div>
        <div class="filter-tag more" @click="showAllDepartments">
          全部
          <el-icon><ArrowDown /></el-icon>
        </div>
      </div>

      <!-- 排序筛选栏 -->
      <div class="sort-filter-bar">
        <el-dropdown @command="handleSortChange" trigger="click">
          <div class="sort-item" :class="{ active: currentSort !== 'default' }">
            {{ sortOptions.find(s => s.value === currentSort)?.label || '综合排序' }}
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="opt in sortOptions" :key="opt.value" :command="opt.value">
                {{ opt.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown @command="handleSpecialtyChange" trigger="click">
          <div class="sort-item" :class="{ active: currentSpecialty !== 'all' }">
            {{ specialtyOptions.find(s => s.value === currentSpecialty)?.label || '医生擅长' }}
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="opt in specialtyOptions" :key="opt.value" :command="opt.value">
                {{ opt.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <div class="sort-item" :class="{ active: showFilterPanel }" @click="toggleFilterPanel">
          筛选
          <el-icon><Filter /></el-icon>
        </div>
      </div>

      <!-- 筛选面板 -->
      <div v-if="showFilterPanel" class="filter-panel">
        <div class="filter-section">
          <div class="filter-title">特色服务</div>
          <div class="filter-options">
            <span 
              v-for="option in featureOptions" 
              :key="option.value"
              class="filter-option"
              :class="{ active: selectedFeatures.includes(option.value) }"
              @click="toggleFeature(option.value)"
            >
              {{ option.label }}
            </span>
          </div>
        </div>
        <div class="filter-section">
          <div class="filter-title">医生职称</div>
          <div class="filter-options">
            <span 
              v-for="option in titleOptions" 
              :key="option.value"
              class="filter-option"
              :class="{ active: selectedTitle === option.value }"
              @click="selectTitle(option.value)"
            >
              {{ option.label }}
            </span>
          </div>
        </div>
        <div class="filter-section">
          <div class="filter-title">医院等级</div>
          <div class="filter-options">
            <span 
              v-for="option in hospitalLevelOptions" 
              :key="option.value"
              class="filter-option"
              :class="{ active: selectedHospitalLevel === option.value }"
              @click="selectHospitalLevel(option.value)"
            >
              {{ option.label }}
            </span>
          </div>
        </div>
        <div class="filter-actions">
          <button class="filter-btn reset" @click="resetFilters">重置</button>
          <button class="filter-btn confirm" @click="confirmFilters">确定</button>
        </div>
      </div>

      <!-- 医生卡片列表 -->
      <div class="doctor-list">
        <div
          v-for="doctor in doctorList"
          :key="doctor.id"
          class="doctor-card"
          @click="handleDoctorClick(doctor)"
        >
          <div class="doctor-main">
            <div class="avatar-section">
              <img :src="doctor.avatar" class="doctor-avatar" :alt="doctor.name" />
              <span v-if="doctor.isOnline" class="online-badge">在线</span>
            </div>
            <div class="doctor-info">
              <div class="doctor-header">
                <span class="doctor-name">{{ doctor.name }}</span>
                <span class="doctor-title">{{ doctor.title }}</span>
                <span class="doctor-dept">{{ doctor.department }}</span>
              </div>
              <div class="hospital-info">
                <span class="hospital-tag">三甲</span>
                <span class="hospital-name">{{ doctor.hospital }}</span>
              </div>
              <div class="prescription-tag" v-if="doctor.canPrescribe">可开方</div>
              <div class="specialty">擅长：{{ doctor.specialty }}</div>
              <div class="doctor-stats">
                <span class="stat-item">
                  <span class="stat-label">好评率</span>
                  <span class="stat-value">{{ (doctor.rating * 100).toFixed(0) }}%</span>
                </span>
                <span class="stat-item">
                  <span class="stat-label">接诊量</span>
                  <span class="stat-value">{{ doctor.consultCount }}</span>
                </span>
                <span class="stat-item">
                  <span class="stat-label">平均等待</span>
                  <span class="stat-value">{{ doctor.waitTime }}秒</span>
                </span>
              </div>
              <div class="consult-types">
                <span class="consult-type">图文</span>
                <span class="consult-price">¥{{ doctor.price }}</span>
                <span class="consult-type">电话</span>
                <span class="consult-price">¥{{ doctor.price }}</span>
                <span class="consult-type">视频</span>
                <span class="consult-price">¥{{ doctor.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more">
        <button class="load-more-btn" @click="loadMore">
          <span v-if="loadingMore">加载中...</span>
          <span v-else>加载更多医生</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, Filter } from '@element-plus/icons-vue'
import DepartmentGrid, { type Department } from '@/components/consultation/DepartmentGrid.vue'
import ServiceShortcuts, { type ShortcutItem } from '@/components/consultation/ServiceShortcuts.vue'
import { businessApi } from '@/api/modules/business'
import { getDoctorList } from '@/api/modules/inquiry'

const router = useRouter()

const onlineCount = ref(2700)

const departments = ref<Department[]>([])
const serviceShortcuts = ref<ShortcutItem[]>([])

const sortOptions = ref<{ label: string; value: string }[]>([])
const currentSort = ref('default')

const specialtyOptions = ref<{ label: string; value: string }[]>([])
const currentSpecialty = ref('all')

const showFilterPanel = ref(false)

const featureOptions = ref<{ label: string; value: string }[]>([])
const selectedFeatures = ref<string[]>([])

const titleOptions = ref<{ label: string; value: string }[]>([])
const selectedTitle = ref('all')

const hospitalLevelOptions = ref<{ label: string; value: string }[]>([])
const selectedHospitalLevel = ref('all')

const departmentTags = ref<{ code: string; label: string }[]>([])
const selectedDepartment = ref('all')

interface DoctorInfo {
  id: string
  name: string
  title: string
  department: string
  hospital: string
  avatar?: string
  specialty: string
  rating: number
  isOnline: boolean
  canPrescribe?: boolean
  waitTime?: number
  price?: number
  consultCount?: string
}

const doctorList = ref<DoctorInfo[]>([])
const hasMore = ref(true)
const loadingMore = ref(false)

const mockDoctors: DoctorInfo[] = [
  {
    id: '1',
    name: '周峰',
    title: '主治医师',
    department: '皮肤科',
    hospital: '武汉市黄陂区人民医院',
    avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop&crop=face',
    specialty: '擅长过敏性疾病（特应性皮炎、湿疹、荨麻疹）、自身免疫性疱病、药疹、结缔组织病及血管炎...',
    rating: 0.99,
    isOnline: true,
    canPrescribe: true,
    waitTime: 4,
    price: 19.9,
    consultCount: '7542'
  },
  {
    id: '2',
    name: '陈琼',
    title: '主治医师',
    department: '皮肤科',
    hospital: '辽宁中医药大学附属第二医院',
    avatar: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face',
    specialty: '陈琼，女，就职于辽宁中医药大学附属第二医院，主治医师，硕士研究生。对中医药治疗痤疮、...',
    rating: 0.97,
    isOnline: true,
    canPrescribe: true,
    waitTime: 37,
    price: 19.9,
    consultCount: '4.2万'
  },
  {
    id: '3',
    name: '李贤光',
    title: '主任医师',
    department: '皮肤科',
    hospital: '昆明医科大学第一附属医院',
    avatar: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=200&h=200&fit=crop&crop=face',
    specialty: '擅长湿疹、荨麻疹、痤疮、银屑病、白癜风等常见皮肤病的诊治',
    rating: 0.98,
    isOnline: true,
    canPrescribe: true,
    waitTime: 13,
    price: 19.9,
    consultCount: '1.8万'
  },
]

onMounted(async () => {
  await Promise.all([
    loadBusinessData(),
    loadDoctors()
  ])
})

async function loadDoctors() {
  try {
    console.log('开始加载医生列表...')
    const doctors = await getDoctorList({})
    console.log('医生列表数据:', doctors)
    if (Array.isArray(doctors) && doctors.length > 0) {
      doctorList.value = doctors.slice(0, 10).map((d: any) => ({
        id: d.id,
        name: d.name,
        title: d.title,
        hospital: d.hospital,
        department: d.department,
        avatar: d.avatar || '',
        specialty: d.specialty || '',
        price: d.price || 0,
        rating: d.rating || 0,
        isOnline: d.isOnline || false,
        canPrescribe: d.canPrescribe || false,
        waitTime: d.waitTime || 0,
        consultCount: d.consultCount || '0'
      }))
      console.log('医生列表加载成功，共', doctorList.value.length, '个医生')
    } else {
      console.warn('医生列表为空')
      // 使用模拟数据作为后备
      doctorList.value = mockDoctors
    }
  } catch (error) {
    console.error('加载医生列表失败:', error)
    // 使用模拟数据作为后备
    doctorList.value = mockDoctors
  }
}

async function loadBusinessData() {
  try {
    console.log('开始加载业务数据...')
    const [deptRes, shortcutRes, tagRes, sortRes, specialtyRes, featureRes, titleRes, hospitalRes] = await Promise.all([
      businessApi.getDepartments(),
      businessApi.getServiceShortcuts(),
      businessApi.getDepartmentTags(),
      businessApi.getDictData('doctor_sort'),
      businessApi.getDictData('doctor_specialty'),
      businessApi.getDictData('doctor_feature'),
      businessApi.getDictData('doctor_title'),
      businessApi.getDictData('hospital_level')
    ])

    console.log('科室数据:', deptRes)
    departments.value = deptRes
    serviceShortcuts.value = shortcutRes
    departmentTags.value = tagRes
    sortOptions.value = sortRes
    specialtyOptions.value = specialtyRes
    featureOptions.value = featureRes
    titleOptions.value = titleRes
    hospitalLevelOptions.value = hospitalRes
    console.log('业务数据加载成功')
  } catch (error) {
    console.error('加载业务数据失败:', error)
  }
}

function handleDeptSelect(dept: Department) {
  router.push({
    path: '/inquiry/triage/' + dept.code,
    query: { deptName: dept.name }
  })
}

function handleShortcutClick(item: ShortcutItem) {
  console.log('快捷服务:', item)
}

function handleDepartmentChange(value: string) {
  selectedDepartment.value = value
}

function showAllDepartments() {
  console.log('显示全部科室')
}

function handleDoctorClick(doctor: DoctorInfo) {
  router.push({
    path: `/doctor/${doctor.id}`,
    query: { name: doctor.name, department: doctor.department }
  })
}

function loadMore() {
  if (loadingMore.value) return
  loadingMore.value = true
  setTimeout(() => {
    loadingMore.value = false
    hasMore.value = false
  }, 500)
}

// 排序切换
function handleSortChange(command: string) {
  currentSort.value = command
  // 这里可以添加排序逻辑
  console.log('排序:', command)
}

// 擅长切换
function handleSpecialtyChange(command: string) {
  currentSpecialty.value = command
  // 这里可以添加筛选逻辑
  console.log('擅长:', command)
}

// 切换筛选面板
function toggleFilterPanel() {
  showFilterPanel.value = !showFilterPanel.value
}

// 切换特色服务
function toggleFeature(value: string) {
  const index = selectedFeatures.value.indexOf(value)
  if (index > -1) {
    selectedFeatures.value.splice(index, 1)
  } else {
    selectedFeatures.value.push(value)
  }
}

// 选择职称
function selectTitle(value: string) {
  selectedTitle.value = value
}

// 选择医院等级
function selectHospitalLevel(value: string) {
  selectedHospitalLevel.value = value
}

// 重置筛选
function resetFilters() {
  selectedFeatures.value = []
  selectedTitle.value = 'all'
  selectedHospitalLevel.value = 'all'
}

// 确认筛选
function confirmFilters() {
  showFilterPanel.value = false
  // 这里可以添加筛选逻辑
  console.log('筛选条件:', {
    features: selectedFeatures.value,
    title: selectedTitle.value,
    hospitalLevel: selectedHospitalLevel.value
  })
}
</script>

<style scoped lang="scss">
$primary-teal: #00C9A7;
$bg-teal: #F0F9F6;
$text-primary: #1A1A1A;
$text-secondary: #666666;
$text-tertiary: #999999;

.doctor-department-section {
  background-color: $bg-teal;
  min-height: 100%;
  padding-bottom: 10px;

  .expert-section {
    background-color: #fff;
    margin: 0 12px 10px;
    border-radius: 16px;
    padding: 14px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);

    .expert-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;

      .expert-title {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }

      .expert-subtitle {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .dept-filter-bar {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;
      overflow-x: auto;
      scrollbar-width: none;
      -ms-overflow-style: none;

      &::-webkit-scrollbar {
        display: none;
      }

      .filter-tag {
        padding: 6px 12px;
        background: #f5f5f5;
        border-radius: 16px;
        font-size: 13px;
        color: $text-secondary;
        white-space: nowrap;
        cursor: pointer;
        transition: all 0.2s;

        &.active {
          background: rgba($primary-teal, 0.1);
          color: $primary-teal;
          border: 1px solid $primary-teal;
        }

        &.more {
          display: flex;
          align-items: center;
          gap: 2px;
        }
      }
    }

    .sort-filter-bar {
      display: flex;
      gap: 16px;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px solid #f0f0f0;

      .sort-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: $text-secondary;
        cursor: pointer;

        &.active {
          color: $primary-teal;
        }

        .el-icon {
          font-size: 12px;
        }
      }
    }

    // 筛选面板
    .filter-panel {
      background: #f9f9f9;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 12px;

      .filter-section {
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .filter-title {
          font-size: 13px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 10px;
        }

        .filter-options {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;

          .filter-option {
            padding: 6px 12px;
            background: #fff;
            border-radius: 16px;
            font-size: 12px;
            color: $text-secondary;
            cursor: pointer;
            transition: all 0.2s;
            border: 1px solid #e0e0e0;

            &.active {
              background: rgba($primary-teal, 0.1);
              color: $primary-teal;
              border-color: $primary-teal;
            }
          }
        }
      }

      .filter-actions {
        display: flex;
        gap: 12px;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid #e0e0e0;

        .filter-btn {
          flex: 1;
          padding: 10px 0;
          border-radius: 20px;
          font-size: 14px;
          cursor: pointer;
          transition: all 0.2s;
          border: none;

          &.reset {
            background: #f5f5f5;
            color: $text-secondary;
          }

          &.confirm {
            background: $primary-teal;
            color: #fff;
          }
        }
      }
    }

    .doctor-list {
      .doctor-card {
        padding: 16px 0;
        border-bottom: 1px solid #f5f5f5;
        cursor: pointer;

        &:last-child {
          border-bottom: none;
        }

        .doctor-main {
          display: flex;
          gap: 12px;

          .avatar-section {
            position: relative;
            flex-shrink: 0;

            .doctor-avatar {
              width: 56px;
              height: 56px;
              border-radius: 50%;
              object-fit: cover;
            }

            .online-badge {
              position: absolute;
              bottom: 0;
              right: 0;
              background: $primary-teal;
              color: #fff;
              padding: 1px 4px;
              border-radius: 8px;
              font-size: 9px;
              font-weight: 600;
            }
          }

          .doctor-info {
            flex: 1;
            min-width: 0;

            .doctor-header {
              display: flex;
              align-items: center;
              gap: 6px;
              margin-bottom: 4px;

              .doctor-name {
                font-size: 16px;
                font-weight: 600;
                color: $text-primary;
              }

              .doctor-title {
                font-size: 12px;
                color: $text-secondary;
              }

              .doctor-dept {
                font-size: 12px;
                color: $text-secondary;
              }
            }

            .hospital-info {
              display: flex;
              align-items: center;
              gap: 6px;
              margin-bottom: 4px;

              .hospital-tag {
                background: $primary-teal;
                color: #fff;
                padding: 1px 6px;
                border-radius: 4px;
                font-size: 10px;
                font-weight: 600;
              }

              .hospital-name {
                font-size: 12px;
                color: $text-secondary;
              }
            }

            .prescription-tag {
              display: inline-block;
              color: $primary-teal;
              font-size: 12px;
              margin-bottom: 4px;
            }

            .specialty {
              font-size: 12px;
              color: $text-secondary;
              line-height: 1.5;
              margin-bottom: 8px;
              overflow: hidden;
              text-overflow: ellipsis;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
            }

            .doctor-stats {
              display: flex;
              gap: 12px;
              margin-bottom: 8px;

              .stat-item {
                font-size: 12px;

                .stat-label {
                  color: $text-tertiary;
                }

                .stat-value {
                  color: $text-primary;
                  font-weight: 500;
                }
              }
            }

            .consult-types {
              display: flex;
              gap: 12px;

              .consult-type {
                font-size: 12px;
                color: $text-secondary;
              }

              .consult-price {
                font-size: 12px;
                color: #FF6B6B;
                font-weight: 600;
              }
            }
          }
        }
      }
    }

    .load-more {
      padding: 16px;
      text-align: center;

      .load-more-btn {
        padding: 10px 24px;
        background-color: #fff;
        border: 1px solid #e0e0e0;
        border-radius: 20px;
        font-size: 14px;
        color: $text-secondary;
        cursor: pointer;
        transition: all 0.2s;

        &:active {
          background-color: #f5f5f5;
        }
      }
    }
  }
}
</style>
