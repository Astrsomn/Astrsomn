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
            placeholder="知识库名称" 
            class="ultra-minimal-input name-input"
          />
        </div>
      </div>

      <div class="v-sep"></div>

      <div class="section desc-section">
        <a-input 
          v-model:value="description" 
          placeholder="点击添加描述信息..." 
          class="ultra-minimal-input desc-input"
        />
      </div>

      <div class="v-sep"></div>

      <div class="section specs-inline">
        <a-tooltip title="Embedding 模型 / 距离策略 / 维度">
          <div class="spec-pill">
            <deployment-unit-outlined />
            <span class="val">{{ selectedModel || '-' }}</span>
            <span class="dot">·</span>
            <span class="val">{{ distanceMetricLabel }}</span>
            <span class="dot">·</span>
            <span class="val">{{ dimension ?? '-' }}D</span>
          </div>
        </a-tooltip>
        <a-tooltip :title="`切片策略: ${chunkStrategyLabel} / 大小: ${chunkSize} / 重叠: ${chunkOverlap}`">
          <div class="spec-pill">
            <scissor-outlined />
            <span class="val">{{ chunkStrategyLabel }}</span>
            <span class="dot">·</span>
            <span class="val">{{ chunkSize }}字</span>
            <span class="dot">·</span>
            <span class="val">重叠{{ chunkOverlap }}</span>
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
          {{ stats.collectionExists === false ? '集合不存在' : '运行中' }}
        </a-tag>
        <a-button type="primary" size="small" @click="handleSave" class="mini-save-btn">
          保存
        </a-button>
      </div>

    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue';
import { message } from 'ant-design-vue';
import {
  ClusterOutlined, DeploymentUnitOutlined,
  DatabaseOutlined, SyncOutlined, ScissorOutlined
} from '@ant-design/icons-vue';
import { aiVecStoreApi } from '@/api/aiVecStore.ts';

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
  const map: Record<string, string> = { cosine: '余弦', euclidean: '欧氏', dot: '点积' };
  return map[distanceMetric.value] || distanceMetric.value || '-';
});

const chunkStrategyLabel = computed(() => {
  const map: Record<string, string> = {
    RECURSIVE: '递归',
    FIXED_SIZE: '固定',
    PARAGRAPH: '段落',
    SENTENCE: '句子'
  };
  return map[chunkStrategy.value] || chunkStrategy.value || '递归';
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
    message.success('已保存');
    emit('updated');
  } catch (e: any) { message.error('失败'); }
};
</script>

<style lang="less" scoped>
.dashboard-header-mini {
  height: 64px; /* 严格控制在 60-70px 之间 */
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
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
  background: #f0f0f0;
}

/* 左侧标题组 */
.main-info {
  gap: 12px;
  min-width: 200px;
  .avatar-mini {
    width: 32px; height: 32px; background: #f5f5f5; border-radius: 6px;
    display: flex; align-items: center; justify-content: center;
    img { width: 20px; height: 20px; }
    span { font-size: 16px; color: #bfbfbf; }
  }
  .title-group {
    display: flex; flex-direction: column;
    .top-meta {
      display: flex; gap: 6px; align-items: center; line-height: 1; margin-bottom: 2px;
      .type-tag { font-size: 9px; font-weight: 700; color: #1890ff; background: #e6f7ff; padding: 0 4px; border-radius: 2px; }
      .host-text { font-size: 10px; color: #bfbfbf; font-family: monospace; }
    }
  }
}

/* 输入框统一极简样式 */
.ultra-minimal-input {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  padding: 0 !important;
  &:hover, &:focus { background: #f5f5f5 !important; border-radius: 4px; padding: 0 4px !important; }
}

.name-input { font-size: 14px; font-weight: 600; color: #262626; width: 140px; }
.desc-input { font-size: 13px; color: #8c8c8c; width: 100%; }

.desc-section { flex: 1; }

/* 只读参数 pills */
.specs-inline {
  gap: 8px;
  .spec-pill, .stat-pill {
    display: flex; align-items: center; gap: 6px;
    background: #f8f9fa; padding: 4px 10px; border-radius: 14px;
    font-size: 12px; color: #595959; border: 1px solid #f0f0f0;
    white-space: nowrap;
    .dot { color: #d9d9d9; }
    .val { font-weight: 500; }
    .num { font-weight: 700; color: #1890ff; }
    .unit { font-size: 10px; color: #bfbfbf; }
  }
}

/* 操作区 */
.actions {
  gap: 12px;
  .mini-status { margin: 0; font-size: 10px; border: none; background: #f6ffed; color: #52c41a; }
  .mini-save-btn { border-radius: 4px; height: 24px; font-size: 12px; padding: 0 8px; }
}

@media (max-width: 1200px) {
  .specs-inline { display: none; } /* 屏幕太窄时隐藏参数区 */
}
</style>