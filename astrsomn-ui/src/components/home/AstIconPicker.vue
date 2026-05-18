<template>
  <div class="avatar-picker-wrapper">
    <div :style="{ width: size + 'px', height: size + 'px' }" class="avatar-trigger" @click="modalOpen = true">
      <div v-if="modelValue && modelValue.startsWith('data:image')" :style="{ width: size + 'px', height: size + 'px' }" class="avatar-img-wrap">
        <img :src="modelValue" alt="avatar" class="avatar-img"/>
      </div>
      <div v-else-if="modelValue && iconMap[modelValue]" :style="{ width: size + 'px', height: size + 'px', fontSize: size * 0.5 + 'px' }" class="avatar-icon-wrap">
        <component :is="iconMap[modelValue]"/>
      </div>
      <div v-else :style="{ width: size + 'px', height: size + 'px', fontSize: size * 0.45 + 'px' }" class="avatar-fallback">
        {{ fallbackChar }}
      </div>
      <div class="avatar-overlay">
        <EditOutlined/>
      </div>
    </div>

    <a-modal
        v-model:open="modalOpen"
        :footer="null"
        title="选择头像"
        width="520px"
        @cancel="onCancel"
    >
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="icon" tab="图标选择">
          <div class="icon-search">
            <a-input
                v-model:value="searchText"
                allow-clear
                placeholder="搜索图标..."
                size="small"
            >
              <template #prefix>
                <SearchOutlined/>
              </template>
            </a-input>
          </div>
          <div class="icon-grid">
            <div
                v-for="icon in filteredIcons"
                :key="icon"
                :class="{ 'icon-item-selected': modelValue === icon }"
                class="icon-item"
                @click="onSelectIcon(icon)"
            >
              <component :is="iconMap[icon]"/>
            </div>
            <div v-if="filteredIcons.length === 0" class="icon-empty">无匹配图标</div>
          </div>
        </a-tab-pane>

        <a-tab-pane key="upload" tab="图片上传">
          <div class="upload-area">
            <a-upload
                :before-upload="onBeforeUpload"
                :max-count="1"
                :show-upload-list="false"
                accept="image/*"
            >
              <div class="upload-trigger">
                <PlusOutlined/>
                <div class="upload-text">点击上传图片</div>
                <div class="upload-hint">支持 JPG、PNG、GIF、SVG，自动转为 Base64</div>
              </div>
            </a-upload>
            <div v-if="modelValue && modelValue.startsWith('data:image')" class="upload-preview">
              <img :src="modelValue" alt="preview" class="preview-img"/>
              <a-button danger size="small" @click="onClear">移除图片</a-button>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>

      <div v-if="modelValue" class="modal-footer-actions">
        <a-button size="small" @click="onClear">清除头像</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {
  EditOutlined,
  PlusOutlined,
  SearchOutlined,
  RobotOutlined,
  RocketOutlined,
  ThunderboltOutlined,
  StarOutlined,
  HeartOutlined,
  FireOutlined,
  BulbOutlined,
  ApiOutlined,
  CloudOutlined,
  CodeOutlined,
  BugOutlined,
  ToolOutlined,
  SettingOutlined,
  SafetyOutlined,
  LockOutlined,
  KeyOutlined,
  DatabaseOutlined,
  CloudServerOutlined,
  DesktopOutlined,
  MobileOutlined,
  TabletOutlined,
  GlobalOutlined,
  EnvironmentOutlined,
  CompassOutlined,
  FlagOutlined,
  CrownOutlined,
  TrophyOutlined,
  GiftOutlined,
  BellOutlined,
  SoundOutlined,
  AudioOutlined,
  VideoCameraOutlined,
  CameraOutlined,
  PictureOutlined,
  FileOutlined,
  FolderOutlined,
  BookOutlined,
  ReadOutlined,
  EditFilled,
  FormOutlined,
  HighlightOutlined,
  FontSizeOutlined,
  TranslationOutlined,
  SmileOutlined,
  MehOutlined,
  FrownOutlined,
  UserOutlined,
  TeamOutlined,
  SolutionOutlined,
  ContactsOutlined,
  CustomerServiceOutlined,
  CommentOutlined,
  MessageOutlined,
  MailOutlined,
  SendOutlined,
  ShareAltOutlined,
  LinkOutlined,
  BranchesOutlined,
  NodeIndexOutlined,
  ApartmentOutlined,
  ClusterOutlined,
  DeploymentUnitOutlined,
  ExperimentOutlined,
  FundOutlined,
  LineChartOutlined,
  PieChartOutlined,
  BarChartOutlined,
  CalculatorOutlined,
  DashboardOutlined,
  MonitorOutlined,
  RadarChartOutlined,
  StockOutlined,
  AimOutlined,
  AlertOutlined,
  CheckCircleOutlined,
  InfoCircleOutlined,
  QuestionCircleOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined,
  SyncOutlined,
  ReloadOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  StopOutlined,
  ForwardOutlined,
  BackwardOutlined,
  SwapOutlined,
  FilterOutlined,
  SortAscendingOutlined,
} from '@ant-design/icons-vue'

const props = withDefaults(defineProps<{
  modelValue?: string
  name?: string
  size?: number
}>(), {
  modelValue: '',
  name: '',
  size: 64,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const modalOpen = ref(false)
const activeTab = ref('icon')
const searchText = ref('')

const iconMap: Record<string, any> = {
  RobotOutlined,
  RocketOutlined,
  ThunderboltOutlined,
  StarOutlined,
  HeartOutlined,
  FireOutlined,
  BulbOutlined,
  ApiOutlined,
  CloudOutlined,
  CodeOutlined,
  BugOutlined,
  ToolOutlined,
  SettingOutlined,
  SafetyOutlined,
  LockOutlined,
  KeyOutlined,
  DatabaseOutlined,
  CloudServerOutlined,
  DesktopOutlined,
  MobileOutlined,
  TabletOutlined,
  GlobalOutlined,
  EnvironmentOutlined,
  CompassOutlined,
  FlagOutlined,
  CrownOutlined,
  TrophyOutlined,
  GiftOutlined,
  BellOutlined,
  SoundOutlined,
  AudioOutlined,
  VideoCameraOutlined,
  CameraOutlined,
  PictureOutlined,
  FileOutlined,
  FolderOutlined,
  BookOutlined,
  ReadOutlined,
  FormOutlined,
  HighlightOutlined,
  FontSizeOutlined,
  TranslationOutlined,
  SmileOutlined,
  MehOutlined,
  FrownOutlined,
  UserOutlined,
  TeamOutlined,
  SolutionOutlined,
  ContactsOutlined,
  CustomerServiceOutlined,
  CommentOutlined,
  MessageOutlined,
  MailOutlined,
  SendOutlined,
  ShareAltOutlined,
  LinkOutlined,
  BranchesOutlined,
  NodeIndexOutlined,
  ApartmentOutlined,
  ClusterOutlined,
  DeploymentUnitOutlined,
  ExperimentOutlined,
  FundOutlined,
  LineChartOutlined,
  PieChartOutlined,
  BarChartOutlined,
  CalculatorOutlined,
  DashboardOutlined,
  MonitorOutlined,
  RadarChartOutlined,
  StockOutlined,
  AimOutlined,
  AlertOutlined,
  CheckCircleOutlined,
  InfoCircleOutlined,
  QuestionCircleOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined,
  SyncOutlined,
  ReloadOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  StopOutlined,
  ForwardOutlined,
  BackwardOutlined,
  SwapOutlined,
  FilterOutlined,
  SortAscendingOutlined,
}

const allIconNames = Object.keys(iconMap)

const filteredIcons = computed(() => {
  const q = searchText.value.trim().toLowerCase()
  if (!q) return allIconNames
  return allIconNames.filter((name) => name.toLowerCase().includes(q))
})

const fallbackChar = computed(() => {
  const n = props.name?.trim()
  if (!n) return 'A'
  return n.charAt(0).toUpperCase()
})

function onSelectIcon(iconName: string) {
  emit('update:modelValue', iconName)
  modalOpen.value = false
}

function onBeforeUpload(file: File) {
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target?.result as string
    emit('update:modelValue', result)
  }
  reader.readAsDataURL(file)
  return false
}

function onClear() {
  emit('update:modelValue', '')
}

function onCancel() {
  searchText.value = ''
}
</script>

<style scoped>
.avatar-picker-wrapper {
  display: inline-flex;
}

.avatar-trigger {
  position: relative;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  border: 2px dashed var(--border-default);
  transition: border-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-input, #f5f5f5);
}

.avatar-trigger:hover {
  border-color: var(--primary);
}

.avatar-trigger:hover .avatar-overlay {
  opacity: 1;
}

.avatar-img-wrap {
  border-radius: 50%;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(59, 130, 246, 0.15));
  border-radius: 50%;
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  user-select: none;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.2s;
  border-radius: 50%;
}

/* Icon grid */
.icon-search {
  margin-bottom: 12px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 6px;
  max-height: 320px;
  overflow-y: auto;
  padding: 2px;
}

.icon-item {
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1;
  border-radius: 8px;
  cursor: pointer;
  font-size: 20px;
  color: var(--text-secondary);
  transition: all 0.15s;
  border: 2px solid transparent;
  background: var(--bg-input, #fafafa);
}

.icon-item:hover {
  color: var(--primary);
  background: rgba(59, 130, 246, 0.08);
  border-color: var(--primary);
}

.icon-item-selected {
  color: var(--primary);
  border-color: var(--primary);
  background: rgba(59, 130, 246, 0.12);
}

.icon-empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 32px 0;
  color: var(--text-muted);
  font-size: 13px;
}

/* Upload area */
.upload-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 280px;
  height: 160px;
  border: 2px dashed var(--border-default);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-muted);
}

.upload-trigger:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
}

.upload-hint {
  font-size: 12px;
  opacity: 0.7;
}

.upload-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.preview-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid var(--border-default);
}

.modal-footer-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-subtle, #f0f0f0);
}
</style>
