<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View, Clock } from '@element-plus/icons-vue'
import { getExceptionList, handleException } from '@/api/consultation'
import type { ConsultationException, ExceptionQueryParams } from '@/types/consultation'

const loading = ref(false)
const tableData = ref<ConsultationException[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<ExceptionQueryParams>({
  exceptionType: '',
  status: '',
  startTime: '',
  endTime: ''
})

const handleDialogVisible = ref(false)
const handleForm = reactive({
  method: '',
  note: '',
  compensationAmount: undefined as number | undefined
})
const currentException = ref<ConsultationException | null>(null)

const typeMap: Record<string, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  timeout_no_response: { label: '超时未接诊', type: 'danger' },
  timeout_no_reply: { label: '超时未回复', type: 'warning' },
  complaint: { label: '投诉', type: 'danger' },
  refund: { label: '退款', type: 'warning' },
  abnormal_cancel: { label: '异常取消', type: 'info' }
}

const statusMap: Record<string, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  pending: { label: '待处理', type: 'danger' },
  processing: { label: '处理中', type: 'warning' },
  handled: { label: '已处理', type: 'success' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getExceptionList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取异常列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { exceptionType: '', status: '', startTime: '', endTime: '' })
  pageNum.value = 1
  getList()
}

const handleView = (row: ConsultationException) => {
  currentException.value = row
  Object.assign(handleForm, { method: '', note: '', compensationAmount: undefined })
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.method) {
    ElMessage.warning('请选择处理方式')
    return
  }
  if (!currentException.value) return

  try {
    await handleException(currentException.value.id, {
      method: handleForm.method,
      note: handleForm.note,
      compensationAmount: handleForm.compensationAmount
    })
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('处理异常失败:', error)
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val: number) => {
  pageNum.value = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="exception-container">
    <!-- 搜索 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="异常类型">
          <el-select v-model="searchForm.exceptionType" placeholder="全部类型" clearable style="width: 140px">
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计 -->
    <el-row :gutter="16" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #F56C6C">25</div>
          <div class="stat-label">异常总量</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">12</div>
          <div class="stat-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">2.5%</div>
          <div class="stat-label">异常率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">异常问诊列表</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="问诊编号" prop="consultationNo" width="150" />
        <el-table-column label="患者" prop="patientNickname" width="120" />
        <el-table-column label="医生" prop="doctorName" width="100" />
        <el-table-column label="异常类型" width="130">
          <template #default="{ row }">
            <el-tag :type="(typeMap[row.exceptionType]?.type as any)" size="small">
              {{ typeMap[row.exceptionType]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="异常描述" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="(statusMap[row.status]?.type as any)" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Clock" @click="handleView(row)">处理</el-button>
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

    <!-- 处理对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理异常问诊" width="500px">
      <el-descriptions v-if="currentException" :column="1" border style="margin-bottom: 20px">
        <el-descriptions-item label="问诊编号">{{ currentException.consultationNo }}</el-descriptions-item>
        <el-descriptions-item label="患者">{{ currentException.patientNickname }}</el-descriptions-item>
        <el-descriptions-item label="异常类型">{{ typeMap[currentException.exceptionType]?.label }}</el-descriptions-item>
        <el-descriptions-item label="异常描述">{{ currentException.description }}</el-descriptions-item>
      </el-descriptions>

      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理方式">
          <el-select v-model="handleForm.method" style="width: 100%">
            <el-option label="联系患者协调" value="contact_patient" />
            <el-option label="联系医生处理" value="contact_doctor" />
            <el-option label="退款处理" value="refund" />
            <el-option label="补偿处理" value="compensation" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="handleForm.method === 'compensation'" label="补偿金额">
          <el-input-number v-model="handleForm.compensationAmount" :precision="2" :min="0" :max="500" />
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="handleForm.note" type="textarea" :rows="3" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.exception-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
