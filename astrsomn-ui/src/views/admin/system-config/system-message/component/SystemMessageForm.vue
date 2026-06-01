<template>
  <AstPageShell
      :title="isEdit ? t.form.editTitle : t.form.createTitle"
      :description="t.form.description"
      empty-text=""
  >
    <div class="form-page">
      <div class="form-toolbar">
        <a-button class="ghost-btn" @click="goBack">
          <template #icon>
            <arrow-left-outlined/>
          </template>
          {{ t.form.btnBack }}
        </a-button>
      </div>

      <a-form :model="form" class="message-form" layout="vertical" @finish="onSubmit">
        <a-row :gutter="16">
          <a-col :md="12" :xs="24">
            <a-form-item :rules="[{ required: true, message: t.form.validationMessageType }]" :label="t.form.labelMessageType" name="messageType">
              <a-select v-model:value="form.messageType" :options="messageTypeOptions" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="24">
            <a-form-item :rules="[{ required: true, message: t.form.validationMessageLevel }]" :label="t.form.labelMessageLevel" name="messageLevel">
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
            <a-form-item :rules="[{ required: true, message: t.form.validationTitle }]" :label="t.form.labelTitle" name="title">
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
          <a-button class="ghost-btn" @click="goBack">{{ t.form.btnCancel }}</a-button>
          <a-button :loading="submitting" class="primary-btn" html-type="submit" type="primary">
            {{ isEdit ? t.form.btnSave : t.form.btnCreate }}
          </a-button>
        </div>
      </a-form>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import {ArrowLeftOutlined} from '@ant-design/icons-vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import {type SystemMessage, systemMessageApi} from '@/api/systemMessage.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import {getDictionary} from '@/locales/dictionary/registry.ts'

const route = useRoute()
const router = useRouter()
const t = usePageTranslation('system-message')
const messageTypeDict = getDictionary('system.message.type')
const messageLevelDict = getDictionary('system.message.level')
const readStatusDict = getDictionary('system.message.readStatus')
const refTypeDict = getDictionary('system.message.refType')

const submitting = ref(false)
const loading = ref(false)

const isEdit = computed(() => route.name === 'AdminSystemMessageEdit')

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

const form = reactive<SystemMessage>({
  messageType: 'SYSTEM_NOTICE',
  messageLevel: 'INFO',
  readStatus: 'UNREAD',
  title: '',
  content: '',
  source: 'SYSTEM'
})

const refIdText = ref('')

const applyForm = (data: SystemMessage) => {
  Object.keys(form).forEach((k) => delete (form as Record<string, unknown>)[k])
  Object.assign(form, {
    messageType: 'SYSTEM_NOTICE',
    messageLevel: 'INFO',
    readStatus: 'UNREAD',
    title: '',
    content: '',
    source: 'SYSTEM'
  }, data)
  refIdText.value = form.refId == null ? '' : String(form.refId)
}

const loadDetail = async (id: string) => {
  loading.value = true
  try {
    const detail = await systemMessageApi.detail(id)
    applyForm(detail)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.form.loadFailed)
    goBack()
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  void router.push({name: 'AdminSystemMessage'})
}

const parseRefId = () => {
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

const onSubmit = async () => {
  if (!parseRefId()) return
  submitting.value = true
  try {
    const payload: SystemMessage = {...form}
    let msg: string
    if (isEdit.value) {
      msg = await systemMessageApi.update(payload)
    } else {
      delete (payload as { id?: unknown }).id
      msg = await systemMessageApi.create(payload)
    }
    message.success(msg)
    goBack()
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || t.value.form.saveFailed)
  } finally {
    submitting.value = false
  }
}

watch(
    [() => route.name, () => route.params.id],
    ([name, id]) => {
      if (name === 'AdminSystemMessageNew') {
        applyForm({})
        return
      }
      if (name === 'AdminSystemMessageEdit' && id != null && String(id)) {
        void loadDetail(String(id))
      }
    },
    {immediate: true}
)
</script>

<style scoped>
.form-page {
  padding: 0 4px;
}

.form-toolbar {
  margin-bottom: 16px;
}

.message-form {
  max-width: 860px;
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
