import { lazyLoad } from './lazyLoad'
import type { App } from 'vue'

export function registerDirectives(app: App) {
  app.directive('lazy', lazyLoad)
}

export { lazyLoad }
export default registerDirectives
