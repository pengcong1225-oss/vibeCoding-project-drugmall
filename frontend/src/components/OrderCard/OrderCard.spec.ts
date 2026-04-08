import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import OrderCard from './index.vue'
import type { Order, OrderStatus } from '@/types'

const mockOrder: Order = {
  id: 'order-001',
  orderNo: 'DM202401010001',
  status: 'pending' as OrderStatus,
  totalAmount: 128.5,
  totalQuantity: 3,
  deliveryFee: 0,
  createTime: '2024-01-01T10:00:00',
  expireTime: '2024-01-01T10:30:00',
  items: [
    {
      id: 'item-1',
      drugId: 'drug-1',
      name: '阿莫西林胶囊',
      specification: '0.25g*24粒',
      price: 28.5,
      quantity: 2,
      image: 'https://example.com/drug1.jpg',
      isRx: true,
      reviewStatus: 'pending'
    },
    {
      id: 'item-2',
      drugId: 'drug-2',
      name: '布洛芬缓释胶囊',
      specification: '0.3g*20粒',
      price: 15.0,
      quantity: 1,
      image: 'https://example.com/drug2.jpg',
      isRx: false,
      reviewStatus: 'pending'
    }
  ]
}

describe('OrderCard', () => {
  let wrapper: ReturnType<typeof mount>

  const createWrapper = (props = {}) => {
    return mount(OrderCard, {
      props: {
        order: mockOrder,
        ...props
      },
      global: {
        mocks: {
          $router: {
            push: vi.fn()
          }
        },
        stubs: {
          'el-icon': true,
          'el-button': true
        }
      }
    })
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders order number correctly', () => {
    wrapper = createWrapper()
    expect(wrapper.find('.order-no .value').text()).toBe(mockOrder.orderNo)
  })

  it('renders correct status text for pending order', () => {
    wrapper = createWrapper()
    const statusEl = wrapper.find('.order-status')
    expect(statusEl.text()).toBe('待支付')
    expect(statusEl.attributes('style')).toContain('color: #ff9500')
  })

  it('renders correct status text for paid order', () => {
    const paidOrder = { ...mockOrder, status: 'paid' as OrderStatus }
    wrapper = createWrapper({ order: paidOrder })
    expect(wrapper.find('.order-status').text()).toBe('已支付')
  })

  it('renders correct status text for shipped order', () => {
    const shippedOrder = { ...mockOrder, status: 'shipped' as OrderStatus }
    wrapper = createWrapper({ order: shippedOrder })
    expect(wrapper.find('.order-status').text()).toBe('配送中')
  })

  it('displays drug thumbnails correctly', () => {
    wrapper = createWrapper()
    const thumbnails = wrapper.findAll('.drug-thumb')
    expect(thumbnails.length).toBe(2)
  })

  it('displays more count when there are more than 3 items', () => {
    const orderWithManyItems = {
      ...mockOrder,
      items: [
        ...mockOrder.items,
        { ...mockOrder.items[0], id: 'item-3' },
        { ...mockOrder.items[0], id: 'item-4' }
      ]
    }
    wrapper = createWrapper({ order: orderWithManyItems })
    const moreCount = wrapper.find('.more-count')
    expect(moreCount.exists()).toBe(true)
    expect(moreCount.text()).toBe('+1')
  })

  it('displays total amount correctly', () => {
    wrapper = createWrapper()
    const totalPrice = wrapper.find('.total-price .value')
    expect(totalPrice.text()).toBe('128.50')
  })

  it('displays total quantity correctly', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('共3件商品')
  })

  it('displays pay button for pending order', () => {
    wrapper = createWrapper()
    const buttons = wrapper.findAll('el-button')
    const buttonTexts = buttons.map(b => b.text())
    expect(buttonTexts).toContain('立即支付')
  })

  it('displays cancel button for pending order', () => {
    wrapper = createWrapper()
    const buttons = wrapper.findAll('el-button')
    const buttonTexts = buttons.map(b => b.text())
    expect(buttonTexts).toContain('取消订单')
  })

  it('emits pay event when pay button is clicked', async () => {
    wrapper = createWrapper()
    await wrapper.find('el-button').trigger('click')
    expect(wrapper.emitted('pay')).toBeTruthy()
  })

  it('emits cancel event when cancel button is clicked', async () => {
    wrapper = createWrapper()
    const buttons = wrapper.findAll('el-button')
    const cancelButton = buttons.find(b => b.text() === '取消订单')
    if (cancelButton) {
      await cancelButton.trigger('click')
      expect(wrapper.emitted('cancel')).toBeTruthy()
    }
  })

  it('displays confirm button for shipped order', () => {
    const shippedOrder = { ...mockOrder, status: 'shipped' as OrderStatus }
    wrapper = createWrapper({ order: shippedOrder })
    const buttons = wrapper.findAll('el-button')
    const buttonTexts = buttons.map(b => b.text())
    expect(buttonTexts).toContain('确认收货')
  })

  it('displays view detail button', () => {
    wrapper = createWrapper()
    const buttons = wrapper.findAll('el-button')
    const buttonTexts = buttons.map(b => b.text())
    expect(buttonTexts).toContain('查看详情')
  })

  it('shows expire time for pending order', () => {
    wrapper = createWrapper()
    const orderTime = wrapper.find('.order-time')
    expect(orderTime.exists()).toBe(true)
    expect(orderTime.text()).toContain('支付截止时间')
  })

  it('does not show expire time for non-pending orders', () => {
    const paidOrder = { ...mockOrder, status: 'paid' as OrderStatus }
    wrapper = createWrapper({ order: paidOrder })
    expect(wrapper.find('.order-time').exists()).toBe(false)
  })

  it('displays delivery fee when greater than 0', () => {
    const orderWithDelivery = { ...mockOrder, deliveryFee: 10 }
    wrapper = createWrapper({ order: orderWithDelivery })
    expect(wrapper.text()).toContain('含运费')
    expect(wrapper.text()).toContain('¥10.00')
  })

  it('does not display delivery fee when 0', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).not.toContain('含运费')
  })
})