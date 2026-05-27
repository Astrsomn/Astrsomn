<template>
  <AstDrawer
    :open="open"
    :width="520"
    root-class-name="tool-selector-drawer"
    @update:open="emit('update:open', $event)"
  >
    <template #icon>
      <ToolOutlined />
    </template>
    <template #title>选择工具</template>
    <template #subtitle>为智能体添加可调用的工具能力</template>

    <div class="select-drawer-content">
      <div class="search-bar">
        <AstSearchInput
          v-model="keyword"
          layout="fluid"
          placeholder="搜索工具名或 Tool Key…"
          style="flex: 1"
          @search="handleSearch"
        />
      </div>

      <a-spin :spinning="loading">
        <div class="item-list">
          <div
            v-for="item in list"
            :key="item.id"
            :class="['item-card', { selected: activeSet.has(item.toolKey || '') }]"
            @click="toggle(item)"
          >
            <div class="item-icon" :class="typeClass(item.type)">
              <ToolOutlined v-if="typeClass(item.type) === 't-method'" />
              <CodeOutlined v-else-if="typeClass(item.type) === 't-html'" />
              <ApiOutlined v-else />
            </div>

            <div class="item-body">
              <div class="item-head">
                <span class="item-name">{{ item.toolName || item.toolKey }}</span>
                <span :class="['status-dot', item.enableFlag === 'enabled' ? 'on' : 'off']" />
                <span class="status-label">{{ item.enableFlag === 'enabled' ? '启用' : '禁用' }}</span>
              </div>
              <code class="item-key">{{ item.toolKey }}</code>
              <div v-if="item.description" class="item-desc">{{ item.description }}</div>
              <div v-if="item.beanName" class="item-meta">
                <span class="meta-chip" :class="typeClass(item.type)">
                  {{ item.type || 'UNKNOWN' }}
                </span>
                <span class="meta-chip bean">
                  <CodeOutlined /> {{ item.beanName }}<template v-if="item.methodName">.{{ item.methodName }}</template>
                </span>
              </div>
            </div>

            <div class="item-right">
              <CheckCircleFilled v-if="activeSet.has(item.toolKey || '')" class="check-on" />
              <div v-else class="check-off" />
            </div>
          </div>

          <div v-if="!list.length && !loading" class="empty-state">
            <div class="empty-icon"><ToolOutlined /></div>
            <p class="empty-title">未找到匹配的工具</p>
            <p class="empty-hint">尝试调整搜索关键词，或确认工具已注册</p>
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
import {ApiOutlined, CheckCircleFilled, CodeOutlined, ToolOutlined} from '@ant-design/icons-vue'
import {type AiTool, aiToolApi} from '@/api/aiTool.ts'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'

const props = defineProps<{
  open: boolean
  selectedKeys?: string[]
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'add', tool: AiTool): void
  (e: 'remove', toolKey: string): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiTool[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const activeSet = computed(() => new Set(props.selectedKeys ?? []))

function typeClass(t?: string) {
  const map: Record<string, string> = { method: 't-method', html: 't-html' }
  return map[t || ''] || 't-unknown'
}

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiToolApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { toolName: keyword.value || undefined, toolKey: keyword.value || undefined },
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

function toggle(item: AiTool) {
  if (!item.toolKey) return
  if (activeSet.value.has(item.toolKey)) {
    emit('remove', item.toolKey)
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
:deep(.tool-selector-drawer .header-icon) {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
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

/* ── list ── */

.item-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* ── card ── */

.item-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s;
}

.item-card:hover {
  border-color: var(--primary);
  background: var(--primary-hover);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.item-card.selected {
  border-color: var(--primary);
  background: var(--primary-hover);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

/* ── icon ── */

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.item-icon.t-method {
  background: rgba(59, 130, 246, 0.08);
  color: #3b82f6;
}

.item-icon.t-html {
  background: rgba(245, 158, 11, 0.08);
  color: #f59e0b;
}

.item-icon.t-unknown {
  background: rgba(107, 114, 128, 0.08);
  color: #6b7280;
}

/* ── body ── */

.item-body {
  flex: 1;
  min-width: 0;
}

.item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 3px;
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot.on  { background: #10b981; box-shadow: 0 0 4px rgba(16, 185, 129, 0.3); }
.status-dot.off { background: #9ca3af; }

.status-label {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 500;
}

.item-key {
  display: block;
  font-size: 11px;
  font-family: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
}

/* ── meta ── */

.item-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-chip {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 7px;
  border-radius: 5px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.meta-chip.t-method {
  background: rgba(59, 130, 246, 0.08);
  color: #3b82f6;
}

.meta-chip.t-html {
  background: rgba(245, 158, 11, 0.08);
  color: #d97706;
}

.meta-chip.t-unknown {
  background: var(--bg-surface);
  color: var(--text-muted);
}

.meta-chip.bean {
  font-family: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
  background: var(--bg-elevated);
  color: var(--text-muted);
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ── check ── */

.item-right {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  padding-top: 2px;
}

.check-on {
  color: var(--primary);
  font-size: 20px;
}

.check-off {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--border-default);
}

/* ── empty ── */

.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  border-radius: 12px;
  background: var(--primary-hover);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.empty-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.empty-hint {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}
</style>
