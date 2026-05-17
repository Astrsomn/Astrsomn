<template>
  <a-menu @click="onMenuClick">
    <a-menu-item key="addDb">
      <template #icon>
        <PlusOutlined/>
      </template>
      新增数据库
    </a-menu-item>
    <a-menu-item key="edit">
      <template #icon>
        <EditOutlined/>
      </template>
      编辑数据源
    </a-menu-item>
    <a-menu-divider/>
    <a-menu-item key="delete" class="danger-item">
      <template #icon>
        <DeleteOutlined/>
      </template>
      删除数据源
    </a-menu-item>
  </a-menu>
</template>

<script lang="ts" setup>
import {DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue'

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
