<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit } from '@element-plus/icons-vue'
import { getSymptomQuestionList, saveSymptomQuestion } from '@/api/operation'
import type { SymptomQuestion } from '@/types/operation'

const loading = ref(false)
const tableData = ref<SymptomQuestion[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentQuestion = ref<SymptomQuestion | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  symptom: ''
})

const form = reactive({
  symptom: '',
  department: '',
  questions: [] as any[],
  possibleDiseases: [] as string[],
  suggestions: '',
  status: 'active' as 'active' | 'disabled'
})

const statusMap: Record<string, { label: string, type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  active: { label: '启用', type: 'success' },
  disabled: { label: '禁用', type: 'info' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getSymptomQuestionList(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('获取题库失败')
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
    symptom: ''
  })
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    symptom: '',
    department: '',
    questions: [],
    possibleDiseases: [],
    suggestions: '',
    status: 'active'
  })
  dialogVisible.value = true
}

function handleEdit(row: SymptomQuestion) {
  isEdit.value = true
  currentQuestion.value = row
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.symptom) {
    ElMessage.warning('请填写症状名称')
    return
  }
  try {
    await saveSymptomQuestion(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('保存失败')
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
  <div class="symptom-question">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>症状题库</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增题目</el-button>
        </div>
      </template>

      <el-form :model="queryParams" inline>
        <el-form-item label="症状">
          <el-input v-model="queryParams.symptom" placeholder="请输入症状" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="symptom" label="症状" width="150" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="possibleDiseases" label="可能疾病" show-overflow-tooltip>
          <template #default="{ row }">{{ row.possibleDiseases.join('、') }}</template>
        </el-table-column>
        <el-table-column prop="questions" label="题目数量" width="100">
          <template #default="{ row }">{{ row.questions.length }}题</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑题目' : '新增题目'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="症状">
          <el-input v-model="form.symptom" placeholder="请输入症状" />
        </el-form-item>
        <el-form-item label="科室">
          <el-input v-model="form.department" placeholder="请输入科室" />
        </el-form-item>
        <el-form-item label="可能疾病">
          <el-input v-model="form.possibleDiseases" placeholder="多个疾病用逗号分隔" />
        </el-form-item>
        <el-form-item label="建议">
          <el-input v-model="form.suggestions" type="textarea" :rows="3" placeholder="请输入建议" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
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
