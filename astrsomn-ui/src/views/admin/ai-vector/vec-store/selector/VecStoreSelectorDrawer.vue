<template>
  <AstDrawer
    :open="open"
    :width="560"
    root-class-name="vecstore-selector-drawer"
    @update:open="emit('update:open', $event)"
  >
    <template #icon>
      <DatabaseOutlined />
    </template>
    <template #title>选择向量存储（知识库）</template>
    <template #subtitle>vecstore.selector</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <AstSearchInput
          v-model="keyword"
          layout="fluid"
          placeholder="搜索集合名称…"
          style="flex: 1"
          @search="handleSearch"
        />
      </div>

      <a-spin :spinning="loading">
        <div class="item-list">
          <div
            v-for="item in list"
            :key="item.id"
            :class="['item-card', { selected: activeSet.has(String(item.id || '')) }]"
            @click="toggle(item)"
          >
            <div class="item-left">
              <div class="item-dot"></div>
              <div class="item-body">
                <div class="item-head">
                  <span class="item-name">{{ item.collectionName }}</span>
                  <span class="item-dim-badge">{{ item.dimension }}d</span>
                  <span class="item-metric-badge">{{ item.distanceMetric }}</span>
                </div>
                <div class="item-source-row">
                  <span class="item-source">{{ item.sourceProvider || '未知来源' }}</span>
                  <template v-if="item.sourceName"> · <span class="item-source">{{ item.sourceName }}</span></template>
                  <template v-if="item.instanceName"> · <code class="item-instance">{{ item.instanceName }}</code></template>
                </div>
                <div v-if="item.chunkStrategy" class="item-meta">
                  <span class="meta-chip">
                    <BlockOutlined /> {{ item.chunkStrategy }}
                    <template v-if="item.chunkSize">· {{ item.chunkSize }}</template>
                    <template v-if="item.chunkOverlap">+{{ item.chunkOverlap }}</template>
                  </span>
                  <span v-if="item.modelKey" class="meta-chip" title="嵌入模型">
                    <RobotOutlined /> {{ item.modelKey }}
                  </span>
                </div>
              </div>
            </div>
            <div class="item-right">
              <CheckCircleFilled v-if="activeSet.has(String(item.id || ''))" class="check-on" />
              <div v-else class="check-off"></div>
            </div>
          </div>
          <div v-if="!list.length && !loading" class="empty-state">
            <div class="empty-icon"><DatabaseOutlined /></div>
            <p class="empty-title">未找到匹配的向量存储</p>
            <p class="empty-hint">尝试调整搜索关键词，或确认集合已创建</p>
          </div>
        </div>
      </a-spin>
    </div>

    <template #footer>
      <AstPagination
        :current="page.pageNum"
        :page-size="page.pageSize"
        :show-size-changer="false"
        :total="page.total"
        @change="onPageChange"
      />
    </template>
  </AstDrawer>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {BlockOutlined, CheckCircleFilled, DatabaseOutlined, RobotOutlined} from '@ant-design/icons-vue'
import {type AiVecStore, aiVecStoreApi} from '@/api/aiVecStore.ts'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'

const props = defineProps<{
  open: boolean
  selectedKeys?: string[]
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'add', kbKey: string, title?: string): void
  (e: 'remove', kbKey: string): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiVecStore[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const activeSet = computed(() => new Set(props.selectedKeys ?? []))

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiVecStoreApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { collectionName: keyword.value || undefined },
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.pageNum = 1
  void fetchList()
}

function toggle(item: AiVecStore) {
  const key = String(item.id || '')
  if (!key) return
  if (activeSet.value.has(key)) {
    emit('remove', key)
  } else {
    emit('add', key, item.collectionName)
  }
}

function onPageChange(p: number, size: number) {
  page.pageNum = p
  page.pageSize = size
  void fetchList()
}

watch(
  () => props.open,
  (open) => {
    if (open) {
      keyword.value = ''
      page.pageNum = 1
      void fetchList()
    }
  },
)
</script>

<style scoped>

:deep(.vecstore-selector-drawer .header-icon) {
  background: var(--primary-gradient);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}


:deep(.drawer-footer) {
  justify-content: center;
}


.select-drawer-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
}

.search-bar {
  display: flex;
  flex-shrink: 0;
}


.item-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}


.item-list::-webkit-scrollbar {
  width: 4px;
}

.item-list::-webkit-scrollbar-thumb {
  background: var(--border-input);
  border-radius: 2px;
}


.item-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.12s ease;
}

.item-card:hover {
  border-color: var(--success);
  background: rgba(16, 185, 129, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.item-card.selected {
  border-color: var(--success);
  background: rgba(16, 185, 129, 0.08);
}

.item-left {
  display: flex;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.item-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 5px;
  background: var(--success);
}

.item-body {
  min-width: 0;
  flex: 1;
}

.item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 2px;
}

.item-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-heading);
  line-height: 1.4;
}

.item-dim-badge {
  font-size: 10px;
  font-weight: 600;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  padding: 1px 6px;
  border-radius: 3px;
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
  flex-shrink: 0;
}

.item-metric-badge {
  font-size: 10px;
  font-weight: 500;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  padding: 1px 5px;
  border-radius: 3px;
  background: var(--bg-input);
  color: var(--text-secondary);
  flex-shrink: 0;
}

.item-source-row {
  font-size: 11px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.item-source {
  color: var(--text-secondary);
}

.item-instance {
  font-size: 10px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  color: var(--text-secondary);
  background: var(--bg-input);
  padding: 1px 5px;
  border-radius: 3px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  flex-wrap: wrap;
}

.meta-chip {
  font-size: 10px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  color: var(--text-secondary);
  background: var(--bg-input);
  padding: 2px 7px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}


.item-right {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  padding-top: 2px;
}

.check-on {
  color: var(--success);
  font-size: 18px;
}

.check-off {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid var(--border-input);
}


.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  border-radius: 12px;
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.empty-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  margin: 0 0 4px;
}

.empty-hint {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}
</style>
