<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增提示词' : '编辑提示词'"
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
      class="prompt-form"
    >
      <a-alert
        v-if="mode === 'create'"
        type="info"
        show-icon
        message="Prompt Key 可留空，后端将按环境与标题自动生成唯一 Key；首次创建为版本 1。"
        class="form-alert"
      />
      <a-alert
        v-else
        type="info"
        show-icon
        message="保存后将写入新版本（同一 Prompt Key 下版本号递增），可在列表中查看历史版本。"
        class="form-alert"
      />
      <div class="form-grid">
        <a-form-item label="Prompt Key" name="promptKey">
          <a-input
            v-model:value="form.promptKey"
            placeholder="留空则服务端生成"
            :disabled="mode === 'edit'"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="标题" name="promptTitle">
          <a-input v-model:value="form.promptTitle" placeholder="展示名称" />
        </a-form-item>

        <a-form-item label="场景" name="scene">
          <a-input v-model:value="form.scene" placeholder="可选，场景分类" allow-clear />
        </a-form-item>

        <a-form-item v-if="mode === 'edit'" label="当前版本（只读）">
          <a-input :value="String(form.version ?? '—')" disabled />
        </a-form-item>

        <a-form-item label="启用状态" name="enabledFlag">
          <a-select v-model:value="form.enabledFlag" :options="enabledOptions" />
        </a-form-item>

        <a-form-item label="提示词内容" name="promptContent" class="span-2">
          <a-textarea
            v-model:value="form.promptContent"
            :auto-size="{ minRows: 14, maxRows: 32 }"
            placeholder="系统 / 用户提示词正文"
            class="content-area"
          />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiPrompt } from '@/api/aiPrompt.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiPrompt | null
}>()

const emit = defineEmits<{
  submit: [payload: AiPrompt]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const enabledOptions = [
  { label: '启用', value: 'enabled' },
  { label: '停用', value: 'disabled' }
]

function emptyForm(): AiPrompt {
  return {
    promptKey: '',
    promptTitle: '',
    promptContent: '',
    scene: '',
    enabledFlag: 'enabled'
  }
}

const form = reactive<AiPrompt>(emptyForm())

const rules = {
  promptTitle: [{ required: true, message: '请输入标题' }],
  promptContent: [{ required: true, message: '请输入提示词内容' }],
  enabledFlag: [{ required: true, message: '请选择启用状态' }]
}

function assignFromInitial(src: AiPrompt) {
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
  const payload = { ...form }
  if (props.mode === 'create' && !String(payload.promptKey || '').trim()) {
    delete payload.promptKey
  }
  emit('submit', payload)
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.prompt-form {
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

.w-full {
  width: 100%;
}

.content-area {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
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
