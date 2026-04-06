<template>
  <a-modal
    v-model:open="open"
    :title="null"
    width="860px"
    :footer="null"
    :destroy-on-close="true"
    @cancel="onCancel"
    class="premium-vecsource-modal"
  >
    <div class="modal-header-gradient">
      <div class="header-content">
        <div class="title-area">
          <div class="icon-box">
            <DatabaseOutlined />
          </div>
          <div class="text-group">
            <h2>{{ mode === 'create' ? '注册向量源' : '编辑向量源配置' }}</h2>
            <p>配置向量数据库连接信息，支持 Milvus、Pinecone、DashVector 等多种向量引擎</p>
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
            <a-form-item label="向量源名称" name="name">
              <a-input v-model:value="form.name" placeholder="例如：Milvus-Production" size="large" />
            </a-form-item>

            <a-form-item label="提供商 (Provider)" name="provider">
              <a-select v-model:value="form.provider" placeholder="选择向量数据库提供商" size="large" allow-clear>
                <a-select-option value="MILVUS">Milvus</a-select-option>
                <a-select-option value="PINECONE">Pinecone</a-select-option>
                <a-select-option value="DASHVECTOR">DashVector</a-select-option>
                <a-select-option value="CHROMA">Chroma</a-select-option>
                <a-select-option value="QDRANT">Qdrant</a-select-option>
                <a-select-option value="WEAVIATE">Weaviate</a-select-option>
                <a-select-option value="OTHER">其他</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="状态" name="status" class="span-2">
              <div class="status-card">
                <div class="info">
                  <span class="t">启用此向量源</span>
                  <span class="d">关闭后，所有关联此向量源的集合将无法使用</span>
                </div>
                <a-switch v-model:checked="statusChecked" />
              </div>
            </a-form-item>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-headline"><LinkOutlined /> 2. 连接配置</h3>

          <div class="form-grid">
            <a-form-item label="主机地址 (Host)" name="host">
              <a-input v-model:value="form.host" placeholder="例如：localhost 或 192.168.1.100" size="large">
                <template #prefix><GlobalOutlined style="color: #bfbfbf" /></template>
              </a-input>
            </a-form-item>

            <a-form-item label="端口 (Port)" name="port">
              <a-input v-model:value="form.port" placeholder="例如：19530" size="large">
                <template #prefix><ApiOutlined style="color: #bfbfbf" /></template>
              </a-input>
            </a-form-item>

            <a-form-item label="数据库名称" name="databaseName">
              <a-input v-model:value="form.databaseName" placeholder="例如：default" size="large" />
            </a-form-item>

            <a-form-item label="用户名" name="username">
              <a-input v-model:value="form.username" placeholder="数据库用户名" size="large">
                <template #prefix><UserOutlined style="color: #bfbfbf" /></template>
              </a-input>
            </a-form-item>

            <a-form-item label="密码" name="password">
              <a-input-password v-model:value="form.password" placeholder="数据库密码" size="large">
                <template #prefix><LockOutlined style="color: #bfbfbf" /></template>
              </a-input-password>
            </a-form-item>

            <a-form-item label="API Token" name="token">
              <a-input-password v-model:value="form.token" placeholder="Pinecone/DashVector API Key" size="large">
                <template #prefix><KeyOutlined style="color: #bfbfbf" /></template>
              </a-input-password>
            </a-form-item>

            <a-form-item label="扩展配置 (JSON)" name="configJson" class="span-2">
              <div class="json-editor-wrapper">
                <a-textarea
                  v-model:value="form.configJson"
                  :auto-size="{ minRows: 4, maxRows: 6 }"
                  placeholder='{"ssl": true, "timeout": 30000}'
                  class="mono-text"
                />
              </div>
            </a-form-item>
          </div>
        </div>
      </div>
    </a-form>

    <div class="modal-footer-action">
      <div class="footer-left">
        <SafetyCertificateOutlined /> 连接信息受系统级加密保护
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
import { computed, reactive, ref, watch } from 'vue'
import { 
  DatabaseOutlined, IdcardOutlined, LinkOutlined, GlobalOutlined, 
  ApiOutlined, UserOutlined, LockOutlined, KeyOutlined, SafetyCertificateOutlined 
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { AiVecSource } from '@/api/aiVecSource'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiVecSource | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSource] }>()
const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

function emptyForm(): AiVecSource {
  return {
    name: '',
    provider: undefined,
    host: '',
    port: '',
    username: '',
    password: '',
    databaseName: '',
    token: '',
    configJson: '',
    status: 'ENABLED'
  }
}

const form = reactive<AiVecSource>(emptyForm())

const statusChecked = computed({
  get: () => form.status === 'ENABLED',
  set: (v: boolean) => { form.status = v ? 'ENABLED' : 'DISABLED' }
})

const rules = {
  name: [{ required: true, message: '请输入向量源名称' }],
  provider: [{ required: true, message: '请选择提供商' }],
  host: [{ required: true, message: '请输入主机地址' }]
}

function assignFromInitial(src: AiVecSource) {
  Object.assign(form, emptyForm(), src)
  if (!form.status) {
    form.status = 'ENABLED'
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
  const payload: AiVecSource = { ...form }
  emit('submit', payload)
}

const onCancel = () => { open.value = false }
</script>

<style scoped>
.premium-vecsource-modal :deep(.ant-modal-content) { padding: 0; border-radius: 20px; overflow: hidden; }

.modal-header-gradient { background: #fff; padding: 32px 40px; border-bottom: 1px solid #f0f2f5; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-area { display: flex; gap: 16px; align-items: center; }
.icon-box {
  width: 48px; height: 48px; background: #10b981; color: white; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 22px;
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.2);
}
.text-group h2 { margin: 0; font-size: 20px; font-weight: 700; color: #111; }
.text-group p { margin: 4px 0 0; color: #999; font-size: 13px; }

.professional-form { height: 500px; display: flex; flex-direction: column; }
.form-body-container { flex: 1; overflow-y: auto; padding: 24px 40px; }
.form-body-container::-webkit-scrollbar { width: 4px; }
.form-body-container::-webkit-scrollbar-thumb { background: #eee; border-radius: 4px; }

.section-headline { font-size: 15px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: #333; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
.span-2 { grid-column: span 2; }

.status-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #f8f9fb; padding: 12px 16px; border-radius: 12px; border: 1px solid #eef1f6;
}
.status-card .t { display: block; font-size: 13px; font-weight: 600; }
.status-card .d { font-size: 12px; color: #999; }

.json-editor-wrapper {
  border: 1px solid #d9d9d9; border-radius: 8px; overflow: hidden;
  background: #fafafa; transition: 0.3s;
}
.json-editor-wrapper:focus-within { border-color: #10b981; box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.1); }
.mono-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px; background: transparent; border: none; padding: 12px;
}
.mono-text:focus { box-shadow: none; }

.modal-footer-action {
  padding: 16px 40px; background: #fff; border-top: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
}
.footer-left { font-size: 12px; color: #52c41a; display: flex; align-items: center; gap: 6px; }
.btn-flat { border: none; color: #999; font-weight: 600; }
.btn-submit { border-radius: 8px; font-weight: 600; height: 38px; padding: 0 24px; background: #10b981; border-color: #10b981; }
.btn-submit:hover, .btn-submit:focus { background: #059669; border-color: #059669; }
</style>
