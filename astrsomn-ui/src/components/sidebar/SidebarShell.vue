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
  width: 250,
})
</script>

<style scoped>
.ast-sidebar {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  box-shadow: 2px 0 8px color-mix(in srgb, var(--shadow-color, #000) 4%, transparent);
  border-right: 1px solid #e5e6eb47;
  position: relative;
}

.ast-sidebar-top {
  flex-shrink: 0;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ast-sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px;
}

.ast-sidebar-footer {
  flex-shrink: 0;
  padding: 12px 14px;
}
</style>
