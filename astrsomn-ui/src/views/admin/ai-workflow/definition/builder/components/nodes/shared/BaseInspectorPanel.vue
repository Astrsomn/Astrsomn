<template>
  <div>
    <p class="node-type-tip">
      {{ selectedNodeDefinition?.title || selectedNode.type }} ·
      {{ selectedNodeDefinition?.description || "标准节点配置" }}
    </p>
    <a-form layout="vertical">
      <a-form-item label="节点名称">
        <a-input :value="selectedNode.data?.label || ''" @update:value="onUpdateLabel" />
      </a-form-item>
      <a-form-item label="节点说明">
        <a-textarea
          :value="selectedNode.data?.description || ''"
          :rows="4"
          @update:value="onUpdateDescription"
        />
      </a-form-item>
      <a-form-item
        v-for="field in selectedNodeDefinition?.configSchema || []"
        :key="field.key"
        :label="field.label"
        :required="!!field.required"
      >
        <a-input
          v-if="field.component === 'input'"
          :value="getConfigValue(field.key)"
          :placeholder="field.placeholder"
          @update:value="(value) => updateNodeConfig(field.key, value)"
        />
        <a-textarea
          v-else-if="field.component === 'textarea'"
          :value="getConfigValue(field.key)"
          :placeholder="field.placeholder"
          :rows="3"
          @update:value="(value) => updateNodeConfig(field.key, value)"
        />
        <a-input-number
          v-else-if="field.component === 'number'"
          :value="Number(getConfigValue(field.key) || 0)"
          class="full-width"
          @update:value="(value) => updateNodeConfig(field.key, String(value ?? ''))"
        />
        <a-select
          v-else-if="field.component === 'select'"
          :value="getConfigValue(field.key)"
          :options="field.options || []"
          @update:value="(value) => updateNodeConfig(field.key, String(value))"
        />
        <a-switch
          v-else-if="field.component === 'switch'"
          :checked="getConfigValue(field.key) === 'true'"
          @update:checked="(value) => updateNodeConfig(field.key, String(value))"
        />
        <div v-if="field.component === 'input' || field.component === 'textarea'" class="var-assist">
          <span class="assist-label">变量:</span>
          <a-tag
            v-for="refName in variableRefs"
            :key="`${field.key}-${refName}`"
            class="ref-tag"
            @click="appendVariableRef(field.key, refName)"
          >
            {{ refName }}
          </a-tag>
        </div>
      </a-form-item>
    </a-form>

    <div class="io-spec">
      <h5>输入锚点</h5>
      <div class="io-list">
        <span v-for="item in inputPorts" :key="item.handleId" class="io-tag in-tag">
          {{ item.handleId }}: {{ item.valueType }}
        </span>
      </div>
      <h5>输出锚点</h5>
      <div class="io-list">
        <span v-for="item in outputPorts" :key="item.handleId" class="io-tag out-tag">
          {{ item.handleId }}: {{ item.valueType }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { getNodeDefinition } from '../../../domain/node-data-factory'
import type { WorkflowNode } from '../../../domain/types'

const props = defineProps<{
  selectedNode: WorkflowNode
  allNodes: WorkflowNode[]
}>()

const emit = defineEmits<{
  'update-node': [payload: { label?: string; description?: string; config?: Record<string, unknown> }]
}>()

const selectedNodeType = computed(() => props.selectedNode?.type)
const selectedNodeDefinition = computed(() => getNodeDefinition(String(selectedNodeType.value || '')))

const getConfigValue = (key: string) => {
  const config = props.selectedNode?.data?.config
  return config && typeof config === 'object' ? String((config as Record<string, unknown>)[key] ?? '') : ''
}

const updateNodeConfig = (key: string, value: string) => {
  emit('update-node', { config: { [key]: value } })
}

const onUpdateLabel = (value: string) => emit('update-node', { label: value })
const onUpdateDescription = (value: string) => emit('update-node', { description: value })

const inputPorts = computed(() => props.selectedNode?.data?.inputs ?? [])
const outputPorts = computed(() => props.selectedNode?.data?.outputs ?? [])
const variableRefs = computed(() => {
  const refs: string[] = ['{{start.query}}']
  props.allNodes.forEach((node) => {
    ;(node.data?.outputs || []).forEach((output) => {
      refs.push(`{{${node.id}.${output.handleId}}}`)
    })
  })
  return Array.from(new Set(refs)).slice(0, 10)
})

const appendVariableRef = (fieldKey: string, refPath: string) => {
  const current = getConfigValue(fieldKey)
  updateNodeConfig(fieldKey, `${current}${current ? ' ' : ''}${refPath}`)
}
</script>

<style scoped>
.node-type-tip {
  margin: 6px 0 10px;
  color: #64748b;
  font-size: 12px;
}

.io-spec {
  margin-top: 14px;
  border-top: 1px dashed #dbe3ee;
  padding-top: 10px;
}

.io-spec h5 {
  margin: 0 0 6px;
  color: #334155;
}

.io-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.io-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
}

.in-tag {
  background: #eaf3ff;
  color: #1d4ed8;
}

.out-tag {
  background: #e9fbf3;
  color: #047857;
}

.full-width {
  width: 100%;
}

.var-assist {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.assist-label {
  color: #64748b;
  font-size: 12px;
}

.ref-tag {
  cursor: pointer;
  user-select: none;
}
</style>

