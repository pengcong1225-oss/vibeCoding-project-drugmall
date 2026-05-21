<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getPrescriptionList, cancelPrescription } from '@/api/prescription'
import type { PrescriptionInfo, PrescriptionQueryParams } from '@/types/prescription'

const router = useRouter()
const loading = ref(false)
const tableData = ref<PrescriptionInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<PrescriptionQueryParams>({
  prescriptionNo: '',
  patientName: '',
  doctorId: undefined,
  type: '',
  status: undefined,
  amountMin: undefined,
  amountMax: undefined
})

const doctorOptions = [
  { label: '张医生', value: 1 },
  { label: '李医生', value: 2 }
]

const typeMap: Record<string, { label: string; type: string }> = {
  normal: { label: '普通处方', type: 'primary' },
  emergency: { label: '急诊处方', type: 'danger' },
  chronic: { label: '慢病处方', type: 'warning' }
}

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '审核通过', type: 'success' },
  2: { label: '审核驳回', type: 'danger' },
  3: { label: '已发药', type: 'info' },
  4: { label: '已取消', type: 'info' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getPrescriptionList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取处方列表失败:', error)
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
    prescriptionNo: '', patientName: '', doctorId: undefined,
    type: '', status: undefined, amountMin: undefined, amountMax: undefined
  })
  pageNum.value = 1
  getList()
}

const handleView = (row: PrescriptionInfo) => {
  router.push(`/prescription/detail/${row.id}`)
}

const handleAudit = (row: PrescriptionInfo) => {
  router.push(`/prescription/audit/${row.id}`)
}

const handleCancel = async (row: PrescriptionInfo) => {
  try {
    await ElMessageBox.confirm('确定要取消该处方吗？', '确认取消', { type: 'warning' })
    await cancelPrescription(row.id)
    ElMessage.success('取消成功')
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
  <div class="prescription-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="处方编号">
          <el-input v-model="searchForm.prescriptionNo" placeholder="处方编号" clearable style="width: 160px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="患者姓名" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="searchForm.doctorId" placeholder="全部医生" clearable style="width: 130px">
            <el-option v-for="item in doctorOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处方类型">
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
          <div class="stat-value">520</div>
          <div class="stat-label">处方总量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">24</div>
          <div class="stat-label">待审核</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">480</div>
          <div class="stat-label">已通过</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409EFF">92%</div>
          <div class="stat-label">通过率</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">处方列表</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="处方编号" prop="prescriptionNo" width="160" />
        <el-table-column label="患者" prop="patientName" width="100" />
        <el-table-column label="医生" prop="doctorName" width="100" />
        <el-table-column label="诊断" prop="diagnosis" min-width="150" show-overflow-tooltip />
        <el-table-column label="药品数" prop="drugCount" width="90" align="center" />
        <el-table-column label="金额" prop="totalAmount" width="100" align="right">
          <template #default="{ row }">&#165;{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.type as any" size="small">
              {{ typeMap[row.type]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 0" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
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
.prescription-list-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
