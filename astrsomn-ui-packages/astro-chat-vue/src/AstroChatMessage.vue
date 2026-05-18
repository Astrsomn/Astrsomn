<template>
  <div :class="[role, { 'is-streaming': streaming, compact: compact }]" class="message-row">
    <div class="avatar-box">
      <div v-if="role === 'ai'" aria-hidden="true" class="avatar ai-avatar">
        <svg height="22" viewBox="0 0 24 24" width="22">
          <path d="M12 2L4.5 20.29L5.21 21L12 18L18.79 21L19.5 20.29L12 2Z" fill="currentColor"/>
        </svg>
      </div>
      <div v-else aria-hidden="true" class="avatar user-avatar user-avatar-icon">
        <svg height="20" viewBox="0 0 24 24" width="20">
          <path
              d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
              fill="currentColor"
          />
        </svg>
      </div>
    </div>

    <div class="message-body">
      <!-- AI：链式步骤 + 翠绿回答区（有 segments 时） -->
      <div v-if="role === 'ai' && hasStructuredSegments" class="ai-structured">
        <div v-for="(group, gIdx) in displayGroups" :key="gIdx" class="structured-segment-group">
          <div v-if="group.kind === 'chain'" class="chain-timeline">
            <div aria-hidden="true" class="chain-line-rail"/>
            <div class="chain-steps">
              <div v-for="(seg, sIdx) in group.segments" :key="sIdx" class="chain-step">
                <div
                    :class="seg.type === 'thought' ? 'is-thought' : 'is-tool'"
                    aria-hidden="true"
                    class="chain-node"
                >
                  <svg v-if="seg.type === 'thought'" height="16" viewBox="0 0 24 24" width="16">
                    <path
                        d="M12 3c-1.5 0-2.8.8-3.5 2-.1-.02-.2-.03-.33-.03C6.84 4.97 6 5.81 6 6.83c0 .47.18.9.47 1.22A3.98 3.98 0 0 0 5 11v1c0 2.21 1.79 4 4 4 .46 0 .9-.08 1.31-.22.41.14.85.22 1.31.22 2.21 0 4-1.79 4-4v-1c0-1.38-.7-2.6-1.76-3.32.48-.4.76-1 .76-1.68C15 5.35 14.65 5 14.22 5c-.13 0-.24.01-.33.03A4.02 4.02 0 0 0 12 3zm0 2c.97 0 1.86.38 2.53 1H12.8c-.44 0-.8.36-.8.8s.36.8.8.8h2.33c.1.32.17.66.17 1v1c0 1.1-.9 2-2 2s-2-.9-2-2v-1c0-1.66 1.34-3 3-3z"
                        fill="currentColor"
                    />
                  </svg>
                  <svg v-else height="16" viewBox="0 0 24 24" width="16">
                    <path
                        d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 14H4V6h16v12zM6 10h2v2H6v-2zm0 4h8v2H6v-2zm10-4h2v2h-2v-2zm-4 4h2v2h-2v-2z"
                        fill="currentColor"
                    />
                  </svg>
                </div>
                <div class="chain-card">
                  <button
                      class="chain-card-head"
                      type="button"
                      @click="toggleChainStep(gIdx, sIdx)"
                  >
                    <span class="chain-card-title">
                      <span v-if="streaming && isActiveStreamingThought(gIdx, sIdx, seg)" class="thought-pulse"/>
                      {{
                        seg.type === 'thought'
                            ? streaming && isActiveStreamingThought(gIdx, sIdx, seg)
                                ? '正在深度思考…'
                                : seg.title || '推理分析'
                            : seg.title || `调用工具: ${seg.toolName || 'tool'}`
                      }}
                    </span>
                    <svg
                        :class="{ 'is-open': isChainStepOpen(gIdx, sIdx) }"
                        aria-hidden="true"
                        class="chain-chevron"
                        height="14"
                        viewBox="0 0 24 24"
                        width="14"
                    >
                      <path d="M7 10l5 5 5-5H7z" fill="currentColor"/>
                    </svg>
                  </button>
                  <div v-show="isChainStepOpen(gIdx, sIdx)" class="chain-card-body">
                    <p v-if="seg.type === 'thought'" class="chain-thought-text">{{ seg.content }}</p>
                    <template v-else>
                      <p v-if="seg.args?.trim()" class="tool-args-label">
                      <pre v-if="seg.args?.trim()" class="tool-args-block">{{ seg.args }}</pre>
                      <p v-if="toolResultText(seg)?.trim()" class="tool-result-label">
                      <pre v-if="toolResultText(seg)?.trim()" class="tool-result-block">{{ toolResultText(seg) }}</pre>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="answer-row-final">
            <div class="final-answer-card">
              <div v-for="(block, bIdx) in blocksForAnswerGroup(group)" :key="bIdx" class="answer-block-wrap">
                <div
                    v-if="block.kind === 'md'"
                    class="markdown-renderer markdown-in-emerald"
                    @click="handleCodeCopy"
                    v-html="renderMarkdownBlock(block.content)"
                />
                <div
                    v-else-if="block.kind === 'html'"
                    class="markdown-renderer html-embed-block markdown-in-emerald"
                    v-html="block.content"
                />
                <div v-else-if="block.kind === 'image'" class="image-embed-block">
                  <img :src="block.content" alt="" class="segment-image"/>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!allAnswerBlocks.length && streaming && !error" class="streaming-placeholder structured-placeholder">
          <div aria-hidden="true" class="typing-loader">
            <span/><span/><span/>
          </div>
          <span>正在为您准备回答…</span>
        </div>

        <div v-else-if="error && (content || answerText)" class="final-answer-card ai-error-plain">
          {{ answerText }}
        </div>

        <div
            v-if="!streaming && (answerText || allAnswerBlocks.length > 0) && !error"
            class="answer-actions structured-actions"
        >
          <button class="text-action" type="button" @click="copyFullContent">
            <svg aria-hidden="true" height="14" viewBox="0 0 24 24" width="14">
              <path
                  d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"
                  fill="currentColor"
              />
            </svg>
            复制全文
          </button>
        </div>
      </div>

      <!-- AI：无 segments 时沿用单卡片 -->
      <div v-else-if="role === 'ai'" class="ai-card">
        <section v-if="thoughtTextLegacy" :class="{ 'is-collapsed': !isThoughtExpanded }" class="thought-section">
          <button
              :class="{ 'is-interactive': shouldShowThoughtToggle }"
              class="thought-head"
              type="button"
              @click="toggleThought"
          >
            <span class="thought-head-left">
              <span v-if="streaming" aria-hidden="true" class="thought-pulse"/>
              <span class="thought-title">{{ streaming ? '正在深度思考…' : '思考过程' }}</span>
            </span>
            <span v-if="shouldShowThoughtToggle" class="thought-toggle">
              {{ isThoughtExpanded ? '收起' : '展开' }}
            </span>
          </button>
          <div ref="thoughtRef" class="thought-scroll">
            <div class="thought-inner">{{ thoughtTextLegacy }}</div>
          </div>
        </section>

        <section class="answer-section">
          <div v-if="!answerBlocks.length && streaming && !error" class="streaming-placeholder">
            <div aria-hidden="true" class="typing-loader">
              <span/><span/><span/>
            </div>
            <span>正在为您准备回答…</span>
          </div>

          <div v-else-if="error && (content || answerText)" class="ai-error-plain">
            {{ answerText }}
          </div>

          <template v-else-if="answerBlocks.length > 0">
            <div v-for="(block, bIdx) in answerBlocks" :key="bIdx" class="answer-block-wrap">
              <div
                  v-if="block.kind === 'md'"
                  class="markdown-renderer"
                  @click="handleCodeCopy"
                  v-html="renderMarkdownBlock(block.content)"
              />
              <div
                  v-else-if="block.kind === 'html'"
                  class="markdown-renderer html-embed-block"
                  v-html="block.content"
              />
              <div v-else-if="block.kind === 'image'" class="image-embed-block">
                <img :src="block.content" alt="" class="segment-image"/>
              </div>
            </div>
          </template>

          <div v-if="!streaming && (answerText || answerBlocks.length > 0) && !error" class="answer-actions">
            <button class="text-action" type="button" @click="copyFullContent">
              <svg aria-hidden="true" height="14" viewBox="0 0 24 24" width="14">
                <path
                    d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"
                    fill="currentColor"
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
          <div class="user-html" v-html="props.content"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, nextTick, ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.min.css'
import {normalizeAiMarkdown} from './markdownNormalize'


const langAlias: Record<string, string> = {
  js: 'javascript',
  ts: 'typescript',
  py: 'python',
  sh: 'bash',
  zsh: 'bash',
  vue: 'html',
  md: 'markdown'
}

const props = withDefaults(
    defineProps<{
      role: 'user' | 'ai'
      content: string
      segments?: any[]
      streaming?: boolean
      error?: boolean
      compact?: boolean
    }>(),
    {compact: false}
)

const THOUGHT_AUTO_COLLAPSE_CHARS = 180
const isThoughtExpanded = ref(true)
const hasManualThoughtToggle = ref(false)
const thoughtRef = ref<HTMLElement | null>(null)


function resolveFenceLang(raw: string): { label: string; hljsKey: string | null } {
  const s = (raw || '').trim()
  if (!s) return {label: 'text', hljsKey: null}

  if (hljs.getLanguage(s)) return {label: s, hljsKey: s}

  const token = s.split(/[/\s]/)[0]?.trim() || 'text'
  if (hljs.getLanguage(token)) return {label: token, hljsKey: token}

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
      if (hljs.getLanguage(cand)) return {label: cand, hljsKey: cand}
    }
  }

  for (let len = Math.min(s.length, 28); len >= 2; len--) {
    const pref = s.slice(0, len)
    if (hljs.getLanguage(pref)) return {label: pref, hljsKey: pref}
  }

  const short = token.length > 24 ? token.slice(0, 21) + '…' : token
  return {label: short, hljsKey: null}
}


function unwrapMalformedFenceContent(raw: string, outerLang: string): { code: string; lang: string } {
  let code = raw
  let lang = outerLang || ''
  const lines = code.split('\n')


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

  return {code, lang}
}


const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})


md.renderer.rules.fence = (tokens, idx) => {
  const token = tokens[idx]
  if (!token) return ''
  const rawLang = (token.info && token.info.trim()) || ''
  const content = token.content
  const unwrapped = unwrapMalformedFenceContent(content, rawLang)
  const mapped = langAlias[rawLang.toLowerCase()] || rawLang
  const {hljsKey} = resolveFenceLang(unwrapped.lang || mapped || '')
  const plain = unwrapped.code

  let codeHtml = md.utils.escapeHtml(plain)
  try {
    if (hljsKey) {
      codeHtml = hljs.highlight(plain, {language: hljsKey, ignoreIllegals: true}).value
    } else if (plain.trim()) {
      codeHtml = hljs.highlightAuto(plain).value
    }
  } catch {

  }

  const langClass = hljsKey ? ` language-${hljsKey}` : ''
  return `<div class="code-block-wrapper"><pre class="vscode-code-block hljs${langClass}"><code class="hljs">${codeHtml}</code></pre><button type="button" class="code-copy-btn">复制</button></div>`
}

type AnswerBlock = { kind: 'md' | 'html' | 'image'; content: string }

type DisplayGroup = { kind: 'chain' | 'answer'; segments: any[] }

const hasStructuredSegments = computed(
    () => props.role === 'ai' && Array.isArray(props.segments) && props.segments.length > 0
)

const displayGroups = computed((): DisplayGroup[] => {
  const segs = props.segments
  if (!segs?.length) return []
  const groups: DisplayGroup[] = []
  let current: DisplayGroup | null = null
  for (const s of segs) {
    const isChain = s.type === 'thought' || s.type === 'tool'
    const kind: 'chain' | 'answer' = isChain ? 'chain' : 'answer'
    if (!current || current.kind !== kind) {
      current = {kind, segments: [s]}
      groups.push(current)
    } else {
      current.segments.push(s)
    }
  }
  return groups
})


const chainStepOpen = ref<Record<string, boolean>>({})

function chainStepKey(gIdx: number, sIdx: number) {
  return `${gIdx}-${sIdx}`
}

function defaultChainStepOpen(seg: any): boolean {
  if (seg?.type === 'thought') {
    const len = String(seg.content ?? '').length
    return len <= THOUGHT_AUTO_COLLAPSE_CHARS
  }
  return true
}

function isChainStepOpen(gIdx: number, sIdx: number) {
  const k = chainStepKey(gIdx, sIdx)
  const v = chainStepOpen.value[k]
  if (v !== undefined) return v
  const group = displayGroups.value[gIdx]
  const seg = group?.segments?.[sIdx]
  return seg ? defaultChainStepOpen(seg) : true
}

function toggleChainStep(gIdx: number, sIdx: number) {
  const k = chainStepKey(gIdx, sIdx)
  const cur = isChainStepOpen(gIdx, sIdx)
  chainStepOpen.value = {...chainStepOpen.value, [k]: !cur}
}


function isActiveStreamingThought(gIdx: number, sIdx: number, seg: any) {
  if (!props.streaming || seg?.type !== 'thought') return false
  let lastG = -1
  let lastS = -1
  displayGroups.value.forEach((gr, gi) => {
    if (gr.kind !== 'chain') return
    lastG = gi
    lastS = gr.segments.length - 1
  })
  return lastG >= 0 && gIdx === lastG && sIdx === lastS
}

function toolResultText(seg: any): string {
  if (!seg || seg.type !== 'tool') return ''
  return String(seg.result ?? seg.content ?? '')
}

function segmentsToAnswerBlocks(segs: any[] | undefined): AnswerBlock[] {
  if (!segs?.length) return []
  const blocks: AnswerBlock[] = []
  for (const s of segs) {
    const t = s.type
    if (t === 'text' || t === undefined || t === null) {
      const text = s.content != null ? String(s.content) : ''
      const last = blocks[blocks.length - 1]
      if (last?.kind === 'md') {
        last.content += text
      } else {
        blocks.push({kind: 'md', content: text})
      }
    } else if (t === 'html') {
      blocks.push({kind: 'html', content: s.content != null ? String(s.content) : ''})
    } else if (t === 'image') {
      blocks.push({kind: 'image', content: s.content != null ? String(s.content) : ''})
    }
  }
  return blocks
}

function blocksForAnswerGroup(group: DisplayGroup): AnswerBlock[] {
  return segmentsToAnswerBlocks(group.segments)
}

const thoughtTextLegacy = computed(() => {
  if (hasStructuredSegments.value) return ''
  if (!props.segments?.length) return ''
  return props.segments
      .filter((s) => s.type === 'thought')
      .map((s) => (s.content != null ? String(s.content) : ''))
      .filter(Boolean)
      .join('\n\n')
})

const shouldShowThoughtToggle = computed(() => thoughtTextLegacy.value.length > THOUGHT_AUTO_COLLAPSE_CHARS)

const answerText = computed(() => {
  if (props.segments?.length) {
    const fromSegments = props.segments
        .filter((s) => s.type !== 'thought' && s.type !== 'tool')
        .map((s) => (s.content != null ? String(s.content) : ''))
        .join('')
    if (fromSegments) {
      return fromSegments
    }
  }
  return props.content || ''
})

const answerBlocks = computed((): AnswerBlock[] => {
  if (props.role !== 'ai' || hasStructuredSegments.value) return []
  const segs =
      props.segments?.filter((s) => s.type !== 'thought' && s.type !== 'tool') ?? []
  if (!segs.length) {
    const c = (props.content || '').trim()
    if (!c) return []
    return [{kind: 'md', content: props.content || ''}]
  }
  return segmentsToAnswerBlocks(segs)
})

const allAnswerBlocks = computed((): AnswerBlock[] => {
  if (!hasStructuredSegments.value) return answerBlocks.value
  return segmentsToAnswerBlocks(
      props.segments?.filter((s) => s.type !== 'thought' && s.type !== 'tool') ?? []
  )
})

function renderMarkdownBlock(src: string): string {
  return md.render(normalizeAiMarkdown(src || ''))
}

watch(
    () => thoughtTextLegacy.value,
    () => {
      if (!thoughtTextLegacy.value) {
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

.message-row.compact {
  margin: 12px 0;
}

.message-row.compact .message-body {
  max-width: calc(100% - 40px);
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

.user-avatar-icon {
  font-size: 0;
}

.message-body {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 48px);
}


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

.answer-block-wrap + .answer-block-wrap .markdown-renderer {
  padding-top: 0;
}

.image-embed-block {
  padding: 12px 18px;
}

.segment-image {
  max-width: 100%;
  height: auto;
  border-radius: 10px;
  display: block;
  border: 1px solid var(--chat-ai-card-border, rgba(0, 0, 0, 0.08));
}

.html-embed-block {
  padding: 12px 18px;
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


.ai-error-plain {
  padding: 16px 18px 12px;
  font-size: 15px;
  line-height: 1.75;
  color: var(--chat-markdown-text);
  white-space: pre-wrap;
  word-break: break-word;
}


.user-card {
  display: flex;
  justify-content: flex-end;
}

.user-bubble {
  max-width: min(100%, 640px);
  padding: 12px 16px;
  border-radius: 16px 4px 16px 16px;
  background: var(--chat-user-bubble-bg);
  color: var(--chat-user-bubble-text);
  border: 1px solid var(--chat-user-bubble-border);
  box-shadow: var(--chat-user-bubble-shadow);
}

.user-html {
  font-size: 15px;
  line-height: 1.65;
  word-break: break-word;
}

.user-html :deep(a),
.user-html :deep(.user-attachment-link) {
  color: var(--chat-user-link);
}


.ai-structured {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.chain-timeline {
  position: relative;
  padding-left: 2.75rem;
  margin-bottom: 4px;
}

.chain-line-rail {
  position: absolute;
  left: 15px;
  top: 10px;
  bottom: 6px;
  width: 2px;
  background: var(--chat-chain-line);
  border-radius: 1px;
}

.chain-steps {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.chain-step {
  position: relative;
  padding-bottom: 14px;
}

.chain-node {
  position: absolute;
  left: -2.75rem;
  top: 2px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid var(--chat-chain-node-ring);
  box-shadow: var(--chat-avatar-shadow);
  z-index: 1;
}

.chain-node.is-thought {
  background: var(--chat-chain-thought-icon-bg);
  color: var(--chat-chain-thought-icon);
}

.chain-node.is-tool {
  background: var(--chat-chain-tool-icon-bg);
  color: var(--chat-chain-tool-icon);
}

.chain-card {
  border: 1px solid var(--chat-chain-card-border);
  border-radius: 12px;
  background: var(--chat-chain-card-bg);
  box-shadow: var(--chat-chain-card-shadow);
  overflow: hidden;
  transition: box-shadow 0.2s ease;
}

.chain-card:hover {
  box-shadow: var(--chat-chain-card-shadow-hover);
}

.chain-card-head {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 8px 14px;
  margin: 0;
  border: none;
  background: var(--chat-chain-card-head-bg);
  font: inherit;
  cursor: pointer;
  text-align: left;
  color: var(--chat-chain-card-title);
}

.chain-card-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--chat-chain-card-title);
}

.chain-chevron {
  flex-shrink: 0;
  color: var(--chat-chain-chevron);
  transition: transform 0.2s ease;
}

.chain-chevron.is-open {
  transform: rotate(180deg);
}

.chain-card-body {
  padding: 10px 14px 12px;
  font-size: 13px;
  color: var(--chat-chain-card-body);
}

.chain-thought-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.65;
}

.tool-args-label,
.tool-result-label {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 10px;
  margin: 0 0 4px;
  opacity: 0.85;
}

.tool-args-label {
  color: var(--chat-tool-args-label);
}

.tool-result-label {
  color: var(--chat-tool-result-label);
}

.tool-args-block {
  margin: 0 0 10px;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--chat-tool-args-bg);
  color: var(--chat-tool-args-text);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 11px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-x: auto;
}

.tool-result-block {
  margin: 0;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--chat-tool-result-bg);
  border: 1px solid var(--chat-tool-result-border);
  color: var(--chat-tool-result-text);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 11px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-x: auto;
}


.answer-row-final {
  width: 100%;
  min-width: 0;
}

.final-answer-card {
  width: 100%;
  min-width: 0;
  border-radius: 16px;
  border: 1px solid var(--chat-answer-emerald-border);
  background: var(--chat-answer-emerald-bg);
  box-shadow: var(--chat-answer-emerald-shadow);
  overflow: hidden;
}

.markdown-in-emerald {
  color: var(--chat-answer-emerald-text);
}

.markdown-in-emerald :deep(h1),
.markdown-in-emerald :deep(h2),
.markdown-in-emerald :deep(h3),
.markdown-in-emerald :deep(h4) {
  color: var(--chat-answer-emerald-heading);
}

.markdown-in-emerald :deep(blockquote) {
  border-left-color: var(--chat-answer-emerald-blockquote-border);
  background: var(--chat-answer-emerald-blockquote-bg);
  color: var(--chat-answer-emerald-muted);
}

.markdown-in-emerald :deep(a) {
  color: var(--chat-answer-emerald-link);
}

.markdown-in-emerald :deep(code:not(pre code)) {
  background: var(--chat-answer-emerald-inline-code-bg);
  color: var(--chat-answer-emerald-inline-code-text);
}

.final-answer-card .image-embed-block {
  background: transparent;
}

.structured-placeholder {
  padding: 12px 0;
}

.structured-actions {
  padding-top: 0;
}

.structured-segment-group {
  display: contents;
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
