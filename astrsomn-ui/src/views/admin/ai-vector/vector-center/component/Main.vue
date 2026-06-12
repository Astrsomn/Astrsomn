<template>
  <div class="vector-center-main">
    <div v-if="!selectedSourceId" class="empty-state">
      {{ t.vectorCenter.main.selectSource }}
    </div>
    <div v-else-if="!selectedStoreId" class="empty-state">
      {{ t.vectorCenter.main.selectStore }}
    </div>
    <template v-else>
      <!-- 固定顶部 -->
      <div class="vector-center-top">
        <RightTop :source="selectedSource" :store="selectedStore" @updated="$emit('store-updated')"/>
      </div>
      <!-- 文档列表 -->
      <div class="vector-center-content">
        <RightCenter
            :docs="docs"
            :selected-doc-id="selectedDocId"
            :store-id="selectedStoreId"
            @changed="$emit('doc-changed')"
            @select-doc="handleSelectDoc"
        />
      </div>
    </template>

    <!-- 段落详情 Modal -->
    <SegmentDetailModal
        :doc-id="selectedDocId"
        :open="segmentModalOpen"
        :store-id="selectedStoreId"
        @update:open="segmentModalOpen = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {usePageTranslation} from '@/locales/pages.ts'
import RightTop from '@/views/admin/ai-vector/vector-center/component/right/RightTop.vue'
import RightCenter from '@/views/admin/ai-vector/vector-center/component/right/RightCenter.vue'
import SegmentDetailModal from '@/views/admin/ai-vector/vector-center/component/right/SegmentDetailModal.vue'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  selectedSourceId?: number | string
  selectedStoreId?: number | string
  selectedSource?: any
  selectedStore?: any
  docs: any[]
  selectedDocId?: number | string
}>()

const emit = defineEmits<{
  'store-updated': []
  'doc-changed': []
  'select-doc': [docId: number | string]
}>()

const segmentModalOpen = ref(false)

const handleSelectDoc = (docId: number | string) => {
  emit('select-doc', docId)
  segmentModalOpen.value = true
}
</script>

<style scoped>

.vector-center-main {
  flex: 1;
  min-height: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--bg-surface);
}


.vector-center-top {
  flex-shrink: 0;
}


.vector-center-content {
  flex: 1;
  overflow-y: auto;
  background-color: var(--bg-surface);
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
