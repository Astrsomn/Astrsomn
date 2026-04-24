<template>
  <section class="middle-grid">
    <div class="panel-card online-panel">
      <div class="panel-head">
        <h3>
          <TeamOutlined />
          在线业务系统
        </h3>
        <span>用户即业务系统，展示实时在线状态</span>
      </div>
      <div class="online-grid">
        <div v-for="system in pagedOnlineSystems" :key="system.name" class="online-item">
          <div class="online-top">
            <div class="online-name">
              <component :is="system.icon" />
              <span>{{ system.name }}</span>
            </div>
            <span class="online-status" :class="`status-${system.status}`">
              <span class="status-dot"></span>
              {{ system.statusText }}
            </span>
          </div>
          <div class="online-meta">
            <span><ClockCircleOutlined /> 最后心跳 {{ system.lastHeartbeat }}</span>
            <span><DatabaseOutlined /> 活跃会话 {{ system.sessions }}</span>
          </div>
        </div>
      </div>
      <div class="pager-wrap">
        <button type="button" class="pager-btn" :disabled="currentPage === 1" @click="emit('prev-page')">
          上一页
        </button>
        <span class="pager-text">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ totalSystems }} 个系统</span>
        <button type="button" class="pager-btn" :disabled="currentPage === totalPages" @click="emit('next-page')">
          下一页
        </button>
      </div>
    </div>

    <div class="right-stack">
      <div class="panel-card">
        <div class="panel-head">
          <h3>
            <AlertOutlined />
            待处理事项
          </h3>
          <span>建议优先处理项</span>
        </div>
        <ul class="todo-list">
          <li v-for="task in todoItems" :key="task">{{ task }}</li>
        </ul>
      </div>

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
    </div>
  </section>
</template>

<script setup lang="ts">
import {
  AlertOutlined,
  ApiOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import type { Component } from 'vue'

type OnlineStatus = 'online' | 'degraded' | 'offline'

type OnlineSystem = {
  name: string
  status: OnlineStatus
  statusText: string
  sessions: number
  lastHeartbeat: string
  icon: Component
}

type ExtensionCard = {
  name: string
  count: number
  desc: string
  icon: Component
  tags: string[]
}

const props = defineProps<{
  pagedOnlineSystems: OnlineSystem[]
  currentPage: number
  totalPages: number
  totalSystems: number
  todoItems: string[]
  extensionCards: ExtensionCard[]
}>()

const emit = defineEmits<{
  (e: 'prev-page'): void
  (e: 'next-page'): void
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
.middle-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.right-stack {
  display: grid;
  grid-template-rows: 1fr 1fr;
  gap: 16px;
  min-height: 0;
}

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

.online-panel {
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

.online-grid {
  display: grid;
  gap: 10px;
}

.online-item {
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  padding: 12px 14px;
}

.online-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.online-name {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-heading);
  font-size: 14px;
  font-weight: 600;
}

.online-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid transparent;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: currentColor;
}

.status-online {
  color: #2e9f5d;
  background: color-mix(in srgb, #2e9f5d 12%, transparent);
  border-color: color-mix(in srgb, #2e9f5d 26%, transparent);
}

.status-degraded {
  color: #f39c12;
  background: color-mix(in srgb, #f39c12 12%, transparent);
  border-color: color-mix(in srgb, #f39c12 24%, transparent);
}

.status-offline {
  color: #dd4b39;
  background: color-mix(in srgb, #dd4b39 12%, transparent);
  border-color: color-mix(in srgb, #dd4b39 24%, transparent);
}

.online-meta {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 12px;
}

.online-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.pager-wrap {
  margin-top: 12px;
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

.todo-list {
  margin: 0;
  padding: 0 0 0 16px;
  display: grid;
  gap: 8px;
  color: var(--text-secondary);
  line-height: 1.5;
  font-size: 13px;
}

.todo-list li::marker {
  color: var(--primary);
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

@media (max-width: 1200px) {
  .middle-grid {
    grid-template-columns: 1fr;
  }

  .right-stack {
    grid-template-rows: auto auto;
  }
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
