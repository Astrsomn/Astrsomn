<template>
  <section class="glass-card bento-span-4-1 log-panel">
    <div class="log-glow" aria-hidden="true" />
    <div class="log-head">
      <h3 class="log-title">Real-time Logs</h3>
      <div class="log-dots" aria-hidden="true">
        <span />
        <span />
        <span />
      </div>
    </div>
    <div class="log-body">
      <p v-for="(line, i) in visibleLines" :key="`${tick}-${i}`" class="log-line">
        <span class="log-ts" :class="`log-ts--${line.tone}`">{{ line.time }}</span>
        {{ line.message }}
      </p>
      <p class="log-cursor">_</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

type LogLine = { time: string; tone: 'blue' | 'green' | 'muted' | 'amber'; message: string }

const logTemplates: LogLine[] = [
  { time: '[09:41:22]', tone: 'blue', message: ' Connecting to Anthropic Claude-3.5...' },
  { time: '[09:41:25]', tone: 'green', message: ' Context Sync: OK (1,244 tokens)' },
  { time: '[09:41:30]', tone: 'muted', message: ' Running workflow: "Doc_Parser_01"...' },
  { time: '[09:41:35]', tone: 'amber', message: ' Task completed: 85% success rate' },
  { time: '[09:41:40]', tone: 'blue', message: ' New agent "CodeGen_v3" activated' },
]

const visibleLines = ref<LogLine[]>(logTemplates.slice(0, 4))
const tick = ref(0)

let step = 0
let timer: ReturnType<typeof setInterval> | undefined

onMounted(() => {
  timer = setInterval(() => {
    const line = logTemplates[step % logTemplates.length]!
    visibleLines.value = [...visibleLines.value.slice(-5), line]
    step += 1
    tick.value += 1
  }, 3000)
})

onUnmounted(() => {
  if (timer != null) clearInterval(timer)
})
</script>

<style scoped>
.log-panel {
  position: relative;
  overflow: hidden;
  padding: 20px;
  background: #0f172a !important;
  border: 1px solid #1e293b !important;
  color: #94a3b8;
}

.log-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, var(--primary) 70%, #3b82f6),
    transparent
  );
  opacity: 0.5;
  pointer-events: none;
}

.log-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
}

.log-title {
  margin: 0;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.log-dots {
  display: flex;
  gap: 6px;
}

.log-dots span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #334155;
}

.log-body {
  position: relative;
  z-index: 1;
  font-family: ui-monospace, 'Cascadia Code', 'Segoe UI Mono', monospace;
  font-size: 11px;
  line-height: 1.65;
}

.log-line {
  margin: 0 0 2px;
}

.log-ts--blue {
  color: #60a5fa;
}
.log-ts--green {
  color: #34d399;
}
.log-ts--muted {
  color: #94a3b8;
}
.log-ts--amber {
  color: #fbbf24;
}

.log-cursor {
  margin: 4px 0 0;
  animation: logPulse 2s infinite;
  color: #64748b;
}

@keyframes logPulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.35;
  }
}
</style>
