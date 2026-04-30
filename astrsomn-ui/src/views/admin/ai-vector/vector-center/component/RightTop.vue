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
                上次同步: 2分钟前 • <span class="node-text">Cluster-AWS-01</span>
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
                <span class="footer-item">~ 2.4M Tokens</span>
                <span class="footer-dot"></span>
                <span class="footer-item">85.2 MB 占用</span>
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
import { computed, ref, watch } from 'vue';
import { message } from 'ant-design-vue'
import { BarChartOutlined, SyncOutlined } from '@ant-design/icons-vue';
import type { AiVecStore } from '@/api/aiVecStore.ts'
import type { AiVecSource } from '@/api/aiVecSource.ts'
import { aiVecStoreApi } from '@/api/aiVecStore.ts'

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

const modelOptions = [
  { value: 'text-embedding-3-large', label: 'text-embedding-3-large (1536维)' },
  { value: 'text-embedding-3-small', label: 'text-embedding-3-small (1536维)' },
  { value: 'text-embedding-ada-002', label: 'text-embedding-ada-002 (1538维)' },
  { value: 'm3e-base', label: 'm3e-base (768维)' },
  { value: 'm3e-large', label: 'm3e-large (1024维)' }
];

watch(
  () => props.store,
  (store) => {
    libraryName.value = store?.collectionName || ''
    description.value = store?.metadataSchema || ''
    selectedModel.value = store?.instanceKey || ''
  },
  { immediate: true }
)

const vectorCount = computed(() => Number(props.store?.dimension || 0) * 8)

const handleSave = async () => {
  if (!props.store?.id) {
    message.warning('请先在左侧选择数据库')
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
    emit('updated')
  } catch (error) {
    const err = error as { message?: string }
    message.error(err?.message || '保存失败')
  }
}
</script>

<style lang="less" scoped>
@text-main: #262626;
@text-secondary: #8c8c8c;
@border-color: #f0f0f0;
@blue-base: #1677ff;

.dashboard-header-container {
  padding: 20px;
  background-color: #ffffff;
}

/* 基础卡片美化 */
.ant-card {
  border-radius: 12px;
  height: 100%;
  transition: all 0.3s;
}

/* 信息卡片样式 */
.info-card {
  background: #ffffff;
  border: 1px solid @border-color !important;

  .content-wrapper {
    display: flex;
    align-items: center;
    height: 100px;
  }

  .brand-section {
    padding-right: 40px;
    border-right: 1px solid @border-color;
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
        color: #1677ff;
        text-transform: uppercase;
        letter-spacing: 1px;
      }

      .subtle-tag {
        border: none;
        background: #f6ffed;
        color: #52c41a;
        font-size: 10px;
        line-height: 18px;
        border-radius: 4px;
      }
    }

    .main-title-input {
      font-size: 20px;
      font-weight: 700;
      border: 1px solid transparent;
      padding: 4px 8px;
      background: transparent;
      color: @text-main;
      margin-bottom: 6px;
      border-radius: 6px;
      transition: all 0.2s ease;

      &:hover {
        border-color: @border-color;
        background: #fafafa;
      }

      &:focus {
        border-color: @blue-base;
        background: #fff;
        box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
      }
    }

    .description-input {
      font-size: 12px;
      color: @text-secondary;
      border: 1px solid transparent;
      padding: 4px 8px;
      background: transparent;
      margin-bottom: 8px;
      border-radius: 6px;
      transition: all 0.2s ease;
      resize: none;

      &:hover {
        border-color: @border-color;
        background: #fafafa;
      }

      &:focus {
        border-color: @blue-base;
        background: #fff;
        box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
        color: @text-main;
      }
    }

    .sync-meta {
      font-size: 12px;
      color: @text-secondary;
      margin-top: 4px;
      .node-text { color: #bfbfbf; font-family: monospace; }
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
      background: #f5f5f5;
      align-self: center;
    }

    .config-item {
      text-align: center;

      .config-label {
        font-size: 11px;
        font-weight: 600;
        color: @text-secondary;
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
          color: @text-main;
          padding: 0 !important;
          height: auto !important;

          .ant-select-selection-item {
            padding-right: 16px;
          }
        }

        :deep(.ant-select-arrow) {
          color: @text-secondary;
        }
      }

      .config-value {
        font-size: 14px;
        font-weight: 700;
        color: @text-main;
      }

      .config-sub {
        font-size: 11px;
        color: @text-secondary;
        margin-top: 2px;
      }
    }
  }
}

/* 指标卡片样式（深色渐变） */
.stats-card {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);

  .stats-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100px;
    color: #fff;
  }

  .stats-label {
    font-size: 10px;
    font-weight: 600;
    color: #94a3b8;
    letter-spacing: 1px;
  }

  .main-number {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-top: 4px;

    .unit {
      font-size: 12px;
      color: #64748b;
    }
  }

  .stats-footer {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 12px;
    font-size: 11px;
    font-weight: 600;
    color: #cbd5e1;

    .footer-dot {
      width: 3px;
      height: 3px;
      background: #475569;
      border-radius: 50%;
    }
  }

  .stats-icon-box {
    background: rgba(255, 255, 255, 0.1);
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    font-size: 22px;
    color: #38bdf8;
    backdrop-filter: blur(4px);
  }
}

/* 响应式微调 */
@media (max-width: 1200px) {
  .brand-section { padding-right: 20px !important; }
  .main-title { font-size: 18px !important; }
}
</style>