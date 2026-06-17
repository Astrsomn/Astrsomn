<template>
  <div class="biz-table-shell">
    <!-- Header Row -->
    <div class="biz-header">
      <h2 class="biz-title">{{ t.dashboard.systemsTitle }}</h2>
      <div class="biz-header-actions">
        <button class="ghost-btn" type="button" @click="emit('export')">
          <DownloadOutlined />
          <span>{{ t.dashboard.export }}</span>
        </button>
        <button class="ghost-btn icon-only" type="button" aria-label="refresh" @click="emit('refresh')">
          <ReloadOutlined />
        </button>
      </div>
    </div>

    <!-- Filter Pills -->
    <div class="biz-filters">
      <button
        v-for="tab in filterTabs"
        :key="tab.key"
        :class="['pill', { 'pill--active': statusFilter === tab.key }]"
        type="button"
        @click="emit('update:statusFilter', tab.key)"
      >
        {{ tab.label }}
        <span class="pill-count">{{ tab.count.toLocaleString() }}</span>
      </button>
    </div>

    <!-- Data Table via AstDataView -->
    <div class="biz-table-wrap">
      <AstDataView
        :columns="columns"
        :data-source="pagedOnlineSystems"
        :empty-text="t.table.empty"
        :loading="loading"
        :row-key="(r: OnlineSystem) => r.id"
        :scroll="{ x: 980 }"
        bordered
        mode="table"
      >
        <template #bodyCell="{ column, record }">
          <!-- System Name -->
          <template v-if="column.key === 'name'">
            <div class="name-cell">
              <div :class="`icon-swatch icon-swatch--${record.iconTheme}`">
                <component :is="record.icon" class="icon-swatch__ico" />
              </div>
              <span class="name-text">{{ record.name }}</span>
            </div>
          </template>

          <!-- Slug -->
          <template v-else-if="column.key === 'slug'">
            <span class="slug-text">{{ record.slug }}</span>
          </template>

          <!-- Owner -->
          <template v-else-if="column.key === 'owner'">
            <span class="owner-line">
              <span :class="`owner-dot owner-dot--${record.ownerDot}`" aria-hidden="true" />
              {{ record.ownerName }}
            </span>
          </template>

          <!-- Env -->
          <template v-else-if="column.key === 'env'">
            <span :class="`env-tag env-tag--${record.env}`">{{ envLabel(record.env) }}</span>
          </template>

          <!-- Status -->
          <template v-else-if="column.key === 'status'">
            <span :class="`status-line status-line--${record.status}`">
              <span aria-hidden="true" class="status-dot" />
              {{ statusLabel(record.status) }}
            </span>
          </template>

          <!-- Today Calls -->
          <template v-else-if="column.key === 'todayCalls'">
            <span class="calls-text">{{ record.todayCalls.toLocaleString() }}</span>
          </template>

          <!-- Error Rate -->
          <template v-else-if="column.key === 'errorRate'">
            <span :class="['err-text', record.errorRate >= 0.5 ? 'err-text--warn' : 'err-text--ok']">
              {{ record.errorRate.toFixed(2) }}%
            </span>
          </template>

          <!-- Last Access -->
          <template v-else-if="column.key === 'lastAccess'">
            <span class="last-text">{{ record.lastAccess }}</span>
          </template>

          <!-- Actions -->
          <template v-else-if="column.key === 'actions'">
            <div class="action-btns">
              <a-tooltip :title="t.table.monitor">
                <button class="icon-action" type="button" @click.stop>
                  <MonitorOutlined />
                </button>
              </a-tooltip>
              <a-tooltip :title="t.table.edit">
                <button class="icon-action" type="button" @click.stop>
                  <EditOutlined />
                </button>
              </a-tooltip>
              <a-tooltip :title="t.table.more">
                <button class="icon-action" type="button" @click.stop>
                  <EllipsisOutlined />
                </button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </AstDataView>
    </div>

    <!-- Footer: 仅上下页切换 -->
    <div class="biz-footer">
      <button
        :disabled="currentPage <= 1"
        class="page-btn"
        type="button"
        @click="emit('update:page', currentPage - 1)"
      >
        <LeftOutlined />
        <span>上一页</span>
      </button>
      <button
        :disabled="currentPage >= totalPages"
        class="page-btn"
        type="button"
        @click="emit('update:page', currentPage + 1)"
      >
        <span>下一页</span>
        <RightOutlined />
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  DownloadOutlined,
  EditOutlined,
  EllipsisOutlined,
  LeftOutlined,
  MonitorOutlined,
  ReloadOutlined,
  RightOutlined,
} from '@ant-design/icons-vue'
import {computed} from 'vue'
import AstDataView from '@/components/home/AstDataView.vue'
import {usePageTranslation} from '@/locales/pages'
import {useDictionary} from '@/locales/dictionary/registry'
import type {OnlineSystem, SystemEnvUi, SystemStatusUi} from '../useSystemConfigCenter'

const props = withDefaults(
  defineProps<{
    loading?: boolean
    pagedOnlineSystems: OnlineSystem[]
    currentPage: number
    filteredTotal: number
    pageSize: number
    statusFilter: 'all' | 'online' | 'offline' | 'maintenance'
    statusCounts: { all: number; online: number; offline: number; maintenance: number }
  }>(),
  { loading: false }
)

const emit = defineEmits<{
  (e: 'update:statusFilter', value: 'all' | 'online' | 'offline' | 'maintenance'): void
  (e: 'update:page', value: number): void
  (e: 'export'): void
  (e: 'refresh'): void
}>()

const totalPages = computed(() => Math.max(1, Math.ceil(props.filteredTotal / props.pageSize)))

const t = usePageTranslation('system-config-center')
const envDict = useDictionary('system.config-center.env')
const statusDict = useDictionary('system.config-center.status')

const envLabel = (env: SystemEnvUi): string => {
  const code = env === 'prod' ? 'PROD' : env === 'pre' ? 'PRE' : env === 'test' ? 'TEST' : 'DEV'
  return envDict.value.getLabel(code) ?? code
}

const statusLabel = (status: SystemStatusUi): string => {
  const code = status === 'online' ? 'ONLINE' : status === 'offline' ? 'OFFLINE' : 'MAINTENANCE'
  return statusDict.value.getLabel(code) ?? code
}

const filterTabs = computed(() => [
  { key: 'all' as const, label: t.value.filter.all, count: props.statusCounts.all },
  { key: 'online' as const, label: t.value.filter.online, count: props.statusCounts.online },
  { key: 'offline' as const, label: t.value.filter.offline, count: props.statusCounts.offline },
  { key: 'maintenance' as const, label: t.value.filter.maintenance, count: props.statusCounts.maintenance },
])

const columns = [
  { title: '系统名称', dataIndex: 'name', key: 'name', width: 180 },
  { title: '系统标识', dataIndex: 'slug', key: 'slug', width: 130 },
  { title: '负责人', dataIndex: 'ownerName', key: 'owner', width: 100 },
  { title: '环境', dataIndex: 'env', key: 'env', width: 110 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 90 },
  { title: '今日调用量', dataIndex: 'todayCalls', key: 'todayCalls', width: 110 },
  { title: '错误率', dataIndex: 'errorRate', key: 'errorRate', width: 80 },
  { title: '最后访问', dataIndex: 'lastAccess', key: 'lastAccess', width: 100 },
  { title: '操作', key: 'actions', width: 120 },
]
</script>

<style scoped>
.biz-table-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

/* ---- Header ---- */
.biz-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px 0;
  flex-shrink: 0;
}

.biz-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--text-heading);
}

.biz-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ghost-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s, color 0.2s;
}

.ghost-btn:hover {
  background: var(--bg-input);
  border-color: var(--primary);
  color: var(--primary);
}

.ghost-btn.icon-only {
  padding: 5px 9px;
}

.ghost-btn :deep(.anticon) {
  font-size: 14px;
}

/* ---- Filters ---- */
.biz-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 16px;
  flex-shrink: 0;
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 14px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  background: var(--bg-input);
  border: 1px solid transparent;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pill:hover {
  background: var(--bg-elevated);
  color: var(--text-primary);
}

.pill--active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.pill--active:hover {
  background: var(--primary-light);
  color: #fff;
}

.pill-count {
  font-size: 11px;
  opacity: 0.8;
}

/* ---- Table Wrap ---- */
.biz-table-wrap {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 0 16px;
}

.biz-table-wrap :deep(.ant-table) {
  font-size: 13px;
}

.biz-table-wrap :deep(.ant-table-thead .ant-table-cell) {
  font-weight: 600;
  color: var(--text-muted);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-default);
  padding: 10px 8px;
}

.biz-table-wrap :deep(.ant-table-tbody .ant-table-cell) {
  padding: 10px 8px;
  border-bottom: 1px solid var(--border-subtle);
}

/* ---- Cell Styles ---- */
.name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-swatch {
  flex-shrink: 0;
  width: 34px;
  height: 34px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-swatch--blue { background: rgba(59,130,246,0.15); color: var(--primary); }
.icon-swatch--green { background: rgba(16,185,129,0.15); color: var(--success); }
.icon-swatch--cyan { background: rgba(56,189,248,0.15); color: #38bdf8; }
.icon-swatch--amber { background: rgba(245,158,11,0.15); color: #fb923c; }

.icon-swatch__ico { font-size: 15px; }

.name-text {
  font-weight: 600;
  color: var(--text-heading);
}

.slug-text {
  color: var(--text-muted);
  font-size: 12px;
}

.owner-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.owner-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.owner-dot--blue { background: var(--primary); }
.owner-dot--green { background: var(--success); }
.owner-dot--violet { background: #a78bfa; }
.owner-dot--orange { background: #fb923c; }

.env-tag {
  display: inline-block;
  padding: 2px 10px;
  font-size: 11px;
  font-weight: 500;
  border-radius: 10px;
}

.env-tag--prod { background: rgba(16,185,129,0.12); color: var(--success); }
.env-tag--pre { background: rgba(59,130,246,0.12); color: var(--primary); }
.env-tag--test { background: rgba(139,92,246,0.12); color: #a78bfa; }
.env-tag--dev { background: rgba(251,146,60,0.12); color: #fb923c; }

.status-line {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-line--online { color: var(--success); }
.status-line--online .status-dot { background: var(--success); }
.status-line--offline { color: var(--text-muted); }
.status-line--offline .status-dot { background: var(--text-muted); }
.status-line--maintenance { color: #fb923c; }
.status-line--maintenance .status-dot { background: #fb923c; }

.calls-text {
  font-variant-numeric: tabular-nums;
}

.err-text {
  font-variant-numeric: tabular-nums;
}

.err-text--ok { color: var(--success); }
.err-text--warn { color: #fb923c; }

.last-text {
  color: var(--text-muted);
  font-size: 12px;
}

.action-btns {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.icon-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  padding: 0;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}

.icon-action:hover {
  background: var(--bg-input);
  color: var(--text-primary);
}

.icon-action :deep(.anticon) {
  font-size: 14px;
}

/* ---- Footer: 仅上下页 ---- */
.biz-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-shrink: 0;
  padding: 10px 16px;
  border-top: 1px solid var(--border-default);
}

.page-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s, color 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-input);
  border-color: var(--primary);
  color: var(--primary);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
