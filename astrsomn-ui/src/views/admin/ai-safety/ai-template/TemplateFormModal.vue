<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    :body-height="'80vh'"
    @update:open="(value) => open = value"
    @cancel="onCancel"
  >
    <template #header-logo>
      <div class="logo-box"><CodeOutlined /></div>
    </template>
    <template #header-title>
      <span class="main-title">{{ mode === 'create' ? '新增 FTL/ST 模板' : '编辑 FTL/ST 模板' }}</span>
    </template>
    <template #header-subtitle>
      <span class="sub-title">配置模板的基本信息和代码内容</span>
    </template>
    <template #header-actions>
      <div class="header-action-pair">
        <a-button class="header-action-btn header-action-btn-cancel" @click="onCancel">取消</a-button>
        <a-button
          type="primary"
          class="header-action-btn header-action-btn-save"
          :loading="confirmLoading"
          @click="handleOk"
        >
          保存模板
        </a-button>
      </div>
    </template>

    <div class="main-content">
      <section class="info-pane">
        <div class="pane-card ">
          <a-form
            ref="formRef"
            :model="form"
            :rules="rules"
            layout="vertical"
            class="template-form"
          >
            <div class="form-grid">
              <a-form-item label="Template Key" name="templateKey">
                <a-input
                  v-model:value="form.templateKey"
                  placeholder="同一逻辑多版本共用的 Key"
                  :disabled="mode === 'edit'"
                  size="large"
                />
              </a-form-item>

              <a-form-item label="标题" name="templateTitle">
                <a-input v-model:value="form.templateTitle" placeholder="展示名称" size="large" />
              </a-form-item>

              <a-form-item label="分类" name="category">
                <a-input v-model:value="form.category" placeholder="可选" allow-clear size="large" />
              </a-form-item>

              <a-form-item label="模板类型" name="templateType">
                <a-select
                  v-model:value="form.templateType"
                  :options="templateTypeOptions"
                  placeholder="选择引擎"
                  size="large"
                />
              </a-form-item>

              <a-form-item label="版本号" name="version">
                <a-input-number
                  v-model:value="form.version"
                  :min="1"
                  :precision="0"
                  class="w-full"
                  placeholder="默认 1"
                  size="large"
                  :disabled="true"
                />
              </a-form-item>

              <a-form-item label="状态" name="status">
                <a-select v-model:value="form.status" :options="statusOptions" size="large" />
              </a-form-item>
            </div>
          </a-form>
        </div>
      </section>

      <aside class="code-pane">
        <a-form-item name="content">
          <div class="editor-shell">
            <div class="editor-toolbar">
              <span class="editor-title">
                {{ form.templateType === 'FREEMARKER' ? 'FTL Code Editor' : 'Template Code Editor' }}
              </span>
              <span class="editor-hint">
                {{ form.templateType === 'FREEMARKER' ? '使用专用代码编辑框编辑 .ftl 模板' : '编辑 StringTemplate 正文' }}
              </span>
            </div>
            <Codemirror
              v-model="form.content"
              :extensions="editorExtensions"
              :autofocus="mode === 'create'"
              :indent-with-tab="true"
              :tab-size="2"
              class="content-editor"
            />
          </div>
        </a-form-item>
      </aside>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import { Codemirror } from 'vue-codemirror'
import { html } from '@codemirror/lang-html'
import { oneDark } from '@codemirror/theme-one-dark'
import { CodeOutlined } from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import type { AiTemplate } from '@/api/aiTemplate.ts'

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiTemplate | null
}>()

const emit = defineEmits<{
  submit: [payload: AiTemplate]
}>()

const open = defineModel<boolean>('open', { required: true })

const formRef = ref<FormInstance | null>(null)

const statusOptions = [
  { label: '启用', value: 'enabled' },
  { label: '禁用', value: 'disabled' }
]

const templateTypeOptions = [
  { label: 'Freemarker (.ftl)', value: 'FREEMARKER' },
  { label: 'StringTemplate (.st)', value: 'STRING_TEMPLATE' }
]

function emptyForm(): AiTemplate {
  return {
    templateKey: '',
    templateTitle: '',
    content: '',
    category: '',
    templateType: 'FREEMARKER',
    version: 1,
    status: 'enabled'
  }
}

const form = reactive<AiTemplate>(emptyForm())

const rules = {
  templateKey: [],
  templateTitle: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入模板内容' }],
  templateType: [{ required: true, message: '请选择模板类型' }],
  status: [{ required: true, message: '请选择状态' }]
}

const editorExtensions = computed(() => {
  if (form.templateType === 'FREEMARKER') {
    return [html(), oneDark]
  }
  return [oneDark]
})

function assignFromInitial(src: AiTemplate) {
  Object.assign(form, emptyForm(), src)
  const v = form.version
  if (v != null && v !== '') {
    const n = Number(v)
    form.version = Number.isFinite(n) ? n : 1
  } else {
    form.version = 1
  }
}

watch(
  () => [open.value, props.initial] as const,
  ([isOpen, initial]) => {
    if (!isOpen) return
    if (initial && Object.keys(initial).length > 0) {
      assignFromInitial(initial)
    } else {
      Object.assign(form, emptyForm())
    }
  }
)

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return Promise.reject(new Error('validation'))
  }
  emit('submit', { ...form })
}

function onCancel() {
  open.value = false
}
</script>

<style scoped>
/* Header */
.logo-box {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  flex-shrink: 0;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 6px rgba(29, 78, 216, 0.35);
}

.main-title {
  display: block;
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.sub-title {
  font-size: 12px;
  color: #94a3b8;
}

.header-actions {
  display: flex;
  align-items: center;
}

.header-action-pair {
  display: inline-flex;
  align-items: stretch;
}

.header-action-btn {
  height: 40px;
  min-width: 120px;
  padding: 0 22px;
  font-weight: 600;
}

.header-action-pair :deep(.header-action-btn-cancel.ant-btn) {
  border-top-left-radius: 14px;
  border-bottom-left-radius: 14px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.header-action-pair :deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
  border-right: none;
}

.header-action-pair :deep(.header-action-btn-cancel.ant-btn-default:hover) {
  color: #334155;
  border-color: #94a3b8;
  background: #f8fafc;
}

.header-action-pair :deep(.header-action-btn-save.ant-btn) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 14px;
  border-bottom-right-radius: 14px;
}

.header-action-pair :deep(.header-action-btn-save.ant-btn-primary) {
  margin-left: -1px;
  box-shadow: none;
}

/* 布局主体 */
.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
}



.pane-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

/* 左侧信息面板 */
.info-pane {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.template-form {
  margin-top: 4px;
  flex: 1;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

/* 右侧代码面板 */
.code-pane {
  width: 50%;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding: 0;
}

/* 编辑器样式 */
.editor-shell {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  background: #0f172a;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* 移除form-item的默认margin */
.code-pane :deep(.ant-form-item) {
  margin-bottom: 0;
  margin-top: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.code-pane :deep(.ant-form-item-control) {
  flex: 1;
  min-height: 0;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: #111827;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}

.editor-title {
  color: #f9fafb;
  font-size: 13px;
  font-weight: 600;
}

.editor-hint {
  color: #9ca3af;
  font-size: 12px;
}

.content-editor {
  font-size: 13px;
  flex: 1;
}

:deep(.content-editor .cm-editor) {
  height: 100%;
  min-height: 400px;
}

:deep(.content-editor .cm-scroller) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

/* 响应式 */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
  
  .code-pane {
    width: 100%;
    min-height: 400px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
