<template>
  <aside style="width: 420px; background: var(--bg-card); border-left: 1px solid var(--border-default); display: flex; flex-direction: column; box-shadow: var(--shadow-card); z-index: 30;">
    <RightTop />
    
    <div style="flex: 1; overflow-y: auto; padding: 24px; display: flex; flex-direction: column; gap: 24px; background: linear-gradient(to bottom, rgba(99, 102, 241, 0.05), var(--bg-card));" class="custom-scrollbar">
      <!-- 消息展示 -->
      <div style="display: flex; align-items: flex-start; gap: 12px;">
        <div style="width: 36px; height: 36px; background: var(--primary-gradient); border-radius: var(--radius-lg); flex-shrink: 0; display: flex; align-items: center; justify-content: center; color: white; font-size: 12px; box-shadow: var(--shadow-card);">
          <span class="anticon anticon-robot"><svg viewBox="64 64 896 896" focusable="false" data-icon="robot" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M880 512c0-176-144-320-320-320S240 336 240 512c0 156.8 118.3 287.4 275.5 315.1V896h83v-68.9C761.7 799.4 880 668.8 880 512zm-616 0c0-132.5 107.5-240 240-240s240 107.5 240 240-107.5 240-240 240-240-107.5-240-240zm360-144c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80zm-80 256c-44.2 0-80-35.8-80-80s35.8-80 80-80 80 35.8 80 80-35.8 80-80 80z"></path></svg></span>
        </div>
        <div style="background: var(--bg-elevated); padding: 16px; border-radius: var(--radius-xl); border-top-left-radius: 0; font-size: 12px; line-height: 1.5; color: var(--text-primary); border: 1px solid rgba(99, 102, 241, 0.1);">
          主链路已就绪！✨ 所有的配置改动都会在这里即时生效，试着跟我打个招呼吧。
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <RightBottom />
  </aside>
</template>

<script setup lang="ts">
import RightTop from './right/RightTop.vue';
import RightBottom from './right/RightBottom.vue';
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