<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Plus } from '@element-plus/icons-vue'
import { getBasicSettings, saveBasicSettings } from '@/api/settings'

const loading = ref(false)

// 表单数据
const formRef = ref()
const formData = ref({
  siteName: '',
  siteLogo: '',
  siteIcon: '',
  siteDescription: '',
  siteKeywords: '',
  servicePhone: '',
  serviceEmail: '',
  companyAddress: '',
  workTime: '',
  icp: '',
  police: '',
  business: '',
  copyright: ''
})

// 加载配置
const loadSettings = async () => {
  loading.value = true
  try {
    const data = await getBasicSettings()
    formData.value = { ...formData.value, ...data }
  } catch (error) {
    console.error('获取基础配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存配置
const handleSave = async () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await saveBasicSettings(formData.value)
        ElMessage.success('配置保存成功')
      } catch (error) {
        console.error('保存配置失败:', error)
        ElMessage.error('配置保存失败')
      }
    }
  })
}

onMounted(() => {
  loadSettings()
})

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  ElMessage.info('已重置为默认配置')
}

// Logo上传前
const beforeLogoUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJpgOrPng) {
    ElMessage.error('Logo只支持 JPG/PNG 格式')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('Logo大小不能超过 2MB')
    return false
  }
  return true
}

// Logo上传成功
const handleLogoSuccess = (response: any) => {
  formData.value.siteLogo = response.url || URL.createObjectURL(response.raw)
  ElMessage.success('Logo上传成功')
}

// Icon上传成功
const handleIconSuccess = (response: any) => {
  formData.value.siteIcon = response.url || URL.createObjectURL(response.raw)
  ElMessage.success('Icon上传成功')
}
</script>

<template>
  <div class="basic-settings">
    <el-form ref="formRef" :model="formData" label-width="120px" class="settings-form">
      <!-- 网站信息 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>网站信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="网站名称" prop="siteName" :rules="[{ required: true, message: '请输入网站名称', trigger: 'blur' }]">
              <el-input v-model="formData.siteName" placeholder="请输入网站名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="网站Logo">
              <el-upload class="logo-uploader" action="#" :show-file-list="false" :before-upload="beforeLogoUpload"
                :http-request="handleLogoSuccess">
                <img v-if="formData.siteLogo" :src="formData.siteLogo" class="uploaded-logo" />
                <div v-else class="upload-placeholder">
                  <el-icon :size="20"><Plus /></el-icon>
                  <span>上传Logo</span>
                </div>
              </el-upload>
              <span class="form-tip">建议尺寸 200x60px</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="网站Icon">
              <el-upload class="icon-uploader" action="#" :show-file-list="false" :before-upload="beforeLogoUpload"
                :http-request="handleIconSuccess">
                <img v-if="formData.siteIcon" :src="formData.siteIcon" class="uploaded-icon" />
                <div v-else class="upload-placeholder">
                  <el-icon :size="16"><Plus /></el-icon>
                </div>
              </el-upload>
              <span class="form-tip">建议尺寸 32x32px</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="网站关键词">
              <el-input v-model="formData.siteKeywords" placeholder="多个关键词用逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="网站描述">
          <el-input v-model="formData.siteDescription" type="textarea" :rows="2" placeholder="请输入网站描述，用于SEO优化" />
        </el-form-item>
      </el-card>

      <!-- 联系方式 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>联系方式</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客服电话">
              <el-input v-model="formData.servicePhone" placeholder="请输入客服电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客服邮箱">
              <el-input v-model="formData.serviceEmail" placeholder="请输入客服邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司地址">
              <el-input v-model="formData.companyAddress" placeholder="请输入公司地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作时间">
              <el-input v-model="formData.workTime" placeholder="如：周一至周日 9:00-21:00" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 备案信息 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>备案信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ICP备案号">
              <el-input v-model="formData.icp" placeholder="如：京ICP备12345678号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公安备案号">
              <el-input v-model="formData.police" placeholder="如：京公网安备11010502030405号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="营业执照">
          <el-input v-model="formData.business" placeholder="请输入药品经营许可证号" />
        </el-form-item>
      </el-card>

      <!-- 版权信息 -->
      <el-card class="setting-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>版权信息</span>
          </div>
        </template>
        <el-form-item label="版权所有">
          <el-input v-model="formData.copyright" placeholder="请输入版权信息" />
        </el-form-item>
      </el-card>

      <!-- 保存按钮 -->
      <div class="form-actions">
        <el-button type="primary" size="large" @click="handleSave">保存配置</el-button>
        <el-button size="large" @click="handleReset">重置</el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped lang="scss">
.basic-settings {
  padding: 0;

  .settings-form {
    max-width: 1200px;
  }

  .setting-card {
    margin-bottom: 20px;

    .card-header {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;
      width: 200px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409eff;
      }
    }

    .uploaded-logo {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }

    .upload-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      color: #8c939d;
      font-size: 12px;

      span {
        margin-top: 4px;
      }
    }
  }

  .icon-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409eff;
      }
    }

    .uploaded-icon {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }

    .upload-placeholder {
      color: #8c939d;
    }
  }

  .form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #909399;
  }

  .form-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding: 20px 0;
  }
}
</style>