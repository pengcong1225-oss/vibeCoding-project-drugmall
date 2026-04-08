<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import type { ProductFormData } from '@/types/product'
import type { UploadFile, FormInstance } from 'element-plus'

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
const categoryOptions = ref([
  { id: '1', name: '感冒药', children: [
    { id: '11', name: '风寒感冒' },
    { id: '12', name: '风热感冒' }
  ]},
  { id: '2', name: '消化系统', children: [] },
  { id: '3', name: '心脑血管', children: [] },
  { id: '4', name: '维生素', children: [] },
  { id: '5', name: '医疗器械', children: [] }
])

// 品牌选项
const brandOptions = ref([
  { id: '1', name: '修正药业' },
  { id: '2', name: '同仁堂' },
  { id: '3', name: '白云山' },
  { id: '4', name: '云南白药' }
])

// 规格SKU
const skuList = ref([
  { spec: '0.25g*24粒', price: 25.5, stock: 100 },
  { spec: '0.25g*48粒', price: 45.0, stock: 80 }
])

// 获取商品详情
const getDetail = async () => {
  if (!productId) return
  
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    Object.assign(productForm, {
      id: productId,
      productCode: 'P000001',
      productName: '阿莫西林胶囊',
      categoryId: '11',
      brandId: '1',
      mainImage: 'https://via.placeholder.com/100x100',
      images: [
        'https://via.placeholder.com/100x100',
        'https://via.placeholder.com/100x100',
        'https://via.placeholder.com/100x100'
