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
            <p>从已同步的向量驱动选择类型，并按驱动支持的参数填写连接信息</p>
          </div>
        </div>
      </div>
    </div>

    <a-form
      ref="formRef"
      :model="form"
      :rules="formRules"
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

            <a-form-item label="向量驱动" name="provider">
              <a-select
                v-model:value="form.provider"
                placeholder="从 AI_VEC_DRIVER 选择驱动"
                size="large"
                allow-clear
                :loading="driversLoading"
                show-search
                option-filter-prop="label"
                :options="driverSelectOptions"
                @change="onProviderChange"
              />
            </a-form-item>

            <a-form-item label="状态" name="status" class="span-2">
              <div class="status-card">
                <div class="info">
                  <span class="t">启用此向量源</span>
                  <span class="d">启用后服务端会注册运行时连接；关闭后释放连接且该源不可用</span>
                </div>
                <a-switch v-model:checked="statusChecked" />
              </div>
            </a-form-item>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-headline"><LinkOutlined /> 2. 连接配置</h3>

          <a-alert
            v-if="!form.provider"
            type="info"
            show-icon
            message="请先选择向量驱动"
            class="span-2"
            style="margin-bottom: 12px"
          />

          <div v-else class="form-grid">
            <a-form-item
              v-for="code in visibleParamCodes"
              :key="code"
              :label="paramMeta[code].label"
              :name="code"
              :class="paramMeta[code].wide ? 'span-2' : ''"
            >
                <a-input-password
                  v-if="paramMeta[code].password"
                  v-model:value="formRow[code]"
                  :placeholder="paramMeta[code].placeholder"
                  size="large"
                  autocomplete="off"
                >
                  <template v-if="paramMeta[code].prefix" #prefix>
                    <component :is="paramMeta[code].prefix" style="color: #bfbfbf" />
                  </template>
                </a-input-password>
                <a-input
                  v-else
                  v-model:value="formRow[code]"
                  :placeholder="paramMeta[code].placeholder"
                  size="large"
                >
                  <template v-if="paramMeta[code].prefix" #prefix>
                    <component :is="paramMeta[code].prefix" style="color: #bfbfbf" />
                  </template>
                </a-input>
            </a-form-item>

            <a-collapse v-model:activeKey="advancedKeys" ghost class="span-2">
              <a-collapse-panel key="adv" header="高级：扩展配置 (JSON)">
                <a-form-item name="configJson" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
                  <div class="json-editor-wrapper">
                    <a-textarea
                      v-model:value="form.configJson"
                      :auto-size="{ minRows: 4, maxRows: 8 }"
                      placeholder='例如 Qdrant TLS：{"useTls": true}'
                      class="mono-text"
                    />
                  </div>
                </a-form-item>
              </a-collapse-panel>
            </a-collapse>
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
        <a-button type="primary" class="btn-submit" :loading="confirmLoading" @click="handleOk">
          保存配置
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref, watch } from 'vue'
import {
  DatabaseOutlined,
  IdcardOutlined,
  LinkOutlined,
  GlobalOutlined,
  ApiOutlined,
  UserOutlined,
  LockOutlined,
  KeyOutlined,
  SafetyCertificateOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue/es/form'
import type { Rule } from 'ant-design-vue/es/form'
import type { AiVecSource } from '@/api/aiVecSource'
import { aiVecDriverApi, type AiVecDriver } from '@/api/aiVecDriver'

const props = defineProps<{ mode: 'create' | 'edit'; confirmLoading: boolean; initial: AiVecSource | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSource] }>()
const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)
const drivers = ref<AiVecDriver[]>([])
const driversLoading = ref(false)
const syncingInitial = ref(false)
const advancedKeys = ref<string | string[]>([])

type ParamMeta = {
  label: string
  placeholder: string
  password?: boolean
  wide?: boolean
  prefix?: typeof GlobalOutlined
}

const paramMeta: Record<string, ParamMeta> = {
  host: {
    label: '主机地址 (Host)',
    placeholder: '例如：localhost 或 192.168.1.100',
    prefix: GlobalOutlined
  },
  port: { label: '端口 (Port)', placeholder: '例如：6334 / 19530', prefix: ApiOutlined },
  databaseName: { label: '数据库 / 库名', placeholder: '例如：default', wide: true },
  username: { label: '用户名', placeholder: '用户名', prefix: UserOutlined },
  password: { label: '密码', placeholder: '密码', password: true, prefix: LockOutlined },
  token: { label: 'API Token / Key', placeholder: 'API Key 或 Token', password: true, prefix: KeyOutlined }
}

type FormRow = AiVecSource & Record<string, string | undefined>

function emptyForm(): FormRow {
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

const form = reactive<FormRow>(emptyForm())
const formRow = form as Record<string, string | undefined>

const statusChecked = computed({
  get: () => form.status === 'ENABLED',
  set: (v: boolean) => {
    form.status = v ? 'ENABLED' : 'DISABLED'
  }
})

const driverSelectOptions = computed(() =>
  drivers.value
    .filter((d) => d.provider)
    .map((d) => ({
      value: d.provider as string,
      label: `${d.driverName ?? d.provider} (${d.provider})`
    }))
)

const selectedDriver = computed(() =>
  drivers.value.find((d) => d.provider === form.provider)
)

const paramCodes = computed(() => {
  const raw = selectedDriver.value?.params
  if (!raw || !String(raw).trim()) return []
  try {
    const j = JSON.parse(String(raw)) as unknown
    return Array.isArray(j) ? (j as string[]).filter((c) => typeof c === 'string') : []
  } catch {
    return []
  }
})

const visibleParamCodes = computed(() => paramCodes.value.filter((c) => Boolean(paramMeta[c])))

const formRules = computed<Record<string, Rule[]>>(() => {
  const r: Record<string, Rule[]> = {
    name: [{ required: true, message: '请输入向量源名称' }],
    provider: [{ required: true, message: '请选择向量驱动' }]
  }
  for (const code of paramCodes.value) {
    if (!paramMeta[code]) continue
    r[code] = [{ required: true, message: `请填写${paramMeta[code].label}` }]
  }
  return r
})

function clearConnectionFields() {
  form.host = ''
  form.port = ''
  form.username = ''
  form.password = ''
  form.databaseName = ''
  form.token = ''
  form.configJson = ''
}

function onProviderChange() {
  if (syncingInitial.value) return
  clearConnectionFields()
}

async function loadDrivers() {
  driversLoading.value = true
  try {
    drivers.value = await aiVecDriverApi.list()
  } catch {
    drivers.value = []
  } finally {
    driversLoading.value = false
  }
}

function assignFromInitial(src: AiVecSource) {
  Object.assign(form, emptyForm(), src)
  if (!form.status) {
    form.status = 'ENABLED'
  }
}

watch(
  () => [open.value, props.initial] as const,
  async ([isOpen, initial]) => {
    if (!isOpen) return
    await loadDrivers()
    syncingInitial.value = true
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
    await nextTick()
    syncingInitial.value = false
  }
)

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecSource = { ...form }
  emit('submit', payload)
}

const onCancel = () => {
  open.value = false
}
</script>

<style scoped>
.premium-vecsource-modal :deep(.ant-modal-content) {
  padding: 0;
  border-radius: 20px;
  overflow: hidden;
}

.modal-header-gradient {
  background: #fff;
  padding: 32px 40px;
  border-bottom: 1px solid #f0f2f5;
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
  background: #10b981;
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.2);
}
.text-group h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #111;
}
.text-group p {
  margin: 4px 0 0;
  color: #999;
  font-size: 13px;
}

.provider-code {
  color: #999;
  font-size: 12px;
  font-weight: 400;
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
  background: #eee;
  border-radius: 4px;
}

.section-headline {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}
.span-2 {
  grid-column: span 2;
}

.status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fb;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #eef1f6;
}
.status-card .t {
  display: block;
  font-size: 13px;
  font-weight: 600;
}
.status-card .d {
  font-size: 12px;
  color: #999;
}

.json-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  background: #fafafa;
  transition: 0.3s;
}
.json-editor-wrapper:focus-within {
  border-color: #10b981;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.1);
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
  background: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-left {
  font-size: 12px;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 6px;
}
.btn-flat {
  border: none;
  color: #999;
  font-weight: 600;
}
.btn-submit {
  border-radius: 8px;
  font-weight: 600;
  height: 38px;
  padding: 0 24px;
  background: #10b981;
  border-color: #10b981;
}
.btn-submit:hover,
.btn-submit:focus {
  background: #059669;
  border-color: #059669;
}
</style>
