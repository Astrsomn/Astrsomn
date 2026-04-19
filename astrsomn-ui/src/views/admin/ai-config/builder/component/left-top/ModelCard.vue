<template>
  <div class="node-connector" style="flex: 1;">
    <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-left: 4px solid var(--primary); border-radius: var(--radius-xl); padding: 16px; position: relative; overflow: hidden; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <span style="font-size: 10px; font-weight: bold; color: var(--primary); text-transform: uppercase;">Model</span>
        <span class="anticon anticon-microchip" style="color: rgba(59, 130, 246, 0.3); font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="microchip" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M192 880h640c4.4 0 8-3.6 8-8V152c0-4.4-3.6-8-8-8H192c-4.4 0-8 3.6-8 8v720c0 4.4 3.6 8 8 8zm64-720v720h512V160H256zM336 280a40 40 0 1080 0 40 40 0 10-80 0zm-40 160a40 40 0 1080 0 40 40 0 10-80 0zm0 160a40 40 0 1080 0 40 40 0 10-80 0zm320-160a40 40 0 1080 0 40 40 0 10-80 0zm-80-160a40 40 0 1080 0 40 40 0 10-80 0zm0 320a40 40 0 1080 0 40 40 0 10-80 0z"></path></svg></span>
      </div>
      <div style="font-weight: bold; color: var(--text-primary);">GPT-4o</div>
      <div style="font-size: 10px; color: var(--text-secondary); margin-top: 4px;">128k context</div>
      <div style="position: absolute; inset: 0; background: rgba(255, 255, 255, 0.1); display: flex; align-items: center; justify-content: center; gap: 12px; opacity: 0; transition: opacity 0.2s; pointer-events: none;" @mouseenter="this.style.opacity = '1'; this.style.pointerEvents = 'auto';">
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: var(--bg-elevated); border: 1px solid var(--border-default); color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)'; this.style.color = 'var(--primary-light)';" @mouseleave="this.style.transform = 'scale(1)'; this.style.color = 'var(--primary)';" title="选择模型库">
          <span class="anticon anticon-search"><svg viewBox="64 64 896 896" focusable="false" data-icon="search" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-131.8-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62.1l259.7 259.6a8.2 8.2 0 0011.6 0l43.6-43.5a8.2 8.2 0 000-11.6zM570.4 570.4C528 612.7 471.8 640 412 640s-116-27.3-158.4-69.6C215.3 528 188 471.8 188 412s27.3-116 69.6-158.4C326 215.3 382.2 188 442 188s116 27.3 158.4 69.6C650.7 326 678 382.2 678 442s-27.3 116-69.6 158.4z"></path></svg></span>
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