<template>
  <AstDrawer
      :open="visible"
      :width="560"
      @update:open="emit('update:visible', $event)"
  >
    <template #title>{{ isEdit ? t.form.titleEdit : t.form.titleCreate }}</template>
    <template #subtitle>{{ t.form.subtitle }}</template>

    <a-spin :spinning="loading">


      <a-form :model="form" layout="vertical">
        <a-form-item :label="t.form.labelAccountKey" name="accountKey">
          <AstKeyGenerator
              v-model="form.accountKey"
              :disabled="accountKeyImmutable"
              :placeholder="accountKeyImmutable ? '' : t.form.placeholderAccountKey"
              :prefix="AI_ACCOUNT_KEY_PREFIX"
          />
        </a-form-item>

        <a-form-item
            :rules="[{ required: true, message: t.form.validationSelectProvider }]"
            :label="t.form.labelExtensionCode"
            name="extensionCode"
        >
          <ExtensionSelector
              v-model:value="form.extensionCode"
              :allow-clear="true"
              :only-applied="true"
              :placeholder="t.form.placeholderExtensionCode"
              size="middle"
          />
        </a-form-item>
        <a-form-item :label="t.form.labelStatus" name="status">
          <a-segmented
              v-model:value="form.status"
              :options="statusOptions"
              block
              class="status-segmented"
              size="large"
          />
        </a-form-item>
        <a-form-item
            :rules="[{ required: true, message: t.form.validationAccountName }]"
            :label="t.form.labelAccountName"
            name="accountName"
        >
          <a-input v-model:value="form.accountName" allow-clear :placeholder="t.form.placeholderAccountName"/>
        </a-form-item>


        <a-form-item :label="t.form.labelApiUrl" name="apiUrl">
          <a-input
              v-model:value="form.apiUrl"
              allow-clear
              :placeholder="t.form.placeholderApiUrl"
          />
        </a-form-item>

        <a-form-item :label="t.form.labelApiKey" name="apiKey">
          <a-input-password v-model:value="form.apiKey" :placeholder="t.form.placeholderApiKey"/>
        </a-form-item>

        <a-form-item :label="t.form.labelApiSecret" name="apiSecret">
          <a-input-password v-model:value="form.apiSecret" :placeholder="t.form.placeholderApiSecret"/>
        </a-form-item>

        <a-form-item :label="t.form.labelAccountTokens" name="accountTokens">
          <a-input-number v-model:value="form.accountTokens" :min="0" class="w-full" :placeholder="t.form.placeholderAccountTokens"/>
        </a-form-item>


      </a-form>

    </a-spin>

    <template #footer>
      <a-button @click="handleCancel">{{ t.form.btnCancel }}</a-button>
      <a-button :loading="submitting" type="primary" @click="onSubmit">
        {{ isEdit ? t.form.btnSubmitSave : t.form.btnSubmitCreate }}
      </a-button>
    </template>
  </AstDrawer>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import AstDrawer from '@/components/home/AstDrawer.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import {type AiAccount, aiAccountApi} from '@/api/aiAccount.ts'
import {AI_ACCOUNT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-account')

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

const statusOptions = computed(() => [
  {label: t.value.form.statusEnabled, value: 'enabled'},
  {label: t.value.form.statusDisabled, value: 'disabled'}
])

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
    message.error(e?.message || t.value.form.errorLoadDetail)
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.accountName?.trim()) {
    message.error(t.value.form.errorAccountNameRequired)
    return
  }
  submitting.value = true
  try {
    const payload = {...form}
    if (!isEdit.value) delete payload.id
    const msg = isEdit.value ? await aiAccountApi.update(payload) : await aiAccountApi.create(payload)
    message.success(msg || t.value.form.successOperation)
    emit('update:visible', false)
    emit('success')
  } catch (e: any) {
    message.error(e?.message || t.value.form.errorSubmit)
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {

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