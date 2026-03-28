<template>
  <section class="dashboard-panel">
    <header v-if="title || subtitle || $slots.headerExtra" class="panel-header">
      <div class="panel-title-area">
        <div class="title-main">
          <span v-if="title" class="panel-title">{{ title }}</span>
          <span v-if="subtitle" class="panel-subtitle">{{ subtitle }}</span>
        </div>
        <slot name="headerExtra" />
      </div>
    </header>

    <div class="panel-body">
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
  subtitle?: string
}>()
</script>

<style scoped>
.dashboard-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 16px;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.panel-header {
  padding: 24px 24px 16px;
}

.panel-title-area {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.title-main {
  min-width: 0;
}

.panel-title {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.02em;
}

.panel-subtitle {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--text-muted);
}

.panel-body {
  padding: 0 24px 24px;
  flex: 1;
}
</style>
