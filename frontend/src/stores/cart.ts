import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface CartItem {
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
  const addItem = (item: Omit<CartItem, 'id'>) => {
    const existingItem = items.value.find(i => i.drugId === item.drugId)
    if (existingItem) {
      existingItem.quantity += item.quantity
    } else {
      items.value.push({
        ...item,
        id: Date.now().toString()
      })
    }
    saveToLocal()
  }
  
  const updateQuantity = (id: string, quantity: number) => {
    const item = items.value.find(i => i.id === id)
    if (item) {
      if (quantity <= 0) {
        removeItem(id)
      } else {
        item.quantity = quantity
      }
    }
    saveToLocal()
  }
  
  const updateDisease = (id: string, disease: string) => {
    const item = items.value.find(i => i.id === id)
    if (item) {
      item.disease = disease
    }
    saveToLocal()
  }
  
  const updateUsage = (id: string, usage: string) => {
    const item = items.value.find(i => i.id === id)
    if (item) {
      item.usage = usage
    }
    saveToLocal()
  }
  
  const removeItem = (id: string) => {
    const index = items.value.findIndex(i => i.id === id)
    if (index > -1) {
      items.value.splice(index, 1)
    }
    saveToLocal()
  }
  
  const clearCart = () => {
    items.value = []
    saveToLocal()
  }
  
  const saveToLocal = () => {
    localStorage.setItem('cart', JSON.stringify(items.value))
  }
  
  const loadFromLocal = () => {
    const data = localStorage.getItem('cart')
    if (data) {
      items.value = JSON.parse(data)
    }
  }
  
  // 初始化加载
  loadFromLocal()
  
  return {
    items,
    totalCount,
    totalPrice,
    hasRxItems,
    addItem,
    updateQuantity,
    updateDisease,
    updateUsage,
    removeItem,
    clearCart,
    saveToLocal,
    loadFromLocal
  }
})
