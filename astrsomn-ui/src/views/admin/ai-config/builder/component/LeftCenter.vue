<template>
  <div style="flex: 1; overflow-y: auto; padding: 16px 32px 32px;" class="custom-scrollbar">
    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 24px;">
      <span style="width: 6px; height: 16px; background: var(--border-default); border-radius: 3px;"></span>
      <h2 style="font-weight: bold; font-size: 14px; color: var(--text-heading);">功能增强模组</h2>
    </div>
    
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
      <!-- 左列 -->
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <!-- Prompt 块 -->
        <PromptCard />
        
        <!-- Image Model 块 -->
        <ImageModelCard />
      </div>

      <!-- 右列 -->
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <!-- Tools 块 -->
        <ToolCard />
        
        <!-- RAG/Embedding 块 -->
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