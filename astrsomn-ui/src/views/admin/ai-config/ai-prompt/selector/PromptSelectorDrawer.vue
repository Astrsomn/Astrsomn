<template>
  <AstDrawer
      :open="props.open"
      :width="560"
      @update:open="handleClose"
  >
    <template #title>选择提示词</template>
    <template #subtitle>AI Prompt</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <AstSearchInput
            v-model="keyword"
            layout="fluid"
            placeholder="搜索标题"
            style="flex: 1"
            @search="handleSearch"
        />
      </div>

      <a-spin :spinning="loading">
        <div class="prompt-list">
          <div
              v-for="prompt in list"
              :key="prompt.id"
              :class="{ selected: selectedId === prompt.id }"
              class="prompt-item"
              @click="handleSelect(prompt)"
          >
            <div class="prompt-icon">
              <EditOutlined/>
            </div>
            <div class="prompt-info">
              <div class="prompt-header">
                <div class="prompt-title">{{ prompt.promptTitle }}</div>
                <div class="prompt-meta">
                  <span class="version-tag">v{{ prompt.version || 1 }}</span>
                  <span :class="prompt.status" class="status-badge">
                    {{ prompt.status === 'enabled' ? '启用' : '禁用' }}
                  </span>
                </div>
              </div>
              <div class="prompt-key">
                <KeyOutlined/>
                {{ prompt.promptKey || '自动生成' }}
              </div>
              <div v-if="prompt.scene" class="prompt-scene">{{ prompt.scene }}</div>
              <div v-if="prompt.promptContent" class="prompt-content-preview">
                {{ truncateContent(prompt.promptContent) }}
              </div>
            </div>
          </div>

          <a-empty v-if="!loading && list.length === 0" description="暂无提示词"/>
        </div>
      </a-spin>
    </div>

    <template #footer>
      <AstPagination
          :current="page.pageNum"
          :page-size="page.pageSize"
          :show-size-changer="true"
          :total="page.total"
          @change="onPageChange"
      />
    </template>
  </AstDrawer>
</template>

<script lang="ts" setup>
import {nextTick, reactive, ref, watch} from 'vue'
import {EditOutlined, KeyOutlined} from '@ant-design/icons-vue'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import {type AiPrompt, aiPromptApi, type PageResponse} from '@/api/aiPrompt.ts'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'select', prompt: AiPrompt): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiPrompt[]>([])
const selectedId = ref<number | string | undefined>()
const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const truncateContent = (raw: string) => {
  const clean = raw.replace(/\s+/g, ' ').trim()
  return clean.length > 80 ? `${clean.slice(0, 80)}...` : clean
}

const fetchList = async () => {
  loading.value = true
  try {
    const payload = {
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        promptTitle: keyword.value || undefined
      }
    }
    const resp: PageResponse<AiPrompt> = await aiPromptApi.queryPage(payload)
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.pageNum = 1
  void fetchList()
}

const onPageChange = (p: number, size: number) => {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

const handleSelect = (prompt: AiPrompt) => {
  selectedId.value = prompt.id
  emit('select', prompt)
}

const handleClose = () => {
  emit('update:open', false)
}

watch(() => props.open, async (val) => {
  if (val) {
    keyword.value = ''
    selectedId.value = undefined
    page.pageNum = 1
    await nextTick()
    void fetchList()
  }
})
</script>

<style scoped>
.select-drawer-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
}

.search-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.prompt-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.prompt-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.prompt-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-card);
}

.prompt-item.selected {
  border-color: var(--primary);
  background: var(--primary-hover);
}

.prompt-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  flex-shrink: 0;
}

.prompt-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.prompt-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.prompt-title {
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.prompt-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.prompt-key {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 4px;
}

.prompt-scene {
  font-size: 12px;
  color: var(--text-hint);
}

.prompt-content-preview {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
}

.version-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 4px;
  color: var(--primary);
  font-weight: 600;
}

.status-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.status-badge.enabled {
  background: #dcfce7;
  color: #16a34a;
}

.status-badge.disabled {
  background: #fee2e2;
  color: #ef4444;
}

:deep(.drawer-footer) {
  justify-content: center;
}
</style>
