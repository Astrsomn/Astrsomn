<script setup lang="ts">
import HomeTemplateCard from '../HomeTemplateCard.vue'
import type { ManagementEntry } from '../../../resource-library/management.ts'

const PAGE_KIND = 'AiTemplate' as const

defineProps<{
  gridW: number
  gridH: number
  editMode?: boolean
  entryByRoute?: Partial<Record<string, ManagementEntry>>
  route: string
}>()
</script>
<template>
  <HomeTemplateCard
    :page-kind="PAGE_KIND"
    :route="route"
    :grid-w="gridW"
    :grid-h="gridH"
    :edit-mode="editMode"
    :entry-by-route="entryByRoute"
  />
</template>
