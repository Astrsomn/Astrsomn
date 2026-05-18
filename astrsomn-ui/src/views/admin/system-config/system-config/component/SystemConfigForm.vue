<template>
  <AstModal
      :body-height="'auto'"
      :confirm-loading="confirmLoading"
      :confirm-text="mode === 'create' ? '创建' : '保存'"
      :max-width="'90vw'"
      :open="open"
      :width="'600px'"
      @cancel="$emit('cancel')"
      @confirm="handleSubmit"
      @update:open="$emit('update:open', $event)"
  >
    <template #header-title>{{ mode === 'create' ? '新增配置' : '编辑配置' }}</template>
    <template #header-subtitle>{{ mode === 'create' ? '创建新的系统配置项' : '修改系统配置项' }}</template>

    <a-form
        ref="formRef"
        :model="formData"
        class="config-form"
        layout="vertical"
    >
      <a-form-item :required="true" label="配置 Key">
        <a-input
            v-model:value="formData.configKey"
            :disabled="mode === 'edit'"
            :maxlength="128"
            placeholder="请输入配置 Key"
        />
      </a-form-item>

      <a-form-item :required="true" label="配置分组">
        <a-input
            v-model:value="formData.configGroup"
            :maxlength="64"
            placeholder="请输入配置分组"
        />
      </a-form-item>

      <a-form-item :required="true" label="配置值">
        <a-textarea
            v-model:value="formData.configValue"
            :maxlength="2000"
            :rows="4"
            placeholder="请输入配置值"
        />
      </a-form-item>

      <a-form-item :required="true" label="状态">
        <a-select v-model:value="formData.status" placeholder="请选择状态">
          <a-select-option value="ENABLED">启用</a-select-option>
          <a-select-option value="DISABLED">禁用</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="描述">
        <a-textarea
            v-model:value="formData.description"
            :maxlength="256"
            :rows="2"
            placeholder="请输入配置描述"
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
