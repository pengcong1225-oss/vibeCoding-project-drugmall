import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Loading from './index.vue'

describe('Loading', () => {
  it('renders loading state when visible is true', () => {
    const wrapper = mount(Loading, {
      props: { visible: true }
    })

    expect(wrapper.find('.loading-wrapper').isVisible()).toBe(true)
    expect(wrapper.find('.loading-spinner').exists()).toBe(true)
    expect(wrapper.find('.loading-text').text()).toBe('加载中...')
  })

  it('hides loading state when visible is false', () => {
    const wrapper = mount(Loading, {
      props: { visible: false }
    })

    expect(wrapper.find('.loading-wrapper').isVisible()).toBe(false)
  })

  it('renders custom loading text when provided', () => {
    const wrapper = mount(Loading, {
      props: { 
        visible: true,
        text: '正在加载数据...'
      }
    })

    expect(wrapper.find('.loading-text').text()).toBe('正在加载数据...')
  })

  it('hides text when text is empty', () => {
    const wrapper = mount(Loading, {
      props: { 
        visible: true,
        text: ''
      }
    })

    expect(wrapper.find('.loading-text').exists()).toBe(false)
  })

  it('applies fullscreen class when fullscreen is true', () => {
    const wrapper = mount(Loading, {
      props: { 
        visible: true,
        fullscreen: true
      }
    })

    expect(wrapper.find('.loading-wrapper').classes()).toContain('is-fullscreen')
  })

  it('does not apply fullscreen class when fullscreen is false', () => {
    const wrapper = mount(Loading, {
      props: { 
        visible: true,
        fullscreen: false
      }
    })

    expect(wrapper.find('.loading-wrapper').classes()).not.toContain('is-fullscreen')
  })

  it('applies custom background when provided', () => {
    const wrapper = mount(Loading, {
      props: { 
        visible: true,
        background: 'rgba(0, 0, 0, 0.5)'
      }
    })

    expect(wrapper.find('.loading-wrapper').attributes('style')).toContain('background: rgba(0, 0, 0, 0.5)')
  })

  it('renders spinner with correct structure', () => {
    const wrapper = mount(Loading, {
      props: { visible: true }
    })

    const spinnerRings = wrapper.findAll('.spinner-ring')
    expect(spinnerRings.length).toBe(3)
  })

  it('renders with default props', () => {
    const wrapper = mount(Loading, {
      props: { visible: true }
    })

    expect(wrapper.find('.loading-wrapper').exists()).toBe(true)
    expect(wrapper.find('.loading-text').text()).toBe('加载中...')
    expect(wrapper.find('.loading-wrapper').classes()).not.toContain('is-fullscreen')
  })
})
