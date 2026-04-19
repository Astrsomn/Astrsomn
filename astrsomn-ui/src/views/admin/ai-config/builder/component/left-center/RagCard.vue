<template>
  <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-radius: var(--radius-xl); padding: 20px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <div style="width: 32px; height: 32px; background: rgba(16, 185, 129, 0.1); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #10b981; box-shadow: var(--shadow-sm);">
          <span class="anticon anticon-database"><svg viewBox="64 64 896 896" focusable="false" data-icon="database" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M832 64H192c-17.7 0-32 14.3-32 32v768c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-32 768H224V128h576v704zM288 580c0 22.1-17.9 40-40 40s-40-17.9-40-40c0-46.7 19.9-88 40-88s40 41.3 40 88zm0-200c0 22.1-17.9 40-40 40s-40-17.9-40-40c0-46.7 19.9-88 40-88s40 41.3 40 88zm320 200c0 22.1-17.9 40-40 40s-40-17.9-40-40c0-46.7 19.9-88 40-88s40 41.3 40 88zm0-200c0 22.1-17.9 40-40 40s-40-17.9-40-40c0-46.7 19.9-88 40-88s40 41.3 40 88z"></path></svg></span>
        </div>
        <h3 style="font-weight: bold; color: var(--text-primary);">知识库 (RAG)</h3>
      </div>
      <div style="display: flex; gap: 4px;">
        <button style="width: 28px; height: 28px; background: rgba(16, 185, 129, 0.1); color: #10b981; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; transition: all 0.2s;" @mouseenter="this.style.background = 'rgba(16, 185, 129, 0.2)';">
          <span class="anticon anticon-search" style="font-size: 10px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="search" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-131.8-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62.1l259.7 259.6a8.2 8.2 0 0011.6 0l43.6-43.5a8.2 8.2 0 000-11.6zM570.4 570.4C528 612.7 471.8 640 412 640s-116-27.3-158.4-69.6C215.3 528 188 471.8 188 412s27.3-116 69.6-158.4C326 215.3 382.2 188 442 188s116 27.3 158.4 69.6C650.7 326 678 382.2 678 442s-27.3 116-69.6 158.4z"></path></svg></span>
        </button>
        <button style="width: 28px; height: 28px; background: #10b981; color: white; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.05)';">
          <span class="anticon anticon-cloud-upload" style="font-size: 10px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="cloud-upload" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M869 352.9c0-16.4-13.2-29.7-29.7-29.7H696c0-44.1-35.9-80-80-80H408c-44.1 0-80 35.9-80 80H184.7c-16.5 0-29.7 13.2-29.7 29.7 0 16.4 13.2 29.7 29.7 29.7h35.1v192h422V382.6h35.1c16.5 0 29.7-13.2 29.7-29.7zm-532.1 58.1h348.6v150.2H336.9V411zM408 232c22.1 0 40 17.9 40 40h216c0-22.1 17.9-40 40-40s40 17.9 40 40H840c0-66.2-53.8-120-120-120H408c-66.2 0-120 53.8-120 120h32z"></path></svg></span>
        </button>
      </div>
    </div>
    <div style="padding: 24px; border: 2px dashed var(--border-default); border-radius: var(--radius-lg); text-align: center; transition: all 0.3s; cursor: pointer;" @mouseenter="this.style.borderColor = 'rgba(16, 185, 129, 0.5)'; this.style.background = 'rgba(16, 185, 129, 0.05)';" @mouseleave="this.style.borderColor = 'var(--border-default)'; this.style.background = 'transparent';">
      <p style="font-size: 11px; color: var(--text-secondary); font-weight: 500;">拖拽或点击上传文档数据</p>
      <span style="font-size: 9px; color: var(--text-tertiary); margin-top: 4px; display: block;">支持 PDF, Markdown, Notion</span>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>