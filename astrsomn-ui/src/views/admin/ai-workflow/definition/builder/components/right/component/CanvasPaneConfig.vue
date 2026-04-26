<template>
  <div class="pane-config">
    <div class="config-head">
      <div class="head-left">
        <SettingOutlined />
        <h4>画布配置</h4>
      </div>
    </div>

    <div class="config-body">
      <a-form layout="vertical" size="small">
        <div class="section">
          <label class="section-label">默认连线样式</label>
          <div class="option-grid option-grid-3">
            <button
              v-for="item in edgeStyleOptions"
              :key="item.value"
              type="button"
              class="option-card"
              :class="{ active: canvasConfig.edgeStyleDefault === item.value }"
              @click="onChangeEdgeDefault(item.value)"
            >
              {{ item.label }}
            </button>
          </div>
          <a-button class="apply-btn" block @click="emit('apply-edge-style-all', canvasConfig.edgeStyleDefault)">应用到现有连线</a-button>
        </div>

        <a-divider class="section-divider" />

        <div class="section">
          <label class="section-label">背景样式</label>
          <div class="option-grid option-grid-4">
            <button
              v-for="item in backgroundOptions"
              :key="item.value"
              type="button"
              class="option-card"
              :class="{ active: canvasConfig.backgroundVariant === item.value }"
              @click="onChangeBackgroundVariant(item.value)"
            >
              {{ item.label }}
            </button>
          </div>

          <div class="color-row">
            <div class="color-item">
              <label class="mini-label">背景颜色</label>
              <div class="color-input-wrap">
                <input class="color-picker" type="color" :value="normalizeHex(canvasConfig.backgroundColor)" @input="onBackgroundColorPick" />
                <a-input size="small" :value="canvasConfig.backgroundColor" placeholder="#f8fafc" @update:value="onBackgroundColorChange" />
              </div>
            </div>
            <div class="color-item">
              <label class="mini-label">点线颜色</label>
              <div class="color-input-wrap">
                <input class="color-picker" type="color" :value="normalizeHex(canvasConfig.patternColor)" @input="onPatternColorPick" />
                <a-input size="small" :value="canvasConfig.patternColor" placeholder="#94a3b8" @update:value="onPatternColorChange" />
              </div>
            </div>
          </div>

          <div class="slider-wrap">
            <div class="slider-head">
              <label class="section-label">网格间距</label>
              <span class="slider-value">{{ canvasConfig.patternGap }}px（固定档位）</span>
            </div>
            <div class="option-grid option-grid-5">
              <button
                v-for="gap in fixedGapOptions"
                :key="gap"
                type="button"
                class="option-card"
                :class="{ active: canvasConfig.patternGap === gap }"
                @click="onSelectFixedGap(gap)"
              >
                {{ gap }}
              </button>
            </div>
          </div>

          <div class="slider-wrap">
            <div class="slider-head">
              <label class="section-label">点线粗细</label>
              <span class="slider-value">{{ canvasConfig.patternSize.toFixed(1) }}</span>
            </div>
            <a-slider :min="1" :max="8" :step="0.2" :value="canvasConfig.patternSize" @change="onPatternSizeChange" />
          </div>

          <div class="switch-row">
            <label class="section-label">显示坐标原点</label>
            <a-switch :checked="canvasConfig.showOriginMarker" @change="onShowOriginChange" />
          </div>

          <div class="snap-block">
            <div class="switch-row">
              <label class="section-label">节点磁吸</label>
              <a-switch :checked="canvasConfig.snapToGridEnabled" @change="onSnapEnabledChange" />
            </div>
            <div class="snap-hint">当前磁吸网格：{{ canvasConfig.snapGridSize }}px</div>
          </div>
        </div>

        <a-divider class="section-divider" />

        <div class="section">
          <div class="preset-head">
            <label class="section-label">快速预设（本地）</label>
            <a-button size="small" @click="saveCurrentAsPreset">保存当前</a-button>
          </div>
          <a-input size="small" :value="presetNameDraft" maxlength="24" placeholder="输入预设名称后保存" @update:value="onPresetDraftChange" />
          <div class="preset-list">
            <div v-for="preset in allPresets" :key="preset.key" class="preset-item">
              <a-button size="small" @click="applyPreset(preset.key)">{{ preset.label }}</a-button>
              <button v-if="preset.isCustom" type="button" class="preset-remove" title="删除预设" @click="removeCustomPreset(preset.key)">×</button>
            </div>
          </div>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import { SettingOutlined } from '@ant-design/icons-vue'
import type { CanvasBackgroundVariant, CanvasConfig, CanvasEdgeStyle } from '../../../domain/types'

const props = defineProps<{
  canvasConfig: CanvasConfig
}>()

const emit = defineEmits<{
  'update-canvas-config': [payload: Partial<CanvasConfig>]
  'apply-edge-style-all': [edgeStyle: CanvasEdgeStyle]
}>()

const edgeStyleOptions: Array<{ label: string; value: CanvasEdgeStyle }> = [
  { label: '曲线', value: 'default' },
  { label: '直线', value: 'straight' },
  { label: '折线', value: 'step' }
]

const backgroundOptions: Array<{ label: string; value: CanvasBackgroundVariant }> = [
  { label: '点阵', value: 'dots' },
  { label: '网格', value: 'lines' },
  { label: '十字', value: 'cross' },
  { label: '无', value: 'none' }
]
const fixedGapOptions = [8, 16, 24, 32, 48]

type LocalPreset = {
  key: string
  label: string
  config: CanvasConfig
  isCustom?: boolean
}

const PRESET_STORAGE_KEY = 'astrsomn-workflow-canvas-presets'

const builtinPresets: LocalPreset[] = [
  {
    key: 'classic',
    label: '经典',
    config: {
      edgeStyleDefault: 'default',
      backgroundVariant: 'dots',
      backgroundColor: '#f8fafc',
      patternColor: '#94a3b8',
      patternGap: 22,
      patternSize: 1.8,
      showOriginMarker: true,
      snapToGridEnabled: true,
      snapGridSize: 24
    }
  },
  {
    key: 'focus',
    label: '聚焦',
    config: {
      edgeStyleDefault: 'step',
      backgroundVariant: 'lines',
      backgroundColor: '#f7fafc',
      patternColor: '#cbd5e1',
      patternGap: 26,
      patternSize: 1.2,
      showOriginMarker: true,
      snapToGridEnabled: true,
      snapGridSize: 24
    }
  },
  {
    key: 'minimal',
    label: '极简',
    config: {
      edgeStyleDefault: 'straight',
      backgroundVariant: 'none',
      backgroundColor: '#ffffff',
      patternColor: '#e2e8f0',
      patternGap: 20,
      patternSize: 1.2,
      showOriginMarker: false,
      snapToGridEnabled: true,
      snapGridSize: 24
    }
  }
]

const presetNameDraft = ref('')
const customPresets = ref<LocalPreset[]>(loadCustomPresets())
const allPresets = computed<LocalPreset[]>(() => [...builtinPresets, ...customPresets.value])

function loadCustomPresets(): LocalPreset[] {
  try {
    const raw = localStorage.getItem(PRESET_STORAGE_KEY)
    if (!raw) return []
    const parsed = JSON.parse(raw) as LocalPreset[]
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function persistCustomPresets() {
  localStorage.setItem(PRESET_STORAGE_KEY, JSON.stringify(customPresets.value))
}

const onChangeEdgeDefault = (value: CanvasEdgeStyle) => {
  emit('update-canvas-config', { edgeStyleDefault: value })
  emit('apply-edge-style-all', value)
}

const onChangeBackgroundVariant = (value: CanvasBackgroundVariant) => {
  emit('update-canvas-config', { backgroundVariant: value })
}

const onBackgroundColorChange = (value: string) => {
  emit('update-canvas-config', { backgroundColor: value || '#f8fafc' })
}

const onPatternColorChange = (value: string) => {
  emit('update-canvas-config', { patternColor: value || '#94a3b8' })
}

const onBackgroundColorPick = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  emit('update-canvas-config', { backgroundColor: value || '#f8fafc' })
}

const onPatternColorPick = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  emit('update-canvas-config', { patternColor: value || '#94a3b8' })
}

const onSelectFixedGap = (gap: number) => {
  emit('update-canvas-config', { patternGap: gap, snapGridSize: gap })
}

const onPatternSizeChange = (value: number | [number, number]) => {
  emit('update-canvas-config', { patternSize: Array.isArray(value) ? Number(value[0]) : Number(value) })
}

const onShowOriginChange = (checked: boolean) => {
  emit('update-canvas-config', { showOriginMarker: checked })
}

const onSnapEnabledChange = (checked: boolean) => {
  emit('update-canvas-config', { snapToGridEnabled: checked })
}

const onPresetDraftChange = (value: string) => {
  presetNameDraft.value = value || ''
}

const normalizeHex = (value: string) => {
  const text = (value || '').trim()
  if (/^#[0-9a-fA-F]{6}$/.test(text)) return text
  return '#94a3b8'
}

const saveCurrentAsPreset = () => {
  const name = presetNameDraft.value.trim()
  if (!name) {
    message.warning('请先输入预设名称')
    return
  }
  const key = `custom-${Date.now()}`
  customPresets.value = [
    ...customPresets.value,
    {
      key,
      label: name,
      config: { ...props.canvasConfig },
      isCustom: true
    }
  ]
  persistCustomPresets()
  presetNameDraft.value = ''
  message.success('预设已保存到本地')
}

const applyPreset = (presetKey: string) => {
  const target = allPresets.value.find((x) => x.key === presetKey)
  if (!target) return
  emit('update-canvas-config', { ...target.config })
  emit('apply-edge-style-all', target.config.edgeStyleDefault)
}

const removeCustomPreset = (presetKey: string) => {
  customPresets.value = customPresets.value.filter((x) => x.key !== presetKey)
  persistCustomPresets()
  message.success('预设已删除')
}
</script>

<style scoped>
.pane-config {
  margin: 0 -12px -12px;
  height: calc(100% + 24px);
  display: flex;
  flex-direction: column;
  background: #fff;
}

.config-head {
  padding: 14px 14px 12px;
  border-bottom: 1px solid #edf2f7;
  background: rgba(248, 250, 252, 0.7);
}

.head-left {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.head-left h4 {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.config-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 14px;
}

.section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.mini-label {
  font-size: 11px;
  color: #94a3b8;
}

.option-grid {
  display: grid;
  gap: 8px;
}

.option-grid-3 {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.option-grid-4 {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.option-grid-5 {
  grid-template-columns: repeat(5, minmax(0, 1fr));
}

.option-card {
  border: 1px solid #dbe3ee;
  border-radius: 8px;
  background: #fff;
  height: 34px;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s ease;
}

.option-card:hover {
  border-color: #93c5fd;
  color: #2563eb;
}

.option-card.active {
  border-color: #60a5fa;
  color: #1d4ed8;
  background: #eef6ff;
  font-weight: 600;
}

.apply-btn {
  height: 32px;
  border-radius: 8px;
  border-color: #e2e8f0;
  color: #475569;
}

.apply-btn:hover {
  border-color: #93c5fd;
  color: #2563eb;
}

.section-divider {
  margin: 14px 0;
  border-color: #f1f5f9;
}

.color-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.color-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.color-input-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}

.color-picker {
  width: 28px;
  height: 28px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 2px;
  background: #fff;
  cursor: pointer;
}

.slider-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.slider-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.slider-value {
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  color: #3b82f6;
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.snap-block {
  margin-top: 2px;
  padding: 8px 10px;
  border: 1px dashed #dbe3ee;
  border-radius: 8px;
}

.snap-hint {
  margin-top: 6px;
  font-size: 11px;
  color: #64748b;
}

.preset-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.preset-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.preset-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.preset-item :deep(.ant-btn) {
  border: 0;
  background: #f1f5f9;
  color: #475569;
  border-radius: 6px;
}

.preset-item :deep(.ant-btn:hover) {
  background: #e2e8f0;
  color: #334155;
}

.preset-remove {
  width: 18px;
  height: 18px;
  border: 0;
  border-radius: 50%;
  background: #fee2e2;
  color: #b91c1c;
  line-height: 18px;
  text-align: center;
  cursor: pointer;
}

:deep(.ant-form-item) {
  margin-bottom: 0;
}

:deep(.ant-slider .ant-slider-handle::after) {
  box-shadow: 0 0 0 2px #2563eb;
}
</style>
