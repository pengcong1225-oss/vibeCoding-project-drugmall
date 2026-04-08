<script setup lang="ts">
import { ref, onMounted, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Top, View } from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

// 分类数据
const categoryList = ref([
  { id: '1', name: '健康常识', icon: 'FirstAidKit', sort: 1, status: 1 },
  { id: '2', name: '疾病预防', icon: 'Warning', sort: 2, status: 1 },
  { id: '3', name: '用药指导', icon: 'Medicine', sort: 3, status: 1 },
  { id: '4', name: '养生保健', icon: 'Apple', sort: 4, status: 1 },
  { id: '5', name: '慢病管理', icon: 'Timer', sort: 5, status: 1 }
])

// 资讯列表
const articleList = ref([
  { id: '1', title: '冬季感冒高发，如何预防？', categoryId: '1', categoryName: '健康常识', cover: 'https://placeholder.com/300x200/409eff/fff?text=健康', summary: '冬季是感冒的高发季节，做好预防措施很重要...', tags: ['感冒', '预防', '冬季'], isRecommend: 1, status: 1, views: 12567, publishTime: '2024-12-01 10:00:00' },
  { id: '2', title: '高血压患者的日常饮食建议', categoryId: '5', categoryName: '慢病管理', cover: 'https://placeholder.com/300x200/67c23a/fff?text=高血压', summary: '高血压患者需要注意饮食控制，少吃盐多吃蔬菜...', tags: ['高血压', '饮食', '健康'], isRecommend: 1, status: 1, views: 8923, publishTime: '2024-11-28 15:30:00' },
  { id: '3', title: '儿童用药安全指南', categoryId: '3', categoryName: '用药指导', cover: 'https://placeholder.com/300x200/fa8c16/fff?text=用药', summary: '儿童用药需要特别注意剂量和禁忌...', tags: ['儿童', '用药', '安全'], isRecommend: 0, status: 1, views: 6789, publishTime: '2024-11-25 09:00:00' },
  { id: '4', title: '春季养生小贴士', categoryId: '4', categoryName: '养生保健', cover: 'https://placeholder.com/300x200/722ed1/fff?text=养生', summary: '春季养生要注意养肝护肝...', tags: ['养生', '春季', '健康'], isRecommend: 0, status: 0, views: 0, publishTime: '' },
  { id: '5', title: '糖尿病并发症的预防', categoryId: '5', categoryName: '慢病管理', cover: 'https://placeholder.com/300x200/13c2c2/fff?text=糖尿病', summary: '糖尿病患者需要定期检查预防并发症...', tags: ['糖尿病', '预防', '健康'], isRecommend: 1, status: 1, views: 3456, publishTime: '2024-11-20 14:00:00' }
])

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增资讯')
const formRef = ref()
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

// 富文本编辑器
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入资讯内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/upload',
      fieldName: 'file'
    }
  }
}

// 搜索
const searchKeyword = ref('')
const searchCategory = ref('')

// 编辑器的创建和销毁
const handleCreated = (editor: any) => {
  editorRef.value = editor
}

onMounted(() => {
  return () => {
    const editor = editorRef.value
    if (editor) {
      editor.destroy()
    }
  }
})

// 分类弹窗
const categoryDialogVisible = ref(false)
const categoryForm = ref({ id: '', name: '', icon: '', sort: 1, status: 1 })

// 获取分类名称
const getCategoryName = (categoryId: string) => {
  const category = categoryList.value.find(c => c.id === categoryId)
  return category?.name || '-'
}

// 搜索
const handleSearch = () => {
  ElMessage.success('查询成功')
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  searchCategory.value = ''
}

// 管理分类
const handleManageCategory = () => {
  categoryDialogVisible.value = true
}

// 新增资讯
const handleAdd = () => {
  dialogTitle.value = '新增资讯'
  formData.value = { id: '', title: '', categoryId: '', cover: '', summary: '', content: '', tags: [], isRecommend: 0, status: 1 }
  dialogVisible.value = true
}

// 编辑资讯
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑资讯'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除资讯
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除资讯"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = articleList.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      articleList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  })
}

// 推荐/取消推荐
const handleToggleRecommend = (row: any) => {
  row.isRecommend = row.isRecommend === 1 ? 0 : 1
  ElMessage.success(row.isRecommend === 1 ? '推荐成功' : '取消推荐成功')
}

// 发布/下架
const handleToggleStatus = (row: any) => {
  row.status = row.status === 1 ? 0 : 1
  ElMessage.success(row.status === 1 ? '发布成功' : '下架成功')
}

// 预览
const handlePreview = (row: any) => {
  ElMessageBox.alert(`
    <div style="padding: 20px;">
      <div style="margin-bottom: 16px;">
        <el-tag size="small">${row.categoryName}</el-tag>
      </div>
      <h2 style="margin-bottom: 16px; font-size: 20px;">${row.title}</h2>
      <img src="${row.cover}" style="width: 100%; max-height: 300px; object-fit: cover; border-radius: 8px; margin-bottom: 16px;">
      <div style="color: #666; line-height: 1.8; margin-bottom: 16px;">
        <strong>摘要：</strong>${row.summary}
      </div>
      <div style="margin-bottom: 16px;">
        ${row.tags.map((tag: string) => `<span style="display: inline-block; padding: 2px 8px; background: #f0f2f5; border-radius: 4px; margin-right: 8px; font-size: 12px; color: #606266;">${tag}</span>`).join('')}
      </div>
      <div style="padding-top: 16px; border-top: 1px solid #eee; font-size: 12px; color: #999;">
        <p>发布时间：${row.publishTime || '未发布'}</p>
        <p>浏览量：${row.views}</p>
      </div>
    </div>
  `, '资讯预览', {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: true,
    customClass: 'article-preview-dialog'
  })
}

// 图片上传前
const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJpgOrPng) {
    ElMessage.error('只支持 JPG/PNG 格式的图片')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 封面上传成功
const handleCoverSuccess = (response: any) => {
  formData.value.cover = response.url || URL.createObjectURL(response.raw)
  ElMessage.success('上传成功')
}

// 提交表单
const submitForm = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (formData.value.id) {
        const index = articleList.value.findIndex(item => item.id === formData.value.id)
        if (index > -1) {
          articleList.value[index] = { ...formData.value, categoryName: getCategoryName(formData.value.categoryId) }
        }
        ElMessage.success('编辑成功')
      } else {
        const newArticle = {
          ...formData.value,
          id: String(Date.now()),
          categoryName: getCategoryName(formData.value.categoryId),
          views: 0,
          publishTime: ''
        }
        articleList.value.push(newArticle)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
    }
  })
}

// 添加标签
const inputTag = ref('')
const handleAddTag = () => {
  if (inputTag.value && !formData.value.tags.includes(inputTag.value)) {
    formData.value.tags.push(inputTag.value)
    inputTag.value = ''
  }
}

// 删除标签
const handleRemoveTag = (tag: string) => {
  const index = formData.value.tags.indexOf(tag)
  if (index > -1) {
    formData.value.tags.splice(index, 1)
  }
}
</script>

<template>
  <div class="article-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon blue">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">资讯总数</div>
              <div class="card-value">{{ stats.total }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon green">
              <el-icon :size="32"><Check /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">已发布</div>
              <div class="card-value">{{ stats.published }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon orange">
              <el-icon :size="32"><Star /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">推荐资讯</div>
              <div class="card-value">{{ stats.top }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon red">
              <el-icon :size="32"><DocumentCopy /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">草稿箱</div>
              <div class="card-value">{{ stats.draft }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form inline>
        <el-form-item label="分类">
          <el-select v-model="searchCategory" placeholder="全部分类" clearable style="width: 140px">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="标题/标签" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增资讯</el-button>
      <el-button @click="handleManageCategory">分类管理</el-button>
    </div>

    <!-- 资讯列表 -->
    <el-card shadow="never">
      <el-table :data="articleList" v-loading="false" stripe border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="封面" width="120" align="center">
          <template #default="{ row }">
            <el-image :src="row.cover" fit="cover" style="width: 100px; height: 67px; border-radius: 4px;" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" align="center" />
        <el-table-column label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 4px;">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览量" width="100" align="center" sortable />
        <el-table-column label="推荐" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isRecommend === 1" type="success" size="small">推荐</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" sortable />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" :icon="Top" @click="handleToggleRecommend(row)">{{ row.isRecommend === 1 ? '取消推荐' : '推荐' }}</el-button>
            <el-button link type="primary" size="small" :icon="View" @click="handlePreview(row)">预览</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="900px" destroy-on-close>
      <el-form ref="formRef" :model="formData" label-width="100px" :rules="{
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        cover: [{ required: true, message: '请上传封面', trigger: 'change' }],
        summary: [{ required: true, message: '请输入摘要', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="资讯标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入资讯标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="选择分类" style="width: 100%">
                <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资讯摘要" prop="summary">
          <el-input v-model="formData.summary" type="textarea" :rows="2" placeholder="请输入资讯摘要，将显示在列表页" maxlength="200" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="封面图片" prop="cover">
              <el-upload class="cover-uploader" action="#" :show-file-list="false" :before-upload="beforeUpload"
                :http-request="handleCoverSuccess">
                <img v-if="formData.cover" :src="formData.cover" class="uploaded-image" />
                <div v-else class="upload-placeholder">
                  <el-icon :size="24"><Plus /></el-icon>
                  <div class="upload-text">点击上传封面</div>
                  <div class="upload-tip">建议尺寸 300x200px</div>
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-select v-model="formData.tags" multiple filterable allow-create default-first-option placeholder="输入标签"
                style="width: 100%">
                <el-option v-for="tag in ['健康', '养生', '用药', '预防', '疾病']" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>
            <el-form-item label="首页推荐">
              <el-radio-group v-model="formData.isRecommend">
                <el-radio :label="1">推荐</el-radio>
                <el-radio :label="0">不推荐</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="发布状态">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">立即发布</el-radio>
                <el-radio :label="0">保存草稿</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资讯内容" prop="content" class="editor-item">
          <div class="editor-wrapper">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #e8e8e8;" />
            <Editor :defaultConfig="editorConfig" v-model="formData.content" @onCreated="handleCreated" style="height: 400px; overflow-y: hidden;" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="700px">
      <el-table :data="categoryList" border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="120" />
        <el-table-column prop="icon" label="图标" width="80" align="center" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEditCategory(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleAddCategory">新增分类</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.article-page {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;

  .stat-card {
    .card-content {
      display: flex;
      align-items: center;
      padding: 20px;
    }

    .card-icon {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;

      &.blue {
        background: rgba(64, 158, 255, 0.1);
        color: #409eff;
      }

      &.green {
        background: rgba(103, 194, 58, 0.1);
        color: #67c23a;
      }

      &.orange {
        background: rgba(230, 162, 60, 0.1);
        color: #e6a23c;
      }

      &.red {
        background: rgba(245, 108, 108, 0.1);
        color: #f56c6c;
      }
    }

    .card-info {
      flex: 1;

      .card-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .card-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}

.filter-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.toolbar {
  margin-bottom: 16px;
}

:deep(.article-preview-dialog) {
  .el-message-box__content {
    max-height: 500px;
    overflow-y: auto;
  }
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;

  :deep(.w-e-toolbar) {
    border-bottom: 1px solid #e8e8e8;
  }

  :deep(.w-e-text-container) {
    min-height: 300px;
  }
}

.cover-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409eff;
    }
  }

  .uploaded-image {
    width: 200px;
    height: 133px;
    object-fit: cover;
    border-radius: 4px;
  }

  .upload-placeholder {
    width: 200px;
    height: 133px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;

    .upload-text {
      font-size: 13px;
      margin-top: 8px;
    }

    .upload-tip {
      font-size: 12px;
      margin-top: 4px;
      color: #b0b0b0;
    }
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>