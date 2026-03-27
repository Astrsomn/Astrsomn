<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增 FTL/ST 模板' : '编辑 FTL/ST 模板'"
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
      class="template-form"
    >
      <div class="form-grid">
        <a-form-item label="Template Key" name="templateKey">
          <a-input
            v-model:value="form.templateKey"
            placeholder="同一逻辑多版本共用的 Key"
            :disabled="mode === 'edit'"
          />
        </a-form-item>

        <a-form-item label="标题" name="templateTitle">
          <a-input v-model:value="form.templateTitle" placeholder="展示名称" />
        </a-form-item>

        <a-form-item label="分类" name="category">
          <a-input v-model:value="form.category" placeholder="可选" allow-clear />
        </a-form-item>

        <a-form-item label="模板类型" name="templateType">
          <a-select
            v-model:value="form.templateType"
            :options="templateTypeOptions"
            placeholder="选择引擎"
          />
        </a-form-item>

        <a-form-item label="版本号" name="version">
          <a-input-number
            v-model:value="form.version"
            :min="1"
            :precision="0"
            class="w-full"
            placeholder="默认 1"
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-select v-model:value="form.status" :options="statusOptions" />
        </a-form-item>

        <a-form-item label="模板内容" name="content" class="span-2">
          <a-textarea
            v-model:value="form.content"
            :auto-size="{ minRows: 12, maxRows: 28 }"
            placeholder="Freemarker / StringTemplate 正文"
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
import type { AiTemplate } from '@/api/aiTemplate.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiTemplate | null
}>()

const emit = defineEmits<{
  submit: [payload: AiTemplate]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const statusOptions = [
  { label: 'Enabled', value: 'enabled' },
  { label: 'Disabled', value: 'disabled' }
]

const templateTypeOptions = [
  { label: 'Freemarker (.ftl)', value: 'FREEMARKER' },
  { label: 'StringTemplate (.st)', value: 'STRING_TEMPLATE' }
]

function emptyForm(): AiTemplate {
  return {
    templateKey: '',
    templateTitle: '',
    content: '',
    category: '',
    templateType: 'FREEMARKER',
    version: 1,
    status: 'enabled'
  }
}

const form = reactive<AiTemplate>(emptyForm())

const rules = {
  templateKey: [{ required: true, message: '请输入 Template Key' }],
  templateTitle: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入模板内容' }],
  templateType: [{ required: true, message: '请选择模板类型' }],
  status: [{ required: true, message: '请选择状态' }]
}

function assignFromInitial(src: AiTemplate) {
  Object.assign(form, emptyForm(), src)
  const v = form.version
  if (v != null && v !== '') {
    const n = Number(v)
    form.version = Number.isFinite(n) ? n : 1
  } else {
    form.version = 1
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
  emit('submit', { ...form })
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.template-form {
  margin-top: 4px;
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
