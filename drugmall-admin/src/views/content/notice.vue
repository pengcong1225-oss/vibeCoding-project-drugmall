<script setup lang="ts">
import { ref, onMounted, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Top, View } from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

// 公告列表
const noticeList = ref([
  { id: '1', title: '关于双十二促销活动的公告', type: 'activity', content: '双十二狂欢节即将到来...', isTop: 1, status: 1, views: 12567, publishTime: '2024-12-01 10:00:00' },
  { id: '2', title: '系统维护通知', type: 'maintenance', content: '为了提供更好的服务...', isTop: 0, status: 1, views: 8923, publishTime: '2024-11-28 15:30:00' },
  { id: '3', title: '药品价格调整公告', type: 'system', content: '根据国家相关政策...', isTop: 0, status: 1, views: 6789, publishTime: '2024-11-25 09:00:00' },
  { id: '4', title: '春节期间配送安排', type: 'activity', content: '春节即将到来...', isTop: 1, status: 0, views: 0, publishTime: '' },
  { id: '5', title: '隐私政策更新说明', type: 'system', content: '为了更好地保护用户隐私...', isTop: 0, status: 1, views: 3456, publishTime: '2024-11-20 14:00:00' }
])

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
  total: 5,
  published: 4,
  top: 2,
  draft: 1
})

// 编辑器创建
const handleCreated = (editor: any) => {
  editorRef.value = editor
}

// 销毁编辑器
onMounted(() => {
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
  ElMessage.success('查询成功')
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  searchType.value = ''
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
  }).then(() => {
    const index = noticeList.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      noticeList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  })
}

// 置顶/取消置顶
const handleToggleTop = (row: any) => {
  row.isTop = row.isTop === 1 ? 0 : 1
  ElMessage.success(row.isTop === 1 ? '置顶成功' : '取消置顶成功')
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
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (formData.value.id) {
        // 编辑
        const index = noticeList.value.findIndex(item => item.id === formData.value.id)
        if (index > -1) {
          noticeList.value[index] = { ...formData.value }
        }
        ElMessage.success('编辑成功')
      } else {
        // 新增
        const newNotice = {
          ...formData.value,
          id: String(Date.now()),
          views: 0,
          publishTime: ''
        }
        noticeList.value.push(newNotice)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
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