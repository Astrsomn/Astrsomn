<template>
  <div class="dashboard-header-mini">
    <div class="mini-flex-container">
      
      <div class="section main-info">
        <div class="avatar-mini">
          <img v-if="source?.providerAvatar" :src="source.providerAvatar" />
          <ClusterOutlined v-else />
        </div>
        <div class="title-group">
          <div class="top-meta">
            <span class="type-tag">{{ source?.extensionCode || 'DB' }}</span>
            <span class="host-text">{{ source?.host }}{{ source.port ? ':' + source.port : '' }}</span>
          </div>
          <a-input 
            v-model:value="libraryName" 
            :placeholder="t.vectorCenter.rightTop.libraryNamePlaceholder"
            class="ultra-minimal-input name-input"
          />
        </div>
      </div>

      <div class="v-sep"></div>

      <div class="section desc-section">
        <a-input 
          v-model:value="description" 
          :placeholder="t.vectorCenter.rightTop.descriptionPlaceholder" 
          class="ultra-minimal-input desc-input"
        />
      </div>

      <div class="v-sep"></div>

      <div class="section specs-inline">
        <a-tooltip :title="t.vectorCenter.rightTop.specTooltip">
          <div class="spec-pill">
            <deployment-unit-outlined />
            <span class="val">{{ selectedModel || '-' }}</span>
            <span class="dot">·</span>
            <span class="val">{{ distanceMetricLabel }}</span>
            <span class="dot">·</span>
            <span class="val">{{ dimension ?? '-' }}D</span>
          </div>
        </a-tooltip>
        <a-tooltip :title="t.vectorCenter.rightTop.chunkTooltip.replace('{strategy}', chunkStrategyLabel).replace('{size}', String(chunkSize)).replace('{overlap}', String(chunkOverlap))">
          <div class="spec-pill">
            <scissor-outlined />
            <span class="val">{{ chunkStrategyLabel }}</span>
            <span class="dot">·</span>
            <span class="val">{{ chunkSize }}{{ t.vectorCenter.rightTop.chars }}</span>
            <span class="dot">·</span>
            <span class="val">{{ t.vectorCenter.rightTop.overlap }}{{ chunkOverlap }}</span>
          </div>
        </a-tooltip>
        <div class="stat-pill">
          <database-outlined />
          <span class="num">{{ vectorCount }}</span>
          <span class="unit">Chunks</span>
        </div>
      </div>

      <div class="section actions">
        <a-tag :color="stats.collectionExists === false ? 'error' : 'success'" class="mini-status">
          {{ stats.collectionExists === false ? t.vectorCenter.rightTop.collectionNotExists : t.vectorCenter.rightTop.running }}
        </a-tag>
        <a-button type="primary" size="small" @click="handleSave" class="mini-save-btn">
          {{ t.vectorCenter.rightTop.save }}
        </a-button>
      </div>

    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, reactive, ref, watch} from 'vue';
import {message} from 'ant-design-vue';
import {ClusterOutlined, DatabaseOutlined, DeploymentUnitOutlined, ScissorOutlined} from '@ant-design/icons-vue';
import {aiVecStoreApi} from '@/api/aiVecStore.ts';
import {usePageTranslation} from '@/locales/pages.ts';

const t = usePageTranslation('ai-vector');

const props = defineProps<{ store?: any; source?: any; }>();
const emit = defineEmits(['updated']);

const libraryName = ref('');
const description = ref('');
const selectedModel = ref('');
const distanceMetric = ref('');
const dimension = ref<number | undefined>(undefined);
const chunkStrategy = ref('');
const chunkSize = ref(800);
const chunkOverlap = ref(100);

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

const vectorCount = computed(() => stats.segmentCount);

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

const handleSave = async () => {
  try {
    await aiVecStoreApi.update({ ...props.store, collectionName: libraryName.value, metadataSchema: description.value });
    message.success(t.value.vectorCenter.rightTop.saved);
    emit('updated');
  } catch (e: any) { message.error(t.value.vectorCenter.rightTop.saveFailed); }
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
  gap: 16px;
}

.section {
  display: flex;
  align-items: center;
}

.v-sep {
  width: 1px;
  height: 24px;
  background: var(--border-default);
}


.main-info {
  gap: 12px;
  min-width: 200px;
  .avatar-mini {
    width: 32px; height: 32px; background: var(--bg-input); border-radius: 6px;
    display: flex; align-items: center; justify-content: center;
    img { width: 20px; height: 20px; }
    span { font-size: 16px; color: var(--text-muted); }
  }
  .title-group {
    display: flex; flex-direction: column;
    .top-meta {
      display: flex; gap: 6px; align-items: center; line-height: 1; margin-bottom: 2px;
      .type-tag { font-size: 9px; font-weight: 700; color: var(--primary); background: var(--primary-hover); padding: 0 4px; border-radius: 2px; }
      .host-text { font-size: 10px; color: var(--text-muted); font-family: monospace; }
    }
  }
}


.ultra-minimal-input {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  padding: 0 !important;
  &:hover, &:focus { background: var(--bg-input) !important; border-radius: 4px; padding: 0 4px !important; }
}

.name-input { font-size: 14px; font-weight: 600; color: var(--text-heading); width: 140px; }
.desc-input { font-size: 13px; color: var(--text-secondary); width: 100%; }

.desc-section { flex: 1; }


.specs-inline {
  gap: 8px;
  .spec-pill, .stat-pill {
    display: flex; align-items: center; gap: 6px;
    background: var(--bg-input); padding: 4px 10px; border-radius: 14px;
    font-size: 12px; color: var(--text-secondary); border: 1px solid var(--border-default);
    white-space: nowrap;
    .dot { color: var(--text-muted); }
    .val { font-weight: 500; }
    .num { font-weight: 700; color: var(--primary); }
    .unit { font-size: 10px; color: var(--text-muted); }
  }
}


.actions {
  gap: 12px;
  .mini-status { margin: 0; font-size: 10px; border: none; background: color-mix(in srgb, var(--success) 15%, transparent); color: var(--success); }
  .mini-save-btn { border-radius: 4px; height: 24px; font-size: 12px; padding: 0 8px; }
}

@media (max-width: 1200px) {
  .specs-inline { display: none; }
}
</style>