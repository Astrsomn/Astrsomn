<template>
  <aside class="node-inspector">
    <div class="panel-head">
      <h3>属性面板</h3>
      <a-button size="small" danger :disabled="!selectedNode && !selectedEdge" @click="$emit('remove-selection')">
        删除所选
      </a-button>
    </div>

    <a-form layout="vertical" class="meta-form">
      <a-form-item label="流程名称" required>
        <a-input v-model:value="workflowMeta.workflowName" maxlength="128" />
      </a-form-item>
      <a-form-item label="Flow Key" required>
        <a-input v-model:value="workflowMeta.workflowKey" maxlength="128" />
      </a-form-item>
      <a-form-item label="业务分类">
        <a-input v-model:value="workflowMeta.description" maxlength="128" />
      </a-form-item>
    </a-form>

    <div v-if="selectedNode" class="selection-block">
      <h4>节点配置</h4>
      <a-form layout="vertical">
        <a-form-item label="节点名称">
          <a-input
            :value="selectedNode.data?.label || ''"
            @update:value="(value) => $emit('update-node', { label: value })"
          />
        </a-form-item>
        <a-form-item label="节点说明">
          <a-textarea
            :value="selectedNode.data?.description || ''"
            :rows="4"
            @update:value="(value) => $emit('update-node', { description: value })"
          />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'llm'" label="模型 Key">
          <a-input :value="getConfigValue('modelKey')" @update:value="(value) => updateNodeConfig('modelKey', value)" />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'llm'" label="温度">
          <a-input :value="getConfigValue('temperature')" @update:value="(value) => updateNodeConfig('temperature', value)" />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'tool'" label="工具 Key">
          <a-input :value="getConfigValue('toolKey')" @update:value="(value) => updateNodeConfig('toolKey', value)" />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'tool'" label="超时时间(ms)">
          <a-input :value="getConfigValue('timeoutMs')" @update:value="(value) => updateNodeConfig('timeoutMs', value)" />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'condition'" label="条件表达式">
          <a-textarea
            :rows="3"
            :value="getConfigValue('expression')"
            @update:value="(value) => updateNodeConfig('expression', value)"
          />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'condition'" label="true 分支标签">
          <a-input :value="getConfigValue('trueLabel')" @update:value="(value) => updateNodeConfig('trueLabel', value)" />
        </a-form-item>
        <a-form-item v-if="selectedNodeType === 'condition'" label="false 分支标签">
          <a-input :value="getConfigValue('falseLabel')" @update:value="(value) => updateNodeConfig('falseLabel', value)" />
        </a-form-item>
      </a-form>
    </div>

    <div v-else-if="selectedEdge" class="selection-block">
      <h4>连线配置</h4>
      <a-form layout="vertical">
        <a-form-item label="连线标签">
          <a-input
            :value="String(selectedEdge.label || '')"
            @update:value="(value) => $emit('update-edge', { label: value })"
          />
        </a-form-item>
      </a-form>
    </div>

    <div v-else class="empty-hint">
      未选择节点或连线。可在画布中点击元素后在此配置。
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { WorkflowEdge, WorkflowMeta, WorkflowNode } from '../types'

const props = defineProps<{
  workflowMeta: WorkflowMeta
  selectedNode?: WorkflowNode
  selectedEdge?: WorkflowEdge
}>()

const emit = defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
  'update-edge': [payload: { label?: string }]
  'remove-selection': []
}>()

const selectedNodeType = computed(() => props.selectedNode?.type)

const getConfigValue = (key: string) => {
  const config = props.selectedNode?.data?.config
  return config && typeof config === 'object' ? String((config as Record<string, unknown>)[key] ?? '') : ''
}

const updateNodeConfig = (key: string, value: string) => {
  emit('update-node', { config: { [key]: value } })
}
</script>

<style scoped>
.node-inspector {
  height: 100%;
  border: 1px solid #edf1f6;
  border-radius: 12px;
  background: #fff;
  padding: 12px;
  overflow: auto;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.panel-head h3,
.selection-block h4 {
  margin: 0;
  color: #1e293b;
}

.meta-form {
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 12px;
}

.selection-block {
  margin-top: 10px;
}

.empty-hint {
  color: #64748b;
  font-size: 12px;
  padding: 8px 0;
}
</style>
