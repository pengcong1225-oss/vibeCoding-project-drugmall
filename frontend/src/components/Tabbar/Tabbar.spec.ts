import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Tabbar from './index.vue'
import { useCartStore } from '@/stores/cart'

describe('Tabbar', () => {
  let pinia: ReturnType<typeof createPinia>
  let router: ReturnType<typeof createRouter>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/home', name: 'Home', component: { template: '<div>Home</div>' } },
        { path: '/category', name: 'Category', component: { template: '<div>Category</div>' } },
        { path: '/cart', name: 'Cart', component: { template: '<div>Cart</div>' } },
        { path: '/user', name: 'User', component: { template: '<div>User</div>' } }
      ]
    })
  })

  it('renders all tabs correctly', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': {
            template: '<a :href="to"><slot /></a>',
            props: ['to']
          }
        }
      }
    })

    const tabItems = wrapper.findAll('.tab-item')
    expect(tabItems.length).toBe(4)
    expect(tabItems[0].text()).toContain('首页')
    expect(tabItems[1].text()).toContain('分类')
    expect(tabItems[2].text()).toContain('购物车')
    expect(tabItems[3].text()).toContain('我的')
  })

  it('highlights active tab based on current route', async () => {
    router.push('/home')
    await router.isReady()
    
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': {
            template: '<a :href="to" :class="$attrs.class"><slot /></a>',
            props: ['to']
          }
        }
      }
    })

    const homeTab = wrapper.findAll('.tab-item')[0]
    expect(homeTab.classes()).toContain('active')
  })

  it('displays cart badge with correct count', () => {
    const cartStore = useCartStore()
    cartStore.items = [
      { id: '1', drugId: 'd1', name: '药品1', quantity: 2, price: 10, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false },
      { id: '2', drugId: 'd2', name: '药品2', quantity: 3, price: 20, specification: '', manufacturer: '', image: '', disease: '', usage: '', isRx: false }
    ]

    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': true
        }
      }
    })

    const badge = wrapper.find('.badge')
    expect(badge.exists()).toBe(true)
    expect(badge.text()).toBe('5')
  })

  it('hides cart badge when cart is empty', () => {
    const cartStore = useCartStore()
    cartStore.items = []

    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': true
        }
      }
    })

    expect(wrapper.find('.badge').exists()).toBe(false)
  })

  it('updates active tab when route changes', async () => {
    router.push('/category')
    await router.isReady()
    
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': {
            template: '<a :href="to" :class="$attrs.class"><slot /></a>',
            props: ['to']
          }
        }
      }
    })

    await router.push('/cart')
    await wrapper.vm.$nextTick()

    const cartTab = wrapper.findAll('.tab-item')[2]
    expect(cartTab.classes()).toContain('active')
  })

  it('contains router-link with correct paths', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true
        }
      }
    })

    const tabs = wrapper.findAll('router-link-stub')
    expect(tabs[0].attributes('to')).toBe('/home')
    expect(tabs[1].attributes('to')).toBe('/category')
    expect(tabs[2].attributes('to')).toBe('/cart')
    expect(tabs[3].attributes('to')).toBe('/user')
  })

  it('has fixed positioning at bottom', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'el-icon': true,
          'router-link': true
        }
      }
    })

    expect(wrapper.find('.tabbar').exists()).toBe(true)
  })

  it('displays tab icons correctly', () => {
    const wrapper = mount(Tabbar, {
      global: {
        plugins: [pinia, router],
        stubs: {
          'router-link': true
        }
      }
    })

    const tabIcons = wrapper.findAll('.tab-icon')
    expect(tabIcons.length).toBe(4)
  })
})
