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
        <ConversationCard
          v-for="item in list"
          :key="item.memoryKey || item.id"
          :conversation="item"
          :conversation-count="1"
          :latest-time="item.updateTime || item.createTime || ''"
          :is-selected="selectedRowKeys.includes(item.memoryKey || '')"
          @select="(memoryKey) => handleCardSelect(memoryKey)"
          @recover="(memoryKey) => handleRecoverConversation(memoryKey)"
          @delete="(memoryKey) => handleDeleteOne(memoryKey)"
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
import { computed, reactive, ref, defineProps, defineEmits } from 'vue'
import { message } from 'ant-design-vue'
import AstrsomnOverview from '@/components/home/AstrsomnOverview.vue'
import ConversationCard from './ConversationCard.vue'
import { aiConversationApi, type AiConversation } from '@/api/aiConversation'

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
  (e: 'pageChange', page: number, pageSize: number): void
  (e: 'fetchList'): void
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
  const conversations = props.list.filter(c => c.memoryKey === memoryKey)
  const ids = conversations.map(c => c.id).filter((id): id is number | string => id !== undefined && id !== null)
  
  if (ids.length === 0) return
  const msg = await aiConversationApi.delete(ids)
  message.success(msg)
  const newSelected = props.selectedRowKeys.filter(key => key !== memoryKey)
  emit('update:selectedRowKeys', newSelected)
  emit('fetchList')
}

const onPageChange = (p: number, size: number) => {
  emit('pageChange', p, size)
}
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