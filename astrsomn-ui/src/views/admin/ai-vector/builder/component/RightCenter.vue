<template>
  <div class="document-list-container">
    <div class="list-toolbar">
      <div class="toolbar-left">
        <h2 class="section-title">文档列表</h2>
        <div class="count-badge">{{ files.length }}</div>
      </div>
      
      <div class="toolbar-right">
        <a-input-search 
          placeholder="搜索文件名..." 
          class="subtle-search"
        />
        <a-button type="primary" class="import-btn">
          <template #icon><plus-outlined /></template>
          导入文档
        </a-button>
      </div>
    </div>

    <div class="file-grid">
      <FileCard 
        v-for="(file, index) in files" 
        :key="index" 
        :file="file" 
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { PlusOutlined } from '@ant-design/icons-vue';
// 确保路径指向你刚才保存 FileCard 的位置
import FileCard from './right-center/FileCard.vue'; 

const files = ref([
  { name: "产品功能矩阵.pdf", segments: 156, size: "3.2MB", status: "已向量化", uploadTime: "2024-03-15" },
  { name: "开发者集成手册.md", segments: 42, size: "125KB", status: "待向量化", uploadTime: "2024-03-14" },
  { name: "2024Q1财报摘要.xlsx", segments: 89, size: "1.1MB", status: "已向量化", uploadTime: "2024-03-13" },
  { name: "品牌设计规范.pdf", segments: 34, size: "12.4MB", status: "待向量化", uploadTime: "2024-03-12" }
]);
</script>

<style lang="less" scoped>
@text-main: #262626;
@text-secondary: #8c8c8c;

.document-list-container {
  padding: 0 24px 24px 24px;
  background: transparent;
}

/* 工具栏样式 */
.list-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;
    .section-title { font-size: 16px; font-weight: 600; color: @text-main; margin: 0; }
    .count-badge { background: #f5f5f5; color: @text-secondary; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
  }

  .toolbar-right {
    display: flex;
    gap: 12px;
    .subtle-search {
      width: 240px;
      :deep(.ant-input) { border-radius: 8px; border-color: #f0f0f0; background: #fcfcfc; }
    }
    .import-btn { border-radius: 8px; font-weight: 500; }
  }
}

/* 布局网格：负责卡片的大小和排列 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
</style>