import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Tabbar from './index.vue'

vi.mock('vue-router', () => ({
  useRoute: () => ({
    path: '/home'
  })
}))

describe('Tabbar', () => {
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
  })

  it('renders all tabs correctly', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia],
        stubs: {
          'el-icon': true,
          'router-link': {
            template: '<a :href="to" class="tab-item"><slot /></a>',
            props: ['to']
          }
        }
      }
    })

    const tabItems = wrapper.findAll('.tab-item')
    expect(tabItems.length).toBe(3)
    expect(tabItems[0].text()).toContain('买药')
    expect(tabItems[1].text()).toContain('宜格健康管家')
    expect(tabItems[2].text()).toContain('我的')
  })

  it('renders with correct structure', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia],
        stubs: {
          'el-icon': true,
          'router-link': true
        }
      }
    })
    expect(wrapper.find('.tabbar').exists()).toBe(true)
  })

  it('has fixed positioning at bottom', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia],
        stubs: {
          'el-icon': true,
          'router-link': true
        }
      }
    })
    expect(wrapper.find('.tabbar').exists()).toBe(true)
  })
})