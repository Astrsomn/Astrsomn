<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增系统配置' : '编辑系统配置'"
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
      class="config-form"
    >
      <div class="form-grid">
        <a-form-item label="配置 Key" name="configKey">
          <a-input
            v-model:value="form.configKey"
            placeholder="例如 email.smtp.host"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="配置分组" name="configGroup">
          <a-input
            v-model:value="form.configGroup"
            placeholder="例如 EMAIL / STORAGE / LLM_GATEWAY"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-select v-model:value="form.status" :options="statusOptions" />
        </a-form-item>

        <a-form-item label="系统内置" name="isSystem">
          <a-switch
            :checked="Boolean(form.isSystem)"
            checked-children="是"
            un-checked-children="否"
            @change="(checked) => form.isSystem = checked"
          />
        </a-form-item>

        <a-form-item label="配置值" name="configValue" class="span-2">
          <a-textarea
            v-model:value="form.configValue"
            :auto-size="{ minRows: 4, maxRows: 8 }"
            placeholder="填写配置内容"
          />
        </a-form-item>

        <a-form-item label="描述" name="description" class="span-2">
          <a-textarea
            v-model:value="form.description"
            :auto-size="{ minRows: 2, maxRows: 6 }"
            placeholder="说明配置用途，可选"
          />
        </a-form-item>
      </div>

      <a-alert
        type="warning"
        show-icon
        class="form-tip"
        message="建议避免在页面直接录入真实密钥；如需录入敏感值，请确认当前环境具备相应保护策略。"
      />
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { SystemConfig } from '@/api/systemConfig.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemConfig | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemConfig]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' }
]

function emptyForm(): SystemConfig {
  return {
    configKey: '',
    configValue: '',
    configGroup: '',
    description: '',
    isSystem: false,
    status: 'ENABLED'
  }
}

const form = reactive<SystemConfig>(emptyForm())

const rules = {
  configKey: [{ required: true, message: '请输入配置 Key' }],
  configValue: [{ required: true, message: '请输入配置值' }],
  status: [{ required: true, message: '请选择状态' }]
}

function assignFromInitial(src: SystemConfig) {
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
  emit('submit', {
    ...form,
    isSystem: Boolean(form.isSystem)
  })
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.config-form {
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
  margin-top: 8px;
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
