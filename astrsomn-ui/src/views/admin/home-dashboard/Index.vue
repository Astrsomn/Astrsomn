<template>
  <div class="bento-dashboard-root demo-dashboard">
    <main class="bento-grid">
      <BentoAgentPanel />

      <BentoWorkflowPanel />

      <BentoSystemLoadPanel />

      <BentoIconTile
        title="向量知识库"
        subtitle="12 个集群"
        to="/admin/knowledge-bases"
        :icon="DatabaseOutlined"
        variant="amber"
        :disabled="!entryByRoute['/admin/knowledge-bases']"
      />

      <BentoIconTile
        title="模型路由"
        subtitle="8 个模型"
        to="/admin/models"
        :icon="NodeIndexOutlined"
        variant="cyan"
        :disabled="!entryByRoute['/admin/models']"
      />

      <BentoWideTile
        title="MCP 协议枢纽"
        description="跨服务实时上下文协议连接器"
        to="/admin/mcp"
        :disabled="!entryByRoute['/admin/mcp']"
      />

      <BentoLogTerminal />

      <BentoIconTile
        title="FTL 渲染"
        subtitle="24 个模板"
        to="/admin/templates"
        :icon="LayoutOutlined"
        variant="rose"
        :disabled="!entryByRoute['/admin/templates']"
      />

      <BentoIconTile
        title="流水线"
        subtitle="1.2k 任务/H"
        to="/admin/documents"
        :icon="PartitionOutlined"
        variant="sky"
        :disabled="!entryByRoute['/admin/documents']"
      />

      <BentoGradientTile
        title="插件市场"
        subtitle="AI能力扩展中心"
        to="/admin/tools"
        :icon="ShopOutlined"
        variant="market"
        :disabled="!entryByRoute['/admin/tools']"
      />

      <BentoGradientTile
        title="全部应用"
        subtitle="AI应用管理中心"
        to="/admin/resource-library"
        :icon="AppstoreOutlined"
        variant="apps"
      />
    </main>

    <div class="fab-wrap">
      <button type="button" class="fab fab--ghost" aria-label="帮助与入口" @click="navigateTo('/admin/resource-library')">
        <question-circle-outlined />
      </button>
      <button type="button" class="fab fab--primary" @click="navigateTo('/admin/workflows')">
        <plus-outlined />
        快速编排
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  DatabaseOutlined,
  NodeIndexOutlined,
  LayoutOutlined,
  PartitionOutlined,
  ShopOutlined,
  AppstoreOutlined,
  QuestionCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import type { ManagementEntry } from './backend/management'
import { getCurrentUserRole, resolveManagementGroups } from './backend/management'
import BentoAgentPanel from './backend/BentoAgentPanel.vue'
import BentoWorkflowPanel from './backend/BentoWorkflowPanel.vue'
import BentoSystemLoadPanel from './backend/BentoSystemLoadPanel.vue'
import BentoLogTerminal from './backend/BentoLogTerminal.vue'
import BentoIconTile from './backend/BentoIconTile.vue'
import BentoWideTile from './backend/BentoWideTile.vue'
import BentoGradientTile from './backend/BentoGradientTile.vue'

const router = useRouter()

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

.bento-dashboard-root {
  position: relative;
  z-index: 1;
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
@import './backend/bento-dashboard.css';
</style>
