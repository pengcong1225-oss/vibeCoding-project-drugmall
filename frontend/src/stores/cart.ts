import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getCartList as apiGetCartList,
  addToCart as apiAddToCart,
  updateCartItem as apiUpdateCartItem,
  removeCartItem as apiRemoveCartItem,
  clearCart as apiClearCart,
  updateCartItemQuantity as apiUpdateCartItemQuantity
} from '@/api/modules/cart'
import type { CartItem, AddToCartParams } from '@/types'

export interface CartItemLocal {
  id: string
  drugId: string
  name: string
  specification: string
  manufacturer: string
  price: number
  quantity: number
  image: string
  disease: string
  usage: string
  isRx: boolean
}

export const useCartStore = defineStore('cart', () => {
  // State
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  // Getters
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
  })

  const hasRxItems = computed(() => {
    return items.value.some(item => item.isRx)
  })

  // Actions
  // 获取购物车列表
  const fetchCartList = async () => {
    loading.value = true
    try {
      const res = await apiGetCartList()
      items.value = res || []
      return items.value
    } catch (error) {
      ElMessage.error('获取购物车失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 添加商品到购物车
  const addItem = async (item: Omit<CartItemLocal, 'id'>) => {
    loading.value = true
    try {
      const params: AddToCartParams = {
        drugId: item.drugId,
        quantity: item.quantity,
        disease: item.disease,
        usage: item.usage
      }
      const res = await apiAddToCart(params)
      // 重新获取购物车列表以同步数据
      await fetchCartList()
      ElMessage.success('已添加到购物车')
      return res
    } catch (error) {
      ElMessage.error('添加失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新商品数量
  const updateQuantity = async (id: string, quantity: number) => {
    if (quantity <= 0) {
      await removeItem(id)
      return
    }
    try {
      await apiUpdateCartItemQuantity(id, quantity)
      // 更新本地状态
      const item = items.value.find(i => i.id === id)
      if (item) {
        item.quantity = quantity
      }
    } catch (error) {
      ElMessage.error('更新数量失败')
      throw error
    }
  }

  // 更新病症信息
  const updateDisease = async (id: string, disease: string) => {
    try {
      await apiUpdateCartItem(id, { disease })
      const item = items.value.find(i => i.id === id)
      if (item) {
        item.disease = disease
      }
    } catch (error) {
      ElMessage.error('更新失败')
      throw error
    }
  }

  // 更新用法用量
  const updateUsage = async (id: string, usage: string) => {
    try {
      await apiUpdateCartItem(id, { usage })
      const item = items.value.find(i => i.id === id)
      if (item) {
        item.usage = usage
      }
    } catch (error) {
      ElMessage.error('更新失败')
      throw error
    }
  }

  // 删除商品
  const removeItem = async (id: string) => {
    try {
      await apiRemoveCartItem(id)
      const index = items.value.findIndex(i => i.id === id)
      if (index > -1) {
        items.value.splice(index, 1)
      }
      ElMessage.success('已删除')
    } catch (error) {
      ElMessage.error('删除失败')
      throw error
    }
  }

  // 清空购物车
  const clearCart = async () => {
    try {
      await apiClearCart()
      items.value = []
      ElMessage.success('购物车已清空')
    } catch (error) {
      ElMessage.error('清空失败')
      throw error
    }
  }

  // 保留旧方法名以兼容现有代码
  const saveToLocal = () => {
    // API模式下不需要本地存储，保留空方法以兼容
  }

  const loadFromLocal = () => {
    // API模式下从服务器加载
    fetchCartList()
  }

  return {
    items,
    loading,
    totalCount,
    totalPrice,
    hasRxItems,
    addItem,
    updateQuantity,
    updateDisease,
    updateUsage,
    removeItem,
    clearCart,
    fetchCartList,
    saveToLocal,
    loadFromLocal
  }
})
