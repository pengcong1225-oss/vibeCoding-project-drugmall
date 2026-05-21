<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDoctorSchedule as getScheduleList, createSchedule, updateSchedule, deleteSchedule } from '@/api/doctor'
import type { ScheduleInfo } from '@/types/doctor'

const route = useRoute()
const loading = ref(false)
const tableData = ref<ScheduleInfo[]>([])
const doctorId = Number(route.params.id)
const doctorName = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const form = reactive<Partial<ScheduleInfo>>({
  date: '',
  morningEnabled: false,
  morningMax: 10,
  afternoonEnabled: false,
  afternoonMax: 10,
  eveningEnabled: false,
  eveningMax: 10,
  consultationTypes: [],
  status: 1
})

const consultTypeOptions = [
  { label: '图文问诊', value: 'text' },
  { label: '视频问诊', value: 'video' },
  { label: '电话问诊', value: 'phone' }
]

const loadSchedule = async () => {
  loading.value = true
  try {
    const res = await getScheduleList(doctorId)
    tableData.value = res
  } catch (error) {
    console.error('获取排班数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  isEdit.value = false
  Object.assign(form, {
    date: '', morningEnabled: false, morningMax: 10,
    afternoonEnabled: false, afternoonMax: 10,
    eveningEnabled: false, eveningMax: 10,
    consultationTypes: [], status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: ScheduleInfo) => {
  dialogTitle.value = '编辑排班'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value && form.id) {
      await updateSchedule(doctorId, form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await createSchedule(doctorId, form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadSchedule()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: ScheduleInfo) => {
  try {
    await ElMessageBox.confirm(`确定要删除 ${row.date} 的排班吗？`, '确认删除', { type: 'warning' })
    if (row.id) {
      await deleteSchedule(doctorId, row.id)
      ElMessage.success('删除成功')
      loadSchedule()
    }
  } catch {
    // 取消操作
  }
}

const handleSuspend = async () => {
  try {
    await ElMessageBox.prompt('请输入停诊原因', '停诊操作', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请输入停诊原因'
    })
    ElMessage.success('停诊操作成功')
    loadSchedule()
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadSchedule()
})
</script>

<template>
  <div class="schedule-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">排班管理 - {{ doctorName }}</span>
      </template>
    </el-page-header>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">排班信息</span>
          <div style="display: flex; gap: 8px">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增排班</el-button>
            <el-button type="warning">批量排班</el-button>
            <el-button type="danger" @click="handleSuspend">停诊设置</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="日期" prop="date" width="140" />
        <el-table-column label="上午" width="180">
          <template #default="{ row }">
            <el-switch v-model="row.morningEnabled" size="small" style="margin-right: 8px" disabled />
            <span v-if="row.morningEnabled">限额 {{ row.morningMax }}</span>
            <span v-else class="text-muted">休息</span>
          </template>
        </el-table-column>
        <el-table-column label="下午" width="180">
          <template #default="{ row }">
            <el-switch v-model="row.afternoonEnabled" size="small" style="margin-right: 8px" disabled />
            <span v-if="row.afternoonEnabled">限额 {{ row.afternoonMax }}</span>
            <span v-else class="text-muted">休息</span>
          </template>
        </el-table-column>
        <el-table-column label="晚上" width="180">
          <template #default="{ row }">
            <el-switch v-model="row.eveningEnabled" size="small" style="margin-right: 8px" disabled />
            <span v-if="row.eveningEnabled">限额 {{ row.eveningMax }}</span>
            <span v-else class="text-muted">休息</span>
          </template>
        </el-table-column>
        <el-table-column label="问诊类型" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="type in row.consultationTypes" :key="type" size="small" style="margin: 2px">
              {{ { text: '图文', video: '视频', phone: '电话' }[type as string] || type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停诊' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="日期" required>
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-divider content-position="left">时段设置</el-divider>
        <el-form-item label="上午">
          <el-switch v-model="form.morningEnabled" />
          <el-input-number v-if="form.morningEnabled" v-model="form.morningMax" :min="1" controls-position="right" style="margin-left: 12px; width: 100px" />
          <span v-if="form.morningEnabled" style="margin-left: 4px; font-size: 12px; color: #909399">限额</span>
        </el-form-item>
        <el-form-item label="下午">
          <el-switch v-model="form.afternoonEnabled" />
          <el-input-number v-if="form.afternoonEnabled" v-model="form.afternoonMax" :min="1" controls-position="right" style="margin-left: 12px; width: 100px" />
          <span v-if="form.afternoonEnabled" style="margin-left: 4px; font-size: 12px; color: #909399">限额</span>
        </el-form-item>
        <el-form-item label="晚上">
          <el-switch v-model="form.eveningEnabled" />
          <el-input-number v-if="form.eveningEnabled" v-model="form.eveningMax" :min="1" controls-position="right" style="margin-left: 12px; width: 100px" />
          <span v-if="form.eveningEnabled" style="margin-left: 4px; font-size: 12px; color: #909399">限额</span>
        </el-form-item>
        <el-form-item label="问诊类型">
          <el-checkbox-group v-model="form.consultationTypes">
            <el-checkbox v-for="opt in consultTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.schedule-container { padding: 20px; }
.page-title { font-size: 18px; font-weight: 600; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.text-muted { color: #c0c4cc; }
</style>
