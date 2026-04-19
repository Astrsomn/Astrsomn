<template>
  <div class="node-connector" style="flex: 1;">
    <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-left: 4px solid #10b981; border-radius: var(--radius-xl); padding: 16px; position: relative; overflow: hidden; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <span style="font-size: 10px; font-weight: bold; color: #10b981; text-transform: uppercase;">Instance</span>
        <span class="anticon anticon-sliders" style="color: rgba(16, 185, 129, 0.3); font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="sliders" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M288 416h256c4.4 0 8-3.6 8-8V280c0-4.4-3.6-8-8-8H288c-4.4 0-8 3.6-8 8v128c0 4.4 3.6 8 8 8zm-64-80c0-17.7 14.3-32 32-32h256c17.7 0 32 14.3 32 32v128c0 17.7-14.3 32-32 32H256c-17.7 0-32-14.3-32-32V336zm-96 112h192c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8H128c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8zm0 160h192c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8H128c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8zm544 0h192c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8H672c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8zm0-160h192c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8H672c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8z"></path></svg></span>
      </div>
      <div style="font-weight: bold; color: var(--text-primary);">创意生成参数</div>
      <div style="font-size: 10px; color: rgba(16, 185, 129, 0.7); margin-top: 4px;">Temp: 0.8</div>
      <div style="position: absolute; inset: 0; background: rgba(255, 255, 255, 0.1); display: flex; align-items: center; justify-content: center; gap: 12px; opacity: 0; transition: opacity 0.2s; pointer-events: none;" @mouseenter="this.style.opacity = '1'; this.style.pointerEvents = 'auto';">
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: var(--bg-elevated); border: 1px solid var(--border-default); color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)'; this.style.color = 'var(--primary-light)';" @mouseleave="this.style.transform = 'scale(1)'; this.style.color = 'var(--primary)';" title="配置实例">
          <span class="anticon anticon-setting"><svg viewBox="64 64 896 896" focusable="false" data-icon="setting" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M880 112H144c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V144c0-17.7-14.3-32-32-32zm-40 160H184V184h656v88zm-40 64H184v-88h616v88zm-40 64H184v-88h576v88zm-40 64H184v-88h536v88zm-40 64H184v-88h496v88z"></path></svg></span>
        </button>
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: var(--bg-elevated); border: 1px solid var(--border-default); color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)'; this.style.color = 'var(--primary-light)';" @mouseleave="this.style.transform = 'scale(1)'; this.style.color = 'var(--primary)';" title="存为新模板">
          <span class="anticon anticon-save"><svg viewBox="64 64 896 896" focusable="false" data-icon="save" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M890.1 253.3L649 494.4l-42.8-42.8-65.4 65.4V184c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v332.2l-65.4-65.4-42.8 42.8L133.9 253.3C119.3 238.7 97 237.5 82.4 252.1 67.8 266.7 66.6 289 81.2 303.7l210.9 210.9c3.2 3.2 7.6 3.2 10.8 0l290.7-290.7c14.6-14.6 15.8-36.9 1.2-51.5zM544 816c-4.4 0-8-3.6-8-8V648c0-4.4-3.6-8-8-8h-80c-4.4 0-8-3.6-8-8V464c0-4.4-3.6-8-8-8h-48c-4.4 0-8-3.6-8-8V256c0-4.4-3.6-8-8-8h-48c-4.4 0-8-3.6-8-8v-48c0-4.4-3.6-8-8-8H144c-4.4 0-8-3.6-8-8V32c0-4.4 3.6-8 8-8h664c35.3 0 64 28.7 64 64v720c0 4.4-3.6 8-8 8H544z"></path></svg></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.node-connector {
  position: relative;
}

.node-connector:not(:last-child)::after {
  content: "";
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 8px solid var(--border-default);
}
</style>