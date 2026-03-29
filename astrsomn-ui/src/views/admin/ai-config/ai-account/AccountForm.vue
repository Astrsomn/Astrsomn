<template>
  <a-modal
    :open="visible"
    :title="isEdit ? '编辑 AI 账号' : '新建 AI 账号'"
    :width="680"
    :confirm-loading="submitting"
    @ok="onSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form layout="vertical" :model="form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="Account Key" name="accountKey">
              <a-tooltip
                v-if="accountKeyImmutable"
                title="已有 AI 模型在同环境下引用该 Key，不可修改"
              >
                <a-input
                  v-model:value="form.accountKey"
                  placeholder="可选，留空则自动生成"
                  disabled
                />
              </a-tooltip>
              <a-input
                v-else
                v-model:value="form.accountKey"
                placeholder="可选，留空则自动生成"
                allow-clear
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item
              label="账号名称"
              name="accountName"
              :rules="[{ required: true, message: '请输入账号名称' }]"
            >
              <a-input v-model:value="form.accountName" placeholder="展示名称" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="API Key" name="apiKey">
              <a-input-password v-model:value="form.apiKey" placeholder="供应商 API Key" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="API Secret" name="apiSecret">
              <a-input-password v-model:value="form.apiSecret" placeholder="可选" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="账号额度（tokens）" name="accountTokens">
              <a-input-number
                v-model:value="form.accountTokens"
                class="w-full"
                :min="0"
                placeholder="可选"
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
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'

interface Props {
  visible: boolean
  record?: AiAccount
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'success': []
}>()

const loading = ref(false)
const submitting = ref(false)
/** 来自详情接口：被模型引用后不可改 Key */
const accountKeyImmutable = ref(false)

const isEdit = computed(() => props.record?.id !== undefined && props.record?.id !== null)

const form = reactive<AiAccount>({})

const handleCancel = () => {
  emit('update:visible', false)
}

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
    Object.assign(form, detail)
    accountKeyImmutable.value = detail.accountKeyImmutable === true
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.accountName?.trim()) {
    message.error('请输入账号名称')
    return
  }
  
  submitting.value = true
  try {
    const payload: AiAccount = { ...form }
    let msg: string
    if (isEdit.value) {
      msg = await aiAccountApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await aiAccountApi.create(payload)
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
        accountKeyImmutable.value = false
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
