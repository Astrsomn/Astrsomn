<template>
  <div class="dashboard-header-mini">
    <div class="mini-flex-container">

      <!-- 1. 导航按钮：前进/后退 -->
      <div class="nav-buttons">
        <a-tooltip :title="t.vectorCenter.rightCenter.back">
          <LeftOutlined class="nav-btn" :class="{ disabled: navIndex <= 0 }" @click="goBack"/>
        </a-tooltip>
        <a-tooltip :title="t.vectorCenter.rightCenter.forward">
          <RightOutlined class="nav-btn" :class="{ disabled: navIndex >= navHistory.length - 1 }" @click="goForward"/>
        </a-tooltip>
      </div>

      <div class="v-sep"></div>

      <!-- 2. 数据源胶囊 -->
      <div class="source-capsule" v-if="source">
        <div class="capsule-icon">
          <img v-if="source?.providerAvatar" :src="source.providerAvatar" />
          <ClusterOutlined v-else />
        </div>
        <span class="capsule-type">{{ source?.extensionCode || 'DB' }}</span>
        <span class="capsule-host">{{ source?.host }}{{ source.port ? ':' + source.port : '' }}</span>
      </div>

      <div class="v-sep"></div>

      <!-- 3. 面包屑导航 -->
      <div class="breadcrumb-section">
        <div class="breadcrumb-nav">
          <span class="breadcrumb-item" @click="navigateToRoot">{{ t.vectorCenter.rightCenter.rootDir }}</span>
          <template v-for="(crumb, idx) in folderPath" :key="crumb.id">
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-item" @click="navigateToPath(idx)">{{ crumb.name }}</span>
          </template>
        </div>
        <div class="count-badge">{{ t.vectorCenter.rightCenter.docCount.replace('{count}', String(docCount ?? 0)) }}</div>
      </div>

      <!-- 4. 搜索框 -->
      <AstSearchInput
          v-model="keyword"
          class="subtle-search"
          layout="fluid"
          :placeholder="t.vectorCenter.rightCenter.searchPlaceholder"
      />

      <!-- 5. 视图切换 -->
      <div class="view-size-switcher">
        <a-dropdown :trigger="['click']">
          <a-button class="view-size-btn" size="small" type="text">
            <template #icon>
              <unordered-list-outlined v-if="viewSize === 'list'"/>
              <appstore-outlined v-else/>
            </template>
          </a-button>
          <template #overlay>
            <a-menu @click="onViewSizeMenuClick">
              <a-menu-item key="small">
                <span :class="{ 'view-size-active': viewSize === 'small' }">{{ t.vectorCenter.rightCenter.smallIcon }}</span>
              </a-menu-item>
              <a-menu-item key="medium">
                <span :class="{ 'view-size-active': viewSize === 'medium' }">{{ t.vectorCenter.rightCenter.mediumIcon }}</span>
              </a-menu-item>
              <a-menu-item key="large">
                <span :class="{ 'view-size-active': viewSize === 'large' }">{{ t.vectorCenter.rightCenter.largeIcon }}</span>
              </a-menu-item>
              <a-menu-divider/>
              <a-menu-item key="list">
                <span :class="{ 'view-size-active': viewSize === 'list' }">{{ t.vectorCenter.rightCenter.listView }}</span>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>

      <!-- 6. 操作按钮 -->
      <div class="action-btns">
        <a-button class="action-btn" @click="$emit('openCreateFolder')">
          <template #icon><FolderOutlined/></template>
          {{ t.vectorCenter.rightCenter.newFolder }}
        </a-button>
        <a-upload :custom-request="handleUpload" :show-upload-list="false">
          <a-button class="action-btn action-btn--primary" type="primary">
            <template #icon><PlusOutlined/></template>
            {{ t.vectorCenter.rightCenter.importDoc }}
          </a-button>
        </a-upload>
      </div>

    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, nextTick, reactive, ref, watch} from 'vue';
import type {UploadProps} from 'ant-design-vue';
import {message} from 'ant-design-vue';
import {
  AppstoreOutlined,
  ClusterOutlined,
  FolderOutlined,
  LeftOutlined,
  PlusOutlined,
  RightOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons-vue';
import {aiVecStoreApi} from '@/api/aiVecStore.ts';
import {usePageTranslation} from '@/locales/pages.ts';
import AstSearchInput from '@/components/home/AstSearchInput.vue';
import {aiVecDocApi} from '@/api/aiVecDoc.ts';

const t = usePageTranslation('ai-vector');

const props = defineProps<{
  store?: any;
  source?: any;
  storeId?: number | string;
  docCount?: number;
  folderPath?: Array<{ id: number | string; name: string }>;
  searchKeyword?: string;
  viewSize?: 'small' | 'medium' | 'large' | 'list';
  currentFolderId?: number | string | null;
}>();
const emit = defineEmits(['updated', 'openCreateFolder', 'openCreate', 'search', 'viewSizeChange', 'navigate']);

const viewSize = ref<'small' | 'medium' | 'large' | 'list'>(props.viewSize || 'large');

const libraryName = ref('');
const description = ref('');
const selectedModel = ref('');
const distanceMetric = ref('');
const dimension = ref<number | undefined>(undefined);
const chunkStrategy = ref('');
const chunkSize = ref(800);
const chunkOverlap = ref(100);

const keyword = ref('');

const navHistory = ref<Array<{ folderId: number | string | null; path: Array<{ id: number | string; name: string }> }>>([{ folderId: null, path: [] }]);
const navIndex = ref(0);
const folderPath = ref<Array<{ id: number | string; name: string }>>([]);
let isInternalNavigation = false;

const stats = reactive({
  docCount: 0,
  segmentCount: 0,
  totalWordCount: 0,
  physicalVectorCount: -1,
  collectionExists: true as boolean | undefined,
});

const distanceMetricLabel = computed(() => {
  const map: Record<string, string> = {
    cosine: t.value.vectorCenter.rightTop.distanceMetric.cosine,
    euclidean: t.value.vectorCenter.rightTop.distanceMetric.euclidean,
    dot: t.value.vectorCenter.rightTop.distanceMetric.dot
  };
  return map[distanceMetric.value] || distanceMetric.value || '-';
});

const chunkStrategyLabel = computed(() => {
  const map: Record<string, string> = {
    RECURSIVE: t.value.vectorCenter.rightTop.chunkStrategy.RECURSIVE,
    FIXED_SIZE: t.value.vectorCenter.rightTop.chunkStrategy.FIXED_SIZE,
    PARAGRAPH: t.value.vectorCenter.rightTop.chunkStrategy.PARAGRAPH,
    SENTENCE: t.value.vectorCenter.rightTop.chunkStrategy.SENTENCE
  };
  return map[chunkStrategy.value] || chunkStrategy.value || t.value.vectorCenter.rightTop.chunkStrategy.default;
});

watch(() => props.store, async (store) => {
  if (!store) return;
  libraryName.value = store?.collectionName || '';
  description.value = store?.metadataSchema || '';
  selectedModel.value = store?.modelKey || '';
  distanceMetric.value = store?.distanceMetric || '';
  dimension.value = store?.dimension;
  chunkStrategy.value = store?.chunkStrategy || 'RECURSIVE';
  chunkSize.value = store?.chunkSize ?? 800;
  chunkOverlap.value = store?.chunkOverlap ?? 100;
  if (store?.id) {
    const resp = await aiVecStoreApi.stats(store.id);
    Object.assign(stats, resp);
  }
}, { immediate: true });

watch(keyword, (val) => {
  emit('search', val);
});

// Sync viewSize prop from Main
watch(() => props.viewSize, (size) => {
  if (size) viewSize.value = size;
});

const onViewSizeMenuClick = (payload: unknown) => {
  const key = String((payload as { key?: string | number })?.key ?? '') as 'small' | 'medium' | 'large' | 'list';
  if (['small', 'medium', 'large', 'list'].includes(key)) {
    viewSize.value = key;
    emit('viewSizeChange', key);
  }
};

// Sync external folderPath changes (from RightCenter) into local navHistory
watch(() => props.folderPath, (newPath) => {
  if (isInternalNavigation) return;
  if (!newPath) return;
  folderPath.value = [...newPath];
  pushNavHistory();
}, { deep: true });

// Sync external searchKeyword changes (from Main on store reset) into local keyword
watch(() => props.searchKeyword, (val) => {
  const newVal = val ?? '';
  if (newVal !== keyword.value) {
    keyword.value = newVal;
  }
});

// Reset local navigation + search state when store changes
watch(() => props.storeId, () => {
  isInternalNavigation = true;
  navHistory.value = [{ folderId: null, path: [] }];
  navIndex.value = 0;
  folderPath.value = [];
  keyword.value = '';
  nextTick(() => { isInternalNavigation = false });
});

const pushNavHistory = () => {
  navHistory.value = navHistory.value.slice(0, navIndex.value + 1);
  const deepestId = folderPath.value.length > 0
    ? folderPath.value[folderPath.value.length - 1].id
    : null;
  navHistory.value.push({folderId: deepestId, path: [...folderPath.value]});
  navIndex.value = navHistory.value.length - 1;
};

const navigateToRoot = () => {
  isInternalNavigation = true;
  folderPath.value = [];
  pushNavHistory();
  emit('navigate', null, []);
  nextTick(() => { isInternalNavigation = false });
};

const navigateToPath = (index: number) => {
  if (index < 0) {
    navigateToRoot();
    return;
  }
  isInternalNavigation = true;
  const target = folderPath.value[index];
  folderPath.value = folderPath.value.slice(0, index + 1);
  pushNavHistory();
  emit('navigate', target.id, [...folderPath.value]);
  nextTick(() => { isInternalNavigation = false });
};

const goBack = () => {
  if (navIndex.value <= 0) return;
  isInternalNavigation = true;
  navIndex.value--;
  const entry = navHistory.value[navIndex.value];
  folderPath.value = [...entry.path];
  emit('navigate', entry.folderId, [...entry.path]);
  nextTick(() => { isInternalNavigation = false });
};

const goForward = () => {
  if (navIndex.value >= navHistory.value.length - 1) return;
  isInternalNavigation = true;
  navIndex.value++;
  const entry = navHistory.value[navIndex.value];
  folderPath.value = [...entry.path];
  emit('navigate', entry.folderId, [...entry.path]);
  nextTick(() => { isInternalNavigation = false });
};

const handleUpload: UploadProps['customRequest'] = async (options) => {
  if (!props.storeId) {
    message.warning(t.value.vectorCenter.rightCenter.selectStoreFirst);
    options.onError?.(new Error('missing store'));
    return;
  }
  try {
    const doc = await aiVecDocApi.upload(options.file as File, props.storeId, props.currentFolderId);
    options.onSuccess?.({});
    message.success(t.value.vectorCenter.rightCenter.uploadSuccess);
    if (doc?.renamed && doc?.originalFileName) {
      message.info(t.value.vectorCenter.rightCenter.fileNameAutoRenamed.replace('{name}', doc.originalFileName));
    }
    if (doc?.id) {
      emit('updated');
    }
  } catch (error) {
    const err = error as { message?: string };
    message.error(err?.message || t.value.vectorCenter.rightCenter.uploadFailed);
    options.onError?.(error as Error);
  }
};
</script>

<style lang="less" scoped>
.dashboard-header-mini {
  height: 64px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.mini-flex-container {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.v-sep {
  width: 1px;
  height: 24px;
  background: var(--border-default);
  flex-shrink: 0;
}

.nav-buttons {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;

  .nav-btn {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: var(--radius-sm);
    cursor: pointer;
    color: var(--text-secondary);
    transition: all 0.2s;

    &:hover:not(.disabled) {
      background: var(--bg-input);
      color: var(--primary);
    }

    &.disabled {
      opacity: 0.3;
      cursor: not-allowed;
    }
  }
}

.source-capsule {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--bg-input);
  border: 1px solid var(--border-default);
  border-radius: 20px;
  padding: 4px 12px 4px 4px;
  font-size: 12px;
  flex-shrink: 0;
  max-width: 220px;
  cursor: default;

  .capsule-icon {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: var(--bg-primary-light);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    img {
      width: 14px;
      height: 14px;
      border-radius: 50%;
    }

    :deep(.anticon) {
      font-size: 13px;
      color: var(--primary);
    }
  }

  .capsule-type {
    font-weight: 700;
    color: var(--primary);
    text-transform: uppercase;
    font-size: 10px;
    letter-spacing: 0.3px;
  }

  .capsule-host {
    color: var(--text-muted);
    font-family: monospace;
    font-size: 11px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.breadcrumb-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;

  .breadcrumb-nav {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    flex: 1;
    overflow: hidden;
    min-width: 0;

    .breadcrumb-item {
      color: var(--text-secondary);
      cursor: pointer;
      transition: color 0.2s;
      white-space: nowrap;

      &:hover {
        color: var(--primary);
      }

      &:last-child {
        color: var(--text-heading);
        font-weight: 600;
      }
    }

    .breadcrumb-sep {
      color: var(--text-muted);
      font-size: 12px;
      flex-shrink: 0;
    }
  }

  .count-badge {
    background: var(--bg-input);
    color: var(--text-secondary);
    padding: 2px 10px;
    border-radius: var(--radius-max);
    font-size: 12px;
    font-weight: 600;
    white-space: nowrap;
    flex-shrink: 0;
  }
}

.subtle-search {
  width: 180px;
  height: 34px;
  flex-shrink: 0;
}

.view-size-switcher {
  flex-shrink: 0;

  .view-size-btn {
    width: 34px;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: var(--radius-md);
    color: var(--text-secondary);
    font-size: 16px;
    transition: all 0.2s;

    &:hover {
      background: var(--bg-input);
      color: var(--primary);
    }
  }

  .view-size-active {
    color: var(--primary);
    font-weight: 600;
  }
}

.action-btns {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;

  .action-btn {
    border-radius: var(--radius-md);
    font-weight: 500;
    height: 34px;
    font-size: 12px;

    &--primary {
      box-shadow: none;
    }
  }
}

@media (max-width: 1400px) {
  .source-capsule {
    .capsule-host {
      display: none;
    }
  }
}

@media (max-width: 1200px) {
  .breadcrumb-section {
    display: none;
  }
  .subtle-search {
    width: 140px;
  }
}

@media (max-width: 900px) {
  .action-btns .action-btn:not(.action-btn--primary) {
    display: none;
  }
}
</style>
