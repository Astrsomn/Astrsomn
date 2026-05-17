<template>
  <div :class="{ collapsed }" :style="{ width: currentWidth + 'px' }" class="ast-sidebar">
    <div class="ast-sidebar-top">
      <slot name="top"/>
    </div>
    <div class="ast-sidebar-body">
      <slot/>
    </div>
    <div class="ast-sidebar-footer">
      <slot name="footer"/>
    </div>
    <a-tooltip :placement="collapsed ? 'right' : 'bottom'">
      <template #title>{{ collapsed ? '展开侧边栏' : '收起侧边栏' }}</template>
      <div class="ast-sidebar-collapse-toggle" @click="emit('toggle-collapse')">
        <RightOutlined v-if="collapsed"/>
        <LeftOutlined v-else/>
      </div>
    </a-tooltip>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {LeftOutlined, RightOutlined} from '@ant-design/icons-vue'

const props = withDefaults(defineProps<{
  collapsed?: boolean
  width?: number
  collapsedWidth?: number
}>(), {
  collapsed: false,
  width: 320,
  collapsedWidth: 64,
})

const emit = defineEmits<{
  'toggle-collapse': []
}>()

const currentWidth = computed(() => props.collapsed ? props.collapsedWidth : props.width)
</script>

<style scoped>
.ast-sidebar {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
  border-right: 1px solid #e5e6eb47;
  position: relative;
}

.ast-sidebar-top {
  flex-shrink: 0;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;

}

.ast-sidebar.collapsed .ast-sidebar-top {
  flex-direction: column;
  padding: 14px 8px;
}

.ast-sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px;
}

.ast-sidebar.collapsed .ast-sidebar-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ast-sidebar-footer {
  flex-shrink: 0;
  padding: 12px 14px;

}

.ast-sidebar.collapsed .ast-sidebar-footer {
  display: flex;
  justify-content: center;
  padding: 12px 8px;
}

/* 收起/展开按钮 - 悬浮在右边框中间 */
.ast-sidebar-collapse-toggle {
  position: absolute;
  right: -14px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-default);
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.ast-sidebar-collapse-toggle:hover {
  color: var(--primary);
  border-color: var(--primary);
  background: var(--primary-hover);
}
</style>
