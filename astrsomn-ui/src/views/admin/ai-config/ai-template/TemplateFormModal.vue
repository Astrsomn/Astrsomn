<template>
  <AstModal
      :body-height="'80vh'"
      :confirm-loading="confirmLoading"
      :confirm-text="t.form.confirmText"
      :open="open"
      width="80vw"
      @confirm="handleOk"
      @update:open="(value) => open = value"
  >
    <template #header-logo>
      <div class="logo-box">
        <CodeOutlined/>
      </div>
    </template>
    <template #header-title>
      <span class="main-title">{{ mode === 'create' ? t.form.createTitle : t.form.editTitle }}</span>
    </template>
    <template #header-subtitle>
      <span class="sub-title">{{ t.form.subtitle }}</span>
    </template>
    <div class="main-content">
      <section class="info-pane">
        <div class="pane-card ">
          <a-form
              ref="formRef"
              :model="form"
              :rules="rules"
              class="template-form"
              layout="vertical"
          >
            <div class="form-grid">
              <a-form-item :label="t.form.templateKeyLabel" name="templateKey">
                <a-input
                    v-model:value="form.templateKey"
                    :disabled="mode === 'edit'"
                    :placeholder="t.form.templateKeyPlaceholder"
                    size="large"
                />
              </a-form-item>

              <a-form-item :label="t.form.templateTitleLabel" name="templateTitle">
                <a-input v-model:value="form.templateTitle" :placeholder="t.form.templateTitlePlaceholder" size="large"/>
              </a-form-item>

              <a-form-item :label="t.form.categoryLabel" name="category">
                <a-input v-model:value="form.category" allow-clear :placeholder="t.form.categoryPlaceholder" size="large"/>
              </a-form-item>

              <a-form-item :label="t.form.templateTypeLabel" name="templateType">
                <a-select
                    v-model:value="form.templateType"
                    :options="templateTypeOptions"
                    :placeholder="t.form.templateTypePlaceholder"
                    size="large"
                />
              </a-form-item>

              <a-form-item :label="t.form.paramsDefinitionLabel" name="paramsDefinition">
                <a-textarea
                    v-model:value="form.paramsDefinition"
                    :placeholder="t.form.paramsDefinitionPlaceholder"
                    :rows="3"
                    size="large"
                />
              </a-form-item>

  

              <a-form-item :label="t.form.statusLabel" name="status">
                <AstStatusToggle
                    v-model="form.status"
                    :enabled-label="t.form.status.enabled"
                    :disabled-label="t.form.status.disabled"
                />
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
                {{ form.templateType === 'FREEMARKER' ? t.form.editorTitleFtl : t.form.editorTitleSt }}
              </span>
              <span class="editor-hint">
                {{
                  form.templateType === 'FREEMARKER' ? t.form.editorHintFtl : t.form.editorHintSt
                }}
              </span>
            </div>
            <Codemirror
                v-model="form.content"
                :autofocus="mode === 'create'"
                :extensions="editorExtensions"
                :indent-with-tab="true"
                :tab-size="2"
                class="content-editor"
            />
          </div>
        </a-form-item>
      </aside>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue'
import type {FormInstance} from 'ant-design-vue'
import {Codemirror} from 'vue-codemirror'
import {html} from '@codemirror/lang-html'
import {oneDark} from '@codemirror/theme-one-dark'
import {CodeOutlined} from '@ant-design/icons-vue'
import AstModal from '@/components/home/AstModal.vue'
import AstStatusToggle from '@/components/home/AstStatusToggle.vue'
import type {AiTemplate} from '@/api/aiTemplate.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-template')

const props = defineProps<{
  mode: 'create' | 'edit'
  confirmLoading: boolean
  initial: AiTemplate | null
}>()

const emit = defineEmits<{
  submit: [payload: AiTemplate]
}>()

const open = defineModel<boolean>('open', {required: true})

const formRef = ref<FormInstance | null>(null)

const templateTypeOptions = computed(() => [
  {label: t.value.form.templateType.freemarker, value: 'FREEMARKER'},
  {label: t.value.form.templateType.stringTemplate, value: 'STRING_TEMPLATE'}
])

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

const rules = computed(() => ({
  templateKey: [],
  templateTitle: [{required: true, message: t.value.form.validation.templateTitleRequired}],
  content: [{required: true, message: t.value.form.validation.contentRequired}],
  templateType: [{required: true, message: t.value.form.validation.templateTypeRequired}],
  status: [{required: true, message: t.value.form.validation.statusRequired}]
}))

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
  emit('submit', {...form})
}

</script>

<style scoped>

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
  box-shadow: 0 1px 0 color-mix(in srgb, var(--primary) 20%, transparent) inset,
  0 2px 6px color-mix(in srgb, var(--primary) 35%, transparent);
}

.main-title {
  display: block;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
}

.sub-title {
  font-size: 12px;
  color: var(--text-tertiary);
}


.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
  min-height: 0;
}


.pane-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}


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


.code-pane {
  width: 50%;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding: 0;
}


.editor-shell {
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: clip;
  background: #0f172a;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}


.code-pane :deep(.ant-form-item) {
  margin-bottom: 0;
  margin-top: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.code-pane :deep(.ant-form-item-row) {
  flex: 1;
  min-height: 0;
}

.code-pane :deep(.ant-form-item-control) {
  flex: 1;
  min-height: 0;
}

.code-pane :deep(.ant-form-item-control-input) {
  flex: 1;
  min-height: 0;
}

.code-pane :deep(.ant-form-item-control-input-content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: #111827;
  border-bottom: 1px solid color-mix(in srgb, var(--text-quaternary) 8%, transparent);
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
  display: flex;
  flex-direction: column;
  min-height: 0;
}

:deep(.content-editor .cm-editor) {
  flex: 1;
  min-height: 0;
}

:deep(.content-editor .cm-scroller) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  overflow: auto !important;
}


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
