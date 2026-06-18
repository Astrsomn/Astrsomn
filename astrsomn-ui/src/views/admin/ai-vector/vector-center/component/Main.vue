<template>
  <div class="vector-center-main">
    <div v-if="!selectedSourceId" class="empty-state">
      {{ t.vectorCenter.main.selectSource }}
    </div>
    <div v-else-if="!selectedStoreId" class="empty-state">
      {{ t.vectorCenter.main.selectStore }}
    </div>
    <template v-else>
      <div class="vector-center-top">
        <RightTop
            :source="selectedSource"
            :store="selectedStore"
            :store-id="selectedStoreId"
            :doc-count="docCount"
            :folder-path="folderPath"
            :search-keyword="keyword"
            :view-size="viewSize"
            :current-folder-id="currentFolderId"
            @updated="$emit('store-updated')"
            @search="keyword = $event"
            @navigate="handleNavigate"
            @view-size-change="viewSize = $event"
            @open-create-folder="rightCenterRef?.openCreateFolder()"
        />
      </div>
      <div class="vector-center-content">
        <RightCenter
            ref="rightCenterRef"
            :docs="docs"
            :selected-doc-id="selectedDocId"
            :store-id="selectedStoreId"
            :current-folder-id="currentFolderId"
            :folder-path="folderPath"
            :keyword="keyword"
            :view-size="viewSize"
            @changed="$emit('doc-changed')"
            @select-doc="handleSelectDoc"
            @update:folder-path="handleFolderPathUpdate"
        />
      </div>
    </template>

    <SegmentDetailModal
        :doc-id="selectedDocId"
        :open="segmentModalOpen"
        :store-id="selectedStoreId"
        @update:open="segmentModalOpen = $event"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
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
const docCount = computed(() => props.docs?.length ?? 0)

// ── Bridge state: shared between RightTop and RightCenter ──
const currentFolderId = ref<number | string | null>(null)
const folderPath = ref<Array<{ id: number | string; name: string }>>([])
const keyword = ref('')
const viewSize = ref<'small' | 'medium' | 'large' | 'list'>('small')
const rightCenterRef = ref<InstanceType<typeof RightCenter> | null>(null)

const handleNavigate = (folderId: number | string | null, path: Array<{ id: number | string; name: string }>) => {
  currentFolderId.value = folderId
  folderPath.value = path
}

const handleFolderPathUpdate = (path: Array<{ id: number | string; name: string }>) => {
  folderPath.value = path
  currentFolderId.value = path.length > 0 ? path[path.length - 1].id : null
}

const handleSelectDoc = (docId: number | string) => {
  emit('select-doc', docId)
  segmentModalOpen.value = true
}

// Reset folder/search state when store changes
watch(() => props.selectedStoreId, () => {
  currentFolderId.value = null
  folderPath.value = []
  keyword.value = ''
})
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
  animation: vc-fade-in 0.35s ease;
}

@keyframes vc-fade-in {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}


.vector-center-top {
  flex-shrink: 0;
}


.vector-center-content {
  flex: 1;
  overflow-y: auto;
  background-color: var(--bg-surface);
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
  animation: vc-fade-in 0.4s 0.15s ease both, float-pulse 3s 0.6s ease-in-out infinite;
}

@keyframes float-pulse {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
</style>
