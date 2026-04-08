<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'

const route = useRoute()
const router = useRouter()
const doctorStore = useDoctorStore()

const tabs = [
  {
    name: 'Home',
    path: '/home',
    title: '首页',
    icon: 'home',
    badge: computed(() => doctorStore.todayStats.pending > 0 ? doctorStore.todayStats.pending : 0)
  },
  {
    name: 'Consultation',
    path: '/consultation',
    title: '问诊管理',
    icon: 'stethoscope',
    badge: computed(() => doctorStore.unreadCount > 0 ? doctorStore.unreadCount : 0)
  },
  {
    name: 'Patients',
    path: '/patients',
    title: '患者管理',
    icon: 'users'
  },
  {
    name: 'Profile',
    path: '/profile',
    title: '我的',
    icon: 'user'
  }
]

const activeTab = computed(() => {
  return tabs.findIndex(tab => route.path.startsWith(tab.path))
})

const switchTab = (index: number) => {
  const tab = tabs[index]
  if (tab && tab.path !== route.path) {
    router.push(tab.path)
  }
}
</script>

<template>
  <div class="tabbar">
    <div class="tabbar-content">
      <div
        v-for="(tab, index) in tabs"
        :key="tab.name"
        class="tabbar-item"
        :class="{ active: index === activeTab }"
        @click="switchTab(index)"
      >
        <div class="tabbar-icon">
          <svg v-if="tab.icon === 'home'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
          <svg v-else-if="tab.icon === 'stethoscope'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4.8 2.3A.3.3 0 1 0 5 2H4a2 2 0 0 0-2 2v5a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6V4a2 2 0 0 0-2-2h-1a.2.2 0 1 0 .3.3"></path>
            <path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4"></path>
            <circle cx="20" cy="10" r="2"></circle>
          </svg>
          <svg v-else-if="tab.icon === 'users'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span v-if="typeof tab.badge === 'function' ? tab.badge() : tab.badge" class="tabbar-badge">
            {{ typeof tab.badge === 'function' ? tab.badge() : tab.badge }}
          </span>
        </div>
        <span class="tabbar-label">{{ tab.title }}</span>
      </div>
    </div>
    <div class="tabbar-safe-area"></div>
  </div>
</template>

<style lang="scss" scoped>
.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  background: #fff;
  border-top: 1px solid $border-light;
  
  .tabbar-content {
    display: flex;
    height: 56px;
    padding: 4px 0;
  }
  
  .tabbar-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;
    
    &.active {
      .tabbar-icon {
        color: $primary;
      }
      
      .tabbar-label {
        color: $primary;
      }
    }
  }
  
  .tabbar-icon {
    position: relative;
    width: 24px;
    height: 24px;
    color: $text-tertiary;
    transition: color 0.2s;
    
    svg {
      width: 100%;
      height: 100%;
    }
  }
  
  .tabbar-badge {
    position: absolute;
    top: -4px;
    right: -6px;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    font-size: 10px;
    font-weight: 600;
    line-height: 16px;
    color: #fff;
    text-align: center;
    background-color: $error;
    border-radius: 8px;
  }
  
  .tabbar-label {
    margin-top: 2px;
    font-size: 10px;
    color: $text-tertiary;
    transition: color 0.2s;
  }
  
  .tabbar-safe-area {
    height: env(safe-area-inset-bottom);
    background: #fff;
  }
}
</style>
