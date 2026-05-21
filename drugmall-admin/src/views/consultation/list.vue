<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getConsultationList, cancelConsultation, refundConsultation } from '@/api/consultation'
import type { ConsultationInfo, ConsultationQueryParams } from '@/types/consultation'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ConsultationInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<ConsultationQueryParams>({
  consultationNo: '',
  patientPhone: '',
  doctorId: undefined,
  departmentId: undefined,
  type: '',
  status: undefined,
  feeMin: undefined,
  feeMax: undefined
})

const doctorOptions = [
  { label: '张医生', value: 1 },
  { label: '李医生', value: 2 },
  { label: '王医生', value: 3 }
]

const departmentOptions = [
  { label: '内科', value: 1 },
  { label: '外科', value: 2 },
  { label: '儿科', value: 3 },
  { label: '妇产科', value: 4 }
]

const typeMap: Record<string, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  text: { label: '图文问诊', type: 'primary' },
  video: { label: '视频问诊', type: 'success' },
  phone: { label: '电话问诊', type: 'warning' },
  ai: { label: 'AI导诊', type: 'info' }
}

const statusMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  0: { label: '待接诊', type: 'warning' },
  1: { label: '问诊中', type: 'primary' },
  2: { label: '待支付', type: 'info' },
  3: { label: '已完成', type: 'success' },
  4: { label: '已取消', type: 'danger' },
  5: { label: '退款中', type: 'warning' },
  6: { label: '已退款', type: 'info' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getConsultationList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取问诊列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    consultationNo: '', patientPhone: '', doctorId: undefined,
    departmentId: undefined, type: '', status: undefined,
    feeMin: undefined, feeMax: undefined
  })
  pageNum.value = 1
  getList()
}

const handleView = (row: ConsultationInfo) => {
  router.push(`/consultation/detail/${row.id}`)
}

const handleCancel = async (row: ConsultationInfo) => {
  try {
    await ElMessageBox.confirm('确定要取消该问诊吗？', '确认取消', { type: 'warning' })
    await cancelConsultation(row.id)
    ElMessage.success('取消成功')
    getList()
  } catch {
    // 取消操作
  }
}

const handleRefund = async (row: ConsultationInfo) => {
  try {
    await ElMessageBox.confirm('确定要对该问诊发起退款吗？', '确认退款', { type: 'warning' })
    await refundConsultation(row.id)
    ElMessage.success('退款申请已提交')
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
  <div class="consultation-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="问诊编号">
          <el-input v-model="searchForm.consultationNo" placeholder="问诊编号" clearable style="width: 160px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="searchForm.doctorId" placeholder="全部医生" clearable style="width: 140px">
            <el-option v-for="item in doctorOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="searchForm.departmentId" placeholder="全部科室" clearable style="width: 130px">
            <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="问诊类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 130px">
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="Number(k)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">356</div>
          <div class="stat-label">问诊总量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409EFF">18</div>
          <div class="stat-label">待接诊</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">298</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">85%</div>
          <div class="stat-label">完成率</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">问诊列表</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="问诊编号" prop="consultationNo" width="150" />
        <el-table-column label="患者" prop="patientNickname" width="100" />
        <el-table-column label="医生" prop="doctorName" width="100" />
        <el-table-column label="科室" prop="departmentName" width="100" />
        <el-table-column label="问诊类型" width="110">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type" size="small">
              {{ typeMap[row.type]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="症状描述" prop="symptom" min-width="180" show-overflow-tooltip />
        <el-table-column label="费用" prop="fee" width="90" align="right">
          <template #default="{ row }">&#165;{{ row.fee }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
            <el-button v-if="[1, 3].includes(row.status)" link type="warning" @click="handleRefund(row)">退款</el-button>
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
  </div>
</template>

<style scoped lang="scss">
.consultation-list-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
