<template>
  <div style="padding: 20px; border-bottom: 1px solid var(--border-default); display: flex; align-items: center; justify-content: space-between; background: var(--bg-card);">
    <div style="display: flex; align-items: center; gap: 8px;">
      <div style="width: 10px; height: 10px; background: #10b981; border-radius: 50%; box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);"></div>
      <span style="font-weight: bold; color: var(--text-primary);">实时预览测试</span>
    </div>
    <button style="font-size: 11px; font-weight: bold; color: var(--text-secondary); padding: 4px 12px; border-radius: var(--radius-md); transition: all 0.2s;" @mouseenter="this.style.color = 'var(--primary)'; this.style.background = 'rgba(99, 102, 241, 0.1)';" @mouseleave="this.style.color = 'var(--text-secondary)'; this.style.background = 'transparent';">
      <span class="anticon anticon-delete" style="margin-right: 4px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="delete" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M352 144h320c4.4 0 8-3.6 8-8V80c0-4.4-3.6-8-8-8H352c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8zm432 80H120c-4.4 0-8 3.6-8 8v528c0 4.4 3.6 8 8 8h664c4.4 0 8-3.6 8-8V232c0-4.4-3.6-8-8-8zm-200 0H328v528H120V232h456z"></path></svg></span>重置对话
    </button>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>