<template>
  <AstrsomnModal
    :open="visible"
    width="80vw"
    :max-width="maxWidth"
    body-height="90vh"
    max-body-height="800px"
    :closable="false"
    main-padding="0"
    wrap-class-name="instance-form-fsm-wrap"
    @update:open="emit('update:visible', $event)"
    @cancel="handleCancel"
  >
    <template #header-logo>
      <ThunderboltFilled />
    </template>
    <template #header-title>
      {{ isEdit ? '编辑推理预设' : '新建推理预设' }}
    </template>
    <template #header-subtitle>
      配置 Astrsomn 核心引擎的运行策略与端点映射
    </template>
    <template #header-actions>
      <a-button class="header-action-btn header-action-btn-cancel" @click="handleCancel">取消</a-button>
      <a-button
        type="primary"
        class="header-action-btn header-action-btn-save"
        :loading="submitting"
        @click="onSubmit"
      >
        保存预设
      </a-button>
    </template>

    <div class="instance-form-shell">
      <div class="main-content">
        <Left
        :form="form"
        :is-edit="isEdit"
        :instance-key-rules="instanceKeyRules"
        :account-selector-open="accountSelectorOpen"
        @preset-name-input="onPresetNameUserInput"
        @open-account-selector="accountSelectorOpen = true"
        @select-account="onAccountSelect"
      />
     <Right
        :is-edit="isEdit"
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
      <Center
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
      </div>
    </div>
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import {
  ThunderboltFilled
} from '@ant-design/icons-vue';
import AstrsomnModal from '@/components/home/AstrsomnModal.vue';
import Left from './instance-form/Left.vue';
import Center from './instance-form/Center.vue';
import Right from './instance-form/Right.vue';
import { ensureWorkspaceEnvInStorage } from '@/utils/workspaceHelper.ts';
import { aiModelApi, type AiModel } from '@/api/aiModel';
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance';
import { useInstanceParamVisibility, getTempInfo } from './useInstanceParamVisibility';

interface Props { visible: boolean; record?: AiInstance }
const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'success']);

const editId = computed(() => props.record?.id);
const isEdit = computed(() => editId.value != null && String(editId.value) !== '');
const maxWidth = computed(() => 'min(80vw, 1600px)');

const submitting = ref(false);
const accountSelectorOpen = ref(false);
const modelsLoading = ref(false);
const modelsLoadingMore = ref(false);
const modelList = ref<AiModel[]>([]);
const selectedKeys = ref<string[]>([]);
const searchDraft = ref('');
const searchQuery = ref('');
const typeFilter = ref('all');
/** 与模型列表一致：按 AI_MODEL.extensionCode（扩展 key）筛选 */
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
  isDefault: 'N',
  temperature: 0.7,
  maxTokens: 2048,
  topP: 1.0,
  frequencyPenalty: 0,
  presencePenalty: 0,
  accountKey: ''
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
const {
  hasParamSchema,
  unsupportedParamCodes,
  modelKind,
  showChatTemperature,
  showChatMaxTokens,
  showChatTopP,
  showChatTopK,
  showChatSeed,
  showChatStopSequences,
  showChatPenalties,
  showChatFrequencyPenalty,
  showChatPresencePenalty,
  showEmbeddingDimensions,
  showImageSize,
  showImageStyle,
  embeddingHasAnyControl,
  imageHasAnyControl,
  paramSectionTitle,
  capabilityHint,
} = useInstanceParamVisibility(selectedModel);

function resetForm() {
  Object.assign(form, {
    id: undefined,
    instanceKey: undefined,
    instanceName: undefined,
    modelKey: undefined,
    status: 'enabled',
    isDefault: 'N',
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
    style: undefined,
    accountKey: ''
  });
  selectedKeys.value = [];
}

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
        extensionCode: providerFilter.value?.trim() || undefined,
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

const onAccountSelect = (account: any) => {
  if (account.accountKey) {
    form.accountKey = account.accountKey;
  }
};

const onSubmit = async () => {
  if (!String(form.instanceName || '').trim()) return message.warning('请输入名称');
  if (!String(form.instanceKey || '').trim()) return message.warning('请输入实例标识');
  if (!String(form.accountKey || '').trim()) return message.warning('请选择关联账号');
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
  accountSelectorOpen.value = false;
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
:global(.instance-form-fsm-wrap.ant-modal-wrap) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:global(.instance-form-fsm-wrap .ant-modal) {
  top: 0;
  padding-bottom: 0;
}

.header-action-btn {
  height: 38px;
  min-width: 110px;
  border-radius: var(--radius-md);
  padding: 0 20px;
  font-weight: 600;
}

:deep(.header-action-btn-cancel.ant-btn-default) {
  color: #475569;
  border-color: #cbd5e1;
  background: #fff;
}

:deep(.header-action-btn-save.ant-btn-primary) {
  box-shadow: none;
}

.instance-form-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.main-content {
  flex: 1;
  display: flex;
  background: #f8fafc;
  min-height: 0;
  gap: 0;
  align-items: stretch;
}


</style>