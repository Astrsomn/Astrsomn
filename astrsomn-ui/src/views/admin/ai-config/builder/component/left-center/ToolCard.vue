<template>
  <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-radius: var(--radius-xl); padding: 20px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <div style="width: 32px; height: 32px; background: rgba(59, 130, 246, 0.1); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #3b82f6; box-shadow: var(--shadow-sm);">
          <span class="anticon anticon-tool"><svg viewBox="64 64 896 896" focusable="false" data-icon="tool" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M901.1 659.8L612.2 370.9c-18.1-18.1-47.5-18.1-65.6 0L255.6 612.6c-14.3 14.3-22.4 33.1-22.4 53.4 0 10.2 2 20.1 5.8 29.5l-65.7 65.7c-8.3 8.3-8.3 21.8 0 30.1l45.3 45.3c8.3 8.3 21.8 8.3 30.1 0l65.7-65.7c9.4 3.8 19.4 5.8 29.5 5.8 20.3 0 39.1-8.1 53.4-22.4l241.7-241.7c18.1-18 18.1-47.4 0-65.5zm-280.7 18.1l-171.4 171.4c-2.6 2.6-6.3 4-10.1 4-3.8 0-7.5-1.4-10.1-4l-65.7-65.7 45.3-45.3 65.7 65.7c2.6 2.6 6.3 4 10.1 4 3.8 0 7.5-1.4 10.1-4l171.4-171.4 45.3 45.3-45.3 45.3z"></path></svg></span>
        </div>
        <h3 style="font-weight: bold; color: var(--text-primary);">扩展插件 (Tools/MCP)</h3>
      </div>
      <button style="width: 32px; height: 32px; background: rgba(59, 130, 246, 0.1); color: #3b82f6; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; transition: all 0.2s; box-shadow: var(--shadow-sm);" @mouseenter="this.style.background = '#3b82f6'; this.style.color = 'white';" @mouseleave="this.style.background = 'rgba(59, 130, 246, 0.1)'; this.style.color = '#3b82f6';">
        <span class="anticon anticon-plus"><svg viewBox="64 64 896 896" focusable="false" data-icon="plus" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M482 152h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm0 656h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm-334-328h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm656 0h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zM152 482h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm656 0h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zM402 814q0 25 17.5 42.5t42.5 17.5 42.5-17.5 17.5-42.5-17.5-42.5-42.5-17.5-42.5 17.5-17.5 42.5zm0-656q0 25 17.5 42.5t42.5 17.5 42.5-17.5 17.5-42.5-17.5-42.5-42.5-17.5-42.5 17.5-17.5 42.5z"></path></svg></span>
      </button>
    </div>
    <div style="display: flex; flex-wrap: wrap; gap: 8px;">
      <div style="padding: 6px 12px; background: rgba(59, 130, 246, 0.1); color: #3b82f6; border: 1px solid rgba(59, 130, 246, 0.2); border-radius: var(--radius-full); display: flex; align-items: center; gap: 8px;">
        <span style="font-size: 10px;">G</span>
        <span style="font-size: 11px; font-weight: bold;">联网搜索</span>
        <span class="anticon anticon-close" style="font-size: 10px; cursor: pointer; transition: all 0.2s;" @mouseenter="this.style.color = '#ef4444';"><svg viewBox="64 64 896 896" focusable="false" data-icon="close" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M563.8 512l262.5-262.5c4.8-4.8 4.8-12.6 0-17.4l-24.6-24.6c-4.8-4.8-12.6-4.8-17.4 0L512 484.2 249.5 221.7c-4.8-4.8-12.6-4.8-17.4 0L207 246.3c-4.8 4.8-4.8 12.6 0 17.4L450.2 512 207 755.5c-4.8 4.8-4.8 12.6 0 17.4l24.6 24.6c4.8 4.8 12.6 4.8 17.4 0L512 539.8l262.5 262.5c4.8 4.8 12.6 4.8 17.4 0l24.6-24.6c4.8-4.8 4.8-12.6 0-17.4L563.8 512z"></path></svg></span>
      </div>
      <div style="padding: 6px 12px; background: var(--bg-elevated); color: var(--text-secondary); border: 1px dashed var(--border-default); border-radius: var(--radius-full); display: flex; align-items: center; gap: 8px; cursor: pointer; transition: all 0.2s;" @mouseenter="this.style.background = 'var(--bg-secondary)';">
        <span class="anticon anticon-plus" style="font-size: 10px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="plus" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M482 152h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm0 656h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm-334-328h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm656 0h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zM152 482h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zm656 0h60q8 0 8 8v60q0 8-8 8h-60q-8 0-8-8v-60q0-8 8-8zM402 814q0 25 17.5 42.5t42.5 17.5 42.5-17.5 17.5-42.5-17.5-42.5-42.5-17.5-42.5 17.5-17.5 42.5zm0-656q0 25 17.5 42.5t42.5 17.5 42.5-17.5 17.5-42.5-17.5-42.5-42.5-17.5-42.5 17.5-17.5 42.5z"></path></svg></span>
        <span style="font-size: 11px; font-weight: bold;">绑定 MCP</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>