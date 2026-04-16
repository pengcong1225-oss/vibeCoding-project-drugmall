<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, User, Phone, Location, Wallet, ShoppingCart, Document } from '@element-plus/icons-vue'
import type { UserInfo } from '@/types/user'
import type { Order } from '@/types/order'
import { getUserDetail as fetchUserDetail, getUserOrders as fetchUserOrders, updateUserStatus } from '@/api/user'

const route = useRoute()
const router = useRouter()
const userId = route.params.id as string

// 加载状态
const loading = ref(false)
const orderLoading = ref(false)

// 用户信息
const userInfo = ref<UserInfo | null>(null)

// 用户订单列表
const orderList = ref<Order[]>([])
const orderTotal = ref(0)
const orderPageNum = ref(1)
const orderPageSize = ref(5)

// 标签页
const activeTab = ref('basic')

// 获取用户详情
const getUserDetail = async () => {
  loading.value = true
  try {
    const data = await fetchUserDetail(userId)
    userInfo.value = data
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 获取用户订单
const getUserOrders = async () => {
  orderLoading.value = true
  try {
    const res = await fetchUserOrders(userId, {
      pageNum: orderPageNum.value,
      pageSize: orderPageSize.value
    })
    orderList.value = res.list
    orderTotal.value = res.total
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    orderLoading.value = false
  }
}

// 处理订单分页
const handleOrderPageChange = (page: number) => {
  orderPageNum.value = page
  getUserOrders()
}

// 查看订单详情
const viewOrderDetail = (order: Order) => {
  router.push(`/order/detail/${order.id}`)
}

// 返回
const goBack = () => {
  router.back()
}

// 禁用/启用用户
const toggleUserStatus = async () => {
  if (!userInfo.value) return
  
  const action = userInfo.value.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}该用户吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const newStatus = userInfo.value.status === 1 ? 0 : 1
    await updateUserStatus(userId, newStatus)
    userInfo.value.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  getUserDetail()
  getUserOrders()
})
</script>

<template>
  <div class="user-detail-container">
    <!-- 头部 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2 class="page-title">用户详情</h2>
      <div class="header-actions">
        <el-button 
          :type="userInfo?.status === 1 ? 'danger' : 'success'"
          @click="toggleUserStatus"
        >
          {{ userInfo?.status === 1 ? '禁用用户' : '启用用户' }}
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="loading" :rows="10" animated />

    <!-- 用户详情内容 -->
    <template v-else-if="userInfo">
      <!-- 基本信息卡片 -->
      <el-card class="user-info-card">
        <div class="user-header">
          <el-avatar :size="80" :src="userInfo.avatar || ''" :icon="User" />
          <div class="user-meta">
            <h3 class="nickname">{{ userInfo.nickname }}</h3>
            <p class="username">用户名: {{ userInfo.username }}</p>
            <div class="user-tags">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">
                {{ userInfo.status === 1 ? '正常' : '已禁用' }}
              </el-tag>
              <el-tag type="info" size="small" v-for="role in userInfo.roles" :key="role">
                {{ role }}
              </el-tag>
            </div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">12</div>
              <div class="stat-label">订单数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥2,580</div>
              <div class="stat-label">消费总额</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">3</div>
              <div class="stat-label">优惠券</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 详细信息标签页 -->
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userInfo.nickname }}</el-descriptions-item>
            <el-descriptions-item label="手机号">
              <el-icon><Phone /></el-icon> {{ userInfo.phone || '未绑定' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              <el-icon><Message /></el-icon> {{ userInfo.email || '未绑定' }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ userInfo.createTime }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ userInfo.lastLoginTime }}</el-descriptions-item>
            <el-descriptions-item label="账号状态">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '正常' : '已禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 收货地址 -->
        <el-tab-pane label="收货地址" name="address">
          <el-empty description="暂无收货地址" />
        </el-tab-pane>

        <!-- 订单记录 -->
        <el-tab-pane label="订单记录" name="orders">
          <el-table :data="orderList" v-loading="orderLoading" stripe>
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column label="商品信息" min-width="200">
              <template #default="{ row }">
                <div class="order-items-preview">
                  <el-image 
                    v-for="item in row.items.slice(0, 3)" 
                    :key="item.id"
                    :src="item.productImage" 
                    :preview-src-list="[item.productImage]"
                    class="item-image"
                  />
                  <span v-if="row.items.length > 3" class="more-items">+{{ row.items.length - 3 }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="金额" width="120" align="right">
              <template #default="{ row }">
                <div class="amount-info">
                  <div class="pay-amount">¥{{ row.payAmount.toFixed(2) }}</div>
                  <div v-if="row.discountAmount > 0" class="discount-amount">
                    优惠: ¥{{ row.discountAmount.toFixed(2) }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)" size="small">
                  {{ getOrderStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="160" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewOrderDetail(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="orderPageNum"
              v-model:page-size="orderPageSize"
              :total="orderTotal"
              :page-sizes="[5, 10, 20]"
              layout="total, sizes, prev, pager, next"
              @size-change="getUserOrders"
              @current-change="handleOrderPageChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- 无数据状态 -->
    <el-empty v-else description="用户不存在或已被删除" />
  </div>
</template>

<script lang="ts">
// 辅助函数：获取订单状态类型
function getOrderStatusType(status: number): string {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info',
    4: 'success',
    [-1]: 'info',
    [-2]: 'warning',
    [-3]: 'info'
  }
  return typeMap[status] || 'info'
}

// 辅助函数：获取订单状态文本
function getOrderStatusText(status: number): string {
  const textMap: Record<number, string> = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '待评价',
    4: '已完成',
    [-1]: '已取消',
    [-2]: '退款中',
    [-3]: '已退款'
  }
  return textMap[status] || '未知'
}
</script>

<style scoped lang="scss">
.user-detail-container {
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

.user-info-card {
  margin-bottom: 20px;

  .user-header {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-meta {
      flex: 1;

      .nickname {
        margin: 0 0 8px;
        font-size: 20px;
        font-weight: 600;
      }

      .username {
        margin: 0 0 12px;
        color: #909399;
        font-size: 14px;
      }

      .user-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }

    .user-stats {
      display: flex;
      gap: 40px;
      padding-left: 40px;
      border-left: 1px solid #e4e7ed;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}

.detail-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 20px;
  }
}

.order-items-preview {
  display: flex;
  align-items: center;
  gap: 4px;

  .item-image {
    width: 40px;
    height: 40px;
    border-radius: 4px;
    object-fit: cover;
  }

  .more-items {
    font-size: 12px;
    color: #909399;
  }
}

.amount-info {
  .pay-amount {
    font-weight: 600;
    color: #f56c6c;
  }

  .discount-amount {
    font-size: 12px;
    color: #67c23a;
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
