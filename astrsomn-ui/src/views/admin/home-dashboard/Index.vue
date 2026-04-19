<template>
  <div class="demo-dashboard-wrapper">
    <main class="main-content">
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

      <div class="bottom-section">
        <div class="animate-fade-in" style="animation-delay: 300ms">
          <ExtensionGrid />
        </div>
      </div>
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
  background: var(--bg-base);
  min-height: calc(100vh - 70px);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 14px;
  color: var(--text-primary);
}

/* 统一字体样式 */
h1, h2, h3, h4, h5, h6 {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-weight: 600;
  color: var(--text-heading);
}

p, span, div {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.main-content {
  max-width: 1600px;
  height: auto;
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
  min-height: 380px;
}

.bottom-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 140px;
}

/* 页面进入动画 */
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