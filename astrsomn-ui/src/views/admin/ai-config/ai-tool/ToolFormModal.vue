<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增工具' : '编辑工具'"
    width="880px"
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
      class="tool-form"
    >
      <a-alert
        v-if="mode === 'create'"
        type="info"
        show-icon
        message="Tool Key 可留空，后端将按环境与名称自动生成。"
        class="form-alert"
      />
      <div class="form-grid">
        <a-form-item label="Tool Key" name="toolKey">
          <a-input
            v-model:value="form.toolKey"
            placeholder="留空则服务端生成"
            :disabled="mode === 'edit'"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="工具名称" name="toolName">
          <a-input v-model:value="form.toolName" placeholder="展示名称" />
        </a-form-item>

        <a-form-item label="类型" name="type">
          <a-select v-model:value="form.type" :options="toolTypeOptions" placeholder="html / method" />
        </a-form-item>

        <a-form-item label="启用状态" name="enableFlag">
          <a-select v-model:value="form.enableFlag" :options="enableOptions" />
        </a-form-item>

        <a-form-item label="Spring Bean" name="beanName">
          <a-input v-model:value="form.beanName" placeholder="如 myToolBean" />
        </a-form-item>

        <a-form-item label="方法名" name="methodName">
          <a-input v-model:value="form.methodName" placeholder="如 execute" />
        </a-form-item>

        <a-form-item label="描述" name="description" class="span-2">
          <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 5 }" placeholder="可选" />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiTool } from '@/api/aiTool.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiTool | null
}>()

const emit = defineEmits<{
  submit: [payload: AiTool]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const toolTypeOptions = [
  { label: 'HTML', value: 'html' },
  { label: 'Method', value: 'method' }
]

const enableOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

function emptyForm(): AiTool {
  return {
    toolKey: '',
    toolName: '',
    description: '',
    beanName: '',
    methodName: '',
    type: 'method',
    enableFlag: 'enabled'
  }
}

const form = reactive<AiTool>(emptyForm())

const rules = {
  toolName: [{ required: true, message: '请输入工具名称' }],
  type: [{ required: true, message: '请选择类型' }],
  enableFlag: [{ required: true, message: '请选择启用状态' }]
}

function assignFromInitial(src: AiTool) {
  Object.assign(form, emptyForm(), src)
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
  const payload: AiTool = { ...form }
  if (props.mode === 'create' && !String(payload.toolKey || '').trim()) {
    delete payload.toolKey
  }
  emit('submit', payload)
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.tool-form {
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

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: auto;
  }
}
</style>
