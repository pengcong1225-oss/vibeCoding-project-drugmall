<template>
  <div class="ai-assistant-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h2 class="page-title">AI智能助手</h2>
      <div class="action-btn" @click="showHistory = true">
        <span>历史记录</span>
      </div>
    </div>

    <!-- 推荐使用场景 -->
    <div v-if="!messages.length" class="recommend-section">
      <div class="ai-avatar">
        <div class="avatar-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="avatar-text">
          <h3>Hi，我是您的AI健康助手</h3>
          <p>描述症状，获取专业用药建议</p>
        </div>
      </div>

      <div class="recommend-list">
        <h4>推荐使用场景</h4>
        <div class="scene-tags">
          <div
              v-for="scene in recommendScenes"
              :key="scene.id"
              class="scene-tag"
              @click="quickQuery(scene.text)"
          >
            {{ scene.text }}
          </div>
        </div>
      </div>

      <div class="tips-section">
        <h4>温馨提示</h4>
        <ul class="tips-list">
          <li>请准确描述症状、持续时间等信息</li>
          <li>AI助手建议仅供参考，处方药仍需医生开具</li>
          <li>紧急情况请立即前往医院就诊</li>
        </ul>
      </div>
    </div>

    <!-- 对话区域 -->
    <div v-else ref="chatContainer" class="chat-section">
      <div class="chat-list">
        <div
            v-for="(message, index) in messages"
            :key="index"
            class="message-item"
            :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
        >
          <div class="message-avatar">
            <img v-if="message.type === 'ai'" src="https://via.placeholder.com/40x40/00b578/ffffff?text=AI" alt="AI">
            <img v-else :src="userAvatar" alt="User">
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(message.content)"></div>
            <div v-if="message.drugs?.length" class="recommend-drugs">
              <h4>推荐药品</h4>
              <div class="drug-list">
                <div
                    v-for="drug in message.drugs"
                    :key="drug.id"
                    class="drug-item"
                    @click="goToDrugDetail(drug.id)"
                >
                  <img :src="drug.image" :alt="drug.name">
                  <div class="drug-info">
                    <span class="name">{{ drug.name }}</span>
                    <span class="price">¥{{ drug.price }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="message.type === 'ai' && message.showActions" class="message-actions">
              <el-button type="primary" size="small" @click="goToInquiry">
                <el-icon><FirstAidKit /></el-icon>
                咨询医生
              </el-button>
              <el-button size="small" @click="addToCart(message.drugs)">
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <div class="input-box">
        <input
            v-model="inputMessage"
            type="text"
            placeholder="描述症状，如：头痛发热怎么办"
            @keyup.enter="sendMessage"
        >
        <button class="send-btn" :disabled="!inputMessage.trim() || isSending" @click="sendMessage">
          <el-icon v-if="isSending"><Loading /></el-icon>
          <el-icon v-else><Promotion /></el-icon>
        </button>
      </div>
      <div class="quick-actions">
        <span v-for="action in quickActions" :key="action" @click="quickQuery(action)">{{ action }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { mockDrugs } from '@/api/mock'

const router = useRouter()
const userStore = useUserStore()

const userAvatar = computed(() => userStore.userInfo?.avatar || 'https://via.placeholder.com/40x40/cccccc/ffffff?text=U')

const messages = ref<Array<{
  type: 'user' | 'ai'
  content: string
  drugs?: any[]
  showActions?: boolean
}>>([])

const inputMessage = ref('')
const isSending = ref(false)
const showHistory = ref(false)
const chatContainer = ref<HTMLElement>()

const recommendScenes = [
  { id: 1, text: '感冒发烧' },
  { id: 2, text: '头痛发热' },
  { id: 3, text: '腹痛腹泻' },
  { id: 4, text: '皮肤过敏' },
  { id: 5, text: '咳嗽多痰' },
  { id: 6, text: '睡眠不佳' }
]

const quickActions = ['感冒', '发烧', '头痛', '腹泻', '过敏', '失眠']

const formatMessage = (content: string) => {
  return content.replace(/\n/g, '<br>')

const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text || isSending.value) return

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: text
  })

  inputMessage.value = ''
  isSending.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 模拟AI响应
  setTimeout(() => {
    const response = generateAIResponse(text)
    messages.value.push({
      type: 'ai',
      content: response.content,
      drugs: response.drugs,
      showActions: Boolean(response.drugs?.length)
    })

    isSending.value = false

    nextTick(() => {
      scrollToBottom()
    })
  }, 1500)
}

const generateAIResponse = (userInput: string) => {
  const input = userInput.toLowerCase()

  if (input.includes('感冒') || input.includes('发烧') || input.includes('头痛')) {
    return {
      content: `根据您描述的症状，可能是上呼吸道感染。建议您：\n\n1. 多休息，多喝水\n2. 监测体温变化\n3. 如持续发热请及时就医\n\n以下药品可能对您有帮助：`,
      drugs: [mockDrugs[1], mockDrugs[2]]
    }
  }

  if (input.includes('腹泻') || input.includes('肠胃') || input.includes('腹痛')) {
    return {
      content: `腹泻可能由多种原因引起。建议您：\n\n1. 注意饮食卫生，少吃生冷食物\n2. 补充电解质，避免脱水\n3. 如腹泻严重或持续3天以上，建议及时就医\n\n以下药品可能对您有帮助：`,
      drugs: [mockDrugs[4]]
    }
  }

  return {
    content: `感谢您的咨询。为更准确判断您的情况，建议：\n\n1. 详细描述症状（部位、性质、持续时间）\n2. 是否有过敏史或既往病史\n3. 可在线咨询专业医生获取个性化建议\n\n如需用药指导，请说明具体症状，我会为您推荐合适的非处方药。`,
    drugs: []
  }
}

const quickQuery = (text: string) => {
  inputMessage.value = text
  sendMessage()
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const goBack = () => router.back()
const goToDrugDetail = (id: string) => router.push(`/drug/${id}`)
const goToInquiry = () => router.push('/inquiry')

const addToCart = (drugs: any[] | undefined) => {
  if (!drugs?.length) return

  drugs.forEach(drug => {
    ElMessage.success(`${drug.name} 已加入购物车`)
  })

  // 跳转到购物车
  setTimeout(() => {
    router.push('/cart')
  }, 1500)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.ai-assistant-page {
  min-height: 100vh;
  background: $bg-primary;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  color: $text-white;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }

  .page-title {
    font-size: $font-lg;
    font-weight: 600;
  }

  .action-btn {
    font-size: $font-sm;
    padding: $spacing-sm $spacing-md;
    cursor: pointer;
  }
}

.recommend-section {
  flex: 1;
  padding: $spacing-lg;

  .ai-avatar {
    display: flex;
    align-items: center;
    margin-bottom: $spacing-xl;

    .avatar-icon {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      color: $text-white;
      margin-right: $spacing-md;
    }

    .avatar-text {
      h3 {
        font-size: $font-lg;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 4px;
      }

      p {
        font-size: $font-sm;
        color: $text-tertiary;
      }
    }
  }

  .recommend-list {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;
    margin-bottom: $spacing-md;

    h4 {
      font-size: $font-md;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: $spacing-md;
    }

    .scene-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-sm;

      .scene-tag {
        padding: $spacing-sm $spacing-md;
        background: rgba($primary, 0.1);
        color: $primary;
        border-radius: $radius-xl;
        font-size: $font-sm;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: $primary;
          color: $text-white;
        }
      }
    }
  }

  .tips-section {
    background: $bg-white;
    border-radius: $radius-lg;
    padding: $spacing-md;

    h4 {
      font-size: $font-md;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: $spacing-md;
    }

    .tips-list {
      list-style: none;

      li {
        font-size: $font-sm;
        color: $text-secondary;
        padding: $spacing-xs 0;
        padding-left: $spacing-lg;
        position: relative;

        &::before {
          content: '✓';
          position: absolute;
          left: 0;
          color: $primary;
        }
      }
    }
  }
}

.chat-section {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-md;

  .chat-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-lg;
  }

  .message-item {
    display: flex;
    gap: $spacing-sm;

    .message-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .message-content {
      max-width: 75%;

      .message-text {
        padding: $spacing-md;
        border-radius: $radius-lg;
        font-size: $font-md;
        line-height: 1.6;
      }

      .recommend-drugs {
        margin-top: $spacing-sm;
        background: $bg-white;
        border-radius: $radius-lg;
        padding: $spacing-md;

        h4 {
          font-size: $font-sm;
          color: $text-secondary;
          margin-bottom: $spacing-sm;
        }

        .drug-list {
          display: flex;
          flex-direction: column;
          gap: $spacing-sm;

          .drug-item {
            display: flex;
            align-items: center;
            gap: $spacing-sm;
            padding: $spacing-sm;
            background: $bg-primary;
            border-radius: $radius-md;
            cursor: pointer;

            img {
              width: 50px;
              height: 50px;
              border-radius: $radius-sm;
              object-fit: cover;
            }

            .drug-info {
              flex: 1;
              display: flex;
              flex-direction: column;

              .name {
                font-size: $font-sm;
                color: $text-primary;
                @extend .text-ellipsis;
              }

              .price {
                font-size: $font-sm;
                color: $error;
                font-weight: 500;
              }
            }
          }
        }
      }

      .message-actions {
        display: flex;
        gap: $spacing-sm;
        margin-top: $spacing-sm;
      }
    }

    &.ai-message {
      .message-text {
        background: $bg-white;
        color: $text-primary;
        border-top-left-radius: $radius-sm;
      }
    }

    &.user-message {
      flex-direction: row-reverse;

      .message-text {
        background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
        color: $text-white;
        border-top-right-radius: $radius-sm;
      }
    }
  }
}

.input-section {
  background: $bg-white;
  padding: $spacing-md;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);

  .input-box {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    background: $bg-primary;
    border-radius: $radius-xl;
    padding: $spacing-sm $spacing-md;

    input {
      flex: 1;
      border: none;
      background: transparent;
      font-size: $font-md;
      outline: none;

      &::placeholder {
        color: $text-tertiary;
      }
    }

    .send-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
      color: $text-white;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s;

      &:disabled {
        background: $text-tertiary;
        cursor: not-allowed;
      }
    }
  }

  .quick-actions {
    display: flex;
    gap: $spacing-md;
    margin-top: $spacing-sm;
    padding-left: $spacing-sm;
    overflow-x: auto;

    span {
      font-size: $font-sm;
      color: $primary;
      white-space: nowrap;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
