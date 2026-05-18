<template>
  <AstModal
      :body-height="'80vh'"
      :confirm-loading="confirmLoading"
      :confirm-text="mode === 'create' ? '保存并同步服务' : '保存修改'"
      :destroy-on-close="true"
      :header-height="'72px'"
      :max-width="'80vw'"
      :open="open"
      :width="'80vw'"
      @confirm="handleOk"
      @update:open="onOpenChange"
  >
    <template #header-logo>
      <div :class="form.type?.toLowerCase()" class="icon-box">
        <ApiOutlined v-if="form.type === 'SSE'"/>
        <ConsoleSqlOutlined v-else-if="form.type === 'STDIO'"/>
        <RocketOutlined v-else/>
      </div>
    </template>

    <template #header-title>
      {{ mode === 'create' ? '注册 MCP 服务' : '编辑 MCP 配置' }}
    </template>

    <template #header-subtitle>
      连接外部工具能力，扩展智能体的专业技能边界
    </template>

    <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="professional-form"
        layout="vertical"
    >
      <div class="form-body-container">
        <div class="form-layout">
          <!-- 左侧：基础信息 -->
          <div class="form-left">
            <div class="section-card">
              <h3 class="section-title">
                <IdcardOutlined/>
                基础设定
              </h3>

              <a-alert
                  v-if="mode === 'create'"
                  class="custom-alert"
                  message="MCP Key 可留空，系统将根据名称自动生成唯一标识。"
                  show-icon
                  type="info"
              />

              <div class="form-fields">
                <a-row :gutter="16">
                  <a-col :span="16">
                    <a-form-item label="服务展示名称" name="serverName">
                      <a-input v-model:value="form.serverName" placeholder="例如：Google Search API" size="large"/>
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item label="服务状态" name="enabled">
                      <a-select v-model:value="form.enabled" size="large">
                        <a-select-option :value="1">已启用</a-select-option>
                        <a-select-option :value="0">已禁用</a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </a-row>

                <a-form-item label="MCP Key (识别码)" name="mcpKey">
                  <AstKeyGenerator
                      v-model="form.mcpKey"
                      :disabled="mode === 'edit'"
                      :prefix="AI_MCP_KEY_PREFIX"
                      placeholder="留空则服务端自动生成"
                      size="large"
                  />
                </a-form-item>

                <a-form-item label="协议类型" name="type">
                  <a-segmented v-model:value="form.type" :options="mcpTypeOptions" block size="large"/>
                </a-form-item>

                <a-form-item label="服务描述" name="description">
                  <a-textarea
                      v-model:value="form.description"
                      :auto-size="{ minRows: 4, maxRows: 6 }"
                      placeholder="详述此 MCP 服务的功能及用途..."
                  />
                </a-form-item>
              </div>
            </div>
          </div>

          <!-- 右侧：协议配置 -->
          <div class="form-right">
            <div class="section-card">
              <h3 class="section-title">
                <LinkOutlined v-if="form.type === 'SSE'"/>
                <CodeOutlined v-else-if="form.type === 'STDIO'"/>
                <RocketOutlined v-else/>
                {{ form.type }} 通讯配置
              </h3>

              <!-- SSE 协议配置 -->
              <div v-if="form.type === 'SSE'" class="protocol-box">
                <div class="form-fields">
                  <a-form-item label="SSE 服务地址" name="sseAddress">
                    <a-input v-model:value="form.sseAddress" placeholder="https://mcp-server.example.com/sse"
                             size="large">
                      <template #prefix>
                        <GlobalOutlined style="color: #bfbfbf"/>
                      </template>
                    </a-input>
                  </a-form-item>

                  <a-form-item label="请求头配置 (Headers JSON)" name="requestHeaderConfig">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.requestHeaderConfig"
                          :auto-size="{ minRows: 6, maxRows: 10 }"
                          class="mono-text"
                          placeholder='{"Authorization": "Bearer your_token"}'
                      />
                    </div>
                  </a-form-item>
                </div>
              </div>

              <!-- STDIO 协议配置 -->
              <div v-else-if="form.type === 'STDIO'" class="protocol-box">
                <div class="form-fields">
                  <a-form-item label="执行命令" name="command">
                    <a-input v-model:value="form.command" placeholder="npx / python / node" size="large">
                      <template #prefix>
                        <RightSquareOutlined style="color: #bfbfbf"/>
                      </template>
                    </a-input>
                  </a-form-item>

                  <a-form-item label="启动参数" name="args">
                    <a-textarea
                        v-model:value="form.args"
                        :auto-size="{ minRows: 3, maxRows: 4 }"
                        class="mono-text"
                        placeholder="请输入启动参数，支持空格分隔或 JSON 数组格式"
                    />
                  </a-form-item>

                  <a-form-item label="环境变量 (Environment Variables)" name="envVars">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.envVars"
                          :auto-size="{ minRows: 6, maxRows: 10 }"
                          class="mono-text"
                          placeholder='{"API_KEY": "sk-xxx"}'
                      />
                    </div>
                  </a-form-item>
                </div>
              </div>

              <!-- STEAMABLE 协议配置 -->
              <div v-else class="protocol-box">
                <div class="form-fields">
                  <a-form-item label="Steamable 配置" name="steamableConfig">
                    <div class="json-editor-wrapper">
                      <a-textarea
                          v-model:value="form.steamableConfig"
                          :auto-size="{ minRows: 8, maxRows: 12 }"
                          class="mono-text"
                          placeholder='{"endpoint": "wss://example.com/stream", "auth": {"type": "bearer", "token": "xxx"}}'
                      />
                    </div>
                  </a-form-item>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>
  </AstModal>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import {
  ApiOutlined,
  CodeOutlined,
  ConsoleSqlOutlined,
  GlobalOutlined,
  IdcardOutlined,
  LinkOutlined,
  RightSquareOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiMcp} from '@/api/aiMcp.ts'
import AstModal from '@/components/home/AstModal.vue'
import AstKeyGenerator from '@/components/home/AstKeyGenerator.vue'
import {AI_MCP_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes.ts'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiMcp | null }>()
const emit = defineEmits<{ submit: [payload: AiMcp] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const mcpTypeOptions = [
  {label: 'SSE (远程)', value: 'SSE'},
  {label: 'STDIO (本地)', value: 'STDIO'},
  {label: 'STEAMABLE', value: 'STEAMABLE'}
]

function emptyForm(): AiMcp {
  return {
    mcpKey: '', serverName: '', description: '', type: 'SSE',
    sseAddress: '', requestHeaderConfig: '', enabled: 1,
    command: '', args: '', envVars: '', steamableConfig: ''
  }
}

const form = reactive<AiMcp>(emptyForm())

const rules = {
  serverName: [{required: true, message: '请输入服务名称'}],
  type: [{required: true, message: '请选择协议类型'}],
  sseAddress: [{required: true, message: 'SSE 地址不能为空', trigger: 'blur'}]
}

const onOpenChange = (val: boolean) => {
  open.value = val
}

function assignFromInitial(src: AiMcp) {
  Object.assign(form, emptyForm(), src)
  if (form.enabled !== 0 && form.enabled !== 1) {
    form.enabled = form.enabled == null ? 1 : Number(form.enabled) === 0 ? 0 : 1
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
  const payload: AiMcp = {...form}
  if (props.mode === 'create' && !String(payload.mcpKey || '').trim()) {
    delete payload.mcpKey
  }
  emit('submit', payload)
}

</script>

<style scoped>
/* 图标样式 */
.icon-box {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: white;
  transition: 0.3s;
}

.icon-box.sse {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}

.icon-box.stdio {
  background: linear-gradient(135deg, #52c41a, #95de64);
}

.icon-box.steamable {
  background: linear-gradient(135deg, #722ed1, #b37feb);
}

/* 核心布局 */
.professional-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.form-body-container {
  flex: 1;
  overflow: hidden;
  padding: 0;
}

.form-layout {
  display: flex;
  height: 100%;
  gap: 0;
}

/* 左侧区域 */
.form-left {
  width: 45%;
  padding: 24px 32px;
  overflow-y: auto;
  border-right: 1px solid #e2e8f0;
  background: #fafbfc;
}

/* 右侧区域 */
.form-right {
  width: 55%;
  padding: 24px 32px;
  overflow-y: auto;
  background: #fff;
}

/* 区域卡片 */
.section-card {
  height: 100%;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

/* 表单字段 */
.form-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 协议配置区 */
.protocol-box {
  animation: fadeIn 0.3s ease;
}

.json-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  background: #fafafa;
  transition: 0.3s;
}

.json-editor-wrapper:focus-within {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
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

/* 其他 */
.custom-alert {
  margin-bottom: 16px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
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
