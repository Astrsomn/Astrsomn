<template>
  <AstrsomnModal
    v-model:open="open"
    width="860px"
    body-height="600px"
    :destroy-on-close="true"
    @cancel="onCancel"
  >
    <template #header-logo>
      <DatabaseOutlined />
    </template>

    <template #header-title>
      {{ mode === 'create' ? '新建向量源' : '编辑向量源' }}
    </template>

    <template #header-subtitle>
      选择数据源类型并配置连接参数
    </template>

    <template #header-actions>
      <AstrsomnSegmentedButton :buttons="headerButtons" />
    </template>

    <a-form
      ref="formRef"
      :model="form"
      :rules="formRules"
      layout="vertical"
      class="professional-form"
    >
      <div class="form-body-container">
        <div class="form-section">
          <h3 class="section-headline"><IdcardOutlined /> 基本信息</h3>

          <div class="form-grid">
            <a-form-item label="名称" name="name">
              <a-input v-model:value="form.name" placeholder="如：Milvus-Production" size="large" />
            </a-form-item>

            <a-form-item label="数据源类型" name="provider">
              <a-select
                v-model:value="form.provider"
                placeholder="选择扩展数据源类型"
                size="large"
                allow-clear
                :loading="extensionsLoading"
                show-search
                option-filter-prop="label"
                :options="extensionSelectOptions"
                @change="onProviderChange"
              />
            </a-form-item>

            <a-form-item label="状态" name="status" class="span-2">
              <div class="status-card">
                <span class="status-label">启用</span>
                <a-switch v-model:checked="statusChecked" />
              </div>
            </a-form-item>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-headline"><LinkOutlined /> 连接配置</h3>

          <a-alert
            v-if="!form.provider"
            type="info"
            show-icon
            message="请先选择数据源类型"
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
              <a-collapse-panel key="adv" header="扩展配置">
                <a-form-item name="configJson" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
                  <div class="json-editor-wrapper">
                    <a-textarea
                      v-model:value="form.configJson"
                      :auto-size="{ minRows: 4, maxRows: 8 }"
                      placeholder='{"useTls": true}'
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
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  DatabaseOutlined,
  IdcardOutlined,
  LinkOutlined,
  GlobalOutlined,
  ApiOutlined,
  UserOutlined,
  LockOutlined,
  KeyOutlined,
  ApiTwoTone,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue/es/form'
import type { Rule } from 'ant-design-vue/es/form'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import AstrsomnSegmentedButton, { type SegmentedButton } from '@/components/home/AstrsomnSegmentedButton.vue'
import type { AiVecSource } from '@/api/aiVecSource.ts'
import { aiVecSourceApi } from '@/api/aiVecSource.ts'
import { systemExtensionApi, type SystemExtension } from '@/api/systemExtension.ts'

const props = defineProps<{ mode: 'create' | 'edit'; confirmLoading: boolean; initial: AiVecSource | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSource] }>()
const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)
const vectorExtensions = ref<SystemExtension[]>([])
const extensionsLoading = ref(false)
const syncingInitial = ref(false)
const advancedKeys = ref<string | string[]>([])
const testLoading = ref(false)

type ParamMeta = {
  label: string
  placeholder: string
  password?: boolean
  wide?: boolean
  prefix?: typeof GlobalOutlined
}

const paramMeta: Record<string, ParamMeta> = {
  host: {
    label: '主机',
    placeholder: 'localhost',
    prefix: GlobalOutlined
  },
  port: { label: '端口', placeholder: '6333', prefix: ApiOutlined },
  databaseName: { label: '数据库', placeholder: 'default', wide: true },
  username: { label: '用户名', placeholder: 'root', prefix: UserOutlined },
  password: { label: '密码', placeholder: '••••••', password: true, prefix: LockOutlined },
  token: { label: 'Token', placeholder: 'API Key', password: true, prefix: KeyOutlined }
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
    status: 'enabled'
  }
}

const form = reactive<FormRow>(emptyForm())
const formRow = form as Record<string, string | undefined>

const statusChecked = computed({
  get: () => form.status === 'enabled',
  set: (v: boolean) => {
    form.status = v ? 'enabled' : 'disabled'
  }
})

const headerButtons = computed<SegmentedButton[]>(() => [
  {
    label: '测试',
    type: 'default',
    icon: ApiTwoTone,
    loading: testLoading.value,
    onClick: testConnection
  },
  {
    label: '保存',
    type: 'primary',
    icon: CheckCircleOutlined,
    loading: props.confirmLoading,
    onClick: handleOk
  }
])

const extensionSelectOptions = computed(() =>
  vectorExtensions.value
    .filter((d) => d.extensionCode)
    .map((d) => ({
      value: d.extensionCode as string,
      label: `${d.extensionName ?? d.extensionCode} (${d.extensionCode})`
    }))
)

const providerParamMap: Record<string, string[]> = {
  qdrant: ['host', 'port', 'token'],
  chroma: ['host', 'port'],
  milvus: ['host', 'port', 'username', 'password', 'databaseName']
}
const paramCodes = computed(() => {
  const provider = String(form.provider || '').toLowerCase()
  return providerParamMap[provider] || ['host', 'port']
})

const visibleParamCodes = computed(() => paramCodes.value.filter((c) => Boolean(paramMeta[c])))

const formRules = computed<Record<string, Rule[]>>(() => {
  const r: Record<string, Rule[]> = {
    name: [{ required: true, message: '请输入向量源名称' }],
    provider: [{ required: true, message: '请选择数据源类型' }]
  }
  for (const code of paramCodes.value) {
    if (!paramMeta[code]) continue
    if (code === 'host' || code === 'port') {
      r[code] = [{ required: true, message: `请填写${paramMeta[code].label}` }]
    }
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

async function loadVectorExtensions() {
  extensionsLoading.value = true
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 200,
      param: {
        type: 'VECTOR_STORE',
        status: 'INSTALLED'
      }
    })
    vectorExtensions.value = resp.list || []
  } catch {
    vectorExtensions.value = []
  } finally {
    extensionsLoading.value = false
  }
}

function assignFromInitial(src: AiVecSource) {
  Object.assign(form, emptyForm(), src)
  if (!form.status) {
    form.status = 'enabled'
  }
}

watch(
  () => [open.value, props.initial] as const,
  async ([isOpen, initial]) => {
    if (!isOpen) return
    await loadVectorExtensions()
    syncingInitial.value = true
    if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
    else Object.assign(form, emptyForm())
    await nextTick()
    syncingInitial.value = false
  }
)

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecSource = { ...form, extensionCode: form.provider }
  emit('submit', payload)
}

const testConnection = async () => {
  testLoading.value = true
  try {
    await formRef.value?.validate()
    const payload: AiVecSource = { ...form, extensionCode: form.provider }
    const msg = await aiVecSourceApi.testConnection(payload)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '测试连接失败')
  } finally {
    testLoading.value = false
  }
}

const onCancel = () => {
  open.value = false
}
</script>

<style scoped>
.professional-form {
  height: 100%;
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

.status-label {
  font-size: 13px;
  font-weight: 600;
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
</style>
