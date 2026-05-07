<template>
  <div class="dashboard-header-container">
    <a-row :gutter="[16, 16]">
      <a-col :span="16">
        <a-card :bordered="false" class="info-card">
          <div class="content-wrapper">
            <div class="brand-section">
              <div class="status-label">
                <span class="label-text">当前选中资产</span>
                <a-tag color="success" class="subtle-tag">
                  <template #icon><sync-outlined :spin="true" /></template>
                  运行中
                </a-tag>
              </div>
              <a-input
                :value="libraryName"
                @update:value="(v) => (libraryName = v)"
                class="main-title-input"
                placeholder="请输入知识库名称"
              />
              <a-input
                :value="description"
                @update:value="(v) => (description = v)"
                type="textarea"
                class="description-input"
                placeholder="请输入描述信息（可选）"
                :rows="2"
                :auto-size="{ minRows: 2, maxRows: 4 }"
              />
              <div class="sync-meta">
                上次同步: {{ lastSyncText }} • <span class="node-text">{{ sourceNodeText }}</span>
              </div>
            </div>

            <div class="config-section">
              <div class="config-item">
                <span class="config-label">Embedding 模型</span>
                <a-select
                  :value="selectedModel"
                  @update:value="(v) => (selectedModel = v)"
                  class="model-select"
                  :options="modelOptions"
                  :loading="instanceLoading"
                  placeholder="选择模型"
                />
              </div>
              <div class="config-divider"></div>
              <div class="config-item">
                <span class="config-label">检索策略</span>
                <div class="config-value">HNSW / 余弦相似度</div>
                <div class="config-sub">M:16 • ef:200</div>
              </div>
            </div>
            <div class="action-area">
              <a-button type="primary" size="small" @click="handleSave">保存</a-button>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card :bordered="false" class="stats-card">
          <div class="stats-wrapper">
            <div class="stats-content">
              <span class="stats-label">存储统计 / STORAGE</span>
              <div class="main-number">
                <a-statistic
                  :value="vectorCount"
                  :value-style="{ color: '#fff', fontSize: '28px', fontWeight: '700' }"
                />
                <span class="unit">个向量片段</span>
              </div>
              <div class="stats-footer">
                <span class="footer-item">文档 {{ stats.docCount }} 个</span>
                <span class="footer-dot"></span>
                <span class="footer-item">字符 {{ stats.totalWordCount }}</span>
              </div>
            </div>
            <div class="stats-icon-box">
              <bar-chart-outlined />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue';
import { message } from 'ant-design-vue'
import { BarChartOutlined, SyncOutlined } from '@ant-design/icons-vue';
import type { AiVecStore } from '@/api/aiVecStore'
import type { AiVecSource } from '@/api/aiVecSource'
import { aiVecStoreApi } from '@/api/aiVecStore'
import { aiInstanceApi } from '@/api/aiInstance'

const props = defineProps<{
  store?: AiVecStore
  source?: AiVecSource
}>()

const emit = defineEmits<{
  updated: []
}>()

const libraryName = ref('');
const description = ref('');
const selectedModel = ref('');
const instanceLoading = ref(false)
const stats = reactive({
  docCount: 0,
  segmentCount: 0,
  totalWordCount: 0,
  lastSyncTime: ''
})

const modelOptions = ref<Array<{ value: string; label: string }>>([])

watch(
  () => props.store,
  async (store) => {
    libraryName.value = store?.collectionName || ''
    description.value = store?.metadataSchema || ''
    selectedModel.value = store?.instanceKey || ''
    if (store?.id) {
      await Promise.all([fetchStoreStats(store.id), fetchInstanceOptions()])
    } else {
      stats.docCount = 0
      stats.segmentCount = 0
      stats.totalWordCount = 0
      stats.lastSyncTime = ''
      modelOptions.value = []
    }
  },
  { immediate: true }
)

const vectorCount = computed(() => stats.segmentCount)
const lastSyncText = computed(() => stats.lastSyncTime || '暂无')
const sourceNodeText = computed(() => props.source?.name || props.source?.extensionCode || '未命名节点')

const fetchStoreStats = async (id: number | string) => {
  const resp = await aiVecStoreApi.stats(id)
  stats.docCount = Number(resp.docCount || 0)
  stats.segmentCount = Number(resp.segmentCount || 0)
  stats.totalWordCount = Number(resp.totalWordCount || 0)
  stats.lastSyncTime = resp.lastSyncTime || ''
}

const fetchInstanceOptions = async () => {
  instanceLoading.value = true
  try {
    const resp = await aiInstanceApi.queryPage({
      pageNo: 1,
      pageSize: 200,
      param: {}
    })
    const list = resp.list || []
    modelOptions.value = list
      .filter((x) => String(x.modelType || '').toLowerCase().includes('embedding'))
      .map((x) => ({
        value: String(x.instanceKey || ''),
        label: `${x.instanceName || x.instanceKey} (${x.instanceKey})`
      }))
      .filter((x) => x.value)
  } finally {
    instanceLoading.value = false
  }
}

const handleSave = async () => {
  if (!props.store?.id) {
    message.warning('请先在左侧选择数据库')
    return
  }
  if (!selectedModel.value) {
    message.warning('请选择 Embedding 实例')
    return
  }
  try {
    const msg = await aiVecStoreApi.update({
      ...props.store,
      collectionName: libraryName.value,
      metadataSchema: description.value,
      instanceKey: selectedModel.value
    })
    message.success(msg || '保存成功')
    await fetchStoreStats(props.store.id)
    emit('updated')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '保存失败')
  }
}
</script>

<style lang="less" scoped>
.dashboard-header-container {
  padding: 20px;
  background-color: var(--bg-card);
}

/* 基础卡片美化 */
.ant-card {
  border-radius: var(--radius-lg);
  height: 100%;
  transition: all 0.3s;
}

/* 信息卡片样式 */
.info-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default) !important;

  .content-wrapper {
    display: flex;
    align-items: center;
    height: 100px;
  }

  .brand-section {
    padding-right: 40px;
    border-right: 1px solid var(--border-default);
    flex-shrink: 0;
    min-width: 280px;

    .status-label {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      .label-text {
        font-size: 11px;
        font-weight: 700;
        color: var(--primary);
        text-transform: uppercase;
        letter-spacing: 1px;
      }

      .subtle-tag {
        border: none;
        background: rgba(16, 185, 129, 0.1);
        color: var(--success);
        font-size: 10px;
        line-height: 18px;
        border-radius: var(--radius-sm);
      }
    }

    .main-title-input {
      font-size: 20px;
      font-weight: 700;
      border: 1px solid transparent;
      padding: 4px 8px;
      background: transparent;
      color: var(--text-heading);
      margin-bottom: 6px;
      border-radius: var(--radius-md);
      transition: all 0.2s ease;

      &:hover {
        border-color: var(--border-default);
        background: var(--bg-input);
      }

      &:focus {
        border-color: var(--primary);
        background: var(--bg-card);
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
      }
    }

    .description-input {
      font-size: 12px;
      color: var(--text-secondary);
      border: 1px solid transparent;
      padding: 4px 8px;
      background: transparent;
      margin-bottom: 8px;
      border-radius: var(--radius-md);
      transition: all 0.2s ease;
      resize: none;

      &:hover {
        border-color: var(--border-default);
        background: var(--bg-input);
      }

      &:focus {
        border-color: var(--primary);
        background: var(--bg-card);
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
        color: var(--text-primary);
      }
    }

    .sync-meta {
      font-size: 12px;
      color: var(--text-secondary);
      margin-top: 4px;
      .node-text { color: var(--text-muted); font-family: monospace; }
    }
  }

  .config-section {
    flex: 1;
    display: flex;
    justify-content: space-around;
    padding-left: 20px;

    .config-divider {
      width: 1px;
      height: 40px;
      background: var(--border-default);
      align-self: center;
    }

    .config-item {
      text-align: center;

      .config-label {
        font-size: 11px;
        font-weight: 600;
        color: var(--text-secondary);
        display: block;
        margin-bottom: 4px;
      }

      .model-select {
        min-width: 180px;

        :deep(.ant-select-selector) {
          border: none !important;
          background: transparent !important;
          box-shadow: none !important;
          font-weight: 600;
          font-size: 14px;
          color: var(--text-primary);
          padding: 0 !important;
          height: auto !important;

          .ant-select-selection-item {
            padding-right: 16px;
          }
        }

        :deep(.ant-select-arrow) {
          color: var(--text-secondary);
        }
      }

      .config-value {
        font-size: 14px;
        font-weight: 700;
        color: var(--text-heading);
      }

      .config-sub {
        font-size: 11px;
        color: var(--text-secondary);
        margin-top: 2px;
      }
    }
  }
}

/* 指标卡片样式（深色渐变） */
.stats-card {
  background: linear-gradient(135deg, var(--bg-elevated) 0%, var(--bg-card) 100%);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-default);

  .stats-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100px;
    color: var(--text-primary);
  }

  .stats-label {
    font-size: 10px;
    font-weight: 600;
    color: var(--text-muted);
    letter-spacing: 1px;
  }

  .main-number {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-top: 4px;

    .unit {
      font-size: 12px;
      color: var(--text-muted);
    }
  }

  .stats-footer {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 12px;
    font-size: 11px;
    font-weight: 600;
    color: var(--text-secondary);

    .footer-dot {
      width: 3px;
      height: 3px;
      background: var(--text-muted);
      border-radius: 50%;
    }
  }

  .stats-icon-box {
    background: rgba(59, 130, 246, 0.15);
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: var(--radius-lg);
    font-size: 22px;
    color: var(--accent-cyan);
    backdrop-filter: blur(4px);
  }
}

/* 响应式微调 */
@media (max-width: 1200px) {
  .brand-section { padding-right: 20px !important; }
  .main-title { font-size: 18px !important; }
}
</style>