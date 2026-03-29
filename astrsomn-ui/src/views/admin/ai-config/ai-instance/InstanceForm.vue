<template>
  <a-modal
    :open="visible"
    :title="isEdit ? '编辑推理实例' : '新建推理实例'"
    :width="820"
    :confirm-loading="submitting"
    @ok="onSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form layout="vertical" :model="form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="Instance Key" name="instanceKey">
              <a-input
                v-model:value="form.instanceKey"
                placeholder="可选，留空则自动生成"
                allow-clear
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="状态" name="status">
              <a-select
                v-model:value="form.status"
                :options="statusOptions"
                placeholder="请选择"
                allow-clear
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Max tokens" name="maxTokens">
              <a-input-number v-model:value="form.maxTokens" class="w-full" :min="0" placeholder="最大生成长度" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Temperature" name="temperature">
              <a-input-number
                v-model:value="form.temperature"
                class="w-full"
                :min="0"
                :max="2"
                :step="0.1"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Top P" name="topP">
              <a-input-number v-model:value="form.topP" class="w-full" :min="0" :max="1" :step="0.05" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Top K" name="topK">
              <a-input-number v-model:value="form.topK" class="w-full" :min="0" placeholder="整数" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Presence penalty" name="presencePenalty">
              <a-input-number v-model:value="form.presencePenalty" class="w-full" :step="0.1" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Frequency penalty" name="frequencyPenalty">
              <a-input-number v-model:value="form.frequencyPenalty" class="w-full" :step="0.1" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Seed" name="seed">
              <a-input-number v-model:value="form.seed" class="w-full" placeholder="可选，整数种子" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Dimensions" name="dimensions">
              <a-input-number v-model:value="form.dimensions" class="w-full" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Style" name="style">
              <a-input v-model:value="form.style" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="8">
            <a-form-item label="Size" name="size">
              <a-input v-model:value="form.size" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="Stop sequences" name="stopSequences">
              <a-textarea
                v-model:value="form.stopSequences"
                :rows="3"
                placeholder="多段结束符，可按后端约定使用 JSON 或分隔符"
                allow-clear
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance'

interface Props {
  visible: boolean
  record?: AiInstance
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'success': []
}>()

const loading = ref(false)
const submitting = ref(false)

const isEdit = computed(() => props.record?.id !== undefined && props.record?.id !== null)

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '禁用', value: 'disabled' }
]

const form = reactive<AiInstance>({})

const handleCancel = () => {
  emit('update:visible', false)
}

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiInstanceApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  submitting.value = true
  try {
    const payload: AiInstance = { ...form }
    let msg: string
    if (isEdit.value) {
      msg = await aiInstanceApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await aiInstanceApi.create(payload)
    }
    message.success(msg)
    emit('update:visible', false)
    emit('success')
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

watch(
  () => props.visible,
  (val) => {
    if (val) {
      if (props.record?.id) {
        void loadDetail(props.record.id)
      } else {
        Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
        form.status = 'enabled'
      }
    }
  }
)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
