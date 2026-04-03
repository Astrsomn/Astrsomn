<template>
  <div class="console-home-root demo-dashboard">
    <div class="dashboard-toolbar">
      <p v-if="layoutEditMode" class="toolbar-hint">拖动卡片调整位置，拖右下角调整大小；编辑时点击入口不会跳转。</p>
      <div class="toolbar-actions">
        <button
          v-if="layoutEditMode"
          type="button"
          class="toolbar-btn"
          @click="confirmResetLayout"
        >
          恢复默认布局
        </button>
        <button type="button" class="toolbar-btn toolbar-btn--primary" @click="layoutEditMode = !layoutEditMode">
          {{ layoutEditMode ? '完成编辑' : '编辑布局' }}
        </button>
      </div>
    </div>

    <main class="dashboard-home-main">
      <DashboardHomeGrid :edit-mode="layoutEditMode" :entry-by-route="entryByRoute" />
    </main>

    <div class="fab-wrap">
      <button type="button" class="fab fab--ghost" aria-label="帮助与入口" @click="navigateTo('/admin/resource-library')">
        <question-circle-outlined />
      </button>
      <button type="button" class="fab fab--primary" @click="navigateTo('/admin/ai-instance')">
        <plus-outlined />
        实例配置
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { QuestionCircleOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { Modal, message } from 'ant-design-vue'
import type { ManagementEntry } from './backend/management'
import { getCurrentUserRole, resolveManagementGroups } from './backend/management'
import DashboardHomeGrid from './backend/DashboardHomeGrid.vue'
import { resetDashboardLayoutToDefault } from './backend/dashboardLayoutStorage'

const router = useRouter()
const layoutEditMode = ref(false)

const currentRole = computed(() => getCurrentUserRole())
const managementGroups = computed(() => resolveManagementGroups(currentRole.value))

const allEntries = computed<ManagementEntry[]>(() =>
  managementGroups.value.flatMap((group) => group.items),
)

const entryByRoute = computed<Partial<Record<string, ManagementEntry>>>(() => {
  const map: Partial<Record<string, ManagementEntry>> = {}
  for (const entry of allEntries.value) map[entry.route] = entry
  return map
})

const navigateTo = (path: string) => {
  void router.push(path)
}

const confirmResetLayout = () => {
  Modal.confirm({
    title: '恢复默认布局？',
    content: '将重置控制台栅格为默认排版（含智能体、实例、负载与快捷入口；仍可在应用库继续添加快捷方式）。',
    okText: '恢复',
    cancelText: '取消',
    onOk() {
      resetDashboardLayoutToDefault()
      message.success('已恢复默认布局')
    },
  })
}
</script>

<style scoped>
.demo-dashboard {
  position: relative;
  box-sizing: border-box;
  padding: 10px 15px 96px;
  background: var(--bg-base);
  min-height: 100%;
  overflow-x: hidden;
}

.demo-dashboard::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(
    color-mix(in srgb, var(--text-muted) 45%, transparent) 1px,
    transparent 0
  );
  background-size: 24px 24px;
  opacity: 0.28;
  pointer-events: none;
}

.console-home-root {
  position: relative;
  z-index: 1;
}

.dashboard-home-main {
  position: relative;
  z-index: 1;
  width: 100%;
}

.dashboard-toolbar {
  position: relative;
  z-index: 2;
  max-width: 1600px;
  margin: 0 auto 12px;
  padding: 0 4px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-hint {
  margin: 0;
  flex: 1;
  min-width: 200px;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.toolbar-btn {
  border-radius: 10px;
  padding: 8px 14px;
  font-size: 12px;
  font-weight: 800;
  cursor: pointer;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-card) 92%, transparent);
  color: var(--text-heading);
}

.toolbar-btn:hover {
  border-color: color-mix(in srgb, var(--primary) 35%, var(--border-subtle));
}

.toolbar-btn--primary {
  border-color: color-mix(in srgb, var(--primary) 45%, var(--border-subtle));
  background: color-mix(in srgb, var(--primary) 12%, var(--bg-card));
  color: color-mix(in srgb, var(--primary) 85%, #2563eb);
}

.fab-wrap {
  position: fixed;
  bottom: 32px;
  right: 32px;
  display: flex;
  gap: 12px;
  z-index: 20;
  pointer-events: none;
}

.fab-wrap > * {
  pointer-events: auto;
}

.fab {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: none;
  cursor: pointer;
  font-weight: 800;
  font-size: 0.875rem;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.fab--ghost {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: color-mix(in srgb, var(--bg-card) 88%, transparent);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-subtle);
  color: var(--text-muted);
  box-shadow: 0 10px 25px -8px rgba(0, 0, 0, 0.12);
  font-size: 18px;
}

.fab--ghost:hover {
  color: color-mix(in srgb, var(--primary) 80%, #2563eb);
  transform: translateY(-1px);
}

.fab--primary {
  height: 56px;
  padding: 0 22px;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--primary) 55%, #2563eb),
    color-mix(in srgb, var(--primary) 35%, #3730a3)
  );
  box-shadow:
    0 12px 28px -6px color-mix(in srgb, var(--primary) 45%, transparent),
    0 4px 12px rgba(0, 0, 0, 0.08);
}

.fab--primary:hover {
  transform: translateY(-2px);
  box-shadow:
    0 16px 36px -8px color-mix(in srgb, var(--primary) 50%, transparent),
    0 6px 14px rgba(0, 0, 0, 0.1);
}

@media (max-width: 1024px) {
  .demo-dashboard {
    padding: 16px 16px 100px;
  }

  .fab-wrap {
    right: 16px;
    bottom: 20px;
  }
}
</style>

<style>
@import './backend/dashboard-shell.css';
</style>
