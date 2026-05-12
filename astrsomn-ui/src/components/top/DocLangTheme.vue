<template>
  <div class="doc-lang-theme">
    <a v-if="showDoc" href="https://doc.astrsomn.com/" class="doc-link" target="_blank">
      <book-outlined />
    </a>

    <div v-if="showDoc" class="th-divider" />

    <a-select
      :value="currentLang"
      size="small"
      class="lang-select"
      :options="languageOptions"
      @change="changeLang"
      :bordered="false"
      option-label-prop="label"
    />

    <div class="th-divider" />

    <button class="theme-toggle" @click="toggleTheme(!isDark)">
      <template v-if="isDark">🌙</template>
      <template v-else>☀️</template>
    </button>
  </div>
</template>

<script lang="ts">
export default {
  name: 'DocLangTheme',
}
</script>

<script setup lang="ts">

import { BookOutlined } from '@ant-design/icons-vue'
import { useTheme } from '../../composables/useTheme'
import { useLanguage } from '../../composables/useLanguage'

const props = defineProps<{
  showDoc?: boolean
  docTo?: string
}>()

const showDoc = props.showDoc ?? true



const { isDark, toggleTheme } = useTheme()
const { currentLang, changeLang, languageOptions } = useLanguage()


</script>

<style scoped>
.doc-lang-theme {
  display: flex;
  align-items: center;
  gap: 0;
}

.doc-link {
  color: var(--text-secondary);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.doc-link:hover {
  font-weight: bolder;
  color: var(--text-hover);
}

.th-divider {
  width: 1px;
  height: 14px;
  background: var(--border-default);
  margin: 0 12px;
}

.lang-select {
  width: 56px;
  color: var(--text-primary);
}

.lang-select :deep(.ant-select-selection-item) {
  font-size: 16px !important;
  height: auto !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  padding: 4px 8px !important;
  color: var(--text-secondary) !important;
}

.lang-select :deep(.ant-select-dropdown-menu-item) {
  font-size: 16px !important;
  padding: 8px 12px !important;
  text-align: center !important;
  color: var(--text-secondary) !important;
}

.theme-toggle {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  color: var(--text-secondary);
  transition: color 0.2s ease;
}

.theme-toggle:hover {
  color: var(--text-hover);
}
</style>

