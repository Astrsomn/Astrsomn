<template>
  <div class="mcp-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ApiOutlined/>
        </div>
        <h3 class="card-title">MCP 服务</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="mcp-list">
      <div v-for="m in mcps" :key="m.mcpKey" class="mcp-tag">
        <span class="mcp-letter">{{ (m.serverName || m.mcpKey || 'M').charAt(0).toUpperCase() }}</span>
        <span class="mcp-name">{{ m.serverName || m.mcpKey }}</span>
        <CloseOutlined class="mcp-close" @click.stop="emit('remove', m.mcpKey!)"/>
      </div>
      <div v-if="!mcps.length" class="empty-hint">暂未绑定 MCP</div>
    </div>

    <a-drawer
        :open="drawerOpen"
        :width="480"
        placement="right"
        title="选择 MCP 服务"
        @close="drawerOpen = false"
    >
      <div class="selector-content">
        <div class="search-bar">
          <a-input
              v-model:value="keyword"
              allow-clear
              placeholder="服务名 / MCP Key"
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
                :class="{ selected: selectedKeys.has(item.mcpKey || '') }"
                class="item-row"
                @click="toggle(item)"
            >
              <div class="item-icon">
                <ApiOutlined/>
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.serverName || item.mcpKey }}</div>
                <div class="item-sub">{{ item.mcpKey }}</div>
              </div>
              <CheckCircleOutlined v-if="selectedKeys.has(item.mcpKey || '')" class="check-icon"/>
            </div>
            <div v-if="!list.length && !loading" class="empty-list">暂无数据</div>
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
import {ApiOutlined, CheckCircleOutlined, CloseOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons-vue'
import {type AiMcp, aiMcpApi} from '@/api/aiMcp'

const props = defineProps<{
  mcps: AiMcp[]
}>()

const emit = defineEmits<{
  (e: 'add', mcp: AiMcp): void
  (e: 'remove', mcpKey: string): void
}>()

const drawerOpen = ref(false)
const keyword = ref('')
const loading = ref(false)
const list = ref<AiMcp[]>([])
const page = reactive({pageNum: 1, pageSize: 20, total: 0})

const selectedKeys = computed(() => new Set(props.mcps.map((m) => m.mcpKey).filter(Boolean) as string[]))

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiMcpApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {serverName: keyword.value || undefined, mcpKey: keyword.value || undefined}
    })
    list.value = resp.list || []
    page.total = resp.total || 0
  } finally {
    loading.value = false
  }
}

function toggle(item: AiMcp) {
  if (!item.mcpKey) return
  if (selectedKeys.value.has(item.mcpKey)) {
    emit('remove', item.mcpKey)
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
.mcp-card {
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.mcp-card:hover {
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
  background: #f3e8ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9333ea;
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

.add-btn {
  width: 28px;
  height: 28px;
  background: #faf5ff;
  color: #9333ea;
  border: 1px solid #e9d5ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #f3e8ff;
  color: #7e22ce;
}

.add-btn .anticon {
  font-size: 14px;
}

.mcp-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.mcp-tag {
  padding: 4px 8px;
  background: #faf5ff;
  color: #9333ea;
  border: 1px solid #e9d5ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 600;
}

.mcp-letter {
  font-size: 10px;
}

.mcp-name {
  font-size: 11px;
  font-weight: bold;
}

.mcp-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.mcp-close:hover {
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
  border-color: #9333ea;
  background: #faf5ff;
}

.item-row.selected {
  border-color: #9333ea;
  background: #f3e8ff;
}

.item-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #f3e8ff;
  color: #9333ea;
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
  font-family: ui-monospace, monospace;
}

.check-icon {
  color: #9333ea;
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
