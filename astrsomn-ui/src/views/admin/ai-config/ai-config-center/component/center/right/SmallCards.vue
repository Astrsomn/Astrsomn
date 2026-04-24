<template>
  <div class="small-cards col-span-2">
    <div class="card-cell">
      <CredentialManagementCard />
    </div>

    <div class="card-cell">
      <ModelAccessCard />
    </div>

    <div class="card-cell">
      <McpCard />
    </div>

    <div class="card-cell">
      <InferenceConfigCard />
    </div>

    <div class="card-cell">
      <ToolPluginCard />
    </div>

    <div class="card-cell">
      <DiscoveryHub />
    </div>
  </div>
</template>

<script setup lang="ts">
import DiscoveryHub from '@/views/admin/ai-config/ai-config-center/card/DiscoveryHub.vue'
import CredentialManagementCard from '@/views/admin/ai-config/ai-config-center/card/CredentialManagementCard.vue'
import ModelAccessCard from '@/views/admin/ai-config/ai-config-center/card/ModelAccessCard.vue'
import McpCard from '@/views/admin/ai-config/ai-config-center/card/McpCard.vue'
import InferenceConfigCard from '@/views/admin/ai-config/ai-config-center/card/InferenceConfigCard.vue'
import ToolPluginCard from '@/views/admin/ai-config/ai-config-center/card/ToolPluginCard.vue'
</script>

<style scoped>
.small-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-template-rows: repeat(3, minmax(0, 1fr));
  gap: 16px;
  height: 100%;
}

.card-cell {
  min-height: 0;
}
</style>