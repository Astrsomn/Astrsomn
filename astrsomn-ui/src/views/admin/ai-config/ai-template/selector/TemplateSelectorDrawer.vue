<template>
  <AstDrawer
    :open="open"
    :width="520"
    root-class-name="template-selector-drawer"
    @update:open="emit('update:open', $event)"
  >
    <template #icon>
      <FileTextOutlined />
    </template>
    <template #title>{{ t.selector.title }}</template>
    <template #subtitle>{{ t.selector.subtitle }}</template>

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
        <!-- 孤儿条目（资源已删除） -->
          <div v-if="orphanedKeys && orphanedKeys.length" class="orphaned-section">
            <div class="orphaned-header">
              <ExclamationCircleOutlined />
              <span>{{ orphanedHint || 'Deleted resources' }}</span>
            </div>
            <div v-for="key in orphanedKeys" :key="key" class="orphaned-item">
              <ExclamationCircleOutlined class="orphaned-item-icon" />
              <code class="orphaned-item-key">{{ key }}</code>
              <CloseOutlined class="orphaned-item-close" @click.stop="emit('remove', key)" />
            </div>
          </div>

          <div class="item-list">
          <div
            v-for="item in list"
            :key="item.id"
            :class="['item-card', { selected: activeSet.has(item.templateKey || '') }]"
            @click="toggle(item)"
          >
            <div class="item-icon">
              <FileTextOutlined />
            </div>

            <div class="item-body">
              <div class="item-head">
                <span class="item-name">{{ item.templateTitle || item.templateKey }}</span>
                <span :class="['status-dot', item.status === 'enabled' ? 'on' : 'off']" />
                <span class="status-label">{{ item.status === 'enabled' ? t.selector.statusEnabled : t.selector.statusDisabled }}</span>
              </div>
              <code class="item-key">{{ item.templateKey }}</code>
              <div v-if="item.category || item.templateType" class="item-meta">
                <span v-if="item.category" class="meta-chip category">{{ item.category }}</span>
                <span class="meta-chip" :class="typeClass(item.templateType)">
                  {{ item.templateType === 'FREEMARKER' ? 'Freemarker' : item.templateType === 'STRING_TEMPLATE' ? 'StringTemplate' : item.templateType || '—' }}
                </span>
              </div>
            </div>

            <div class="item-right">
              <CheckCircleFilled v-if="activeSet.has(item.templateKey || '')" class="check-on" />
              <div v-else class="check-off" />
            </div>
          </div>

          <div v-if="!list.length && !loading" class="empty-state">
            <div class="empty-icon"><FileTextOutlined /></div>
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
import {CheckCircleFilled, CloseOutlined, ExclamationCircleOutlined, FileTextOutlined} from '@ant-design/icons-vue'
import {type AiTemplate, aiTemplateApi} from '@/api/aiTemplate.ts'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import AstPagination from '@/components/home/AstPagination.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-template')

const props = defineProps<{
  open: boolean
  selectedKeys?: string[]
  orphanedKeys?: string[]
  orphanedHint?: string
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'add', key: string): void
  (e: 'remove', key: string): void
}>()

const keyword = ref('')
const loading = ref(false)
const list = ref<AiTemplate[]>([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const activeSet = computed(() => new Set(props.selectedKeys ?? []))

function typeClass(t?: string) {
  const map: Record<string, string> = { FREEMARKER: 't-freemarker', STRING_TEMPLATE: 't-string' }
  return map[t || ''] || 't-unknown'
}

async function fetchList() {
  loading.value = true
  try {
    const resp = await aiTemplateApi.queryPage({
      pageNo: page.pageNum,
      pageSize: page.pageSize,
      param: {
        templateTitle: keyword.value || undefined,
        templateKey: keyword.value || undefined,
      },
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

function toggle(item: AiTemplate) {
  if (!item.templateKey) return
  if (activeSet.value.has(item.templateKey)) {
    emit('remove', item.templateKey)
  } else {
    emit('add', item.templateKey)
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
:deep(.template-selector-drawer .header-icon) {
  background: linear-gradient(135deg, #f97316, #ea580c);
  box-shadow: 0 4px 12px color-mix(in srgb, #f97316 30%, transparent);
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
  gap: 8px;
}

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
  border-color: #f97316;
  background: color-mix(in srgb, #f97316 4%, transparent);
  box-shadow: 0 2px 8px color-mix(in srgb, #f97316 10%, transparent);
}

.item-card.selected {
  border-color: #f97316;
  background: color-mix(in srgb, #f97316 6%, transparent);
  box-shadow: 0 0 0 3px color-mix(in srgb, #f97316 15%, transparent);
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: color-mix(in srgb, #f97316 8%, transparent);
  color: #f97316;
}

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

.status-dot.on  { background: #10b981; box-shadow: 0 0 4px color-mix(in srgb, #10b981 30%, transparent); }
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

.item-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
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

.meta-chip.t-freemarker {
  background: color-mix(in srgb, #f97316 8%, transparent);
  color: #ea580c;
}

.meta-chip.t-string {
  background: color-mix(in srgb, #8b5cf6 8%, transparent);
  color: #7c3aed;
}

.meta-chip.t-unknown {
  background: var(--bg-surface);
  color: var(--text-muted);
}

.meta-chip.category {
  background: var(--bg-elevated);
  color: var(--text-muted);
  font-family: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
}

.item-right {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  padding-top: 2px;
}

.check-on {
  color: #f97316;
  font-size: 20px;
}

.check-off {
  width: 20px;
  height: 20px;
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
  background: color-mix(in srgb, #f97316 8%, transparent);
  color: #f97316;
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

/* ── 孤儿条目（资源已删除） ── */
.orphaned-section {
  margin-bottom: 12px;
  padding: 10px 12px;
  background: color-mix(in srgb, var(--error) 6%, transparent);
  border: 1px solid color-mix(in srgb, var(--error) 20%, transparent);
  border-radius: 8px;
}

.orphaned-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--error);
  font-weight: 500;
  margin-bottom: 8px;
}

.orphaned-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: color-mix(in srgb, var(--error) 10%, transparent);
  border: 1px solid color-mix(in srgb, var(--error) 25%, transparent);
  border-radius: 6px;
  margin-bottom: 4px;
}

.orphaned-item:last-child {
  margin-bottom: 0;
}

.orphaned-item-icon {
  font-size: 12px;
  color: var(--error);
  flex-shrink: 0;
}

.orphaned-item-key {
  flex: 1;
  font-size: 12px;
  font-family: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
  color: var(--error);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.orphaned-item-close {
  font-size: 12px;
  color: var(--error);
  cursor: pointer;
  flex-shrink: 0;
  transition: opacity 0.2s;
}

.orphaned-item-close:hover {
  opacity: 0.6;
}
</style>
