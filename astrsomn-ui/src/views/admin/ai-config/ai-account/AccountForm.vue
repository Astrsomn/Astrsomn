<template>
  <AstrsomnModal
    :open="visible"
    width="680px"
    max-width="min(680px, 96vw)"
    body-height="80vh"
    :closable="true"
    main-padding="0"
    :wrap-class-name="isEdit ? 'account-form-fsm-wrap account-form-fsm-wrap--edit' : 'account-form-fsm-wrap'"
    @update:open="emit('update:visible', $event)"
    @cancel="handleCancel"
  >
    <template #header-logo>
      <component :is="isEdit ? FormOutlined : PlusCircleOutlined" />
    </template>
    <template #header-title>{{ isEdit ? '编辑账号' : '新建账号' }}</template>

    <div class="account-form-shell">
    <div class="account-form-spin-wrap">
    <a-spin :spinning="loading">
      <div class="modal-body-content">
        <div v-if="accountKeyImmutable" class="info-alert">
          <div class="alert-content">
            <LockFilled class="alert-icon" />
            <span>核心索引已锁定：该凭证正被活跃的端点使用，无法修改 Key 标识。</span>
          </div>
        </div>

        <a-form layout="vertical" :model="form" class="refined-form">
          <div class="form-layout">
            <div class="form-column">
              <div class="form-section">
                <a-form-item name="accountKey">
                  <template #label><span class="field-label">账号 Key</span></template>
                  <a-input
                    v-model:value="form.accountKey"
                    :placeholder="accountKeyImmutable ? '' : '系统自动生成'"
                    :disabled="accountKeyImmutable"
                    class="premium-input mono"
                  />
                </a-form-item>
              </div>
              <div class="form-section">
                <a-form-item
                  name="accountName"
                  :rules="[{ required: true, message: '请定义凭证展示名称' }]"
                >
                  <template #label><span class="field-label">展示名称</span></template>
                  <a-input
                    v-model:value="form.accountName"
                    placeholder="请输入账号名称"
                    class="premium-input"
                    allow-clear
                  />
                </a-form-item>
              </div>
              <div class="form-section">
                <a-form-item
                  name="provider"
                  :rules="[{ required: true, message: '请选择供应商' }]"
                >
                  <template #label><span class="field-label">Provider</span></template>
                  <ModelProviderSelect
                    v-model:value="form.provider"
                    placeholder="请选择供应商"
                    :allow-clear="true"
                    size="middle"
                  />
                </a-form-item>
              </div>
            </div>

            <div class="form-column">
              <div class="form-section">
                <a-form-item name="apiUrl">
                  <template #label><span class="field-label">API URL</span></template>
                  <a-input
                    v-model:value="form.apiUrl"
                    placeholder="例如：https://api.openai.com/v1"
                    class="premium-input mono"
                    allow-clear
                  />
                </a-form-item>
              </div>
              <div class="form-section">
                <a-form-item name="apiKey">
                  <template #label><span class="field-label">API Key</span></template>
                  <a-input-password
                    v-model:value="form.apiKey"
                    placeholder="请输入 API Key"
                    class="premium-input mono"
                  />
                </a-form-item>
              </div>
              <div class="form-section">
                <a-form-item name="apiSecret">
                  <template #label><span class="field-label">API Secret</span></template>
                  <a-input-password
                    v-model:value="form.apiSecret"
                    placeholder="请输入 Secret 密钥"
                    class="premium-input mono"
                  />
                </a-form-item>
              </div>
              <div class="form-section">
                <a-form-item name="accountTokens">
                  <template #label><span class="field-label">消耗上限 (Tokens)</span></template>
                  <a-input-number
                    v-model:value="form.accountTokens"
                    :min="0"
                    placeholder="无限制"
                    class="premium-input-number w-full mono"
                  />
                </a-form-item>
              </div>
            </div>
          </div>
        </a-form>
      </div>
    </a-spin>
    </div>

    <div class="premium-footer">
      <div class="footer-info">
        <SyncOutlined spin class="sync-icon" />
        <span>变更将实时同步至下游实例</span>
      </div>
      <div class="footer-actions">
        <a-button @click="handleCancel" class="btn-cancel">放弃修改</a-button>
        <a-button type="primary" :loading="submitting" @click="onSubmit" class="btn-submit">
          <span>确认部署更新</span>
          <ArrowRightOutlined />
        </a-button>
      </div>
    </div>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  LockFilled,
  FormOutlined, PlusCircleOutlined, SyncOutlined, ArrowRightOutlined
} from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import ModelProviderSelect from '@/views/admin/ai-config/ai-model/ModelProviderSelect.vue'
import { aiAccountApi, type AiAccount } from '@/api/aiAccount'

interface Props {
  visible: boolean
  record?: AiAccount
}

const props = defineProps<Props>()
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const submitting = ref(false)
const accountKeyImmutable = ref(false)
const isEdit = computed(() => !!props.record?.id)

const form = reactive<AiAccount>({
  accountKey: '',
  accountName: '',
  provider: undefined,
  apiUrl: '',
  apiKey: '',
  apiSecret: '',
  accountTokens: undefined
})

const handleCancel = () => emit('update:visible', false)

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.assign(form, detail, {
      provider: detail.provider || undefined,
      apiUrl: detail.apiUrl || ''
    })
    accountKeyImmutable.value = detail.accountKeyImmutable === true
  } catch (e: any) {
    message.error(e?.message || '详情加载失败')
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.accountName?.trim()) {
    message.error('请填写账户名称')
    return
  }
  submitting.value = true
  try {
    const payload = { ...form }
    if (!isEdit.value) delete payload.id
    const msg = isEdit.value ? await aiAccountApi.update(payload) : await aiAccountApi.create(payload)
    message.success(msg || '操作成功')
    emit('update:visible', false)
    emit('success')
  } catch (e: any) {
    message.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    // 先清空表单，避免数据残留
    Object.assign(form, {
      id: undefined,
      accountKey: '',
      accountName: '',
      provider: undefined,
      apiUrl: '',
      apiKey: '',
      apiSecret: '',
      accountTokens: undefined
    })
    accountKeyImmutable.value = false
    
    if (props.record?.id) {
      loadDetail(props.record.id)
    }
  }
})
</script>

<style scoped>
.account-form-shell {
  --primary-color: #3b82f6;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.account-form-spin-wrap {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.account-form-spin-wrap :deep(.ant-spin-nested-loading) {
  flex: 1;
  min-height: 0;
  display: flex !important;
  flex-direction: column;
}

.account-form-spin-wrap :deep(.ant-spin-container) {
  flex: 1;
  min-height: 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

:global(.account-form-fsm-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.account-form-fsm-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

:global(.account-form-fsm-wrap--edit .fsm-logo-box) {
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 6px rgba(22, 163, 74, 0.35);
}

.modal-body-content {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 8px 24px 20px;
}

/* 温和的 Alert */
.info-alert {
  background: #f1f5f9;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 24px;
}
.alert-content {
  display: flex; align-items: center; gap: 10px;
  font-size: 13px; color: #475569;
}
.alert-icon { color: #94a3b8; }

/* 表单分段卡片 */
.form-section {
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s;
}
.form-section:hover {
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}

.form-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* 输入控件优化 */
.field-label {
  font-size: 14px; font-weight: 600; color: #334155;
}
.field-label small { color: #94a3b8; font-weight: normal; margin-left: 4px; }

.premium-input, .premium-input-number {
  border-radius: 10px;
  padding: 8px 12px;
  border-color: #e2e8f0;
  background: #f8fafc;
}
.premium-input:focus, .premium-input:hover {
  background: #fff;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
}

/* Footer 优化 */
.premium-footer {
  flex-shrink: 0;
  padding: 14px 24px 20px;
  border-top: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-info {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: #94a3b8;
}
.sync-icon { color: #10b981; }

.footer-actions { display: flex; gap: 12px; }

.btn-cancel {
  border-radius: 10px; height: 44px; padding: 0 20px;
  border-color: #e2e8f0; color: #64748b; font-weight: 600;
}
.btn-submit {
  border-radius: 10px; height: 44px; padding: 0 24px;
  font-weight: 700; display: flex; align-items: center; gap: 8px;
  background: var(--primary-color); box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
}

:deep(.ant-form-item) { margin-bottom: 0; } /* 卡片已经有间距 */
:deep(.ant-form-item-label) { padding-bottom: 8px; }
.w-full { width: 100%; }

@media (max-width: 900px) {
  .form-layout {
    grid-template-columns: 1fr;
  }
}
</style>