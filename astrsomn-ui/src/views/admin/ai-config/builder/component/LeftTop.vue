<template>
  <div class="left-top">
    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">核心运行链</h2>
      </div>
      <span class="section-badge">必填项</span>
    </div>

    <div class="card-chain">
      <AccountCard />
      <ModelCard />
      <InstanceCard />
      <AgentCard />
    </div>
  </div>
</template>

<script setup lang="ts">
import AccountCard from './left-top/AccountCard.vue';
import ModelCard from './left-top/ModelCard.vue';
import InstanceCard from './left-top/InstanceCard.vue';
import AgentCard from './left-top/AgentCard.vue';
</script>

<style scoped>
.left-top {
  padding: 32px 32px 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-indicator {
  width: 6px;
  height: 16px;
  background: var(--primary);
  border-radius: 3px;
}

.title-text {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-heading);
  margin: 0;
}

.section-badge {
  font-size: 10px;
  color: var(--primary);
  background: var(--primary-hover);
  padding: 2px 8px;
  border-radius: var(--radius-max);
  font-weight: bold;
}

.card-chain {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>