import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Empty from './index.vue'

describe('Empty', () => {
  it('renders default empty state correctly', () => {
    const wrapper = mount(Empty, {
      props: { type: 'default' },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('.empty-title').text()).toBe('暂无数据')
    expect(wrapper.find('.empty-description').text()).toContain('暂时没有相关内容')
  })

  it('renders different empty types correctly', () => {
    const types = ['search', 'cart', 'order', 'address', 'coupon', 'message', 'network'] as const
    type EmptyType = typeof types[number]
    
    const expectedTexts: Record<EmptyType, string> = {
      search: '没有找到相关商品',
      cart: '购物车是空的',
      order: '暂无订单',
      address: '暂无收货地址',
      coupon: '暂无优惠券',
      message: '暂无消息',
      network: '网络异常'
    }

    types.forEach((type) => {
      const wrapper = mount(Empty, {
        props: { type },
        global: {
          stubs: {
            'el-icon': true,
            'el-button': { template: '<button><slot /></button>' }
          }
        }
      })

      expect(wrapper.find('.empty-title').text()).toBe(expectedTexts[type])
    })
  })

  it('renders custom title when provided', () => {
    const wrapper = mount(Empty, {
      props: { 
        type: 'default',
        title: '自定义标题'
      },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('.empty-title').text()).toBe('自定义标题')
  })

  it('renders custom description when provided', () => {
    const wrapper = mount(Empty, {
      props: { 
        type: 'default',
        description: '自定义描述内容'
      },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('.empty-description').text()).toBe('自定义描述内容')
  })

  it('renders custom button text when provided', () => {
    const wrapper = mount(Empty, {
      props: { 
        type: 'default',
        buttonText: '自定义按钮'
      },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('button').text()).toBe('自定义按钮')
  })

  it('emits click event when button is clicked', async () => {
    const wrapper = mount(Empty, {
      props: { type: 'default' },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })

  it('hides button when showButton is false', () => {
    const wrapper = mount(Empty, {
      props: { 
        type: 'default',
        showButton: false 
      },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('.empty-action').exists()).toBe(false)
  })

  it('applies type-specific css classes', () => {
    const wrapper = mount(Empty, {
      props: { type: 'network' },
      global: {
        stubs: {
          'el-icon': true,
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.find('.empty-state').classes()).toContain('type-network')
  })
})
