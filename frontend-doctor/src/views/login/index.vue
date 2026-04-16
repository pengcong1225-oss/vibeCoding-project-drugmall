<template>
  <div class="login-page">
    <div class="login-header">
      <div class="logo">
        <span class="logo-icon">🩺</span>
        <span class="logo-text">DrugMall医生端</span>
      </div>
      <p class="subtitle">互联网医疗服务平台</p>
    </div>

    <div class="login-form">
      <h2 class="form-title">医生登录</h2>
      
      <div class="form-item">
        <input 
          v-model="form.phone" 
          type="tel" 
          placeholder="请输入手机号"
          maxlength="11"
        />
      </div>
      
      <div class="form-item">
        <input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入密码"
        />
      </div>

      <button class="login-btn" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <div class="form-options">
        <span class="forgot-password">忘记密码？</span>
        <span class="register">注册账号</span>
      </div>
    </div>

    <div class="login-footer">
      <p>登录即表示同意 <a href="#">用户协议</a> 和 <a href="#">隐私政策</a></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.phone) {
    alert('请输入手机号')
    return
  }
  if (!form.password) {
    alert('请输入密码')
    return
  }
  
  loading.value = true
  try {
    // 模拟登录
    localStorage.setItem('token', 'doctor_token_' + Date.now())
    localStorage.setItem('doctorInfo', JSON.stringify({
      name: '张医生',
      hospital: '北京协和医院',
      department: '心内科'
    }))
    router.push('/home')
  } catch (error) {
    alert('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  display: flex;
  flex-direction: column;
  padding: 40px 24px;
}

.login-header {
  text-align: center;
  margin-top: 60px;
  margin-bottom: 40px;

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 12px;

    .logo-icon {
      font-size: 40px;
    }

    .logo-text {
      font-size: 24px;
      font-weight: 600;
      color: #fff;
    }
  }

  .subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  margin-bottom: 24px;

  .form-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    text-align: center;
    margin-bottom: 24px;
  }

  .form-item {
    margin-bottom: 16px;

    input {
      width: 100%;
      height: 48px;
      padding: 0 16px;
      border: 1px solid #E8E8E8;
      border-radius: 8px;
      font-size: 14px;
      outline: none;
      transition: border-color 0.3s;

      &:focus {
        border-color: #00B578;
      }

      &::placeholder {
        color: #999;
      }
    }
  }

  .login-btn {
    width: 100%;
    height: 48px;
    background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
    border: none;
    border-radius: 8px;
    color: #fff;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: opacity 0.3s;

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    &:active {
      opacity: 0.9;
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
    font-size: 14px;
    color: #666;

    span {
      cursor: pointer;

      &:hover {
        color: #00B578;
      }
    }
  }
}

.login-footer {
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);

  a {
    color: #fff;
    text-decoration: none;
  }
}
</style>
