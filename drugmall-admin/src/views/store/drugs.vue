<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { getStoreDrugs, addStoreDrug, updateStoreDrug, removeStoreDrug } from '@/api/store'
import type { StoreDrugItem, StoreDrugQueryParams } from '@/types/store'

const route = useRoute()
const loading = ref(false)
const storeId = Number(route.params.id)
const storeName = ref('')
const tableData = ref<StoreDrugItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive<StoreDrugQueryParams>({
  keyword: '',
  status: undefined
})

const addDialogVisible = ref(false)
const addForm = reactive({
  drugId: undefined as number | undefined,
  price: 0,
  stock: 0
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getStoreDrugs(storeId, {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取门店药品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  Object.assign(searchForm, { keyword: '', status: undefined })
  pageNum.value = 1
  getList()
}

const handleAdd = () => {
  addForm.drugId = undefined
  addForm.price = 0
  addForm.stock = 0
  addDialogVisible.value = true
}

const submitAdd = async () => {
  if (!addForm.drugId) {
    ElMessage.warning('请选择药品')
    return
  }
  await addStoreDrug(storeId, { drugId: addForm.drugId!, price: addForm.price, stock: addForm.stock })
  ElMessage.success('添加成功')
  addDialogVisible.value = false
  getList()
}

const handleRemove = async (row: StoreDrugItem) => {
  try {
    await ElMessageBox.confirm(`确定要从门店药品中移除 "${row.drugName}" 吗？`, '确认移除', { type: 'warning' })
    await removeStoreDrug(storeId, row.drugId)
    ElMessage.success('移除成功')
    getList()
  } catch {
    // 取消操作
  }
}

const handleStatusChange = async (row: StoreDrugItem) => {
  const status = row.status === 1 ? 0 : 1
  await updateStoreDrug(storeId, row.drugId, { status })
  row.status = status
  ElMessage.success(status === 1 ? '已上架' : '已下架')
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
  <div class="store-drugs-container">
    <el-page-header @back="$router.back()" title="返回" style="margin-bottom: 20px">
      <template #content>
        <span class="page-title">门店药品 - {{ storeName }}</span>
      </template>
    </el-page-header>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.keyword" placeholder="药品名称/规格" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
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
          <span class="card-title">药品列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加药品</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="药品ID" prop="drugId" width="100" />
        <el-table-column label="药品名称" prop="drugName" min-width="160" />
        <el-table-column label="规格" prop="specification" width="120" />
        <el-table-column label="厂家" prop="manufacturer" min-width="140" show-overflow-tooltip />
        <el-table-column label="售价" prop="price" width="100" align="right">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="库存" prop="stock" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock < 10 ? 'danger' : row.stock < 50 ? 'warning' : 'success'" size="small">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleRemove(row)">移除</el-button>
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

    <!-- 添加药品对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加药品" width="500px" destroy-on-close>
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="选择药品">
          <el-select v-model="addForm.drugId" placeholder="搜索药品" filterable style="width: 100%">
            <el-option label="阿莫西林胶囊" :value="1" />
            <el-option label="布洛芬缓释胶囊" :value="2" />
            <el-option label="对乙酰氨基酚片" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价">
          <el-input-number v-model="addForm.price" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="addForm.stock" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.store-drugs-container { padding: 20px; }
.page-title { font-size: 18px; font-weight: 600; }
.search-card { margin-bottom: 20px; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card .card-title { font-size: 16px; font-weight: 600; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
