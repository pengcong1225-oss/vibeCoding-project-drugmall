<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Search, RefreshRight, View, Document } from '@element-plus/icons-vue'

// 筛选表单
const filterForm = ref({
  timeRange: [],
  type: '',
  minAmount: null as number | null,
  maxAmount: null as number | null,
  status: '',
  keyword: '',
  userType: ''
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(156)

// 交易类型枚举
const transactionTypes = [
  { label: '订单支付', value: 'order', tagType: 'success' },
  { label: '退款', value: 'refund', tagType: 'warning' },
  { label: '提现', value: 'withdrawal', tagType: 'primary' },
  { label: '充值', value: 'recharge', tagType: 'info' },
  { label: '佣金', value: 'commission', tagType: 'danger' },
  { label: '活动奖励', value: 'reward', tagType: 'success' }
]

// 交易状态枚举
const transactionStatus = [
  { label: '成功', value: 'success', tagType: 'success' },
  { label: '处理中', value: 'processing', tagType: 'warning' },
  { label: '失败', value: 'failed', tagType: 'danger' },
  { label: '已取消', value: 'cancelled', tagType: 'info' }
]

// 用户类型
const userTypes = [
  { label: '普通用户', value: 'user' },
  { label: '商户', value: 'merchant' },
  { label: '平台账户', value: 'platform' }
]

// 交易记录数据（模拟）
const generateTransactionData = () => {
  const data = []
  const types = ['order', 'refund', 'withdrawal', 'recharge', 'commission', 'reward']
  const statuses = ['success', 'processing', 'failed', 'cancelled']
  const users = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '平台商户A', '平台商户B']

  for (let i = 1; i <= 156; i++) {
    const type = types[Math.floor(Math.random() * types.length)]
    const status = Math.random() > 0.8 ? statuses[Math.floor(Math.random() * statuses.length)] : 'success'
    const amount = Math.floor(Math.random() * 10000 + 10) + Math.random()
    const createTime = new Date(Date.now() - Math.floor(Math.random() * 30 * 24 * 60 * 60 * 1000))

    data.push({
      id: `TRX${createTime.getFullYear()}${String(createTime.getMonth() + 1).padStart(2, '0')}${String(createTime.getDate()).padStart(2, '0')}${String(i).padStart(4, '0')}`,
      type,
      amount,
      status,
      orderNo: type === 'order' || type === 'refund' ? `ORD${String(Math.floor(Math.random() * 1000000)).padStart(6, '0')}` : '',
      remark: getRemarkByType(type),
      createTime: createTime.toISOString().slice(0, 19).replace('T', ' '),
      userName: users[Math.floor(Math.random() * users.length)],
      userType: Math.random() > 0.7 ? 'merchant' : 'user',
      payChannel: ['微信支付', '支付宝', '余额支付'][Math.floor(Math.random() * 3)],
      completeTime: status === 'success' ? new Date(createTime.getTime() + Math.floor(Math.random() * 60000)).toISOString().slice(0, 19).replace('T', ' ') : ''
    })
  }
  return data.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
}

const getRemarkByType = (type: string) => {
  const remarks: Record<string, string> = {
    order: '订单支付',
    refund: '订单退款',
    withdrawal: '商户提现',
    recharge: '账户充值',
    commission: '分销佣金',
    reward: '活动奖励'
  }
  return remarks[type] || '交易'
}

const transactionList = ref(generateTransactionData())

// 明细弹窗
const detailDialogVisible = ref(false)
const currentDetail = ref<any>({})

// 计算合计金额
const totalAmount = computed(() => {
  return transactionList.value
    .filter(item => item.status === 'success')
    .reduce((sum, item) => {
      return item.type === 'refund' || item.type === 'withdrawal' ? sum - item.amount : sum + item.amount
    }, 0)
})

// 获取交易类型标签文本
const getTypeLabel = (type: string) => {
  const item = transactionTypes.find(t => t.value === type)
  return item?.label || type
}

// 获取交易类型标签样式
const getTypeTagType = (type: string) => {
  const item = transactionTypes.find(t => t.value === type)
  return item?.tagType as any || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status: string) => {
  const item = transactionStatus.find(s => s.value === status)
  return item?.label || status
}

// 获取状态标签样式
const getStatusTagType = (status: string) => {
  const item = transactionStatus.find(s => s.value === status)
  return item?.tagType as any || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  // 模拟筛选
  ElMessage.success('查询成功')
}

// 重置
const handleReset = () => {
  filterForm.value = {
    timeRange: [],
    type: '',
    minAmount: null,
    maxAmount: null,
    status: '',
    keyword: '',
    userType: ''
  }
  currentPage.value = 1
}

// 导出Excel
const handleExport = () => {
  ElMessageBox.confirm('确定导出当前筛选条件下的交易流水吗？', '导出确认', {
    confirmButtonText: '确定导出',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.success('交易流水导出成功，正在下载...')
  })
}

// 批量导出
const handleBatchExport = () => {
  ElMessageBox.prompt('请输入导出条数（1-10000）', '批量导出', {
    confirmButtonText: '导出',
    cancelButtonText: '取消',
    inputPattern: /^(?!0)\d{1,4}$/,
    inputErrorMessage: '请输入1-10000的数字'
  }).then(({ value }) => {
    ElMessage.success(`正在导出前${value}条交易记录...`)
  })
}

// 查看明细
const handleViewDetail = (row: any) => {
  currentDetail.value = { ...row }
  detailDialogVisible.value = true
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleSearch()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 格式化金额
const formatAmount = (amount: number) => {
  return '¥' + amount.toFixed(2)
}
</script>

<template>
  <div class="transaction-page">
    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="时间范围">
          <el-date-picker v-model="filterForm.timeRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="filterForm.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option v-for="item in transactionTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额区间">
          <el-input-number v-model="filterForm.minAmount" :precision="2" :min="0" placeholder="最小金额"
            style="width: 120px" />
          <span style="margin: 0 8px">-</span>
          <el-input-number v-model="filterForm.maxAmount" :precision="2" :min="0" placeholder="最大金额"
            style="width: 120px" />
        </el-form-item>
        <el-form-item label="交易状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option v-for="item in transactionStatus" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="filterForm.userType" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="item in userTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="交易号/用户/订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏和合计 -->
    <div class="toolbar">
      <div class="left">
        <el-button type="success" :icon="Download" @click="handleExport">导出Excel</el-button>
        <el-button :icon="Document" @click="handleBatchExport">批量导出</el-button>
      </div>
      <div class="right">
        <div class="amount-summary">
          <span class="label">合计金额：</span>
          <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <div class="count-summary">
          <span class="label">共 {{ total }} 条记录</span>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table :data="transactionList.slice((currentPage - 1) * pageSize, currentPage * pageSize)" v-loading="false"
        stripe border>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="交易编号" min-width="170" show-overflow-tooltip />
        <el-table-column label="交易类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" effect="plain" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额" width="120" align="right">
          <template #default="{ row }">
            <span
              :class="{ 'income': row.type === 'order' || row.type === 'recharge' || row.type === 'commission' || row.type === 'reward', 'expense': row.type === 'refund' || row.type === 'withdrawal' }">
              {{ row.type === 'order' || row.type === 'recharge' || row.type === 'commission' || row.type === 'reward'
              ? '+' : '-' }}{{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" effect="light" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单" min-width="160" show-overflow-tooltip />
        <el-table-column prop="remark" label="交易说明" min-width="120" show-overflow-tooltip />
        <el-table-column prop="userName" label="交易用户" width="100" />
        <el-table-column prop="payChannel" label="支付渠道" width="100" />
        <el-table-column prop="createTime" label="交易时间" width="160" sortable />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="View" @click="handleViewDetail(row)">明细</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 交易明细弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="交易明细" width="700px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="交易编号" :span="2">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="交易类型">
          <el-tag :type="getTypeTagType(currentDetail.type)" size="small">{{ getTypeLabel(currentDetail.type)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易金额" :span="1">
          <span style="font-size: 18px; font-weight: bold; color: #f56c6c;">{{ formatAmount(currentDetail.amount)
          }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="交易状态">
          <el-tag :type="getStatusTagType(currentDetail.status)" size="small">{{ getStatusLabel(currentDetail.status)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付渠道">{{ currentDetail.payChannel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联订单" :span="2">{{ currentDetail.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交易说明" :span="2">{{ currentDetail.remark }}</el-descriptions-item>
        <el-descriptions-item label="交易用户">{{ currentDetail.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">
          {{ currentDetail.userType === 'merchant' ? '商户' : currentDetail.userType === 'platform' ? '平台' : '普通用户'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentDetail.completeTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleExport">导出记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.transaction-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding: 16px;
  }

  .el-form-item {
    margin-bottom: 12px;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .left {
    display: flex;
    gap: 8px;
  }

  .right {
    display: flex;
    align-items: center;
    gap: 16px;

    .amount-summary {
      .label {
        font-size: 14px;
        color: #606266;
      }

      .value {
        font-size: 18px;
        font-weight: 600;
        color: #67c23a;
      }
    }

    .count-summary {
      .label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

:deep(.el-table) {
  .income {
    color: #67c23a;
    font-weight: 500;
  }

  .expense {
    color: #f56c6c;
    font-weight: 500;
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
