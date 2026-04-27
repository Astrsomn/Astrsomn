<template>
  <div class="left-panel">
    <AstrsomnOverview
      :list-length="list.length"
      :selected-count="selectedRowKeys.length"
      :all-current-selected="allCurrentSelected"
      :part-current-selected="partCurrentSelected"
      :show-actions="list.length > 0"
      :summary-text="`当前页 ${list.length} 条对话组，已选 ${selectedRowKeys.length} 条。`"
      @toggle-select-all="toggleSelectAllCurrentPage"
    />

    <a-spin :spinning="loading">
      <div class="conversation-cards">
        <SessionList
          :loading="loading"
          :items="sessionItems"
          :selected-keys="selectedRowKeys"
          :selectable="true"
          :deletable="true"
          @toggle-select="handleCardSelect"
          @open="handleRecoverConversation"
          @delete="handleDeleteOne"
        />
      </div>

      <div v-if="list.length === 0" class="empty-wrap">
        <a-empty description="暂无匹配的对话组" />
      </div>

      <div class="pagination-wrap">
        <a-pagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :total="page.total"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits } from 'vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import SessionList from '@/components/chat-session/SessionList.vue'
import { adaptConversationToSessionItem, type AiConversation } from '@/api/aiConversation'

const props = defineProps<{
  loading: boolean
  list: AiConversation[]
  selectedRowKeys: string[]
  page: {
    pageNum: number
    pageSize: number
    total: number
  }
}>()

const emit = defineEmits<{
  (e: 'update:selectedRowKeys', value: string[]): void
  (e: 'recover', memoryKey: string): void
  (e: 'delete', memoryKey: string): void
  (e: 'pageChange', page: number, pageSize: number): void
}>()

const currentPageIds = computed(() =>
  props.list
    .map((item) => item.memoryKey)
    .filter((id): id is string => id !== undefined && id !== null)
)

const allCurrentSelected = computed(() => {
  return currentPageIds.value.length > 0 && currentPageIds.value.every((id) => props.selectedRowKeys.includes(id))
})

const partCurrentSelected = computed(() => {
  if (currentPageIds.value.length === 0) return false
  const count = currentPageIds.value.filter((id) => props.selectedRowKeys.includes(id)).length
  return count > 0 && count < currentPageIds.value.length
})

const toggleSelectAllCurrentPage = (checked: boolean) => {
  if (checked) {
    const newSelected = Array.from(new Set([...props.selectedRowKeys, ...currentPageIds.value]))
    emit('update:selectedRowKeys', newSelected)
    return
  }
  const newSelected = props.selectedRowKeys.filter((id) => !currentPageIds.value.includes(id))
  emit('update:selectedRowKeys', newSelected)
}

const handleCardSelect = (memoryKey: string) => {
  const index = props.selectedRowKeys.indexOf(memoryKey)
  let newSelected: string[]
  if (index > -1) {
    newSelected = [...props.selectedRowKeys]
    newSelected.splice(index, 1)
  } else {
    newSelected = [...props.selectedRowKeys, memoryKey]
  }
  emit('update:selectedRowKeys', newSelected)
}

const handleRecoverConversation = async (memoryKey: string) => {
  emit('recover', memoryKey)
}

const handleDeleteOne = async (memoryKey: string) => {
  emit('delete', memoryKey)
}

const onPageChange = (p: number, size: number) => {
  emit('pageChange', p, size)
}

const sessionItems = computed(() =>
  props.list
    .filter((item) => !!item.memoryKey)
    .map((item) => adaptConversationToSessionItem(item))
)
</script>

<style scoped>
.left-panel {
  flex: 0 0 30%;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.conversation-cards {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.conversation-cards::-webkit-scrollbar {
  width: 6px;
}

.conversation-cards::-webkit-scrollbar-track {
  background: var(--bg-surface);
  border-radius: 3px;
}

.conversation-cards::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}

.conversation-cards::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}

.empty-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}

.pagination-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  flex-wrap: wrap;
}

@media (max-width: 1200px) {
  .left-panel {
    flex: none;
    min-width: unset;
    width: 100%;
  }
}
</style>