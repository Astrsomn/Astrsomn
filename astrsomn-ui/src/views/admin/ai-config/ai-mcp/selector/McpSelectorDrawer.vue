<template>
  <AstDrawer
    :open="open"
    :width="520"
    root-class-name="mcp-selector-drawer"
    @update:open="emit('update:open', $event)"
  >
    <template #icon>
      <ApiOutlined />
    </template>
    <template #title>{{ t.selector.title }}</template>
    <template #subtitle>mcp.selector</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <AstSearchInput
          v-model="keyword"
          layout="fluid"
          :placeholder="t.selector.searchPlaceholder"
          style="flex: 1"
          @search="handleSearch"
        />
      </div>

      <a-spin :spinning="loading">
        <div class="item-list">
          <div
            v-for="item in list"
            :key="item.id"
            :class="['item-card', { selected: activeSet.has(item.mcpKey || '') }]"
            @click="toggle(item)"
          >
            <div class="item-left">
              <div class="item-dot" :class="typeClass(item.type)"></div>
              <div class="item-body">
                <div class="item-head">
                  <span class="item-name">{{ item.serverName || item.mcpKey }}</span>
                  <span class="item-type-tag" :class="typeClass(item.type)">{{ item.type || 'UNKNOWN' }}</span>
                </div>
                <code class="item-key">{{ item.mcpKey }}</code>
                <div v-if="item.description" class="item-desc">{{ item.description }}</div>
                <div class="item-meta">
                  <span v-if="item.command" class="meta-chip" :title="t.selector.startCommand">
                    <CodeOutlined /> {{ item.command }}
                  </span>
                  <span :class="['status-dot', item.enabled === 1 ? 'on' : 'off']"></span>
                  <span class="status-label">{{ item.enabled === 1 ? t.selector.statusLabel.enabled : t.selector.statusLabel.disabled }}</span>
                </div>
              </div>
            </div>
            <div class="item-right">
              <CheckCircleFilled v-if="activeSet.has(item.mcpKey || '')" class="check-on" />
              <div v-else class="check-off"></div>
            </div>
          </div>
          <div v-if="!list.length && !loading" class="empty-state">
            <div class="empty-icon"><ApiOutlined /></div>
            <p class="empty-title">{{ t.selector.emptyTitle }}</p>
            <p class="empty-hint">{{ t.selector.emptyHint }}</p>
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
import {ApiOutlined, CheckCircleFilled, CodeOutlined} from '@ant-design/icons-vue'
import {type AiMcp, aiMcpApi} from '@/api/aiMcp.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'

const t = usePageTranslation('ai-mcp')

const props = defineProps<{
  open: boolean
  selectedKeys?: string[]
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'add', mcp: AiMcp): void
  (e: 'remove', mcpKey: string): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiMcp[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const activeSet = computed(() => new Set(props.selectedKeys ?? []))

function typeClass(t?: string) {
  const map: Record<string, string> = { SSE: 't-sse', STEAMABLE: 't-stream', STDIO: 't-stdio' }
  return map[t || ''] || 't-unknown'
}

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiMcpApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { serverName: keyword.value || undefined, mcpKey: keyword.value || undefined },
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

function toggle(item: AiMcp) {
  if (!item.mcpKey) return
  if (activeSet.value.has(item.mcpKey)) {
    emit('remove', item.mcpKey)
  } else {
    emit('add', item)
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

:deep(.mcp-selector-drawer .header-icon) {
  background: linear-gradient(135deg, #a855f7, #7c3aed);
  box-shadow: 0 4px 12px color-mix(in srgb, #7c3aed 30%, transparent);
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
  background: var(--border-default);
  border-radius: 2px;
}


.item-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.12s ease;
}

.item-card:hover {
  border-color: color-mix(in srgb, var(--primary) 40%, var(--border-default));
  background: var(--bg-elevated);
  box-shadow: 0 1px 3px color-mix(in srgb, var(--shadow-color, #000) 4%, transparent);
}

.item-card.selected {
  border-color: color-mix(in srgb, var(--primary) 60%, var(--border-default));
  background: var(--bg-elevated);
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
}

.item-dot.t-sse     { background: #10b981; }
.item-dot.t-stream  { background: #f59e0b; }
.item-dot.t-stdio   { background: #3b82f6; }
.item-dot.t-unknown { background: #9ca3af; }

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
  color: var(--text-primary);
  line-height: 1.4;
}

.item-type-tag {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.04em;
  padding: 1px 6px;
  border-radius: 3px;
  flex-shrink: 0;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
}

.item-type-tag.t-sse     { background: #ecfdf5; color: #059669; }
.item-type-tag.t-stream  { background: #fffbeb; color: #d97706; }
.item-type-tag.t-stdio   { background: #eff6ff; color: #2563eb; }
.item-type-tag.t-unknown { background: #f3f4f6; color: #6b7280; }

.item-key {
  display: block;
  font-size: 11px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', 'JetBrains Mono', monospace;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 4px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.meta-chip {
  font-size: 10px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
  color: var(--text-secondary);
  background: var(--bg-elevated);
  padding: 2px 7px;
  border-radius: 4px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot.on  { background: #10b981; }
.status-dot.off { background: #d1d5db; }

.status-label {
  font-size: 11px;
  color: var(--text-muted);
}


.item-right {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  padding-top: 2px;
}

.check-on {
  color: var(--primary);
  font-size: 18px;
}

.check-off {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid var(--border-default);
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
  background: var(--bg-elevated);
  color: var(--primary);
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
