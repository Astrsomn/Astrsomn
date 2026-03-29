<template>
  <main class="assembly-canvas">
    <div class="canvas-header">
      <a-form layout="vertical" :colon="false" class="agent-assembly-form">
        <div class="header-row">
          <div class="form-group-main">
            <a-form-item label="名称" class="fi-name">
              <a-input v-model:value="agentForm.agentName" placeholder="智能体名称" allow-clear />
            </a-form-item>
            <a-form-item label="Streamable" class="fi-stream">
              <a-switch v-model:checked="agentForm.enableStream" />
            </a-form-item>
            <a-form-item label="记忆模式" class="fi-mem">
              <a-select v-model:value="agentForm.memoryMode" :options="memoryModeOptions" />
            </a-form-item>
            <a-form-item label="窗口大小" class="fi-win">
              <a-input-number v-model:value="memoryWindowNum" :min="0" />
            </a-form-item>
          </div>
          
          <div class="canvas-actions">
            <a-button class="action-btn btn-ghost" @click="emit('reset')">
              <template #icon><UndoOutlined /></template>
            </a-button>
            <a-button type="primary" class="action-btn btn-primary" @click="emit('submit')">
              <template #icon><SendOutlined /></template>
              发布
            </a-button>
          </div>
        </div>

        <div class="header-row row-meta">
          <a-form-item label="标识符 (Agent Key)" class="fi-key">
            <a-input v-model:value="agentForm.agentKey" placeholder="唯一 ID" />
          </a-form-item>
          <a-form-item label="角色描述" class="fi-desc">
            <a-textarea
              v-model:value="agentForm.description"
              placeholder="定义 AI 的行为准则..."
              :auto-size="{ minRows: 1, maxRows: 3 }"
            />
          </a-form-item>
        </div>
      </a-form>
    </div>

    <div class="drop-stack">
      <div class="drop-row">
        <AssemblyDropZone
          slot-key="chatInstance"
          title="对话核心 (Chat)"
          variant="chat"
          :icon="MessageOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="!!chatInstance"
          class="canvas-slot"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <template v-if="chatInstance">
            <div class="placed-card-fill">
              <div class="card-icon"><MessageOutlined /></div>
              <div class="card-content">
                <div class="p-title">{{ chatInstance.instanceName || chatInstance.instanceKey }}</div>
                <div class="p-sub">{{ chatInstance.modelKey }}</div>
              </div>
              <a-button type="link" size="small" danger @click="$emit('clear', 'chatInstance')">移除</a-button>
            </div>
          </template>
          <div v-else class="placeholder-minimal">请拖入对话模型</div>
        </AssemblyDropZone>
      </div>

      <div class="drop-grid">
        <AssemblyDropZone
          slot-key="embeddingInstance"
          title="向量检索"
          variant="emb"
          :icon="PartitionOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="!!embeddingInstance"
          class="canvas-slot"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <div v-if="embeddingInstance" class="placed-card-fill">
            <div class="card-content">
              <div class="p-title">{{ embeddingInstance.instanceName || embeddingInstance.instanceKey }}</div>
              <div class="p-sub">{{ embeddingInstance.modelKey }}</div>
            </div>
            <a-button type="link" size="small" danger @click="$emit('clear', 'embeddingInstance')">移除</a-button>
          </div>
          <div v-else class="placeholder-minimal">未配置向量</div>
        </AssemblyDropZone>

        <AssemblyDropZone
          slot-key="imageInstance"
          title="多媒体支持"
          variant="img"
          :icon="PictureOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="!!imageInstance"
          class="canvas-slot"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <div v-if="imageInstance" class="placed-card-fill">
            <div class="card-content">
              <div class="p-title">{{ imageInstance.instanceName || imageInstance.instanceKey }}</div>
              <div class="p-sub">{{ imageInstance.modelKey }}</div>
            </div>
            <a-button type="link" size="small" danger @click="$emit('clear', 'imageInstance')">移除</a-button>
          </div>
          <div v-else class="placeholder-minimal">未配置图像</div>
        </AssemblyDropZone>

        <AssemblyDropZone
          slot-key="tools"
          title="插件工具 (Tools)"
          variant="tool"
          :icon="ToolOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="tools.length > 0"
          class="canvas-slot"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <div v-if="tools.length" class="placed-card-fill tag-wrap">
            <a-tag v-for="t in tools" :key="t.toolKey" closable color="blue" @close="removeToolTag(t.toolKey)">
              {{ t.toolName || t.toolKey }}
            </a-tag>
          </div>
          <div v-else class="placeholder-minimal">拖入 Tool</div>
        </AssemblyDropZone>

        <AssemblyDropZone
          slot-key="mcps"
          title="MCP 服务"
          variant="mcp"
          :icon="ApiOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="mcps.length > 0"
          class="canvas-slot"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <div v-if="mcps.length" class="placed-card-fill tag-wrap">
            <a-tag v-for="m in mcps" :key="m.mcpKey" closable color="purple" @close="removeMcpTag(m.mcpKey)">
              {{ m.serverName || m.mcpKey }}
            </a-tag>
          </div>
          <div v-else class="placeholder-minimal">拖入 MCP</div>
        </AssemblyDropZone>
      </div>
    </div>

    <div class="knowledge-section">
      <AssemblyDropZone
        slot-key="knowledgeBase"
        title="关联知识库"
        variant="kb"
        :icon="BookOutlined"
        :dragging-payload="draggingPayload"
        :active-drop-key="activeDropKey"
        :has-content="knowledgeKeys.length > 0"
        class="canvas-slot"
        @hover="$emit('hover', $event)"
        @drop="$emit('drop', $event)"
      >
        <div v-if="knowledgeKeys.length" class="placed-card-fill tag-wrap">
          <a-tag v-for="k in knowledgeKeys" :key="k" closable color="cyan" @close="emit('removeKnowledgeKey', k)">
            {{ k }}
          </a-tag>
        </div>
        <div v-else class="placeholder-minimal">未关联知识库</div>
      </AssemblyDropZone>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ApiOutlined, BookOutlined, MessageOutlined, PartitionOutlined, 
  PictureOutlined, SendOutlined, ToolOutlined, UndoOutlined
} from '@ant-design/icons-vue'
import type { AiInstance } from '@/api/aiInstance'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import type { AssemblyAgentForm, AssemblyDragPayload, AssemblySlotKey } from './assemblyTypes'
import AssemblyDropZone from './AssemblyDropZone.vue'

const memoryModeOptions = [
  { label: '禁用', value: 'NONE' },
  { label: '滑动窗口', value: 'SLIDING_WINDOW' },
  { label: '长期向量', value: 'VECTOR' },
  { label: '混合模式', value: 'hybrid' }
]

const props = defineProps<{
  draggingPayload: AssemblyDragPayload | null
  activeDropKey: AssemblySlotKey | null
  chatInstance: AiInstance | null
  embeddingInstance: AiInstance | null
  imageInstance: AiInstance | null
  tools: AiTool[]
  mcps: AiMcp[]
  knowledgeKeys: string[]
}>()

const agentForm = defineModel<AssemblyAgentForm>('agentForm', { required: true })

const memoryWindowNum = computed({
  get(): number | undefined {
    const raw = agentForm.value.memoryWindowSize
    return (raw === '' || raw == null) ? undefined : Number(raw)
  },
  set(v: number | null | undefined) {
    agentForm.value.memoryWindowSize = (v == null) ? '' : String(v)
  }
})

const emit = defineEmits<{
  hover: [key: AssemblySlotKey | null]
  drop: [payload: AssemblyDragPayload]
  clear: [key: 'chatInstance' | 'embeddingInstance' | 'imageInstance']
  removeTool: [toolKey: string]
  removeMcp: [mcpKey: string]
  removeKnowledgeKey: [key: string]
  reset: []
  submit: []
}>()

function removeToolTag(key?: string) { if (key) emit('removeTool', key) }
function removeMcpTag(key?: string) { if (key) emit('removeMcp', key) }
</script>

<style scoped>
.assembly-canvas {
  --canvas-bg: #f5f7fa;
  --header-bg: #ffffff;
  --accent-blue: #1677ff;
  
  position: relative;
  background: var(--canvas-bg);
  border: 1px solid #e1e6eb;
  border-radius: 12px;
  padding: 0; /* 取消外层大内边距，让内部结构更紧凑 */
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部配置面板 */
.canvas-header {
  background: var(--header-bg);
  padding: 24px 30px;
  border-bottom: 1px solid #e1e6eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  z-index: 10;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
}

.row-meta { margin-top: 20px; }

.form-group-main {
  display: flex;
  flex: 1;
  gap: 20px;
  align-items: flex-end;
}

:deep(.ant-form-item) { margin-bottom: 0 !important; }
:deep(.ant-form-item-label > label) {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 600;
}

.fi-name { flex: 2; }
.fi-stream { flex: 0; }
.fi-mem { flex: 1; }
.fi-win { flex: 0 0 80px; }
.fi-key { flex: 1; }
.fi-desc { flex: 2; }

/* 按钮样式 */
.action-btn {
  height: 38px;
  border-radius: 8px;
  font-weight: 500;
}
.btn-ghost { border: 1px solid #dcdfe6; color: #606266; }
.btn-primary { padding: 0 24px; box-shadow: 0 4px 10px rgba(22, 119, 255, 0.25); }

/* 拖拽区域堆叠 */
.drop-stack {
  flex: 1;
  padding: 24px 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.drop-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* 深度定制 DropZone 内容填充 */
:deep(.assembly-drop-zone) {
  padding: 0 !important; /* 强制覆盖，让子元素卡片占满 */
  border-radius: 10px !important;
  background: rgba(255, 255, 255, 0.4);
  overflow: hidden;
}

.placed-card-fill {
  width: 100%;
  min-height: 80px; /* 确保高度足以占满 DropZone */
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #ffffff; /* 拖入后背景变为纯白 */
  transition: all 0.2s ease;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-icon {
  font-size: 24px;
  color: var(--accent-blue);
  margin-right: 16px;
  opacity: 0.8;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.p-title {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.4;
}

.p-sub {
  font-size: 12px;
  color: #909399;
  font-family: ui-monospace, 'Cascadia Code', monospace;
  margin-top: 2px;
}

.tag-wrap {
  flex-wrap: wrap;
  gap: 8px;
}

.placeholder-minimal {
  padding: 20px;
  text-align: center;
  color: #abb2bb;
  font-size: 13px;
  letter-spacing: 0.5px;
}

/* 底部区域 */
.knowledge-section {
  padding: 16px 30px 24px;
  background: var(--header-bg);
  border-top: 1px solid #e1e6eb;
}

@media (max-width: 1024px) {
  .drop-grid { grid-template-columns: 1fr; }
  .form-group-main { flex-wrap: wrap; }
}
</style>