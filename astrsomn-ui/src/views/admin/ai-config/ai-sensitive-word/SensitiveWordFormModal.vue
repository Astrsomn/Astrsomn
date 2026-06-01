<template>
  <a-modal
      v-model:open="open"
      :body-style="{ maxHeight: '78vh', overflowY: 'auto' }"
      :confirm-loading="confirmLoading"
      :title="mode === 'create' ? t.form.createTitle : t.form.editTitle"
      width="760px"
      @cancel="onCancel"
      @ok="handleOk"
  >
    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="sensitive-word-form"
        layout="vertical"
    >
      <div class="form-grid">
        <a-form-item :label="t.form.wordLabel" name="word">
          <a-input
              v-model:value="form.word"
              autocomplete="off"
              :placeholder="t.form.wordPlaceholder"
          />
        </a-form-item>

        <a-form-item :label="t.form.matchTypeLabel" name="matchType">
          <a-select v-model:value="form.matchType" :options="matchTypeOptions"/>
        </a-form-item>

        <a-form-item :label="t.form.scopeKeyLabel" name="scopeKey">
          <a-input
              v-model:value="form.scopeKey"
              allow-clear
              :placeholder="t.form.scopeKeyPlaceholder"
          />
        </a-form-item>

        <a-form-item :label="t.form.actionLabel" name="action">
          <a-select v-model:value="form.action" :options="actionOptions"/>
        </a-form-item>

        <a-form-item :label="t.form.categoryLabel" name="category">
          <a-input
              v-model:value="form.category"
              allow-clear
              :placeholder="t.form.categoryPlaceholder"
          />
        </a-form-item>

        <a-form-item :label="t.form.statusLabel" name="status">
          <a-select v-model:value="form.status" :options="statusOptions"/>
        </a-form-item>

        <a-form-item
            class="span-2"
            :label="t.form.replacementLabel"
            name="replacement"
        >
          <a-input
              v-model:value="form.replacement"
              :disabled="form.action !== 'REPLACE'"
              :placeholder="form.action === 'REPLACE' ? t.form.replacementPlaceholder : t.form.replacementDisabledPlaceholder"
              allow-clear
          />
        </a-form-item>
      </div>

      <a-alert
          class="form-tip"
          :message="t.form.alertMessage"
          show-icon
          type="info"
      />
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiSensitiveWord} from '@/api/aiSensitiveWord.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-sensitive-word')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiSensitiveWord | null
}>()

const emit = defineEmits<{
  submit: [payload: AiSensitiveWord]
}>()

const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const matchTypeOptions = computed(() => [
  {label: t.value.form.matchType.exact, value: 'EXACT'},
  {label: t.value.form.matchType.fuzzy, value: 'FUZZY'},
  {label: t.value.form.matchType.regex, value: 'REGEX'}
])

const actionOptions = computed(() => [
  {label: t.value.form.action.block, value: 'BLOCK'},
  {label: t.value.form.action.replace, value: 'REPLACE'},
  {label: t.value.form.action.warn, value: 'WARN'}
])

const statusOptions = computed(() => [
  {label: t.value.form.status.enabled, value: 'ENABLED'},
  {label: t.value.form.status.disabled, value: 'DISABLED'}
])

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

const rules = computed(() => ({
  word: [{required: true, message: t.value.form.validation.wordRequired}],
  matchType: [{required: true, message: t.value.form.validation.matchTypeRequired}],
  action: [{required: true, message: t.value.form.validation.actionRequired}],
  status: [{required: true, message: t.value.form.validation.statusRequired}],
  replacement: [
    {
      validator: async (_rule: unknown, value: string) => {
        if (form.action === 'REPLACE' && !String(value || '').trim()) {
          return Promise.reject(new Error(t.value.form.validation.replacementRequired))
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}))

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
