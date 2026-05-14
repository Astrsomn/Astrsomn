<template>


  <a-layout>
    <!-- 侧边栏 -->
    <a-layout-sider class="bg-white border-r" width="320">
      <Left
          :selected-store-id="selectedStoreId"
          :sources="sources"
          :stores="stores"
          @changed="handleTreeChanged"
          @select-source="handleSelectSource"
          @select-store="handleSelectStore"
      />
    </a-layout-sider>

    <!-- 主内容区 -->
    <a-layout-content class="bg-gray-50">
      <div v-if="!selectedSourceId" class="empty-state">
        请先在左侧选择数据源
      </div>
      <div v-else-if="!selectedStoreId" class="empty-state">
        请先在左侧选择数据库
      </div>
      <template v-else>
        <RightTop :source="selectedSource" :store="selectedStore" @updated="handleStoreUpdated"/>
        <RightCenter
            :docs="docs"
            :selected-doc-id="selectedDocId"
            :store-id="selectedStoreId"
            @changed="handleDocChanged"
            @select-doc="handleSelectDoc"
        />
        <RightBottom
            :doc-id="selectedDocId"
            :segments="segments"
            :store-id="selectedStoreId"
            @changed="handleBottomChanged"
        />
      </template>
    </a-layout-content>
  </a-layout>

</template>

<script lang="ts" setup>
import {onMounted} from 'vue'
import Left from '@/views/admin/ai-vector/vector-center/component/Left.vue'
import RightTop from '@/views/admin/ai-vector/vector-center/component/RightTop.vue'
import RightCenter from '@/views/admin/ai-vector/vector-center/component/RightCenter.vue'
import RightBottom from '@/views/admin/ai-vector/vector-center/component/RightBottom.vue'
import {useVectorCenterState} from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState.ts'

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

.empty-state {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-size: 15px;
}
</style>
