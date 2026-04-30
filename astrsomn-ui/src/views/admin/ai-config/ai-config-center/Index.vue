<template>
  <DashboardWrapper>

      <div class="animate-fade-in" style="animation-delay: 0ms">
      <StatsGrid />
    </div>

    <div class="center-grid">
      <div class="animate-fade-in" style="animation-delay: 100ms">
        <AgentBrainCard />
      </div>
      <div class="animate-fade-in" style="animation-delay: 200ms">
        <SmallCards />
      </div>
    </div>

  </DashboardWrapper>
</template>

<script setup lang="ts">
import DashboardWrapper from '@/components/home/DashboardWrapper.vue'
import StatsGrid from '@/views/admin/ai-config/ai-config-center/component/top/StatsGrid.vue'
import AgentBrainCard from '@/views/admin/ai-config/ai-config-center/component/center/left/AgentBrainCard.vue'
import SmallCards from '@/views/admin/ai-config/ai-config-center/component/center/right/SmallCards.vue'

</script>

<style scoped>
.center-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
  min-height: 380px;
}

.bottom-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 140px;
}

.animate-fade-in {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.6s ease-out forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
