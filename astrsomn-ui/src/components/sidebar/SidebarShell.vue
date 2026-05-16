<template>
  <div :class="{ collapsed }" :style="{ width: currentWidth + 'px' }" class="ast-sidebar">
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
import {computed} from 'vue'

const props = withDefaults(defineProps<{
  collapsed?: boolean
  width?: number
  collapsedWidth?: number
}>(), {
  collapsed: false,
  width: 320,
  collapsedWidth: 64,
})

const currentWidth = computed(() => props.collapsed ? props.collapsedWidth : props.width)
</script>

<style scoped>
.ast-sidebar {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
  overflow: hidden;
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.ast-sidebar-top {
  flex-shrink: 0;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px solid var(--border-default);
}

.ast-sidebar.collapsed .ast-sidebar-top {
  flex-direction: column;
  padding: 14px 8px;
}

.ast-sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px;
}

.ast-sidebar.collapsed .ast-sidebar-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ast-sidebar-footer {
  flex-shrink: 0;
  padding: 12px 14px;
  border-top: 1px solid var(--border-default);
}

.ast-sidebar.collapsed .ast-sidebar-footer {
  display: flex;
  justify-content: center;
  padding: 12px 8px;
}
</style>
