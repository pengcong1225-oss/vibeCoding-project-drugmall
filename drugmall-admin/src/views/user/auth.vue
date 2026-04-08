<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close, View } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined,
  startTime: '',
  endTime: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 详情弹窗
const detailVisible = ref(false)
const currentRecord = ref<any>(null)

// 审核弹窗
const auditVisible = ref(false)
const auditForm = reactive({
  result: 'pass',
  reason: ''
})

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    const mockData = [
      {
        id: '1',
        userId: '10001',
        username: 'zhangsan',
        realName: '张三',
        idCard: '110101199001011234',
        phone: '13800138000',
        status: 0,
        submitTime: '2024-03-20 10:30:00',
        idCardFront: 'https://via.placeholder.com/300x200',
        idCardBack: 'https://via.placeholder.com/300x200'
      },
      {
        id: '2',
        userId: '10002',
        username: 'lisi',
        realName: '李四',
        idCard: '110101199002022345',
        phone: '13800138001',
        status: 1,
        submitTime: '2024-03-19 15:20:00',
        auditTime: '2024-03-19 16:00:00',
        idCardFront: 'https://via.placeholder.com/300x200',
        idCardBack: 'https://via.placeholder.com/300x200'
      },
      {
        id: '3',
        userId: '10003',
        username: 'wangwu',
        realName: '王五',
        idCard: '110101199003033456',
        phone: '13800138002',
        status: 2,
        submitTime: '2024-03-18 09:10:00',
        auditTime: '2024-03-18 10:30:00',
        rejectReason: '身份证照片不清晰，请重新上传',
        idCardFront: 'https://via.placeholder.com/300x200',
        idCardBack: 'https://via.placeholder.com/300x200'
      }
    ]
    
    // 筛选
    let filteredData = [...mockData]
    if (searchForm.status !== undefined) {
      filteredData = filteredData.filter(item => item.status === searchForm.status)
    }
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      filteredData = filteredData.filter(item => 
        item.username.toLowerCase().includes(keyword) ||
        item.realName.includes(keyword) ||
        item.phone.includes(keyword)
      )
    }
    
    total.value = filteredData.length
    tableData.value = filteredData.slice((pageNum.value - 1) * pageSize.value, pageNum.value * pageSize.value)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  getList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: undefined,
    startTime: '',
    endTime: ''
  })
  pageNum.value = 1
  getList()
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getList()
}

const handleCurrentChange = (page: number) => {
  pageNum.value = page
  getList()
}

// 查看详情
const handleView = (row: any) => {
  currentRecord.value = row
  detailVisible.value = true
}

// 审核
const handleAudit = (row: any) => {
  currentRecord.value = row
  auditForm.result = 'pass'
  auditForm.reason = ''
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  if (auditForm.result === 'reject' && !auditForm.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success(auditForm.result === 'pass' ? '审核通过' : '已拒绝')
    auditVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 获取状态标签类型
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

// 身份证脱敏
const maskIdCard = (idCard: string) => {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.slice(0, 4) + '********' + idCard.slice(-4)
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="user-auth-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/真实姓名/手机号"
            clearable
            @keyup.enter="handleSearch"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <span style="margin: 0 8px">至</span>
          <el-date-picker
            v-model="searchForm.endTime"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6f7ff; color: #1890ff;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">128</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f6ffed; color: #52c41a;">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">3,256</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff2f0; color: #f5222d;">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">86</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f9f0ff; color: #722ed1;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">3,470</div>
              <div class="stat-label">认证总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">实名认证列表</span>
          <el-radio-group v-model="searchForm.status" size="small" @change="handleSearch">
            <el-radio-button :label="undefined">全部</el-radio-button>
            <el-radio-button :label="0">待审核</el-radio-button>
            <el-radio-button :label="1">已通过</el-radio-button>
            <el-radio-button :label="2">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="180">
          <template #default="{ row }">
            <span>{{ maskIdCard(row.idCard) }}</span>
            <el-button 
              link 
              type="primary" 
              size="small" 
              @click="$event.target.previousElementSibling.textContent = row.idCard"
            >
              显示
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.status === 0" 
              link 
              type="success" 
              :icon="Check" 
              @click="handleAudit(row)"
            >
              审核
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="认证详情" width="700px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentRecord.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentRecord.realName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentRecord.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentRecord.phone }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentRecord.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ getStatusText(currentRecord.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="currentRecord.auditTime">
          {{ currentRecord.auditTime }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="idcard-images" v-if="currentRecord">
        <h4>身份证照片</h4>
        <div class="image-list">
          <div class="image-item">
            <el-image 
              :src="currentRecord.idCardFront" 
              :preview-src-list="[currentRecord.idCardFront, currentRecord.idCardBack]"
              fit="cover"
            />
            <span class="image-label">人像面</span>
          </div>
          <div class="image-item">
            <el-image 
              :src="currentRecord.idCardBack" 
              :preview-src-list="[currentRecord.idCardFront, currentRecord.idCardBack]"
              fit="cover"
            />
            <span class="image-label">国徽面</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button 
          v-if="currentRecord?.status === 0"
          type="primary" 
          @click="detailVisible = false; handleAudit(currentRecord)"
        >
          立即审核
        </el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" title="实名认证审核" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="用户">
          <span>{{ currentRecord?.username }} ({{ currentRecord?.realName }})</span>
        </el-form-item>
        <el-form-item label="身份证号">
          <span>{{ currentRecord?.idCard }}</span>
        </el-form-item>
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.result">
            <el-radio label="pass">通过</el-radio>
            <el-radio label="reject">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          label="拒绝原因" 
          v-if="auditForm.result === 'reject'" 
          required
        >
          <el-input
            v-model="auditForm.reason"
            type="textarea"
            rows="3"
            placeholder="请输入拒绝原因，将告知用户"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.user-auth-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 8px;
      font-size: 24px;
    }

    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        line-height: 1;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.idcard-images {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;

  h4 {
    margin: 0 0 16px;
    font-size: 16px;
    color: #303133;
  }

  .image-list {
    display: flex;
    gap: 20px;

    .image-item {
      text-align: center;

      .el-image {
        width: 280px;
        height: 180px;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid #e4e7ed;
      }

      .image-label {
        display: block;
        margin-top: 8px;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>
