<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('all')
const loading = ref(false)

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'system', label: '系统' },
  { key: 'business', label: '业务' }
]

const messages = ref([
  {
    id: '1',
    type: 'system',
    title: '系统维护通知',
    content: '系统将于今晚22:00-24:00进行维护升级，期间可能影响正常使用，请提前安排。',
    time: '2024-04-14 10:30',
    isRead: false
  },
  {
    id: '2',
    type: 'business',
    title: '新患者预约',
    content: '您有一条新的图文问诊预约，患者：李女士，预约时间：2024-04-14 14:00',
    time: '2024-04-14 09:15',
    isRead: false
  },
  {
    id: '3',
    type: 'system',
    title: '账户安全提醒',
    content: '您的账户于今日08:30在新设备登录，如非本人操作，请及时修改密码。',
    time: '2024-04-14 08:35',
    isRead: true
  },
  {
    id: '4',
    type: 'business',
    title: '处方审核通过',
    content: '您开具的处方（编号：RX20240414001）已通过审核，患者已收到处方信息。',
    time: '2024-04-13 16:45',
    isRead: true
  },
  {
    id: '5',
    type: 'system',
    title: '收入结算通知',
    content: '您上月的问诊收入已结算，结算金额：¥3,580.00，已转入您的账户余额。',
    time: '2024-04-13 10:00',
    isRead: true
  },
  {
    id: '6',
    type: 'business',
    title: '患者评价提醒',
    content: '患者王先生对您的服务进行了评价，评分：5星，评价内容：医生很专业，解答详细。',
    time: '2024-04-12 18:20',
    isRead: true
  }
])

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') return messages.value
  return messages.value.filter(m => m.type === activeTab.value)
})

const unreadCount = computed(() => {
  return messages.value.filter(m => !m.isRead).length
})

const goBack = () => {
  router.back()
}

const markAsRead = (id: string) => {
  const msg = messages.value.find(m => m.id === id)
  if (msg) {
    msg.isRead = true
  }
}

const markAllAsRead = () => {
  messages.value.forEach(m => m.isRead = true)
}

const deleteMessage = (id: string) => {
  const index = messages.value.findIndex(m => m.id === id)
  if (index > -1) {
    messages.value.splice(index, 1)
  }
}

const getMessageIcon = (type: string) => {
  return type === 'system' ? '🔔' : '📋'
}

const getMessageClass = (type: string) => {
  return type === 'system' ? 'message-system' : 'message-business'
}
</script>

<template>
  <div class="messages-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">消息中心</h1>
      <button class="read-all-btn" @click="markAllAsRead" v-if="unreadCount > 0">
        全部已读
      </button>
      <div v-else class="header-right"></div>
    </header>

    <!-- 标签切换 -->
    <div class="tab-bar">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-item', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span v-if="tab.key === 'all' && unreadCount > 0" class="badge">{{ unreadCount }}</span>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="message-list">
      <div
        v-for="message in filteredMessages"
        :key="message.id"
        :class="['message-item', getMessageClass(message.type), { unread: !message.isRead }]"
        @click="markAsRead(message.id)"
      >
        <div class="message-icon">{{ getMessageIcon(message.type) }}</div>
        <div class="message-content">
          <div class="message-header">
            <span class="message-title">{{ message.title }}</span>
            <span class="message-time">{{ message.time }}</span>
          </div>
          <p class="message-text">{{ message.content }}</p>
        </div>
        <div class="message-actions">
          <span v-if="!message.isRead" class="unread-dot"></span>
          <button class="delete-btn" @click.stop="deleteMessage(message.id)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredMessages.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p class="empty-text">暂无消息</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$messages-primary: #2E7D32;
$messages-primary-light: #4CAF50;
$messages-primary-bg: #E8F5E9;
$messages-text-primary: #333;
$messages-text-secondary: #666;
$messages-text-tertiary: #999;
$messages-bg-gray: #f5f5f5;
$messages-border-light: #e8e8e8;

.messages-page {
  min-height: 100vh;
  background: $messages-bg-gray;
}

// 顶部导航
.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $messages-border-light;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: $messages-text-primary;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: $messages-bg-gray;
    }

    svg {
      width: 24px;
      height: 24px;
    }
  }

  .page-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: $messages-text-primary;
    margin: 0;
  }

  .read-all-btn {
    width: 80px;
    height: 32px;
    background: $messages-primary-bg;
    border: none;
    border-radius: 16px;
    font-size: 13px;
    color: $messages-primary;
    cursor: pointer;
    transition: all 0.2s;

    &:active {
      background: $messages-primary;
      color: #fff;
    }
  }

  .header-right {
    width: 80px;
  }
}

// 标签栏
.tab-bar {
  display: flex;
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid $messages-border-light;

  .tab-item {
    position: relative;
    flex: 1;
    padding: 16px 0;
    text-align: center;
    font-size: 15px;
    color: $messages-text-secondary;
    cursor: pointer;
    transition: all 0.2s;

    &.active {
      color: $messages-primary;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 24px;
        height: 3px;
        background: $messages-primary;
        border-radius: 2px;
      }
    }

    .badge {
      position: absolute;
      top: 12px;
      right: 20%;
      min-width: 18px;
      height: 18px;
      padding: 0 5px;
      background: #D32F2F;
      color: #fff;
      border-radius: 9px;
      font-size: 11px;
      font-weight: 600;
      line-height: 18px;
    }
  }
}

// 消息列表
.message-list {
  padding: 12px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.99);
  }

  &.unread {
    background: linear-gradient(135deg, #fff 0%, $messages-primary-bg 100%);
    border-left: 3px solid $messages-primary;
  }

  .message-icon {
    width: 40px;
    height: 40px;
    background: $messages-bg-gray;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    flex-shrink: 0;
  }

  .message-content {
    flex: 1;
    min-width: 0;

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .message-title {
        font-size: 15px;
        font-weight: 600;
        color: $messages-text-primary;
      }

      .message-time {
        font-size: 12px;
        color: $messages-text-tertiary;
        flex-shrink: 0;
      }
    }

    .message-text {
      font-size: 14px;
      color: $messages-text-secondary;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  .message-actions {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;

    .unread-dot {
      width: 8px;
      height: 8px;
      background: #D32F2F;
      border-radius: 50%;
    }

    .delete-btn {
      width: 28px;
      height: 28px;
      background: $messages-bg-gray;
      border: none;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $messages-text-tertiary;
      cursor: pointer;
      opacity: 0;
      transition: all 0.2s;

      svg {
        width: 14px;
        height: 14px;
      }
    }
  }

  &:hover .delete-btn {
    opacity: 1;
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;

  .empty-icon {
    font-size: 64px;
    margin-bottom: 16px;
  }

  .empty-text {
    font-size: 15px;
    color: $messages-text-tertiary;
  }
}
</style>
