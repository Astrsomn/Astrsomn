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
  z-index: 30;
}
</style>
