import { vi } from 'vitest'
import { config } from '@vue/test-utils'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
})

// Mock sessionStorage
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock
})

// Mock matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn()
  }))
})

// Mock IntersectionObserver
class IntersectionObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}
Object.defineProperty(window, 'IntersectionObserver', {
  writable: true,
  value: IntersectionObserverMock
})

// Mock ResizeObserver
class ResizeObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}
Object.defineProperty(window, 'ResizeObserver', {
  writable: true,
  value: ResizeObserverMock
})

// Mock Element Plus icons
const mockIcons = {
  HomeFilled: 'HomeFilled',
  Grid: 'Grid',
  ShoppingCart: 'ShoppingCart',
  ShoppingCartFull: 'ShoppingCartFull',
  User: 'User',
  UserFilled: 'UserFilled',
  Plus: 'Plus',
  Clock: 'Clock',
  Box: 'Box',
  Search: 'Search',
  Document: 'Document',
  Location: 'Location',
  Ticket: 'Ticket',
  ChatDotRound: 'ChatDotRound',
  Connection: 'Connection'
}

// Vue Test Utils global config
config.global.stubs = {
  'el-icon': true,
  'el-button': true,
  ...Object.fromEntries(
    Object.keys(mockIcons).map(name => [`el-icon-${name.toLowerCase()}`, true])
  )
}

// Mock console methods in test environment
global.console = {
  ...console,
  error: vi.fn(),
  warn: vi.fn()
}
