<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增 MCP' : '编辑 MCP'"
    width="900px"
    :confirm-loading="confirmLoading"
    :body-style="{ maxHeight: '78vh', overflowY: 'auto' }"
    @ok="handleOk"
    @cancel="onCancel"
  >
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="mcp-form"
    >
      <a-alert
        v-if="mode === 'create'"
        type="info"
        show-icon
        message="MCP Key 可留空，后端将按环境与名称自动生成。"
        class="form-alert"
      />
      <div class="form-grid">
        <a-form-item label="MCP Key" name="mcpKey">
          <a-input
            v-model:value="form.mcpKey"
            placeholder="留空则服务端生成"
            :disabled="mode === 'edit'"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="服务名称" name="serverName">
          <a-input v-model:value="form.serverName" placeholder="展示名称" />
        </a-form-item>

        <a-form-item label="类型" name="type">
          <a-select v-model:value="form.type" :options="mcpTypeOptions" placeholder="SSE / STDIO / STEAMABLE" />
        </a-form-item>

        <a-form-item label="启用" name="enabled">
          <a-switch v-model:checked="enabledChecked" />
          <span class="switch-hint">{{ form.enabled === 1 ? '启用' : '停用' }}</span>
        </a-form-item>

        <a-form-item label="描述" name="description" class="span-2">
          <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 4 }" placeholder="可选" />
        </a-form-item>

        <a-form-item label="SSE 地址" name="sseAddress" class="span-2">
          <a-input v-model:value="form.sseAddress" placeholder="type 为 SSE 时填写" />
        </a-form-item>

        <a-form-item label="请求头（JSON）" name="requestHeaderConfig" class="span-2">
          <a-textarea
            v-model:value="form.requestHeaderConfig"
            :auto-size="{ minRows: 2, maxRows: 6 }"
            placeholder='例如 {"Authorization":"Bearer xxx"}'
            class="mono"
          />
        </a-form-item>

        <a-divider orientation="left">STDIO 等</a-divider>

        <a-form-item label="启动命令" name="command">
          <a-input v-model:value="form.command" placeholder="如 npx、node" />
        </a-form-item>

        <a-form-item label="参数" name="args" class="span-2">
          <a-textarea
            v-model:value="form.args"
            :auto-size="{ minRows: 2, maxRows: 4 }"
            placeholder="JSON 数组或空格分隔"
            class="mono"
          />
        </a-form-item>

        <a-form-item label="环境变量（JSON）" name="envVars" class="span-2">
          <a-textarea
            v-model:value="form.envVars"
            :auto-size="{ minRows: 2, maxRows: 6 }"
            placeholder='例如 {"API_KEY":"..."}'
            class="mono"
          />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiMcp } from '@/api/aiMcp.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiMcp | null
}>()

const emit = defineEmits<{
  submit: [payload: AiMcp]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const mcpTypeOptions = [
  { label: 'SSE', value: 'SSE' },
  { label: 'STDIO', value: 'STDIO' },
  { label: 'STEAMABLE', value: 'STEAMABLE' }
]

function emptyForm(): AiMcp {
  return {
    mcpKey: '',
    serverName: '',
    description: '',
    type: 'SSE',
    sseAddress: '',
    requestHeaderConfig: '',
    enabled: 1,
    command: '',
    args: '',
    envVars: ''
  }
}

const form = reactive<AiMcp>(emptyForm())

const enabledChecked = computed({
  get: () => form.enabled === 1,
  set: (v: boolean) => {
    form.enabled = v ? 1 : 0
  }
})

const rules = {
  serverName: [{ required: true, message: '请输入服务名称' }],
  type: [{ required: true, message: '请选择类型' }]
}

function assignFromInitial(src: AiMcp) {
  Object.assign(form, emptyForm(), src)
  if (form.enabled !== 0 && form.enabled !== 1) {
    form.enabled = form.enabled == null ? 1 : Number(form.enabled) === 0 ? 0 : 1
  }
}

watch(
  () => [open.value, props.initial] as const,
  ([isOpen, initial]) => {
    if (!isOpen) return
    if (initial && Object.keys(initial).length > 0) {
      assignFromInitial(initial)
    } else {
      Object.assign(form, emptyForm())
    }
  }
)

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return Promise.reject(new Error('validation'))
  }
  const payload: AiMcp = { ...form }
  if (props.mode === 'create' && !String(payload.mcpKey || '').trim()) {
    delete payload.mcpKey
  }
  emit('submit', payload)
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.mcp-form {
  margin-top: 4px;
}

.form-alert {
  margin-bottom: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 16px;
}

.span-2 {
  grid-column: span 2;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 13px;
}

.switch-hint {
  margin-left: 10px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 13px;
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: auto;
  }
}
</style>
