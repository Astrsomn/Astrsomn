<template>
  <a-tooltip :title="collapsed ? '展开侧栏' : '收起侧栏'">
    <a-button class="canvas-left-toggle" shape="circle" @click="$emit('toggle')">
      <span :class="{ collapsed }" class="toggle-icon">
        <MenuUnfoldOutlined v-if="collapsed"/>
        <MenuFoldOutlined v-else/>
      </span>
    </a-button>
  </a-tooltip>
</template>

<script lang="ts" setup>
import {MenuFoldOutlined, MenuUnfoldOutlined} from '@ant-design/icons-vue'

defineProps<{
  collapsed: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<style scoped>
.canvas-left-toggle {
  position: absolute;
  bottom: 14px;
  left: 14px;
  z-index: 12;
  width: 36px;
  height: 36px;
  border-color: #d7dee8;
  color: #8a94a6;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 6px 14px rgba(15, 23, 42, 0.1);
  transition: border-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.canvas-left-toggle:hover {
  border-color: #c5cedb;
  color: #64748b;
  transform: translateY(-1px);
}

.toggle-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.24s ease, opacity 0.24s ease;
}

.toggle-icon.collapsed {
  transform: rotate(180deg) scale(1.06);
}
</style>
