<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePatientStore } from '@/stores/patient'
import Tabbar from '@/components/Tabbar/index.vue'

const router = useRouter()
const patientStore = usePatientStore()

const searchKey = ref('')
const activeFilter = ref('all')

const filterOptions = [
  { key: 'all', label: '全部' },
  { key: 'vip', label: 'VIP' },
  { key: 'recent', label: '最近' }
]

const filteredPatients = computed(() => {
  let result = patientStore.patients

  // 搜索过滤
  if (searchKey.value.trim()) {
    const key = searchKey.value.toLowerCase()
    result = result.filter(p => 
      p.name.toLowerCase().includes(key) ||
      p.phone.includes(key)
    )
  }

  // 筛选
  switch (activeFilter.value) {
    case 'vip':
      result = result.filter(p => p.isVip)
      break
    case 'recent':
      // 按最近就诊排序
      result = [...result].sort((a, b) => 
        new Date(b.lastVisit).getTime() - new Date(a.lastVisit).getTime()
      )
      break
  }

  return result
})

const goToDetail = (id: string) => {
  router.push(`/patients/detail/${id}`)
}

const goToRecords = (id: string) => {
  router.push(`/patients/records/${id}`)
}

onMounted(() => {
  patientStore.fetchPatients()
})
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
          placeholder="搜索患者姓名/手机号"
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
    <div class="filter-tabs">
      <div
        v-for="option in filterOptions"
        :key="option.key"
        class="filter-tab"
        :class="{ active: activeFilter === option.key }"
        @click="activeFilter = option.key"
      >
        {{ option.label }}
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
            <span v-if="patient.isVip" class="vip-badge">VIP</span>
          </div>
          <div class="patient-info">
            <div class="info-header">
              <span class="patient-name">{{ patient.name }}</span>
              <span class="patient-gender" :class="patient.gender">{{ patient.gender }}</span>
              <span class="patient-age">{{ patient.age }}岁</span>
            </div>
            <div class="info-tags">
              <span v-for="(tag, index) in patient.tags.slice(0, 3)" :key="index" class="tag">
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

    <Tabbar />
  </div>
</template>

<style lang="scss" scoped>
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

.filter-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;
  
  .filter-tab {
    padding: 6px 16px;
    background: #F5F5F5;
    border-radius: 16px;
    font-size: 13px;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s;
    
    &.active {
      background: $primary;
      color: #fff;
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
    
    .vip-badge {
      position: absolute;
      bottom: -2px;
      right: -2px;
      padding: 2px 6px;
      background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
      color: #fff;
      border-radius: 8px;
      font-size: 9px;
      font-weight: 700;
    }
  }
  
  .patient-info {
    flex: 1;
    min-width: 0;
    
    .info-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 6px;
      
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
    
    .info-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 6px;
      
      .tag {
        padding: 2px 8px;
        background: $primary-50;
        color: $primary;
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
</style>
