import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Order, OrderStatus, OrderStats, OrderQueryParams } from '@/types'

export const useOrderStore = defineStore('order', () => {
  // State
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

  // Getters
  const pendingOrders = computed(() => orders.value.filter(o => o.status === 'pending'))
  const paidOrders = computed(() => orders.value.filter(o => o.status === 'paid'))
  const shippedOrders = computed(() => orders.value.filter(o => o.status === 'shipped'))
  const completedOrders = computed(() => orders.value.filter(o => o.status === 'completed'))

  // Actions
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
    // Getters
    pendingOrders,
    paidOrders,
    shippedOrders,
    completedOrders,
    // Actions
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
