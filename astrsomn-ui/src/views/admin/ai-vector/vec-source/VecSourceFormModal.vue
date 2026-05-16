<template>
  <AstrsomnDrawerShell
      :destroy-on-close="true"
      :mask-closable="true"
      :open="open"
      width="560px"
      @update:open="onOpenChange"
  >
    <template #icon>
      <DatabaseOutlined/>
    </template>

    <template #title>
      {{ mode === 'create' ? '新建向量源' : '编辑向量源' }}
    </template>

    <template #subtitle>
      选择数据源类型并配置连接参数
    </template>

    <a-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        class="drawer-form"
        layout="vertical"
    >
      <div class="form-section">
        <h3 class="section-headline">
          <IdcardOutlined/>
          基本信息
        </h3>

        <a-form-item label="名称" name="name">
          <a-input v-model:value="form.name" placeholder="如：Milvus-Production" size="large"/>
        </a-form-item>

        <a-form-item label="数据源类型" name="extensionCode">
          <ExtensionSelector
              :value="form.extensionCode"
              allow-clear
              extension-type="VECTOR_STORE"
              placeholder="选择扩展数据源类型"
              size="large"
              @update:value="onExtensionChange"
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-segmented
              v-model:value="form.status"
              :options="statusOptions"
              block
              class="status-segmented"
              size="large"
          />
        </a-form-item>
      </div>

      <div class="form-section">
        <h3 class="section-headline">
          <LinkOutlined/>
          连接配置
        </h3>

        <a-alert
            v-if="!form.extensionCode"
            message="请先选择数据源类型"
            show-icon
            style="margin-bottom: 12px"
            type="info"
        />

        <template v-else>
          <a-form-item
              v-for="code in visibleParamCodes"
              :key="code"
              :label="paramMeta[code].label"
              :name="code"
          >
            <a-input-password
                v-if="paramMeta[code].password"
                v-model:value="formRow[code]"
                :placeholder="paramMeta[code].placeholder"
                autocomplete="off"
                size="large"
            >
              <template v-if="paramMeta[code].prefix" #prefix>
                <component :is="paramMeta[code].prefix" style="color: var(--text-muted)"/>
              </template>
            </a-input-password>
            <a-input
                v-else
                v-model:value="formRow[code]"
                :placeholder="paramMeta[code].placeholder"
                size="large"
            >
              <template v-if="paramMeta[code].prefix" #prefix>
                <component :is="paramMeta[code].prefix" style="color: var(--text-muted)"/>
              </template>
            </a-input>
          </a-form-item>

          <a-collapse v-model:activeKey="advancedKeys" ghost>
            <a-collapse-panel key="adv" header="扩展配置">
              <a-form-item :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }" name="configJson">
                <div class="json-editor-wrapper">
                  <a-textarea
                      v-model:value="form.configJson"
                      :auto-size="{ minRows: 4, maxRows: 8 }"
                      class="mono-text"
                      placeholder='{"useTls": true}'
                  />
                </div>
              </a-form-item>
            </a-collapse-panel>
          </a-collapse>
        </template>
      </div>
    </a-form>

    <template #footer>
      <a-button :loading="testLoading" @click="testConnection">
        <template #icon>
          <ApiTwoTone/>
        </template>
        测试连接
      </a-button>
      <a-button :loading="confirmLoading" type="primary" @click="handleOk">
        <template #icon>
          <CheckCircleOutlined/>
        </template>
        保存
      </a-button>
    </template>
  </AstrsomnDrawerShell>
</template>

<script lang="ts" setup>
import {computed, nextTick, reactive, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import {
  ApiOutlined,
  ApiTwoTone,
  CheckCircleOutlined,
  DatabaseOutlined,
  GlobalOutlined,
  IdcardOutlined,
  KeyOutlined,
  LinkOutlined,
  LockOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import type {FormInstance, Rule} from 'ant-design-vue/es/form'
import AstrsomnDrawerShell from '@/components/home/AstrsomnDrawerShell.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selectors/ExtensionSelector.vue'
import type {AiVecSource} from '@/api/aiVecSource.ts'
import {aiVecSourceApi} from '@/api/aiVecSource.ts'

const props = defineProps<{ mode: 'create' | 'edit'; confirmLoading: boolean; initial: AiVecSource | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSource] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)
const syncingInitial = ref(false)
const advancedKeys = ref<string | string[]>([])
const testLoading = ref(false)

const statusOptions = [
  {label: '启用', value: 'enabled'},
  {label: '停用', value: 'disabled'}
]

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
  port: {label: '端口', placeholder: '6333', prefix: ApiOutlined},
  databaseName: {label: '数据库', placeholder: 'default', wide: true},
  username: {label: '用户名', placeholder: 'root', prefix: UserOutlined},
  password: {label: '密码', placeholder: '••••••', password: true, prefix: LockOutlined},
  token: {label: 'Token', placeholder: 'API Key', password: true, prefix: KeyOutlined}
}

type FormRow = AiVecSource & Record<string, string | undefined>

function emptyForm(): FormRow {
  return {
    name: '',
    extensionCode: undefined,
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
    name: [{required: true, message: '请输入向量源名称'}],
    extensionCode: [{required: true, message: '请选择数据源类型'}]
  }
  for (const code of paramCodes.value) {
    if (!paramMeta[code]) continue
    if (code === 'host' || code === 'port') {
      r[code] = [{required: true, message: `请填写${paramMeta[code].label}`}]
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

function onExtensionChange(val: string | undefined) {
  form.extensionCode = val as AiVecSource['extensionCode']
  onProviderChange()
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
      syncingInitial.value = true
      if (initial && Object.keys(initial).length > 0) assignFromInitial(initial)
      else Object.assign(form, emptyForm())
      await nextTick()
      syncingInitial.value = false
    }
)

async function handleOk() {
  await formRef.value?.validate()
  const payload: AiVecSource = {...form}
  emit('submit', payload)
}

const testConnection = async () => {
  testLoading.value = true
  try {
    await formRef.value?.validate()
    const payload: AiVecSource = {...form}
    const msg = await aiVecSourceApi.testConnection(payload)
    message.success(msg)
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '测试连接失败')
  } finally {
    testLoading.value = false
  }
}

function onOpenChange(val: boolean) {
  open.value = val
}
</script>

<style scoped>
.drawer-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.section-headline {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
}

.status-segmented {
  width: 100%;
}

.status-segmented :deep(.ant-segmented-item) {
  flex: 1;
  text-align: center;
}

.json-editor-wrapper {
  border: 1px solid var(--border-input);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-input);
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: var(--success);
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.15);
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
