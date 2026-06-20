<template>
  <AstModal
      :open="open"
      :confirm-loading="confirmLoading"
      :confirm-text="mode === 'create' ? t.form.btnCreate : t.form.btnSave"
      :width="800"
      body-height="auto"
      main-padding="24px"
      max-body-height="85vh"
      @cancel="onCancel"
      @confirm="handleOk"
      @update:open="onCancel"
  >
    <template #header-title>{{ mode === 'create' ? t.form.createTitle : t.form.editTitle }}</template>
    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="user-form"
        layout="vertical"
    >
      <a-form-item :label="t.form.labelUsername" name="username">
        <a-input
            v-model:value="form.username"
            :disabled="mode === 'edit'"
            autocomplete="off"
            :placeholder="t.form.placeholderUsername"
        />
      </a-form-item>

      <a-form-item :label="mode === 'create' ? t.form.labelPassword : t.form.labelNewPassword" name="password">
        <a-input-password
            v-model:value="form.password"
            :placeholder="mode === 'create' ? t.form.placeholderPassword : t.form.placeholderPasswordEdit"
            autocomplete="new-password"
        />
      </a-form-item>

      <a-form-item :label="t.form.labelEmail" name="email">
        <a-input v-model:value="form.email" allow-clear :placeholder="t.form.placeholderEmail"/>
      </a-form-item>

      <a-form-item :label="t.form.labelRole" name="userRole">
        <a-select v-model:value="form.userRole" :options="roleOptions"/>
      </a-form-item>

      <a-form-item :label="t.form.labelEnvCode" name="envCode">
        <a-select
            v-model:value="form.envCode"
            :filter-option="filterEnvOption"
            :loading="envLoading"
            :options="envOptions"
            allow-clear
            :placeholder="t.form.placeholderEnvCode"
            show-search
        />
      </a-form-item>

      <a-alert
          class="form-tip"
          :message="t.form.tipMessage"
          show-icon
          type="warning"
      />
    </a-form>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import AstModal from '@/components/home/AstModal.vue'
import type {SystemUser} from '@/api/systemUser.ts'
import {type SystemEnv, systemEnvApi} from '@/api/systemEnv.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import {getDictionary} from '@/locales/dictionary/registry.ts'

const t = usePageTranslation('system-user')
const roleDict = getDictionary('system.user.role')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemUser | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemUser]
}>()

const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const roleOptions = computed(() => roleDict.order.map(key => ({
  label: roleDict.getLabel(key) ?? key,
  value: key
})))

const envLoading = ref(false)
const envOptions = ref<Array<{ label: string; value: string }>>([])

function filterEnvOption(input: string, option: { label?: string; value?: string }) {
  const q = String(input || '').toLowerCase()
  const label = String(option?.label || '').toLowerCase()
  const value = String(option?.value || '').toLowerCase()
  return label.includes(q) || value.includes(q)
}

async function loadEnvOptions() {
  envLoading.value = true
  try {
    const resp = await systemEnvApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {}
    })
    const list: SystemEnv[] = resp.list || []
    envOptions.value = list
        .filter((row) => row.envKey != null && String(row.envKey).trim() !== '')
        .map((row) => ({
          value: String(row.envKey).trim(),
          label: row.envName
              ? `${row.envName}（${row.envKey}）`
              : String(row.envKey)
        }))
  } catch {
    envOptions.value = []
  } finally {
    envLoading.value = false
  }
}

function emptyForm(): SystemUser {
  return {
    username: '',
    password: '',
    email: '',
    userRole: 'USER',
    envCode: ''
  }
}

const form = reactive<SystemUser>(emptyForm())

const rules = {
  username: [{required: true, message: t.value.form.validationUsername}],
  password: [
    {
      validator: async (_rule: unknown, value: string) => {
        if (props.mode === 'create' && !String(value || '').trim()) {
          return Promise.reject(new Error(t.value.form.validationPassword))
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  userRole: [{required: true, message: t.value.form.validationRole}]
}

function assignFromInitial(src: SystemUser) {
  Object.assign(form, emptyForm(), src)
  form.password = ''
  if (!form.userRole) {
    form.userRole = 'USER'
  }
}

watch(
    () => [open.value, props.initial] as const,
    ([isOpen, initial]) => {
      if (!isOpen) return
      void loadEnvOptions()
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
  const payload: SystemUser = {
    id: form.id,
    username: form.username,
    email: form.email,
    userRole: form.userRole,
    envCode: form.envCode || undefined
  }
  if (props.mode === 'create') {
    payload.password = form.password
  } else if (String(form.password || '').trim()) {
    payload.password = form.password
  }
  emit('submit', payload)
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.user-form {
  width: 100%;
}

.form-tip {
  margin-top: 8px;
}
</style>
