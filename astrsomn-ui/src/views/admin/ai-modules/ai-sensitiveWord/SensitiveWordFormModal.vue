<template>
  <a-modal
    v-model:open="open"
    :title="mode === 'create' ? '新增敏感词规则' : '编辑敏感词规则'"
    width="760px"
    :confirm-loading="confirmLoading"
    :body-style="{ maxHeight: '78vh', overflowY: 'auto' }"
    @ok="handleOk"
    @cancel="onCancel"
  >
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="sensitive-word-form"
    >
      <div class="form-grid">
        <a-form-item label="敏感词" name="word">
          <a-input
            v-model:value="form.word"
            placeholder="支持文本或正则表达式"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="匹配类型" name="matchType">
          <a-select v-model:value="form.matchType" :options="matchTypeOptions" />
        </a-form-item>

        <a-form-item label="作用范围" name="scopeKey">
          <a-input
            v-model:value="form.scopeKey"
            placeholder="ALL 或具体 AGENT_KEY"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="处置动作" name="action">
          <a-select v-model:value="form.action" :options="actionOptions" />
        </a-form-item>

        <a-form-item label="分类" name="category">
          <a-input
            v-model:value="form.category"
            placeholder="如 POLITICS / VIOLENCE / CUSTOM"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-select v-model:value="form.status" :options="statusOptions" />
        </a-form-item>

        <a-form-item
          label="替换文本"
          name="replacement"
          class="span-2"
        >
          <a-input
            v-model:value="form.replacement"
            :disabled="form.action !== 'REPLACE'"
            :placeholder="form.action === 'REPLACE' ? '例如 ***' : '仅 REPLACE 动作需要填写'"
            allow-clear
          />
        </a-form-item>
      </div>

      <a-alert
        type="info"
        show-icon
        class="form-tip"
        message="BLOCK 会直接拦截，REPLACE 会使用替换文本覆盖命中内容，WARN 只记录风险日志。"
      />
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiSensitiveWord } from '@/api/aiSensitiveWord.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiSensitiveWord | null
}>()

const emit = defineEmits<{
  submit: [payload: AiSensitiveWord]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const matchTypeOptions = [
  { label: '精确匹配', value: 'EXACT' },
  { label: '模糊匹配', value: 'FUZZY' },
  { label: '正则匹配', value: 'REGEX' }
]

const actionOptions = [
  { label: '直接拦截', value: 'BLOCK' },
  { label: '替换文本', value: 'REPLACE' },
  { label: '仅告警', value: 'WARN' }
]

const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' }
]

function emptyForm(): AiSensitiveWord {
  return {
    word: '',
    matchType: 'EXACT',
    scopeKey: 'ALL',
    action: 'BLOCK',
    replacement: '',
    status: 'ENABLED',
    category: 'CUSTOM'
  }
}

const form = reactive<AiSensitiveWord>(emptyForm())

const rules = {
  word: [{ required: true, message: '请输入敏感词' }],
  matchType: [{ required: true, message: '请选择匹配类型' }],
  action: [{ required: true, message: '请选择处置动作' }],
  status: [{ required: true, message: '请选择状态' }],
  replacement: [
    {
      validator: async (_rule: unknown, value: string) => {
        if (form.action === 'REPLACE' && !String(value || '').trim()) {
          return Promise.reject(new Error('REPLACE 动作必须填写替换文本'))
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}

function assignFromInitial(src: AiSensitiveWord) {
  Object.assign(form, emptyForm(), src)
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

  const payload: AiSensitiveWord = {
    ...form,
    scopeKey: String(form.scopeKey || '').trim() || 'ALL',
    replacement: form.action === 'REPLACE' ? String(form.replacement || '').trim() || '***' : undefined
  }
  emit('submit', payload)
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
.sensitive-word-form {
  margin-top: 4px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 16px;
}

.span-2 {
  grid-column: span 2;
}

.form-tip {
  margin-top: 8px;
}

@media (max-width: 860px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .span-2 {
    grid-column: auto;
  }
}
</style>
