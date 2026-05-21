<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Plus, Edit } from '@element-plus/icons-vue'
import { getAIConfigList, saveAIConfig } from '@/api/operation'
import type { AIConfig } from '@/types/operation'

const loading = ref(false)
const tableData = ref<AIConfig[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentConfig = ref<AIConfig | null>(null)

const form = reactive({
  name: '',
  type: 'knowledge' as 'knowledge' | 'qa' | 'recommendation',
  content: {} as Record<string, any>,
  status: 'active' as 'active' | 'disabled'
})

const typeMap: Record<string, string> = {
  knowledge: '知识库',
  qa: '问答配置',
  recommendation: '推荐配置'
}

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  active: { label: '启用', type: 'success' },
  disabled: { label: '禁用', type: 'info' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAIConfigList()
    tableData.value = res.data
  } catch {
    ElMessage.error('获取配置失败')
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    name: '',
    type: 'knowledge',
    content: {},
    status: 'active'
  })
  dialogVisible.value = true
}

function handleEdit(row: AIConfig) {
  isEdit.value = true
  currentConfig.value = row
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name) {
    ElMessage.warning('请填写配置名称')
    return
  }
  try {
    await saveAIConfig(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="ai-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>AI配置</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增配置</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="name" label="配置名称" width="200" />
        <el-table-column prop="type" label="类型" width="150">
          <template #default="{ row }">{{ typeMap[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑配置' : '新增配置'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="配置名称">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="配置类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="配置内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入JSON格式配置内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
