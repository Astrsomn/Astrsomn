<template>
  <AstrsomnModal
      :body-height="'80vh'"
      :closable="false"
      :destroy-on-close="true"
      :header-height="'72px'"
      :max-width="'80vw'"
      :open="open"
      :width="'80vw'"
      @cancel="onCancel"
      @update:open="onOpenChange"
  >
    <template #header-logo>
      <div :class="form.type" class="icon-box">
        <BuildOutlined v-if="form.type === 'method'"/>
        <Html5Outlined v-else/>
      </div>
    </template>

    <template #header-title>
      {{ mode === 'create' ? '构建新增强工具' : '编辑工具配置' }}
    </template>

    <template #header-subtitle>
      定义 AI 智能体可调用的外部函数或界面渲染组件
    </template>

    <template #header-actions>
      <div class="header-actions">
        <a-button class="btn-flat" @click="onCancel">取消</a-button>
        <a-button :loading="confirmLoading" class="btn-submit" type="primary" @click="handleOk">
          {{ mode === 'create' ? '注册工具并发布' : '保存修改' }}
        </a-button>
      </div>
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
                基础标识
              </h3>

              <a-alert
                  v-if="mode === 'create'"
                  class="custom-alert"
                  message="Tool Key 是工具的唯一逻辑标识，建议使用下划线命名（如 weather_api）。"
                  show-icon
                  type="info"
              />

              <div class="form-fields">
                <a-row :gutter="16">
                  <a-col :span="16">
                    <a-form-item label="工具显示名称" name="toolName">
                      <a-input v-model:value="form.toolName" placeholder="例如：实时天气查询" size="large"/>
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item label="状态" name="enableFlag">
                      <a-select v-model:value="form.enableFlag" size="large">
                        <a-select-option value="enabled">已启用</a-select-option>
                        <a-select-option value="disabled">已禁用</a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </a-row>

                <a-form-item label="Tool Key (逻辑标识)" name="toolKey">
                  <AstrsomnKeyGenerator
                      v-model="form.toolKey"
                      :disabled="mode === 'edit'"
                      :prefix="AI_TOOL_KEY_PREFIX"
                      placeholder="留空则由系统自动生成"
                      size="large"
                  />
                </a-form-item>

                <a-form-item label="实现类型" name="type">
                  <a-segmented v-model:value="form.type" :options="toolTypeOptions" block size="large"/>
                </a-form-item>

                <a-form-item label="功能详细描述 (给 AI 看)" name="description">
                  <a-textarea
                      v-model:value="form.description"
                      :auto-size="{ minRows: 6, maxRows: 10 }"
                      placeholder="请清晰描述工具的功能及其参数含义，这有助于大模型更准确地进行 Tool Call..."
                  />
                </a-form-item>
              </div>
            </div>
          </div>

          <!-- 右侧：执行配置 -->
          <div class="form-right">
            <div class="section-card">
              <h3 class="section-title">
                <RocketOutlined/>
                执行配置
              </h3>

              <div class="impl-hint">
                <div class="hint-title">Spring Context 注入配置</div>
                <p>系统将通过指定的 Bean 名称从 Spring 容器中索引实例，并反射执行目标方法。</p>
              </div>

              <div class="form-fields">
                <a-form-item label="Spring Bean ID" name="beanName">
                  <a-input v-model:value="form.beanName" placeholder="例如：weatherToolService" size="large">
                    <template #prefix>
                      <BlockOutlined style="color: #bfbfbf"/>
                    </template>
                  </a-input>
                </a-form-item>

                <a-form-item label="执行方法名 (Method)" name="methodName">
                  <a-input v-model:value="form.methodName" placeholder="例如：getWeather" size="large">
                    <template #prefix>
                      <CodeOutlined style="color: #bfbfbf"/>
                    </template>
                  </a-input>
                </a-form-item>

                <a-form-item label="Class 名称" name="className">
                  <a-input v-model:value="form.className" placeholder="例如：WeatherToolImpl" size="large"/>
                </a-form-item>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-form>
  </AstrsomnModal>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import {
  BlockOutlined,
  BuildOutlined,
  CodeOutlined,
  Html5Outlined,
  IdcardOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'
import type {FormInstance} from 'ant-design-vue'
import type {AiTool} from '@/api/aiTool.ts'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import AstrsomnKeyGenerator from '@/components/home/AstrsomnKeyGenerator.vue'
import {AI_TOOL_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes'

const props = defineProps<{ mode: 'create' | 'edit', confirmLoading: boolean, initial: AiTool | null }>()
const emit = defineEmits<{ submit: [payload: AiTool] }>()
const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const toolTypeOptions = [
  {label: 'Method (后端)', value: 'method'},
  {label: 'HTML (组件)', value: 'html'}
]

function emptyForm(): AiTool {
  return {
    toolKey: '', toolName: '', description: '',
    beanName: '', methodName: '', type: 'method', enableFlag: 'enabled',
    className: ''
  }
}

const form = reactive<AiTool>(emptyForm())

const rules = {
  toolName: [{required: true, message: '请输入工具名称'}],
  type: [{required: true, message: '请选择实现类型'}],
  beanName: [{required: true, message: 'Bean ID 不能为空'}],
  methodName: [{required: true, message: '方法名不能为空'}]
}

const onOpenChange = (val: boolean) => {
  open.value = val
}

function assignFromInitial(src: AiTool) {
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
  const payload: AiTool = {...form}
  if (props.mode === 'create' && !String(payload.toolKey || '').trim()) {
    delete payload.toolKey
  }
  emit('submit', payload)
}

const onCancel = () => {
  open.value = false
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

.icon-box.method {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
}

.icon-box.html {
  background: linear-gradient(135deg, #fa8c16, #ffd666);
}

/* 头部操作按钮 */
.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
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

/* 提示块 */
.impl-hint {
  background: #f0f5ff;
  border: 1px solid #adc6ff;
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.hint-title {
  font-size: 14px;
  font-weight: 700;
  color: #1d39c4;
  margin-bottom: 4px;
}

.impl-hint p {
  font-size: 13px;
  color: #2f54eb;
  margin: 0;
  opacity: 0.8;
}

/* 按钮样式 */
.btn-flat {
  border: none;
  color: #64748b;
  font-weight: 600;
}

.btn-flat:hover {
  color: #475569;
}

.btn-submit {
  border-radius: 8px;
  font-weight: 600;
  height: 38px;
  padding: 0 24px;
}

/* 其他 */
.mt-16 {
  margin-top: 16px;
}

.custom-alert {
  margin-bottom: 16px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}
</style>
