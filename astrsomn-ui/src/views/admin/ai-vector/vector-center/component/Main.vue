<template>
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
        <RightTop :source="selectedSource" :store="selectedStore" @updated="$emit('store-updated')"/>
      </div>
      <!-- 可滚动内容 -->
      <div class="vector-center-content">
        <div class="snap-section">
          <RightCenter
              :docs="docs"
              :selected-doc-id="selectedDocId"
              :store-id="selectedStoreId"
              @changed="$emit('doc-changed')"
              @select-doc="$emit('select-doc', $event)"
          />
          <VectorSearchPanel :store-id="selectedStoreId"/>
        </div>
        <div class="snap-section">
          <RightBottom
              :doc-id="selectedDocId"
              :segments="segments"
              :store-id="selectedStoreId"
              @changed="$emit('bottom-changed')"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import RightTop from '@/views/admin/ai-vector/vector-center/component/right/RightTop.vue'
import RightCenter from '@/views/admin/ai-vector/vector-center/component/right/RightCenter.vue'
import RightBottom from '@/views/admin/ai-vector/vector-center/component/right/RightBottom.vue'
import VectorSearchPanel from '@/views/admin/ai-vector/vector-center/component/sidebar/VectorSearchPanel.vue'

defineProps<{
  selectedSourceId?: number | string
  selectedStoreId?: number | string
  selectedSource?: any
  selectedStore?: any
  docs: any[]
  segments: any[]
  selectedDocId?: number | string
}>()

defineEmits<{
  'store-updated': []
  'doc-changed': []
  'select-doc': [docId: number | string]
  'bottom-changed': []
}>()
</script>

<style scoped>
/* 右侧主内容区 */
.vector-center-main {
  flex: 1;
  min-height: 0;
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
