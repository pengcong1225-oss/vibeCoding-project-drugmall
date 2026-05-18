<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getNoticeList, createNotice, updateNotice, deleteNotice } from '@/api/content'
import type { NoticeInfo } from '@/types/content'

const loading = ref(false)
const noticeList = ref<NoticeInfo[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  title: '',
  type: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)

const formData = ref<NoticeInfo>({
  id: '',
  title: '',
  content: '',
  type: 'system',
  status: 1,
  publishTime: null,
  views: 0,
  isTop: 0
})

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const typeOptions = [
  { label: '系统', value: 'system' },
  { label: '活动', value: 'activity' },
  { label: '公告', value: 'notice' }
]

const getStatusTagType = (type: number): 'success' | 'warning' | 'info' | 'primary' | 'danger' => {
  return type === 1 ? 'success' : 'info'
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchForm.title,
      type: searchForm.type,
      status: searchForm.status
    })
    noticeList.value = res.list
    total.value = res.total
  } catch {
    console.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadList()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.type = ''
  searchForm.status = undefined
  currentPage.value = 1
  loadList()
}

const handleAdd = () => {
  dialogTitle.value = '新增通知'
  isEdit.value = false
  formData.value = { id: '', title: '', content: '', type: 'system', status: 1, publishTime: null, views: 0, isTop: 0 }
  dialogVisible.value = true
}

const handleEdit = (row: NoticeInfo) => {
  dialogTitle.value = '编辑通知'
  isEdit.value = true
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: NoticeInfo) => {
  try {
    await ElMessageBox.confirm(`确定要删除通知"${row.title}"吗？`, '确认删除', { type: 'warning' })
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch {
    // 取消
  }
}

const submitForm = async () => {
  submitLoading.value = true
  try {
    if (isEdit.value && formData.value.id) {
      await updateNotice(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createNotice(formData.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadList()
  } catch {
    console.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

const handleToggleStatus = async (row: NoticeInfo) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateNotice(row.id, { status: newStatus })
    row.status = newStatus
    ElMessage.success('操作成功')
  } catch {
    console.error('操作失败')
  }
}

const handleToggleTop = async (row: NoticeInfo) => {
  const newTop = row.isTop ? 0 : 1
  try {
    await updateNotice(row.id, { isTop: newTop })
    row.isTop = newTop
    ElMessage.success('操作成功')
  } catch {
    console.error('操作失败')
  }
}

const handlePreview = (_row: NoticeInfo) => {
  ElMessage.info('预览功能开发中')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <div class="notice-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="通知标题" clearable @keyup.enter="handleSearch" style="width: 200px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
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
          <span class="card-title">通知列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增通知</el-button>
        </div>
      </template>

      <el-table :data="noticeList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ typeOptions.find(t => t.value === row.type)?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.isTop" :active-value="1" :inactive-value="0" @change="handleToggleTop(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="success" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" style="width: 100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入通知内容" maxlength="5000" show-word-limit />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.notice-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
