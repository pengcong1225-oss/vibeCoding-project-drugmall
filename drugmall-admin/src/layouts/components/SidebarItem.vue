<script setup lang="ts">
import { computed } from 'vue'

interface MenuItem {
  path: string
  meta?: {
    title: string
    icon?: string
  }
  children?: MenuItem[]
  hidden?: boolean
}

const props = defineProps<{
  item: MenuItem
  basePath?: string
}>()

// 是否只有一个子菜单
const _isOnlyOneChild = computed(() => {
  if (!props.item.children || props.item.children.length === 0) {
    return true
  }
  // 过滤掉 hidden 的菜单
  const showingChildren = props.item.children.filter(item => !item.hidden)
  return showingChildren.length === 1
})

// 获取菜单标题
const getMenuTitle = (item: MenuItem) => {
  return item.meta?.title || ''
}

// 获取菜单图标
const getMenuIcon = (item: MenuItem) => {
  return item.meta?.icon || ''
}
</script>

<template>
  <!-- 没有子菜单 -->
  <el-menu-item v-if="!item.children || item.children.length === 0" :index="item.path">
    <el-icon v-if="getMenuIcon(item)">
      <component :is="getMenuIcon(item)" />
    </el-icon>
    <template #title>{{ getMenuTitle(item) }}</template>
  </el-menu-item>
  
  <!-- 有子菜单 -->
  <el-sub-menu v-else :index="item.path">
    <template #title>
      <el-icon v-if="getMenuIcon(item)">
        <component :is="getMenuIcon(item)" />
      </el-icon>
      <span>{{ getMenuTitle(item) }}</span>
    </template>
    <sidebar-item
      v-for="child in item.children"
      :key="child.path"
      :item="child"
    />
  </el-sub-menu>
</template>
