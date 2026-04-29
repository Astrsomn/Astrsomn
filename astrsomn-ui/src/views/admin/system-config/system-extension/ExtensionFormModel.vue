<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增系统扩展' : '编辑系统扩展'"
    width="760px"
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
      class="extension-form"
    >
      <div class="status-row">
        <a-tag :color="statusTagColor(form.status)">{{ statusLabel(form.status) }}</a-tag>
        <a-tag :color="appliedTagColor(form.applied)">{{ appliedLabel(form.applied) }}</a-tag>
      </div>

      <a-alert
        v-if="isApplied"
        type="warning"
        show-icon
        class="form-tip"
        message="当前处于已应用状态。若修改插件包信息，建议先执行卸载并确保 jarName 与实际加载内容一致。"
      />

      <div class="form-grid">
        <a-form-item label="扩展 Key" name="extensionKey">
          <a-input
            v-model:value="form.extensionKey"
            placeholder="唯一标识，例如 qwen-plugin"
            :disabled="mode === 'edit'"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="扩展名称" name="extensionName">
          <a-input v-model:value="form.extensionName" placeholder="展示名称，例如 Qwen 插件" autocomplete="off" />
        </a-form-item>

        <a-form-item label="类型" name="type">
          <a-input v-model:value="form.type" placeholder="插件类型，例如 PROVIDER / TOOLS" autocomplete="off" />
        </a-form-item>

        <a-form-item label="版本" name="version">
          <a-input v-model:value="form.version" placeholder="可选，例如 1.0.0" autocomplete="off" />
        </a-form-item>

        <a-form-item label="作者" name="author">
          <a-input v-model:value="form.author" placeholder="可选" autocomplete="off" />
        </a-form-item>

        <a-form-item label="jarName" name="jarName">
          <a-input v-model:value="form.jarName" placeholder="插件 jar 文件名，例如 xxx-extension.jar" :disabled="isApplied" autocomplete="off" />
        </a-form-item>

        <a-form-item label="厂商 code" name="extensionCode">
          <a-input
            v-model:value="form.extensionCode"
            placeholder="公用：如 deepseek（模型扩展用于同步 AI_MODEL；可选，默认同 extensionKey）"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="描述" name="description" class="span-2">
          <a-textarea
            v-model:value="form.description"
            :auto-size="{ minRows: 3, maxRows: 7 }"
            placeholder="说明插件用途，可选"
          />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { SystemExtension } from '@/api/systemExtension'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemExtension | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemExtension]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

function emptyForm(): SystemExtension {
  return {
    extensionKey: '',
    extensionName: '',
    type: '',
    version: '',
    author: '',
    description: '',
    jarName: '',
    extensionCode: '',
    applied: '',
    status: ''
  }
}

const form = reactive<SystemExtension>(emptyForm())

const rules = {
  extensionKey: [{ required: true, message: '请输入扩展 Key' }],
  extensionName: [{ required: true, message: '请输入扩展名称' }],
  type: [{ required: true, message: '请输入扩展类型' }],
  jarName: [{ required: true, message: '请输入 jarName（插件包文件名）' }]
}

function assignFromInitial(src: SystemExtension) {
  Object.assign(form, emptyForm(), src)
}

const statusLabel = (value: string | undefined) => {
  if (value === 'INSTALLED') return '已安装'
  if (value === 'APPLIED') return '已应用'
  if (value === 'UNINSTALLED') return '未安装'
  return value ?? '—'
}

const statusTagColor = (value: string | undefined) => {
  if (value === 'APPLIED') return 'green'
  if (value === 'INSTALLED') return 'blue'
  if (value === 'UNINSTALLED') return 'red'
  return 'default'
}

const appliedLabel = (value: string | undefined) => {
  if (value === 'Y') return '已应用'
  if (value === 'N') return '未应用'
  return value ?? '—'
}

const appliedTagColor = (value: string | undefined) => {
  if (value === 'Y') return 'green'
  return 'default'
}

const isApplied = computed(() => form.status === 'APPLIED' || form.applied === 'Y')

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
.extension-form {
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

.form-tip {
  margin-top: 10px;
  margin-bottom: 12px;
}

.status-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

@media (max-width: 860px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .span-2 {
    grid-column: auto;
  }
}
</style>

