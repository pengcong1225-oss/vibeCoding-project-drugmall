<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePatientStore } from '@/stores/patient'
import Tabbar from '@/components/Tabbar/index.vue'

const router = useRouter()
const patientStore = usePatientStore()

const searchKey = ref('')
const activeFilter = ref('all')
const showGroupManager = ref(false)

// 可配置的疾病分组 - 可从后端获取或本地配置
interface DiseaseGroup {
  key: string
  label: string
  color: string
  bgColor: string
  matchFields: string[] // 匹配的字段：diagnosis, tags, condition等
  matchValues: string[] // 匹配的值
  isSystem?: boolean // 是否是系统内置分组
}

// 默认分组配置
const defaultGroups: DiseaseGroup[] = [
  {
    key: 'all',
    label: '全部',
    color: '#333',
    bgColor: '#F5F5F5',
    matchFields: [],
    matchValues: [],
    isSystem: true
  },
  {
    key: 'hypertension',
    label: '高血压',
    color: '#D32F2F',
    bgColor: '#FFEBEE',
    matchFields: ['diagnosis', 'tags', 'condition'],
    matchValues: ['高血压', '血压高', '原发性高血压']
  },
  {
    key: 'diabetes',
    label: '糖尿病',
    color: '#F57C00',
    bgColor: '#FFF3E0',
    matchFields: ['diagnosis', 'tags', 'condition'],
    matchValues: ['糖尿病', '2型糖尿病', '1型糖尿病', '血糖高']
  },
  {
    key: 'heart',
    label: '心脏病',
    color: '#7B1FA2',
    bgColor: '#F3E5F5',
    matchFields: ['diagnosis', 'tags', 'condition'],
    matchValues: ['心脏病', '冠心病', '心绞痛', '心肌梗死', '心衰']
  },
  {
    key: 'respiratory',
    label: '呼吸系统',
    color: '#1976D2',
    bgColor: '#E3F2FD',
    matchFields: ['diagnosis', 'tags', 'condition'],
    matchValues: ['哮喘', '慢阻肺', '肺炎', '支气管炎', '呼吸道感染']
  },
  {
    key: 'chronic',
    label: '慢病管理',
    color: '#388E3C',
    bgColor: '#E8F5E9',
    matchFields: ['tags'],
    matchValues: ['慢病管理', '慢性病']
  },
  {
    key: 'recent',
    label: '最近就诊',
    color: '#00796B',
    bgColor: '#E0F2F1',
    matchFields: [],
    matchValues: [],
    isSystem: true
  }
]

// 自定义分组（可从localStorage或后端获取）
const customGroups = ref<DiseaseGroup[]>([])

// 所有分组
const allGroups = computed(() => [...defaultGroups, ...customGroups.value])

// 当前显示的分组（限制数量，超出可展开）
const visibleGroups = computed(() => {
  return allGroups.value.slice(0, 8)
})

// 筛选患者
const filteredPatients = computed(() => {
  let result = patientStore.patients

  // 搜索过滤
  if (searchKey.value.trim()) {
    const key = searchKey.value.toLowerCase()
    result = result.filter(p =>
      p.name.toLowerCase().includes(key) ||
      p.phone.includes(key) ||
      p.diagnosis?.some(d => d.toLowerCase().includes(key)) ||
      p.tags?.some(t => t.toLowerCase().includes(key))
    )
  }

  // 分组筛选
  if (activeFilter.value === 'all') {
    return result
  }

  if (activeFilter.value === 'recent') {
    return [...result].sort((a, b) =>
      new Date(b.lastVisit).getTime() - new Date(a.lastVisit).getTime()
    )
  }

  const group = allGroups.value.find(g => g.key === activeFilter.value)
  if (group && group.matchFields.length > 0) {
    result = result.filter(p => {
      // 检查所有匹配字段
      for (const field of group.matchFields) {
        const fieldValue = (p as any)[field]
        if (Array.isArray(fieldValue)) {
          // 如果是数组（如diagnosis, tags）
          if (fieldValue.some(v => group.matchValues.some(mv => v.includes(mv)))) {
            return true
          }
        } else if (typeof fieldValue === 'string') {
          // 如果是字符串
          if (group.matchValues.some(mv => fieldValue.includes(mv))) {
            return true
          }
        }
      }
      return false
    })
  }

  return result
})

// 获取当前分组的样式
const getGroupStyle = (key: string) => {
  const group = allGroups.value.find(g => g.key === key)
  if (!group) return {}
  return {
    background: activeFilter.value === key ? group.color : group.bgColor,
    color: activeFilter.value === key ? '#fff' : group.color
  }
}

// 获取患者的疾病标签
const getPatientDiseaseTags = (patient: any) => {
  const tags: { label: string; color: string; bgColor: string }[] = []

  for (const group of allGroups.value) {
    if (group.isSystem) continue

    for (const field of group.matchFields) {
      const fieldValue = patient[field]
      if (Array.isArray(fieldValue)) {
        if (fieldValue.some(v => group.matchValues.some(mv => v.includes(mv)))) {
          tags.push({ label: group.label, color: group.color, bgColor: group.bgColor })
          break
        }
      } else if (typeof fieldValue === 'string') {
        if (group.matchValues.some(mv => fieldValue.includes(mv))) {
          tags.push({ label: group.label, color: group.color, bgColor: group.bgColor })
          break
        }
      }
    }
  }

  return tags.slice(0, 3) // 最多显示3个标签
}

// 分组管理
const newGroupName = ref('')
const newGroupKeywords = ref('')

const addCustomGroup = () => {
  if (!newGroupName.value.trim()) return

  const keywords = newGroupKeywords.value.split(/[,，]/).map(k => k.trim()).filter(Boolean)
  if (keywords.length === 0) return

  const colors = [
    { color: '#D32F2F', bgColor: '#FFEBEE' },
    { color: '#F57C00', bgColor: '#FFF3E0' },
    { color: '#7B1FA2', bgColor: '#F3E5F5' },
    { color: '#1976D2', bgColor: '#E3F2FD' },
    { color: '#388E3C', bgColor: '#E8F5E9' },
    { color: '#00796B', bgColor: '#E0F2F1' },
    { color: '#5D4037', bgColor: '#EFEBE9' },
    { color: '#455A64', bgColor: '#ECEFF1' }
  ]
  const randomColor = colors[Math.floor(Math.random() * colors.length)]

  customGroups.value.push({
    key: `custom_${Date.now()}`,
    label: newGroupName.value.trim(),
    color: randomColor.color,
    bgColor: randomColor.bgColor,
    matchFields: ['diagnosis', 'tags', 'condition'],
    matchValues: keywords
  })

  newGroupName.value = ''
  newGroupKeywords.value = ''
}

const removeCustomGroup = (key: string) => {
  const index = customGroups.value.findIndex(g => g.key === key)
  if (index > -1) {
    customGroups.value.splice(index, 1)
    if (activeFilter.value === key) {
      activeFilter.value = 'all'
    }
  }
}

const goToDetail = (id: string) => {
  router.push(`/patients/detail/${id}`)
}

const goToRecords = (id: string) => {
  router.push(`/patients/records/${id}`)
}

onMounted(() => {
  patientStore.fetchPatients()
  // 从localStorage加载自定义分组
  const saved = localStorage.getItem('patient_custom_groups')
  if (saved) {
    try {
      customGroups.value = JSON.parse(saved)
    } catch (e) {
      console.error('加载自定义分组失败', e)
    }
  }
})

// 保存自定义分组
const saveCustomGroups = () => {
  localStorage.setItem('patient_custom_groups', JSON.stringify(customGroups.value))
  showGroupManager.value = false
}
</script>

<template>
  <div class="patients-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <h1 class="page-title">患者管理</h1>
      <div class="header-stats">
        <span class="stat-item">共 {{ patientStore.patients.length }} 位患者</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-input">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input
          v-model="searchKey"
          type="text"
          placeholder="搜索患者姓名/手机号/疾病"
        />
        <button v-if="searchKey" class="clear-btn" @click="searchKey = ''">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
        </button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <div
          v-for="group in visibleGroups"
          :key="group.key"
          class="filter-tab"
          :class="{ active: activeFilter === group.key }"
          :style="getGroupStyle(group.key)"
          @click="activeFilter = group.key"
        >
          {{ group.label }}
        </div>
        <div class="filter-tab manage-btn" @click="showGroupManager = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="3"></circle>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
          </svg>
        </div>
      </div>
    </div>

    <!-- 患者列表 -->
    <div class="patients-list">
      <div
        v-for="patient in filteredPatients"
        :key="patient.id"
        class="patient-card"
        @click="goToDetail(patient.id)"
      >
        <div class="card-main">
          <div class="patient-avatar">
            {{ patient.name[0] }}
          </div>
          <div class="patient-info">
            <div class="info-header">
              <span class="patient-name">{{ patient.name }}</span>
              <span class="patient-gender" :class="patient.gender">{{ patient.gender }}</span>
              <span class="patient-age">{{ patient.age }}岁</span>
            </div>
            <!-- 疾病标签 -->
            <div class="disease-tags">
              <span
                v-for="(tag, index) in getPatientDiseaseTags(patient)"
                :key="index"
                class="disease-tag"
                :style="{ background: tag.bgColor, color: tag.color }"
              >
                {{ tag.label }}
              </span>
              <span
                v-for="(tag, index) in patient.tags.filter(t => !getPatientDiseaseTags(patient).some(dt => t.includes(dt.label))).slice(0, 2)"
                :key="`other-${index}`"
                class="other-tag"
              >
                {{ tag }}
              </span>
            </div>
            <div class="info-footer">
              <span class="last-visit">最近就诊: {{ patient.lastVisit }}</span>
              <span class="visit-count">就诊{{ patient.visitCount }}次</span>
            </div>
          </div>
        </div>
        <div class="card-actions">
          <button class="action-btn primary" @click.stop="goToRecords(patient.id)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            病历
          </button>
          <button class="action-btn" @click.stop="goToDetail(patient.id)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
              <circle cx="12" cy="12" r="3"></circle>
            </svg>
            详情
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredPatients.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
        </div>
        <p class="empty-text">暂无患者数据</p>
      </div>
    </div>

    <!-- 分组管理弹窗 -->
    <div v-if="showGroupManager" class="modal-mask" @click.self="showGroupManager = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>管理疾病分组</h3>
          <button class="close-btn" @click="showGroupManager = false">&times;</button>
        </div>

        <div class="modal-body">
          <!-- 现有分组 -->
          <div class="group-list">
            <div class="section-title">现有分组</div>
            <div
              v-for="group in customGroups"
              :key="group.key"
              class="group-item"
            >
              <div class="group-info">
                <span
                  class="group-color"
                  :style="{ background: group.color }"
                ></span>
                <span class="group-name">{{ group.label }}</span>
                <span class="group-keywords">{{ group.matchValues.join(', ') }}</span>
              </div>
              <button class="delete-btn" @click="removeCustomGroup(group.key)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
            <div v-if="customGroups.length === 0" class="empty-groups">
              暂无自定义分组
            </div>
          </div>

          <!-- 添加新分组 -->
          <div class="add-group">
            <div class="section-title">添加新分组</div>
            <div class="form-item">
              <label>分组名称</label>
              <input
                v-model="newGroupName"
                type="text"
                placeholder="如：肿瘤患者"
              />
            </div>
            <div class="form-item">
              <label>关键词（用逗号分隔）</label>
              <input
                v-model="newGroupKeywords"
                type="text"
                placeholder="如：肿瘤,癌症,化疗"
              />
            </div>
            <button
              class="btn-add"
              :disabled="!newGroupName.trim() || !newGroupKeywords.trim()"
              @click="addCustomGroup"
            >
              添加分组
            </button>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showGroupManager = false">取消</button>
          <button class="btn-save" @click="saveCustomGroups">保存</button>
        </div>
      </div>
    </div>

    <Tabbar />
  </div>
</template>

<style lang="scss" scoped>
$primary: #2E7D32;
$primary-light: #4CAF50;
$primary-50: #E8F5E9;
$text-primary: #333;
$text-secondary: #666;
$text-tertiary: #999;
$bg-primary: #f5f5f5;
$border-light: #e8e8e8;

.patients-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(56px + env(safe-area-inset-bottom));
}

.page-header {
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;

  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 4px;
  }

  .header-stats {
    .stat-item {
      font-size: 13px;
      color: $text-tertiary;
    }
  }
}

.search-bar {
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;

  .search-input {
    position: relative;
    display: flex;
    align-items: center;

    .search-icon {
      position: absolute;
      left: 12px;
      width: 18px;
      height: 18px;
      color: $text-tertiary;
    }

    input {
      flex: 1;
      height: 40px;
      padding: 0 40px;
      background: #F5F5F5;
      border: none;
      border-radius: 20px;
      font-size: 14px;
      outline: none;

      &::placeholder {
        color: $text-tertiary;
      }
    }

    .clear-btn {
      position: absolute;
      right: 12px;
      width: 20px;
      height: 20px;
      padding: 0;
      background: none;
      border: none;
      color: $text-tertiary;
      cursor: pointer;

      svg {
        width: 100%;
        height: 100%;
      }
    }
  }
}

.filter-section {
  background: #fff;
  border-bottom: 1px solid $border-light;
  padding: 12px 16px;
}

.filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .filter-tab {
    padding: 6px 14px;
    border-radius: 16px;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;

    &:active {
      transform: scale(0.95);
    }

    &.manage-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      padding: 0;
      background: #F5F5F5;
      color: $text-secondary;

      svg {
        width: 16px;
        height: 16px;
      }

      &:hover {
        background: #E0E0E0;
      }
    }
  }
}

.patients-list {
  padding: 12px;
}

.patient-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.99);
  }

  .card-main {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid $border-light;
  }

  .patient-avatar {
    position: relative;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: $primary-50;
    color: $primary;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .patient-info {
    flex: 1;
    min-width: 0;

    .info-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .patient-name {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }

      .patient-gender {
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;

        &.男 {
          background: #E6F7FF;
          color: #1890FF;
        }

        &.女 {
          background: #FFF0F6;
          color: #EB2F96;
        }
      }

      .patient-age {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .disease-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 8px;

      .disease-tag {
        padding: 3px 8px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }

      .other-tag {
        padding: 3px 8px;
        background: #F5F5F5;
        color: $text-secondary;
        border-radius: 4px;
        font-size: 11px;
      }
    }

    .info-footer {
      display: flex;
      gap: 12px;
      font-size: 12px;
      color: $text-tertiary;
    }
  }

  .card-actions {
    display: flex;
    gap: 8px;

    .action-btn {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      padding: 8px;
      background: #F5F5F5;
      border: none;
      border-radius: 8px;
      font-size: 13px;
      color: $text-secondary;
      cursor: pointer;
      transition: all 0.2s;

      svg {
        width: 14px;
        height: 14px;
      }

      &.primary {
        background: $primary-50;
        color: $primary;
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;

  .empty-icon {
    width: 80px;
    height: 80px;
    background: #F5F5F5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;

    svg {
      width: 40px;
      height: 40px;
      color: $text-tertiary;
    }
  }

  .empty-text {
    font-size: 14px;
    color: $text-secondary;
  }
}

// 弹窗样式
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  width: 100%;
  max-width: 480px;
  border-radius: 24px 24px 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid $border-light;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
  }

  .close-btn {
    width: 32px;
    height: 32px;
    background: #F5F5F5;
    border: none;
    border-radius: 50%;
    font-size: 20px;
    color: $text-secondary;
    cursor: pointer;
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
}

.group-list {
  margin-bottom: 24px;

  .group-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px;
    background: #F5F5F5;
    border-radius: 12px;
    margin-bottom: 8px;

    .group-info {
      display: flex;
      align-items: center;
      gap: 8px;
      flex: 1;
      min-width: 0;

      .group-color {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        flex-shrink: 0;
      }

      .group-name {
        font-size: 14px;
        color: $text-primary;
        font-weight: 500;
      }

      .group-keywords {
        font-size: 12px;
        color: $text-tertiary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .delete-btn {
      width: 28px;
      height: 28px;
      background: #FFEBEE;
      border: none;
      border-radius: 50%;
      color: #D32F2F;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;

      svg {
        width: 16px;
        height: 16px;
      }
    }
  }

  .empty-groups {
    text-align: center;
    padding: 24px;
    color: $text-tertiary;
    font-size: 14px;
  }
}

.add-group {
  .form-item {
    margin-bottom: 16px;

    label {
      display: block;
      font-size: 13px;
      color: $text-secondary;
      margin-bottom: 8px;
    }

    input {
      width: 100%;
      height: 44px;
      padding: 0 16px;
      border: 1px solid $border-light;
      border-radius: 12px;
      font-size: 14px;
      outline: none;

      &:focus {
        border-color: $primary;
      }
    }
  }

  .btn-add {
    width: 100%;
    height: 44px;
    background: $primary;
    border: none;
    border-radius: 12px;
    color: #fff;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;

    &:disabled {
      background: #E0E0E0;
      color: $text-tertiary;
      cursor: not-allowed;
    }
  }
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 20px calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid $border-light;

  .btn-cancel,
  .btn-save {
    flex: 1;
    height: 48px;
    border: none;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
  }

  .btn-cancel {
    background: #F5F5F5;
    color: $text-secondary;
  }

  .btn-save {
    background: $primary;
    color: #fff;
  }
}
</style>
