<template>
  <a-modal
    :open="visible"
    width="80vw"
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
      </header>

      <div class="main-content">
        <BasicPane
          :form="form"
          :status-options="statusOptions"
          :instance-key-rules="instanceKeyRules"
          @preset-name-input="onPresetNameUserInput"
        />

        <ParamsPane
          :form="form"
          :param-section-title="paramSectionTitle"
          :capability-hint="capabilityHint"
          :has-param-schema="hasParamSchema"
          :unsupported-param-codes="unsupportedParamCodes"
          :model-kind="modelKind"
          :show-chat-temperature="showChatTemperature"
          :show-chat-max-tokens="showChatMaxTokens"
          :show-chat-top-p="showChatTopP"
          :show-chat-top-k="showChatTopK"
          :show-chat-seed="showChatSeed"
          :show-chat-stop-sequences="showChatStopSequences"
          :show-chat-penalties="showChatPenalties"
          :show-chat-frequency-penalty="showChatFrequencyPenalty"
          :show-chat-presence-penalty="showChatPresencePenalty"
          :show-embedding-dimensions="showEmbeddingDimensions"
          :show-image-size="showImageSize"
          :show-image-style="showImageStyle"
          :embedding-has-any-control="embeddingHasAnyControl"
          :image-has-any-control="imageHasAnyControl"
          :get-temp-info="getTempInfo"
        />

        <ModelSelectorPane
          :provider-filter="providerFilter"
          :search-draft="searchDraft"
          :type-filter="typeFilter"
          :model-list="modelList"
          :selected-keys="selectedKeys"
          :models-loading="modelsLoading"
          :models-loading-more="modelsLoadingMore"
          :total="modelPager.total"
          :has-next="modelPager.hasNext"
          :current-page="modelPager.pageNo"
          :page-size="modelPager.pageSize"
          :page-size-options="pageSizeOptions"
          :provider-avatar-cell="providerAvatarCell"
          :model-type-label="modelTypeLabel"
          @provider-change="onProviderFilterChange"
          @update:search-draft="searchDraft = $event"
          @search="applyModelSearch"
          @update:type-filter="typeFilter = $event"
          @page-change="onModelPageChange"
          @page-size-change="onModelPageSizeChange"
          @panel-scroll="onModelPanelScroll"
          @select-model="onSelectModelCard"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import {
  ThunderboltFilled
} from '@ant-design/icons-vue';
import BasicPane from './instance-form/BasicPane.vue';
import ParamsPane from './instance-form/ParamsPane.vue';
import ModelSelectorPane from './instance-form/ModelSelectorPane.vue';
import { ensureWorkspaceEnvInStorage } from '@/utils/ensureWorkspaceEnvStorage';
import { aiModelApi, type AiModel } from '@/api/aiModel';
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance';

function parseCapabilitiesRaw(raw?: string): string[] {
  if (!raw) return [];
  try {
    const p = JSON.parse(raw);
    return Array.isArray(p) ? p.map(String) : [];
  } catch {
    return [];
  }
}

type ModelParamDef = {
  id?: string
  mapping?: string
  active?: boolean
}

function parseModelParamsRaw(raw?: string): ModelParamDef[] {
  if (!raw) return []
  try {
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) return []
    return parsed
      .filter((item) => item && typeof item === 'object')
      .map((item) => item as ModelParamDef)
  } catch {
    return []
  }
}

interface Props { visible: boolean; record?: AiInstance }
const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'success']);

const editId = computed(() => props.record?.id);
const isEdit = computed(() => editId.value != null && String(editId.value) !== '');

const submitting = ref(false);
const modelsLoading = ref(false);
const modelsLoadingMore = ref(false);
const modelList = ref<AiModel[]>([]);
const selectedKeys = ref<string[]>([]);
const searchDraft = ref('');
const searchQuery = ref('');
const typeFilter = ref('all');
/** 与模型列表一致：按 AI_MODEL.supplier（扩展 key）筛选 */
const providerFilter = ref<string | undefined>(undefined);
const pageSizeOptions = [
  { label: '每页 12', value: 12 },
  { label: '每页 24', value: 24 },
  { label: '每页 36', value: 36 }
];
const modelPager = reactive({
  pageNo: 1,
  pageSize: 12,
  total: 0,
  hasNext: false
});

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
  await fetchModels(true);
  await nextTick();
  const mk = form.modelKey;
  if (mk && !modelList.value.some((m) => m.modelKey === mk)) {
    selectedKeys.value = [];
    form.modelKey = undefined;
  }
}

function applyModelSearch() {
  searchQuery.value = String(searchDraft.value ?? '').trim();
  void fetchModels(true);
}

const form = reactive<AiInstance>({ 
  status: 'enabled', 
  temperature: 0.7, 
  maxTokens: 2048,
  topP: 1.0,
  frequencyPenalty: 0,
  presencePenalty: 0
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
const selectedModelParams = computed(() => {
  const model = selectedModel.value
  const raw = model?.params || model?.param
  return parseModelParamsRaw(raw)
})
const hasParamSchema = computed(() => selectedModelParams.value.length > 0)
const supportedParamCodes = new Set<string>([
  'temperature',
  'max_tokens',
  'top_p',
  'top_k',
  'seed',
  'stop_sequences',
  'frequency_penalty',
  'presence_penalty',
  'dimensions',
  'size',
  'style'
])
const activeParamCodes = computed(() => {
  if (!hasParamSchema.value) return new Set<string>()
  const codes = selectedModelParams.value
    .filter((item) => item.active !== false)
    .map((item) => String(item.mapping || item.id || '').trim().toLowerCase())
    .filter(Boolean)
  return new Set(codes)
})
const unsupportedParamCodes = computed(() => {
  if (!hasParamSchema.value) return []
  return Array.from(activeParamCodes.value).filter((code) => !supportedParamCodes.has(code))
})

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
  if (hasParamSchema.value) return activeParamCodes.value.has('temperature')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('temperature') || c.includes('temperature_setting') || c.includes('text_generation');
});

const showChatMaxTokens = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('max_tokens')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('max_tokens') || c.includes('max_token_setting');
});

const showChatTopP = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('top_p')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('top_p') || c.includes('top_p_setting');
});

const showChatTopK = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('top_k')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('top_k') || c.includes('top_k_setting');
});

const showChatSeed = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('seed')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('seed') || c.includes('seed_setting');
});

const showChatStopSequences = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('stop_sequences')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return false;
  return c.includes('stop_sequences_setting');
});

const showChatFrequencyPenalty = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('frequency_penalty')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('frequency_penalty') || c.includes('frequency_penalty_setting');
});

const showChatPresencePenalty = computed(() => {
  if (modelKind.value !== 'chat') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('presence_penalty')
  const c = selectedCaps.value;
  if (legacyChatFullPanel.value) return true;
  return c.includes('presence_penalty') || c.includes('presence_penalty_setting');
});

const showChatPenalties = computed(
  () => showChatFrequencyPenalty.value || showChatPresencePenalty.value
);

const showEmbeddingDimensions = computed(() => {
  if (modelKind.value !== 'embedding') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('dimensions')
  const c = selectedCaps.value;
  if (legacyEmbeddingPanel.value) return true;
  return c.includes('embedding_dimensions');
});

const showImageSize = computed(() => {
  if (modelKind.value !== 'image') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('size')
  const c = selectedCaps.value;
  if (legacyImagePanel.value) return true;
  return c.includes('image_size') || c.includes('size_setting');
});

const showImageStyle = computed(() => {
  if (modelKind.value !== 'image') return false;
  if (hasParamSchema.value) return activeParamCodes.value.has('style')
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

const getTempInfo = (v: number) => {
  if (v <= 0.3) return { text: '🎯 适合：代码编写、数学逻辑、事实问答', color: 'c-blue' };
  if (v <= 0.8) return { text: '⚖️ 适合：通用对话、周报草拟、翻译', color: 'c-purple' };
  if (v <= 1.4) return { text: '🎨 适合：创意写作、角色扮演、头脑风暴', color: 'c-orange' };
  return { text: '🎲 适合：极高随机性的发散性内容', color: 'c-red' };
};

const fetchModels = async (reset = false) => {
  if (reset) {
    modelPager.pageNo = 1;
    modelPager.total = 0;
    modelPager.hasNext = false;
    modelList.value = [];
  }
  const loadingState = reset || modelPager.pageNo === 1;
  if (loadingState) modelsLoading.value = true;
  else modelsLoadingMore.value = true;
  try {
    await ensureWorkspaceEnvInStorage();
    const res = await aiModelApi.queryPage({
      pageNo: modelPager.pageNo,
      pageSize: modelPager.pageSize,
      param: {
        modelType: typeFilter.value === 'all' ? undefined : typeFilter.value,
        modelName: searchQuery.value || undefined,
        supplier: providerFilter.value?.trim() || undefined,
        status: 'enabled'
      }
    });
    const rows = (res.list || []).filter((item: AiModel) => item.modelKey);
    const merged = reset ? rows : [...modelList.value, ...rows];
    const uniq = new Map<string, AiModel>();
    merged.forEach((item: AiModel) => {
      const key = String(item.modelKey || '').trim();
      if (key) uniq.set(key, item);
    });
    modelList.value = Array.from(uniq.values());
    modelPager.total = Number(res.total || 0);
    modelPager.hasNext = Boolean(res.hasNext);
  } finally {
    modelsLoading.value = false;
    modelsLoadingMore.value = false;
  }
};

const onModelPanelScroll = (event?: Event) => {
  void event;
};

const onModelPageSizeChange = async (size: number) => {
  modelPager.pageNo = 1;
  modelPager.pageSize = Number(size || 12);
  await fetchModels(true);
};

const onModelPageChange = async (page: number) => {
  modelPager.pageNo = Number(page || 1);
  await fetchModels(true);
};

const onSelectModelCard = (record: AiModel) => {
  selectedKeys.value = [String(record.modelKey || '')];
  applyModelSelection(record);
};

const handleCancel = () => emit('update:visible', false);

const onSubmit = async () => {
  if (!String(form.instanceName || '').trim()) return message.warning('请输入名称');
  if (form.instanceKey && !/^[a-zA-Z0-9_-]+$/.test(String(form.instanceKey).trim())) {
    return message.warning('实例标识仅支持字母、数字、下划线、连字符');
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
  modelPager.pageNo = 1;
  modelPager.pageSize = 12;
  providerFilter.value = undefined;
  searchDraft.value = '';
  searchQuery.value = '';
  typeFilter.value = 'all';
  await fetchModels(true);
  if (isEdit.value && editId.value != null) {
    presetNameUserEdited.value = true;
    const detail = await aiInstanceApi.detail(editId.value);
    resetForm();
    Object.assign(form, detail);
    if (detail.modelKey) {
      const key = String(detail.modelKey);
      selectedKeys.value = [key];
      if (!modelList.value.some((m) => m.modelKey === key)) {
        const extra = await aiModelApi.queryPage({
          pageNo: 1,
          pageSize: 1,
          param: { modelKey: key }
        });
        const hit = extra.list?.[0];
        if (hit?.modelKey) {
          modelList.value = [hit, ...modelList.value];
        }
      }
    }
  } else {
    presetNameUserEdited.value = false;
    resetForm();
  }
  await nextTick();
});

watch(typeFilter, () => {
  if (!props.visible) return;
  void fetchModels(true);
});

</script>

<style scoped>
/* 全屏容器 */
:global(.astrsomn-full-modal.ant-modal-wrap) {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  min-height: 100vh !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  margin: 0 !important;
  padding: 0 !important;
}

:global(.astrsomn-full-modal .ant-modal) { 
  max-width: 80vw !important; 
  width: 80vw !important;
  padding: 0 !important; 
  margin: 0 !important; 
  top: auto !important;
  left: auto !important;
  transform: none !important;
  position: relative !important;
}
:global(.astrsomn-full-modal .ant-modal-content) { 
  height: 80vh !important; 
  border-radius: 20px !important; 
  padding: 0 !important; 
  background: var(--bg-surface, #f8fafc) !important;
  overflow: hidden !important;
}

.fullscreen-wrapper { 
  display: flex; 
  flex-direction: column; 
  height: 100%; 
  overflow: hidden;
}

/* Header */
.modal-header {
  height: 74px;
  background: #fff;
  padding: 0 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.logo-box {
  width: 40px; height: 40px; border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22px;
}
.main-title { display: block; font-size: 17px; font-weight: 700; color: #0f172a; }
.sub-title { font-size: 12px; color: #64748b; }

.header-actions { display: flex; align-items: center; gap: 10px; }
.header-action-btn {
  height: 38px;
  min-width: 110px;
  border-radius: 10px;
  padding: 0 20px;
  font-weight: 600;
}
.header-actions :deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
}
.header-actions :deep(.header-action-btn-save.ant-btn-primary) {
  box-shadow: none;
}

/* 布局主体 - 三栏布局 */
.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 16px;
  overflow: hidden;
  background: #f8fafc;
}

/* 通用卡片样式 */
/* 本弹层为浅色底，左右面板固定白底 + 主题变量阴影（避免深色 :root 下 --bg-card 发灰） */
.glass-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.04);
}
.pane-card { height: 100%; display: flex; flex-direction: column; padding: 18px; }

/* 左侧：基础定义 */
.basic-pane { width: 320px; flex-shrink: 0; }
.basic-pane .pane-card { overflow-y: auto; }

/* 中间：参数设定 */
.params-pane { flex: 1; min-width: 0; min-height: 0; display: flex; flex-direction: column; }
.params-pane .pane-card { overflow-y: auto; }

/* 右侧：模型选择 */
.selection-pane { width: 420px; flex-shrink: 0; min-height: 0; display: flex; flex-direction: column; }
.pane-header { flex-shrink: 0; margin-bottom: 16px; display: flex; flex-direction: column; gap: 10px; }

.pane-toolbar-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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
  color: var(--text-secondary, #64748b);
}
.instance-provider-select {
  min-width: 200px;
}

.inst-provider-avatar-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}
.inst-provider-avatar-cell :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}
.inst-provider-avatar-cell--empty {
  color: #cbd5e1;
  background: #f8fafc;
  border-radius: 6px;
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
  color: var(--text-secondary, #475569);
}
.text-muted {
  color: var(--text-muted, #94a3b8);
}

.endpoint-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.endpoint-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.endpoint-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.endpoint-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.model-type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}
.model-type-tabs :deep(.ant-tabs-tab) {
  padding-top: 2px;
  padding-bottom: 8px;
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
  background: var(--bg-card, #fff);
  border-top: 1px solid var(--border-default, #e2e8f0);
  font-size: 12px;
  color: var(--text-secondary, #64748b);
}
.meta-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.meta-label {
  color: var(--text-muted, #94a3b8);
  font-weight: 600;
}
.meta-value {
  color: var(--text-primary, #334155);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.meta-divider {
  width: 1px;
  height: 14px;
  background: var(--border-default, #e2e8f0);
}

.model-list-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.meta-count {
  font-size: 12px;
  color: #64748b;
}

.model-card-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.model-card-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-select-card {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.model-select-card:hover {
  border-color: #93c5fd;
  background: #f8fbff;
}

.model-select-card.is-active {
  border-color: #60a5fa;
  background: #eff6ff;
}

.model-select-radio {
  width: 16px;
  height: 16px;
  border: 1px solid #cbd5e1;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.model-select-radio .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: transparent;
}

.model-select-card.is-active .model-select-radio {
  border-color: #3b82f6;
}

.model-select-card.is-active .model-select-radio .dot {
  background: #3b82f6;
}

.model-list-empty,
.model-list-loading-more {
  text-align: center;
  color: #94a3b8;
  font-size: 12px;
  padding: 12px 0;
}
.selection-pane .pane-card { min-height: 0; }

/* 滚动容器 */
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

.section-title { font-size: 15px; font-weight: 700; color: var(--text-heading, #1e293b); margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.section-title .anticon { color: var(--primary); }

.basic-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.basic-form-grid .span-2 {
  grid-column: span 2;
}

.instance-key-display {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
}

.instance-key-display :deep(.instance-key-input.ant-input-affix-wrapper),
.instance-key-display :deep(.instance-key-input.ant-input) {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
  padding-left: 4px;
  padding-right: 4px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.instance-key-clear-btn {
  color: #94a3b8;
  width: 24px;
  min-width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 6px;
}

.basic-form-grid :deep(.ant-segmented) {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 3px;
}

.basic-form-grid :deep(.ant-segmented-item-selected) {
  background: #eff6ff !important;
  color: #1d4ed8 !important;
}

.cap-hint {
  font-size: 12px;
  color: var(--text-secondary, #64748b);
  line-height: 1.5;
  padding: 10px 12px;
  background: var(--bg-surface, #f8fafc);
  border-radius: 10px;
  border: 1px solid var(--border-default, #e2e8f0);
  margin-bottom: 16px;
}
.cap-hint.muted { color: var(--text-muted, #94a3b8); background: var(--bg-surface, #fafafa); border-style: dashed; }
.p-inline-hint { font-size: 11px; color: var(--text-muted, #94a3b8); margin: 0; }
.stop-seq-input { margin-top: 8px; }

/* 参数卡片 */
.param-group-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 14px;
  transition: all 0.2s;
}
.param-group-card:hover { background: #fff; border-color: #cbd5e1; box-shadow: 0 4px 10px rgba(15,23,42,0.06); }

.p-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.p-label { font-size: 13px; font-weight: 600; color: #334155; cursor: help; display: flex; align-items: center; gap: 4px; }
.slider-box { padding: 0 8px 16px 8px; }

/* 状态描述条 */
.p-desc-bar { margin-top: 8px; padding: 8px 12px; border-radius: 8px; font-size: 11px; font-weight: 600; border-left: 4px solid transparent; }
.c-blue { background: #eff6ff; color: #1d4ed8; border-left-color: #3b82f6; }
.c-purple { background: #faf5ff; color: #7e22ce; border-left-color: #a855f7; }
.c-orange { background: #fff7ed; color: #c2410c; border-left-color: #f97316; }
.c-red { background: #fef2f2; color: #b91c1c; border-left-color: #ef4444; }

/* 其他 */
.penalty-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.mini-param-card { background: var(--bg-surface, #f8fafc); padding: 12px; border-radius: 12px; border: 1px solid var(--border-default, #f1f5f9); }
.mini-label { font-size: 11px; font-weight: 700; color: var(--text-secondary, #64748b); margin-bottom: 8px; display: block; }
.empty-state { text-align: center; padding: 100px 0; color: var(--text-muted, #cbd5e1); }
.empty-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  margin: 0 auto 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef2ff;
  color: #4f46e5;
}

:deep(.ant-slider-mark-text) { font-size: 10px; color: var(--text-muted, #94a3b8); }
:deep(.ant-slider-mark-text-active) { color: var(--primary); font-weight: 700; }

@media (max-width: 1480px) {
  .basic-pane {
    width: 280px;
  }
  .selection-pane {
    width: 360px;
  }
}

@media (max-width: 1280px) {
  .main-content {
    flex-direction: column;
    overflow-y: auto;
  }
  .basic-pane,
  .selection-pane,
  .params-pane {
    width: 100%;
  }
  .basic-pane .pane-card,
  .params-pane .pane-card,
  .selection-pane .pane-card {
    max-height: none;
  }
}
</style>