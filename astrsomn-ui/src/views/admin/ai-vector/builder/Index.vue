<template>
  <a-layout class="min-h-screen">


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
  </a-layout>
</template>

<script lang="ts" setup>
import { defineComponent } from 'vue';
import { RobotOutlined } from '@ant-design/icons-vue';
import { Layout, Badge } from 'ant-design-vue';
import Left from './component/Left.vue';
import RightTop from './component/RightTop.vue';
import RightCenter from './component/RightCenter.vue';
import RightBottom from './component/RightBottom.vue';

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