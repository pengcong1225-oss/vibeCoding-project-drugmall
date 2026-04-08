<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Fold, Expand, FullScreen, Bell, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps<{
  isCollapse: boolean
}>()

const emit = defineEmits<['toggle-collapse']>()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched
})

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      userStore.logout()
      break
  }
}
</script>

<template>
  <div class="navbar">
    <!-- 左侧 -->
    <div class="left">
      <!-- 折叠按钮 -->
      <div class="toggle-btn" @click="$emit('toggle-collapse')">
        <el-icon size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      
      <!-- 面包屑 -->
      <breadcrumb class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
            {{ item.meta.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </breadcrumb>
    </div>
    
    <!-- 右侧 -->
    <div class="right">
      <!-- 全屏按钮 -->
      <div class="action-item" @click="toggleFullscreen">
        <el-icon size="18"><FullScreen /></el-icon>
      </div>
      
      <!-- 消息通知 -->
      <div class="action-item">
        <el-badge :value="3" class="message-badge">
          <el-icon size="18"><Bell /></el-icon>
        </el-badge>
      </div>
      
      <!-- 用户下拉菜单 -->
      <el-dropdown class="user-dropdown" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
          <span class="username">{{ userStore.userInfo?.username || '管理员' }}</span>
          <el-icon class="arrow-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>系统设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped lang="scss">
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
  .left {
    display: flex;
    align-items: center;
    
    .toggle-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      cursor: pointer;
      border-radius: 4px;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #f5f7fa;
      }
    }
    
    .breadcrumb {
      margin-left: 16px;
    }
  }
  
  .right {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .action-item {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      cursor: pointer;
      border-radius: 4px;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      .message-badge {
        :deep(.el-badge__content) {
          top: 4px;
          right: 4px;
        }
      }
    }
    
    .user-dropdown {
      margin-left: 8px;
      
      .user-info {
        display: flex;
        align-items: center;
        padding: 4px 8px;
        cursor: pointer;
        border-radius: 4px;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: #f5f7fa;
        }
        
        .username {
          margin: 0 8px;
          font-size: 14px;
          color: #606266;
        }
        
        .arrow-icon {
          color: #909399;
        }
      }
    }
  }
}
</style>
