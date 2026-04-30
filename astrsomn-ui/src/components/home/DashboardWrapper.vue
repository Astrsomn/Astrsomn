<template>
  <div class="dashboard-wrapper">
    <main class="dashboard-main">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.dashboard-wrapper {
  position: relative;
  background: var(--bg-base);
  min-height: calc(100vh - 70px);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 14px;
  color: var(--text-primary);
}

.dashboard-main {
  max-width: 1600px;
  max-height: calc(100vh - 120px);
  width: 100%;
  height: auto;
  margin: 24px auto;
  padding: 0 24px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
</style>
