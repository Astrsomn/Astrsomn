<template>
  <section class="config-section">
    <div class="section-header">
      <div class="step-number">{{ step }}</div>
      <h3 class="section-title">{{ title }}</h3>
    </div>
    <p v-if="hint" class="section-hint">{{ hint }}</p>
    <slot/>
  </section>
</template>

<script lang="ts" setup>
defineProps<{
  step: number | string
  title: string
  hint?: string
}>()
</script>

<style scoped>
.config-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-hint {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.45;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-elevated);
  color: var(--text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
}

.section-title {
  font-size: 12px;
  font-weight: bold;
  color: var(--text-primary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
</style>
