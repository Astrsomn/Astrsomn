<template>
  <AstrsomnModal
    :open="open"
    :width="'600px'"
    :body-height="'auto'"
    :max-width="'90vw'"
    :closable="true"
  >
    <template #header-title>{{ mode === 'create' ? '新增配置' : '编辑配置' }}</template>
    <template #header-subtitle>{{ mode === 'create' ? '创建新的系统配置项' : '修改系统配置项' }}</template>

    <a-form
      ref="formRef"
      :model="formData"
      layout="vertical"
      class="config-form"
    >
      <a-form-item label="配置 Key" :required="true">
        <a-input
          v-model:value="formData.configKey"
          :disabled="mode === 'edit'"
          placeholder="请输入配置 Key"
          :maxlength="128"
        />
      </a-form-item>

      <a-form-item label="配置分组" :required="true">
        <a-input
          v-model:value="formData.configGroup"
          placeholder="请输入配置分组"
          :maxlength="64"
        />
      </a-form-item>

      <a-form-item label="配置值" :required="true">
        <a-textarea
          v-model:value="formData.configValue"
          placeholder="请输入配置值"
          :rows="4"
          :maxlength="2000"
        />
      </a-form-item>

      <a-form-item label="状态" :required="true">
        <a-select v-model:value="formData.status" placeholder="请选择状态">
          <a-select-option value="ENABLED">启用</a-select-option>
          <a-select-option value="DISABLED">禁用</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="描述">
        <a-textarea
          v-model:value="formData.description"
          placeholder="请输入配置描述"
          :rows="2"
          :maxlength="256"
        />
      </a-form-item>

      <div class="form-actions">
        <a-button @click="$emit('cancel')">取消</a-button>
        <a-button type="primary" :loading="confirmLoading" @click="handleSubmit">
          {{ mode === 'create' ? '创建' : '保存' }}
        </a-button>
      </div>
    </a-form>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import type { SystemConfig } from '@/api/systemConfig.ts'

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
  { immediate: true }
)

const handleSubmit = () => {
  const payload: SystemConfig = { ...formData }
  emit('submit', payload)
}
</script>

<style scoped>
.config-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
}

.form-actions :deep(.ant-btn) {
  height: 40px;
  padding: 0 24px;
  border-radius: 10px;
}
</style>
