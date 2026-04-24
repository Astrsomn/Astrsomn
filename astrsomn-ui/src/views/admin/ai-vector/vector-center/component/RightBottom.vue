<template>
  <a-layout-footer class="bg-white border-t px-6 py-2">
    <div class="flex items-center justify-between text-xs text-gray-500">
      <div class="flex items-center gap-6">
        <div class="flex items-center gap-2">
          <a-badge status="success" size="small" />
          服务节点: AWS-E1-NODE
        </div>
        <span>延迟: 124ms</span>
      </div>
      <span>版本: V2.4 Stable</span>
    </div>
  </a-layout-footer>
</template>

<script lang="ts" setup>
import { defineComponent } from 'vue';
import { Layout, Badge } from 'ant-design-vue';

const { Footer } = Layout;
</script>

<style scoped>
/* 底部状态栏样式 */
.ant-layout-footer {
  padding: 8px 24px;
  height: 40px;
  line-height: 40px;
}
</style>