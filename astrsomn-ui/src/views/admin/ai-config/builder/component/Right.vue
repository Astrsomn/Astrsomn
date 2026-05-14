<template>
  <aside class="right-panel">
    <RightTop :mode="mode" @toggle="toggleMode"/>

    <component :is="currentPanel"/>

  </aside>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue';
import RightTop from './right/RightTop.vue';
import CodePanel from './right/CodePanel.vue';
import ChatPanel from './right/ChatPanel.vue';

const mode = ref<'chat' | 'code'>('chat');

const currentPanel = computed(() => {
  return mode.value === 'chat' ? ChatPanel : CodePanel;
});

const toggleMode = () => {
  mode.value = mode.value === 'chat' ? 'code' : 'chat';
};
</script>

<style scoped>
.right-panel {
  flex: 1 1 0;
  min-width: 0;
  max-width: 520px;
  display: flex;
  flex-direction: column;
  z-index: 10;
  position: relative;
  min-height: 0;
  height: 100%;
  background: var(--builder-right-bg);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--builder-right-border);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--builder-right-shadow);
  overflow: hidden;
}

.float-toolbar {
  position: absolute;
  bottom: 32px;
  right: 32px;
  z-index: 100;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 8px;
  box-shadow: var(--shadow-card);
}

.toolbar-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toolbar-btn:hover {
  background: var(--bg-elevated);
  color: var(--primary);
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background: var(--border-default);
}
</style>
