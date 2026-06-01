<template>
  <a-modal
      v-model:open="open"
      :confirm-loading="confirmLoading"
      :title="mode === 'create' ? t.form.createTitle : t.form.editTitle"
      width="560px"
      @cancel="onCancel"
      @ok="handleOk"
  >
    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="env-form"
        layout="vertical"
    >
      <a-form-item :label="t.form.labelEnvName" name="envName">
        <a-input v-model:value="form.envName" :placeholder="t.form.placeholderEnvName"/>
      </a-form-item>

      <a-form-item :label="t.form.labelEnvKey" name="envKey">
        <a-input
            v-model:value="form.envKey"
            :disabled="mode === 'edit'"
            :placeholder="t.form.placeholderEnvKey"
        />
      </a-form-item>

      <a-form-item :label="t.form.labelDescription" name="description">
        <a-textarea v-model:value="form.description" :auto-size="{ minRows: 2, maxRows: 6 }" :placeholder="t.form.placeholderDescription"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {SystemEnv} from '@/api/systemEnv.ts'

const t = usePageTranslation('system-env')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemEnv | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemEnv]
}>()

const open = defineModel<boolean>('open', {required: true})

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
  envName: [{required: true, message: t.value.form.validationEnvName}],
  envKey: [{required: true, message: t.value.form.validationEnvKey}]
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
  emit('submit', {...form})
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
