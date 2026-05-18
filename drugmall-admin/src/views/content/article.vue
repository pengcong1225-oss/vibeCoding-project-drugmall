<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, View } from '@element-plus/icons-vue'
import { getArticleList, createArticle, updateArticle, deleteArticle, getArticleCategories } from '@/api/content'
import type { ArticleInfo } from '@/types/content'

const loading = ref(false)

// 统计数据
const stats = ref({
  total: 0,
  published: 0,
  top: 0,
  draft: 0
})

// 分类数据
const categoryList = ref<Array<{ id: string; name: string; icon?: string; articleCount?: number }>>([])

// 资讯列表
const articleList = ref<ArticleInfo[]>([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增资讯')
const formRef = ref()
const submitLoading = ref(false)

const formData = ref({
  id: '',
  title: '',
  categoryId: '',
  cover: '',
  summary: '',
  content: '',
  tags: [] as string[],
  isRecommend: 0,
  status: 1
})

// 搜索
const searchKeyword = ref('')
const searchCategory = ref('')

// 分类弹窗
const categoryDialogVisible = ref(false)

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

onMounted(() => {
  loadArticleList()
  loadCategories()
})

// 获取分类名称
const getCategoryName = (categoryId: string) => {
  const category = categoryList.value.find(c => c.id === categoryId)
  return category?.name || '-'
}

// 加载分类
const loadCategories = async () => {
  try {
    categoryList.value = await getArticleCategories()
  } catch {
    console.error('加载分类失败')
  }
}

// 获取资讯列表
const loadArticleList = async () => {
  loading.value = true
  try {
    const res = await getArticleList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: searchCategory.value
    })
    articleList.value = res.list
    total.value = res.total
  } catch {
    console.error('获取资讯列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadArticleList()
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  searchCategory.value = ''
  currentPage.value = 1
  loadArticleList()
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadArticleList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadArticleList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增资讯'
  formData.value = { id: '', title: '', categoryId: '', cover: '', summary: '', content: '', tags: [], isRecommend: 0, status: 1 }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: ArticleInfo) => {
  dialogTitle.value = '编辑资讯'
  formData.value = { ...row, content: row.content || '', tags: row.tags || [], isRecommend: row.isRecommend ?? 0 }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: ArticleInfo) => {
  try {
    await ElMessageBox.confirm(`确定要删除资讯"${row.title}"吗？`, '确认删除', { type: 'warning' })
    await deleteArticle(row.id)
    ElMessage.success('删除成功')
    loadArticleList()
  } catch {
    // 取消
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  try {
    if (formData.value.id) {
      await updateArticle(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createArticle(formData.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadArticleList()
  } catch {
    console.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

// 上下架
const handleToggleStatus = async (row: ArticleInfo) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateArticle(row.id, { status: newStatus })
    row.status = newStatus
    ElMessage.success('操作成功')
  } catch {
    console.error('操作失败')
  }
}

// 置顶
const handleToggleTop = async (row: ArticleInfo) => {
  const newTop = row.isTop ? 0 : 1
  try {
    await updateArticle(row.id, { isTop: newTop })
    row.isTop = newTop
    ElMessage.success('操作成功')
  } catch {
    console.error('操作失败')
  }
}

// 预览
const handlePreview = (_row: ArticleInfo) => {
  ElMessage.info('预览功能开发中')
}

// 添加标签
const handleAddTag = () => {
  if (!formData.value.tags) formData.value.tags = []
  formData.value.tags.push('')
}

// 移除标签
const handleRemoveTag = (index: number) => {
  formData.value.tags.splice(index, 1)
}

// 封面上传
const handleCoverUpload = (response: any) => {
  formData.value.cover = response.url || response.data?.url || ''
}

// 管理分类
const handleManageCategory = () => {
  categoryDialogVisible.value = true
}
</script>

<template>
  <div class="article-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总资讯</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67C23A">{{ stats.published }}</div>
          <div class="stat-label">已发布</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #E6A23C">{{ stats.top }}</div>
          <div class="stat-label">置顶</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #909399">{{ stats.draft }}</div>
          <div class="stat-label">草稿</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索 -->
    <el-card class="search-card" shadow="never">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="标题/内容" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchCategory" placeholder="全部分类" clearable style="width: 140px">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
          <el-button type="primary" @click="handleManageCategory">管理分类</el-button>
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never" style="margin-top: 20px">
      <el-table :data="articleList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image :src="row.cover" style="width: 60px; height: 40px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="100">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isRecommend" type="warning" size="small">推荐</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.isTop" :active-value="1" :inactive-value="0" @change="handleToggleTop(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="handleCoverUpload">
            <el-image v-if="formData.cover" :src="formData.cover" style="width: 120px; height: 80px" fit="cover" />
            <el-button v-else type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="formData.summary" type="textarea" :rows="3" maxlength="300" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="12" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="标签">
          <el-tag v-for="(tag, index) in formData.tags" :key="index" closable @close="handleRemoveTag(index)" style="margin-right: 8px">
            {{ tag }}
          </el-tag>
          <el-button size="small" @click="handleAddTag">+ 添加标签</el-button>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="500px">
      <el-table :data="categoryList" stripe>
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="articleCount" label="文章数" width="80" align="center" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.article-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
