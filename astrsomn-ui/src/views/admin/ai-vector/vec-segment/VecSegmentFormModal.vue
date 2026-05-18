<template>
  <a-modal
      v-model:open="open"
      :destroy-on-close="true"
      :footer="null"
      :title="null"
      class="vec-segment-modal"
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
            <h2>{{ mode === 'create' ? '创建向量分段' : '编辑向量分段' }}</h2>
            <p>管理文档切片与向量映射，支持分段内容预览</p>
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
            基本配置
          </h3>

          <div class="form-grid">
            <a-form-item label="文档 ID" name="docId">
              <a-input-number v-model:value="form.docId" min="1" placeholder="关联的文档 ID" size="large"/>
            </a-form-item>

            <a-form-item label="集合 ID" name="collectionId">
              <a-input-number v-model:value="form.collectionId" min="1" placeholder="关联的向量集合 ID" size="large"/>
            </a-form-item>

            <a-form-item label="向量 ID" name="vectorId">
              <a-input v-model:value="form.vectorId" placeholder="向量库中的唯一标识" size="large"/>
            </a-form-item>

            <a-form-item label="分段序号" name="chunkIndex">
              <a-input-number v-model:value="form.chunkIndex" min="0" placeholder="切片序号" size="large"/>
            </a-form-item>

            <a-form-item label="字符数" name="wordCount">
              <a-input-number v-model:value="form.wordCount" min="0" placeholder="字符数" size="large"/>
            </a-form-item>

            <a-form-item class="span-2" label="分段内容" name="segmentContent">
              <a-textarea
                  v-model:value="form.segmentContent"
                  :auto-size="{ minRows: 4, maxRows: 6 }"
                  placeholder="切片文本内容"
              />
            </a-form-item>

            <a-form-item class="span-2" label="元数据 (JSON)" name="metadataJson">
              <div class="json-editor-wrapper">
                <a-textarea
                    v-model:value="form.metadataJson"
                    :auto-size="{ minRows: 4, maxRows: 6 }"
                    class="mono-text"
                    placeholder='{"page": 1, "author": "admin"}'
                />
              </div>
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyCertificateOutlined/>
        数据安全加密存储
      </div>
      <div class="footer-right">
        <a-button class="btn-flat" @click="onCancel">取消</a-button>
        <a-button
            :loading="confirmLoading"
            class="btn-submit"
            type="primary"
            @click="handleOk"
        >
          保存配置
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import {FileTextOutlined, IdcardOutlined, SafetyCertificateOutlined} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiVecSegment} from '@/api/aiVecSegment.ts'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiVecSegment | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSegment] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

function emptyForm(): AiVecSegment {
  return {
    docId: 0,
    collectionId: 0,
    vectorId: '',
    segmentContent: '',
    wordCount: 0,
    chunkIndex: 0,
    metadataJson: ''
  }
}

const form = reactive<AiVecSegment>(emptyForm())

const rules = {
  docId: [{required: true, message: '请输入文档 ID'}],
  collectionId: [{required: true, message: '请输入集合 ID'}],
  vectorId: [{required: true, message: '请输入向量 ID'}],
  chunkIndex: [{required: true, message: '请输入分段序号'}],
  wordCount: [{required: true, message: '请输入字符数'}],
  segmentContent: [{required: true, message: '请输入分段内容'}]
}

function assignFromInitial(src: AiVecSegment) {
  Object.assign(form, emptyForm(), src)
}

watch(() => [open.value, props.initial] as const, ([isOpen, initial]) => {
  if (isOpen) {
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
  }
})

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecSegment = {...form}
  emit('submit', payload)
}

const onCancel = () => {
  open.value = false
}
</script>

<style scoped>

.vec-segment-modal :deep(.ant-modal-content) {
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
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
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
  height: 500px;
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


.json-editor-wrapper {
  border: 1px solid var(--border-input);
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-input);
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
}

.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  background: transparent;
  border: none;
  padding: 12px;
}

.mono-text:focus {
  box-shadow: none;
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
}

.mt-16 {
  margin-top: 16px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
