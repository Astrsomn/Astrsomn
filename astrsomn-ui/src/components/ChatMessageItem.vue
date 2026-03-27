<template>
  <div class="message" :class="[role, { error }]">
    <div v-if="role === 'ai'" class="avatar-mini">A</div>
    <div class="content">
      <button
        v-if="role === 'ai' && content"
        type="button"
        class="copy-full-btn"
        @click="copyText(content, '已复制全文')"
      >
        复制
      </button>
      <div
        v-if="content"
        ref="messageTextRef"
        class="message-text"
        v-html="role === 'ai' ? renderedMarkdown : escapedPlainText"
        @click="handleMessageClick"
      ></div>
      <div v-else-if="streaming" class="typing-placeholder">正在思考中...</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message as antMessage } from 'ant-design-vue'
import hljs from 'highlight.js'
import MarkdownIt from 'markdown-it'

const props = defineProps<{
  role: 'user' | 'ai'
  content: string
  streaming?: boolean
  error?: boolean
}>()

const messageTextRef = ref<HTMLElement | null>(null)

const escapeHtml = (value: string) =>
  value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

const encodeCodePayload = (value: string) => {
  const bytes = new TextEncoder().encode(value)
  let binary = ''
  for (const byte of bytes) {
    binary += String.fromCharCode(byte)
  }
  return btoa(binary)
}

const decodeCodePayload = (value: string) => {
  const binary = atob(value)
  const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0))
  return new TextDecoder().decode(bytes)
}

const renderCodeBlock = (code: string, lang?: string) => {
  const highlighted = lang && hljs.getLanguage(lang)
    ? hljs.highlight(code, { language: lang, ignoreIllegals: true }).value
    : hljs.highlightAuto(code).value
  const encoded = encodeCodePayload(code)
  const languageLabel = escapeHtml(lang || 'code')
  return `
    <div class="md-code-wrapper">
      <div class="md-code-header">
        <span class="md-code-lang">${languageLabel}</span>
        <button type="button" class="md-copy-btn" data-copy-code="${encoded}">复制代码</button>
      </div>
      <pre class="md-code-block"><code>${highlighted}</code></pre>
    </div>
  `
}

const markdown = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
  highlight(code, lang) {
    return renderCodeBlock(code, lang)
  }
})

markdown.renderer.rules.code_block = (tokens, idx) => renderCodeBlock(tokens[idx].content)
markdown.renderer.rules.table_open = () => '<div class="md-table-wrap"><table>'
markdown.renderer.rules.table_close = () => '</table></div>'

const renderedMarkdown = computed(() => markdown.render(props.content))

const escapedPlainText = computed(() =>
  props.content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/\n/g, '<br />')
)

const copyText = async (value: string, successMessage: string) => {
  try {
    await navigator.clipboard.writeText(value)
    antMessage.success(successMessage)
  } catch {
    antMessage.error('复制失败')
  }
}

const handleMessageClick = async (event: MouseEvent) => {
  const target = event.target as HTMLElement | null
  const button = target?.closest('.md-copy-btn') as HTMLElement | null
  const encoded = button?.getAttribute('data-copy-code')
  if (!encoded) {
    return
  }
  event.preventDefault()
  await copyText(decodeCodePayload(encoded), '已复制代码')
}
</script>

<style scoped>
.message {
  display: flex;
  gap: 16px;
  max-width: 85%;
}

.message.ai {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar-mini {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.message .content {
  position: relative;
  padding: 12px 18px;
  border-radius: 18px;
  line-height: 1.6;
  font-size: 15px;
}

.message.ai .content {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  border-top-left-radius: 4px;
}

.message.user .content {
  background: var(--primary);
  color: white;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
}

.message.error .content {
  border-color: rgba(255, 77, 79, 0.35);
  color: #ff7875;
}

.message-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.message-text :deep(p) {
  margin: 0;
}

.message-text :deep(p + p) {
  margin-top: 12px;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 8px 0 8px 20px;
  padding: 0;
}

.message-text :deep(li + li) {
  margin-top: 4px;
}

.message-text :deep(blockquote) {
  margin: 12px 0;
  padding-left: 12px;
  border-left: 3px solid var(--border-default);
  color: var(--text-secondary);
}

.message-text :deep(a) {
  color: var(--primary);
  text-decoration: none;
}

.message-text :deep(a:hover) {
  text-decoration: underline;
}

.message-text :deep(code) {
  font-family: Consolas, Monaco, monospace;
  font-size: 0.92em;
}

.message-text :deep(:not(pre) > code) {
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(127, 127, 127, 0.12);
}

.message-text :deep(pre) {
  margin: 0;
  overflow-x: auto;
}

.message-text :deep(.md-code-wrapper) {
  margin: 12px 0;
  border-radius: 12px;
  overflow: hidden;
  background: #0f172a;
  color: #e2e8f0;
}

.message-text :deep(.md-code-header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 14px;
  background: rgba(148, 163, 184, 0.14);
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
}

.message-text :deep(.md-code-lang) {
  font-size: 12px;
  text-transform: lowercase;
  color: #cbd5e1;
}

.message-text :deep(.md-copy-btn) {
  border: none;
  background: transparent;
  color: #93c5fd;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
}

.message-text :deep(.md-copy-btn:hover) {
  color: #bfdbfe;
}

.message-text :deep(.md-code-block) {
  padding: 14px 16px;
  border-radius: 0;
  background: #0f172a;
  color: #e2e8f0;
}

.message-text :deep(.md-code-block code) {
  background: transparent;
  padding: 0;
}

.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4),
.message-text :deep(h5),
.message-text :deep(h6) {
  margin: 16px 0 8px;
  line-height: 1.35;
}

.message-text :deep(hr) {
  margin: 16px 0;
  border: none;
  border-top: 1px solid var(--border-default);
}

.message-text :deep(.md-table-wrap) {
  margin: 12px 0;
  overflow-x: auto;
  border: 1px solid var(--border-default);
  border-radius: 12px;
}

.message-text :deep(table) {
  width: 100%;
  min-width: 420px;
  border-collapse: collapse;
  background: transparent;
}

.message-text :deep(th),
.message-text :deep(td) {
  padding: 10px 12px;
  border-bottom: 1px solid var(--border-default);
  text-align: left;
  vertical-align: top;
}

.message-text :deep(th) {
  font-weight: 600;
  background: rgba(127, 127, 127, 0.08);
}

.message-text :deep(tr:last-child td) {
  border-bottom: none;
}

.copy-full-btn {
  position: absolute;
  top: 10px;
  right: 12px;
  border: none;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  padding: 0;
}

.copy-full-btn:hover {
  color: var(--primary);
}

.typing-placeholder {
  color: var(--text-secondary);
}
</style>
