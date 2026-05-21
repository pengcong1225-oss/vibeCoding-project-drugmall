<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getHelpCenterList, createHelpCenter, updateHelpCenter, deleteHelpCenter } from '@/api/help'
import type { HelpCenter } from '@/types/help'

const loading = ref(false)
const tableData = ref<HelpCenter[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentHelp = ref<HelpCenter | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  category: '',
  status: ''
})

const form = reactive({
  title: '',
  category: '',
  content: '',
  sort: 0,
  status: 'active' as 'active' | 'disabled'
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  active: { label: '启用', type: 'success' },
  disabled: { label: '禁用', type: 'info' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getHelpCenterList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取帮助中心失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  fetchData()
}

function handleReset() {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    title: '',
    category: '',
    status: ''
  })
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    title: '',
    category: '',
    content: '',
    sort: 0,
    status: 'active'
  })
  dialogVisible.value = true
}

function handleEdit(row: HelpCenter) {
  isEdit.value = true
  currentHelp.value = row
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleDelete(row: HelpCenter) {
  try {
    await deleteHelpCenter(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function submitForm() {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  try {
    if (isEdit.value && currentHelp.value) {
      await updateHelpCenter(currentHelp.value.id, form)
    } else {
      await createHelpCenter(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('保存失败')
  }
}

function handlePageChange(page: number) {
  queryParams.pageNum = page
  fetchData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="help-center">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>帮助中心</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增文章</el-button>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="queryParams.category" placeholder="请输入分类" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文章' : '新增文章'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入文章内容" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="disabled">禁用</el-radio>
          </el-radio-group>
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
