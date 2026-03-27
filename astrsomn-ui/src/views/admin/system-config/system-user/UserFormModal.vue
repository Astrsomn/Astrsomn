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

      <a-form-item label="归属环境编码" name="envCode">
        <a-input
          v-model:value="form.envCode"
          placeholder="与 astrsomn.env-code 一致，如 pro；超级管理员可留空或填默认"
          allow-clear
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
