<template>
  <section class="mode-switch-card">
    <div class="mode-copy">
      <div class="mode-title">Astrsomn控制台</div>
      <div class="mode-desc">深度封装 LangChain4j 全能力（LLM / Embedding / 向量库 / 记忆 / RAG / Tools / Agent）</div>
    </div>

    <div class="mode-switch" :class="{ 'mode-switch--management': modelValue === 'management' }">
      <span class="mode-switch__glow" aria-hidden="true"></span>
      <span class="mode-switch__thumb" aria-hidden="true"></span>

      <button
        type="button"
        class="mode-option"
        :class="{ 'mode-option--active': modelValue === 'screen' }"
        @click="updateMode('screen')"
      >
        <span class="mode-option__icon">屏</span>
        <span class="mode-option__text">
          <span class="mode-option__title">数据大屏</span>
          <span class="mode-option__meta">专注展示</span>
        </span>
      </button>

      <button
        type="button"
        class="mode-option"
        :class="{ 'mode-option--active': modelValue === 'management' }"
        @click="updateMode('management')"
      >
        <span class="mode-option__icon">管</span>
        <span class="mode-option__text">
          <span class="mode-option__title">管理后台</span>
          <span class="mode-option__meta">高效操作</span>
        </span>
      </button>
    </div>
  </section>
</template>

<script lang="ts">
export default {
  name: 'DashboardModeSwitch',
}
</script>

<script setup lang="ts">
type DashboardMode = 'screen' | 'management'

defineProps<{
  modelValue: DashboardMode
}>()

const emit = defineEmits<{
  'update:modelValue': [value: DashboardMode]
}>()

const updateMode = (value: DashboardMode) => {
  emit('update:modelValue', value)
}
</script>

<style scoped>
.mode-switch-card {
  position: relative;
  margin-bottom: 24px;
  padding: 6px 20px;
}

.mode-copy {
  grid-column: 1;
  min-width: 0;
  padding-right: 400px;
}

.mode-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-heading);
}

.mode-desc {
  margin-top: 4px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--text-muted);
}

.mode-switch {
  position: absolute;
  top: 50%;
  left: 900px;
  transform: translateY(-50%);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  align-items: stretch;
  gap: 8px;
  min-width: 360px;
  padding: 6px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  overflow: hidden;
}

.mode-switch__glow {
  position: absolute;
  inset: auto -10% -55% auto;
  width: 160px;
  height: 120px;
  /* background: radial-gradient(circle, color-mix(in srgb, var(--primary) 10%, transparent) 0%, transparent 72%); */
  pointer-events: none;
  filter: blur(6px);
}

.mode-switch__thumb {
  position: absolute;
  top: 6px;
  left: 6px;
  width: calc(50% - 10px);
  height: calc(100% - 12px);
  border-radius: var(--radius-sm);
  /* background: */
    /* linear-gradient(135deg, color-mix(in srgb, white 92%, var(--bg-card)), color-mix(in srgb, var(--primary) 4%, white)); */
  border: 1px solid color-mix(in srgb, var(--primary) 8%, var(--border-subtle));
  box-shadow:
    0 8px 18px color-mix(in srgb, black 6%, transparent),
    inset 0 1px 0 color-mix(in srgb, white 56%, transparent);
  transition:
    transform 0.32s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.28s ease;
}

.mode-switch--management .mode-switch__thumb {
  transform: translateX(calc(100% + 8px));
}

.mode-option {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  padding: 12px 16px;
  border: 0;
  background: transparent;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  cursor: pointer;
  transition:
    transform 0.22s ease,
    color 0.22s ease;
}

.mode-option:hover {
  transform: translateY(-1px);
}

.mode-option__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  color: color-mix(in srgb, var(--primary) 48%, var(--text-heading));
  /* background: color-mix(in srgb, var(--primary) 8%, var(--bg-card)); */
  box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--primary) 8%, var(--border-subtle));
  transition:
    transform 0.22s ease,
    background 0.22s ease,
    color 0.22s ease;
}

.mode-option__text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.mode-option__title {
  font-size: 14px;
  font-weight: 700;
  color: inherit;
  white-space: nowrap;
}

.mode-option__meta {
  margin-top: 2px;
  font-size: 12px;
  color: var(--text-muted);
  opacity: 0.82;
  white-space: nowrap;
}

.mode-option--active {
  color: var(--text-heading);
}

.mode-option--active .mode-option__icon {
  transform: scale(1.04);
  background: color-mix(in srgb, var(--primary) 12%, var(--bg-card));
  color: color-mix(in srgb, var(--primary) 68%, var(--text-heading));
}

.mode-option--active .mode-option__meta {
  color: color-mix(in srgb, var(--primary) 36%, var(--text-muted));
  opacity: 1;
}

@media (max-width: 900px) {
  .mode-switch-card {
    padding: 6px 20px;
  }

  .mode-switch {
    position: static;
    transform: none;
    min-width: 100%;
    margin-top: 16px;
  }

  .mode-copy {
    padding-right: 0;
  }
}

@media (max-width: 560px) {
  .mode-switch {
    grid-template-columns: 1fr;
    min-width: 0;
  }

  .mode-switch__thumb,
  .mode-switch__glow {
    display: none;
  }

  .mode-option {
    padding: 12px 14px;
  }

  .mode-option--active {
    background: color-mix(in srgb, var(--primary) 8%, var(--bg-card));
    box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--primary) 14%, transparent);
  }
}
</style>
