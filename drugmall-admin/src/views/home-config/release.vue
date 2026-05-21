<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Upload, Delete, View } from '@element-plus/icons-vue'
import { getReleaseList, publishRelease, rollbackRelease, deleteRelease, getReleaseSummary } from '@/api/homeConfig'
import type { ReleaseVersion, ReleaseQueryParams } from '@/types/homeConfig'

const loading = ref(false)
const tableData = ref<ReleaseVersion[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<ReleaseQueryParams>({
  status: undefined
})

const publishDialogVisible = ref(false)
const publishForm = reactive({ description: '' })
const publishLoading = ref(false)
const summaryData = ref<any>(null)

const statusMap: Record<string, { label: string; type: 'primary' | 'success' | 'info' }> = {
  draft: { label: '草稿', type: 'primary' },
  published: { label: '已发布', type: 'success' },
  rollbacked: { label: '已回滚', type: 'info' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getReleaseList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取发布版本列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { status: undefined })
  pageNum.value = 1
  getList()
}

const handlePublish = async () => {
  publishForm.description = ''
  summaryData.value = null

  try {
    summaryData.value = await getReleaseSummary()
  } catch {
    summaryData.value = null
  }

  publishDialogVisible.value = true
}

const submitPublish = async () => {
  if (!publishForm.description.trim()) {
    ElMessage.warning('请输入版本说明')
    return
  }
  publishLoading.value = true
  try {
    await publishRelease(publishForm)
    ElMessage.success('发布成功')
    publishDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    publishLoading.value = false
  }
}

const handleRollback = async (row: ReleaseVersion) => {
  try {
    await ElMessageBox.confirm(
      `确定要回滚到版本 ${row.version} 吗？回滚后将生成新版本记录`,
      '确认回滚',
      { confirmButtonText: '确认回滚', cancelButtonText: '取消', type: 'warning' }
    )
    await rollbackRelease(row.id)
    ElMessage.success('回滚成功')
    getList()
  } catch {
    // 取消操作
  }
}

const handleDelete = async (row: ReleaseVersion) => {
  try {
    await ElMessageBox.confirm(`确定要删除版本 ${row.version} 吗？`, '确认删除', {
      confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning'
    })
    await deleteRelease(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch {
    // 取消操作
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
  <div class="release-config-container">
    <!-- 操作提示 -->
    <el-alert title="发布管理说明" type="info" :closable="false" show-icon style="margin-bottom: 20px">
      <template #default>
        编辑配置后需通过发布操作才能生效。发布前可预览效果确认，发布后可通过回滚恢复历史版本。
      </template>
    </el-alert>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="草稿" value="draft" />
            <el-option label="已发布" value="published" />
            <el-option label="已回滚" value="rollbacked" />
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
          <span class="card-title">发布管理</span>
          <div style="display: flex; gap: 8px">
            <el-button type="primary" @click="handlePublish">发布配置</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="版本号" prop="version" width="120" />
        <el-table-column label="版本说明" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="创建人" prop="createdBy" width="100" />
        <el-table-column label="创建时间" prop="createdTime" width="160" />
        <el-table-column label="发布时间" prop="publishedTime" width="160" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View">预览</el-button>
            <el-button v-if="row.status === 'draft'" link type="primary" @click="handlePublish">发布</el-button>
            <el-button v-if="row.status === 'published'" link type="warning" :icon="Upload" @click="handleRollback(row)">回滚</el-button>
            <el-button v-if="row.status === 'draft'" link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <!-- 发布确认对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布配置" width="600px" destroy-on-close>
      <el-alert v-if="summaryData" title="本次变更摘要" type="warning" :closable="false" style="margin-bottom: 20px">
        <template #default>
          <div>新增: {{ summaryData.added }} 项 | 修改: {{ summaryData.modified }} 项 | 删除: {{ summaryData.deleted }} 项</div>
        </template>
      </el-alert>

      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本说明">
          <el-input v-model="publishForm.description" type="textarea" :rows="4" placeholder="请描述本次发布的主要内容" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishLoading" @click="submitPublish">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.release-config-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
