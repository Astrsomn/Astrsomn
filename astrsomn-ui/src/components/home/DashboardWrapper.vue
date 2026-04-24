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
  height: auto;
  margin-left: auto;
  margin-right: auto;
  display: flex;
  flex-direction: column;

}
</style>
