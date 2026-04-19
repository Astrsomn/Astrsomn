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
  background: #fff;
  height: calc(100vh - 70px);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  overflow: hidden;
}

.main-content {
  max-width: 1600px;
  height: 100%;
  margin-left: auto;
  margin-right: auto;
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.center-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
  min-height: 0;
}
</style>