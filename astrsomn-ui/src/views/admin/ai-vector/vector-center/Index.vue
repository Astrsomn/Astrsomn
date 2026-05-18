<template>
  <div class="vector-center-layout">
    <!-- 侧边栏固定 -->
    <div class="vector-center-sider">
      <Sidebar
          :collapsed="sidebarCollapsed"
          :selected-store-id="selectedStoreId"
          :sources="sources"
          :stores="stores"
          @changed="handleTreeChanged"
          @select-source="handleSelectSource"
          @select-store="handleSelectStore"
          @toggle-collapse="sidebarCollapsed = !sidebarCollapsed"
      />
    </div>

    <!-- 主内容区 -->
    <Main
        :selected-source-id="selectedSourceId"
        :selected-store-id="selectedStoreId"
        :selected-source="selectedSource"
        :selected-store="selectedStore"
        :docs="docs"
        :selected-doc-id="selectedDocId"
        @store-updated="handleStoreUpdated"
        @doc-changed="handleDocChanged"
        @select-doc="handleSelectDoc"
    />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import Sidebar from '@/views/admin/ai-vector/vector-center/component/Sidebar.vue'
import Main from '@/views/admin/ai-vector/vector-center/component/Main.vue'
import {useVectorCenterState} from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState'

const sidebarCollapsed = ref(false)

const {
  sources,
  stores,
  docs,
  selectedSource,
  selectedStore,
  selectedStoreId,
  selectedDocId,
  selectedSourceId,
  bootstrap,
  reloadByDoc,
  fetchSources,
  fetchStores,
  fetchDocs
} = useVectorCenterState()

const handleSelectSource = async (sourceId: number | string) => {
  selectedSourceId.value = sourceId
  selectedStoreId.value = undefined
  selectedDocId.value = undefined
  docs.value = []
  await fetchStores()
}

const handleSelectStore = async (storeId: number | string) => {
  selectedStoreId.value = storeId
  selectedDocId.value = undefined
  await fetchDocs()
}

const handleSelectDoc = async (docId: number | string) => {
  selectedDocId.value = docId || undefined
}

const handleTreeChanged = async () => {
  await fetchSources()
}

const handleStoreUpdated = async () => {
  await fetchStores()
}

const handleDocChanged = async () => {
  await reloadByDoc()
}

onMounted(async () => {
  await bootstrap()
})
</script>

<style scoped>
/* 整体布局占满视口，阻止父容器滚动 */
.vector-center-layout {
  position: fixed;
  top: 60px;
  left: 56px;
  right: 0;
  bottom: 0;
  overflow: hidden;
  display: flex;
}

/* 侧边栏固定 */
.vector-center-sider {

  flex-shrink: 0;
  z-index: 10;
}

/* 右侧主内容区 */
.vector-center-main {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--bg-surface);
}

/* 右侧顶部固定 */
.vector-center-top {
  flex-shrink: 0;
}

/* 右侧可滚动内容区 */
.vector-center-content {
  flex: 1;
  overflow-y: auto;
  background-color: var(--bg-surface);
  scroll-snap-type: y mandatory;
}

/* 磁吸分区 */
.snap-section {
  min-height: 100%;
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-size: 15px;
}
</style>
