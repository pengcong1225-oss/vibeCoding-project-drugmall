import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, RouterLinkStub } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import DrugCard from './index.vue'
import type { Drug } from '@/types'

// Mock drug data
const mockDrug: Drug = {
  id: '1',
  name: '阿莫西林胶囊',
  specification: '0.25g*24粒',
  manufacturer: '华北制药',
  price: 28.5,
  originalPrice: 35.0,
  image: 'https://example.com/drug1.jpg',
  isRx: true,
  tags: ['抗生素', '消炎'],
  sales: 1523,
  disease: '呼吸道感染',
  usage: '口服，一次2粒，一日3次'
}

const mockDrugNoDiscount: Drug = {
  ...mockDrug,
  id: '2',
  originalPrice: 28.5,
  price: 28.5
}

describe('DrugCard', () => {
  let router: ReturnType<typeof createRouter>

  beforeEach(() => {
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/drug/:id', name: 'DrugDetail', component: { template: '<div>Drug Detail</div>' } }
      ]
    })
  })

  it('renders drug information correctly', async () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        plugins: [router],
        stubs: {
          'el-icon': true,
          'router-link': RouterLinkStub
        }
      }
    })

    expect(wrapper.find('.drug-name').text()).toBe(mockDrug.name)
    expect(wrapper.find('.drug-spec').text()).toBe(mockDrug.specification)
    expect(wrapper.find('.price-value').text()).toBe('28.50')
  })

  it('displays discount badge when there is discount', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.discount-badge').exists()).toBe(true)
    expect(wrapper.find('.discount-badge').text()).toBe('8折')
    expect(wrapper.find('.original-price').exists()).toBe(true)
  })

  it('does not display discount badge when there is no discount', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrugNoDiscount },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.discount-badge').exists()).toBe(false)
    expect(wrapper.find('.original-price').exists()).toBe(false)
  })

  it('displays Rx badge for prescription drugs', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.rx-badge').exists()).toBe(true)
    expect(wrapper.find('.rx-badge').text()).toBe('Rx')
    expect(wrapper.find('.drug-card').classes()).toContain('is-rx')
  })

  it('emits addToCart event when add button is clicked', async () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    await wrapper.find('.add-btn').trigger('click')
    
    expect(wrapper.emitted('addToCart')).toBeTruthy()
    expect(wrapper.emitted('addToCart')![0]).toEqual([mockDrug])
  })

  it('navigates to drug detail when card is clicked', async () => {
    const push = vi.fn()
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        mocks: {
          $router: { push }
        },
        stubs: {
          'el-icon': true
        }
      }
    })

    await wrapper.find('.drug-card').trigger('click')
    
    expect(push).toHaveBeenCalledWith(`/drug/${mockDrug.id}`)
  })

  it('renders in horizontal layout when specified', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug, layout: 'horizontal' },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.drug-card').classes()).toContain('layout-horizontal')
  })

  it('renders in vertical layout by default', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.drug-card').classes()).toContain('layout-vertical')
  })

  it('displays sales count when showSales is true', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug, showSales: true },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.drug-sales').exists()).toBe(true)
    expect(wrapper.find('.drug-sales').text()).toContain('已售')
  })

  it('displays tags when showTag is true', () => {
    const wrapper = mount(DrugCard, {
      props: { drug: mockDrug, showTag: true },
      global: {
        stubs: {
          'el-icon': true
        }
      }
    })

    expect(wrapper.find('.drug-tags').exists()).toBe(true)
  })
})