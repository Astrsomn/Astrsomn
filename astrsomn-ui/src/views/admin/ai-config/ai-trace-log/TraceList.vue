<template>
  <AstPageShell
      :description="t.list.description"
      :empty-text="t.list.emptyText"
      :title="t.list.title"
  />
</template>

<script lang="ts" setup>
import AstPageShell from '@/components/home/AstPageShell.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-trace-log')
</script>
