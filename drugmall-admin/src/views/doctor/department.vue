<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDepartmentList, createDepartment, updateDepartment, deleteDepartment } from '@/api/doctor'
import type { DepartmentInfo } from '@/types/doctor'

const loading = ref(false)
const tableData = ref<DepartmentInfo[]>([])

const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<DepartmentInfo>>({
  name: '',
  parentId: 0,
  icon: '',
  sortOrder: 0,
  status: 1,
  description: ''
})

const formRules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }]
}

const loadDepartments = async () => {
  loading.value = true
  try {
    tableData.value = await getDepartmentList(searchForm)
  } catch (error) {
    console.error('获取科室列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadDepartments()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', status: undefined })
  loadDepartments()
}

const handleAdd = (parentId: number | null = null) => {
  dialogTitle.value = '新增科室'
  isEdit.value = false
  Object.assign(form, {
    name: '', parentId: parentId || 0, icon: '',
    sortOrder: 0, status: 1, description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: DepartmentInfo) => {
  dialogTitle.value = '编辑科室'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && form.id) {
      await updateDepartment(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createDepartment(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadDepartments()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: DepartmentInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除科室 "${row.name}" 吗？${row.doctorCount ? `该科室下有 ${row.doctorCount} 名医生，请先移除医生。` : ''}`,
      '确认删除',
      { type: 'warning' }
    )
    if (row.id) {
      await deleteDepartment(row.id)
      ElMessage.success('删除成功')
      loadDepartments()
    }
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadDepartments()
})
</script>

<template>
  <div class="department-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="科室名称">
          <el-input v-model="searchForm.keyword" placeholder="科室名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
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
          <span class="card-title">科室管理</span>
          <el-button type="primary" :icon="Plus" @click.stop="handleAdd(null)">新增科室</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" row-key="id" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="科室名称" prop="name" min-width="200">
          <template #default="{ row }">
            <span v-if="row.parentId" style="padding-left: 24px">{{ row.name }}</span>
            <span v-else style="font-weight: 600">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="医生数" prop="doctorCount" width="100" align="center" />
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" :icon="Plus" @click="handleAdd(row.id)">添加子科室</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="科室名称" />
        </el-form-item>
        <el-form-item label="上级科室">
          <el-select v-model="form.parentId" placeholder="无（顶级科室）" clearable style="width: 100%">
              <el-option label="无" :value="0" />
            <el-option v-for="item in tableData" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.department-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
</style>
