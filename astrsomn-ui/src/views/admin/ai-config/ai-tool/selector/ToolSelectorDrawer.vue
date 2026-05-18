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
    <template #subtitle>tool.selector</template>

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
            <div class="item-left">
              <div class="item-dot" :class="typeClass(item.type)"></div>
              <div class="item-body">
                <div class="item-head">
                  <span class="item-name">{{ item.toolName || item.toolKey }}</span>
                  <span class="item-type-tag" :class="typeClass(item.type)">{{ item.type || 'UNKNOWN' }}</span>
                  <span :class="['status-chip', item.enableFlag === 'enabled' ? 'on' : 'off']">
                    {{ item.enableFlag === 'enabled' ? 'ON' : 'OFF' }}
                  </span>
                </div>
                <code class="item-key">{{ item.toolKey }}</code>
                <div v-if="item.description" class="item-desc">{{ item.description }}</div>
                <div v-if="item.beanName" class="item-meta">
                  <span class="meta-chip" title="Bean">
                    <CodeOutlined /> {{ item.beanName }}<template v-if="item.methodName">.{{ item.methodName }}</template>
                  </span>
                </div>
              </div>
            </div>
            <div class="item-right">
              <CheckCircleFilled v-if="activeSet.has(item.toolKey || '')" class="check-on" />
              <div v-else class="check-off"></div>
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
import { computed, reactive, ref, watch } from 'vue'
import { CheckCircleFilled, CodeOutlined, ToolOutlined } from '@ant-design/icons-vue'
import { type AiTool, aiToolApi } from '@/api/aiTool.ts'
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


.item-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}


.item-list::-webkit-scrollbar {
  width: 4px;
}

.item-list::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 2px;
}


.item-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  background: #fff;
  border: 1px solid #f0f0f2;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.12s ease;
}

.item-card:hover {
  border-color: #93c5fd;
  background: #fafcff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.item-card.selected {
  border-color: #60a5fa;
  background: #eff6ff;
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

.item-dot.t-method   { background: #2563eb; }
.item-dot.t-html     { background: #f59e0b; }
.item-dot.t-unknown  { background: #9ca3af; }

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
  color: #111827;
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

.item-type-tag.t-method   { background: #eff6ff; color: #2563eb; }
.item-type-tag.t-html     { background: #fffbeb; color: #d97706; }
.item-type-tag.t-unknown  { background: #f3f4f6; color: #6b7280; }

.status-chip {
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 0.05em;
  padding: 1px 5px;
  border-radius: 3px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', monospace;
}

.status-chip.on  { background: #ecfdf5; color: #059669; }
.status-chip.off { background: #f3f4f6; color: #9ca3af; }

.item-key {
  display: block;
  font-size: 11px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', 'JetBrains Mono', monospace;
  color: #6b7280;
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: #9ca3af;
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
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 7px;
  border-radius: 4px;
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  color: #2563eb;
  font-size: 18px;
}

.check-off {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid #e5e7eb;
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
  background: #eff6ff;
  color: #60a5fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.empty-title {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  margin: 0 0 4px;
}

.empty-hint {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}
</style>
