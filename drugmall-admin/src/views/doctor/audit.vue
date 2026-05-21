<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getAuditList, auditDoctor } from '@/api/doctor'
import type { DoctorAudit, AuditQueryParams } from '@/types/doctor'

const router = useRouter()
const loading = ref(false)
const tableData = ref<DoctorAudit[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<AuditQueryParams>({
  keyword: '',
  status: undefined,
  departmentId: undefined
})

const auditDialogVisible = ref(false)
const currentAudit = ref<DoctorAudit | null>(null)
const auditForm = reactive({
  result: 'pass',
  opinion: '',
  account: '',
  password: ''
})

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '审核中', type: 'primary' },
  2: { label: '已通过', type: 'success' },
  3: { label: '已驳回', type: 'danger' }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getAuditList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取审核列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', status: undefined, departmentId: undefined })
  pageNum.value = 1
  getList()
}

const handleAudit = (row: DoctorAudit) => {
  currentAudit.value = row
  auditForm.result = 'pass'
  auditForm.opinion = ''
  auditForm.account = ''
  auditForm.password = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (auditForm.result === 'reject' && !auditForm.opinion.trim()) {
    ElMessage.warning('驳回时审核意见必填')
    return
  }
  if (currentAudit.value) {
    await auditDoctor(currentAudit.value.id, {
      result: auditForm.result,
      opinion: auditForm.opinion
    })
    ElMessage.success('审核完成')
    auditDialogVisible.value = false
    getList()
  }
}

const handleView = (row: DoctorAudit) => {
  router.push(`/doctor/audit/${row.id}`)
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
  <div class="doctor-audit-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.keyword" placeholder="医生姓名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="待审核" :value="0" />
            <el-option label="审核中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 待审核统计 -->
    <el-alert title="待审核事项" type="warning" :closable="false" style="margin-bottom: 20px">
      <template #default>当前有 <strong>{{ total }}</strong> 条入驻申请待审核</template>
    </el-alert>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <span class="card-title">入驻审核</span>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="申请ID" width="100" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="手机号" prop="phone" width="130" />
        <el-table-column label="科室" prop="departmentName" width="100" />
        <el-table-column label="职称" prop="title" width="130" />
        <el-table-column label="医院" prop="hospital" min-width="160" show-overflow-tooltip />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="(statusMap[row.status]?.type as any)" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 0" link type="primary" @click="handleAudit(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 审核操作对话框（双栏布局） -->
    <el-dialog v-model="auditDialogVisible" title="审核医生入驻申请" width="900px" destroy-on-close>
      <el-row :gutter="20">
        <!-- 左侧：详情展示 -->
        <el-col :span="14">
          <el-card header="申请信息" shadow="never" style="height: 100%">
            <template v-if="currentAudit">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="姓名">{{ currentAudit.name }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ currentAudit.phone }}</el-descriptions-item>
                <el-descriptions-item label="科室">{{ currentAudit.departmentName }}</el-descriptions-item>
                <el-descriptions-item label="职称">{{ currentAudit.title }}</el-descriptions-item>
                <el-descriptions-item label="医院" :span="2">{{ currentAudit.hospital }}</el-descriptions-item>
              </el-descriptions>
              <el-divider content-position="left">资质材料</el-divider>
              <el-row :gutter="12">
                <el-col :span="8">
                  <el-card shadow="hover" class="cert-preview">
                    <el-image style="width: 100%; height: 100px" fit="contain" />
                    <div class="cert-label">身份证正面</div>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card shadow="hover" class="cert-preview">
                    <el-image style="width: 100%; height: 100px" fit="contain" />
                    <div class="cert-label">身份证反面</div>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card shadow="hover" class="cert-preview">
                    <el-image style="width: 100%; height: 100px" fit="contain" />
                    <div class="cert-label">医师资格证书</div>
                  </el-card>
                </el-col>
              </el-row>
            </template>
          </el-card>
        </el-col>

        <!-- 右侧：审核操作 -->
        <el-col :span="10">
          <el-card header="审核操作" shadow="never" style="height: 100%">
            <el-form :model="auditForm" label-width="80px">
              <el-form-item label="审核结果">
                <el-radio-group v-model="auditForm.result">
                  <el-radio value="pass">通过</el-radio>
                  <el-radio value="reject">驳回</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="审核意见">
                <el-input
                  v-model="auditForm.opinion"
                  type="textarea"
                  :rows="4"
                  :placeholder="auditForm.result === 'reject' ? '驳回原因（必填）' : '审核备注（可选）'"
                />
              </el-form-item>
              <el-divider>账号信息</el-divider>
              <el-form-item label="医生账号">
                <el-input v-model="auditForm.account" placeholder="自动生成或手动设置" />
              </el-form-item>
              <el-form-item label="初始密码">
                <el-input v-model="auditForm.password" placeholder="自动生成" />
              </el-form-item>
              <el-button type="primary" style="width: 100%; margin-top: 12px" @click="submitAudit">提交审核</el-button>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.doctor-audit-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
.cert-preview { text-align: center; margin-bottom: 8px; }
.cert-label { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
