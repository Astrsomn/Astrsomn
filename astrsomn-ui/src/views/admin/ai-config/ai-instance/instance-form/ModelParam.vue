<template>
  <section class="selection-pane">
    <div class="pane-card glass-card">
      <div class="pane-header">
        <div v-if="!isEdit" class="pane-toolbar-row">
          <div class="provider-field">
            <ExtensionSelector
                :allow-clear="true"
                :value="providerFilter"
                class="instance-provider-select"
                :placeholder="t.modelParam.providerPlaceholder"
                size="middle"
                @update:value="emit('provider-change', $event)"
            />
            <div class="provider-search-pill">
              <AstSearchInput
                  :model-value="searchDraft"
                  layout="fluid"
                  :placeholder="t.modelParam.searchPlaceholder"
                  @search="emit('search')"
                  @update:model-value="emit('update:searchDraft', $event)"
              />
            </div>
          </div>
        </div>
        <a-tabs v-if="!isEdit" :active-key="typeFilter" class="model-type-tabs"
                @update:activeKey="emit('update:typeFilter', $event)">
          <a-tab-pane key="all" :tab="t.modelParam.tab.all"/>
          <a-tab-pane key="chat" :tab="t.modelParam.tab.chat"/>
          <a-tab-pane key="embedding" :tab="t.modelParam.tab.embedding"/>
          <a-tab-pane key="image" :tab="t.modelParam.tab.image"/>
        </a-tabs>
        <div v-if="isEdit" class="edit-locked-hint">
          <LockOutlined/>
          {{ t.modelParam.editLockedHint }}
        </div>
      </div>

      <div class="model-list-body">
        <div class="model-card-scroll" @scroll="emit('panel-scroll', $event)">
          <a-spin :spinning="modelsLoading">
            <div class="model-card-list">
              <button
                  v-for="record in modelList"
                  :key="record.modelKey"
                  :class="{
                  'is-active': selectedKeys.includes(String(record.modelKey || '')),
                  'is-locked': isEdit && !selectedKeys.includes(String(record.modelKey || ''))
                }"
                  :disabled="isEdit"
                  class="model-select-card"
                  type="button"
                  @click="!isEdit && emit('select-model', record)"
              >
                <div class="model-select-radio"><span class="dot"/></div>

                <div class="endpoint-main">
                  <div :title="record.modelName" class="endpoint-name">{{ record.modelName || t.modelParam.unnamed }}</div>
                  <div class="endpoint-meta">
                    <div :class="record.modelType" class="inst-model-type-icon">
                      <MessageOutlined v-if="record.modelType === 'chat'"/>
                      <PartitionOutlined v-else-if="record.modelType === 'embedding'"/>
                      <PictureOutlined v-else-if="record.modelType === 'image'"/>
                      <MessageOutlined v-else/>
                    </div>
                    <span class="inst-model-type-label">{{ modelTypeLabel(record.modelType) }}</span>
                  </div>
                </div>
              </button>
            </div>
          </a-spin>
          <div v-if="modelList.length === 0 && !modelsLoading" class="model-list-empty">{{ t.modelParam.empty }}</div>
          <div v-else-if="modelsLoadingMore" class="model-list-loading-more">{{ t.modelParam.loadingMore }}</div>
          <div v-else-if="!hasNext && modelList.length > 0" class="model-list-loading-more">{{ t.modelParam.allLoaded }}</div>
        </div>
      </div>

      <AstPagination
          v-if="!isEdit"
          :current="currentPage"
          :page-size="pageSize"
          :show-size-changer="true"
          :total="total"
          @change="onPaginationChange"
      />
    </div>
  </section>
</template>

<script lang="ts" setup>
import {LockOutlined, MessageOutlined, PartitionOutlined, PictureOutlined} from '@ant-design/icons-vue'
import AstPagination from '@/components/home/AstPagination.vue'
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

const props = defineProps<{
  isEdit?: boolean
  providerFilter?: string
  searchDraft: string
  typeFilter: string
  modelList: Array<Record<string, any>>
  selectedKeys: string[]
  modelsLoading: boolean
  modelsLoadingMore: boolean
  total: number
  hasNext: boolean
  pageSize: number
  currentPage: number
  pageSizeOptions: Array<{ label: string; value: number }>
  providerAvatarCell: (record: any) => string
  modelTypeLabel: (type?: string) => string
}>()

const emit = defineEmits<{
  (e: 'provider-change', value: string | undefined): void
  (e: 'update:searchDraft', value: string): void
  (e: 'search'): void
  (e: 'update:typeFilter', value: string): void
  (e: 'page-size-change', value: number): void
  (e: 'page-change', page: number): void
  (e: 'panel-scroll', event: Event): void
  (e: 'select-model', record: any): void
}>()

const onPaginationChange = (page: number, size: number) => {
  if (size !== props.pageSize) {
    emit('page-size-change', size)
    return
  }
  emit('page-change', page)
}
</script>

<style scoped>
.selection-pane {
  width: 520px;
  flex-shrink: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pane-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 18px;
}

.glass-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
}

.pane-header {
  flex-shrink: 0;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pane-toolbar-row {
  display: flex;
  flex-wrap: nowrap;
  gap: 10px;
  align-items: center;
  min-width: 0;
}

.provider-field {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-width: 0;
}

.field-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.instance-provider-select {
  flex: 0 0 auto;
  min-width: 140px;
  max-width: 200px;
}

.provider-search-pill {
  flex: 1;
  min-width: 0;
}

.provider-search-pill :deep(.toolbar-search-pill) {
  width: 100%;
}

.model-type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.model-type-tabs :deep(.ant-tabs-content-holder) {
  display: none;
}

.model-list-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.meta-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.model-list-body {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.model-card-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.model-card-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-select-card {
  width: 100%;
  border: 1px solid var(--border-default);
  border-radius: 12px;
  background: var(--bg-card);
  padding: 10px 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.model-select-card.is-active {
  border-color: var(--primary);
  background: var(--primary-hover, color-mix(in srgb, var(--primary) 5%, transparent));
}

.model-select-radio {
  width: 16px;
  height: 16px;
  border: 1px solid var(--border-default);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.model-select-radio .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: transparent;
}

.model-select-card.is-active .model-select-radio {
  border-color: var(--primary);
}

.model-select-card.is-active .model-select-radio .dot {
  background: var(--primary);
}

.inst-provider-avatar-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.inst-provider-avatar-cell :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}

.inst-provider-avatar-cell--empty {
  color: var(--text-tertiary);
  background: var(--bg-secondary);
  border-radius: 6px;
}

.endpoint-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.endpoint-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.endpoint-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.inst-model-type-icon {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
}

.inst-model-type-icon.chat {
  background: linear-gradient(135deg, #0061ff, #60efff);
}

.inst-model-type-icon.embedding {
  background: linear-gradient(135deg, #7c4dff, #f94dff);
}

.inst-model-type-icon.image {
  background: linear-gradient(135deg, #ff6b6b, #ffd93d);
}

.inst-model-type-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.model-list-empty, .model-list-loading-more {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 12px;
  padding: 12px 0;
}

.model-select-card.is-locked {
  cursor: not-allowed;
  opacity: 0.35;
  pointer-events: none;
}

.model-select-card:disabled {
  cursor: not-allowed;
}

.edit-locked-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-tertiary);
  padding: 8px 0;
}
</style>
