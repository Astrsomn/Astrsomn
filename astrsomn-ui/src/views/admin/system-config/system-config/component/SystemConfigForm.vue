<template>
  <AstModal
      :body-height="'auto'"
      :confirm-loading="confirmLoading"
      :confirm-text="mode === 'create' ? t.form.btnCreate : t.form.btnSave"
      :max-width="'90vw'"
      :open="open"
      :width="'600px'"
      @cancel="$emit('cancel')"
      @confirm="handleSubmit"
      @update:open="$emit('update:open', $event)"
  >
    <template #header-title>{{ mode === 'create' ? t.form.createTitle : t.form.editTitle }}</template>
    <template #header-subtitle>{{ mode === 'create' ? t.form.createSubtitle : t.form.editSubtitle }}</template>

    <a-form
        ref="formRef"
        :model="formData"
        class="config-form"
        layout="vertical"
    >
      <a-form-item :required="true" :label="t.form.labelConfigKey">
        <a-input
            v-model:value="formData.configKey"
            :disabled="mode === 'edit'"
            :maxlength="128"
            :placeholder="t.form.placeholderConfigKey"
        />
      </a-form-item>

      <a-form-item :required="true" :label="t.form.labelConfigGroup">
        <a-input
            v-model:value="formData.configGroup"
            :maxlength="64"
            :placeholder="t.form.placeholderConfigGroup"
        />
      </a-form-item>

      <a-form-item :required="true" :label="t.form.labelConfigValue">
        <a-textarea
            v-model:value="formData.configValue"
            :maxlength="2000"
            :rows="4"
            :placeholder="t.form.placeholderConfigValue"
        />
      </a-form-item>

      <a-form-item :required="true" :label="t.form.labelStatus">
        <a-select v-model:value="formData.status" :placeholder="t.form.placeholderStatus">
          <a-select-option value="ENABLED">{{ t.form.optionEnabled }}</a-select-option>
          <a-select-option value="DISABLED">{{ t.form.optionDisabled }}</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t.form.labelDescription">
        <a-textarea
            v-model:value="formData.description"
            :maxlength="256"
            :rows="2"
            :placeholder="t.form.placeholderDescription"
        />
      </a-form-item>

    </a-form>
  </AstModal>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import AstModal from '@/components/home/AstModal.vue'
import type {SystemConfig} from '@/api/systemConfig.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('system-config')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemConfig | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemConfig]
}>()

const formRef = ref<FormInstance>()

const formData = reactive<SystemConfig>({
  configKey: '',
  configGroup: '',
  configValue: '',
  status: 'ENABLED',
  description: ''
})

watch(
    () => props.initial,
    (val) => {
      if (val) {
        Object.assign(formData, val)
      } else {
        formData.configKey = ''
        formData.configGroup = ''
        formData.configValue = ''
        formData.status = 'ENABLED'
        formData.description = ''
      }
    },
    {immediate: true}
)

const handleSubmit = () => {
  const payload: SystemConfig = {...formData}
  emit('submit', payload)
}
</script>

<style scoped>
.config-form {
  padding: 20px 0;
}

</style>
