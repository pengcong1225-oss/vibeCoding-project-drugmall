<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, View, Rank, Search, RefreshRight, TrendCharts, Device } from '@element-plus/icons-vue'
import Sortable from 'sortablejs'
import VueCropper from 'vue-cropper/lib/vue-cropper.vue'
import 'vue-cropper/dist/index.css'

// Banner列表
const bannerList = ref([
  { id: '1', title: '双十二狂欢节', image: 'https://placeholder.com/800x400/1890ff/fff?text=双十二狂欢', link: '/activity/1212', sort: 1, status: 1, clicks: 2345, views: 45678, position: 'home', device: 'all' },
  { id: '2', title: '健康节大促', image: 'https://placeholder.com/800x400/52c41a/fff?text=健康节', link: '/activity/health', sort: 2, status: 1, clicks: 1890, views: 32156, position: 'home', device: 'all' },
  { id: '3', title: '新人专享优惠', image: 'https://placeholder.com/800x400/fa8c16/fff?text=新人专享', link: '/activity/newuser', sort: 3, status: 1, clicks: 5678, views: 89234, position: 'home', device: 'mobile' },
  { id: '4', title: '处方药专区', image: 'https://placeholder.com/800x400/722ed1/fff?text=处方药专区', link: '/category/rx', sort: 4, status: 0, clicks: 1234, views: 23456, position: 'category', device: 'all' },
  { id: '5', title: '家庭常备药品', image: 'https://placeholder.com/800x400/13c2c2/fff?text=家庭常备', link: '/category/home', sort: 5, status: 1, clicks: 3456, views: 56789, position: 'home', device: 'pc' }
])

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增Banner')
const formRef = ref()
const formData = ref({
  id: '',
  title: '',
  image: '',
  link: '',
  sort: 0,
  status: 1,
  position: 'home',
  device: 'all'
})

// 图片裁剪相关
const cropperVisible = ref(false)
const cropperImage = ref('')
const cropperRef = ref()
const cropOption = ref({
  img: '',
  outputSize: 1,
  outputType: 'png',
  canScale: true,
  autoCrop: true,
  autoCropWidth: 800,
  autoCropHeight: 400,
  fixedBox: false,
  fixed: true,
  fixedNumber: [2, 1]
})

// 预览相关
const previewVisible = ref(false)
const previewImage = ref('')
const previewDevice = ref('pc')

// 点击统计弹窗
const statsDialogVisible = ref(false)
const currentStats = ref<any>({})

// 广告位选项
const positionOptions = [
  { label: '首页轮播', value: 'home' },
  { label: '分类页', value: 'category' },
  { label: '购物车页', value: 'cart' },
  { label: '个人中心', value: 'user' }
]

// 设备选项
const deviceOptions = [
  { label: '全部设备', value: 'all' },
  { label: '仅PC端', value: 'pc' },
  { label: '仅移动端', value: 'mobile' }
]

// 初始化拖拽排序
const initSortable = () => {
  nextTick(() => {
    const tbody = document.querySelector('.banner-table .el-table__body-wrapper tbody')
    if (tbody) {
      Sortable.create(tbody as HTMLElement, {
        handle: '.drag-handle',
        animation: 150,
        onEnd: (evt) => {
          if (evt.oldIndex !== evt.newIndex) {
            const item = bannerList.value.splice(evt.oldIndex!, 1)[0]
            bannerList.value.splice(evt.newIndex!, 0, item)
            // 更新排序号
            bannerList.value.forEach((item, index) => {
              item.sort = index + 1
            })
            ElMessage.success('排序已更新')
          }
        }
      })
    }
  })
}

onMounted(() => {
  initSortable()
})

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增Banner'
  formData.value = { id: '', title: '', image: '', link: '', sort: bannerList.value.length + 1, status: 1, position: 'home', device: 'all' }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑Banner'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除Banner"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = bannerList.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      bannerList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  })
}

// 切换状态
const handleStatusChange = (row: any) => {
  ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
}

// 打开图片裁剪
const openCropper = () => {
  if (!formData.value.image) {
    // 如果没有图片，选择文件
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*'
    input.onchange = (e: any) => {
      const file = e.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
          cropOption.value.img = e.target?.result as string
          cropperVisible.value = true
        }
        reader.readAsDataURL(file)
      }
    }
    input.click()
  } else {
    cropOption.value.img = formData.value.image
    cropperVisible.value = true
  }
}

// 确认裁剪
const confirmCrop = () => {
  cropperRef.value.getCropData((data: string) => {
    formData.value.image = data
    cropperVisible.value = false
    ElMessage.success('图片裁剪成功')
  })
}

// 多设备预览
const handlePreview = (row: any, device: string) => {
  previewImage.value = row.image
  previewDevice.value = device
  previewVisible.value = true
}

// 查看点击统计
const handleViewStats = (row: any) => {
  currentStats.value = row
  statsDialogVisible.value = true
}

// 提交表单
const submitForm = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (formData.value.id) {
        // 编辑
        const index = bannerList.value.findIndex(item => item.id === formData.value.id)
        if (index > -1) {
          bannerList.value[index] = { ...formData.value }
        }
        ElMessage.success('编辑成功')
      } else {
        // 新增
        const newBanner = {
          ...formData.value,
          id: String(Date.now()),
          clicks: 0,
          views: 0
        }
        bannerList.value.push(newBanner)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
    }
  })
}
</script>

<template>
  <div class="banner-page">
    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增Banner</el-button>
      <el-button :icon="TrendCharts" @click="handleViewStats">点击统计</el-button>
    </div>

    <!-- Banner列表 -->
    <el-card shadow="never">
      <el-table :data="bannerList" v-loading="false" stripe border class="banner-table">
        <el-table-column type="index" label="排序" width="70" align="center">
          <template #default="{ $index }">
            <div class="drag-handle">
              <el-icon>
                <Rank />
              </el-icon>
              <span>{{ $index + 1 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Banner图片" width="180" align="center">
          <template #default="{ row }">
            <el-image :src="row.image" fit="cover"
              style="width: 150px; height: 75px; border-radius: 4px; cursor: pointer" @click="handlePreview(row, 'pc')" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column label="设备类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.device === 'all' ? 'primary' : row.device === 'pc' ? 'success' : 'warning'">
              {{ row.device === 'all' ? '全部' : row.device === 'pc' ? 'PC' : '移动端' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="位置" width="100" align="center">
          <template #default="{ row }">
            {{ positionOptions.find(p => p.value === row.position)?.label || row.position }}
          </template>
        </el-table-column>
        <el-table-column prop="link" label="链接" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" :href="row.link" target="_blank">{{ row.link }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="数据" width="140" align="center">
          <template #default="{ row }">
            <div class="stats">
              <span class="views"><el-icon>
                  <View />
                </el-icon> {{ row.views }}</span>
              <span class="clicks"><el-icon>
                  <TrendCharts />
                </el-icon> {{ row.clicks }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" :icon="Device" @click="handlePreview(row, 'mobile')">预览</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="formData" label-width="100px" :rules="{
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        image: [{ required: true, message: '请上传图片', trigger: 'change' }],
        link: [{ required: true, message: '请输入链接', trigger: 'blur' }]
      }">
        <el-form-item label="Banner标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入Banner标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="位置" prop="position">
          <el-select v-model="formData.position" placeholder="选择展示位置" style="width: 100%">
            <el-option v-for="item in positionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="device">
          <el-radio-group v-model="formData.device">
            <el-radio v-for="item in deviceOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Banner图片" prop="image">
          <div class="image-upload-wrapper">
            <div v-if="formData.image" class="image-preview">
              <img :src="formData.image" alt="banner">
              <div class="image-actions">
                <el-button type="primary" circle size="small" @click="openCropper">
                  <el-icon>
                    <Edit />
                  </el-icon>
                </el-button>
                <el-button type="danger" circle size="small" @click="formData.image = ''">
                  <el-icon>
                    <Delete />
                  </el-icon>
                </el-button>
              </div>
            </div>
            <div v-else class="upload-placeholder" @click="openCropper">
              <el-icon :size="28">
                <Plus />
              </el-icon>
              <div class="upload-text">点击上传图片</div>
              <div class="upload-tip">建议尺寸 800x400px，支持裁剪</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="跳转链接" prop="link">
          <el-input v-model="formData.link" placeholder="请输入跳转链接，如 /activity/xxx" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="1" :max="99" />
          <span class="form-tip">数字越小排序越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 图片裁剪弹窗 -->
    <el-dialog v-model="cropperVisible" title="图片裁剪" width="900px" destroy-on-close>
      <div class="cropper-container">
        <vue-cropper ref="cropperRef" :img="cropOption.img" :output-size="cropOption.outputSize"
          :output-type="cropOption.outputType" :info="true" :can-scale="cropOption.canScale"
          :auto-crop="cropOption.autoCrop" :auto-crop-width="cropOption.autoCropWidth"
          :auto-crop-height="cropOption.autoCropHeight" :fixed="cropOption.fixed"
          :fixed-number="cropOption.fixedNumber" :fixed-box="cropOption.fixedBox" :center-box="true" :full="true"
          :can-move="true" :can-move-box="true" />
      </div>
      <div class="cropper-tools">
        <el-button @click="cropperRef?.rotateLeft()">向左旋转</el-button>
        <el-button @click="cropperRef?.rotateRight()">向右旋转</el-button>
        <el-button @click="cropperRef?.zoom(0.1)">放大</el-button>
        <el-button @click="cropperRef?.zoom(-0.1)">缩小</el-button>
        <el-button @click="cropperRef?.reset()">重置</el-button>
      </div>
      <template #footer>
        <el-button @click="cropperVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCrop">确认裁剪</el-button>
      </template>
    </el-dialog>

    <!-- 多设备预览弹窗 -->
    <el-dialog v-model="previewVisible" title="设备预览" width="900px">
      <div class="device-preview">
        <div class="device-tabs">
          <el-radio-group v-model="previewDevice">
            <el-radio-button label="pc">PC端</el-radio-button>
            <el-radio-button label="tablet">平板</el-radio-button>
            <el-radio-button label="mobile">手机端</el-radio-button>
          </el-radio-group>
        </div>
        <div class="preview-content" :class="previewDevice">
          <img :src="previewImage" alt="preview">
        </div>
      </div>
    </el-dialog>

    <!-- 点击统计弹窗 -->
    <el-dialog v-model="statsDialogVisible" title="点击统计" width="800px">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stats-card">
            <div class="stats-title">今日点击</div>
            <div class="stats-value">1,234</div>
            <div class="stats-change up">+12.5%</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stats-card">
            <div class="stats-title">本周点击</div>
            <div class="stats-value">8,567</div>
            <div class="stats-change up">+8.3%</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stats-card">
            <div class="stats-title">本月点击</div>
            <div class="stats-value">32,456</div>
            <div class="stats-change down">-2.1%</div>
          </div>
        </el-col>
      </el-row>
      <div class="stats-chart">
        <div class="chart-title">近7天点击趋势</div>
        <!-- 这里可以集成图表组件 -->
        <div class="chart-placeholder">图表区域</div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.banner-page {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
}

.banner-table {
  .drag-handle {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    cursor: move;
    color: #909399;

    &:hover {
      color: #409eff;
    }
  }

  .stats {
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 12px;

    .views,
    .clicks {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #606266;
    }
  }
}

// 图片上传
.image-upload-wrapper {
  .image-preview {
    position: relative;
    width: 400px;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .image-actions {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      display: flex;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.3s;
    }

    &:hover .image-actions {
      opacity: 1;
    }
  }

  .upload-placeholder {
    width: 400px;
    height: 200px;
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #909399;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409eff;
      color: #409eff;
    }

    .upload-text {
      font-size: 14px;
      margin-top: 8px;
    }

    .upload-tip {
      font-size: 12px;
      margin-top: 4px;
    }
  }
}

// 裁剪器
.cropper-container {
  height: 400px;
  background: #f5f7fa;
}

.cropper-tools {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: center;
}

// 设备预览
.device-preview {
  .device-tabs {
    text-align: center;
    margin-bottom: 20px;
  }

  .preview-content {
    margin: 0 auto;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f7fa;

    &.pc {
      width: 800px;
      height: 300px;
    }

    &.tablet {
      width: 600px;
      height: 300px;
    }

    &.mobile {
      width: 375px;
      height: 300px;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
}

// 统计
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  color: #fff;
  text-align: center;

  .stats-title {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .stats-value {
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .stats-change {
    font-size: 12px;
    opacity: 0.8;

    &.up {
      color: #67c23a;
    }

    &.down {
      color: #f56c6c;
    }
  }
}

.stats-chart {
  margin-top: 20px;

  .chart-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
  }

  .chart-placeholder {
    height: 200px;
    background: #f5f7fa;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #909399;
  }
}

.form-tip {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}
</style>
