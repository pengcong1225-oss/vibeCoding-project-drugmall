import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getOrders as apiGetOrders,
  getOrderDetail as apiGetOrderDetail,
  createOrder as apiCreateOrder,
  cancelOrder as apiCancelOrder,
  payOrder as apiPayOrder,
  getOrderStatistics as apiGetOrderStatistics,
  confirmReceipt as apiConfirmReceipt,
  deleteOrder as apiDeleteOrder
} from '@/api/modules/order'
import { OrderStatus } from '@/constants'
import type { Order, OrderStatus as OrderStatusType, OrderStats, OrderQueryParams, CreateOrderParams, PayParams, PayType } from '@/types'

export const useOrderStore = defineStore('order', () => {
  const orders = ref<Order[]>([])
  const currentOrder = ref<Order | null>(null)
  const orderStats = ref<OrderStats>({
    totalCount: 0,
    pendingPayment: 0,
    pendingShipment: 0,
    pendingReceipt: 0,
    pendingReview: 0,
    afterSale: 0,
    totalAmount: 0
  })
  const loading = ref(false)
  const hasMore = ref(true)
  const total = ref(0)

  const pendingOrders = computed(() => orders.value.filter(o => o.status === OrderStatus.PENDING))
  const paidOrders = computed(() => orders.value.filter(o => o.status === OrderStatus.PAID))
  const shippedOrders = computed(() => orders.value.filter(o => o.status === OrderStatus.SHIPPED))
  const completedOrders = computed(() => orders.value.filter(o => o.status === OrderStatus.COMPLETED))

  // Actions
  // 获取订单列表
  const fetchOrders = async (params?: OrderQueryParams, append = false) => {
    loading.value = true
    try {
      const res = await apiGetOrders(params)
      if (append) {
        orders.value.push(...res.list)
      } else {
        orders.value = res.list
      }
      total.value = res.total
      orderStats.value = res.stats
      hasMore.value = orders.value.length < res.total
      return res
    } catch (error) {
      ElMessage.error('获取订单列表失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取订单详情
  const fetchOrderDetail = async (orderId: string) => {
    loading.value = true
    try {
      const res = await apiGetOrderDetail(orderId)
      currentOrder.value = res
      return res
    } catch (error) {
      ElMessage.error('获取订单详情失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建订单
  const createOrder = async (params: CreateOrderParams) => {
    loading.value = true
    try {
      const res = await apiCreateOrder(params)
      currentOrder.value = res
      ElMessage.success('订单创建成功')
      return res
    } catch (error) {
      ElMessage.error('创建订单失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 取消订单
  const cancelOrder = async (orderId: string, reason?: string) => {
    try {
      await apiCancelOrder(orderId, reason)
      // 更新本地状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = OrderStatus.CANCELLED
      }
      if (currentOrder.value?.id === orderId) {
        currentOrder.value.status = OrderStatus.CANCELLED
      }
      ElMessage.success('订单已取消')
    } catch (error) {
      ElMessage.error('取消订单失败')
      throw error
    }
  }

  // 支付订单
  const payOrder = async (orderId: string, payType: PayType) => {
    loading.value = true
    try {
      const params: PayParams = {
        orderId,
        payType
      }
      const res = await apiPayOrder(params)
      // 更新本地状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = OrderStatus.PAID
        order.payType = payType
        order.payTime = res.payTime
      }
      if (currentOrder.value?.id === orderId) {
        currentOrder.value.status = OrderStatus.PAID
        currentOrder.value.payType = payType
        currentOrder.value.payTime = res.payTime
      }
      ElMessage.success('支付成功')
      return res
    } catch (error) {
      ElMessage.error('支付失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 确认收货
  const confirmReceipt = async (orderId: string) => {
    try {
      await apiConfirmReceipt(orderId)
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'completed'
      }
      if (currentOrder.value?.id === orderId) {
        currentOrder.value.status = 'completed'
      }
      ElMessage.success('确认收货成功')
    } catch (error) {
      ElMessage.error('确认收货失败')
      throw error
    }
  }

  // 删除订单
  const deleteOrder = async (orderId: string) => {
    try {
      await apiDeleteOrder(orderId)
      const index = orders.value.findIndex(o => o.id === orderId)
      if (index > -1) {
        orders.value.splice(index, 1)
      }
      if (currentOrder.value?.id === orderId) {
        currentOrder.value = null
      }
      ElMessage.success('订单已删除')
    } catch (error) {
      ElMessage.error('删除订单失败')
      throw error
    }
  }

  // 获取订单统计
  const fetchOrderStats = async () => {
    try {
      const res = await apiGetOrderStatistics()
      orderStats.value = res
      return res
    } catch (error) {
      console.error('获取订单统计失败', error)
      throw error
    }
  }

  // 保留旧方法名以兼容现有代码
  const setOrders = (list: Order[]) => {
    orders.value = list
  }

  const addOrders = (list: Order[]) => {
    orders.value.push(...list)
  }

  const setCurrentOrder = (order: Order | null) => {
    currentOrder.value = order
  }

  const updateOrderStatus = (orderId: string, status: OrderStatus) => {
    const order = orders.value.find(o => o.id === orderId)
    if (order) {
      order.status = status
    }
    if (currentOrder.value?.id === orderId) {
      currentOrder.value.status = status
    }
  }

  const removeOrder = (orderId: string) => {
    const index = orders.value.findIndex(o => o.id === orderId)
    if (index > -1) {
      orders.value.splice(index, 1)
    }
  }

  const setOrderStats = (stats: OrderStats) => {
    orderStats.value = stats
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  const setHasMore = (value: boolean) => {
    hasMore.value = value
  }

  const clearOrders = () => {
    orders.value = []
    hasMore.value = true
  }

  return {
    // State
    orders,
    currentOrder,
    orderStats,
    loading,
    hasMore,
    total,
    // Getters
    pendingOrders,
    paidOrders,
    shippedOrders,
    completedOrders,
    // API Actions
    fetchOrders,
    fetchOrderDetail,
    createOrder,
    cancelOrder,
    payOrder,
    confirmReceipt,
    deleteOrder,
    fetchOrderStats,
    // Compatible Actions
    setOrders,
    addOrders,
    setCurrentOrder,
    updateOrderStatus,
    removeOrder,
    setOrderStats,
    setLoading,
    setHasMore,
    clearOrders
  }
})
