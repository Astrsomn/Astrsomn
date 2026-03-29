<template>
  <main class="assembly-canvas">
    <div class="canvas-top">
      <a-form layout="vertical" :colon="false" class="agent-assembly-form">
        <div class="form-row form-row-main">
          <a-form-item label="名称" class="fi-name">
            <a-input v-model:value="agentForm.agentName" allow-clear />
          </a-form-item>
          <a-form-item label="流式" class="fi-stream">
            <a-switch v-model:checked="agentForm.enableStream" />
          </a-form-item>
          <a-form-item label="记忆" class="fi-mem">
            <a-select
              v-model:value="agentForm.memoryMode"
              :options="memoryModeOptions"
              class="select-mem"
            />
          </a-form-item>
          <a-form-item label="窗口" class="fi-win">
            <a-input-number v-model:value="memoryWindowNum" :min="0" class="input-win" />
          </a-form-item>
          <div class="canvas-actions">
            <a-button type="primary" shape="circle" class="canvas-action-btn" @click="emit('reset')">
              <template #icon><UndoOutlined /></template>
            </a-button>
            <a-button type="primary" shape="circle" class="canvas-action-btn" @click="emit('submit')">
              <template #icon><SendOutlined /></template>
            </a-button>
          </div>
        </div>
        <a-form-item label="描述" class="fi-desc">
          <a-textarea
            v-model:value="agentForm.description"
            :auto-size="{ minRows: 2, maxRows: 3 }"
            allow-clear
          />
        </a-form-item>
      </a-form>
    </div>

    <div class="drop-stack">
      <div class="drop-row drop-row-full">
        <AssemblyDropZone
          slot-key="chatInstance"
          title="对话预设"
          variant="chat"
          :icon="MessageOutlined"
          :dragging-payload="draggingPayload"
          :active-drop-key="activeDropKey"
          :has-content="!!chatInstance"
          @hover="$emit('hover', $event)"
          @drop="$emit('drop', $event)"
        >
          <template v-if="chatInstance">
            <div class="placed-card">
              <div class="placed-row">
                <span class="placed-name">{{ chatInstance.instanceName || chatInstance.instanceKey }}</span>
                <a-button type="link" size="small" danger @click="$emit('clear', 'chatInstance')">移除</a-button>
              </div>
              <div class="placed-meta">{{ chatInstance.instanceKey }} · {{ chatInstance.modelKey || '—' }}</div>
            </div>
          </template>
          <div v-else class="placeholder">拖入对话预设</div>
        </AssemblyDropZone>
      </div>

      <div class="drop-row drop-row-pair">
        <div class="drop-cell">
          <AssemblyDropZone
            slot-key="embeddingInstance"
            title="向量预设"
            variant="emb"
            :icon="PartitionOutlined"
            :dragging-payload="draggingPayload"
            :active-drop-key="activeDropKey"
            :has-content="!!embeddingInstance"
            @hover="$emit('hover', $event)"
            @drop="$emit('drop', $event)"
          >
            <template v-if="embeddingInstance">
              <div class="placed-card">
                <div class="placed-row">
                  <span class="placed-name">{{ embeddingInstance.instanceName || embeddingInstance.instanceKey }}</span>
                  <a-button type="link" size="small" danger @click="$emit('clear', 'embeddingInstance')">移除</a-button>
                </div>
                <div class="placed-meta">{{ embeddingInstance.instanceKey }} · {{ embeddingInstance.modelKey || '—' }}</div>
              </div>
            </template>
            <div v-else class="placeholder">拖入向量预设</div>
          </AssemblyDropZone>
        </div>
        <div class="drop-cell">
          <AssemblyDropZone
            slot-key="imageInstance"
            title="图像预设"
            variant="img"
            :icon="PictureOutlined"
            :dragging-payload="draggingPayload"
            :active-drop-key="activeDropKey"
            :has-content="!!imageInstance"
            @hover="$emit('hover', $event)"
            @drop="$emit('drop', $event)"
          >
            <template v-if="imageInstance">
              <div class="placed-card">
                <div class="placed-row">
                  <span class="placed-name">{{ imageInstance.instanceName || imageInstance.instanceKey }}</span>
                  <a-button type="link" size="small" danger @click="$emit('clear', 'imageInstance')">移除</a-button>
                </div>
                <div class="placed-meta">{{ imageInstance.instanceKey }} · {{ imageInstance.modelKey || '—' }}</div>
              </div>
            </template>
            <div v-else class="placeholder">拖入图像预设</div>
          </AssemblyDropZone>
        </div>
      </div>

      <div class="drop-row drop-row-pair">
        <div class="drop-cell">
          <AssemblyDropZone
            slot-key="tools"
            title="工具"
            variant="tool"
            :icon="ToolOutlined"
            :dragging-payload="draggingPayload"
            :active-drop-key="activeDropKey"
            :has-content="tools.length > 0"
            @hover="$emit('hover', $event)"
            @drop="$emit('drop', $event)"
          >
            <div v-if="tools.length" class="tag-area">
              <a-tag
                v-for="t in tools"
                :key="t.toolKey"
                closable
                color="blue"
                @close="removeToolTag(t.toolKey)"
              >
                {{ t.toolName || t.toolKey }}
              </a-tag>
            </div>
            <div v-else class="placeholder">拖入工具</div>
          </AssemblyDropZone>
        </div>
        <div class="drop-cell">
          <AssemblyDropZone
            slot-key="mcps"
            title="MCP"
            variant="mcp"
            :icon="ApiOutlined"
            :dragging-payload="draggingPayload"
            :active-drop-key="activeDropKey"
            :has-content="mcps.length > 0"
            @hover="$emit('hover', $event)"
            @drop="$emit('drop', $event)"
          >
            <div v-if="mcps.length" class="tag-area">
              <a-tag
                v-for="m in mcps"
                :key="m.mcpKey"
                closable
                color="purple"
                @close="removeMcpTag(m.mcpKey)"
              >
                {{ m.serverName || m.mcpKey }}
              </a-tag>
            </div>
            <div v-else class="placeholder">拖入 MCP</div>
          </AssemblyDropZone>
        </div>
      </div>
    </div>

    <div class="knowledge-block">
      <AssemblyDropZone
        slot-key="knowledgeBase"
        title="关联知识库"
        variant="kb"
        :icon="BookOutlined"
        :dragging-payload="draggingPayload"
        :active-drop-key="activeDropKey"
        :has-content="knowledgeKeys.length > 0"
        @hover="$emit('hover', $event)"
        @drop="$emit('drop', $event)"
      >
        <div v-if="knowledgeKeys.length" class="tag-area">
          <a-tag
            v-for="k in knowledgeKeys"
            :key="k"
            closable
            color="cyan"
            @close="emit('removeKnowledgeKey', k)"
          >
            {{ k }}
          </a-tag>
        </div>
        <div v-else class="placeholder">拖入知识库</div>
      </AssemblyDropZone>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ApiOutlined,
  BookOutlined,
  MessageOutlined,
  PartitionOutlined,
  PictureOutlined,
  SendOutlined,
  ToolOutlined,
  UndoOutlined
} from '@ant-design/icons-vue'
import type { AiInstance } from '@/api/aiInstance'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import type { AssemblyAgentForm, AssemblyDragPayload, AssemblySlotKey } from './assemblyTypes'
import AssemblyDropZone from './AssemblyDropZone.vue'

/** 与 AgentFormModal 中 memoryModeOptions 一致 */
const memoryModeOptions = [
  { label: '不开启记忆', value: 'NONE' },
  { label: '滑动窗口', value: 'SLIDING_WINDOW' },
  { label: '向量长期记忆', value: 'VECTOR' },
  { label: '混合模式', value: 'hybrid' }
]

defineProps<{
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
    if (raw === '' || raw === undefined || raw === null) return undefined
    const n = Number(raw)
    return Number.isFinite(n) ? n : undefined
  },
  set(v: number | null | undefined) {
    agentForm.value.memoryWindowSize = v == null || v === undefined ? '' : String(v)
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

function removeToolTag(key?: string) {
  if (key) emit('removeTool', key)
}

function removeMcpTag(key?: string) {
  if (key) emit('removeMcp', key)
}
</script>

<style scoped>
.assembly-canvas {
  position: relative;
  border: 1px dashed #d9d9d9;
  border-radius: 12px;
  padding: 8px 10px 10px;
  background: linear-gradient(145deg, #fbfcff 0%, #f7f9fc 100%);
  min-height: calc(100vh - 100px);
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  min-width: 0;
}

.canvas-top {
  flex-shrink: 0;
  padding-bottom: 6px;
  margin-bottom: 6px;
  border-bottom: 1px solid #e8e8e8;
}

.agent-assembly-form {
  width: 100%;
  min-width: 0;
}

.agent-assembly-form :deep(.ant-form-item) {
  margin-bottom: 8px;
}

.agent-assembly-form :deep(.ant-form-item-label) {
  padding-bottom: 0;
}

.agent-assembly-form :deep(.ant-form-item-label > label) {
  font-size: 12px;
  color: #595959;
  height: auto;
}

.form-row-main {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 8px 12px;
}

.form-row-main .fi-name {
  flex: 1 1 160px;
  min-width: 0;
}

.form-row-main .fi-stream {
  flex: 0 0 auto;
}

.form-row-main .fi-mem {
  flex: 0 1 160px;
  min-width: 120px;
}

.form-row-main .fi-win {
  flex: 0 0 104px;
}

.fi-desc :deep(.ant-form-item-label) {
  padding-bottom: 2px;
}

.select-mem {
  width: 100%;
  min-width: 120px;
}

.input-win {
  width: 100%;
}

.canvas-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: auto;
  padding-bottom: 2px;
}

@media (max-width: 640px) {
  .form-row-main .canvas-actions {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
  }
}

.canvas-action-btn {
  width: 40px !important;
  height: 40px !important;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  background: #1677ff !important;
  border-color: #1677ff !important;
  border-radius: 12px !important;
}

.canvas-action-btn:hover {
  background: #4096ff !important;
  border-color: #4096ff !important;
}

.drop-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  min-height: 0;
  min-width: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.knowledge-block {
  flex-shrink: 0;
  margin-top: 4px;
  padding-top: 6px;
  border-top: 1px solid #e8e8e8;
  min-width: 0;
}

.knowledge-block :deep(.slot-wrap) {
  margin-bottom: 0;
}

.knowledge-block :deep(.assembly-drop-zone) {
  min-height: 76px;
}

.drop-row-full {
  min-width: 0;
  overflow-x: hidden;
}

.drop-row-full :deep(.slot-wrap) {
  min-width: 0;
}

.drop-row-pair {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  align-items: stretch;
  min-width: 0;
}

@media (max-width: 640px) {
  .drop-row-pair {
    grid-template-columns: 1fr;
  }
}

.drop-cell {
  min-width: 0;
}

.drop-cell :deep(.slot-wrap) {
  margin-bottom: 0;
}

.placeholder {
  font-size: 12px;
  color: #bfbfbf;
  text-align: center;
  padding: 2px 4px;
}

.placed-card {
  width: 100%;
}

.placed-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.placed-name {
  font-weight: 600;
  font-size: 13px;
  color: #262626;
  min-width: 0;
  overflow-wrap: anywhere;
}

.placed-meta {
  font-size: 11px;
  color: #8c8c8c;
  margin-top: 4px;
  font-family: ui-monospace, monospace;
  word-break: break-all;
}

.tag-area {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-width: 0;
  max-width: 100%;
}
</style>
