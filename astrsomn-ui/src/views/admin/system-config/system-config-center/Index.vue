<template>
  <div class="config-center-layout">
    <!-- 左侧 Sidebar 已被上提至 Home.vue 的 AdminSidebar；本页面仅负责主内容 -->
    <Main :current-view-key="currentViewKey"/>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import Main from './component/Main.vue'

const route = useRoute()

const moduleViewKeys = ['users', 'env', 'config', 'messages', 'extensions']

const currentViewKey = computed(() => {
  const view = route.query.view as string | undefined
  if (view && moduleViewKeys.includes(view)) {
    return view
  }
  return 'all'
})
</script>

<style scoped>
.config-center-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  overflow: hidden;
  background-color: var(--bg-surface);
}
</style>
