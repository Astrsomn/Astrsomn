<template>
  <div class="vector-center-layout">
    <!-- 左侧 Sidebar 已被上提至 Home.vue 的 AdminSidebar；本页面仅负责主内容 -->
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
import {onMounted} from 'vue'
import Main from '@/views/admin/ai-vector/vector-center/component/Main.vue'
import {useVectorCenterState} from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState'

const {
  docs,
  selectedSource,
  selectedStore,
  selectedStoreId,
  selectedDocId,
  selectedSourceId,
  bootstrap,
  reloadByDoc,
  fetchSources
} = useVectorCenterState()

const handleSelectDoc = async (docId: number | string) => {
  selectedDocId.value = docId || undefined
}

const handleStoreUpdated = async () => {
  await fetchSources()
}

const handleDocChanged = async () => {
  await reloadByDoc()
}

onMounted(async () => {
  await bootstrap()
})
</script>

<style scoped>

.vector-center-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  overflow: hidden;
}
</style>
