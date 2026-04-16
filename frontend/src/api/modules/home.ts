import request from '@/api/request'
import type { HomePageConfig } from '@/types/home'

/**
 * 首页相关 API
 */
export const homeApi = {
  /**
   * 获取首页渲染配置
   * @returns 首页完整配置数据
   */
  getHomePageRender: () => {
    return request.get<HomePageConfig>('/home/render/page')
  }
}
