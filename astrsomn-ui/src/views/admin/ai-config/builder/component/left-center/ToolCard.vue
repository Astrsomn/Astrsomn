<template>
  <div class="tool-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ToolOutlined />
        </div>
        <h3 class="card-title">扩展插件 (Tools)</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined />
      </button>
    </div>
    <div class="tool-list">
      <div v-for="t in tools" :key="t.toolKey" class="tool-tag">
        <span class="tool-letter">{{ (t.toolName || t.toolKey || 'T').charAt(0).toUpperCase() }}</span>
        <span class="tool-name">{{ t.toolName || t.toolKey }}</span>
        <CloseOutlined class="tool-close" @click.stop="emit('remove', t.toolKey!)" />
      </div>
      <div v-if="!tools.length" class="empty-hint">暂未绑定工具</div>
    </div>

    <a-drawer
      :open="drawerOpen"
      placement="right"
      :width="480"
      title="选择工具"
      @close="drawerOpen = false"
    >
      <div class="selector-content">
        <div class="search-bar">
          <a-input
            v-model:value="keyword"
            placeholder="工具名 / Tool Key"
            allow-clear
            @pressEnter="fetchList"
          >
            <template #prefix><SearchOutlined /></template>
          </a-input>
          <a-button type="primary" @click="fetchList">搜索</a-button>
        </div>
        <a-spin :spinning="loading">
          <div class="item-list">
            <div
              v-for="item in list"
              :key="item.id"
              class="item-row"
              :class="{ selected: selectedKeys.has(item.toolKey || '') }"
              @click="toggle(item)"
            >
              <div class="item-icon">
                <ToolOutlined />
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.toolName || item.toolKey }}</div>
                <div class="item-sub">{{ item.toolKey }}</div>
              </div>
              <CheckCircleOutlined v-if="selectedKeys.has(item.toolKey || '')" class="check-icon" />
            </div>
            <div v-if="!list.length && !loading" class="empty-list">暂无数据</div>
          </div>
        </a-spin>
        <a-pagination
          v-if="page.total > page.pageSize"
          class="pager"
          size="small"
          :current="page.pageNum"
          :total="page.total"
          :page-size="page.pageSize"
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ToolOutlined, PlusOutlined, CloseOutlined, SearchOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
import { aiToolApi, type AiTool } from '@/api/aiTool'

const props = defineProps<{
  tools: AiTool[]
}>()

const emit = defineEmits<{
  (e: 'add', tool: AiTool): void
  (e: 'remove', toolKey: string): void
}>()

const drawerOpen = ref(false)
const keyword = ref('')
const loading = ref(false)
const list = ref<AiTool[]>([])
const page = reactive({ pageNum: 1, pageSize: 20, total: 0 })

const selectedKeys = computed(() => new Set(props.tools.map((t) => t.toolKey).filter(Boolean) as string[]))

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiToolApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: { toolName: keyword.value || undefined, toolKey: keyword.value || undefined }
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

function toggle(item: AiTool) {
  if (!item.toolKey) return
  if (selectedKeys.value.has(item.toolKey)) {
    emit('remove', item.toolKey)
  } else {
    emit('add', item)
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
.tool-card {
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.tool-card:hover {
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
  background: #dbeafe;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
}

.icon-badge .anticon { font-size: 16px; }

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: #334155;
  margin: 0;
}

.add-btn {
  width: 28px;
  height: 28px;
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover { background: #dbeafe; color: #1d4ed8; }
.add-btn .anticon { font-size: 14px; }

.tool-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.tool-tag {
  padding: 4px 8px;
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 600;
}

.tool-letter { font-size: 10px; }
.tool-name { font-size: 11px; font-weight: bold; }

.tool-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}
.tool-close:hover { color: #ef4444; }

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
  border-color: var(--primary, #3b82f6);
  background: #f0f7ff;
}

.item-row.selected {
  border-color: var(--primary, #3b82f6);
  background: #eff6ff;
}

.item-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #dbeafe;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 13px; font-weight: 600; color: #1e293b; }
.item-sub { font-size: 11px; color: #94a3b8; font-family: ui-monospace, monospace; }

.check-icon { color: #2563eb; font-size: 16px; }

.pager { text-align: center; flex-shrink: 0; }

.empty-list {
  text-align: center;
  color: #94a3b8;
  padding: 24px;
}
</style>
