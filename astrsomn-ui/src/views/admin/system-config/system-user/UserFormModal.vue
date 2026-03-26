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

      <a-form-item label="管理员" name="adminFlag">
        <a-select v-model:value="form.adminFlag" :options="adminOptions" />
      </a-form-item>

      <a-alert
        type="warning"
        show-icon
        message="登录校验使用 BCrypt。若新建用户无法登录，请在库中确认密码为 BCrypt 或在后端接入加密。"
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

const adminOptions = [
  { label: '是 (Y)', value: 'Y' },
  { label: '否 (N)', value: 'N' }
]

function emptyForm(): SystemUser {
  return {
    username: '',
    password: '',
    email: '',
    adminFlag: 'N'
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
  adminFlag: [{ required: true, message: '请选择是否管理员' }]
}

function assignFromInitial(src: SystemUser) {
  Object.assign(form, emptyForm(), src)
  form.password = ''
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
    adminFlag: form.adminFlag
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
