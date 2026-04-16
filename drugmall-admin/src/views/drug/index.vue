<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import type { Product, ProductQueryParams } from '@/types/product'
import { getProductList, deleteProduct, updateProductStatus, getCategoryList, getBrandList } from '@/api/product'

const router = useRouter()

// 搜索表单
const searchForm = reactive<ProductQueryParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  categoryId: '',
  brandId: '',
  isRx: undefined,
  status: undefined
})

// 表格数据
const loading = ref(false)
const tableData = ref<Product[]>([])
const total = ref(0)

// 分类和品牌选项
const categoryOptions = ref<{ id: string; name: string }[]>([])
const brandOptions = ref<{ id: string; name: string }[]>([])

// 加载分类和品牌下拉选项
const loadOptions = async () => {
  try {
    const [categories, brandRes] = await Promise.all([
      getCategoryList(),
      getBrandList({ pageNum: 1, pageSize: 100 })
    ])
    categoryOptions.value = categories
    brandOptions.value = brandRes.list
  } catch (error) {
    console.error('加载选项数据失败:', error)
  }
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      keyword: searchForm.keyword,
      categoryId: searchForm.categoryId,
      brandId: searchForm.brandId,
      isRx: searchForm.isRx,
      status: searchForm.status
    })
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取药品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    pageNum: 1,
    pageSize: 10,
    keyword: '',
    categoryId: '',
    brandId: '',
    isRx: undefined,
    status: undefined
  })
  getList()
}

// 分页
const handleSizeChange = (size: number) => {
  searchForm.pageSize = size
  getList()
}

const handleCurrentChange = (page: number) => {
  searchForm.pageNum = page
  getList()
}

// 新增药品
const handleAdd = () => {
  router.push('/drug/edit')
}

// 编辑药品
const handleEdit = (row: Product) => {
  router.push(`/drug/edit?id=${row.id}`)
}

// 查看详情
const handleView = (row: Product) => {
  router.push(`/drug/edit?id=${row.id}&readonly=true`)
}

// 删除药品
const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除药品 "${row.productName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning' as const
      }
    )
    
    // 调用删除API
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch {
    // 取消操作
  }
}

// 上下架
const handleStatusChange = async (row: Product) => {
  const action = row.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(
      `确定要${action}该药品吗？`,
      '提示',
      { type: 'warning' }
    )
    
    // 调用上下架API
    const newStatus = row.status === 1 ? 0 : 1
    await updateProductStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadOptions()
  getList()
})
</script>

<template>
  <div class="product-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="药品名称/编码/厂家"
            clearable
            @keyup.enter="handleSearch"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width: 140px">
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处方药">
          <el-select v-model="searchForm.isRx" placeholder="全部" clearable style="width: 120px">
            <el-option label="处方药" :value="1" />
            <el-option label="非处方药" :value="0" />
          </el-select>
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

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">药品列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增药品</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="药品信息" min-width="280">
          <template #default="{ row }">
            <div class="product-info">
              <el-image 
                :src="row.mainImage" 
                class="product-image"
                fit="cover"
              />
              <div class="product-meta">
                <div class="product-name">
                  {{ row.productName }}
                  <el-tag v-if="row.isRx === 1" type="danger" size="small" effect="plain">RX</el-tag>
                </div>
                <div class="product-code">编码: {{ row.productCode }}</div>
                <div class="product-spec">{{ row.spec }} / {{ row.manufacturer }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类/品牌" width="140">
          <template #default="{ row }">
            <div>{{ row.categoryName }}</div>
            <div class="brand-name">{{ row.brandName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="150" align="right">
          <template #default="{ row }">
            <div class="price-info">
              <div class="current-price">¥{{ row.price.toFixed(2) }}</div>
              <div v-if="row.originalPrice" class="original-price">¥{{ row.originalPrice.toFixed(2) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.stock <= row.warningStock ? 'danger' : 'success'" 
              size="small"
            >
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salesCount" label="销量" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              link 
              :type="row.status === 1 ? 'warning' : 'success'" 
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="searchForm.pageNum"
          v-model:page-size="searchForm.pageSize"
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
.product-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
  }
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .product-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
  }

  .product-meta {
    .product-name {
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .product-code {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }

    .product-spec {
      font-size: 12px;
      color: #606266;
    }
  }
}

.brand-name {
  font-size: 12px;
  color: #909399;
}

.price-info {
  .current-price {
    font-weight: 600;
    color: #f56c6c;
  }

  .original-price {
    font-size: 12px;
    color: #909399;
    text-decoration: line-through;
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
