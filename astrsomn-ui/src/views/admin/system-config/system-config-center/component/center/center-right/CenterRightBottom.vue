<template>
  <div class="panel-card extension-panel">
    <div class="panel-head">
      <h3>
        <ApiOutlined />
        系统扩展能力
      </h3>
      <span>支持多类型扩展：插件、向量库、模型等</span>
    </div>
    <div class="extension-grid">
      <article v-for="item in pagedExtensionCards" :key="item.name" class="extension-item">
        <div class="extension-top">
          <div class="extension-name">
            <component :is="item.icon" />
            <span>{{ item.name }}</span>
          </div>
          <strong>{{ item.count }}</strong>
        </div>
        <p class="extension-desc">{{ item.desc }}</p>
        <div class="extension-tags">
          <span v-for="tag in item.tags" :key="tag" class="extension-tag">{{ tag }}</span>
        </div>
      </article>
    </div>
    <div class="pager-wrap extension-pager">
      <button
        type="button"
        class="pager-btn"
        :disabled="extensionCurrentPage === 1"
        @click="prevExtensionPage"
      >
        上一页
      </button>
      <span class="pager-text">
        第 {{ extensionCurrentPage }} / {{ extensionTotalPages }} 页，共 {{ extensionCards.length }} 项
      </span>
      <button
        type="button"
        class="pager-btn"
        :disabled="extensionCurrentPage === extensionTotalPages"
        @click="nextExtensionPage"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ApiOutlined } from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import type { Component } from 'vue'

type ExtensionCard = {
  name: string
  count: number
  desc: string
  icon: Component
  tags: string[]
}

const props = defineProps<{
  extensionCards: ExtensionCard[]
}>()

const extensionPageSize = 2
const extensionCurrentPage = ref(1)

const extensionTotalPages = computed(() => Math.max(1, Math.ceil(props.extensionCards.length / extensionPageSize)))

const pagedExtensionCards = computed(() => {
  const start = (extensionCurrentPage.value - 1) * extensionPageSize
  return props.extensionCards.slice(start, start + extensionPageSize)
})

const prevExtensionPage = () => {
  if (extensionCurrentPage.value > 1) {
    extensionCurrentPage.value -= 1
  }
}

const nextExtensionPage = () => {
  if (extensionCurrentPage.value < extensionTotalPages.value) {
    extensionCurrentPage.value += 1
  }
}
</script>

<style scoped>
.panel-card {
  border: 1px solid var(--border-default);
  border-radius: 14px;
  background: var(--bg-card);
  padding: 20px;
  min-height: 0;
}

.extension-panel {
  display: flex;
  flex-direction: column;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: var(--text-heading);
}

.panel-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.extension-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  flex: 1;
}

.extension-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  padding: 10px;
  background: color-mix(in srgb, var(--bg-card) 88%, var(--bg-base));
}

.extension-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: var(--text-heading);
}

.extension-name {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.extension-top strong {
  font-size: 16px;
  color: var(--text-heading);
}

.extension-desc {
  margin: 6px 0;
  color: var(--text-secondary);
  font-size: 12px;
  line-height: 1.5;
}

.extension-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.extension-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  color: var(--text-secondary);
  border: 1px solid color-mix(in srgb, var(--border-default) 80%, transparent);
  background: color-mix(in srgb, var(--bg-card) 90%, var(--bg-base));
}

.extension-pager {
  margin-top: 10px;
}

.pager-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.pager-btn {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
}

.pager-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pager-text {
  color: var(--text-secondary);
  font-size: 12px;
}

@media (max-width: 900px) {
  .pager-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .pager-text {
    text-align: center;
  }
}
</style>
