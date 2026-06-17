<template>
  <div class="template-debugger">
    <!-- Toolbar -->
    <div class="debug-toolbar">
      <a-select
          v-model:value="selectedTemplateKey"
          :loading="templateLoading"
          :options="templateOptions"
          :placeholder="t.debugger.templateSelect"
          allow-clear
          class="toolbar-select debug-template-select"
          show-search
          @change="loadTemplateContent"
      />
      <a-tooltip title="刷新模板列表">
        <a-button
            :loading="templateLoading"
            class="refresh-btn"
            size="small"
            type="text"
            @click="fetchTemplateList"
        >
          <template #icon><ReloadOutlined/></template>
        </a-button>
      </a-tooltip>
      <a-input
          v-model:value="paramsJson"
          :placeholder="t.debugger.paramsPlaceholder"
          class="toolbar-input debug-params-input"
          @pressEnter="handleRender"
      />
      <a-button :loading="rendering" type="primary" @click="handleRender">
        <template #icon><PlayCircleOutlined/></template>
        {{ rendering ? t.debugger.rendering : t.debugger.render }}
      </a-button>
    </div>

    <!-- Split: Editor (top) + Preview (bottom) -->
    <div class="debug-split">
      <!-- Editor Pane -->
      <div class="editor-pane">
        <div class="pane-header">
          <CodeOutlined/>
          <span>FTL</span>
        </div>
        <Codemirror
            v-model="ftlContent"
            :extensions="editorExtensions"
            :indent-with-tab="true"
            :tab-size="2"
            class="debug-editor"
        />
      </div>

      <!-- Preview Pane -->
      <div class="preview-pane">
        <div class="pane-header preview-header-bar">
          <span><EyeOutlined/> {{ t.debugger.preview }}</span>
          <a-button
              size="small"
              type="text"
              @click="previewModalVisible = true"
          >
            <template #icon><FullscreenOutlined/></template>
            {{ t.debugger.fullscreen }}
          </a-button>
        </div>
        <iframe
            v-if="renderedContent"
            :srcdoc="renderedContent"
            class="preview-frame"
            sandbox="allow-scripts"
            title="Preview"
        />
        <div v-else-if="!rendering" class="preview-empty">
          <FileSearchOutlined/>
          <span>{{ t.debugger.noTemplate }}</span>
        </div>
      </div>
    </div>

    <!-- Full Preview Modal -->
    <a-modal
        v-model:open="previewModalVisible"
        :title="t.debugger.preview"
        :footer="null"
        width="90vw"
        class="preview-modal"
    >
      <iframe
          v-if="renderedContent"
          :srcdoc="renderedContent"
          class="preview-modal-frame"
          sandbox="allow-scripts"
          title="Preview"
      />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {message} from 'ant-design-vue'
import {Codemirror} from 'vue-codemirror'
import {html} from '@codemirror/lang-html'
import {oneDark} from '@codemirror/theme-one-dark'
import {
  CodeOutlined,
  EyeOutlined,
  FileSearchOutlined,
  FullscreenOutlined,
  PlayCircleOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'
import {type AiTemplate, aiTemplateApi} from '@/api/aiTemplate.ts'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-template')

// ── Template list from server ──
const templateList = ref<AiTemplate[]>([])
const templateLoading = ref(false)

const templateOptions = computed(() =>
    templateList.value
        .filter(t => t.templateType === 'FREEMARKER' || !t.templateType)
        .map(t => ({
          label: `${t.templateTitle || t.templateKey} (${t.templateKey})`,
          value: t.templateKey || '',
        }))
        .filter(o => o.value)
)

const fetchTemplateList = async () => {
  templateLoading.value = true
  try {
    const resp = await aiTemplateApi.queryPage({
      pageNo: 1,
      pageSize: 999,
      param: {}
    })
    templateList.value = resp.list || []
  } finally {
    templateLoading.value = false
  }
}

onMounted(fetchTemplateList)

// ── Selected template ──
const selectedTemplateKey = ref<string | undefined>(undefined)
const ftlContent = ref('')
const originalContent = ref('')

const loadTemplateContent = async (key: string | undefined) => {
  if (!key) {
    ftlContent.value = ''
    originalContent.value = ''
    return
  }
  const template = templateList.value.find(t => t.templateKey === key)
  if (template) {
    ftlContent.value = template.content || ''
    originalContent.value = template.content || ''
    // Pre-fill params from template's paramsDefinition as a starting hint
    if (template.paramsDefinition && !paramsJson.value) {
      paramsJson.value = template.paramsDefinition
    }
  }
}

// ── Params ──
const paramsJson = ref('')

// ── Rendering ──
const rendering = ref(false)
const renderedContent = ref('')

const handleRender = async () => {
  if (!ftlContent.value.trim()) {
    message.warning(t.value.debugger.noTemplate)
    return
  }
  rendering.value = true
  try {
    const result = await aiTemplateApi.debugRender(ftlContent.value, paramsJson.value)
    renderedContent.value = result
    message.success(t.value.debugger.renderSuccess)
  } catch {
    renderedContent.value = ''
    message.error(t.value.debugger.renderFailed)
  } finally {
    rendering.value = false
  }
}

// ── Preview Modal State ──
const previewModalVisible = ref(false)

// ── Editor extensions ──
const editorExtensions = computed(() => [html(), oneDark])
</script>

<style scoped>
.template-debugger {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 260px);
  min-height: 500px;
  gap: 12px;
  width: calc(100vw - 400px);
  max-width: calc(100vw - 400px);
  overflow-x: hidden;
}

/* ── Toolbar ── */
.debug-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  padding: 12px 16px;
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  border-radius: 10px;
}

.debug-template-select {
  min-width: 260px;
}

.debug-params-input {
  flex: 1;
  min-width: 200px;
}

/* ── Split pane ── */
.debug-split {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
  overflow: hidden;
  max-width: 100%;
}

/* ── Editor pane ── */
.editor-pane {
  flex: 1;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  overflow: hidden;
  background: #0f172a;
}

.pane-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #111827;
  border-bottom: 1px solid color-mix(in srgb, var(--text-quaternary) 8%, transparent);
  color: #f9fafb;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.debug-editor {
  flex: 1;
  font-size: 13px;
}

.debug-editor :deep(.cm-editor) {
  height: 100%;
}

.debug-editor :deep(.cm-scroller) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

/* ── Preview pane ── */
.preview-pane {
  flex: 1;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-card);
  position: relative;
}

.preview-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-default);
  color: var(--text-primary);
  flex-shrink: 0;
}

.preview-frame {
  flex: 1;
  width: 100%;
  max-width: 100%;
  border: none;
  background: #fff;
}

.preview-empty {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: var(--text-muted);
  font-size: 14px;
  pointer-events: none;
}

.preview-empty :deep(svg) {
  font-size: 36px;
  opacity: 0.3;
}

/* ── Preview Modal ── */
.preview-modal :deep(.ant-modal-body) {
  padding: 0;
  height: 70vh;
}

.preview-modal-frame {
  width: 100%;
  height: 100%;
  border: none;
  background: #fff;
}

/* ── Responsive ── */
@media (max-width: 860px) {
  .debug-template-select {
    min-width: 180px;
  }

  .debug-params-input {
    min-width: 120px;
  }

  .debug-split {
    flex-direction: column;
  }
}
</style>
