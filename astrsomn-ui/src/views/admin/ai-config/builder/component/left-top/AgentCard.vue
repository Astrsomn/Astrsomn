<template>
  <div style="flex: 1;">
    <div style="background: var(--primary-gradient); border-radius: var(--radius-xl); padding: 16px; position: relative; overflow: hidden; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); box-shadow: var(--shadow-overview); color: white; cursor: pointer;" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'var(--shadow-overview)';">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <span style="font-size: 10px; font-weight: bold; color: rgba(255, 255, 255, 0.8); text-transform: uppercase;">Agent Name</span>
        <span class="anticon anticon-robot" style="color: rgba(255, 255, 255, 0.5); font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="robot" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M880 512c0-176-144-320-320-320S240 336 240 512c0 156.8 118.3 287.4 275.5 315.1V896h83v-68.9C761.7 799.4 880 668.8 880 512zm-616 0c0-132.5 107.5-240 240-240s240 107.5 240 240-107.5 240-240 240-240-107.5-240-240zm360-144c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80zm-80 256c-44.2 0-80-35.8-80-80s35.8-80 80-80 80 35.8 80 80-35.8 80-80 80z"></path></svg></span>
      </div>
      <div style="font-weight: bold; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Astrsomn_v2</div>
      <div style="font-size: 10px; color: rgba(255, 255, 255, 0.7); margin-top: 4px;">Ready to sync</div>
      <div style="position: absolute; inset: 0; background: rgba(255, 255, 255, 0.1); display: flex; align-items: center; justify-content: center; gap: 12px; opacity: 0; transition: opacity 0.2s; pointer-events: none;" @mouseenter="this.style.opacity = '1'; this.style.pointerEvents = 'auto';">
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: white; color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)';" @mouseleave="this.style.transform = 'scale(1)';" title="修改基本信息">
          <span class="anticon anticon-edit"><svg viewBox="64 64 896 896" focusable="false" data-icon="edit" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M257.7 752c13.3 0 24.1-10.8 24.1-24.1V432.7L592.3 176.3c15.1-15.1 40.5-4.4 40.5 16.9V728c0 17.7-14.3 32-32 32H257.7zM832 64H600c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h184v696c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8V72c0-4.4-3.6-8-8-8H832z"></path><path d="M375.4 650.6c-6.3 6.3-16.4 6.3-22.7 0l-117-117c-6.3-6.3-6.3-16.4 0-22.7s16.4-6.3 22.7 0l117 117c6.3 6.3 6.3 16.4 0 22.7z"></path><path d="M481.4 650.6c-6.3 6.3-16.4 6.3-22.7 0l-117-117c-6.3-6.3-6.3-16.4 0-22.7s16.4-6.3 22.7 0l117 117c6.3 6.3 6.3 16.4 0 22.7z"></path></svg></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>