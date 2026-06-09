<template>
  <div :style="{ width: width + 'px' }" class="ast-sidebar">
    <div class="ast-sidebar-top">
      <slot name="top"/>
    </div>
    <div class="ast-sidebar-body">
      <slot/>
    </div>
    <div class="ast-sidebar-footer">
      <slot name="footer"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
withDefaults(defineProps<{
  width?: number
}>(), {
  width: 288,
})
</script>

<style scoped>
.ast-sidebar {
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
  position: relative;
  flex-shrink: 0;
}

.ast-sidebar-top {
  flex-shrink: 0;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px solid var(--border-default);
}

.ast-sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px;
}

.ast-sidebar-footer {
  flex-shrink: 0;
  padding: 12px 14px;
  border-top: 1px solid var(--border-default);
}
</style>
