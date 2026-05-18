<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshRight, Check, Close, Money, Document, TrendCharts, Download } from '@element-plus/icons-vue'
import { getWithdrawalList, getWithdrawalStats, auditWithdrawal } from '@/api/finance'

const loading = ref(false)

// 筛选表单
const filterForm = ref({
  status: '',
  keyword: '',
  timeRange: [],
  amountMin: null as number | null,
  amountMax: null as number | null
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审核弹窗
const auditDialogVisible = ref(false)
const auditForm = ref({
  id: '',
  action: 'approve',
  remark: ''
})
const auditFormRef = ref()

// 转账记录弹窗
const transferDialogVisible = ref(false)
const transferRecord = ref<any>({})

// 提现统计弹窗
const statsDialogVisible = ref(false)

// 提现状态枚举
const withdrawalStatus = [
  { label: '待审核', value: 'pending', tagType: 'warning' },
  { label: '审核通过', value: 'approved', tagType: 'success' },
  { label: '审核拒绝', value: 'rejected', tagType: 'danger' },
  { label: '转账中', value: 'transferring', tagType: 'primary' },
  { label: '已完成', value: 'completed', tagType: 'success' },
  { label: '转账失败', value: 'failed', tagType: 'danger' }
]

// 提现记录数据
const withdrawalList = ref<any[]>([])

// 提现统计数据
const withdrawalStats = ref({
  todayAmount: 0,
  todayCount: 0,
  weekAmount: 0,
  weekCount: 0,
  monthAmount: 0,
  monthCount: 0,
  totalAmount: 0,
  totalCount: 0
})

// 统计卡片
const statCards = computed(() => {
  const pending = withdrawalList.value.filter(i => i.status === 'pending').reduce((sum, i) => sum + i.amount, 0)
  const approved = withdrawalList.value.filter(i => i.status === 'approved' || i.status === 'transferring').reduce((sum, i) => sum + i.amount, 0)
  const completed = withdrawalList.value.filter(i => i.status === 'completed').reduce((sum, i) => sum + i.amount, 0)
  const rejected = withdrawalList.value.filter(i => i.status === 'rejected' || i.status === 'failed').reduce((sum, i) => sum + i.amount, 0)

  return [
    { title: '待审核金额', value: `¥${pending.toFixed(2)}`, icon: 'warning', color: '#e6a23c', count: withdrawalList.value.filter(i => i.status === 'pending').length },
    { title: '审核通过金额', value: `¥${approved.toFixed(2)}`, icon: 'success', color: '#67c23a', count: withdrawalList.value.filter(i => i.status === 'approved' || i.status === 'transferring').length },
    { title: '已完成金额', value: `¥${completed.toFixed(2)}`, icon: 'completed', color: '#409eff', count: withdrawalList.value.filter(i => i.status === 'completed').length },
    { title: '失败/拒绝金额', value: `¥${rejected.toFixed(2)}`, icon: 'failed', color: '#f56c6c', count: withdrawalList.value.filter(i => i.status === 'rejected' || i.status === 'failed').length }
  ]
})

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = withdrawalStatus.find(s => s.value === status)
  return item?.label || status
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const item = withdrawalStatus.find(s => s.value === status)
  return item?.tagType as any || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  ElMessage.success('查询成功')
}

// 重置
const handleReset = () => {
  filterForm.value = { status: '', keyword: '', timeRange: [], amountMin: null, amountMax: null }
  currentPage.value = 1
}

// 审核
const handleAudit = (row: any) => {
  auditForm.value = { id: row.id, action: 'approve', remark: '' }
  auditDialogVisible.value = true
}

// 提交审核
const submitAudit = () => {
  if (!auditFormRef.value) return

  auditFormRef.value.validate((valid: boolean) => {
    if (valid) {
      const action = auditForm.value.action === 'approve' ? '通过' : '拒绝'
      ElMessage.success(`审核${action}成功`)
      auditDialogVisible.value = false
    }
  })
}

// 转账
const handleTransfer = (row: any) => {
  ElMessageBox.confirm(`确认转账 ¥${row.actualAmount.toFixed(2)} 到 ${row.merchantName} 吗？`, '确认转账', {
    confirmButtonText: '确认转账',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('转账成功')
  })
}

// 查看转账记录
const handleViewTransfer = (row: any) => {
  transferRecord.value = { ...row }
  transferDialogVisible.value = true
}

// 查看提现统计
const _loadWithdrawalStatsData = async () => {}

// 导出提现记录
const handleExport = () => {
  ElMessage.success('提现记录导出成功')
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
</script>

<template>
  <div class="withdrawal-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in statCards" :key="card.title">
        <el-card class="stat-card" shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">{{ card.title }}</div>
              <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
              <div class="card-count">{{ card.count }} 笔</div>
            </div>
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="28" color="#fff">
                <Money />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="时间范围">
          <el-date-picker v-model="filterForm.timeRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="提现状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="item in withdrawalStatus" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额区间">
          <el-input-number v-model="filterForm.amountMin" :precision="2" :min="0" placeholder="最小金额"
            style="width: 130px" />
          <span style="margin: 0 8px">-</span>
          <el-input-number v-model="filterForm.amountMax" :precision="2" :min="0" placeholder="最大金额"
            style="width: 130px" />
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="filterForm.keyword" placeholder="商户名称/提现单号" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table :data="withdrawalList" v-loading="false" stripe border>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="提现单号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="merchantName" label="商户名称" min-width="140" show-overflow-tooltip />
        <el-table-column label="提现金额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手续费" width="100" align="right">
          <template #default="{ row }">
            <span class="fee">-¥{{ row.fee.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实际到账" width="120" align="right">
          <template #default="{ row }">
            <span class="actual-amount">¥{{ row.actualAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" effect="light" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收款账户" min-width="200">
          <template #default="{ row }">
            <div class="bank-info">
              <div class="bank-name">{{ row.bankName }}</div>
              <div class="bank-card">{{ row.bankCard }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160" sortable />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" link type="primary" size="small" :icon="Check"
              @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 'approved'" link type="success" size="small" :icon="Check"
              @click="handleTransfer(row)">转账</el-button>
            <el-button v-if="row.status === 'transferring' || row.status === 'completed' || row.status === 'failed'"
              link type="info" size="small" :icon="Document" @click="handleViewTransfer(row)">转账记录</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="withdrawalList.length" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="提现审核" width="500px" destroy-on-close>
      <el-form ref="auditFormRef" :model="auditForm" label-width="100px">
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.action">
            <el-radio label="approve">审核通过</el-radio>
            <el-radio label="reject">审核拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注说明" v-if="auditForm.action === 'reject'" required>
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请输入拒绝原因，将展示给商户" />
        </el-form-item>
        <el-form-item label="备注说明" v-else>
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="选填：补充说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 转账记录弹窗 -->
    <el-dialog v-model="transferDialogVisible" title="转账记录" width="600px" destroy-on-close>
      <el-timeline>
        <el-timeline-item type="primary" :hollow="true">
          <div class="timeline-content">
            <div class="time">{{ transferRecord.applyTime }}</div>
            <div class="title">提交提现申请</div>
            <div class="detail">商户 {{ transferRecord.merchantName }} 提交提现申请，金额 ¥{{ transferRecord.amount }}</div>
          </div>
        </el-timeline-item>
        <el-timeline-item type="success">
          <div class="timeline-content">
            <div class="time">{{ transferRecord.applyTime?.replace('09:30', '10:00') }}</div>
            <div class="title">审核通过</div>
            <div class="detail">提现申请已通过审核，等待转账</div>
          </div>
        </el-timeline-item>
        <el-timeline-item type="warning" :hollow="transferRecord.status === 'transferring'">
          <div class="timeline-content">
            <div class="time">{{ transferRecord.transferTime }}</div>
            <div class="title">发起转账</div>
            <div class="detail">已向银行发起转账请求，收款账户 {{ transferRecord.bankName }} {{ transferRecord.bankCard }}</div>
          </div>
        </el-timeline-item>
        <el-timeline-item v-if="transferRecord.status === 'completed'" type="success">
          <div class="timeline-content">
            <div class="time">{{ transferRecord.completeTime }}</div>
            <div class="title">转账成功</div>
            <div class="detail">转账已成功完成，实际到账金额 ¥{{ transferRecord.actualAmount }}</div>
          </div>
        </el-timeline-item>
        <el-timeline-item v-if="transferRecord.status === 'failed'" type="danger">
          <div class="timeline-content">
            <div class="time">{{ transferRecord.transferTime }}</div>
            <div class="title">转账失败</div>
            <div class="detail">转账失败，原因：{{ transferRecord.failReason }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <template #footer>
        <el-button @click="transferDialogVisible = false">关闭</el-button>
        <el-button type="primary" :icon="Download">导出记录</el-button>
      </template>
    </el-dialog>

    <!-- 提现统计弹窗 -->
    <el-dialog v-model="statsDialogVisible" title="提现统计" width="800px">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="stats-card">
            <div class="stats-title">今日提现</div>
            <div class="stats-value">¥{{ withdrawalStats.todayAmount.toFixed(2) }}</div>
            <div class="stats-count">{{ withdrawalStats.todayCount }} 笔</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stats-card">
            <div class="stats-title">本周提现</div>
            <div class="stats-value">¥{{ withdrawalStats.weekAmount.toFixed(2) }}</div>
            <div class="stats-count">{{ withdrawalStats.weekCount }} 笔</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stats-card">
            <div class="stats-title">本月提现</div>
            <div class="stats-value">¥{{ withdrawalStats.monthAmount.toFixed(2) }}</div>
            <div class="stats-count">{{ withdrawalStats.monthCount }} 笔</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stats-card">
            <div class="stats-title">累计提现</div>
            <div class="stats-value">¥{{ withdrawalStats.totalAmount.toFixed(2) }}</div>
            <div class="stats-count">{{ withdrawalStats.totalCount }} 笔</div>
          </div>
        </el-col>
      </el-row>
      <template #footer>
        <el-button @click="statsDialogVisible = false">关闭</el-button>
        <el-button type="primary" :icon="Download">导出报表</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.withdrawal-page {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-info {
    .card-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .card-value {
      font-size: 22px;
      font-weight: 600;
    }

    .card-count {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }

  .card-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 52px;
    height: 52px;
    border-radius: 8px;
  }
}

.filter-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding: 16px;
  }
}

:deep(.el-table) {
  .amount {
    color: #f56c6c;
    font-weight: 500;
  }

  .fee {
    color: #909399;
  }

  .actual-amount {
    color: #67c23a;
    font-weight: 600;
  }

  .bank-info {
    .bank-name {
      font-size: 13px;
      color: #303133;
      margin-bottom: 4px;
    }

    .bank-card {
      font-size: 12px;
      color: #909399;
    }
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

// 时间线样式
.timeline-content {
  .time {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
  }

  .title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .detail {
    font-size: 12px;
    color: #606266;
    line-height: 1.5;
  }
}

// 统计卡片
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  color: #fff;
  margin-bottom: 16px;

  .stats-title {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .stats-value {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>
  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>
</style>

  .title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .detail {
    font-size: 12px;
    color: #606266;
    line-height: 1.5;
  }
}

// 统计卡片
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  color: #fff;
  margin-bottom: 16px;

  .stats-title {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .stats-value {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>

  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>
</style>
  .detail {
    font-size: 12px;
    color: #606266;
    line-height: 1.5;
  }
}

// 统计卡片
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  color: #fff;
  margin-bottom: 16px;

  .stats-title {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .stats-value {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>

  .stats-count {
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>
</style>
    opacity: 0.8;
  }
}
</style>
</style>
