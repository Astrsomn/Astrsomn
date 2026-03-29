<template>
  <a-modal
      :open="visible"
      :title="isEdit ? 'UPDATE_CREDENTIAL_NODE' : 'PROVISION_CREDENTIAL_NODE'"
      :width="640"
      :confirm-loading="submitting"
      :centered="true"
      destroy-on-close
      @ok="onSubmit"
      @cancel="handleCancel"
      class="dev-modal"
  >
    <a-spin :spinning="loading">
      <div v-if="accountKeyImmutable" class="immutable-banner">
        <LockOutlined class="icon" />
        <span class="text">该凭证已绑定至活跃接入端点 (Endpoints)，核心索引标识已锁定</span>
      </div>

      <a-form layout="vertical" :model="form" class="industrial-form">
        <div class="form-group">
          <div class="group-title">
            <IdcardOutlined /> 资产身份标识 / IDENTITY_INDEX
          </div>
          <a-row :gutter="20">
            <a-col :span="12">
              <a-form-item name="accountKey">
                <template #label><span class="label-txt">凭证唯一标识 (Key)</span></template>
                <a-input
                    v-model:value="form.accountKey"
                    :placeholder="accountKeyImmutable ? '' : 'SYSTEM_AUTO_GENERATE'"
                    :disabled="accountKeyImmutable"
                    class="dev-input mono"
                />
                <div class="helper-text">引用此凭证资产的唯一 Key，建议使用大写蛇形命名</div>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item
                  name="accountName"
                  :rules="[{ required: true, message: '请定义凭证展示名称' }]"
              >
                <template #label><span class="label-txt">凭证名称</span></template>
                <a-input
                    v-model:value="form.accountName"
                    placeholder="例如：OPENAI_PROD_01"
                    class="dev-input"
                    allow-clear
                />
                <div class="helper-text">在接入端点 (Endpoints) 配置中显示的识别名称</div>
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <div class="form-group security">
          <div class="group-title">
            <SafetyOutlined /> 安全鉴权凭证 / AUTH_CREDENTIALS
          </div>
          <a-row :gutter="20">
            <a-col :span="12">
              <a-form-item name="apiKey">
                <template #label><span class="label-txt">API_KEY</span></template>
                <a-input-password
                    v-model:value="form.apiKey"
                    placeholder="Secret API Key"
                    class="dev-input mono"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="apiSecret">
                <template #label><span class="label-txt">API_SECRET (Optional)</span></template>
                <a-input-password
                    v-model:value="form.apiSecret"
                    placeholder="供应商提供的 Secret 密钥"
                    class="dev-input mono"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <div class="form-group">
          <div class="group-title">
            <ControlOutlined /> 资产配额控制 / QUOTA_CIRCUIT_BREAKER
          </div>
          <a-row :gutter="20">
            <a-col :span="12">
              <a-form-item name="accountTokens">
                <template #label><span class="label-txt">Token 消耗上限</span></template>
                <a-input-number
                    v-model:value="form.accountTokens"
                    :min="0"
                    placeholder="UNLIMITED"
                    class="dev-input-number w-full mono"
                />
                <div class="helper-text">达到限额后，所有关联端点将自动熔断停止服务</div>
              </a-form-item>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </a-spin>

    <template #footer>
      <div class="modal-footer">
        <span class="footer-hint">变更将实时同步至所有下游推理实例 (Instances)</span>
        <div class="footer-btns">
          <a-button @click="handleCancel" class="dev-btn">DISCARD</a-button>
          <a-button type="primary" :loading="submitting" @click="onSubmit" class="dev-btn-primary">
            COMMIT_CHANGES
            </a-button>
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  LockOutlined,
  IdcardOutlined,
  SafetyOutlined,
  ControlOutlined
} from '@ant-design/icons-vue'
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
  apiKey: '',
  apiSecret: '',
  accountTokens: undefined
})

const handleCancel = () => emit('update:visible', false)

const loadDetail = async (id: string | number) => {
  loading.value = true
  try {
    const detail = await aiAccountApi.detail(id)
    Object.assign(form, detail)
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

    const msg = isEdit.value
        ? await aiAccountApi.update(payload)
        : await aiAccountApi.create(payload)

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
    if (props.record?.id) {
      loadDetail(props.record.id)
    } else {
      Object.assign(form, {
        id: undefined,
        accountKey: '',
        accountName: '',
        apiKey: '',
        apiSecret: '',
        accountTokens: undefined
      })
      accountKeyImmutable.value = false
    }
  }
})
</script>

<style scoped>
/* 样式重构：开发者精致工业风 */

/* 顶部 Banner */
.immutable-banner {
  background: #fffbe6;
  border: 1px solid #ffe58f;
  padding: 10px 16px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}
.immutable-banner .icon { color: #faad14; }
.immutable-banner .text { font-size: 13px; color: #856404; font-weight: 500; }

/* 表单分组 */
.form-group {
  margin-bottom: 32px;
}
.group-title {
  font-family: 'JetBrains Mono', sans-serif;
  font-size: 12px;
  font-weight: 700;
  color: #bfbfbf;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.label-txt {
  font-size: 13px;
  font-weight: 600;
  color: #262626;
}

.helper-text {
  font-size: 11px;
  color: #bfbfbf;
  margin-top: 4px;
}

/* 输入框统一风格 */
.dev-input, .dev-input-number {
  border-radius: 2px;
  background: #fcfcfc;
  border-color: #d9d9d9;
}
.dev-input:hover, .dev-input:focus {
  background: #fff;
}

.mono {
  font-family: 'JetBrains Mono', 'Roboto Mono', monospace;
  font-size: 13px;
}

/* Footer 布局 */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.footer-hint {
  font-size: 12px;
  color: #bfbfbf;
}
.footer-btns {
  display: flex;
  gap: 12px;
}

.dev-btn { border-radius: 2px; }
.dev-btn-primary { border-radius: 2px; background: #000; border: none; }
.dev-btn-primary:hover { background: #333; }

/* 响应式微调 */
.w-full { width: 100%; }

/* 全局覆盖 (Scoped) */
:deep(.ant-form-item-label) { padding-bottom: 6px; }
:deep(.ant-modal-title) { font-weight: 800; }
</style>