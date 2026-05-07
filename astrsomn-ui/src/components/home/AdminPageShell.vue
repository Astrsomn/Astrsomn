<template>
  <div class="page-content" :class="{ 'page-content--in-module': inModuleLayout }">
    <div v-if="breadcrumbs && breadcrumbs.length > 0" class="page-breadcrumb">
      <a-breadcrumb>
        <a-breadcrumb-item v-for="(crumb, index) in breadcrumbs" :key="index">
          <a v-if="crumb.href" :href="crumb.href">{{ crumb.title }}</a>
          <span v-else style="color: gray; font-weight: 600;">{{ crumb.title }}</span>
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>
    <div v-else-if="title || description" class="page-header">
      <h1 class="page-title">{{ title }}</h1>
      <p class="page-description">{{ description }}</p>
    </div>
    <slot>
      <a-empty :description="emptyText" />
    </slot>
    <div v-if="showViewToggle && viewToggleHandler" class="float-view-toggle">
      <a-button
        type="primary"
        shape="circle"
        size="large"
        @click="viewToggleHandler"
        :title="viewMode === 'grid' ? '切换为列表视图' : '切换为卡片视图'"
      >
  
        <BarsOutlined  v-if="viewMode === 'grid'"/>
        <AppstoreOutlined v-else />
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { inject } from 'vue';
import { AppstoreOutlined, BarsOutlined } from '@ant-design/icons-vue';

interface BreadcrumbItem {
  title: string;
  href?: string;
}

withDefaults(
  defineProps<{
    title?: string;
    description?: string;
    emptyText?: string;
    breadcrumbs?: BreadcrumbItem[];
    showViewToggle?: boolean;
    viewMode?: 'grid' | 'list';
    viewToggleHandler?: () => void;
  }>(),
  {
    emptyText: '暂无数据',
    showViewToggle: false,
    viewMode: 'list',
  }
);

const inModuleLayout = inject('adminInModuleLayout', false);
</script>

<style scoped>
.page-content {
  min-height: calc(100vh - 70px);
  border-radius: var(--radius-sm);
}

.page-content--in-module {
  min-height: auto;
}

.page-breadcrumb {
  padding: 10px 20px 0 20px;
}

.page-header {
  padding: 20px;

}

.page-title {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 200;
  color: var(--text-primary);
}

.page-description {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.float-view-toggle {
  position: fixed;
  right: 32px;
  bottom: 32px;
  z-index: 100;
  border-radius: 50%;
  border: 1px solid var(--border-default, rgba(0, 0, 0, 0.1));
}

.float-view-toggle :deep(.ant-btn) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
