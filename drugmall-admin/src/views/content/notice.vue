<script setup lang="ts">
import { ref, onMounted, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Top, View } from '@element-plus/icons-vue'
import { getNoticeList, createNotice, updateNotice, deleteNotice, toggleNoticeTop, getNoticeStats } from '@/api/content'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const loading = ref(false)

// 公告列表
const noticeList = ref<any[]>([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 类型选项
const typeOptions = [
  { label: '系统公告', value: 'system', tagType: 'primary' },
  { label: '活动公告', value: 'activity', tagType: 'success' },
  { label: '维护通知', value: 'maintenance', tagType: 'warning' }
]

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增公告')
const formRef = ref()
const formData = ref({
  id: '',
  title: '',
  type: 'system',
  content: '',
  isTop: 0,
  status: 1
})

// 富文本编辑器
const editorRef = shallowRef()
const toolbarConfig = {
  excludeKeys: ['uploadVideo', 'insertVideo', 'uploadImage']
}
const editorConfig = {
  placeholder: '请输入公告内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/upload',
      fieldName: 'file'
    }
  }
}

// 搜索
const searchKeyword = ref('')
const searchType = ref('')

// 统计
const stats = ref({
  total: 0,
  published: 0,
  top: 0,
  draft: 0
})

// 加载公告列表
const loadNoticeList = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (searchType.value) params.type = searchType.value
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const data = await getNoticeList(params)
    noticeList.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const data = await getNoticeStats()
    stats.value = {
      total: data.total || 0,
      published: data.published || 0,
      top: data.top || 0,
      draft: data.draft || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 编辑器创建
const handleCreated = (editor: any) => {
  editorRef.value = editor
}

// 初始化
onMounted(() => {
  loadNoticeList()
  loadStats()
  return () => {
    const editor = editorRef.value
    if (editor) {
      editor.destroy()
    }
  }
})

// 获取类型标签
const getTypeLabel = (type: string) => {
  const item = typeOptions.find(t => t.value === type)
  return item?.label || type
}

// 获取类型标签样式
const getTypeTagType = (type: string) => {
  const item = typeOptions.find(t => t.value === type)
  return item?.tagType as any || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadNoticeList()
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  searchType.value = ''
  currentPage.value = 1
  loadNoticeList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增公告'
  formData.value = { id: '', title: '', type: 'system', content: '', isTop: 0, status: 1 }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑公告'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadNoticeList()
      loadStats()
    } catch (error) {
      console.error('删除公告失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 置顶/取消置顶
const handleToggleTop = async (row: any) => {
  const newIsTop = row.isTop === 1 ? 0 : 1
  try {
    await toggleNoticeTop(row.id, newIsTop)
    row.isTop = newIsTop
    ElMessage.success(newIsTop === 1 ? '置顶成功' : '取消置顶成功')
    loadStats()
  } catch (error) {
    console.error('置顶操作失败:', error)
    ElMessage.error('操作失败')
  }
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
      <h3 style="margin-bottom: 16px;">${row.title}</h3>
      <div style="color: #666; line-height: 1.8;">
        ${row.content}
      </div>
      <div style="margin-top: 20px; padding-top: 16px; border-top: 1px solid #eee; font-size: 12px; color: #999;">
        <p>发布时间：${row.publishTime || '未发布'}</p>
        <p>浏览量：${row.views}</p>
      </div>
    </div>
  `, '公告预览', {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: true,
    customClass: 'notice-preview-dialog'
  })
}

// 提交表单
const submitForm = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formData.value.id) {
          await updateNotice(formData.value.id, formData.value)
          ElMessage.success('编辑成功')
        } else {
          await createNotice(formData.value)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadNoticeList()
        loadStats()
      } catch (error) {
        console.error('保存公告失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}
</script>

<style scoped lang="scss">
.notice-page {
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

:deep(.notice-preview-dialog) {
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

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
