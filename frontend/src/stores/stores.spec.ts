import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from './user'
import { useCartStore } from './cart'
import { useOrderStore } from './order'

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('initializes with empty state', () => {
    const store = useUserStore()
    
    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
    expect(store.patients).toEqual([])
    expect(store.isLoggedIn).toBe(false)
  })

  it('sets token correctly', () => {
    const store = useUserStore()
    const mockToken = 'test-token-123'
    
    store.setToken(mockToken)
    
    expect(store.token).toBe(mockToken)
    expect(store.isLoggedIn).toBe(true)
  })

  it('sets user info correctly', () => {
    const store = useUserStore()
    const mockUserInfo = {
      id: '1',
      phone: '13800138000',
      nickname: 'TestUser',
      avatar: 'https://example.com/avatar.jpg',
      isAuthenticated: true
    }
    
    store.setUserInfo(mockUserInfo)
    
    expect(store.userInfo).toEqual(mockUserInfo)
  })

  it('logs in successfully', async () => {
    const store = useUserStore()
    
    const result = await store.login('13800138000', '123456')
    
    expect(result.success).toBe(true)
    expect(store.token).not.toBe('')
    expect(store.userInfo).not.toBeNull()
    expect(store.isLoggedIn).toBe(true)
  })

  it('logs out successfully', () => {
    const store = useUserStore()
    store.setToken('test-token')
    store.setUserInfo({
      id: '1',
      phone: '13800138000',
      nickname: 'TestUser',
      avatar: '',
      isAuthenticated: true
    })
    store.patients = [{ id: '1', name: 'Test', gender: 'male', age: 30, idCard: '', phone: '', relationship: '本人', isDefault: true }]
    
    store.logout()
    
    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
    expect(store.patients).toEqual([])
    expect(store.isLoggedIn).toBe(false)
  })

  it('adds patient correctly', () => {
    const store = useUserStore()
    const newPatient = {
      name: '张三',
      gender: 'male' as const,
      age: 30,
      idCard: '110101199001011234',
      phone: '13800138000',
      relationship: '本人',
      isDefault: false
    }
    
    store.addPatient(newPatient)
    
    expect(store.patients.length).toBe(1)
    expect(store.patients[0].name).toBe('张三')
    expect(store.patients[0].id).toBeDefined()
  })

  it('updates patient correctly', () => {
    const store = useUserStore()
    store.patients = [
      { id: '1', name: '张三', gender: 'male', age: 30, idCard: '', phone: '', relationship: '本人', isDefault: true }
    ]
    
    store.updatePatient('1', { name: '张三（已更新）', age: 31 })
    
    expect(store.patients[0].name).toBe('张三（已更新）')
    expect(store.patients[0].age).toBe(31)
  })

  it('deletes patient correctly', () => {
    const store = useUserStore()
    store.patients = [
      { id: '1', name: '张三', gender: 'male', age: 30, idCard: '', phone: '', relationship: '本人', isDefault: true },
      { id: '2', name: '李四', gender: 'female', age: 25, idCard: '', phone: '', relationship: '配偶', isDefault: false }
    ]
    
    store.deletePatient('1')
    
    expect(store.patients.length).toBe(1)
    expect(store.patients[0].name).toBe('李四')
  })

  it('sets default patient correctly', () => {
    const store = useUserStore()
    store.patients = [
      { id: '1', name: '张三', gender: 'male', age: 30, idCard: '', phone: '', relationship: '本人', isDefault: true },
      { id: '2', name: '李四', gender: 'female', age: 25, idCard: '', phone: '', relationship: '配偶', isDefault: false }
    ]
    
    store.setDefaultPatient('2')
    
    expect(store.patients[0].isDefault).toBe(false)
    expect(store.patients[1].isDefault).toBe(true)
  })

  it('returns default patient getter correctly', () => {
    const store = useUserStore()
    store.patients = [
      { id: '1', name: '张三', gender: 'male', age: 30, idCard: '', phone: '', relationship: '本人', isDefault: false },
      { id: '2', name: '李四', gender: 'female', age: 25, idCard: '', phone: '', relationship: '配偶', isDefault: true }
    ]
    
    const defaultPatient = store.defaultPatient
    
    expect(defaultPatient).not.toBeNull()
    expect(defaultPatient?.name).toBe('李四')
  })
})

describe('Cart Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('initializes with empty cart', () => {
    const store = useCartStore()
    
    expect(store.items).toEqual([])
    expect(store.totalCount).toBe(0)
    expect(store.totalPrice).toBe(0)
    expect(store.hasRxItems).toBe(false)
  })

  it('adds item to cart', () => {
    const store = useCartStore()
    const newItem = {
      drugId: 'drug-1',
      name: '阿莫西林胶囊',
      specification: '0.25g*24粒',
      manufacturer: '华北制药',
      price: 28.5,
      quantity: 2,
      image: 'https://example.com/drug.jpg',
      disease: '感冒',
      usage: '口服',
      isRx: true
    }
    
    store.addItem(newItem)
    
    expect(store.items.length).toBe(1)
    expect(store.items[0].drugId).toBe('drug-1')
    expect(store.items[0].quantity).toBe(2)
    expect(store.hasRxItems).toBe(true)
  })

  it('increases quantity when adding existing item', () => {
    const store = useCartStore()
    const item = {
      drugId: 'drug-1',
      name: '阿莫西林胶囊',
      specification: '',
      manufacturer: '',
      price: 28.5,
      quantity: 2,
      image: '',
      disease: '',
      usage: '',
      isRx: false
    }
    
    store.addItem(item)
    store.addItem({ ...item, quantity: 3 })
    
    expect(store.items.length).toBe(1)
    expect(store.items[0].quantity).toBe(5)
  })

  it('updates quantity correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.updateQuantity('1', 5)
    
    expect(store.items[0].quantity).toBe(5)
  })

  it('removes item when quantity is 0 or less', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.updateQuantity('1', 0)
    
    expect(store.items.length).toBe(0)
  })

  it('updates disease correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.updateDisease('1', '感冒')
    
    expect(store.items[0].disease).toBe('感冒')
  })

  it('updates usage correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.updateUsage('1', '一日三次')
    
    expect(store.items[0].usage).toBe('一日三次')
  })

  it('removes item correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false },
      { id: '2', drugId: 'd2', name: '药品2', quantity: 1, price: 20, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.removeItem('1')
    
    expect(store.items.length).toBe(1)
    expect(store.items[0].id).toBe('2')
  })

  it('clears cart correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    store.clearCart()
    
    expect(store.items.length).toBe(0)
    expect(store.totalCount).toBe(0)
    expect(store.totalPrice).toBe(0)
  })

  it('calculates total count correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false },
      { id: '2', drugId: 'd2', name: '药品2', quantity: 3, price: 20, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    expect(store.totalCount).toBe(5)
  })

  it('calculates total price correctly', () => {
    const store = useCartStore()
    store.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false },
      { id: '2', drugId: 'd2', name: '药品2', quantity: 1, price: 25.5, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]
    
    expect(store.totalPrice).toBe(45.5)
  })

  it('detects Rx items correctly', () => {
    const store = useCartStore()
    
    expect(store.hasRxItems).toBe(false)
    
    store.addItem({
      drugId: 'rx-drug-1',
      name: '处方药1',
      specification: '',
      manufacturer: '',
      price: 50,
      quantity: 1,
      image: '',
      disease: '',
      usage: '',
      isRx: true
    })
    
    expect(store.hasRxItems).toBe(true)
  })

  it('saves to localStorage', () => {
    const store = useCartStore()
    const setItemSpy = vi.spyOn(Storage.prototype, 'setItem')
    
    store.addItem({
      drugId: 'd1',
      name: '药品1',
      specification: '',
      manufacturer: '',
      price: 10,
      quantity: 1,
      image: '',
      disease: '',
      usage: '',
      isRx: false
    })
    
    expect(setItemSpy).toHaveBeenCalled()
  })
})

describe('Order Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with default state', () => {
    const store = useOrderStore()
    
    expect(store.orders).toEqual([])
    expect(store.currentOrder).toBeNull()
    expect(store.loading).toBe(false)
    expect(store.hasMore).toBe(true)
    expect(store.orderStats).toEqual({
      totalCount: 0,
      pendingPayment: 0,
      pendingShipment: 0,
      pendingReceipt: 0,
      pendingReview: 0,
      afterSale: 0,
      totalAmount: 0
    })
  })

  it('sets orders correctly', () => {
    const store = useOrderStore()
    const mockOrders = [
      { id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] },
      { id: '2', orderNo: 'DM002', status: 'paid', totalAmount: 200, items: [] }
    ]
    
    store.setOrders(mockOrders)
    
    expect(store.orders).toEqual(mockOrders)
    expect(store.pendingOrders.length).toBe(1)
    expect(store.paidOrders.length).toBe(1)
  })

  it('adds orders correctly', () => {
    const store = useOrderStore()
    const initialOrders = [{ id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] }]
    store.setOrders(initialOrders)
    
    const newOrders = [{ id: '2', orderNo: 'DM002', status: 'paid', totalAmount: 200, items: [] }]
    store.addOrders(newOrders)
    
    expect(store.orders.length).toBe(2)
  })

  it('sets current order correctly', () => {
    const store = useOrderStore()
    const mockOrder = { id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] }
    
    store.setCurrentOrder(mockOrder)
    
    expect(store.currentOrder).toEqual(mockOrder)
  })

  it('updates order status correctly', () => {
    const store = useOrderStore()
    store.orders = [
      { id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] }
    ]
    
    store.updateOrderStatus('1', 'paid')
    
    expect(store.orders[0].status).toBe('paid')
  })

  it('removes order correctly', () => {
    const store = useOrderStore()
    store.orders = [
      { id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] },
      { id: '2', orderNo: 'DM002', status: 'paid', totalAmount: 200, items: [] }
    ]
    
    store.removeOrder('1')
    
    expect(store.orders.length).toBe(1)
    expect(store.orders[0].id).toBe('2')
  })

  it('sets order stats correctly', () => {
    const store = useOrderStore()
    const mockStats = {
      totalCount: 10,
      pendingPayment: 2,
      pendingShipment: 3,
      pendingReceipt: 2,
      pendingReview: 2,
      afterSale: 1,
      totalAmount: 5000
    }
    
    store.setOrderStats(mockStats)
    
    expect(store.orderStats).toEqual(mockStats)
  })

  it('sets loading state correctly', () => {
    const store = useOrderStore()
    
    store.setLoading(true)
    expect(store.loading).toBe(true)
    
    store.setLoading(false)
    expect(store.loading).toBe(false)
  })

  it('sets hasMore correctly', () => {
    const store = useOrderStore()
    
    store.setHasMore(false)
    expect(store.hasMore).toBe(false)
  })

  it('clears orders correctly', () => {
    const store = useOrderStore()
    store.orders = [
      { id: '1', orderNo: 'DM001', status: 'pending', totalAmount: 100, items: [] }
    ]
    store.hasMore = false
    
    store.clearOrders()
    
    expect(store.orders).toEqual([])
    expect(store.hasMore).toBe(true)
  })
})
