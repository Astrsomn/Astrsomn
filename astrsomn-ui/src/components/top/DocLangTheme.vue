<template>
  <div class="doc-lang-theme">
    <a v-if="showDoc" class="doc-link" href="https://doc.astrsomn.com/" target="_blank">
      <book-outlined/>
    </a>

    <a-select
        :bordered="false"
        :options="languageOptions"
        :value="currentLang"
        class="lang-select"
        option-label-prop="label"
        size="small"
        @change="changeLang"
    />

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

<script lang="ts" setup>

import {BookOutlined} from '@ant-design/icons-vue'
import {useTheme} from '../../composables/useTheme'
import {useLanguage} from '../../composables/useLanguage'

const props = defineProps<{
  showDoc?: boolean
  docTo?: string
}>()

const showDoc = props.showDoc ?? true


const {isDark, toggleTheme} = useTheme()
const {currentLang, changeLang, languageOptions} = useLanguage()


</script>

<style scoped>
.doc-lang-theme {
  display: flex;
  align-items: center;
  gap: 8px;
}

.doc-link {
  color: var(--text-secondary);
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.doc-link :deep(.anticon) {
  color: var(--text-secondary);
}

.doc-link:hover {
  color: var(--text-primary);
}

.doc-link:hover :deep(.anticon) {
  color: var(--text-primary);
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
  color: var(--text-primary);
}
</style>

