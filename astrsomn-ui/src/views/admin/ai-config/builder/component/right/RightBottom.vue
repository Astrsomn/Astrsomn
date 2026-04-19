<template>
  <div style="padding: 24px; background: var(--bg-card);">
    <div style="position: relative; background: var(--bg-elevated); border-radius: var(--radius-xl); border: 1px solid var(--border-default); padding: 8px; transition: all 0.3s;" @focusin="this.style.borderColor = 'var(--primary)'; this.style.boxShadow = '0 0 0 4px rgba(99, 102, 241, 0.1)';" @focusout="this.style.borderColor = 'var(--border-default)'; this.style.boxShadow = 'none';">
      <textarea rows="2" style="width: 100%; background: transparent; border: none; outline: none; font-size: 12px; padding: 12px; resize: none; font-weight: 500; color: var(--text-primary);" placeholder="发送指令给 Agent..." class="custom-scrollbar"></textarea>
      <div style="display: flex; align-items: center; justify-content: space-between; padding: 0 12px 8px;">
        <div style="display: flex; gap: 16px; color: var(--text-secondary);">
          <span class="anticon anticon-attachment" style="cursor: pointer; transition: all 0.2s; font-size: 14px;" @mouseenter="this.style.color = 'var(--primary)';" @mouseleave="this.style.color = 'var(--text-secondary)';"><svg viewBox="64 64 896 896" focusable="false" data-icon="attachment" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M512 64c-176.7 0-320 143.3-320 320v320c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V384c0-176.7-143.3-320-320-320zM704 480c0 4.4-3.6 8-8 8H544v144c0 4.4-3.6 8-8 8h-64c-4.4 0-8-3.6-8-8V488H328c-4.4 0-8-3.6-8-8v-64c0-4.4 3.6-8 8-8h144V288c0-4.4 3.6-8 8-8h64c4.4 0 8 3.6 8 8v144h144c4.4 0 8 3.6 8 8v64z"></path></svg></span>
          <span class="anticon anticon-microphone" style="cursor: pointer; transition: all 0.2s; font-size: 14px;" @mouseenter="this.style.color = 'var(--primary)';" @mouseleave="this.style.color = 'var(--text-secondary)';"><svg viewBox="64 64 896 896" focusable="false" data-icon="microphone" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M476 800c-196 0-356-160-356-356V256c0-17.7 14.3-32 32-32s32 14.3 32 32v188c0 141.1 114.9 256 256 256 17.7 0 32-14.3 32-32s-14.3-32-32-32zm48-248c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v112c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8v-112zm32-64c0-17.7-14.3-32-32-32s-32 14.3-32 32v48c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8v-48z"></path></svg></span>
        </div>
        <button style="background: var(--primary-gradient); color: white; width: 36px; height: 36px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; transition: all 0.3s; box-shadow: var(--shadow-card);" @mouseenter="this.style.opacity = '0.9';" @mouseleave="this.style.opacity = '1';" @mousedown="this.style.transform = 'scale(0.9)';" @mouseup="this.style.transform = 'scale(1)';">
          <span class="anticon anticon-send" style="font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="send" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M214.9 496.1a28.6 28.6 0 000 40l165.4 116.8c30.2 21.3 71.3 19.8 99.5-3.8l423.1-353c8.1-6.7 8.1-18 0-24.7L480 147.9c-28.2-23.6-69.3-25.1-99.5-3.8L214.9 456.2a28.6 28.6 0 000 40z"></path></svg></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}
</style>