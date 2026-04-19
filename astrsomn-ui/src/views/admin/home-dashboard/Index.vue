<template>
  <div class="demo-dashboard-wrapper">
    <main class="main-content">
      <StatsGrid />

      <div class="center-grid">
        <AgentBrainCard />
        <SmallCards />
      </div>

      <ExtensionGrid />
    </main>
  </div>
</template>

<script setup lang="ts">
import StatsGrid from './dashboard-component/top/StatsGrid.vue'
import AgentBrainCard from './dashboard-component/center/left/AgentBrainCard.vue'
import SmallCards from './dashboard-component/center/right/SmallCards.vue'
import ExtensionGrid from './dashboard-component/bottom/ExtensionGrid.vue'
</script>

<style scoped>
.demo-dashboard-wrapper {
  position: relative;
  background: #f8fafc;
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.main-content {
  max-width: 1600px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 32px 32px;
}

.center-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}
</style>
