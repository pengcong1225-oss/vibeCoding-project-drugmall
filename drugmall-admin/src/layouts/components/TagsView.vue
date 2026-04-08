<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 已访问的标签列表
const visitedViews = ref<RouteLocationNormalizedLoaded[]>([])

// 当前激活的标签
const currentView = ref('')

// 添加标签
const addView = (view: RouteLocationNormalizedLoaded) => {
  if (visitedViews.value.some(v => v.path === view.path)) {
    return
  }
  visitedViews.value.push(view)
}

// 关闭标签
const closeView = (view: RouteLocationNormalizedLoaded) => {
  const index = visitedViews.value.findIndex(v => v.path === view.path)
  if (index === -1) return
  
  visitedViews.value.splice(index, 1)
  
  // 如果关闭的是当前标签，跳转到相邻标签
  if (currentView.value === view.path) {
    const toPath = index === visitedViews.value.length 
      ? visitedViews.value[index - 1]?.path 
      : visitedViews.value[index]?.path
    if (toPath) {
      router.push(toPath)
    }
  }
}

// 关闭其他标签
const closeOthers = (view: RouteLocationNormalizedLoaded) => {
  visitedViews.value = visitedViews.value.filter(v => v.path === view.path)
  router.push(view.path)
}

// 关闭所有标签
const closeAll = () => {
  visitedViews.value = []
  router.push('/dashboard')
}

// 刷新标签
const refreshView = (view: RouteLocationNormalizedLoaded) => {
  router.replace({
    path: '/redirect' + view.path,
    query: view.query
  })
}

// 监听路由变化
watch(
  () => route.path,
  () => {
    if (route.meta && !route.meta.hidden) {
      addView(route)
    }
    currentView.value = route.path
  },
  { immediate: true }
)
</script>

<template>
  <div class="tags-view">
    <el-scrollbar>
      <div class="tags-list">
        <div
          v-for="tag in visitedViews"
          :key="tag.path"
          class="tags-item"
          :class="{ active: currentView === tag.path }"
          @click="router.push(tag.path)"
        >
          <span class="title">{{ tag.meta?.title }}</span>
          <el-icon class="close-icon" @click.stop="closeView(tag)">
            <Close />
          </el-icon>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<style scoped lang="scss">
.tags-view {
  height: 40px;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12);
  
  :deep(.el-scrollbar__wrap) {
    height: 40px;
  }
  
  .tags-list {
    display: flex;
    align-items: center;
    height: 40px;
    padding: 0 10px;
  }
  
  .tags-item {
    display: flex;
    align-items: center;
    height: 26px;
    padding: 0 12px;
    margin-right: 8px;
    font-size: 12px;
    color: #495060;
    background: #fff;
    border: 1px solid #d8dce5;
    border-radius: 2px;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      background: #f0f0f0;
    }
    
    &.active {
      color: #fff;
      background-color: #409eff;
      border-color: #409eff;
      
      &::before {
        content: "";
        display: inline-block;
        width: 8px;
        height: 8px;
        margin-right: 6px;
        background: #fff;
        border-radius: 50%;
      }
    }
    
    .title {
      margin-right: 4px;
    }
    
    .close-icon {
      font-size: 10px;
      
      &:hover {
        color: #f56c6c;
      }
    }
  }
}
</style>
