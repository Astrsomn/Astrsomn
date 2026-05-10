<template>
  <AstrsomnDrawerShell
    :open="visible"
    :width="560"
    @update:open="emit('update:visible', $event)"
  >
    <template #title>{{ isEdit ? '编辑账号' : '新建账号' }}</template>
    <template #subtitle>AI Account 配置</template>

    <a-spin :spinning="loading">



        <a-form layout="vertical" :model="form">
          <a-form-item label="账号 Key" name="accountKey">
            <AstrsomnKeyGenerator
              v-model="form.accountKey"
              :prefix="AI_ACCOUNT_KEY_PREFIX"
              :placeholder="accountKeyImmutable ? '' : '系统自动生成'"
              :disabled="accountKeyImmutable"
            />
          </a-form-item>

          <a-form-item
              label="模型供应商"
              name="extensionCode"
              :rules="[{ required: true, message: '请选择供应商' }]"
          >
            <ExtensionSelector
                v-model:value="form.extensionCode"
                placeholder="请选择供应商"
                :allow-clear="true"
                size="middle"
                :only-applied="true"
            />
          </a-form-item>
          <a-form-item label="启用状态" name="status">
            <a-segmented
                v-model:value="form.status"
                :options="[{label:'已启用', value:'enabled'}, {label:'已禁用', value:'disabled'}]"
                block
                size="large"
                class="status-segmented"
            />
          </a-form-item>
          <a-form-item
            label="展示名称"
            name="accountName"
            :rules="[{ required: true, message: '请定义凭证展示名称' }]"
          >
            <a-input v-model:value="form.accountName" placeholder="请输入账号名称" allow-clear />
          </a-form-item>



          <a-form-item label="API URL" name="apiUrl">
            <a-input
              v-model:value="form.apiUrl"
              placeholder="例如：https://api.openai.com/v1"
              allow-clear
            />
          </a-form-item>

          <a-form-item label="API Key" name="apiKey">
            <a-input-password v-model:value="form.apiKey" placeholder="请输入 API Key" />
          </a-form-item>

          <a-form-item label="API Secret" name="apiSecret">
            <a-input-password v-model:value="form.apiSecret" placeholder="请输入 Secret 密钥" />
          </a-form-item>

          <a-form-item label="消耗上限 (Tokens)" name="accountTokens">
            <a-input-number v-model:value="form.accountTokens" :min="0" placeholder="无限制" class="w-full" />
          </a-form-item>


        </a-form>

    </a-spin>

    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" :loading="submitting" @click="onSubmit">
        {{ isEdit ? '保存' : '创建' }}
      </a-button>
    </template>
  </AstrsomnDrawerShell>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import AstrsomnDrawerShell from '@/components/home/AstrsomnDrawerShell.vue'
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selectors/ExtensionSelector.vue'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'
import { AI_ACCOUNT_KEY_PREFIX } from '@/constants/aiConfigKeyPrefixes'

interface Props {
  visible: boolean
  record?: AiAccount
}

const props = defineProps<Props>()
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const submitting = ref(false)
const accountKeyImmutable = ref(false)
const isEdit = computed(() => !!props.record?.id)

const form = reactive<AiAccount>({
  accountKey: '',
  accountName: '',
  extensionCode: undefined,
  apiUrl: '',
  apiKey: '',
  apiSecret: '',
  accountTokens: undefined,
  status: 'enabled'
})

const handleCancel = () => emit('update:visible', false)

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.assign(form, detail, {
      provider: detail.provider || undefined,
      apiUrl: detail.apiUrl || '',
      status: detail.status || 'enabled'
    })
    accountKeyImmutable.value = detail.accountKeyImmutable === true
  } catch (e: any) {
    message.error(e?.message || '详情加载失败')
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.accountName?.trim()) {
    message.error('请填写账户名称')
    return
  }
  submitting.value = true
  try {
    const payload = { ...form }
    if (!isEdit.value) delete payload.id
    const msg = isEdit.value ? await aiAccountApi.update(payload) : await aiAccountApi.create(payload)
    message.success(msg || '操作成功')
    emit('update:visible', false)
    emit('success')
  } catch (e: any) {
    message.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    // 先清空表单，避免数据残留
    Object.assign(form, {
      id: undefined,
      accountKey: '',
      accountName: '',
      provider: undefined,
      apiUrl: '',
      apiKey: '',
      apiSecret: '',
      accountTokens: undefined,
      status: 'enabled'
    })
    accountKeyImmutable.value = false
    
    if (props.record?.id) {
      loadDetail(props.record.id)
    }
  }
})
</script>

<style scoped>


.key-alert {
  margin-bottom: 16px;
}

.w-full {
  width: 100%;
}
</style>