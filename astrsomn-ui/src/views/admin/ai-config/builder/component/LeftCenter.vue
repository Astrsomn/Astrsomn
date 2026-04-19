<template>
  <div class="left-center custom-scrollbar">
    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">功能增强模组</h2>
      </div>
    </div>

    <div class="card-grid">
      <div class="card-column">
        <PromptCard />
        <ImageModelCard />
      </div>
      <div class="card-column">
        <ToolCard />
        <RagCard />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import PromptCard from './left-center/PromptCard.vue';
import ImageModelCard from './left-center/ImageModelCard.vue';
import ToolCard from './left-center/ToolCard.vue';
import RagCard from './left-center/RagCard.vue';
</script>

<style scoped>
.left-center {
  flex: 1;
  overflow-y: auto;
  padding: 16px 32px 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.title-indicator {
  width: 6px;
  height: 16px;
  background: var(--border-default);
  border-radius: 3px;
}

.title-text {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-heading);
  margin: 0;
}

.card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.card-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

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