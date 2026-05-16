<template>
  <div class="vector-center-layout">
    <!-- 侧边栏固定 -->
    <div :class="{ collapsed: sidebarCollapsed }" class="vector-center-sider">
      <Left
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
    <div class="vector-center-main">
      <div v-if="!selectedSourceId" class="empty-state">
        请先在左侧选择数据源
      </div>
      <div v-else-if="!selectedStoreId" class="empty-state">
        请先在左侧选择数据库
      </div>
      <template v-else>
        <!-- 固定顶部 -->
        <div class="vector-center-top">
          <RightTop :source="selectedSource" :store="selectedStore" @updated="handleStoreUpdated"/>
        </div>
        <!-- 可滚动内容 -->
        <div class="vector-center-content">
          <div class="snap-section">
            <RightCenter
                :docs="docs"
                :selected-doc-id="selectedDocId"
                :store-id="selectedStoreId"
                @changed="handleDocChanged"
                @select-doc="handleSelectDoc"
            />
            <VectorSearchPanel :store-id="selectedStoreId"/>
          </div>
          <div class="snap-section">
            <RightBottom
                :doc-id="selectedDocId"
                :segments="segments"
                :store-id="selectedStoreId"
                @changed="handleBottomChanged"
            />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import Left from '@/views/admin/ai-vector/vector-center/component/Left.vue'
import RightTop from '@/views/admin/ai-vector/vector-center/component/RightTop.vue'
import RightCenter from '@/views/admin/ai-vector/vector-center/component/RightCenter.vue'
import RightBottom from '@/views/admin/ai-vector/vector-center/component/RightBottom.vue'
import VectorSearchPanel from '@/views/admin/ai-vector/vector-center/component/VectorSearchPanel.vue'
import {useVectorCenterState} from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState.ts'

const sidebarCollapsed = ref(false)

const {
  sources,
  stores,
  docs,
  segments,
  selectedSource,
  selectedStore,
  selectedStoreId,
  selectedDocId,
  selectedSourceId,
  bootstrap,
  reloadByDoc,
  fetchSources,
  fetchStores,
  fetchDocs,
  fetchSegments
} = useVectorCenterState()

const handleSelectSource = async (sourceId: number | string) => {
  selectedSourceId.value = sourceId
  selectedStoreId.value = undefined
  selectedDocId.value = undefined
  docs.value = []
  segments.value = []
  await fetchStores()
}

const handleSelectStore = async (storeId: number | string) => {
  selectedStoreId.value = storeId
  selectedDocId.value = undefined
  segments.value = []
  await fetchDocs()
}

const handleSelectDoc = async (docId: number | string) => {
  selectedDocId.value = docId || undefined
  if (!selectedDocId.value) {
    segments.value = []
    return
  }
  await fetchSegments()
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

const handleBottomChanged = async () => {
  await fetchSegments()
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
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  display: flex;
}

/* 侧边栏固定 */
.vector-center-sider {
  width: 320px;
  height: 100%;
  overflow: hidden;
  flex-shrink: 0;
  background-color: var(--bg-card);
  border-right: 1px solid var(--border-default);
  z-index: 10;
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.vector-center-sider.collapsed {
  width: 56px;
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
