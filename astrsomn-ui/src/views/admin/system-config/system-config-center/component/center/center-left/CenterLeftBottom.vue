<template>
  <div class="biz-list-card">
    <div class="biz-header-row">
      <h2 class="biz-title">业务系统列表</h2>
      <div class="biz-actions">
        <button type="button" class="ghost-btn" @click="emit('export')">
          <DownloadOutlined />
          <span>导出</span>
        </button>
        <button type="button" class="ghost-btn icon-only" aria-label="刷新" @click="emit('refresh')">
          <ReloadOutlined />
        </button>
      </div>
    </div>

    <div class="biz-filter-row">
      <div class="filter-pills" role="tablist">
        <button
          v-for="tab in filterTabs"
          :key="tab.key"
          type="button"
          role="tab"
          :class="['pill', { 'pill--active': statusFilter === tab.key }]"
          @click="emit('update:statusFilter', tab.key)"
        >
          {{ tab.label }} {{ tab.count }}
        </button>
      </div>
    </div>

    <div class="table-scroll">
      <table class="biz-table">
        <thead>
          <tr>
            <th class="col-name">系统名称</th>
            <th class="col-slug">系统标识</th>
            <th class="col-owner">负责人</th>
            <th class="col-env">环境</th>
            <th class="col-status">状态</th>
            <th class="col-calls">今日调用量</th>
            <th class="col-err">错误率</th>
            <th class="col-last">最后访问</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="!loading && pagedOnlineSystems.length === 0">
            <td colspan="9" class="biz-empty">暂无业务系统</td>
          </tr>
          <template v-else-if="!loading">
            <OnlineSystemItem
              v-for="system in pagedOnlineSystems"
              :key="system.id"
              :system="system"
            />
          </template>
        </tbody>
      </table>
    </div>

    <div class="biz-footer">
      <span class="footer-total">共 {{ filteredTotal }} 条</span>
      <a-select
        class="page-size-select"
        :value="pageSize"
        size="small"
        :options="pageSizeOptions"
        @update:value="onPageSizeChange"
      />
      <a-pagination
        class="biz-pagination"
        size="small"
        :current="currentPage"
        :total="filteredTotal"
        :page-size="pageSize"
        :show-size-changer="false"
        :show-quick-jumper="false"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { DownloadOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'
import OnlineSystemItem from './OnlineSystemItem.vue'
import type { Component } from 'vue'

type SystemStatus = 'online' | 'offline' | 'maintenance'
type SystemEnv = 'prod' | 'pre' | 'test' | 'dev'
type IconTheme = 'blue' | 'green' | 'cyan' | 'amber'
type OwnerDot = 'blue' | 'green' | 'violet' | 'orange'

type OnlineSystem = {
  id: string
  name: string
  slug: string
  ownerName: string
  ownerDot: OwnerDot
  env: SystemEnv
  status: SystemStatus
  todayCalls: number
  errorRate: number
  lastAccess: string
  icon: Component
  iconTheme: IconTheme
}

type StatusCounts = {
  all: number
  online: number
  offline: number
  maintenance: number
}

const props = withDefaults(
  defineProps<{
    loading?: boolean
    pagedOnlineSystems: OnlineSystem[]
    currentPage: number
    filteredTotal: number
    statusFilter: 'all' | 'online' | 'offline' | 'maintenance'
    statusCounts: StatusCounts
    pageSize: number
  }>(),
  { loading: false }
)

const emit = defineEmits<{
  (e: 'update:statusFilter', value: 'all' | 'online' | 'offline' | 'maintenance'): void
  (e: 'update:page', value: number): void
  (e: 'update:pageSize', value: number): void
  (e: 'export'): void
  (e: 'refresh'): void
}>()

const filterTabs = computed(() => [
  { key: 'all' as const, label: '全部', count: props.statusCounts.all },
  { key: 'online' as const, label: '在线', count: props.statusCounts.online },
  { key: 'offline' as const, label: '离线', count: props.statusCounts.offline },
  { key: 'maintenance' as const, label: '维护中', count: props.statusCounts.maintenance }
])

const pageSizeOptions = [
  { value: 10, label: '10 条/页' },
  { value: 20, label: '20 条/页' },
  { value: 50, label: '50 条/页' }
]

const onPageChange = (page: number) => {
  emit('update:page', page)
}

const onPageSizeChange = (value: number) => {
  emit('update:pageSize', value)
}
</script>

<style scoped>
.biz-list-card {
  display: flex;
  flex-direction: column;
  margin-top: 6px;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-default, #e2e8f0);
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}

.biz-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px 8px;
  flex-shrink: 0;
}

.biz-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.biz-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ghost-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  font-size: 13px;
  color: #475569;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}

.ghost-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.ghost-btn.icon-only {
  padding: 6px 10px;
}

.ghost-btn :deep(.anticon) {
  font-size: 14px;
  color: #64748b;
}

.biz-filter-row {
  padding: 0 16px 10px;
  flex-shrink: 0;
}

.filter-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pill {
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.pill:hover {
  background: #e2e8f0;
}

.pill--active {
  background: #3b82f6;
  color: #fff;
}

.pill--active:hover {
  background: #2563eb;
  color: #fff;
}

.table-scroll {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 0 16px;
}

.biz-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.biz-table thead th {
  text-align: left;
  font-weight: 600;
  color: #64748b;
  padding: 8px 8px;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.col-name {
  min-width: 160px;
}

.col-slug {
  min-width: 120px;
}

.col-owner {
  min-width: 88px;
}

.col-env {
  min-width: 100px;
}

.col-status {
  min-width: 88px;
}

.col-calls {
  min-width: 100px;
}

.col-err {
  min-width: 72px;
}

.col-last {
  min-width: 88px;
}

.col-actions {
  width: 104px;
  text-align: right;
}

.biz-empty {
  padding: 28px 16px;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}

.biz-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
  padding: 10px 16px;
  border-top: 1px solid #f1f5f9;
}

.footer-total {
  font-size: 13px;
  color: #64748b;
}

.page-size-select {
  width: 108px;
}

.biz-pagination :deep(.ant-pagination-item-active) {
  border-color: #3b82f6;
}

.biz-pagination :deep(.ant-pagination-item-active a) {
  color: #3b82f6;
}
</style>
