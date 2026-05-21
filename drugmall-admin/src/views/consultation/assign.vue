<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getConsultationList, assignConsultation, getAvailableDoctors } from '@/api/consultation'
import type { ConsultationInfo, ConsultationQueryParams, AvailableDoctor } from '@/types/consultation'

const loading = ref(false)
const tableData = ref<ConsultationInfo[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<ConsultationQueryParams>({
  status: 0  // 默认只显示待接诊
})

const assignDialogVisible = ref(false)
const currentConsultation = ref<ConsultationInfo | null>(null)
const availableDoctors = ref<AvailableDoctor[]>([])
const selectedDoctorId = ref<number | undefined>()
const assignReason = ref('')

const typeMap: Record<string, string> = {
  text: '图文问诊',
  video: '视频问诊',
  phone: '电话问诊'
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

const handleReset = () => {
  Object.assign(searchForm, { status: 0, patientPhone: '' })
  pageNum.value = 1
  getList()
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleAssign = async (row: ConsultationInfo) => {
  currentConsultation.value = row
  selectedDoctorId.value = undefined
  assignReason.value = ''
  assignDialogVisible.value = true

  try {
    availableDoctors.value = await getAvailableDoctors(row.id)
  } catch (error) {
    console.error('获取可分配医生失败:', error)
  }
}

const submitAssign = async () => {
  if (!selectedDoctorId.value) {
    ElMessage.warning('请选择要分配的医生')
    return
  }
  if (currentConsultation.value) {
    await assignConsultation(currentConsultation.value.id, {
      doctorId: selectedDoctorId.value,
      reason: assignReason.value
    })
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    getList()
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
  <div class="assign-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="患者电话">
          <el-input v-model="searchForm.patientPhone" placeholder="患者手机号" clearable style="width: 160px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-alert title="问诊分配" type="info" :closable="false" style="margin-bottom: 20px">
      <template #default>
        将待接诊的问诊分配给合适的医生。可根据科室、在线状态和当前负载选择合适的医生。
      </template>
    </el-alert>

    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">待分配问诊</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="问诊编号" prop="consultationNo" width="150" />
        <el-table-column label="患者" prop="patientNickname" width="100" />
        <el-table-column label="问诊类型" width="110">
          <template #default="{ row }">
            <el-tag type="primary" size="small">{{ typeMap[row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="症状描述" prop="symptom" min-width="200" show-overflow-tooltip />
        <el-table-column label="费用" prop="fee" width="90" align="right">
          <template #default="{ row }">¥{{ row.fee }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAssign(row)">分配</el-button>
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

    <!-- 分配医生对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配问诊" width="700px" destroy-on-close>
      <div v-if="currentConsultation" style="margin-bottom: 16px">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="问诊编号">{{ currentConsultation.consultationNo }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ currentConsultation.patientNickname }}</el-descriptions-item>
          <el-descriptions-item label="症状">{{ currentConsultation.symptom }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="doctor-list">
        <div
          v-for="doctor in availableDoctors"
          :key="doctor.id"
          class="doctor-card"
          :class="{ selected: selectedDoctorId === doctor.id }"
          @click="selectedDoctorId = doctor.id"
        >
          <el-radio v-model="selectedDoctorId" :value="doctor.id">
            <div class="doctor-info">
              <div class="doctor-name">{{ doctor.name }} <el-tag size="small">{{ doctor.title }}</el-tag></div>
              <div class="doctor-dept">{{ doctor.departmentName }}</div>
              <div class="doctor-stats">
                <el-rate v-model="doctor.rating" disabled size="small" />
                <span>负载: {{ doctor.currentConsultations }}/{{ doctor.maxConsultations }}</span>
              </div>
            </div>
          </el-radio>
          <el-tag v-if="doctor.onlineStatus" type="success" size="small">在线</el-tag>
          <el-tag v-else type="info" size="small">离线</el-tag>
        </div>
        <el-empty v-if="!availableDoctors.length" description="暂无可分配的医生" />
      </div>

      <el-form style="margin-top: 16px" label-width="80px">
        <el-form-item label="分配原因">
          <el-input v-model="assignReason" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.assign-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
.doctor-list { max-height: 400px; overflow-y: auto; }
.doctor-card { display: flex; justify-content: space-between; align-items: center; padding: 12px; border: 1px solid #ebeef5; border-radius: 4px; margin-bottom: 8px; cursor: pointer; }
.doctor-card:hover { border-color: #409eff; background: #ecf5ff; }
.doctor-card.selected { border-color: #409eff; background: #ecf5ff; }
.doctor-info { margin-left: 8px; }
.doctor-name { font-weight: 500; margin-bottom: 4px; }
.doctor-dept { font-size: 12px; color: #909399; margin-bottom: 4px; }
.doctor-stats { display: flex; align-items: center; gap: 12px; font-size: 12px; }
</style>
