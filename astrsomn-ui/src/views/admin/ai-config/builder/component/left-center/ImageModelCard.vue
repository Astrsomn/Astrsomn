<template>
  <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-radius: var(--radius-xl); padding: 20px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <div style="width: 32px; height: 32px; background: rgba(236, 72, 153, 0.1); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #ec4899; box-shadow: var(--shadow-sm);">
          <span class="anticon anticon-picture"><svg viewBox="64 64 896 896" focusable="false" data-icon="picture" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zm-40 632H136V232h752v560zM512 504c35.3 0 64-28.7 64-64s-28.7-64-64-64-64 28.7-64 64 28.7 64 64 64z"></path></svg></span>
        </div>
        <h3 style="font-weight: bold; color: var(--text-primary);">多模态增强</h3>
      </div>
      <button style="color: #ec4899; padding: 4px; border-radius: var(--radius-md); transition: all 0.2s;" @mouseenter="this.style.background = 'rgba(236, 72, 153, 0.1)';">
        <span class="anticon anticon-plus-circle"><svg viewBox="64 64 896 896" focusable="false" data-icon="plus-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm170.3 440H544v136.3c0 4.7-3.9 8.6-8.6 8.6H480.6c-4.7 0-8.6-3.9-8.6-8.6V504H341.7c-4.7 0-8.6-3.9-8.6-8.6V448c0-4.7 3.9-8.6 8.6-8.6h136.3V309.7c0-4.7 3.9-8.6 8.6-8.6h56.7c4.7 0 8.6 3.9 8.6 8.6V440h136.3c4.7 0 8.6 3.9 8.6 8.6v56.7c0 4.7-3.9 8.6-8.6 8.6z"></path></svg></span>
      </button>
    </div>
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
      <div style="padding: 12px; background: var(--bg-elevated); border: 1px solid var(--border-default); border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: space-between; cursor: pointer; transition: all 0.2s;" @mouseenter="this.style.borderColor = 'rgba(236, 72, 153, 0.5)';">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span class="anticon anticon-image" style="color: #ec4899; font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="image" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M912 192H384v320h528V192zm-528 32v288H112V224h272zm64 224a64 64 0 11128 0 64 64 0 01-128 0z"></path></svg></span>
          <span style="font-size: 11px; font-weight: bold; color: var(--text-primary);">DALL-E 3</span>
        </div>
        <span class="anticon anticon-right" style="font-size: 10px; color: var(--text-tertiary);"><svg viewBox="64 64 896 896" focusable="false" data-icon="right" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M765.7 486.8L314.9 134.7A7.97 7.97 0 00302 141v742.5a7.97 7.97 0 0012.9 6.3l450.8-352.1a7.95 7.95 0 000-12.6z"></path></svg></span>
      </div>
      <div style="padding: 12px; background: var(--bg-elevated); border: 1px solid var(--border-default); border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: space-between; cursor: pointer; transition: all 0.2s;" @mouseenter="this.style.borderColor = 'rgba(236, 72, 153, 0.5)';">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span class="anticon anticon-microphone" style="color: #f97316; font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="microphone" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M476 800c-196 0-356-160-356-356V256c0-17.7 14.3-32 32-32s32 14.3 32 32v188c0 141.1 114.9 256 256 256 17.7 0 32-14.3 32-32s-14.3-32-32-32zm48-248c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v112c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8v-112zm32-64c0-17.7-14.3-32-32-32s-32 14.3-32 32v48c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8v-48z"></path></svg></span>
          <span style="font-size: 11px; font-weight: bold; color: var(--text-primary);">TTS-1</span>
        </div>
        <span class="anticon anticon-right" style="font-size: 10px; color: var(--text-tertiary);"><svg viewBox="64 64 896 896" focusable="false" data-icon="right" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M765.7 486.8L314.9 134.7A7.97 7.97 0 00302 141v742.5a7.97 7.97 0 0012.9 6.3l450.8-352.1a7.95 7.95 0 000-12.6z"></path></svg></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>