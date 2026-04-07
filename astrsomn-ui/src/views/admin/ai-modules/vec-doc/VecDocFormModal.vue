<template>
  <a-modal
    v-model:open="open"
    :title="null"
    width="860px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-vecdoc-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box">
            <FileTextOutlined />
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '添加向量文档' : '编辑向量文档' }}</h2>
            <p>管理向量知识库中的文档记录，支持文档入库状态追踪</p>
          </div>
        </div>
      </div>
    </div>

    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="professional-form"
    >
      <div class="form-body-container">
        <div class="form-section">
          <h3 class="section-headline"><IdcardOutlined /> 1. 基础信息</h3>

          <div class="form-grid">
            <a-form-item label="集合 ID" name="collectionId">
              <a-input v-model:value="form.collectionId" placeholder="所属向量集合的 ID" size="large" />
            </a-form-item>

            <a-form-item label="存储文档 ID" name="docIdInStore">
              <a-input v-model:value="form.docIdInStore" placeholder="向量库中的唯一标识" size="large" />
            </a-form-item>

            <a-form-item label="同步状态" name="syncStatus">
              <a-select v-model:value="form.syncStatus" placeholder="选择同步状态" size="large" allow-clear>
                <a-select-option value="PENDING">待向量化</a-select-option>
                <a-select-option value="STORED">已入库</a-select-option>
                <a-select-option value="INVALID">已失效</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="内容摘要" name="contentSummary" class="span-2">
              <a-textarea 
                v-model:value="form.contentSummary" 
                :auto-size="{ minRows: 3, maxRows: 5 }" 
                placeholder="文档内容摘要或路径"
              />
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyCertificateOutlined /> 文档信息受系统级保护
      </div>
      <div class="footer-right">
        <a-button class="btn-flat" @click="onCancel">取消</a-button>
        <a-button 
          type="primary" 
          class="btn-submit" 
          :loading="confirmLoading" 
          @click="handleOk"
        >
          保存配置
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { 
  FileTextOutlined, IdcardOutlined, SafetyCertificateOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiVecDoc } from '@/api/aiVecDoc'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiVecDoc | null }>()
const emit = defineEmits<{ submit: [payload: AiVecDoc] }>()
const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

function emptyForm(): AiVecDoc {
  return {
    collectionId: undefined,
    docIdInStore: '',
    contentSummary: '',
    syncStatus: 'PENDING'
  }
}

const form = reactive<AiVecDoc>(emptyForm())

const rules = {
  collectionId: [{ required: true, message: '请输入集合 ID' }],
  docIdInStore: [{ required: true, message: '请输入存储文档 ID' }],
  contentSummary: [{ required: true, message: '请输入内容摘要' }],
  syncStatus: [{ required: true, message: '请选择同步状态' }]
}

function assignFromInitial(src: AiVecDoc) {
  Object.assign(form, emptyForm(), src)
  if (!form.syncStatus) {
    form.syncStatus = 'PENDING'
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
  const payload: AiVecDoc = { ...form }
  emit('submit', payload)
}

const onCancel = () => { open.value = false }
</script>

<style scoped>
.premium-vecdoc-modal :deep(.ant-modal-content) { padding: 0; border-radius: 20px; overflow: hidden; }

.modal-header-gradient { background: #fff; padding: 32px 40px; border-bottom: 1px solid #f0f2f5; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 48px; height: 48px; background: #3b82f6; color: white; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 22px;
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
}
.text-group h2 { margin: 0; font-size: 20px; font-weight: 700; color: #111; }
.text-group p { margin: 4px 0 0; color: #999; font-size: 13px; }

.professional-form { height: 400px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }
.form-body-container::-webkit-scrollbar { width: 4px; }
.form-body-container::-webkit-scrollbar-thumb { background: #eee; border-radius: 4px; }

.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }

.modal-footer-action {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-left { font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 6px; }
.btn-flat { border: none; color: #999; font-weight: 600; }
.btn-submit { border-radius: 8px; font-weight: 600; height: 38px; padding: 0 24px; background: #3b82f6; border-color: #3b82f6; }
.btn-submit:hover, .btn-submit:focus { background: #2563eb; border-color: #2563eb; }
</style>
