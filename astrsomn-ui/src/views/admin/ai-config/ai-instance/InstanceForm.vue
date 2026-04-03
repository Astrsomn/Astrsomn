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
          <a-button class="action-btn" @click="handleCancel">取消</a-button>
          <a-button 
            type="primary" 
            class="action-btn gradient-btn" 
            :loading="submitting" 
            @click="onSubmit"
          >
            保存预设
          </a-button>
        </div>
      </header>

      <div class="main-content">
        <section class="selection-pane">
          <div class="pane-card glass-card">
            <div class="pane-header">
              <div class="search-row">
                <a-input-search
                  v-model:value="searchQuery"
                  placeholder="搜索名称、Provider 或 Model Key..."
                  allow-clear
                  size="large"
                  class="custom-search"
                />
              </div>
              <div class="filter-row">
                <a-radio-group v-model:value="typeFilter" button-style="solid">
                  <a-radio-button value="all">全部类型</a-radio-button>
                  <a-radio-button value="chat">对话</a-radio-button>
                  <a-radio-button value="embedding">向量</a-radio-button>
                  <a-radio-button value="image">图像</a-radio-button>
                </a-radio-group>
              </div>
            </div>

            <div ref="tableWrapRef" class="table-container">
              <a-table
                :columns="columns"
                :data-source="filteredModels"
                :loading="modelsLoading"
                :pagination="{ pageSize: 12, showTotal: t => `共 ${t} 个可用端点`, showSizeChanger: false }"
                :scroll="{ y: tableScrollY }"
                :row-selection="{ selectedRowKeys: selectedKeys, onChange: onRowSelectChange, type: 'radio' }"
                :custom-row="customRow"
                row-key="modelKey"
                size="middle"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'provider'">
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
                  <a-input v-model:value="form.instanceName" placeholder="例如：通用对话-生产环境" size="large" />
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
                  <h3 class="section-title"><ControlOutlined /> 推理参数</h3>
                  <a-tag v-if="form.modelKey" color="purple" class="model-key-tag">{{ form.modelKey }}</a-tag>
                </div>

                <div v-if="!form.modelKey" class="empty-state">
                  <div class="empty-icon"><SelectOutlined /></div>
                  <p>请在左侧表格中选中一个接入端点</p>
                </div>

                <div v-else class="params-list">
                  
                  <div class="param-group-card">
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
                        :min="0" :max="2" :step="0.1" 
                        :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }" 
                      />
                    </div>
                    <div class="p-desc-bar" :class="getTempInfo(form.temperature).color">
                      {{ getTempInfo(form.temperature).text }}
                    </div>
                  </div>

                  <div class="param-group-card">
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
                        :min="0" :max="8192" :step="256"
                        :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }"
                      />
                    </div>
                  </div>

                  <div class="param-group-card">
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

                  <div class="penalty-row">
                    <div class="mini-param-card">
                      <span class="mini-label">重复惩罚 (Frequency)</span>
                      <a-slider v-model:value="form.frequencyPenalty" :min="-2" :max="2" :step="0.1" />
                    </div>
                    <div class="mini-param-card">
                      <span class="mini-label">新鲜度 (Presence)</span>
                      <a-slider v-model:value="form.presencePenalty" :min="-2" :max="2" :step="0.1" />
                    </div>
                  </div>

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
  ControlOutlined, SelectOutlined
} from '@ant-design/icons-vue';
import { aiModelApi, type AiModel } from '@/api/aiModel';
import { aiInstanceApi, type AiInstance } from '@/api/aiInstance';

interface Props { visible: boolean; record?: AiInstance }
const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'success']);

const formRef = ref();
const tableWrapRef = ref<HTMLElement | null>(null);
const submitting = ref(false);
const modelsLoading = ref(false);
const modelList = ref<AiModel[]>([]);
const selectedKeys = ref<string[]>([]);
const searchQuery = ref('');
const typeFilter = ref('all');
const tableScrollY = ref(320);

const form = reactive<AiInstance>({ 
  status: 'enabled', 
  temperature: 0.7, 
  maxTokens: 2048,
  topP: 1.0,
  frequencyPenalty: 0,
  presencePenalty: 0
});

const editId = computed(() => props.record?.id);
const isEdit = computed(() => editId.value != null && String(editId.value) !== '');

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
    frequencyPenalty: 0,
    presencePenalty: 0
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
  { title: '端点名称', dataIndex: 'modelName', key: 'modelName', width: 220 },
  { title: 'Model Key', dataIndex: 'modelKey', key: 'modelKey' },
  { title: '云供应商', dataIndex: 'provider', key: 'provider', width: 120 },
];

const filteredModels = computed(() => {
  return modelList.value.filter(m => {
    const matchType = typeFilter.value === 'all' || m.modelType === typeFilter.value;
    const matchSearch = !searchQuery.value || 
      m.modelName?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      m.modelKey?.toLowerCase().includes(searchQuery.value.toLowerCase());
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
    const res = await aiModelApi.queryPage({ pageNo: 1, pageSize: 1000, param: {} });
    modelList.value = res.list || [];
  } finally { modelsLoading.value = false; }
};

const customRow = (record: AiModel) => ({
  onClick: () => {
    selectedKeys.value = [record.modelKey!];
    form.modelKey = record.modelKey;
  },
  class: selectedKeys.value.includes(record.modelKey!) ? 'selected-row' : ''
});

const onRowSelectChange = (keys: any[]) => {
  selectedKeys.value = keys;
  form.modelKey = keys[0];
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
  await fetchModels();
  if (isEdit.value && editId.value != null) {
    const detail = await aiInstanceApi.detail(editId.value);
    resetForm();
    Object.assign(form, detail);
    if (detail.modelKey) selectedKeys.value = [detail.modelKey];
  } else {
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
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22px;
}
.main-title { display: block; font-size: 18px; font-weight: 800; color: #0f172a; }
.sub-title { font-size: 12px; color: #94a3b8; }
.gradient-btn {
  background: linear-gradient(90deg, #6366f1, #a855f7); border: none;
  height: 40px; padding: 0 24px; border-radius: 8px; font-weight: 600;
}

/* 布局主体 */
.main-content { flex: 1; display: flex; padding: 20px; gap: 20px; overflow: hidden; }

/* 通用卡片样式 */
.glass-card { background: #fff; border-radius: 20px; border: 1px solid #fff; box-shadow: 0 4px 20px rgba(0,0,0,0.03); }
.pane-card { height: 100%; display: flex; flex-direction: column; padding: 20px; }

/* 左侧 */
.selection-pane { flex: 1; min-width: 0; min-height: 0; display: flex; flex-direction: column; }
.pane-header { flex-shrink: 0; margin-bottom: 20px; display: flex; flex-direction: column; gap: 12px; }
.table-container {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.table-container :deep(.ant-pagination) {
  margin: 12px 0 0;
}
.selection-pane .pane-card { min-height: 0; }
:deep(.selected-row td) { background-color: #f5f3ff !important; color: #6366f1 !important; font-weight: 600; }

/* 右侧 */
.config-pane { width: 440px; flex-shrink: 0; }
.scroll-y { overflow-y: auto; }
.section-title { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.section-title .anticon { color: #6366f1; }

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
:deep(.ant-slider-mark-text-active) { color: #6366f1; font-weight: 700; }
</style>