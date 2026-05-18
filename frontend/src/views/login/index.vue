<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Close, FirstAidKit, ChatDotRound, CircleCheck, View, Hide } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { sendVerifyCode } from '@/api/modules/user'
import { ROUTES } from '@/constants/routes'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 登录方式: phone - 手机号登录, password - 密码登录
const loginType = ref<'phone' | 'password'>('phone')

// 表单数据
const form = reactive({
  phone: '',
  code: '',
  password: ''
})

// 状态
const phoneError = ref('')
const showPassword = ref(false)
const isLoggingIn = ref(false)

// 验证码倒计时
const countdown = ref(0)
const countdownText = computed(() => {
  return countdown.value > 0 ? `${countdown.value}s` : '获取验证码'
})
const canSendCode = computed(() => {
  return form.phone.length === 11 && countdown.value === 0
})

// 表单验证
const isFormValid = computed(() => {
  if (loginType.value === 'phone') {
    return form.phone.length === 11 && form.code.length === 6
  } else {
    return form.phone.length === 11 && form.password.length >= 6
  }
})

// 发送验证码
const sendCode = async () => {
  if (!canSendCode.value) return
  
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(form.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  try {
    await sendVerifyCode(form.phone)
    ElMessage.success('验证码已发送')
  } catch (error) {
    ElMessage.error('验证码发送失败，请重试')
    return
  }
  
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 手机号验证
const validatePhone = () => {
  if (!form.phone) {
    phoneError.value = ''
    return
  }
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(form.phone)) {
    phoneError.value = '请输入正确的11位手机号'
  } else {
    phoneError.value = ''
  }
}

// 登录
const handleLogin = async () => {
  if (!isFormValid.value) return

  isLoggingIn.value = true
  try {
    if (loginType.value === 'phone') {
      // 手机号+验证码登录
      await userStore.login(form.phone, form.code)
    } else {
      // 手机号+密码登录（模拟）
      await userStore.login(form.phone, 'password')
    }
    
    ElMessage.success('登录成功')
    
    // 跳转到指定页面或首页
    const redirect = route.query.redirect as string
    if (redirect) {
      router.replace(redirect)
    } else {
      router.replace(ROUTES.HOME)
    }
  } catch (error) {
    ElMessage.error('登录失败，请重试')
  } finally {
    isLoggingIn.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 切换登录方式
const toggleLoginType = () => {
  loginType.value = loginType.value === 'phone' ? 'password' : 'phone'
  // 清空表单
  form.code = ''
  form.password = ''
}

// 微信登录
const handleWechatLogin = () => {
  ElMessage.info('微信登录功能开发中')
}
</script>

<template>
  <div class="login-page">
    <!-- 头部 -->
    <div class="header">
      <div class="back-btn" @click="goBack">
        <el-icon><Close /></el-icon>
      </div>
    </div>

    <!-- 登录内容 -->
    <div class="login-content">
      <!-- Logo和标题 -->
      <div class="login-header">
        <div class="logo">
          <el-icon :size="48" color="#00B578"><FirstAidKit /></el-icon>
        </div>
        <h1 class="title">药康购</h1>
        <p class="subtitle">24小时送药上门</p>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <!-- 手机号输入 -->
        <div class="form-item">
          <div class="input-wrapper" :class="{ 'has-error': phoneError }">
            <span class="prefix">+86</span>
            <input
              v-model="form.phone"
              type="tel"
              placeholder="请输入手机号"
              maxlength="11"
              class="form-input"
              @blur="validatePhone"
            />
            <el-icon v-if="form.phone.length === 11" class="valid-icon"><CircleCheck /></el-icon>
          </div>
          <span v-if="phoneError" class="error-text">{{ phoneError }}</span>
        </div>

        <!-- 验证码输入 -->
        <div v-if="loginType === 'phone'" class="form-item">
          <div class="input-wrapper code-input">
            <input
              v-model="form.code"
              type="tel"
              placeholder="请输入验证码"
              maxlength="6"
              class="form-input"
            />
            <button
              class="code-btn"
              :disabled="!canSendCode"
              :class="{ 'is-counting': countdown > 0 }"
              @click="sendCode"
            >
              {{ countdownText }}
            </button>
          </div>
        </div>

        <!-- 密码输入 -->
        <div v-else class="form-item">
          <div class="input-wrapper">
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              class="form-input"
            />
            <el-icon class="eye-icon" @click="showPassword = !showPassword">
              <component :is="showPassword ? 'View' : 'Hide'" />
            </el-icon>
          </div>
        </div>

        <!-- 登录按钮 -->
        <button
          class="login-btn"
          :disabled="!isFormValid"
          :class="{ 'is-loading': isLoggingIn }"
          @click="handleLogin"
        >
          <span v-if="!isLoggingIn">登录</span>
          <span v-else class="loading-dots">
            <span></span><span></span><span></span>
          </span>
        </button>

        <!-- 切换登录方式 -->
        <div class="login-type-toggle">
          <span @click="toggleLoginType">
            {{ loginType === 'phone' ? '密码登录' : '验证码登录' }}
          </span>
        </div>

        <!-- 其他登录方式 -->
        <div class="other-login">
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">其他登录方式</span>
            <span class="divider-line"></span>
          </div>
          <div class="login-methods">
            <button class="login-method-btn" @click="handleWechatLogin">
              <el-icon :size="28"><ChatDotRound /></el-icon>
              <span>微信登录</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 协议提示 -->
      <div class="agreement">
        <p>
          登录即表示您同意
          <a href="#">《用户协议》</a>
          和
          <a href="#">《隐私政策》</a>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.login-page {
  min-height: 100vh;
  background: $bg-white;
}

// 头部
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: $spacing-md;
  padding-top: calc($safe-area-top + $spacing-md);

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    color: $text-secondary;
    transition: background 0.2s ease;

    &:hover {
      background: $bg-gray;
    }
  }
}

// 登录内容
.login-content {
  padding: 0 $spacing-xl;
  padding-bottom: $spacing-xl;
}

// 登录头部
.login-header {
  text-align: center;
  margin-bottom: $spacing-xxl;

  .logo {
    width: 80px;
    height: 80px;
    margin: 0 auto $spacing-md;
    background: rgba($primary, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .title {
    font-size: 28px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-xs;
  }

  .subtitle {
    font-size: $font-md;
    color: $text-tertiary;
  }
}

// 登录表单
.login-form {
  .form-item {
    margin-bottom: $spacing-md;

    .input-wrapper {
      display: flex;
      align-items: center;
      height: 48px;
      padding: 0 $spacing-md;
      background: $bg-gray;
      border-radius: $radius-lg;
      border: 1px solid transparent;
      transition: all 0.2s ease;

      &:focus-within {
        background: $bg-white;
        border-color: $primary;
      }

      &.has-error {
        border-color: $error;
        background: rgba($error, 0.05);

        &:focus-within {
          border-color: $error;
        }
      }

      .prefix {
        font-size: $font-md;
        color: $text-secondary;
        margin-right: $spacing-sm;
        padding-right: $spacing-sm;
        border-right: 1px solid $border-light;
      }

      .form-input {
        flex: 1;
        height: 100%;
        border: none;
        background: transparent;
        font-size: $font-md;
        color: $text-primary;
        outline: none;

        &::placeholder {
          color: $text-tertiary;
        }
      }

      .valid-icon {
        color: $success;
        font-size: 18px;
        margin-left: $spacing-sm;
      }

      .eye-icon {
        color: $text-tertiary;
        font-size: 18px;
        margin-left: $spacing-sm;
        cursor: pointer;
        transition: color 0.2s;

        &:hover {
          color: $text-secondary;
        }
      }

      &.code-input {
        padding-right: $spacing-xs;

        .form-input {
          padding-right: $spacing-sm;
        }

        .code-btn {
          height: 36px;
          padding: 0 $spacing-md;
          background: transparent;
          border: none;
          border-left: 1px solid $border-light;
          color: $primary;
          font-size: $font-sm;
          cursor: pointer;
          white-space: nowrap;
          transition: all 0.2s ease;

          &:hover:not(:disabled) {
            opacity: 0.8;
          }

          &:disabled {
            color: $text-tertiary;
            cursor: not-allowed;
          }

          &.is-counting {
            color: $text-tertiary;
            font-size: $font-xs;
          }
        }
      }
    }

    .error-text {
      display: block;
      margin-top: $spacing-xs;
      font-size: $font-xs;
      color: $error;
      padding-left: $spacing-sm;
    }
  }

  .login-btn {
    width: 100%;
    height: 48px;
    margin-top: $spacing-lg;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    border: none;
    border-radius: $radius-lg;
    color: $text-white;
    font-size: $font-lg;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover:not(:disabled) {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    &:disabled {
      background: $text-tertiary;
      cursor: not-allowed;
      opacity: 0.6;
    }

    &.is-loading {
      cursor: not-allowed;
      opacity: 0.8;

      .loading-dots {
        display: flex;
        gap: 6px;
        align-items: center;

        span {
          width: 8px;
          height: 8px;
          background: $text-white;
          border-radius: 50%;
          animation: dotPulse 1.4s ease-in-out infinite;

          &:nth-child(1) { animation-delay: 0s; }
          &:nth-child(2) { animation-delay: 0.2s; }
          &:nth-child(3) { animation-delay: 0.4s; }
        }
      }
    }
  }

  @keyframes dotPulse {
    0%, 80%, 100% {
      transform: scale(0);
      opacity: 0.5;
    }
    40% {
      transform: scale(1);
      opacity: 1;
    }
  }

  .login-type-toggle {
    text-align: center;
    margin-top: $spacing-lg;

    span {
      font-size: $font-sm;
      color: $primary;
      cursor: pointer;
      transition: opacity 0.2s ease;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .other-login {
    margin-top: $spacing-xxl;

    .divider {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-lg;

      .divider-line {
        flex: 1;
        height: 1px;
        background: $border-light;
      }

      .divider-text {
        font-size: $font-xs;
        color: $text-tertiary;
        white-space: nowrap;
      }
    }

    .login-methods {
      display: flex;
      justify-content: center;
      gap: $spacing-xl;

      .login-method-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: $spacing-xs;
        background: transparent;
        border: none;
        cursor: pointer;
        padding: $spacing-sm;
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-2px);
        }

        &:active {
          transform: scale(0.95);
        }

        .el-icon {
          color: #07C160;
        }

        span {
          font-size: $font-xs;
          color: $text-secondary;
        }
      }
    }
  }
}

// 协议
.agreement {
  text-align: center;
  margin-top: $spacing-xxl;
  padding: 0 $spacing-md;

  p {
    font-size: $font-xs;
    color: $text-tertiary;
    line-height: 1.6;

    a {
      color: $primary;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
