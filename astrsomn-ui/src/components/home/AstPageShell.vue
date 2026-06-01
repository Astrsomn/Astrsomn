<template>
  <div :class="{ 'page-content--in-module': inModuleLayout }" class="page-content">
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
      <a-empty :description="effectiveEmptyText"/>
    </slot>
    <div v-if="showViewToggle && viewToggleHandler" class="float-view-toggle">
      <a-button
          :title="viewMode === 'grid' ? t.pageShell.switchToList : t.pageShell.switchToCard"
          shape="circle"
          size="large"
          type="primary"
          @click="viewToggleHandler"
      >

        <BarsOutlined v-if="viewMode === 'grid'"/>
        <AppstoreOutlined v-else/>
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, inject} from 'vue';
import {AppstoreOutlined, BarsOutlined} from '@ant-design/icons-vue';
import {usePageTranslation} from '@/locales/pages.ts';

const t = usePageTranslation('common')

interface BreadcrumbItem {
  title: string;
  href?: string;
}

const props = withDefaults(
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
      emptyText: '',
      showViewToggle: false,
      viewMode: 'list',
    }
);

const effectiveEmptyText = computed(() => props.emptyText || t.value.pageShell.emptyText)

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
  border: 1px solid var(--border-default);
}

.float-view-toggle :deep(.ant-btn) {
  box-shadow: 0 4px 12px color-mix(in srgb, var(--shadow-color, #000) 15%, transparent);
}
</style>
