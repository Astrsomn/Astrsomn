<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增环境' : '编辑环境'"
    width="560px"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    @cancel="onCancel"
  >
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="env-form"
    >
      <a-form-item label="环境名称" name="envName">
        <a-input v-model:value="form.envName" placeholder="展示名称" />
      </a-form-item>

      <a-form-item label="环境 Key" name="envKey">
        <a-input
          v-model:value="form.envKey"
          placeholder="如 DEV、SIT、UAT、PRO"
          :disabled="mode === 'edit'"
        />
      </a-form-item>

      <a-form-item label="描述" name="description">
        <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 6 }" placeholder="可选" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { SystemEnv } from '@/api/systemEnv.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemEnv | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemEnv]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

function emptyForm(): SystemEnv {
  return {
    envName: '',
    envKey: '',
    description: ''
  }
}

const form = reactive<SystemEnv>(emptyForm())

const rules = {
  envName: [{ required: true, message: '请输入环境名称' }],
  envKey: [{ required: true, message: '请输入环境 Key' }]
}

function assignFromInitial(src: SystemEnv) {
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
  emit('submit', { ...form })
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.env-form {
  margin-top: 4px;
}
</style>
