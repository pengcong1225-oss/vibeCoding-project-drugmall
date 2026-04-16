<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import type { ProductFormData } from '@/types/product'
import type { UploadFile, FormInstance } from 'element-plus'
import { getProductDetail, createProduct, updateProduct, getCategoryList, getBrandList } from '@/api/product'

const route = useRoute()
const router = useRouter()
const productId = route.query.id as string

// 表单引用
const formRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 商品表单数据
const productForm = reactive<ProductFormData>({
  id: '',
  productCode: '',
  productName: '',
  categoryId: '',
  brandId: '',
  mainImage: '',
  images: [],
  detail: '',
  price: 0,
  originalPrice: undefined,
  stock: 0,
  warningStock: 10,
  isRx: 0,
  approvalNumber: '',
  manufacturer: '',
  spec: '',
  unit: '',
  status: 1,
  sortOrder: 0
})

// 表单校验规则
const formRules = {
  productName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  mainImage: [{ required: true, message: '请上传主图', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  isRx: [{ required: true, message: '请选择是否处方药', trigger: 'change' }],
  approvalNumber: [{ required: true, message: '请输入批准文号', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }],
  spec: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

// 分类选项
const categoryOptions = ref<any[]>([])

// 品牌选项
const brandOptions = ref<any[]>([])

// 规格SKU
const skuList = ref<{ spec: string; price: number; stock: number }[]>([])

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

// 获取商品详情
const getDetail = async () => {
  if (!productId) return
  
  loading.value = true
  try {
    const data = await getProductDetail(productId)
    Object.assign(productForm, data)
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (productId) {
      await updateProduct(productId, productForm)
      ElMessage.success('修改成功')
    } else {
      await createProduct(productForm)
      ElMessage.success('创建成功')
    }
    router.back()
  } catch (error) {
    console.error('提交商品失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 添加SKU
const addSku = () => {
  skuList.value.push({ spec: '', price: 0, stock: 0 })
}

// 删除SKU
const removeSku = (index: number) => {
  skuList.value.splice(index, 1)
}

// 返回
const goBack = () => {
  router.back()
}

// 图片上传成功
const handleMainImageSuccess = (url: string) => {
  productForm.mainImage = url
  formRef.value?.validateField('mainImage')
}

onMounted(() => {
  loadOptions()
  getDetail()
})
</script>

<template>
  <div class="product-edit-container">
    <!-- 头部 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2 class="page-title">{{ productId ? '编辑药品' : '新增药品' }}</h2>
    </div>

    <!-- 表单 -->
    <el-card v-loading="loading" shadow="never">
      <el-form
        ref="formRef"
        :model="productForm"
        :rules="formRules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-form-item label="药品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入药品名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="药品编码">
          <el-input v-model="productForm.productCode" placeholder="系统自动生成" disabled />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="productForm.categoryId"
            :options="categoryOptions"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="品牌">
          <el-select v-model="productForm.brandId" placeholder="请选择品牌" clearable style="width: 100%">
            <el-option v-for="item in brandOptions" :key="item.id" :label="item.brandName || item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否处方药" prop="isRx">
          <el-radio-group v-model="productForm.isRx">
            <el-radio :label="0">非处方药(OTC)</el-radio>
            <el-radio :label="1">处方药(RX)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="批准文号" prop="approvalNumber">
          <el-input v-model="productForm.approvalNumber" placeholder="请输入批准文号" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="productForm.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="productForm.spec" placeholder="如：0.25g*24粒" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="productForm.unit" placeholder="如：盒、瓶" style="width: 120px" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" :step="1" />
          <span style="margin-left: 8px; color: #909399;">元</span>
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" :step="1" />
          <span style="margin-left: 8px; color: #909399;">元</span>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="库存预警值">
          <el-input-number v-model="productForm.warningStock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="主图" prop="mainImage">
          <el-upload
            class="main-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="(res: any) => handleMainImageSuccess(res.url)"
          >
            <img v-if="productForm.mainImage" :src="productForm.mainImage" class="main-image" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="productForm.detail" type="textarea" :rows="6" placeholder="请输入商品详情" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="productForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="productForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ productId ? '保存修改' : '立即创建' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.product-edit-container {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  .page-title {
    flex: 1;
    margin: 0 20px;
    font-size: 20px;
    font-weight: 600;
  }
}

.main-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  .uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
    line-height: 120px;
  }

  .main-image {
    width: 120px;
    height: 120px;
    display: block;
    object-fit: cover;
  }
}
</style>
