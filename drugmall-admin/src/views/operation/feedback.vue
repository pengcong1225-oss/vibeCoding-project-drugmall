<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getFeedbackList, replyFeedback } from '@/api/operation'
import type { Feedback } from '@/types/operation'

const loading = ref(false)
const tableData = ref<Feedback[]>([])
const total = ref(0)
const replyDialogVisible = ref(false)
const currentFeedback = ref<Feedback | null>(null)
const replyContent = ref('')

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  type: '',
  status: '',
  startDate: '',
  endDate: ''
})

const typeMap: Record<string, string> = {
  suggestion: '建议',
  bug: 'Bug反馈',
  feature: '功能需求',
  other: '其他'
}

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待处理', type: 'warning' },
  processed: { label: '已处理', type: 'success' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeedbackList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取反馈列表失败')
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
    type: '',
    status: '',
    startDate: '',
    endDate: ''
  })
  fetchData()
}

function handleReply(row: Feedback) {
  currentFeedback.value = row
  replyContent.value = ''
  replyDialogVisible.value = true
}

async function submitReply() {
  if (!currentFeedback.value || !replyContent.value) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await replyFeedback(currentFeedback.value.id, { reply: replyContent.value })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('回复失败')
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
  <div class="feedback-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>意见反馈</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v" :value="k" />
          </el-select>
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
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">{{ typeMap[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="contact" label="联系方式" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'closed'" type="primary" link :icon="View" @click="handleReply(row)">回复</el-button>
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

    <el-dialog v-model="replyDialogVisible" title="回复反馈" width="500px">
      <el-form label-width="80px">
        <el-form-item label="反馈内容">
          <el-input :value="currentFeedback?.content" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
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
