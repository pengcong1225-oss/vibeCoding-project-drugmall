<template>
  <div class="settings-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">设置</span>
    </div>

    <div class="settings-content">
      <!-- 账户安全 -->
      <div class="settings-group">
        <h3 class="group-title">账户安全</h3>
        <div class="settings-list">
          <div class="setting-item" @click="handleRealName">
            <div class="item-left">
              <el-icon><User /></el-icon>
              <span>实名认证</span>
            </div>
            <div class="item-right">
              <span class="item-value" :class="{ success: isAuthenticated }">
                {{ isAuthenticated ? '已认证' : '未认证' }}
              </span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item" @click="handleChangePhone">
            <div class="item-left">
              <el-icon><Phone /></el-icon>
              <span>修改手机号</span>
            </div>
            <div class="item-right">
              <span class="item-value">{{ maskedPhone }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="settings-group">
        <h3 class="group-title">通知设置</h3>
        <div class="settings-list">
          <div class="setting-item">
            <div class="item-left">
              <el-icon><Bell /></el-icon>
              <span>推送通知</span>
            </div>
            <el-switch v-model="pushEnabled" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon><ChatDotSquare /></el-icon>
              <span>问诊消息提醒</span>
            </div>
            <el-switch v-model="inquiryNotifyEnabled" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon><Van /></el-icon>
              <span>物流通知</span>
            </div>
            <el-switch v-model="logisticsNotifyEnabled" />
          </div>
        </div>
      </div>

      <!-- 其他 -->
      <div class="settings-group">
        <h3 class="group-title">其他</h3>
        <div class="settings-list">
          <div class="setting-item" @click="handleClearCache">
            <div class="item-left">
              <el-icon><Delete /></el-icon>
              <span>清除缓存</span>
            </div>
            <div class="item-right">
              <span class="item-value">{{ cacheSize }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item" @click="handleAbout">
            <div class="item-left">
              <el-icon><InfoFilled /></el-icon>
              <span>关于药康购</span>
            </div>
            <div class="item-right">
              <span class="item-value">v1.0.0</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 退出登录 -->
      <div v-if="isLoggedIn" class="logout-section">
        <el-button type="danger" size="large" round plain class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, User, Phone, Bell, ChatDotSquare, Van, Delete, InfoFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ROUTES } from '@/constants/routes'

const router = useRouter()
const userStore = useUserStore()

const pushEnabled = ref(true)
const inquiryNotifyEnabled = ref(true)
const logisticsNotifyEnabled = ref(true)
const cacheSize = ref('12.3 MB')

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAuthenticated = computed(() => userStore.userInfo?.isAuthenticated || false)
const maskedPhone = computed(() => {
  const phone = userStore.userInfo?.phone || ''
  if (phone.length >= 7) {
    return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
  }
  return phone || '未绑定'
})

const goBack = () => {
  router.back()
}

const handleRealName = () => {
  ElMessage.info('实名认证功能开发中')
}

const handleChangePhone = () => {
  ElMessage.info('修改手机号功能开发中')
}

const handleClearCache = () => {
  ElMessageBox.confirm('确定要清除缓存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cacheSize.value = '0 B'
    ElMessage.success('缓存已清除')
  }).catch(() => {})
}

const handleAbout = () => {
  ElMessage.info('药康购 v1.0.0 - 24小时送药上门')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push(ROUTES.HOME)
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.settings-page {
  min-height: 100vh;
  background: $bg-primary;
}

.nav-header {
  display: flex;
  align-items: center;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
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
    margin-right: 36px;
  }
}

.settings-content {
  padding: $spacing-md;
}

.settings-group {
  margin-bottom: $spacing-lg;

  .group-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 $spacing-md 0;
    padding-left: $spacing-xs;
  }

  .settings-list {
    background: $bg-white;
    border-radius: $radius-lg;
    overflow: hidden;
    box-shadow: $shadow-sm;

    .setting-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-md $spacing-lg;
      border-bottom: 1px solid $border-light;
      cursor: pointer;
      transition: background 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: $bg-gray;
      }

      .item-left {
        display: flex;
        align-items: center;
        gap: $spacing-md;
        font-size: $font-md;
        color: $text-primary;

        .el-icon {
          font-size: 18px;
          color: $primary;
        }
      }

      .item-right {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .item-value {
          font-size: $font-sm;
          color: $text-tertiary;

          &.success {
            color: $success;
          }
        }

        .el-icon {
          font-size: 14px;
          color: $text-tertiary;
        }
      }
    }
  }
}

.logout-section {
  margin-top: $spacing-xl;
  padding: 0 $spacing-md;

  .logout-btn {
    width: 100%;
  }
}
</style>
