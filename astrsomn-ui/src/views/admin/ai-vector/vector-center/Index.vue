<template>


    <a-layout>
      <!-- 侧边栏 -->
      <a-layout-sider width="256" class="bg-white border-r">
        <Left
          :sources="sources"
          :stores="stores"
          :selected-store-id="selectedStoreId"
          @select-source="handleSelectSource"
          @select-store="handleSelectStore"
          @changed="handleTreeChanged"
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
          <RightTop :store="selectedStore" :source="selectedSource" @updated="handleStoreUpdated" />
          <RightCenter
            :docs="docs"
            :store-id="selectedStoreId"
            :selected-doc-id="selectedDocId"
            @select-doc="handleSelectDoc"
            @changed="handleDocChanged"
          />
          <RightBottom
            :segments="segments"
            :store-id="selectedStoreId"
            :doc-id="selectedDocId"
            @changed="handleBottomChanged"
          />
        </template>
      </a-layout-content>
    </a-layout>

</template>

<script lang="ts" setup>
import { onMounted, watch } from 'vue'
import { Layout } from 'ant-design-vue'
import Left from '@/views/admin/ai-vector/vector-center/component/Left.vue'
import RightTop from '@/views/admin/ai-vector/vector-center/component/RightTop.vue'
import RightCenter from '@/views/admin/ai-vector/vector-center/component/RightCenter.vue'
import RightBottom from '@/views/admin/ai-vector/vector-center/component/RightBottom.vue'
import { useVectorCenterState } from '@/views/admin/ai-vector/vector-center/hooks/useVectorCenterState.ts'

const { Header, Sider, Content } = Layout;

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
  reloadBySource,
  reloadByStore,
  reloadByDoc,
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
  selectedDocId.value = docId
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
  background-color: #ffffff;
}

/* 主内容区样式 */
.ant-layout-content {
  padding: 0;
  overflow-y: auto;
  background-color: #fff;
}

/* 侧边栏样式 */
.ant-layout-sider {
  background-color: #ffffff;
}

.empty-state {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  font-size: 15px;
}
</style>
