<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getHomeGlobalConfig, saveHomeGlobalConfig } from '@/api/homeConfig'
import type { HomeGlobalConfig } from '@/types/homeConfig'

const loading = ref(false)
const saving = ref(false)
const form = reactive<HomeGlobalConfig>({
  pageTitle: 'DrugMall药品电商',
  backgroundColor: '#f5f5f5',
  theme: 'light',
  primaryColor: '#409EFF',
  fontSize: 14,
  showSearchBar: true,
  showCategoryNav: true,
  layout: 'grid',
  customCSS: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await getHomeGlobalConfig()
    Object.assign(form, res.data)
  } catch {
    ElMessage.warning('使用默认配置')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    await saveHomeGlobalConfig(form)
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="home-global-config" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>全局设置</span>
          <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
        </div>
      </template>

      <el-form :model="form" label-width="120px" style="max-width: 600px">
        <el-divider content-position="left">基础设置</el-divider>
        
        <el-form-item label="页面标题">
          <el-input v-model="form.pageTitle" placeholder="请输入页面标题" />
        </el-form-item>

        <el-form-item label="主题风格">
          <el-radio-group v-model="form.theme">
            <el-radio value="light">浅色</el-radio>
            <el-radio value="dark">深色</el-radio>
            <el-radio value="custom">自定义</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="背景颜色">
          <el-color-picker v-model="form.backgroundColor" />
        </el-form-item>

        <el-form-item label="主题色">
          <el-color-picker v-model="form.primaryColor" />
        </el-form-item>

        <el-form-item label="字体大小">
          <el-slider v-model="form.fontSize" :min="12" :max="20" show-input />
        </el-form-item>

        <el-divider content-position="left">布局设置</el-divider>

        <el-form-item label="布局方式">
          <el-radio-group v-model="form.layout">
            <el-radio value="grid">网格布局</el-radio>
            <el-radio value="list">列表布局</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="显示搜索栏">
          <el-switch v-model="form.showSearchBar" />
        </el-form-item>

        <el-form-item label="显示分类导航">
          <el-switch v-model="form.showCategoryNav" />
        </el-form-item>

        <el-divider content-position="left">自定义样式</el-divider>

        <el-form-item label="自定义CSS">
          <el-input
            v-model="form.customCSS"
            type="textarea"
            :rows="8"
            placeholder="请输入自定义CSS代码"
            style="font-family: monospace"
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
