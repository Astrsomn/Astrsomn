<template>
  <div class="node-connector" style="flex: 1;">
    <div style="background: var(--bg-card); border: 1px solid var(--border-default); border-left: 4px solid #f97316; border-radius: var(--radius-xl); padding: 16px; position: relative; overflow: hidden; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" @mouseenter="this.style.transform = 'translateY(-3px)'; this.style.boxShadow = 'var(--shadow-card)'; this.style.borderColor = 'var(--primary)';" @mouseleave="this.style.transform = 'translateY(0)'; this.style.boxShadow = 'none'; this.style.borderColor = 'var(--border-default)';">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <span style="font-size: 10px; font-weight: bold; color: #f97316; text-transform: uppercase;">Account</span>
        <span class="anticon anticon-key" style="color: rgba(249, 115, 22, 0.3); font-size: 12px;"><svg viewBox="64 64 896 896" focusable="false" data-icon="key" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M533.4 286.5c-5.9 5.9-15.5 5.9-21.4 0l-84.2-84.2c-5.9-5.9-5.9-15.5 0-21.4l21.4-21.4c5.9-5.9 15.5-5.9 21.4 0l105.6 105.6c5.9 5.9 5.9 15.5 0 21.4l-21.4 21.4zm-135.8-30.1c-12 12-31.4 12-43.4 0L181 149.4c-12-12-12-31.4 0-43.4s31.4-12 43.4 0l48.9 48.9-21.4 21.4-48.9-48.9c-5.9-5.9-15.5-5.9-21.4 0s-5.9 15.5 0 21.4l62.7 62.7c5.9 5.9 15.5 5.9 21.4 0l21.4-21.4 109.8 109.8c5.9 5.9 15.5 5.9 21.4 0s5.9-15.5 0-21.4L397.6 256.4z"></path><path d="M908.1 330.1c-4.4-4.4-11.4-4.4-15.8 0l-139.8 139.8c-12 12-31.4 12-43.4 0l-110-110c-12-12-31.4-12-43.4 0l-162.1 162.1c-4.4 4.4-4.4 11.4 0 15.8l323.8 323.8c4.4 4.4 11.4 4.4 15.8 0l323.8-323.8c4.4-4.4 4.4-11.4 0-15.8zm-64.9 259-259 259-259-259 110-110c12-12 31.4-12 43.4 0l110 110c12 12 31.4 12 43.4 0l110-110c12-12 31.4-12 43.4 0l110 110z"></path></svg></span>
      </div>
      <div style="font-weight: bold; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">OpenAI_Personal</div>
      <div style="font-size: 10px; color: var(--text-secondary); margin-top: 4px;">余额充足</div>
      <!-- 预留操作入口 -->
      <div style="position: absolute; inset: 0; background: rgba(255, 255, 255, 0.1); display: flex; align-items: center; justify-content: center; gap: 12px; opacity: 0; transition: opacity 0.2s; pointer-events: none;" @mouseenter="this.style.opacity = '1'; this.style.pointerEvents = 'auto';">
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: var(--bg-elevated); border: 1px solid var(--border-default); color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)'; this.style.color = 'var(--primary-light)';" @mouseleave="this.style.transform = 'scale(1)'; this.style.color = 'var(--primary)';" title="切换账号">
          <span class="anticon anticon-unordered-list"><svg viewBox="64 64 896 896" focusable="false" data-icon="unordered-list" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M112 112a16 16 0 00-16 16v16a16 16 0 0016 16h64a16 16 0 0016-16v-16a16 16 0 00-16-16h-64zm0 192a16 16 0 00-16 16v16a16 16 0 0016 16h64a16 16 0 0016-16v-16a16 16 0 00-16-16h-64zm0 192a16 16 0 00-16 16v16a16 16 0 0016 16h64a16 16 0 0016-16v-16a16 16 0 00-16-16h-64zm736-352h-160a16 16 0 010-32h160a16 16 0 010 32zm0 192h-352a16 16 0 010-32h352a16 16 0 010 32zm0 192h-352a16 16 0 010-32h352a16 16 0 010 32z"></path></svg></span>
        </button>
        <button style="width: 32px; height: 32px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; background: var(--bg-elevated); border: 1px solid var(--border-default); color: var(--primary); transition: all 0.2s;" @mouseenter="this.style.transform = 'scale(1.1)'; this.style.color = 'var(--primary-light)';" @mouseleave="this.style.transform = 'scale(1)'; this.style.color = 'var(--primary)';" title="添加新账号">
          <span class="anticon anticon-plus"><svg viewBox="64 64 896 896" focusable="false" data-icon="plus" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z"></path><path d="M176 474h672q8 0 8 8v60q0 8-8 8H176q-8 0-8-8v-60q0-8 8-8z"></path></svg></span>
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