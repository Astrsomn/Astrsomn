<template>
  <AstDrawer
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
      {{ mode === 'create' ? t.vecSource.form.createTitle : t.vecSource.form.editTitle }}
    </template>

    <template #subtitle>
      {{ t.vecSource.form.subtitle }}
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
          {{ t.vecSource.form.basicInfo }}
        </h3>

        <a-form-item :label="t.vecSource.form.name.label" name="name">
          <a-input v-model:value="form.name" :placeholder="t.vecSource.form.name.placeholder" size="large"/>
        </a-form-item>

        <a-form-item :label="t.vecSource.form.extensionCode.label" name="extensionCode">
          <ExtensionSelector
              :value="form.extensionCode"
              allow-clear
              extension-type="VECTOR_STORE"
              :placeholder="t.vecSource.form.extensionCode.placeholder"
              size="large"
              @update:value="onExtensionChange"
          />
        </a-form-item>

        <a-form-item :label="t.vecSource.form.status.label" name="status">
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
          {{ t.vecSource.form.connectionConfig }}
        </h3>

        <a-alert
            v-if="!form.extensionCode"
            :message="t.vecSource.form.selectSourceFirst"
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
            <a-collapse-panel key="adv" :header="t.vecSource.form.advancedConfig">
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
        {{ t.vecSource.form.testConnection }}
      </a-button>
      <a-button :loading="confirmLoading" type="primary" @click="handleOk">
        <template #icon>
          <CheckCircleOutlined/>
        </template>
        {{ t.vecSource.form.save }}
      </a-button>
    </template>
  </AstDrawer>
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
import AstDrawer from '@/components/home/AstDrawer.vue'
import ExtensionSelector from '@/views/admin/system-config/system-extension/selector/ExtensionSelector.vue'
import type {AiVecSource} from '@/api/aiVecSource.ts'
import {aiVecSourceApi} from '@/api/aiVecSource.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{ mode: 'create' | 'edit'; confirmLoading: boolean; initial: AiVecSource | null }>()
const emit = defineEmits<{ submit: [payload: AiVecSource] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)
const syncingInitial = ref(false)
const advancedKeys = ref<string | string[]>([])
const testLoading = ref(false)

const statusOptions = computed(() => [
  {label: t.value.vecSource.form.enabled, value: 'enabled'},
  {label: t.value.vecSource.form.disabled, value: 'disabled'}
])

type ParamMeta = {
  label: string
  placeholder: string
  password?: boolean
  wide?: boolean
  prefix?: typeof GlobalOutlined
}

const paramMeta = computed<Record<string, ParamMeta>>(() => ({
  host: {label: t.value.vecSource.form.paramMeta.host.label, placeholder: t.value.vecSource.form.paramMeta.host.placeholder, prefix: GlobalOutlined},
  port: {label: t.value.vecSource.form.paramMeta.port.label, placeholder: t.value.vecSource.form.paramMeta.port.placeholder, prefix: ApiOutlined},
  databaseName: {label: t.value.vecSource.form.paramMeta.databaseName.label, placeholder: t.value.vecSource.form.paramMeta.databaseName.placeholder, wide: true},
  username: {label: t.value.vecSource.form.paramMeta.username.label, placeholder: t.value.vecSource.form.paramMeta.username.placeholder, prefix: UserOutlined},
  password: {label: t.value.vecSource.form.paramMeta.password.label, placeholder: t.value.vecSource.form.paramMeta.password.placeholder, password: true, prefix: LockOutlined},
  token: {label: t.value.vecSource.form.paramMeta.token.label, placeholder: t.value.vecSource.form.paramMeta.token.placeholder, password: true, prefix: KeyOutlined}
}))

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

const visibleParamCodes = computed(() => paramCodes.value.filter((c) => Boolean(paramMeta.value[c])))

const formRules = computed<Record<string, Rule[]>>(() => {
  const r: Record<string, Rule[]> = {
    name: [{required: true, message: t.value.vecSource.form.validation.nameRequired}],
    extensionCode: [{required: true, message: t.value.vecSource.form.validation.extensionCodeRequired}]
  }
  for (const code of paramCodes.value) {
    if (!paramMeta.value[code]) continue
    if (code === 'host' || code === 'port') {
      r[code] = [{required: true, message: t.value.vecSource.form.validation.fieldRequired.replace('{label}', paramMeta.value[code].label)}]
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
    message.error(err?.message || t.value.vecSource.form.testFailed)
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
  box-shadow: var(--success-shadow);
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
