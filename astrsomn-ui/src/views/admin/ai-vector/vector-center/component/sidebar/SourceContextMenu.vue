<template>
  <a-menu @click="onMenuClick">
    <a-menu-item key="addDb">
      <template #icon>
        <PlusOutlined/>
      </template>
      {{ t.vectorCenter.contextMenu.addDatabase }}
    </a-menu-item>
    <a-menu-item key="edit">
      <template #icon>
        <EditOutlined/>
      </template>
      {{ t.vectorCenter.contextMenu.editSource }}
    </a-menu-item>
    <a-menu-divider/>
    <a-menu-item key="delete" class="danger-item">
      <template #icon>
        <DeleteOutlined/>
      </template>
      {{ t.vectorCenter.contextMenu.deleteSource }}
    </a-menu-item>
  </a-menu>
</template>

<script lang="ts" setup>
import {DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const emit = defineEmits<{
  'add-db': []
  edit: []
  delete: []
}>()

const onMenuClick = ({key}: { key: string }) => {
  switch (key) {
    case 'addDb':
      emit('add-db')
      break
    case 'edit':
      emit('edit')
      break
    case 'delete':
      emit('delete')
      break
  }
}
</script>
