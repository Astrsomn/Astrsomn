<template>
  <AgentConfigSectionShell :step="2" title="推理实例列表">
    <div class="instance-list-container">
      <div class="instance-list-header">
        <span class="header-desc">一个 Agent 可以配置多个推理实例，支持负载均衡和故障降级</span>
        <a-button type="primary" size="small" @click="showAddModal = true">
          <PlusOutlined />
          添加实例
        </a-button>
      </div>

      <div v-if="instanceList.length === 0" class="empty-state">
        <CloudServerOutlined class="empty-icon" />
        <p class="empty-text">暂无推理实例</p>
        <a-button type="primary" @click="showAddModal = true">添加第一个实例</a-button>
      </div>

      <div v-else class="instance-card-list">
        <div
            v-for="(instance, index) in instanceList"
            :key="instance.instanceKey || index"
            class="instance-card"
        >
          <div class="card-header">
            <div class="card-title">
              <span class="instance-index">{{ index + 1 }}</span>
              <span>{{ instance.instanceName || instance.instanceKey || '未命名实例' }}</span>
            </div>
            <div class="card-actions">
              <a-button size="small" @click="editInstance(index)">
                <EditOutlined />
              </a-button>
              <a-button size="small" danger @click="removeInstance(index)">
                <DeleteOutlined />
              </a-button>
            </div>
          </div>

          <div class="card-content">
            <div class="model-info">
              <span class="label">模型</span>
              <span class="value">{{ instance.modelKey || '未选择' }}</span>
            </div>
            <div class="strategy-info">
              <span class="label">路由策略</span>
              <span class="value">{{ getRouteStrategyLabel(instance.routeStrategy) }}</span>
            </div>
            <div class="weight-info" v-if="instance.routeWeight">
              <span class="label">权重</span>
              <span class="value">{{ instance.routeWeight }}</span>
            </div>
          </div>

          <div class="card-advanced" v-if="showAdvanced[index]">
            <div class="param-section">
              <div class="param-title">模型参数</div>
              <div class="param-grid">
                <div class="param-item">
                  <label>温度</label>
                  <a-input-number :value="instance.temperature" @change="(v) => updateField(index, 'temperature', v)" />
                </div>
                <div class="param-item">
                  <label>最大Token</label>
                  <a-input-number :value="instance.maxTokens" @change="(v) => updateField(index, 'maxTokens', v)" />
                </div>
                <div class="param-item">
                  <label>Top P</label>
                  <a-input-number :value="instance.topP" :step="0.1" @change="(v) => updateField(index, 'topP', v)" />
                </div>
                <div class="param-item">
                  <label>Top K</label>
                  <a-input-number :value="instance.topK" @change="(v) => updateField(index, 'topK', v)" />
                </div>
              </div>
            </div>

            <div class="resilience-section">
              <div class="resilience-title">Resilience4j 配置</div>
              
              <a-space direction="vertical" class="resilience-content">
                <a-switch
                    :checked="instance.resilienceEnabled === 'true'"
                    @change="(v) => updateField(index, 'resilienceEnabled', v ? 'true' : 'false')"
                >
                  启用 Resilience4j
                </a-switch>

                <template v-if="instance.resilienceEnabled === 'true'">
                  <div class="resilience-group">
                    <span class="group-label">断路器</span>
                    <a-switch
                        :checked="instance.circuitBreakerEnabled === 'true'"
                        @change="(v) => updateField(index, 'circuitBreakerEnabled', v ? 'true' : 'false')"
                    >
                      启用
                    </a-switch>
                    <div v-if="instance.circuitBreakerEnabled === 'true'" class="nested-fields">
                      <a-input-number
                          :value="instance.circuitBreakerFailureRateThreshold"
                          :placeholder="'故障率阈值(%)'"
                          @change="(v) => updateField(index, 'circuitBreakerFailureRateThreshold', v)"
                      />
                      <a-input-number
                          :value="instance.circuitBreakerWaitDuration"
                          :placeholder="'等待时间(ms)'"
                          @change="(v) => updateField(index, 'circuitBreakerWaitDuration', v)"
                      />
                    </div>
                  </div>

                  <div class="resilience-group">
                    <span class="group-label">重试</span>
                    <a-switch
                        :checked="instance.retryEnabled === 'true'"
                        @change="(v) => updateField(index, 'retryEnabled', v ? 'true' : 'false')"
                    >
                      启用
                    </a-switch>
                    <div v-if="instance.retryEnabled === 'true'" class="nested-fields">
                      <a-input-number
                          :value="instance.retryMaxAttempts"
                          :placeholder="'最大重试次数'"
                          @change="(v) => updateField(index, 'retryMaxAttempts', v)"
                      />
                      <a-input-number
                          :value="instance.retryWaitDuration"
                          :placeholder="'重试间隔(ms)'"
                          @change="(v) => updateField(index, 'retryWaitDuration', v)"
                      />
                    </div>
                  </div>

                  <div class="resilience-group">
                    <span class="group-label">超时</span>
                    <a-switch
                        :checked="instance.timeoutEnabled === 'true'"
                        @change="(v) => updateField(index, 'timeoutEnabled', v ? 'true' : 'false')"
                    >
                      启用
                    </a-switch>
                    <div v-if="instance.timeoutEnabled === 'true'" class="nested-fields">
                      <a-input-number
                          :value="instance.timeoutDuration"
                          :placeholder="'超时时间(ms)'"
                          @change="(v) => updateField(index, 'timeoutDuration', v)"
                      />
                    </div>
                  </div>

                  <div class="resilience-group">
                    <span class="group-label">降级</span>
                    <a-switch
                        :checked="instance.fallbackEnabled === 'true'"
                        @change="(v) => updateField(index, 'fallbackEnabled', v ? 'true' : 'false')"
                    >
                      启用
                    </a-switch>
                    <div v-if="instance.fallbackEnabled === 'true'" class="nested-fields">
                      <a-select
                          :value="instance.fallbackInstanceKey"
                          :options="getFallbackOptions(index)"
                          :placeholder="'选择降级实例'"
                          @change="(v) => updateField(index, 'fallbackInstanceKey', v)"
                      />
                    </div>
                  </div>
                </template>
              </a-space>
            </div>
          </div>

          <div class="card-footer">
            <a-button type="text" @click="showAdvanced[index] = !showAdvanced[index]">
              {{ showAdvanced[index] ? '收起' : '展开配置' }}
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <a-modal
        :title="editingIndex >= 0 ? '编辑实例' : '添加实例'"
        :visible="showAddModal"
        @ok="handleModalOk"
        @cancel="showAddModal = false"
    >
      <div class="modal-form">
        <a-form :model="formData" layout="vertical">
          <a-form-item label="实例名称">
            <a-input v-model:value="formData.instanceName" />
          </a-form-item>

          <a-form-item label="选择模型">
            <a-select
                v-model:value="formData.modelKey"
                :options="modelOptions"
                :placeholder="'请选择模型'"
                @change="onModelSelect"
            />
          </a-form-item>

          <a-form-item label="路由策略">
            <a-select
                v-model:value="formData.routeStrategy"
                :options="routeStrategyOptions"
                :placeholder="'请选择路由策略'"
            />
          </a-form-item>

          <a-form-item label="权重" v-if="formData.routeStrategy === 'weightedRandom'">
            <a-input-number v-model:value="formData.routeWeight" :min="1" :max="100" />
          </a-form-item>

          <a-form-item label="是否启用参数配置">
            <a-switch
                v-model:checked="formData.customParams"
                @change="(v) => formData.customParams = v"
            />
          </a-form-item>

          <template v-if="formData.customParams">
            <a-divider />
            <div class="param-section-title">模型参数（可选）</div>
            <a-form-item label="温度">
              <a-input-number v-model:value="formData.temperature" :step="0.1" :min="0" :max="2" />
            </a-form-item>
            <a-form-item label="最大Token">
              <a-input-number v-model:value="formData.maxTokens" :min="1" />
            </a-form-item>
            <a-form-item label="Top P">
              <a-input-number v-model:value="formData.topP" :step="0.1" :min="0" :max="1" />
            </a-form-item>
            <a-form-item label="Top K">
              <a-input-number v-model:value="formData.topK" :min="1" />
            </a-form-item>
          </template>
        </a-form>
      </div>
    </a-modal>
  </AgentConfigSectionShell>
</template>

<script lang="ts" setup>import {ref, reactive, watch} from 'vue';
import {PlusOutlined, EditOutlined, DeleteOutlined, CloudServerOutlined} from '@ant-design/icons-vue';
import type {AiModel} from '@/api/aiModel';
import type {AiInstance} from '@/api/aiInstance';
import AgentConfigSectionShell from './AgentConfigSectionShell.vue';
const props = defineProps<{
 instanceList: AiInstance[];
 availableModels: AiModel[];
}>();
const emit = defineEmits<{
 (e: 'update:instanceList', list: AiInstance[]): void;
}>();
const showAddModal = ref(false);
const editingIndex = ref(-1);
const showAdvanced = ref<Record<number, boolean>>({});
const modelOptions = ref<{
 value: string;
 label: string;
}[]>([]);
const routeStrategyOptions = [
 {value: 'roundRobin', label: '轮询'},
 {value: 'random', label: '随机'},
 {value: 'weightedRandom', label: '加权随机'},
 {value: 'stickyMemory', label: '粘性会话'},
 {value: 'failoverOrdered', label: '故障转移'},
];
const formData = reactive({
 instanceName: '',
 modelKey: '',
 routeStrategy: 'roundRobin',
 routeWeight: 10,
 customParams: false,
 temperature: 0.7,
 maxTokens: 2048,
 topP: 1.0,
 topK: undefined as number | undefined,
});
watch(() => props.availableModels, (models) => {
 modelOptions.value = models.map((m) => ({
 value: m.modelKey || '',
 label: m.modelName || m.modelKey || '',
 }));
}, {immediate: true});
function getRouteStrategyLabel(strategy?: string): string {
 const found = routeStrategyOptions.find((o) => o.value === strategy);
 return found?.label || strategy || '未配置';
}
function getFallbackOptions(excludeIndex: number): {
 value: string;
 label: string;
}[] {
 return props.instanceList
 .filter((_, i) => i !== excludeIndex)
 .map((inst, i) => ({
 value: inst.instanceKey || '',
 label: inst.instanceName || inst.instanceKey || `实例 ${i + 1}`,
 }));
}
function updateField(index: number, field: string, value: unknown) {
 const newList = [...props.instanceList];
 newList[index] = {...newList[index], [field]: value};
 emit('update:instanceList', newList);
}
function removeInstance(index: number) {
 const newList = props.instanceList.filter((_, i) => i !== index);
 emit('update:instanceList', newList);
}
function editInstance(index: number) {
 const instance = props.instanceList[index];
 editingIndex.value = index;
 formData.instanceName = instance.instanceName || '';
 formData.modelKey = instance.modelKey || '';
 formData.routeStrategy = instance.routeStrategy || 'roundRobin';
 formData.routeWeight = instance.routeWeight || 10;
 formData.customParams = !!(instance.temperature !== undefined || instance.maxTokens !== undefined);
 formData.temperature = instance.temperature ?? 0.7;
 formData.maxTokens = instance.maxTokens ?? 2048;
 formData.topP = instance.topP ?? 1.0;
 formData.topK = instance.topK;
 showAddModal.value = true;
}
function onModelSelect(modelKey: string) {
 const model = props.availableModels.find((m) => m.modelKey === modelKey);
 if (model) {
 formData.modelKey = modelKey;
 }
}
function handleModalOk() {
 const newInstance: AiInstance = {
 instanceName: formData.instanceName,
 modelKey: formData.modelKey,
 routeStrategy: formData.routeStrategy,
 routeWeight: formData.routeStrategy === 'weightedRandom' ? formData.routeWeight : undefined,
 status: 'enabled',
 isDefault: 'N',
 };
 if (formData.customParams) {
 newInstance.temperature = formData.temperature;
 newInstance.maxTokens = formData.maxTokens;
 newInstance.topP = formData.topP;
 newInstance.topK = formData.topK;
 }
 if (editingIndex.value >= 0) {
 const newList = [...props.instanceList];
 newList[editingIndex.value] = {...newList[editingIndex.value], ...newInstance};
 emit('update:instanceList', newList);
 } else {
 emit('update:instanceList', [...props.instanceList, newInstance]);
 }
 showAddModal.value = false;
 editingIndex.value = -1;
 formData.instanceName = '';
 formData.modelKey = '';
 formData.routeStrategy = 'roundRobin';
 formData.routeWeight = 10;
 formData.customParams = false;
 formData.temperature = 0.7;
 formData.maxTokens = 2048;
 formData.topP = 1.0;
 formData.topK = undefined;
}
</script>

<style scoped>
.instance-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.instance-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  border: 1px dashed var(--border-subtle);
  border-radius: var(--radius-lg);
}

.empty-icon {
  font-size: 48px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.instance-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.instance-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-subtle);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.instance-index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-actions {
  display: flex;
  gap: 4px;
}

.card-content {
  padding: 14px 16px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.card-content .label {
  font-size: 11px;
  color: var(--text-muted);
  margin-right: 6px;
}

.card-content .value {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-advanced {
  padding: 14px 16px;
  background: var(--bg-surface);
  border-top: 1px solid var(--border-subtle);
}

.param-section,
.resilience-section {
  margin-bottom: 16px;
}

.param-section:last-child,
.resilience-section:last-child {
  margin-bottom: 0;
}

.param-title,
.resilience-title {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 12px;
}

.param-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-item label {
  font-size: 11px;
  color: var(--text-secondary);
}

.resilience-content {
  width: 100%;
}

.resilience-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-label {
  font-size: 12px;
  color: var(--text-secondary);
  width: 80px;
}

.nested-fields {
  display: flex;
  gap: 8px;
  flex: 1;
}

.nested-fields :deep(.ant-input-number) {
  width: 120px;
}

.card-footer {
  padding: 8px 16px;
  border-top: 1px solid var(--border-subtle);
}

.modal-form {
  max-height: 60vh;
  overflow-y: auto;
}

.param-section-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
</style>