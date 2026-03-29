<template>
  <main class="assembly-canvas">
    <div class="canvas-top">
      <div class="preview-section">
        <div v-if="hasPreview" class="preview-flow">
          <template v-if="chatInstance"><span class="pill pill-chat">对话</span></template>
          <span v-if="chatInstance && (embeddingInstance || imageInstance)" class="arrow">→</span>
          <template v-if="embeddingInstance"><span class="pill pill-emb">向量</span></template>
          <span v-if="embeddingInstance && imageInstance" class="arrow">→</span>
          <template v-if="imageInstance"><span class="pill pill-img">图像</span></template>
          <span v-if="tools.length" class="pill pill-tool">工具 ×{{ tools.length }}</span>
          <span v-if="mcps.length" class="pill pill-mcp">MCP ×{{ mcps.length }}</span>
        </div>
        <div v-else class="preview-empty">拖拽两侧资源到下方槽位，链路预览将显示在此处</div>
      </div>
      <div class="canvas-actions">
        <a-tooltip title="重置">
          <a-button type="primary" shape="circle" class="canvas-action-btn" @click="emit('reset')">
            <template #icon><UndoOutlined /></template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="提交">
          <a-button type="primary" shape="circle" class="canvas-action-btn" @click="emit('submit')">
            <template #icon><SendOutlined /></template>
          </a-button>
        </a-tooltip>
      </div>
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
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ApiOutlined,
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
import type { AssemblyDragPayload, AssemblySlotKey } from './assemblyTypes'
import AssemblyDropZone from './AssemblyDropZone.vue'

const props = defineProps<{
  draggingPayload: AssemblyDragPayload | null
  activeDropKey: AssemblySlotKey | null
  chatInstance: AiInstance | null
  embeddingInstance: AiInstance | null
  imageInstance: AiInstance | null
  tools: AiTool[]
  mcps: AiMcp[]
}>()

const emit = defineEmits<{
  hover: [key: AssemblySlotKey | null]
  drop: [payload: AssemblyDragPayload]
  clear: [key: 'chatInstance' | 'embeddingInstance' | 'imageInstance']
  removeTool: [toolKey: string]
  removeMcp: [mcpKey: string]
  reset: []
  submit: []
}>()

function removeToolTag(key?: string) {
  if (key) emit('removeTool', key)
}

function removeMcpTag(key?: string) {
  if (key) emit('removeMcp', key)
}

const hasPreview = computed(
  () =>
    !!props.chatInstance ||
    !!props.embeddingInstance ||
    !!props.imageInstance ||
    props.tools.length > 0 ||
    props.mcps.length > 0
)
</script>

<style scoped>
.assembly-canvas {
  position: relative;
  border: 1px dashed #d9d9d9;
  border-radius: 16px;
  padding: 12px 12px 14px;
  background: linear-gradient(145deg, #fbfcff 0%, #f7f9fc 100%);
  min-height: calc(100vh - 100px);
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  min-width: 0;
}

.canvas-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-shrink: 0;
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.preview-section {
  flex: 1;
  min-width: 0;
}

.canvas-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  padding-top: 2px;
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

.preview-flow {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.preview-empty {
  font-size: 12px;
  color: #bfbfbf;
  text-align: center;
  padding: 6px 8px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.02);
}

.drop-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-height: 0;
  min-width: 0;
  overflow-y: auto;
  overflow-x: hidden;
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
  gap: 10px;
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

.drop-cell :deep(.assembly-drop-zone) {
  min-height: 120px;
  border-radius: 14px;
}

.placeholder {
  font-size: 12px;
  color: #bfbfbf;
  text-align: center;
  padding: 6px;
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
  gap: 6px;
  min-width: 0;
  max-width: 100%;
}

.pill {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.pill-chat {
  background: #1890ff;
}
.pill-emb {
  background: #722ed1;
}
.pill-img {
  background: #fa8c16;
}
.pill-tool {
  background: #52c41a;
}
.pill-mcp {
  background: #9254de;
}

.arrow {
  color: #bfbfbf;
  font-size: 12px;
}
</style>
