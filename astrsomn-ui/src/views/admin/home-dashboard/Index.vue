<template>
  <div class="demo-dashboard">
    <div class="demo-main-grid">
      <section class="demo-left">
        <section class="demo-section">
          <header class="demo-section-head">
            <div class="demo-section-title-wrap">
              <h2 class="demo-section-title">核心编排中心</h2>
              <p class="demo-section-subtitle">AI 智能体、工作流及模型路由管理</p>
            </div>
          </header>

          <div class="demo-slot-grid">
            <MenuSlotCard
              v-for="slot in coreSlots"
              :key="slot.route"
              :entry="entryByRoute[slot.route]"
              :title="slot.title"
              :description="slot.description"
              :accent="slot.accent"
              @navigate="navigateTo"
            />
          </div>
        </section>

        <section class="demo-section">
          <header class="demo-section-head">
            <div class="demo-section-title-wrap">
              <h2 class="demo-section-title">数据与资产资产</h2>
              <p class="demo-section-subtitle">向量知识库及渲染引擎管理</p>
            </div>
          </header>

          <div class="demo-slot-grid">
            <MenuSlotCard
              v-for="slot in resourceSlots"
              :key="slot.route"
              :entry="entryByRoute[slot.route]"
              :title="slot.title"
              :description="slot.description"
              :accent="slot.accent"
              @navigate="navigateTo"
            />
          </div>
        </section>
      </section>

      <aside class="demo-right">
        <section class="side-card side-card--news">
          <div class="side-news-icon" aria-hidden="true">✦</div>
          <h4 class="side-news-title">新版本发布 v2.6</h4>
          <p class="side-news-desc">
            推理引擎已升级，对多模态模型支持提升了 30% 的稳定性。
          </p>
          <button type="button" class="side-news-btn" disabled>查看更新说明</button>
        </section>

        <section class="side-card">
          <div class="side-card-head">
            <h3 class="side-card-title">系统负载</h3>
            <span class="side-status">
              <span class="side-status-dot" />
              稳定运行
            </span>
          </div>

          <div class="side-metrics">
            <div class="side-metric">
              <div class="side-metric-row">
                <span class="side-metric-label">API 吞吐量</span>
                <span class="side-metric-value">1.2k req/s</span>
              </div>
              <div class="side-bar" aria-hidden="true">
                <div class="side-bar-seg side-bar-seg--1" />
                <div class="side-bar-seg side-bar-seg--2" />
              </div>
            </div>

            <div class="side-mini-grid">
              <div class="side-mini-card">
                <div class="side-mini-label">平均延迟</div>
                <div class="side-mini-value">124ms</div>
              </div>
              <div class="side-mini-card">
                <div class="side-mini-label">错误率</div>
                <div class="side-mini-value">0.02%</div>
              </div>
            </div>
          </div>
        </section>

        <section class="side-card side-card--tools">
          <h3 class="side-tools-title">快捷提效</h3>
                <button
            type="button"
            class="side-resource-btn"
            @click="navigateTo('/admin/resource-library')"
          >
            浏览全部入口
          </button>
          <div class="side-tools-list">
            <button class="side-tool-btn" type="button" disabled>
              <span class="side-tool-icon">C</span>
              <span class="side-tool-text">
                <span class="side-tool-name">清理模型缓存</span>
                <span class="side-tool-desc">释放约 2.4GB 内存</span>
              </span>
            </button>
            <button class="side-tool-btn" type="button" disabled>
              <span class="side-tool-icon">E</span>
              <span class="side-tool-text">
                <span class="side-tool-name">导出当日审计日志</span>
                <span class="side-tool-desc">格式为 CSV / JSON</span>
              </span>
            </button>
          </div>
        </section>


      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import MenuSlotCard from './backend/MenuSlotCard.vue'
import type { EntryAccent, ManagementEntry } from './backend/management'
import { getCurrentUserRole, resolveManagementGroups } from './backend/management'

type Slot = {
  route: string
  title: string
  description: string
  accent: EntryAccent
}

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

const coreSlots: Slot[] = [
  {
    route: '/admin/agents',
    title: '智能体管理',
    description: '封装复杂推理逻辑与多模态能力',
    accent: 'indigo',
  },
  {
    route: '/admin/workflows',
    title: '工作流引擎',
    description: '基于 DAG 的视觉化任务编排',
    accent: 'blue',
  },
  {
    route: '/admin/models',
    title: '模型路由',
    description: '统一管理 API 转发及多模型热切换',
    accent: 'sky',
  },
  {
    route: '/admin/prompts',
    title: '提示词仓库',
    description: '版本化提示词与模板管理',
    accent: 'violet',
  },
  {
    route: '/admin/mcp',
    title: 'MCP 协议枢纽',
    description: '跨服务实时上下文协议连接器',
    accent: 'frost',
  },
  {
    route: '/admin/ai-instance',
    title: '推理实例配置',
    description: '预设 Token 限制与采样参数',
    accent: 'cyan',
  },
]

const resourceSlots: Slot[] = [
  {
    route: '/admin/knowledge-bases',
    title: '向量知识库',
    description: '管理 RAG 嵌入模型与语义索引',
    accent: 'mint',
  },
  {
    route: '/admin/templates',
    title: 'FTL 渲染引擎',
    description: 'Server-Driven UI 动态模板管理',
    accent: 'teal',
  },
  {
    route: '/admin/documents',
    title: '文档流水线',
    description: '非结构化文档解析与切片',
    accent: 'primary-light',
  },
]

const navigateTo = (path: string) => {
  void router.push(path)
}
</script>

<style scoped>
.demo-dashboard {
  position: relative;
  box-sizing: border-box;
  padding: 10px 15px;
  background: var(--bg-base);
  overflow: hidden;
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

.demo-main-grid {
  position: relative;
  z-index: 1;
  max-width: 1600px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 32px;
}

.demo-left {
  grid-column: span 9;
  min-width: 0;
}

.demo-right {
  padding-top: 65px;
  grid-column: span 3;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.demo-section {
  margin-bottom: 48px;
}

.demo-section-head {
  margin-bottom: 20px;
}

.demo-section-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.demo-section-title {
  padding-top: 20px;
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--text-heading);
}

.demo-section-subtitle {
  margin: 0;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
}

.demo-slot-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.side-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 24px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.02),
    0 4px 6px -1px rgba(0, 0, 0, 0.03);
  padding: 20px;
}

.side-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 12px;
}

.side-card-title {
  margin: 0;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted);
}

.side-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 800;
  color: color-mix(in srgb, var(--success) 85%, var(--text-heading));
}

.side-status-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: var(--success);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--success) 20%, transparent);
}

.side-metric-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.side-metric-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 700;
}

.side-metric-value {
  font-size: 13px;
  color: var(--text-heading);
  font-weight: 900;
}

.side-bar {
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--text-muted) 14%, transparent);
  overflow: hidden;
  display: flex;
  gap: 6px;
}

.side-bar-seg {
  height: 100%;
  border-radius: 999px;
}

.side-bar-seg--1 {
  width: 40%;
  background: color-mix(in srgb, var(--primary) 85%, #3b82f6);
}

.side-bar-seg--2 {
  width: 15%;
  background: color-mix(in srgb, var(--primary) 35%, var(--success));
}

.side-mini-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.side-mini-card {
  padding: 14px;
  border-radius: 16px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--text-muted) 10%, transparent);
}

.side-mini-label {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 800;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.side-mini-value {
  font-size: 18px;
  font-weight: 900;
  color: var(--text-heading);
}

.side-card--tools {
  padding-top: 16px;
}

.side-tools-title {
  margin: 0 0 14px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted);
}

.side-tools-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.side-tool-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  border-radius: 16px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-card) 92%, var(--text-muted) 8%);
  padding: 12px 14px;
  cursor: default;
}

.side-tool-btn:hover {
  border-color: color-mix(in srgb, var(--primary) 35%, var(--border-subtle));
  background: color-mix(in srgb, var(--primary) 10%, var(--bg-card));
}

.side-tool-icon {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: color-mix(in srgb, var(--text-muted) 18%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-weight: 900;
}

.side-tool-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}

.side-tool-name {
  font-size: 12px;
  font-weight: 900;
  color: var(--text-heading);
  margin-bottom: 4px;
}

.side-tool-desc {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 700;
}

.side-card--news {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary) 55%, #2563eb), color-mix(in srgb, var(--primary) 35%, #7c3aed));
  border-color: color-mix(in srgb, var(--primary) 45%, var(--border-subtle));
  color: #ffffff;
  position: relative;
  overflow: hidden;
}

.side-news-icon {
  position: absolute;
  right: -10px;
  top: -12px;
  font-size: 44px;
  opacity: 0.18;
  transform: rotate(12deg);
  user-select: none;
}

.side-news-title {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 900;
}

.side-news-desc {
  margin: 0 0 16px;
  font-size: 12px;
  line-height: 1.6;
  opacity: 0.9;
}

.side-news-btn {
  width: 100%;
  border-radius: 12px;
  border: 1px solid color-mix(in srgb, #ffffff 22%, transparent);
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
  padding: 10px 12px;
  font-size: 11px;
  font-weight: 900;
}

.side-news-btn:disabled {
  opacity: 0.85;
}

.side-card--resource-entry {
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--primary) 14%, var(--bg-card)),
    color-mix(in srgb, var(--accent-blue) 6%, var(--bg-card))
  );
}

.side-resource-toc {
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary) 65%, var(--text-heading));
  background: color-mix(in srgb, var(--primary) 12%, var(--bg-card));
  border: 1px solid color-mix(in srgb, var(--primary) 28%, var(--border-subtle));
  padding: 6px 10px;
  border-radius: 999px;
}

.side-resource-desc {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.6;
  margin-bottom: 14px;
}

.side-resource-btn {
  width: 100%;
  border-radius: var(--radius-xl);
  border: 1px solid color-mix(in srgb, var(--primary) 45%, var(--border-subtle));
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--primary) 55%, #2563eb),
    color-mix(in srgb, var(--primary) 35%, #7c3aed)
  );
  padding: 10px 42px 10px 14px; /* 为右侧箭头预留空间 */
  color: #ffffff;
  font-size: 11px;
  font-weight: 900;
  cursor: pointer;
  position: relative;
  isolation: isolate;
  overflow: hidden;
  letter-spacing: 0.02em;
  margin-bottom: 12px;
  transition:
    border-color 0.2s ease,
    background 0.2s ease,
    transform 0.2s ease,
    box-shadow 0.2s ease;
  box-shadow:
    0 10px 24px -18px color-mix(in srgb, var(--primary) 45%, transparent),
    0 0 0 1px color-mix(in srgb, var(--primary) 12%, transparent);
}

.side-resource-btn::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: calc(var(--radius-xl) + 2px);
  background:
    radial-gradient(circle at 20% 20%, color-mix(in srgb, #2563eb 75%, transparent), transparent 60%),
    radial-gradient(circle at 80% 40%, color-mix(in srgb, #7c3aed 62%, transparent), transparent 58%),
    radial-gradient(circle at 50% 90%, color-mix(in srgb, #2563eb 55%, transparent), transparent 60%);
  opacity: 0.35;
  z-index: -1;
  animation: sideResourcePulse 2.6s ease-in-out infinite;
  pointer-events: none;
}

.side-resource-btn::after {
  content: '→';
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%) translateX(0);
  font-size: 14px;
  line-height: 1;
  color: rgba(255, 255, 255, 0.95);
  opacity: 0.95;
  transition: transform 0.22s ease, opacity 0.22s ease, color 0.22s ease;
  pointer-events: none;
}

.side-resource-btn:hover {
  border-color: color-mix(in srgb, var(--primary) 55%, var(--border-subtle));
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--primary) 60%, #2563eb),
    color-mix(in srgb, var(--primary) 42%, #7c3aed)
  );
  transform: translateY(-1px) scale(1.01);
  box-shadow:
    0 18px 34px -24px color-mix(in srgb, var(--primary) 60%, transparent),
    0 0 0 3px color-mix(in srgb, var(--primary) 18%, transparent);
}

.side-resource-btn:hover::after {
  transform: translateY(-50%) translateX(3px);
  color: rgba(255, 255, 255, 1);
}

/* 键盘可访问性：聚焦时给更清晰的外圈 */
.side-resource-btn:focus-visible {
  outline: none;
  box-shadow:
    0 18px 34px -24px color-mix(in srgb, var(--primary) 60%, transparent),
    0 0 0 3px color-mix(in srgb, var(--primary) 30%, transparent);
}

@keyframes sideResourcePulse {
  0% {
    opacity: 0.22;
    transform: scale(1);
    filter: blur(0px);
  }
  50% {
    opacity: 0.42;
    transform: scale(1.02);
    filter: blur(0.2px);
  }
  100% {
    opacity: 0.22;
    transform: scale(1);
    filter: blur(0px);
  }
}

@media (prefers-reduced-motion: reduce) {
  .side-resource-btn::before {
    animation: none;
  }
  .side-resource-btn:hover::after {
    transform: translateY(-50%) translateX(0);
  }
}

@media (max-width: 1024px) {
  .demo-dashboard {
    padding: 16px;
  }

  .demo-main-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .demo-left,
  .demo-right {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .demo-slot-grid {
    grid-template-columns: 1fr;
  }
}
</style>