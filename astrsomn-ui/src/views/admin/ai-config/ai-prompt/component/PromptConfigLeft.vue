<template>
  <div class="config-side">


    <!-- Prompt Key -->
    <a-form-item :label="t.configLeft.promptKeyLabel" name="promptKey">
      <div class="key-input-wrapper">
        <span class="key-prefix">{{ AI_PROMPT_KEY_PREFIX }}</span>
        <a-input
            :value="form.promptKey"
            readonly
            :placeholder="t.configLeft.promptKeyPlaceholder"
            class="key-input"
        />
        <a-button type="text" class="key-refresh-btn" @click="generateRandomKey">
          <ReloadOutlined class="w-4 h-4"/>
        </a-button>
      </div>
    </a-form-item>

    <!-- 标题 -->
    <a-form-item :label="t.configLeft.titleLabel" name="promptTitle">
      <a-input
          v-model:value="form.promptTitle"
          :placeholder="t.configLeft.titlePlaceholder"
          size="large"
          class="config-input"
      />
    </a-form-item>


    <!-- 状态 -->
    <a-form-item :label="t.configLeft.statusLabel" name="status">
      <div class="status-toggle">
        <div class="status-toggle-bg" :class="{ active: form.status === 'enabled' }"/>
        <a-button
            type="text"
            class="status-btn"
            :class="{ active: form.status === 'enabled' }"
            @click="form.status = 'enabled'"
        >
          <span class="status-dot enabled"></span>
          {{ t.configLeft.statusOptions.enabled }}
        </a-button>
        <a-button
            type="text"
            class="status-btn"
            :class="{ active: form.status === 'disabled' }"
            @click="form.status = 'disabled'"
        >
          <span class="status-dot disabled"></span>
          {{ t.configLeft.statusOptions.disabled }}
        </a-button>
      </div>
    </a-form-item>

    <!-- 场景标签 -->
    <a-form-item :label="t.configLeft.sceneLabel" name="scene">
      <div class="tag-container-wrapper">
        <div class="tag-container">
          <div
              v-for="(tag, index) in sceneTags"
              :key="index"
              class="tag-item"
          >
            {{ tag }}
            <button type="button" class="tag-remove" @click="removeTag(index)">
              <CloseOutlined class="w-3 h-3"/>
            </button>
          </div>
          <a-input
              v-model:value="newTag"
              type="text"
              class="tag-input"
              placeholder="输入标签..."
              @keydown="handleTagInput"
          />
        </div>
        <div class="preset-tags">
          <span class="preset-label">{{ t.configLeft.presetTagsLabel }}</span>
          <div class="preset-list">
            <a-button
                v-for="tag in presetTags"
                :key="tag"
                type="text"
                class="preset-tag-btn"
                @click="addPresetTag(tag)"
            >
              {{ tag }}
            </a-button>
          </div>
        </div>
      </div>
    </a-form-item>

  </div>
</template>

<script lang="ts" setup>import {ref, watch} from 'vue';
import {ReloadOutlined, CloseOutlined} from '@ant-design/icons-vue';
import {AI_PROMPT_KEY_PREFIX} from '@/constants/aiConfigKeyPrefixes';
import {usePageTranslation} from '@/locales/pages.ts';
const t = usePageTranslation('ai-prompt');
const props = defineProps<{
 form: Record<string, any>;
 sceneOptions?: Array<{
 label: string;
 value: string;
 }>;
}>();
const emit = defineEmits<{
 (e: 'update:scene', value: string[]): void;
}>();
const newTag = ref('');
const sceneTags = ref<string[]>([]);
const presetTags = ['文案润色', '代码助理', '周报生成', '角色扮演', '数据分析', '创意写作'];
watch(() => props.form.scene, (val) => {
 if (val) {
 sceneTags.value = typeof val === 'string' ? val.split(',').filter(Boolean) : [...val];
 }
}, {immediate: true});
function generateRandomKey() {
 const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
 let randomString = '';
 for (let i = 0; i < 10; i++) {
 randomString += characters.charAt(Math.floor(Math.random() * characters.length));
 }
 props.form.promptKey = 'PROMPT_' + randomString;
}
function handleTagInput(event: KeyboardEvent) {
 const key = event.key;
 if (key === ' ' || key === 'Enter' || key === ',') {
 event.preventDefault();
 const value = newTag.value.trim().replace(/,/g, '');
 if (value && !sceneTags.value.includes(value)) {
 addTag(value);
 newTag.value = '';
 }
 }
}
function addTag(value: string) {
 sceneTags.value.push(value);
 updateSceneValue();
}
function removeTag(index: number) {
 sceneTags.value.splice(index, 1);
 updateSceneValue();
}
function addPresetTag(tagName: string) {
 if (!sceneTags.value.includes(tagName)) {
 sceneTags.value.push(tagName);
 updateSceneValue();
 }
}
function updateSceneValue() {
 props.form.scene = sceneTags.value.join(',');
 emit('update:scene', sceneTags.value);
}
</script>

<style scoped>
.config-side {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 16px;
  border-right: 1px solid var(--border-default);
}

.config-input {
  border-radius: 8px;
  border-color: var(--border-input);
  transition: all 0.2s ease;
}

.config-input:focus {
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary) 20%, transparent);
  border-color: var(--primary);
}

.key-input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid var(--border-input);
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-input);
}

.key-prefix {
  padding: 0 12px;
  font-family: monospace;
  font-size: 14px;
  color: var(--text-muted);
  border-right: 1px solid var(--border-input);
  height: 40px;
  display: flex;
  align-items: center;
  background: var(--bg-elevated);
}

.key-input {
  flex: 1;
  border: none;
  border-radius: 0;
  background: transparent;
}

.key-refresh-btn {
  padding: 0 12px;
  color: var(--primary);
  height: 40px;
  border-left: 1px solid var(--border-input);
}

.key-refresh-btn:hover {
  background: color-mix(in srgb, var(--primary) 10%, transparent);
}

.status-toggle {
  display: flex;
  align-items: center;
  background: var(--bg-elevated);
  border-radius: 8px;
  padding: 2px;
  position: relative;
  height: 44px;
}

.status-toggle-bg {
  position: absolute;
  top: 2px;
  left: 2px;
  right: 50%;
  bottom: 2px;
  background: #fff;
  border-radius: 6px;
  box-shadow: var(--shadow-sm);
  transition: transform 0.3s ease;
}

.status-toggle-bg.active {
  transform: translateX(100%);
}

.status-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.3s ease;
  z-index: 1;
}

.status-btn.active {
  color: var(--primary);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.enabled {
  background: var(--success);
}

.status-dot.disabled {
  background: var(--text-muted);
}

.tag-container-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 8px;
  border: 1px solid var(--border-input);
  border-radius: 8px;
  background: var(--bg-card);
  min-height: 44px;
  align-items: center;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  color: var(--primary);
  border: 1px solid color-mix(in srgb, var(--primary) 20%, transparent);
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.tag-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px;
  border: none;
  background: transparent;
  color: var(--primary);
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s ease;
}

.tag-remove:hover {
  background: color-mix(in srgb, var(--primary) 20%, transparent);
}

.tag-input {
  flex: 1;
  min-width: 80px;
  border: none;
  background: transparent;
  font-size: 13px;
  color: var(--text-primary);
  outline: none;
}

.tag-input::placeholder {
  color: var(--text-muted);
}

.preset-tags {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.preset-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.preset-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.preset-tag-btn {
  padding: 4px 10px;
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-elevated);
  border-radius: 6px;
  transition: all 0.2s ease;
}

.preset-tag-btn:hover {
  color: var(--primary);
  background: color-mix(in srgb, var(--primary) 10%, transparent);
}
</style>
