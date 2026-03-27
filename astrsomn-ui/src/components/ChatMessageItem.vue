<template>
  <div class="message-row" :class="[role, { 'is-streaming': streaming }]">
    <div class="avatar-box">
      <div v-if="role === 'ai'" class="avatar ai-avatar" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="22" height="22">
          <path fill="currentColor" d="M12 2L4.5 20.29L5.21 21L12 18L18.79 21L19.5 20.29L12 2Z" />
        </svg>
      </div>
      <div v-else class="avatar user-avatar">
        <span>ME</span>
      </div>
    </div>

    <div class="message-body">
      <!-- AI：思考 + 正文同一卡片 -->
      <div v-if="role === 'ai'" class="ai-card">
        <section v-if="thoughtText" class="thought-section" :class="{ 'is-collapsed': !isThoughtExpanded }">
          <button
            type="button"
            class="thought-head"
            :class="{ 'is-interactive': shouldShowThoughtToggle }"
            @click="toggleThought"
          >
            <span class="thought-head-left">
              <span v-if="streaming" class="thought-pulse" aria-hidden="true" />
              <span class="thought-title">{{ streaming ? '正在深度思考…' : '思考过程' }}</span>
            </span>
            <span v-if="shouldShowThoughtToggle" class="thought-toggle">
              {{ isThoughtExpanded ? '收起' : '展开' }}
            </span>
          </button>
          <div ref="thoughtRef" class="thought-scroll">
            <div class="thought-inner">{{ thoughtText }}</div>
          </div>
        </section>

        <section class="answer-section">
          <div v-if="!answerText && streaming" class="streaming-placeholder">
            <div class="typing-loader" aria-hidden="true">
              <span /><span /><span />
            </div>
            <span>正在为您准备回答…</span>
          </div>

          <div
            v-else-if="content || answerText"
            class="markdown-renderer"
            @click="handleCodeCopy"
            v-html="renderedHtml"
          />

          <div v-if="!streaming && answerText" class="answer-actions">
            <button type="button" class="text-action" @click="copyFullContent">
              <svg viewBox="0 0 24 24" width="14" height="14" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"
                />
              </svg>
              复制全文
            </button>
          </div>
        </section>
      </div>

      <!-- 用户消息 -->
      <div v-else class="user-card">
        <div class="user-bubble">
          <div v-html="props.content" class="user-html" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.min.css'
import { normalizeAiMarkdown } from '@/utils/aiMarkdownNormalize'

/** 与 demo/ChatArea.vue 一致：短语言名 → hljs 注册名 */
const langAlias: Record<string, string> = {
  js: 'javascript',
  ts: 'typescript',
  py: 'python',
  sh: 'bash',
  zsh: 'bash',
  vue: 'html',
  md: 'markdown'
}

const props = defineProps<{
  role: 'user' | 'ai'
  content: string
  segments?: any[]
  streaming?: boolean
  error?: boolean
}>()

const THOUGHT_AUTO_COLLAPSE_CHARS = 180
const isThoughtExpanded = ref(true)
const hasManualThoughtToggle = ref(false)
const thoughtRef = ref<HTMLElement | null>(null)

/** 围栏语言：修复流式/换行异常导致的「javaimport」等粘连，并匹配 hljs 语言 key */
function resolveFenceLang(raw: string): { label: string; hljsKey: string | null } {
  const s = (raw || '').trim()
  if (!s) return { label: 'text', hljsKey: null }

  if (hljs.getLanguage(s)) return { label: s, hljsKey: s }

  const token = s.split(/[/\s]/)[0]?.trim() || 'text'
  if (hljs.getLanguage(token)) return { label: token, hljsKey: token }

  const gluedSuffixes = [
    'import',
    'package',
    'class',
    'public',
    'private',
    'protected',
    'interface',
    'enum',
    'abstract',
    'extends',
    'implements',
    'static',
    'void',
    'return',
    'def',
    'async',
    'fn',
    'const',
    'let',
    'var',
    'type',
    'namespace',
    'module',
    'from',
    'include',
    'using',
    'struct',
    'trait',
    'impl',
    'function',
    'new'
  ]
  for (const suf of gluedSuffixes) {
    if (s.length > suf.length && s.endsWith(suf)) {
      const cand = s.slice(0, -suf.length)
      if (hljs.getLanguage(cand)) return { label: cand, hljsKey: cand }
    }
  }

  for (let len = Math.min(s.length, 28); len >= 2; len--) {
    const pref = s.slice(0, len)
    if (hljs.getLanguage(pref)) return { label: pref, hljsKey: pref }
  }

  const short = token.length > 24 ? token.slice(0, 21) + '…' : token
  return { label: short, hljsKey: null }
}

/** 模型偶发把标题/重复 ``` 行吃进围栏，复制与高亮前尽量剥掉 */
function unwrapMalformedFenceContent(raw: string, outerLang: string): { code: string; lang: string } {
  let code = raw
  let lang = outerLang || ''
  const lines = code.split('\n')

  // 首行是 ### 标题、下一行是 ```lang
  if (
    lines.length >= 3 &&
    /^#{1,6}\s/.test(lines[0].trim()) &&
    /^\s*```[\w+-]*\s*$/.test(lines[1])
  ) {
    const inner = lines[1].replace(/^\s*```/, '').trim()
    if (inner && hljs.getLanguage(inner)) lang = inner
    code = lines.slice(2).join('\n')
  } else if (
    lines.length >= 2 &&
    /^#{1,6}\s/.test(lines[0].trim()) &&
    /^(import|package|#include|from\s|def\s|async\s|module\s|export\s|public\s|private\s|class\s|interface\s|enum\s|@\w|\/\/|\/\*)/.test(
      lines[1].trim()
    )
  ) {
    code = lines.slice(1).join('\n')
  }

  return { code, lang }
}

// html: false — 避免 <dependency> 等被当作 HTML 块吞掉（常见于 Maven/XML 与围栏错位时）
const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

/** 与 demo/ChatArea.vue：自定义 fence，结构为 code-block-wrapper + vscode-code-block + 复制按钮 */
md.renderer.rules.fence = (tokens, idx) => {
  const token = tokens[idx]
  if (!token) return ''
  const rawLang = (token.info && token.info.trim()) || ''
  const content = token.content
  const unwrapped = unwrapMalformedFenceContent(content, rawLang)
  const mapped = langAlias[rawLang.toLowerCase()] || rawLang
  const { hljsKey } = resolveFenceLang(unwrapped.lang || mapped || '')
  const plain = unwrapped.code

  let codeHtml = md.utils.escapeHtml(plain)
  try {
    if (hljsKey) {
      codeHtml = hljs.highlight(plain, { language: hljsKey, ignoreIllegals: true }).value
    } else if (plain.trim()) {
      codeHtml = hljs.highlightAuto(plain).value
    }
  } catch {
    /* 保持转义后的纯文本 */
  }

  const langClass = hljsKey ? ` language-${hljsKey}` : ''
  return `<div class="code-block-wrapper"><pre class="vscode-code-block hljs${langClass}"><code class="hljs">${codeHtml}</code></pre><button type="button" class="code-copy-btn">复制</button></div>`
}

const thoughtText = computed(() => props.segments?.find((s) => s.type === 'thought')?.content || '')
const shouldShowThoughtToggle = computed(() => thoughtText.value.length > THOUGHT_AUTO_COLLAPSE_CHARS)
const answerText = computed(() => {
  if (props.segments) {
    return props.segments.filter((s) => s.type !== 'thought').map((s) => s.content).join('')
  }
  return props.content
})

const renderedHtml = computed(() => {
  if (props.role === 'user') return props.content
  const src = normalizeAiMarkdown(answerText.value || '')
  return md.render(src)
})

watch(
  () => thoughtText.value,
  () => {
    if (!thoughtText.value) {
      isThoughtExpanded.value = true
      hasManualThoughtToggle.value = false
      return
    }
    if (shouldShowThoughtToggle.value && !hasManualThoughtToggle.value) {
      isThoughtExpanded.value = false
    }
    if (props.streaming && isThoughtExpanded.value) {
      nextTick(() => {
        const el = thoughtRef.value
        if (el) el.scrollTop = el.scrollHeight
      })
    }
  }
)

const toggleThought = () => {
  if (!shouldShowThoughtToggle.value) return
  hasManualThoughtToggle.value = true
  isThoughtExpanded.value = !isThoughtExpanded.value
}

const copyFullContent = () => {
  navigator.clipboard.writeText(answerText.value)
  message.success('已复制到剪贴板')
}

const handleCodeCopy = (e: MouseEvent) => {
  const btn = (e.target as HTMLElement).closest('.code-copy-btn')
  if (!btn) return
  const wrapper = btn.closest('.code-block-wrapper')
  const text = wrapper?.querySelector('pre code')?.textContent ?? ''
  if (!text) {
    message.warning('无法复制，请重试')
    return
  }
  navigator.clipboard.writeText(text)
  message.success('代码已复制')
}
</script>

<style scoped>
.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin: 20px 0;
  max-width: 100%;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar-box {
  flex-shrink: 0;
  padding-top: 2px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--chat-avatar-shadow);
}

.ai-avatar {
  background: var(--chat-ai-avatar-bg);
  border: 1px solid var(--chat-ai-avatar-border);
  color: var(--chat-ai-avatar-icon);
}

.user-avatar {
  background: var(--chat-user-avatar-bg);
  border: 1px solid var(--chat-user-avatar-border);
  color: var(--chat-user-avatar-text);
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.02em;
}

.message-body {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 48px);
}

/* —— AI 统一卡片 —— */
.ai-card {
  border-radius: 16px;
  border: 1px solid var(--chat-ai-card-border);
  background: var(--chat-ai-card-bg);
  box-shadow: var(--chat-ai-card-shadow);
  overflow: hidden;
}

.thought-section {
  --thought-bg: var(--chat-thought-bg);
  background: var(--thought-bg);
  border-bottom: 1px solid var(--chat-thought-border);
}

.thought-head {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 14px;
  margin: 0;
  border: none;
  background: transparent;
  font: inherit;
  color: var(--chat-thought-body);
  text-align: left;
}

.thought-head.is-interactive {
  cursor: pointer;
}

.thought-head.is-interactive:hover {
  background: var(--chat-thought-head-hover);
}

.thought-head-left {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.thought-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--chat-thought-title);
}

.thought-pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent-blue);
  flex-shrink: 0;
  animation: pulse 1.4s ease-in-out infinite;
}

.thought-toggle {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 500;
  color: var(--chat-link);
}

.thought-scroll {
  padding: 0 14px 12px;
  max-height: none;
  overflow: visible;
}

.thought-inner {
  font-size: 13px;
  line-height: 1.65;
  color: var(--chat-thought-body);
  white-space: pre-wrap;
  word-break: break-word;
}

/* 收起：限制高度、底部渐变，不出现顶部裁剪与内部滚动条 */
.thought-section.is-collapsed .thought-scroll {
  max-height: 5.25rem;
  overflow: hidden;
  position: relative;
  padding-bottom: 14px;
}

.thought-section.is-collapsed .thought-scroll::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2rem;
  background: linear-gradient(to bottom, transparent, var(--thought-bg));
  pointer-events: none;
}

/* 展开且内容长时由整块区域滚动（可选上限，避免占满屏） */
.thought-section:not(.is-collapsed) .thought-scroll {
  max-height: min(40vh, 200px);
  overflow-y: auto;
  overscroll-behavior: contain;
}

.answer-section {
  background: var(--chat-answer-bg);
}

.streaming-placeholder {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 18px 20px;
  color: var(--chat-stream-placeholder);
  font-size: 14px;
}

.typing-loader {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.typing-loader span {
  width: 5px;
  height: 5px;
  background: var(--accent-blue);
  border-radius: 50%;
  animation: typing 0.9s ease-in-out infinite;
}

.typing-loader span:nth-child(2) {
  animation-delay: 0.15s;
}
.typing-loader span:nth-child(3) {
  animation-delay: 0.3s;
}

.markdown-renderer {
  padding: 16px 18px 12px;
  font-size: 15px;
  line-height: 1.75;
  color: var(--chat-markdown-text);
}

.markdown-renderer :deep(p) {
  margin: 0.65em 0;
}

.markdown-renderer :deep(p:first-child) {
  margin-top: 0;
}

.markdown-renderer :deep(p:last-child) {
  margin-bottom: 0;
}

.markdown-renderer :deep(h1),
.markdown-renderer :deep(h2),
.markdown-renderer :deep(h3),
.markdown-renderer :deep(h4) {
  margin: 1.15em 0 0.5em;
  font-weight: 700;
  line-height: 1.35;
  color: var(--chat-markdown-heading);
}

.markdown-renderer :deep(h1) {
  font-size: 1.35em;
}
.markdown-renderer :deep(h2) {
  font-size: 1.2em;
}
.markdown-renderer :deep(h3) {
  font-size: 1.08em;
}
.markdown-renderer :deep(h4) {
  font-size: 1em;
}

.markdown-renderer :deep(ul),
.markdown-renderer :deep(ol) {
  margin: 0.5em 0;
  padding-left: 1.35em;
}

.markdown-renderer :deep(li) {
  margin: 0.25em 0;
}

.markdown-renderer :deep(blockquote) {
  margin: 0.75em 0;
  padding: 0.4em 0 0.4em 0.9em;
  border-left: 3px solid var(--chat-blockquote-border);
  color: var(--chat-markdown-muted);
  background: var(--chat-blockquote-bg);
  border-radius: 0 6px 6px 0;
}

.markdown-renderer :deep(a) {
  color: var(--chat-link);
  text-decoration: underline;
  text-underline-offset: 2px;
}

.markdown-renderer :deep(hr) {
  margin: 1.25em 0;
  border: none;
  border-top: 1px solid var(--chat-hr);
}

.markdown-renderer :deep(code:not(pre code)) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 0.88em;
  background: var(--chat-inline-code-bg);
  color: var(--chat-inline-code-text);
  padding: 0.15em 0.4em;
  border-radius: 6px;
}

/* 代码块：对齐 demo/ChatArea.vue（Atom One Dark + VS Code 式布局） */
.markdown-renderer :deep(.code-block-wrapper) {
  position: relative;
  margin: 12px 0;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  background: #282c34;
  border: 1px solid #1e2127;
}

.markdown-renderer :deep(.code-block-wrapper .code-copy-btn) {
  position: absolute;
  top: 6px;
  right: 10px;
  z-index: 1;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.08);
  color: #abb2bf;
  border: 1px solid rgba(255, 255, 255, 0.12);
  padding: 4px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s ease, border-color 0.15s ease, color 0.15s ease;
}

.markdown-renderer :deep(.code-block-wrapper .code-copy-btn:hover) {
  background: rgba(255, 255, 255, 0.14);
  color: #e6e6e6;
  border-color: rgba(255, 255, 255, 0.22);
}

.markdown-renderer :deep(.code-block-wrapper pre.vscode-code-block) {
  margin: 0;
  padding: 32px 14px 14px;
  background: transparent;
  border-radius: 0;
  position: relative;
  width: 100%;
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(171, 178, 191, 0.35) transparent;
}

.markdown-renderer :deep(.code-block-wrapper pre.vscode-code-block::-webkit-scrollbar) {
  height: 6px;
}

.markdown-renderer :deep(.code-block-wrapper pre.vscode-code-block::-webkit-scrollbar-thumb) {
  background: rgba(171, 178, 191, 0.35);
  border-radius: 999px;
}

.markdown-renderer :deep(.code-block-wrapper pre.vscode-code-block code.hljs) {
  display: block;
  margin: 0;
  padding: 0 !important;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 13px;
  line-height: 1.6;
  tab-size: 4;
  white-space: pre;
  background: transparent !important;
}

.answer-actions {
  display: flex;
  justify-content: flex-end;
  padding: 0 12px 12px;
}

.text-action {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--chat-action-text);
  font-size: 12px;
  cursor: pointer;
  transition: color 0.15s ease, background 0.15s ease;
}

.text-action:hover {
  color: var(--chat-link);
  background: var(--chat-action-hover-bg);
}

/* 用户气泡 */
.user-card {
  display: flex;
  justify-content: flex-end;
}

.user-bubble {
  max-width: min(100%, 640px);
  padding: 12px 16px;
  border-radius: 16px 4px 16px 16px;
  background: var(--primary-gradient);
  color: #fff;
  box-shadow: 0 4px 14px rgba(0, 123, 255, 0.28);
}

.user-html {
  font-size: 15px;
  line-height: 1.65;
  word-break: break-word;
}

.user-html :deep(a) {
  color: #dbeafe;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(0.92);
    opacity: 0.85;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

@keyframes typing {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}
</style>
