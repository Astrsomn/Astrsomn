<template>


    <a-layout>
      <!-- 侧边栏 -->
      <a-layout-sider width="256" class="bg-white border-r">
        <Left />
      </a-layout-sider>

      <!-- 主内容区 -->
      <a-layout-content class="bg-gray-50">
        <RightTop />
        <RightCenter />

      </a-layout-content>
    </a-layout>

</template>

<script lang="ts" setup>

import { Layout, Badge } from 'ant-design-vue';
import Left from '@/views/admin/ai-vector/vector-center/component/Left.vue';
import RightTop from '@/views/admin/ai-vector/vector-center/component/RightTop.vue';
import RightCenter from '@/views/admin/ai-vector/vector-center/component/RightCenter.vue';
import RightBottom from '@/views/admin/ai-vector/vector-center/component/RightBottom.vue';

const { Header, Sider, Content } = Layout;
</script>

<style scoped>
/* 整体布局样式 */
.ant-layout {
  background-color: #ffffff;
}

/* 主内容区样式 */
.ant-layout-content {
  padding: 0;
  overflow-y: auto;
  background-color: #fff;
}

/* 侧边栏样式 */
.ant-layout-sider {
  background-color: #ffffff;
}
</style>
