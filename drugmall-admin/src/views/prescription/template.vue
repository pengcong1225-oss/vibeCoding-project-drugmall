<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, CopyDocument } from '@element-plus/icons-vue'
import { getTemplateList, createTemplate, updateTemplate, deleteTemplate, copyTemplate } from '@/api/prescription'
import type { PrescriptionTemplate, TemplateQueryParams } from '@/types/prescription'

const loading = ref(false)
const tableData = ref<PrescriptionTemplate[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<TemplateQueryParams>({
  keyword: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<PrescriptionTemplate>>({
  name: '',
  diagnosis: '',
  usageNotes: '',
  precautions: '',
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  diagnosis: [{ required: true, message: '请输入诊断', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getTemplateList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取模板列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', status: undefined })
  pageNum.value = 1
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增模板'
  isEdit.value = false
  Object.assign(form, {
    name: '', diagnosis: '', usageNotes: '',
    precautions: '', status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: PrescriptionTemplate) => {
  dialogTitle.value = '编辑模板'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateTemplate(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createTemplate(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: PrescriptionTemplate) => {
  try {
    await ElMessageBox.confirm(`确定要删除模板 "${row.name}" 吗？`, '确认删除', { type: 'warning' })
    if (row.id) {
      await deleteTemplate(row.id)
      ElMessage.success('删除成功')
      getList()
    }
  } catch {
    // 取消操作
  }
}

const handleCopy = async (row: PrescriptionTemplate) => {
  if (row.id) {
    await copyTemplate(row.id)
    ElMessage.success('复制成功')
    getList()
  }
}

const handleStatusChange = async (row: PrescriptionTemplate) => {
  const status = row.status === 1 ? 0 : 1
  if (row.id) {
    await updateTemplate(row.id, { status })
    row.status = status
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  getList()
}

const handleCurrentChange = (page: number) => {
  pageNum.value = page
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="template-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="模板名称/诊断" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">处方模板</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增模板</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="模板名称" prop="name" width="160" />
        <el-table-column label="创建医生" prop="doctorName" width="100" />
        <el-table-column label="科室" prop="departmentName" width="100" />
        <el-table-column label="诊断" prop="diagnosis" min-width="200" show-overflow-tooltip />
        <el-table-column label="药品数" prop="drugCount" width="90" align="center" />
        <el-table-column label="使用次数" prop="useCount" width="100" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" :icon="CopyDocument" @click="handleCopy(row)">复制</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" placeholder="模板名称" />
        </el-form-item>
        <el-form-item label="诊断" prop="diagnosis">
          <el-input v-model="form.diagnosis" type="textarea" :rows="2" placeholder="诊断内容" />
        </el-form-item>
        <el-form-item label="用药说明">
          <el-input v-model="form.usageNotes" type="textarea" :rows="2" placeholder="用药说明" />
        </el-form-item>
        <el-form-item label="注意事项">
          <el-input v-model="form.precautions" type="textarea" :rows="2" placeholder="注意事项" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-divider content-position="left">药品明细</el-divider>
        <el-empty description="请在编辑后添加药品明细" :image-size="60" />
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.template-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
