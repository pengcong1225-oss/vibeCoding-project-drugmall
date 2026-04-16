<template>
  <div class="feedback-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">意见反馈</span>
    </div>

    <div class="feedback-content">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
        <el-form-item label="反馈类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio value="bug">功能异常</el-radio>
            <el-radio value="suggestion">功能建议</el-radio>
            <el-radio value="complaint">投诉</el-radio>
            <el-radio value="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您的问题或建议，以便我们更好地改进..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系方式（选填）">
          <el-input v-model="formData.contact" placeholder="请输入手机号或邮箱，方便我们回复您" />
        </el-form-item>
      </el-form>

      <el-button
        type="primary"
        size="large"
        round
        :loading="submitting"
        class="submit-btn"
        @click="handleSubmit"
      >
        提交反馈
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = reactive({
  type: 'suggestion',
  content: '',
  contact: ''
})

const formRules: FormRules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '反馈内容不少于10个字', trigger: 'blur' }
  ]
}

const goBack = () => {
  router.back()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true

    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('感谢您的反馈，我们会尽快处理')
    router.back()
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error('提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.feedback-page {
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

.feedback-content {
  padding: $spacing-lg;

  .submit-btn {
    width: 100%;
    margin-top: $spacing-lg;
  }
}
</style>
