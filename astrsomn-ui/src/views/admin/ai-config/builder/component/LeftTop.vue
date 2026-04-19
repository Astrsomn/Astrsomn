<template>
  <div style="padding: 32px 32px 16px;">
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <span style="width: 6px; height: 16px; background: var(--primary); border-radius: 3px;"></span>
        <h2 style="font-weight: bold; font-size: 14px; color: var(--text-heading);">核心运行链</h2>
      </div>
      <span style="font-size: 10px; color: var(--primary); background: var(--primary-hover); padding: 2px 8px; border-radius: var(--radius-max); font-weight: bold;">必填项</span>
    </div>
    
    <div style="display: flex; align-items: center; gap: 16px;">
      <!-- 账号配置方块 -->
      <AccountCard />
      
      <!-- 模型配置方块 -->
      <ModelCard />
      
      <!-- 实例配置方块 -->
      <InstanceCard />
      
      <!-- Agent 最终态方块 -->
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
.node-connector {
  position: relative;
}

.node-connector:not(:last-child)::after {
  content: "";
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 8px solid var(--border-default);
}
</style>