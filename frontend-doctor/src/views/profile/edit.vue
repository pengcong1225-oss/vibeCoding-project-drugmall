<template>
  <div class="edit-profile-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">编辑资料</h1>
      <button class="save-btn" @click="handleSave">保存</button>
    </header>

    <!-- 头像编辑 -->
    <div class="avatar-section">
      <div class="avatar-wrapper">
        <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=doctor" alt="头像" />
        <div class="avatar-edit">
          <span>更换头像</span>
        </div>
      </div>
    </div>

    <!-- 表单 -->
    <div class="form-section">
      <div class="form-item">
        <label>姓名</label>
        <input v-model="form.name" type="text" placeholder="请输入姓名" />
      </div>
      <div class="form-item">
        <label>职称</label>
        <select v-model="form.title">
          <option value="">请选择职称</option>
          <option value="主任医师">主任医师</option>
          <option value="副主任医师">副主任医师</option>
          <option value="主治医师">主治医师</option>
          <option value="住院医师">住院医师</option>
        </select>
      </div>
      <div class="form-item">
        <label>执业医院</label>
        <input v-model="form.hospital" type="text" placeholder="请输入执业医院" />
      </div>
      <div class="form-item">
        <label>科室</label>
        <input v-model="form.department" type="text" placeholder="请输入科室" />
      </div>
      <div class="form-item">
        <label>擅长领域</label>
        <textarea v-model="form.specialty" placeholder="请输入擅长领域，用逗号分隔" rows="3"></textarea>
      </div>
      <div class="form-item">
        <label>个人简介</label>
        <textarea v-model="form.introduction" placeholder="请输入个人简介" rows="4"></textarea>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  name: '张医生',
  title: '副主任医师',
  hospital: '北京协和医院',
  department: '心内科',
  specialty: '冠心病、高血压、心律失常',
  introduction: '从事心血管内科临床工作20余年，擅长冠心病、高血压、心律失常等疾病的诊治。'
})

const goBack = () => {
  router.back()
}

const handleSave = () => {
  alert('保存成功！')
  router.back()
}
</script>

<style scoped lang="scss">
.edit-profile-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: #333;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: #f5f5f5;
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
    color: #333;
    margin: 0;
  }

  .save-btn {
    width: 40px;
    height: 40px;
    background: none;
    border: none;
    font-size: 14px;
    color: #00B578;
    cursor: pointer;
  }
}

.avatar-section {
  background: #fff;
  padding: 24px;
  display: flex;
  justify-content: center;
  margin-bottom: 12px;

  .avatar-wrapper {
    position: relative;

    img {
      width: 80px;
      height: 80px;
      border-radius: 50%;
    }

    .avatar-edit {
      position: absolute;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
      background: #00B578;
      color: #fff;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      white-space: nowrap;
    }
  }
}

.form-section {
  background: #fff;
  padding: 0 16px;

  .form-item {
    padding: 16px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    label {
      display: block;
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
    }

    input, select, textarea {
      width: 100%;
      border: none;
      outline: none;
      font-size: 15px;
      color: #333;
      background: transparent;

      &::placeholder {
        color: #ccc;
      }
    }

    textarea {
      resize: none;
    }

    select {
      appearance: none;
      background: transparent;
    }
  }
}
</style>
