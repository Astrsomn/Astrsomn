<template>
  <div class="rag-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <DatabaseOutlined/>
        </div>
        <h3 class="card-title">知识库 (RAG)</h3>
      </div>
      <button class="action-btn primary" title="选择知识库" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="kb-list">
      <div v-for="k in knowledgeKeys" :key="k" class="kb-tag">
        <span class="kb-name">{{ k }}</span>
        <CloseOutlined class="kb-close" @click.stop="emit('remove', k)"/>
      </div>
      <div v-if="!knowledgeKeys.length" class="empty-hint">暂未关联知识库</div>
    </div>

    <a-drawer
        :open="drawerOpen"
        :width="520"
        placement="right"
        title="选择向量存储（知识库）"
        @close="drawerOpen = false"
    >
      <div class="selector-content">
        <div class="search-bar">
          <a-input
              v-model:value="keyword"
              allow-clear
              placeholder="集合名称"
              @pressEnter="fetchList"
          >
            <template #prefix>
              <SearchOutlined/>
            </template>
          </a-input>
          <a-button type="primary" @click="fetchList">搜索</a-button>
        </div>
        <a-spin :spinning="loading">
          <div class="item-list">
            <div
                v-for="item in list"
                :key="item.id"
                :class="{ selected: selectedKeys.has(String(item.id || '')) }"
                class="item-row"
                @click="toggle(item)"
            >
              <div class="item-icon">
                <DatabaseOutlined/>
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.collectionName }}</div>
                <div class="item-sub">{{ item.sourceProvider || '未知' }} · dim={{ item.dimension }} ·
                  {{ item.distanceMetric }}
                </div>
              </div>
              <CheckCircleOutlined v-if="selectedKeys.has(String(item.id || ''))" class="check-icon"/>
            </div>
            <div v-if="!list.length && !loading" class="empty-list">暂无向量存储</div>
          </div>
        </a-spin>
        <a-pagination
            v-if="page.total > page.pageSize"
            :current="page.pageNum"
            :page-size="page.pageSize"
            :show-size-changer="false"
            :total="page.total"
            class="pager"
            size="small"
            @change="onPageChange"
        />
      </div>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {CheckCircleOutlined, CloseOutlined, DatabaseOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons-vue'
import {type AiVecStore, aiVecStoreApi} from '@/api/aiVecStore'

const props = defineProps<{
  knowledgeKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'add', kbKey: string, title?: string): void
  (e: 'remove', kbKey: string): void
}>()

const drawerOpen = ref(false)
const keyword = ref('')
const loading = ref(false)
const list = ref<AiVecStore[]>([])
const page = reactive({pageNum: 1, pageSize: 20, total: 0})

const selectedKeys = computed(() => new Set(props.knowledgeKeys))

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiVecStoreApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {collectionName: keyword.value || undefined}
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

function toggle(item: AiVecStore) {
  const key = String(item.id || '')
  if (!key) return
  if (selectedKeys.value.has(key)) {
    emit('remove', key)
  } else {
    emit('add', key, item.collectionName)
  }
}

function onPageChange(p: number) {
  page.pageNum = p
  void fetchList()
}

watch(drawerOpen, (open) => {
  if (open) {
    keyword.value = ''
    page.pageNum = 1
    void fetchList()
  }
})
</script>

<style scoped>
.rag-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.rag-card:hover {
  border-color: var(--ab-hover-line, #3b82f6);
  box-shadow: var(--ab-hover-shadow, 0 0 15px rgba(59, 130, 246, 0.15));
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: #d1fae5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #059669;
}

.icon-badge .anticon {
  font-size: 16px;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: #334155;
  margin: 0;
}

.action-btn {
  width: 28px;
  height: 28px;
  background: transparent;
  color: #94a3b8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #2563eb;
}

.action-btn.primary {
  background: #10b981;
  color: #fff;
  box-shadow: 0 1px 3px rgba(16, 185, 129, 0.35);
}

.action-btn.primary:hover {
  color: #fff;
  background: #059669;
}

.action-btn .anticon {
  font-size: 12px;
}

.kb-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.kb-tag {
  padding: 4px 8px;
  background: #ecfdf5;
  color: #059669;
  border: 1px solid #a7f3d0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
}

.kb-name {
  font-size: 11px;
}

.kb-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.kb-close:hover {
  color: #ef4444;
}

.empty-hint {
  font-size: 12px;
  color: #94a3b8;
  padding: 4px 0;
}

.selector-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 12px;
}

.search-bar {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.item-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.15s;
}

.item-row:hover {
  border-color: #059669;
  background: #ecfdf5;
}

.item-row.selected {
  border-color: #059669;
  background: #d1fae5;
}

.item-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #d1fae5;
  color: #059669;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.item-sub {
  font-size: 11px;
  color: #94a3b8;
}

.check-icon {
  color: #059669;
  font-size: 16px;
}

.pager {
  text-align: center;
  flex-shrink: 0;
}

.empty-list {
  text-align: center;
  color: #94a3b8;
  padding: 24px;
}
</style>
