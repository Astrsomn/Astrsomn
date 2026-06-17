<template>
  <AstModal
    :destroy-on-close="true"
    :open="open"
    :width="760"
    body-height="auto"
    main-padding="24px"
    max-body-height="85vh"
    @update:open="onClose"
  >
    <template #header-title>
      {{ mode === 'create' ? t.form.createTitle : t.form.editTitle }}
    </template>

    <div class="form-page">
      <a-form
        ref="formRef"
        :model="form"
        class="message-form"
        layout="vertical"
        @finish="handleOk"
      >
        <a-row :gutter="16">
          <a-col :md="12" :xs="24">
            <a-form-item
              :rules="[{ required: true, message: t.form.validationMessageType }]"
              :label="t.form.labelMessageType"
              name="messageType"
            >
              <a-select v-model:value="form.messageType" :options="messageTypeOptions" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item
              :rules="[{ required: true, message: t.form.validationMessageLevel }]"
              :label="t.form.labelMessageLevel"
              name="messageLevel"
            >
              <a-select v-model:value="form.messageLevel" :options="messageLevelOptions" allow-clear/>
            </a-form-item>
          </a-col>

          <a-col :md="12" :xs="24">
            <a-form-item :label="t.form.labelReadStatus" name="readStatus">
              <a-select v-model:value="form.readStatus" :options="readStatusOptions" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item :label="t.form.labelSource" name="source">
              <a-input v-model:value="form.source" allow-clear :placeholder="t.form.placeholderSource"/>
            </a-form-item>
          </a-col>

          <a-col :xs="24">
            <a-form-item
              :rules="[{ required: true, message: t.form.validationTitle }]"
              :label="t.form.labelTitle"
              name="title"
            >
              <a-input v-model:value="form.title" allow-clear :placeholder="t.form.placeholderTitle"/>
            </a-form-item>
          </a-col>

          <a-col :xs="24">
            <a-form-item :label="t.form.labelContent" name="content">
              <a-textarea
                  v-model:value="form.content"
                  :auto-size="{ minRows: 4, maxRows: 10 }"
                  :placeholder="t.form.placeholderContent"
              />
            </a-form-item>
          </a-col>

          <a-col :md="8" :xs="24">
            <a-form-item :label="t.form.labelRefType" name="refType">
              <a-select v-model:value="form.refType" :options="refTypeOptions" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :xs="24">
            <a-form-item :label="t.form.labelRefId" name="refId">
              <a-input v-model:value="refIdText" allow-clear :placeholder="t.form.placeholderRefId"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :xs="24">
            <a-form-item :label="t.form.labelRefKey" name="refKey">
              <a-input v-model:value="form.refKey" allow-clear :placeholder="t.form.placeholderRefKey"/>
            </a-form-item>
          </a-col>

          <a-col :md="12" :xs="24">
            <a-form-item :label="t.form.labelErrorCode" name="errorCode">
              <a-input v-model:value="form.errorCode" allow-clear :placeholder="t.form.placeholderErrorCode"/>
            </a-form-item>
          </a-col>
        </a-row>

        <div class="form-actions">
          <a-button class="ghost-btn" @click="onClose">{{ t.form.btnCancel }}</a-button>
          <a-button :loading="confirmLoading" class="primary-btn" html-type="submit" type="primary">
            {{ mode === 'create' ? t.form.btnCreate : t.form.btnSave }}
          </a-button>
        </div>
      </a-form>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import AstModal from '@/components/home/AstModal.vue'
import { type SystemMessage } from '@/api/systemMessage.ts'
import { usePageTranslation } from '@/locales/pages.ts'
import { getDictionary } from '@/locales/dictionary/registry.ts'

const t = usePageTranslation('system-message')
const messageTypeDict = getDictionary('system.message.type')
const messageLevelDict = getDictionary('system.message.level')
const readStatusDict = getDictionary('system.message.readStatus')
const refTypeDict = getDictionary('system.message.refType')

// ---- props & emits ----
const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: SystemMessage | null
}>()

const emit = defineEmits<{
  submit: [payload: SystemMessage]
}>()

const open = defineModel<boolean>('open', { required: true })

// ---- form ----
const formRef = ref<FormInstance | null>(null)

const messageTypeOptions = computed(() => messageTypeDict.order.map(key => ({
  label: messageTypeDict.getLabel(key) ?? key,
  value: key
})))
const messageLevelOptions = computed(() => messageLevelDict.order.map(key => ({
  label: messageLevelDict.getLabel(key) ?? key,
  value: key
})))
const readStatusOptions = computed(() => readStatusDict.order.map(key => ({
  label: readStatusDict.getLabel(key) ?? key,
  value: key
})))
const refTypeOptions = computed(() => refTypeDict.order.map(key => ({
  label: refTypeDict.getLabel(key) ?? key,
  value: key
})))

function emptyForm(): SystemMessage {
  return {
    messageType: 'SYSTEM_NOTICE',
    messageLevel: 'INFO',
    readStatus: 'UNREAD',
    title: '',
    content: '',
    source: 'SYSTEM'
  }
}

const form = reactive<SystemMessage>(emptyForm())
const refIdText = ref('')

function applyForm(data: SystemMessage) {
  Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
  Object.assign(form, emptyForm(), data)
  refIdText.value = form.refId == null ? '' : String(form.refId)
}

function parseRefId(): boolean {
  const val = refIdText.value.trim()
  if (!val) {
    form.refId = undefined
    return true
  }
  if (!/^\d+$/.test(val)) {
    message.warning(t.value.form.refIdMustBeNumber)
    return false
  }
  form.refId = val
  return true
}

function onClose() {
  open.value = false
}

async function handleOk() {
  if (!parseRefId()) return
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  emit('submit', { ...form })
}

// ---- watch: reset form on open ----
watch(
    () => [open.value, props.initial] as const,
    ([isOpen, initial]) => {
      if (!isOpen) return
      if (initial && Object.keys(initial).length > 0) {
        applyForm(initial)
      } else {
        applyForm({})
      }
    }
)
</script>

<style scoped>
.form-page {
  min-height: 0;
}

.message-form {
  max-width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid var(--border-subtle);
}

.primary-btn,
.ghost-btn {
  height: 40px;
  border-radius: 12px;
  min-width: 104px;
}
</style>
