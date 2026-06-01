<template>
  <a-modal
      v-model:open="open"
      :destroy-on-close="true"
      :footer="null"
      :title="null"
      class="premium-vecdoc-modal"
      width="860px"
      @cancel="onCancel"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box">
            <FileTextOutlined/>
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? t.vecDoc.form.createTitle : t.vecDoc.form.editTitle }}</h2>
            <p>{{ t.vecDoc.form.subtitle }}</p>
          </div>
        </div>
      </div>
    </div>

    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="professional-form"
        layout="vertical"
    >
      <div class="form-body-container">
        <div class="form-section">
          <h3 class="section-headline">
            <IdcardOutlined/>
            {{ t.vecDoc.form.basicInfo }}
          </h3>

          <div class="form-grid">
            <a-form-item :label="t.vecDoc.form.collectionId.label" name="collectionId">
              <a-input v-model:value="form.collectionId" :placeholder="t.vecDoc.form.collectionId.placeholder" size="large"/>
            </a-form-item>

            <a-form-item :label="t.vecDoc.form.docIdInStore.label" name="docIdInStore">
              <a-input v-model:value="form.docIdInStore" :placeholder="t.vecDoc.form.docIdInStore.placeholder" size="large"/>
            </a-form-item>

            <a-form-item :label="t.vecDoc.form.syncStatus.label" name="syncStatus">
              <a-select v-model:value="form.syncStatus" allow-clear :placeholder="t.vecDoc.form.syncStatus.placeholder" size="large">
                <a-select-option :value="AiVecDocSyncStatus.PENDING">{{ t.vecDoc.form.pending }}</a-select-option>
                <a-select-option :value="AiVecDocSyncStatus.STORED">{{ t.vecDoc.form.stored }}</a-select-option>
                <a-select-option :value="AiVecDocSyncStatus.INVALID">{{ t.vecDoc.form.invalid }}</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item :label="t.vecDoc.form.contentSummary.label" class="span-2" name="contentSummary">
              <a-textarea
                  v-model:value="form.contentSummary"
                  :auto-size="{ minRows: 3, maxRows: 5 }"
                  :placeholder="t.vecDoc.form.contentSummary.placeholder"
              />
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyCertificateOutlined/>
        {{ t.vecDoc.form.securityInfo }}
      </div>
      <div class="footer-right">
        <a-button class="btn-flat" @click="onCancel">{{ t.vecDoc.form.cancel }}</a-button>
        <a-button
            :loading="confirmLoading"
            class="btn-submit"
            type="primary"
            @click="handleOk"
        >
          {{ t.vecDoc.form.save }}
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import {FileTextOutlined, IdcardOutlined, SafetyCertificateOutlined} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import {type AiVecDoc, AiVecDocSyncStatus} from '@/api/aiVecDoc.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiVecDoc | null }>()
const emit = defineEmits<{ submit: [payload: AiVecDoc] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

function emptyForm(): AiVecDoc {
  return {
    collectionId: undefined,
    docIdInStore: '',
    contentSummary: '',
    syncStatus: AiVecDocSyncStatus.PENDING
  }
}

const form = reactive<AiVecDoc>(emptyForm())

const rules = computed(() => ({
  collectionId: [{required: true, message: t.value.vecDoc.form.validation.collectionIdRequired}],
  docIdInStore: [{
    validator: async (_rule: unknown, value: string) => {
      const st = String(form.syncStatus || '').toUpperCase()
      if (st === AiVecDocSyncStatus.STORED && !String(value || '').trim()) {
        return Promise.reject(new Error(t.value.vecDoc.form.validation.storedDocIdRequired))
      }
      return Promise.resolve()
    }
  }],
  contentSummary: [{required: true, message: t.value.vecDoc.form.validation.contentSummaryRequired}],
  syncStatus: [{required: true, message: t.value.vecDoc.form.validation.syncStatusRequired}]
}))

function assignFromInitial(src: AiVecDoc) {
  Object.assign(form, emptyForm(), src)
  if (!form.syncStatus) {
    form.syncStatus = AiVecDocSyncStatus.PENDING
  }
}

watch(() => [open.value, props.initial] as const, ([isOpen, initial]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecDoc = {...form}
  emit('submit', payload)
}

const onCancel = () => {
  open.value = false
}
</script>

<style scoped>
.premium-vecdoc-modal :deep(.ant-modal-content) {
  padding: 0;
  border-radius: 20px;
  overflow: hidden;
}

.modal-header-gradient {
  background: var(--bg-card);
  padding: 32px 40px;
  border-bottom: 1px solid var(--border-default);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-area {
  display: flex;
  gap: 16px;
  align-items: center;
}

.icon-box {
  width: 48px;
  height: 48px;
  background: var(--primary);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 8px 16px color-mix(in srgb, var(--primary) 20%, transparent);
}

.text-group h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-heading);
}

.text-group p {
  margin: 4px 0 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.professional-form {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.form-body-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px 40px;
}

.form-body-container::-webkit-scrollbar {
  width: 4px;
}

.form-body-container::-webkit-scrollbar-thumb {
  background: var(--border-input);
  border-radius: 4px;
}

.section-headline {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.span-2 {
  grid-column: span 2;
}

.modal-footer-action {
  padding: 16px 40px;
  background: var(--bg-card);
  border-top: 1px solid var(--border-default);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  font-size: 12px;
  color: var(--success);
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-flat {
  border: none;
  color: var(--text-secondary);
  font-weight: 600;
}

.btn-submit {
  border-radius: 8px;
  font-weight: 600;
  height: 38px;
  padding: 0 24px;
  background: var(--primary);
  border-color: var(--primary);
}

.btn-submit:hover, .btn-submit:focus {
  background: var(--primary);
  border-color: var(--primary);
}
</style>
