<template>
  <a-menu @click="onMenuClick">
    <a-menu-item key="edit">
      <template #icon>
        <EditOutlined/>
      </template>
      {{ t.vectorCenter.contextMenu.editDatabase }}
    </a-menu-item>
    <a-menu-divider/>
    <a-menu-item key="delete" class="danger-item">
      <template #icon>
        <DeleteOutlined/>
      </template>
      {{ t.vectorCenter.contextMenu.deleteDatabase }}
    </a-menu-item>
  </a-menu>
</template>

<script lang="ts" setup>
import {DeleteOutlined, EditOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const emit = defineEmits<{
  edit: []
  delete: []
}>()

const onMenuClick = ({key}: { key: string }) => {
  switch (key) {
    case 'edit':
      emit('edit')
      break
    case 'delete':
      emit('delete')
      break
  }
}
</script>
