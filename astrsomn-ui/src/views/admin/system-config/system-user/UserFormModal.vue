<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增用户' : '编辑用户'"
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
      class="user-form"
    >
      <a-form-item label="用户名" name="username">
        <a-input
          v-model:value="form.username"
          placeholder="登录名"
          :disabled="mode === 'edit'"
          autocomplete="off"
        />
      </a-form-item>

      <a-form-item :label="mode === 'create' ? '密码' : '新密码（留空不修改）'" name="password">
        <a-input-password
          v-model:value="form.password"
          :placeholder="mode === 'create' ? '登录密码' : '不修改请留空'"
          autocomplete="new-password"
        />
      </a-form-item>

      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="form.email" placeholder="可选" allow-clear />
      </a-form-item>

      <a-form-item label="角色" name="userRole">
        <a-select v-model:value="form.userRole" :options="roleOptions" />
      </a-form-item>

      <a-form-item label="归属环境" name="envCode">
        <a-select
          v-model:value="form.envCode"
          :options="envOptions"
          :loading="envLoading"
          placeholder="请选择环境（与 SYSTEM_ENV 一致）"
          allow-clear
          show-search
          :filter-option="filterEnvOption"
        />
      </a-form-item>

      <a-alert
        type="warning"
        show-icon
        message="登录校验使用 BCrypt。角色：超级管理员可管用户/环境与全部配置；环境管理员仅能管本环境相关 AI 配置；普通用户不能进后台配置。"
        class="form-tip"
      />
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { SystemUser } from '@/api/systemUser.ts'
import { systemEnvApi, type SystemEnv } from '@/api/systemEnv.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemUser | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemUser]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const roleOptions = [
  { label: '超级管理员', value: 'SUPER_ADMIN' },
  { label: '环境管理员', value: 'ENV_ADMIN' },
  { label: '普通用户', value: 'USER' }
]

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
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    {
      validator: async (_rule: unknown, value: string) => {
        if (props.mode === 'create' && !String(value || '').trim()) {
          return Promise.reject(new Error('请设置密码'))
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  userRole: [{ required: true, message: '请选择角色' }]
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
  margin-top: 4px;
}

.form-tip {
  margin-top: 8px;
}
</style>
