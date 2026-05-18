<template>
  <div class="config-center-layout">
    <Sidebar
        @select="handleSidebarSelect"
        @update:collapsed="sidebarCollapsed = $event"
    />
    <Main :current-view-key="currentViewKey"/>
  </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import Sidebar from './component/Sidebar.vue'
import Main from './component/Main.vue'

const route = useRoute()
const router = useRouter()

const moduleViewKeys = ['users', 'env', 'config', 'messages', 'extensions']

const currentViewKey = computed(() => {
  const view = route.query.view as string | undefined
  if (view && moduleViewKeys.includes(view)) {
    return view
  }
  return 'all'
})

const configCenterPath = '/admin/system'

const sidebarCollapsed = ref(false)

const handleSidebarSelect = (key: string) => {
  if (key === 'all') {
    router.push({path: configCenterPath, query: {}})
  } else {
    router.push({path: configCenterPath, query: {view: key}})
  }
}
</script>

<style scoped>
.config-center-layout {
  height: calc(100vh - 60px);
  display: flex;
  overflow: hidden;
  background-color: var(--bg-surface);
}
</style>
