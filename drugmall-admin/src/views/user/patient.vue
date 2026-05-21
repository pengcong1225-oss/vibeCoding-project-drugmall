<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getPatientList } from '@/api/patient'
import type { Patient } from '@/types/patient'

const loading = ref(false)
const tableData = ref<Patient[]>([])
const total = ref(0)
const detailDialogVisible = ref(false)
const currentPatient = ref<Patient | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: '',
  name: '',
  relationship: ''
})

const relationshipMap: Record<string, string> = {
  self: '本人',
  parent: '父母',
  child: '子女',
  spouse: '配偶',
  other: '其他'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPatientList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取就诊人列表失败')
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
    userId: '',
    name: '',
    relationship: ''
  })
  fetchData()
}

function handleViewDetail(row: Patient) {
  currentPatient.value = row
  detailDialogVisible.value = true
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
  <div class="patient-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>就诊人管理</span>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="用户ID">
          <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="关系">
          <el-select v-model="queryParams.relationship" placeholder="请选择" clearable>
            <el-option v-for="(v, k) in relationshipMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="就诊人ID" width="120" />
        <el-table-column prop="userName" label="所属用户" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 'male' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="relationship" label="关系" width="100">
          <template #default="{ row }">{{ relationshipMap[row.relationship] }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="consultationCount" label="问诊次数" width="100" />
        <el-table-column prop="prescriptionCount" label="处方数" width="100" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleViewDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailDialogVisible" title="就诊人详情" width="600px">
      <template v-if="currentPatient">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="就诊人ID">{{ currentPatient.id }}</el-descriptions-item>
          <el-descriptions-item label="所属用户">{{ currentPatient.userName }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentPatient.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentPatient.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentPatient.age }}</el-descriptions-item>
          <el-descriptions-item label="关系">{{ relationshipMap[currentPatient.relationship] }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentPatient.idCard }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentPatient.phone }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>健康档案</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="血型">{{ currentPatient.healthRecord.bloodType || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="最近体检">{{ currentPatient.healthRecord.lastCheckup || '未体检' }}</el-descriptions-item>
          <el-descriptions-item label="过敏史" :span="2">
            <el-tag v-for="item in currentPatient.healthRecord.allergies" :key="item" style="margin-right: 8px">{{ item }}</el-tag>
            <span v-if="!currentPatient.healthRecord.allergies.length">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="慢性病" :span="2">
            <el-tag v-for="item in currentPatient.healthRecord.chronicDiseases" :key="item" style="margin-right: 8px" type="warning">{{ item }}</el-tag>
            <span v-if="!currentPatient.healthRecord.chronicDiseases.length">无</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>统计数据</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="问诊次数" :value="currentPatient.consultationCount" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="处方数量" :value="currentPatient.prescriptionCount" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="订单数量" :value="currentPatient.orderCount" />
          </el-col>
        </el-row>
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
