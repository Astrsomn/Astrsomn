<template>
  <div class="admin-page">
    <div class="page-content">
      <slot>
        <a-empty :description="emptyText" />
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string;
    description: string;
    emptyText?: string;
  }>(),
  {
    emptyText: '暂无数据'
  }
);
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.page-title-wrap {
  min-width: 0;
}

.page-title {
  margin: 0 0 6px;
  color: var(--text-heading);
  font-size: 22px;
  font-weight: 700;
}

.page-description {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.page-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.page-content {
  min-height: 360px;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 24px;
}
</style>
