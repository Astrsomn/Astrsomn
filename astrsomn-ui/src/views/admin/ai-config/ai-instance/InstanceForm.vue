<template>
  <a-modal
    :open="visible"
    width="100%"
    :footer="null"
    :closable="false"
    wrap-class-name="astrsomn-full-modal"
    destroy-on-close
    @cancel="handleCancel"
  >
    <div class="fullscreen-wrapper">
      <header class="modal-header">
        <div class="header-left">
          <div class="logo-box"><ThunderboltFilled /></div>
          <div class="title-group">
            <span class="main-title">{{ isEdit ? '编辑推理预设' : '新建推理预设' }}</span>
            <span class="sub-title">配置 Astrsomn 核心引擎的运行策略与端点映射</span>
          </div>
        </div>
        <div class="header-actions">
          <div class="header-action-pair">
            <a-button class="header-action-btn header-action-btn-cancel" @click="handleCancel">取消</a-button>
            <a-button
              type="primary"
              class="header-action-btn header-action-btn-save"
              :loading="submitting"
              @click="onSubmit"
            >
              保存预设
            </a-button>
          </div>
        </div>
      </header>

      <div class="main-content">
        <section class="selection-pane">
          <div class="pane-card glass-card">
            <div class="pane-header">
              <div class="pane-toolbar-row">
                <div class="provider-field">
                  <span class="field-label">模型提供商</span>
                  <ModelProviderSelect
                    :value="providerFilter"
                    class="instance-provider-select"
                    placeholder="全部提供商"
                    size="middle"
                    :allow-clear="true"
                    @update:value="onProviderFilterChange"
                  />
                </div>
                <AstrsomnSearchPill
                  v-model="searchDraft"
                  layout="pane"
                  placeholder="名称、Model Key..."
                  @search="applyModelSearch"
                />
              </div>
              <a-tabs v-model:activeKey="typeFilter" class="model-type-tabs">
                <a-tab-pane key="all" tab="全部类型" />
                <a-tab-pane key="chat" tab="对话" />
                <a-tab-pane key="embedding" tab="向量" />
                <a-tab-pane key="image" tab="图像" />
              </a-tabs>
            </div>

            <div ref="tableWrapRef" class="table-container">
              <a-table
                :columns="columns"
                :data-source="filteredModels"
                :loading="modelsLoading"
                :pagination="{ pageSize: 12, showTotal: (t: number) => `共 ${t} 个可用端点`, showSizeChanger: false }"
                :scroll="{ y: tableScrollY }"
                :row-selection="{ selectedRowKeys: selectedKeys, onChange: onRowSelectChange, type: 'radio' }"
                :custom-row="customRow"
                row-key="modelKey"
                size="middle"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'providerAvatar'">
                    <span
                      v-if="providerAvatarCell(record)"
                      class="inst-provider-avatar-cell"
                      v-html="providerAvatarCell(record)"
                      aria-hidden="true"
                    />
                    <span v-else class="text-muted">—</span>
                  </template>
                  <template v-else-if="column.key === 'modelType'">
                    <div class="inst-model-type-cell">
                      <div class="inst-model-type-icon" :class="record.modelType">
                        <template v-if="record.modelType === 'chat'"><MessageOutlined /></template>
                        <template v-else-if="record.modelType === 'embedding'"><PartitionOutlined /></template>
                        <template v-else-if="record.modelType === 'image'"><PictureOutlined /></template>
                        <template v-else><MessageOutlined /></template>
                      </div>
                      <span class="inst-model-type-label">{{ modelTypeLabel(record.modelType) }}</span>
                    </div>
                  </template>
                  <template v-else-if="column.key === 'provider'">
                    <span class="provider-tag" :data-type="record.provider">{{ record.provider || 'Local' }}</span>
                  </template>
                </template>
              </a-table>
            </div>
          </div>
        </section>

        <aside class="config-pane">
          <div class="pane-card glass-card scroll-y">
            <a-form ref="formRef" layout="vertical" :model="form">
              
              <div class="config-section">
                <h3 class="section-title"><InfoCircleOutlined /> 基础定义</h3>
                <a-form-item label="预设名称" name="instanceName" :rules="[{ required: true, message: '请输入名称' }]">
                  <a-input
                    v-model:value="form.instanceName"
                    placeholder="默认与端点名称一致，可改为任意展示名"
                    size="large"
                    @update:value="onPresetNameUserInput"
                  />
                </a-form-item>

                <a-form-item label="实例标识 (instanceKey)" name="instanceKey" :rules="instanceKeyRules">
                  <a-input
                    v-model:value="form.instanceKey"
                    placeholder="留空可由系统自动分配；自定义时请使用英文标识"
                    size="large"
                    allow-clear
                  />
                </a-form-item>
                
                <a-form-item label="运行状态">
                  <a-segmented v-model:value="form.status" :options="statusOptions" block size="large" />
                </a-form-item>
              </div>

              <a-divider />

              <div class="config-section">
                <div class="section-header-flex">
                  <h3 class="section-title"><ControlOutlined /> {{ paramSectionTitle }}</h3>
                  <a-tag v-if="form.modelKey" color="blue" class="model-key-tag">{{ form.modelKey }}</a-tag>
                </div>

                <div v-if="!form.modelKey" class="empty-state">
                  <div class="empty-icon"><SelectOutlined /></div>
                  <p>请在左侧表格中选中一个接入端点</p>
                </div>

                <div v-else class="params-list">
                  <p v-if="capabilityHint" class="cap-hint">{{ capabilityHint }}</p>

                  <!-- 对话：按 capabilities / InferenceParamEnum 与后端 containedIn 对齐 -->
                  <template v-if="modelKind === 'chat'">
                    <div v-if="showChatTemperature" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left">
                          <template #title>
                            控制生成内容的随机性。较低值使输出更聚焦严谨，较高值使输出更具创意和不可预测。
                          </template>
                          <span class="p-label">采样温度 (Temperature) <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.temperature" :min="0" :max="2" :step="0.1" size="small" />
                      </div>
                      <div class="slider-box">
                        <a-slider
                          v-model:value="form.temperature"
                          :min="0"
                          :max="2"
                          :step="0.1"
                          :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }"
                        />
                      </div>
                      <div class="p-desc-bar" :class="getTempInfo(form.temperature ?? 0.7).color">
                        {{ getTempInfo(form.temperature ?? 0.7).text }}
                      </div>
                    </div>

                    <div v-if="showChatMaxTokens" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left">
                          <template #title>设置生成内容的最大长度限制。1000 tokens 约为 750 个英文单词。</template>
                          <span class="p-label">响应上限 (Max Tokens) <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.maxTokens" :min="1" :max="128000" size="small" />
                      </div>
                      <div class="slider-box">
                        <a-slider
                          v-model:value="form.maxTokens"
                          :min="0"
                          :max="8192"
                          :step="256"
                          :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }"
                        />
                      </div>
                    </div>

                    <div v-if="showChatTopP" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left" title="核心采样。模型仅考虑概率累积达到此比例的候选词。建议不与 Temperature 同时大幅调整。">
                          <span class="p-label">核采样 (Top P) <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.topP" :min="0" :max="1" :step="0.01" size="small" />
                      </div>
                      <div class="slider-box">
                        <a-slider v-model:value="form.topP" :min="0" :max="1" :step="0.05" :marks="{ 0: '极窄', 0.5: '标准', 1: '完整' }" />
                      </div>
                    </div>

                    <div v-if="showChatTopK" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left" title="仅从每步概率最高的 K 个 token 中采样；与部分厂商对话模型对齐。">
                          <span class="p-label">Top K <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.topK" :min="0" :max="100" :step="1" size="small" />
                      </div>
                      <p class="p-inline-hint">0 表示不启用（由服务端/模型默认处理）</p>
                    </div>

                    <div v-if="showChatSeed" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left" title="固定种子可在支持该能力的模型上复现输出。">
                          <span class="p-label">随机种子 (Seed) <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.seed" :min="0" :max="2147483647" :step="1" size="small" />
                      </div>
                    </div>

                    <div v-if="showChatStopSequences" class="param-group-card">
                      <div class="p-header">
                        <span class="p-label">停止序列 (Stop)</span>
                      </div>
                      <a-textarea
                        v-model:value="form.stopSequences"
                        placeholder="多个序列用英文逗号分隔"
                        :rows="3"
                        class="stop-seq-input"
                      />
                    </div>

                    <div v-if="showChatPenalties" class="penalty-row">
                      <div v-if="showChatFrequencyPenalty" class="mini-param-card">
                        <span class="mini-label">重复惩罚 (Frequency)</span>
                        <a-slider v-model:value="form.frequencyPenalty" :min="-2" :max="2" :step="0.1" />
                      </div>
                      <div v-if="showChatPresencePenalty" class="mini-param-card">
                        <span class="mini-label">新鲜度 (Presence)</span>
                        <a-slider v-model:value="form.presencePenalty" :min="-2" :max="2" :step="0.1" />
                      </div>
                    </div>
                  </template>

                  <!-- 向量：EmbeddingInferenceParamEnum -->
                  <template v-else-if="modelKind === 'embedding'">
                    <div v-if="showEmbeddingDimensions" class="param-group-card">
                      <div class="p-header">
                        <a-tooltip placement="left" title="与 OpenAiEmbeddingModel.dimensions() 等对齐；请与模型实际输出维度一致。">
                          <span class="p-label">向量维度 (Dimensions) <QuestionCircleOutlined /></span>
                        </a-tooltip>
                        <a-input-number v-model:value="form.dimensions" :min="1" :max="8192" :step="1" size="small" placeholder="如 1536" />
                      </div>
                    </div>
                    <p v-if="!embeddingHasAnyControl" class="cap-hint muted">
                      当前端点 capabilities 未包含可映射到实例的向量参数；可在模型管理中勾选「向量维度」等能力。
                    </p>
                  </template>

                  <!-- 图像：ImageGenParamEnum -->
                  <template v-else-if="modelKind === 'image'">
                    <div v-if="showImageSize" class="param-group-card">
                      <div class="p-header">
                        <span class="p-label">画幅尺寸 (Size)</span>
                      </div>
                      <a-input v-model:value="form.size" placeholder="例如 1024x1024" size="large" allow-clear />
                    </div>
                    <div v-if="showImageStyle" class="param-group-card">
                      <div class="p-header">
                        <span class="p-label">风格 (Style)</span>
                      </div>
                      <a-input v-model:value="form.style" placeholder="例如 vivid / natural" size="large" allow-clear />
                    </div>
                    <p v-if="!imageHasAnyControl" class="cap-hint muted">
                      当前端点 capabilities 未包含尺寸或风格等图像参数；可在模型管理中勾选对应能力。
                    </p>
                  </template>
                </div>
              </div>
            </a-form>
          </div>
        </aside>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import {
  ThunderboltFilled, InfoCircleOutlined, QuestionCircleOutlined,
  ControlOutlined, SelectOutlined,
  MessageOutlined, PartitionOutlined, PictureOutlined
} from '@ant-design/icons-vue';
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue';
import ModelProviderSelect from '@/views/admin/ai-config/ai-model/ModelProviderSelect.vue';
import { ensureWorkspaceEnvInStorage } from '@/utils/ensureWorkspaceEnvStorage';
import { aiModelApi, type AiModel } from '@/api/aiModel';
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance';
import dayjs from 'dayjs';
import { WORKSPACE_ENV_STORAGE_KEY } from '@/constants/workspaceEnv';

function parseCapabilitiesRaw(raw?: string): string[] {
  if (!raw) return [];
  try {
    const p = JSON.parse(raw);
    return Array.isArray(p) ? p.map(String) : [];
  } catch {
    return [];
  }
}

interface Props { visible: boolean; record?: AiInstance }
const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'success']);

const editId = computed(() => props.record?.id);
const isEdit = computed(() => editId.value != null && String(editId.value) !== '');

const formRef = ref();
const tableWrapRef = ref<HTMLElement | null>(null);
const submitting = ref(false);
const modelsLoading = ref(false);
const modelList = ref<AiModel[]>([]);
const selectedKeys = ref<string[]>([]);
const searchDraft = ref('');
const searchQuery = ref('');
const typeFilter = ref('all');
/** 与模型列表一致：按 AI_MODEL.supplier（扩展 key）筛选 */
const providerFilter = ref<string | undefined>(undefined);

function providerAvatarCell(record: AiModel): string {
  const raw = record.providerAvatar;
  return typeof raw === 'string' && raw.trim() ? raw.trim() : '';
}

function modelTypeLabel(t?: string) {
  if (t === 'embedding') return '向量';
  if (t === 'image') return '图像';
  return '对话';
}

async function onProviderFilterChange(v: string | undefined) {
  providerFilter.value = v;
  if (!props.visible) return;
  await fetchModels();
  await nextTick();
  const mk = form.modelKey;
  if (mk && !modelList.value.some((m) => m.modelKey === mk)) {
    selectedKeys.value = [];
    form.modelKey = undefined;
  }
}

function applyModelSearch() {
  searchQuery.value = String(searchDraft.value ?? '').trim();
}

const tableScrollY = ref(320);

const form = reactive<AiInstance>({ 
  status: 'enabled', 
  temperature: 0.7, 
  maxTokens: 2048,
  topP: 1.0,
  frequencyPenalty: 0,
  presencePenalty: 0
});

const createTimeDisplay = computed(() => {
  if (!isEdit.value) return '保存后生成';
  const t = form.createTime;
  if (!t) return '—';
  const d = dayjs(t);
  return d.isValid() ? d.format('YYYY-MM-DD HH:mm:ss') : String(t);
});

const envDisplay = computed(() => {
  if (isEdit.value) {
    return form.envCode?.trim() || '默认';
  }
  if (typeof localStorage === 'undefined') return '跟随当前工作区';
  const v = localStorage.getItem(WORKSPACE_ENV_STORAGE_KEY);
  return v?.trim() || '跟随当前工作区';
});

/** 新建时跟随所选 modelName；用户改过预设名称后不再自动覆盖 */
const presetNameUserEdited = ref(false);
const syncingPresetNameFromModel = ref(false);

function setPresetNameFromModel(name?: string) {
  syncingPresetNameFromModel.value = true;
  form.instanceName = name ?? '';
  nextTick(() => {
    syncingPresetNameFromModel.value = false;
  });
}

function onPresetNameUserInput() {
  if (syncingPresetNameFromModel.value) return;
  presetNameUserEdited.value = true;
}

function applyModelSelection(record: AiModel) {
  form.modelKey = record.modelKey;
  if (!isEdit.value && !presetNameUserEdited.value) {
    setPresetNameFromModel(record.modelName);
  }
}

const selectedModel = computed(() => modelList.value.find((m) => m.modelKey === form.modelKey));
const selectedCaps = computed(() => parseCapabilitiesRaw(selectedModel.value?.capabilities));

const modelKind = computed<'chat' | 'embedding' | 'image'>(() => {
  const t = selectedModel.value?.modelType;
  if (t === 'embedding' || t === 'image') return t;
  return 'chat';
});

const legacyChatFullPanel = computed(() => modelKind.value === 'chat' && selectedCaps.value.length === 0);
const legacyEmbeddingPanel = computed(() => modelKind.value === 'embedding' && selectedCaps.value.length === 0);
const legacyImagePanel = computed(() => modelKind.value === 'image' && selectedCaps.value.length === 0);

const showChatTemperature = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('temperature') || c.includes('temperature_setting') || c.includes('text_generation');
});

const showChatMaxTokens = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('max_tokens') || c.includes('max_token_setting');
});

const showChatTopP = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('top_p') || c.includes('top_p_setting');
});

const showChatTopK = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('top_k') || c.includes('top_k_setting');
});

const showChatSeed = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('seed') || c.includes('seed_setting');
});

const showChatStopSequences = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('stop_sequences_setting');
});

const showChatFrequencyPenalty = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('frequency_penalty') || c.includes('frequency_penalty_setting');
});

const showChatPresencePenalty = computed(() => {
  if (modelKind.value !== 'chat') return false;
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('presence_penalty') || c.includes('presence_penalty_setting');
});

const showChatPenalties = computed(
  () => showChatFrequencyPenalty.value || showChatPresencePenalty.value
);

const showEmbeddingDimensions = computed(() => {
  if (modelKind.value !== 'embedding') return false;
  const c = selectedCaps.value;
  if (legacyEmbeddingPanel.value) return true;
  return c.includes('embedding_dimensions');
});

const showImageSize = computed(() => {
  if (modelKind.value !== 'image') return false;
  const c = selectedCaps.value;
  if (legacyImagePanel.value) return true;
  return c.includes('image_size') || c.includes('size_setting');
});

const showImageStyle = computed(() => {
  if (modelKind.value !== 'image') return false;
  const c = selectedCaps.value;
  if (legacyImagePanel.value) return true;
  return c.includes('image_style') || c.includes('style_setting');
});

const chatHasAnyControl = computed(
  () =>
    showChatTemperature.value ||
    showChatMaxTokens.value ||
    showChatTopP.value ||
    showChatTopK.value ||
    showChatSeed.value ||
    showChatStopSequences.value ||
    showChatFrequencyPenalty.value ||
    showChatPresencePenalty.value
);

const embeddingHasAnyControl = computed(() => showEmbeddingDimensions.value);
const imageHasAnyControl = computed(() => showImageSize.value || showImageStyle.value);

const paramSectionTitle = computed(() => {
  switch (modelKind.value) {
    case 'embedding':
      return '向量参数';
    case 'image':
      return '图像生成参数';
    default:
      return '对话推理参数';
  }
});

const capabilityHint = computed(() => {
  if (modelKind.value !== 'chat' || legacyChatFullPanel.value) return '';
  if (!chatHasAnyControl.value) {
    return '当前端点 capabilities 中未包含可调推理超参，请先在「模型管理」中为该端点勾选温度、Max Tokens 等推理能力。';
  }
  return '';
});

function resetForm() {
  Object.assign(form, {
    id: undefined,
    instanceKey: undefined,
    instanceName: undefined,
    modelKey: undefined,
    status: 'enabled',
    temperature: 0.7,
    maxTokens: 2048,
    topP: 1.0,
    topK: undefined,
    seed: undefined,
    stopSequences: undefined,
    frequencyPenalty: 0,
    presencePenalty: 0,
    dimensions: undefined,
    size: undefined,
    style: undefined
  });
  selectedKeys.value = [];
}

function updateTableScrollY() {
  const el = tableWrapRef.value;
  if (el && el.clientHeight > 160) {
    // 预留表头、分页与间距，仅表体区域滚动
    tableScrollY.value = Math.max(160, el.clientHeight - 140);
    return;
  }
  tableScrollY.value = Math.max(200, window.innerHeight - 380);
}
const statusOptions = [{ label: '立即激活', value: 'enabled' }, { label: '暂存停用', value: 'disabled' }];

const instanceKeyRules = [
  {
    validator: (_rule: unknown, value: unknown) => {
      if (value == null || String(value).trim() === '') return Promise.resolve();
      return /^[a-zA-Z0-9_-]+$/.test(String(value).trim())
        ? Promise.resolve()
        : Promise.reject('仅字母、数字、下划线、连字符');
    }
  }
];

const columns = [
  { title: '', dataIndex: 'providerAvatar', key: 'providerAvatar', width: 52 },
  { title: '类型', dataIndex: 'modelType', key: 'modelType', width: 100 },
  { title: '端点名称', dataIndex: 'modelName', key: 'modelName', width: 200 },
  { title: 'Model Key', dataIndex: 'modelKey', key: 'modelKey', ellipsis: true },
  { title: '云供应商', dataIndex: 'provider', key: 'provider', width: 120 },
];

const filteredModels = computed(() => {
  return modelList.value.filter(m => {
    const matchType = typeFilter.value === 'all' || m.modelType === typeFilter.value;
    const q = searchQuery.value.toLowerCase();
    const matchSearch = !searchQuery.value ||
      m.modelName?.toLowerCase().includes(q) ||
      m.modelKey?.toLowerCase().includes(q) ||
      (m.provider && m.provider.toLowerCase().includes(q));
    return matchType && matchSearch;
  });
});

const getTempInfo = (v: number) => {
  if (v <= 0.3) return { text: '🎯 适合：代码编写、数学逻辑、事实问答', color: 'c-blue' };
  if (v <= 0.8) return { text: '⚖️ 适合：通用对话、周报草拟、翻译', color: 'c-purple' };
  if (v <= 1.4) return { text: '🎨 适合：创意写作、角色扮演、头脑风暴', color: 'c-orange' };
  return { text: '🎲 适合：极高随机性的发散性内容', color: 'c-red' };
};

const fetchModels = async () => {
  modelsLoading.value = true;
  try {
    await ensureWorkspaceEnvInStorage();
    const res = await aiModelApi.queryPage({
      pageNo: 1,
      pageSize: 1000,
      param: {
        supplier: providerFilter.value?.trim() || undefined,
        status: 'enabled'
      }
    });
    modelList.value = res.list || [];
  } finally { modelsLoading.value = false; }
};

const customRow = (record: AiModel) => ({
  onClick: () => {
    selectedKeys.value = [record.modelKey!];
    applyModelSelection(record);
  },
  class: selectedKeys.value.includes(record.modelKey!) ? 'selected-row' : ''
});

const onRowSelectChange = (keys: string[]) => {
  selectedKeys.value = keys;
  const key = keys[0];
  if (!key) {
    form.modelKey = undefined;
    return;
  }
  const record = modelList.value.find((m) => m.modelKey === key);
  if (record) applyModelSelection(record);
  else form.modelKey = key;
};

const handleCancel = () => emit('update:visible', false);

const onSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  if (!form.modelKey) return message.warning('请先选择一个模型端点');
  submitting.value = true;
  try {
    const api = isEdit.value ? aiInstanceApi.update : aiInstanceApi.create;
    await api(form);
    message.success('预设配置已同步至 Astrsomn 引擎');
    emit('success');
    handleCancel();
  } finally { submitting.value = false; }
};

watch(() => props.visible, async (val) => {
  if (!val) return;
  providerFilter.value = undefined;
  searchDraft.value = '';
  searchQuery.value = '';
  await fetchModels();
  if (isEdit.value && editId.value != null) {
    presetNameUserEdited.value = true;
    const detail = await aiInstanceApi.detail(editId.value);
    resetForm();
    Object.assign(form, detail);
    if (detail.modelKey) selectedKeys.value = [detail.modelKey];
  } else {
    presetNameUserEdited.value = false;
    resetForm();
  }
  await nextTick();
  requestAnimationFrame(() => {
    updateTableScrollY();
  });
});

onMounted(() => {
  updateTableScrollY();
  window.addEventListener('resize', updateTableScrollY);
});
onUnmounted(() => {
  window.removeEventListener('resize', updateTableScrollY);
});
</script>

<style scoped>
/* 全屏容器 */
:global(.astrsomn-full-modal .ant-modal) { max-width: 100vw; top: 0; padding: 0; margin: 0; }
:global(.astrsomn-full-modal .ant-modal-content) { height: 100vh; border-radius: 0; padding: 0; background: #f8fafc; }

.fullscreen-wrapper { display: flex; flex-direction: column; height: 100vh; }

/* Header */
.modal-header {
  height: 72px; background: #fff; padding: 0 32px;
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #e2e8f0; flex-shrink: 0;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.logo-box {
  width: 42px; height: 42px; border-radius: 10px;
  background: var(--logo-gradient);
  display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22px;
}
.main-title { display: block; font-size: 18px; font-weight: 800; color: #0f172a; }
.sub-title { font-size: 12px; color: #94a3b8; }

.header-actions { display: flex; align-items: center; }
.header-action-pair {
  display: inline-flex;
  align-items: stretch;
}
.header-action-btn {
  height: 40px;
  min-width: 120px;
  padding: 0 22px;
  font-weight: 600;
}
.header-action-pair :deep(.header-action-btn-cancel.ant-btn) {
  border-top-left-radius: 14px;
  border-bottom-left-radius: 14px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.header-action-pair :deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
  border-right: none;
}
.header-action-pair :deep(.header-action-btn-cancel.ant-btn-default:hover) {
  color: #334155;
  border-color: #94a3b8;
  background: #f8fafc;
}
.header-action-pair :deep(.header-action-btn-save.ant-btn) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 14px;
  border-bottom-right-radius: 14px;
}
.header-action-pair :deep(.header-action-btn-save.ant-btn-primary) {
  margin-left: -1px;
  box-shadow: none;
}

/* 布局主体 */
.main-content { flex: 1; display: flex; padding: 20px; gap: 20px; overflow: hidden; }

/* 通用卡片样式 */
/* 本弹层为浅色底，左右面板固定白底 + 主题变量阴影（避免深色 :root 下 --bg-card 发灰） */
.glass-card {
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  box-shadow: var(--shadow-pane-elevated, 0 6px 24px rgba(15, 23, 42, 0.08));
}
.pane-card { height: 100%; display: flex; flex-direction: column; padding: 20px; }

/* 左侧 */
.selection-pane { flex: 1; min-width: 0; min-height: 0; display: flex; flex-direction: column; }
.pane-header { flex-shrink: 0; margin-bottom: 16px; display: flex; flex-direction: column; gap: 10px; }

.pane-toolbar-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
}
.provider-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 200px;
  flex: 0 1 220px;
}
.field-label {
  font-size: 12px;
  color: #64748b;
}
.instance-provider-select {
  min-width: 200px;
}

.inst-provider-avatar-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}
.inst-provider-avatar-cell :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}
.inst-model-type-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.inst-model-type-icon {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
}
.inst-model-type-icon.chat {
  background: linear-gradient(135deg, #0061ff, #60efff);
}
.inst-model-type-icon.embedding {
  background: linear-gradient(135deg, #7c4dff, #f94dff);
}
.inst-model-type-icon.image {
  background: linear-gradient(135deg, #ff6b6b, #ffd93d);
}
.inst-model-type-label {
  font-size: 12px;
  color: #475569;
}
.text-muted {
  color: #94a3b8;
}

.model-type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}
.model-type-tabs :deep(.ant-tabs-content-holder) {
  display: none;
}

.instance-meta-footer {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 32px;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  font-size: 12px;
  color: #64748b;
}
.meta-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.meta-label {
  color: #94a3b8;
  font-weight: 600;
}
.meta-value {
  color: #334155;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.meta-divider {
  width: 1px;
  height: 14px;
  background: #e2e8f0;
}

.table-container {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.table-container :deep(.ant-pagination) {
  margin: 12px 0 0;
}
.selection-pane .pane-card { min-height: 0; }
:deep(.selected-row td) {
  background-color: color-mix(in srgb, var(--primary) 12%, #ffffff) !important;
  color: var(--primary) !important;
  font-weight: 600;
}

/* 右侧 */
.config-pane { width: 440px; flex-shrink: 0; }
.scroll-y { overflow-y: auto; }
.section-header-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}
.section-header-flex .section-title { margin-bottom: 0; flex: 1; min-width: 0; }
.model-key-tag { flex-shrink: 0; max-width: 180px; overflow: hidden; text-overflow: ellipsis; }

.section-title { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.section-title .anticon { color: var(--primary); }

.cap-hint {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  margin-bottom: 16px;
}
.cap-hint.muted { color: #94a3b8; background: #fafafa; border-style: dashed; }
.p-inline-hint { font-size: 11px; color: #94a3b8; margin: 0; }
.stop-seq-input { margin-top: 8px; }

/* 参数卡片 */
.param-group-card {
  background: #f8fafc; border: 1px solid #f1f5f9; border-radius: 16px;
  padding: 16px; margin-bottom: 16px; transition: all 0.3s;
}
.param-group-card:hover { background: #fff; border-color: #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,0.04); }

.p-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.p-label { font-size: 13px; font-weight: 700; color: #475569; cursor: help; display: flex; align-items: center; gap: 4px; }
.slider-box { padding: 0 8px 16px 8px; }

/* 状态描述条 */
.p-desc-bar { margin-top: 8px; padding: 8px 12px; border-radius: 8px; font-size: 11px; font-weight: 600; border-left: 4px solid transparent; }
.c-blue { background: #eff6ff; color: #1d4ed8; border-left-color: #3b82f6; }
.c-purple { background: #faf5ff; color: #7e22ce; border-left-color: #a855f7; }
.c-orange { background: #fff7ed; color: #c2410c; border-left-color: #f97316; }
.c-red { background: #fef2f2; color: #b91c1c; border-left-color: #ef4444; }

/* 其他 */
.penalty-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.mini-param-card { background: #f8fafc; padding: 12px; border-radius: 12px; border: 1px solid #f1f5f9; }
.mini-label { font-size: 11px; font-weight: 700; color: #64748b; margin-bottom: 8px; display: block; }
.empty-state { text-align: center; padding: 100px 0; color: #cbd5e1; }

:deep(.ant-slider-mark-text) { font-size: 10px; color: #94a3b8; }
:deep(.ant-slider-mark-text-active) { color: var(--primary); font-weight: 700; }
</style>