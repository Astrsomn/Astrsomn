<script setup lang="ts">
import ConsolePageModuleShell from './ConsolePageModuleShell.vue'
import type { ManagementEntry } from '../../resource-library/management.ts'

const PAGE_KIND = 'AiDocument' as const

defineProps<{
  gridW: number
  gridH: number
  editMode?: boolean
  entryByRoute?: Partial<Record<string, ManagementEntry>>
  route: string
}>()
</script>
<template>
  <ConsolePageModuleShell
    :page-kind="PAGE_KIND"
    :route="route"
    :grid-w="gridW"
    :grid-h="gridH"
    :edit-mode="editMode"
    :entry-by-route="entryByRoute"
  />
</template>
