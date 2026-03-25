<template>
  <div class="doc-lang-theme">
    <a v-if="showDoc" href="javascript:;" class="doc-link" @click.prevent="goDoc">
      <book-outlined />
      <span>文档</span>
    </a>

    <div v-if="showDoc" class="th-divider" />

    <a-select
      :value="currentLang"
      size="small"
      class="lang-select"
      :options="languageOptions"
      @change="changeLang"
      :bordered="false"
    />

    <div class="th-divider" />

    <button class="theme-toggle" @click="toggleTheme(!isDark)">
      <template v-if="isDark">🌙</template>
      <template v-else>☀️</template>
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { BookOutlined } from '@ant-design/icons-vue'
import { useTheme } from '@/composables/useTheme'
import { useLanguage } from '@/composables/useLanguage'

const props = defineProps<{
  showDoc?: boolean
  docTo?: string
}>()

const showDoc = props.showDoc ?? true
const docTo = props.docTo ?? '/admin/documents'

const router = useRouter()
const { isDark, toggleTheme } = useTheme()
const { currentLang, changeLang, languageOptions } = useLanguage()

const goDoc = () => {
  router.push(docTo)
}
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
}

.th-divider {
  width: 1px;
  height: 14px;
  background: var(--border-default);
  margin: 0 12px;
}

.lang-select {
  width: 90px;
}

.theme-toggle {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  color: var(--text-secondary);
}
</style>

