<template>
  <AstModal
      :confirm-loading="submitting"
      :confirm-text="t.form.saveButton"
      :max-width="maxWidth"
      :open="visible"
      body-height="90vh"
      main-padding="0"
      max-body-height="800px"
      width="80vw"
      wrap-class-name="instance-form-fsm-wrap"
      @confirm="onSubmit"
      @update:open="emit('update:visible', $event)"
  >
    <template #header-logo>
      <ThunderboltFilled/>
    </template>
    <template #header-title>
      {{ isEdit ? t.form.editTitle : t.form.createTitle }}
    </template>
    <template #header-subtitle>
      {{ t.form.subtitle }}
    </template>
    <div class="instance-form-shell">
      <div class="main-content">
        <Left
            :account-selector-open="accountSelectorOpen"
            :form="form"
            :instance-key-rules="instanceKeyRules"
            :is-edit="isEdit"
            @preset-name-input="onPresetNameUserInput"
            @open-account-selector="accountSelectorOpen = true"
            @select-account="onAccountSelect"
        />
        <ModelParam
            :current-page="modelPager.pageNo"
            :has-next="modelPager.hasNext"
            :is-edit="isEdit"
            :model-list="modelList"
            :model-type-label="modelTypeLabel"
            :models-loading="modelsLoading"
            :models-loading-more="modelsLoadingMore"
            :page-size="modelPager.pageSize"
            :page-size-options="pageSizeOptions"
            :provider-avatar-cell="providerAvatarCell"
            :provider-filter="providerFilter"
            :search-draft="searchDraft"
            :selected-keys="selectedKeys"
            :total="modelPager.total"
            :type-filter="typeFilter"
            @search="applyModelSearch"
            @provider-change="onProviderFilterChange"
            @update:search-draft="searchDraft = $event"
            @update:type-filter="typeFilter = $event"
            @page-change="onModelPageChange"
            @page-size-change="onModelPageSizeChange"
            @panel-scroll="onModelPanelScroll"
            @select-model="onSelectModelCard"
        />
        <InstanceParam
            :capability-hint="capabilityHint"
            :embedding-has-any-control="embeddingHasAnyControl"
            :form="form"
            :get-temp-info="getTempInfo"
            :has-param-schema="hasParamSchema"
            :image-has-any-control="imageHasAnyControl"
            :model-kind="modelKind"
            :param-section-title="paramSectionTitle"
            :show-chat-frequency-penalty="showChatFrequencyPenalty"
            :show-chat-max-tokens="showChatMaxTokens"
            :show-chat-penalties="showChatPenalties"
            :show-chat-presence-penalty="showChatPresencePenalty"
            :show-chat-seed="showChatSeed"
            :show-chat-stop-sequences="showChatStopSequences"
            :show-chat-temperature="showChatTemperature"
            :show-chat-top-k="showChatTopK"
            :show-chat-top-p="showChatTopP"
            :show-embedding-dimensions="showEmbeddingDimensions"
            :show-image-size="showImageSize"
            :show-image-style="showImageStyle"
            :unsupported-param-codes="unsupportedParamCodes"
        />
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, nextTick, reactive, ref, watch} from 'vue';
import {message} from 'ant-design-vue';
import {ThunderboltFilled} from '@ant-design/icons-vue';
import AstModal from '@/components/home/AstModal.vue';
import Left from './instance-form/Left.vue';
import InstanceParam from './instance-form/InstanceParam.vue';
import ModelParam from './instance-form/ModelParam.vue';
import {ensureWorkspaceEnvInStorage} from '@/utils/workspaceHelper.ts';
import {type AiModel, aiModelApi} from '@/api/aiModel';
import {type AiInstance, aiInstanceApi} from '@/api/aiInstance';
import {getTempInfo, useInstanceParamVisibility} from './useInstanceParamVisibility';
import {usePageTranslation} from '@/locales/pages.ts';

const t = usePageTranslation('ai-instance')

interface Props {
  visible: boolean;
  record?: AiInstance
}

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

const providerFilter = ref<string | undefined>(undefined);
const pageSizeOptions = computed(() => [
  {label: t.value.form.pageSizeOption.perPage12, value: 12},
  {label: t.value.form.pageSizeOption.perPage24, value: 24},
  {label: t.value.form.pageSizeOption.perPage36, value: 36}
]);
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

function modelTypeLabel(t2?: string) {
  if (t2 === 'embedding') return t.value.modelType.embedding;
  if (t2 === 'image') return t.value.modelType.image;
  return t.value.modelType.chat;
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
          : Promise.reject(t.value.form.validation.instanceKeyRule);
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

const onAccountSelect = (account: any) => {
  if (account.accountKey) {
    form.accountKey = account.accountKey;
  }
};

const onSubmit = async () => {
  if (!String(form.instanceName || '').trim()) return message.warning(t.value.form.validation.nameRequired);
  if (!String(form.instanceKey || '').trim()) return message.warning(t.value.form.validation.instanceKeyRequired);
  if (!String(form.accountKey || '').trim()) return message.warning(t.value.form.validation.accountKeyRequired);
  if (form.instanceKey && !/^[a-zA-Z0-9_-]+$/.test(String(form.instanceKey).trim())) {
    return message.warning(t.value.form.validation.instanceKeyPattern);
  }
  if (!form.modelKey) return message.warning(t.value.form.validation.modelRequired);
  submitting.value = true;
  try {
    const api = isEdit.value ? aiInstanceApi.update : aiInstanceApi.create;
    await api(form);
    message.success(t.value.form.saveSuccess);
    emit('success');
    emit('update:visible', false);
  } finally {
    submitting.value = false;
  }
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
          param: {modelKey: key}
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
  background: var(--bg-secondary);
  min-height: 0;
  gap: 0;
  align-items: stretch;
}


</style>
