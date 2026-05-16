<template>
  <div class="dashboard-wrapper">
    <main class="dashboard-main">
      <slot/>
    </main>
  </div>
</template>

<style scoped>
.dashboard-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  background: var(--bg-base);

  height: calc(100vh - 60px);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 14px;
  color: var(--text-primary);
}

.dashboard-main {
  max-width: 1600px;
  /* 高度在 600px 到 800px 之间，理想高度是视口高度减去 60px */
  height: clamp(600px, calc(100vh - 60px), 800px);
  width: 100%;

  padding: 30px 30px 100px 30px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  /* 移除了 min-height, max-height 和 height: auto */
}
</style>
