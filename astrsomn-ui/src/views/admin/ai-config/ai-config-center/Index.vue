<template>
  <a-layout>
    <!-- 左侧侧边栏 -->
    <a-layout-sider width="320" class="bg-white border-r">
      <Sidebar />
    </a-layout-sider>

    <!-- 右侧主内容区域 -->
    <a-layout-content class="bg-gray-50">
      <transition name="fade" mode="out-in">
        <AgentList :key="currentProviderKey" :provider-key="currentProviderKey" />
      </transition>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from './component/Sidebar.vue'
import AgentList from './component/AgentList.vue'

const route = useRoute()
const router = useRouter()

const currentProviderKey = computed(() => {
  const provider = route.query.provider as string | undefined
  return provider || 'all'
})

// 监听路由参数变化，用于高亮侧边栏
watch(
  () => route.query.provider,
  () => {
    // 路由参数变化时会触发 Sidebar 中的 watch
  }
)
</script>

<style scoped>
/* 整体布局样式 */
.ant-layout {
  background-color: var(--bg-surface);
}

/* 主内容区样式 */
.ant-layout-content {
  padding: 0;
  overflow-y: auto;
  background-color: var(--bg-surface);
}

/* 侧边栏样式 */
.ant-layout-sider {
  background-color: var(--bg-card);
  border-right: 1px solid var(--border-default);
}

/* 视图切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
