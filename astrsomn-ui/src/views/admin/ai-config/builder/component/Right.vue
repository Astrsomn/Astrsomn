<template>
  <aside class="right-panel">
    <RightTop :mode="mode" @toggle="toggleMode" />

    <component :is="currentPanel" />

  </aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
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
  width: 420px;
  background: var(--bg-card);
  border-left: 1px solid var(--border-default);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-card);
  z-index: 10;
  position: relative;
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
