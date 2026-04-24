<template>
  <div class="sidebar-container">
    <div class="sidebar-header">
      <div class="header-main">
        <database-filled class="header-icon" />
        <span class="header-title">资产目录</span>
      </div>
      <plus-outlined class="add-icon" />
    </div>

    <div class="sidebar-menu-wrapper">
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        :inline-indent="0" 
        class="hierarchy-menu"
      >
        <a-sub-menu v-for="source in sources" :key="source.id" class="level-1-group">
          <template #title>
            <div class="source-card">
              <div class="source-main">
                <div class="source-title">
                  <cluster-outlined class="p-icon" />
                  <span class="p-title">数据源</span>
                </div>
                <div class="source-connection">
                  <span class="p-conn">{{ source.ip }}:{{ source.port }}</span>
                  <span class="p-user">({{ source.user }})</span>
                  <span class="p-tag">{{ source.tag }}</span>
                </div>
              </div>
              <edit-outlined class="edit-icon" />
            </div>
          </template>

          <a-menu-item v-for="db in source.dbs" :key="db.id" class="level-2-item">
            <div class="asset-tree">
              <div class="vertical-guide"></div>
              
              <div class="asset-content">
                <div class="db-node">
                  <div class="db-label">
                    <component :is="getVdbIcon(db.type)" class="v-icon" />
                    <span class="v-name">{{ db.dbName }}</span>
                  </div>
                  <div v-if="db.active" class="active-pulse"></div>
                  <div class="model-info">
                    <div class="model-name">{{ db.modelName }}</div>
                    <div class="model-dim">{{ db.dim }}维</div>
                  </div>
                </div>
              </div>
            </div>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </div>

    <div class="sidebar-footer">
      <div class="footer-row">
        <div class="driver-info">
          <div class="s-avatars">
            <span class="s-av">M</span>
            <span class="s-av">Q</span>
          </div>
          <span class="s-text">驱动就绪</span>
        </div>
        <appstore-outlined class="m-btn" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { 
  PlusOutlined, DatabaseFilled, ClusterOutlined, 
  AppstoreOutlined, DeploymentUnitOutlined, 
  NodeIndexOutlined, BlockOutlined, EditOutlined 
} from '@ant-design/icons-vue';

const selectedKeys = ref(['db-1']);
const openKeys = ref(['src-1']);

const sources = ref([
  {
    id: 'src-1', ip: '10.0.4.12', port: '19530', user: 'admin', tag: '生产',
    dbs: [
      { id: 'db-1', dbName: '用户行为库', type: 'milvus', modelName: '语义增强-V3', dim: 1536, active: true },
      { id: 'db-2', dbName: '日志特征库', type: 'milvus', modelName: '多语言-L', dim: 1024, active: false }
    ]
  },
  {
    id: 'src-2', ip: '127.0.0.1', port: '6333', user: 'default', tag: '测试',
    dbs: [
      { id: 'db-3', dbName: '本地测试集', type: 'qdrant', modelName: '轻量嵌入', dim: 768, active: false }
    ]
  }
]);

const getVdbIcon = (type: string) => type === 'milvus' ? NodeIndexOutlined : BlockOutlined;
</script>

<style lang="less" scoped>
@primary-blue: #1890ff;
@text-main: #262626;
@text-sub: #8c8c8c;
@line-color: #e8e8e8;

.sidebar-container {
  height: calc(100vh - 70px);
  width: 250px;
  background: #fff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
}

.sidebar-header {
  padding: 20px 16px;
  display: flex; justify-content: space-between; align-items: center;
  .header-main {
    display: flex; align-items: center; gap: 8px;
    .header-icon { color: @primary-blue; font-size: 16px; }
    .header-title { font-size: 14px; font-weight: 600; color: #002766; }
  }
  .add-icon { color: #ccc; cursor: pointer; }
}

.sidebar-menu-wrapper {
  flex: 1;
  overflow-y: auto;
  
  .hierarchy-menu {
    border: none !important;

    // 调整展开按钮位置到左边
    :deep(.ant-menu-submenu-arrow) {
      left: 12px !important;
      right: auto !important;
      top: 50% !important;
      transform: translateY(-50%) !important;
    }

    // 调整标题区域的padding，避免与展开按钮重叠
    :deep(.ant-menu-submenu-title .source-card) {
      padding-left: 24px !important;
    }

    // 第一层：标题行，增加背景色拉开档次
    :deep(.ant-menu-submenu-title) {
      margin: 4px 12px !important;
      width: calc(100% - 24px) !important;
      height: auto !important;
      line-height: normal !important;
      padding: 12px !important;
      background: #f8f9fb;
      border-radius: 6px;

      .source-card {
        position: relative;
        display: flex; justify-content: space-between; align-items: flex-start;
        .source-main {
          display: flex; flex-direction: column; gap: 4px; flex: 1;
          .source-title {
            display: flex; align-items: center; gap: 6px;
            .p-icon { color: @primary-blue; font-size: 16px; }
            .p-title { font-size: 14px; font-weight: 700; color: @text-main; }
          }
          .source-connection {
            display: flex; align-items: center; gap: 6px;
            .p-conn { font-size: 12px; color: #666; font-family: monospace; }
            .p-user { font-size: 11px; color: #999; }
            .p-tag { font-size: 9px; background: #fff; color: #bfbfbf; border: 1px solid #eee; padding: 0 4px; border-radius: 4px; }
          }
        }
        .edit-icon { 
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          color: #ccc; 
          font-size: 14px; 
          cursor: pointer; 
          &:hover { color: @primary-blue; }
        }
      }
    }

    // 第二层：关键缩进
    :deep(.ant-menu-item) {
      height: auto !important;
      line-height: normal !important;
      margin-left: 28px !important; // 核心：通过大缩进产生视觉层级
      padding: 0 !important;
      width: calc(100% - 44px) !important;
      background: transparent !important;

      &.ant-menu-item-selected {
        .v-name { color: @primary-blue; font-weight: 700; }
        .vertical-guide, .h-connector { border-color: @primary-blue; }
      }
    }
  }
}

.asset-tree {
  position: relative;
  padding: 10px 0 10px 16px;
  
  // 垂直导引线：从第一层下方垂直拉出
  .vertical-guide {
    position: absolute;
    left: 0; top: -10px; bottom: 10px;
    border-left: 1px solid @line-color;
  }

  .asset-content {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .db-node {
      display: flex; flex-direction: column; align-items: flex-start; gap: 4px;
      .db-label {
        display: flex; align-items: center; gap: 8px;
        .v-icon { font-size: 14px; color: @text-sub; }
        .v-name { font-size: 13px; color: @text-main; }
      }
      .active-pulse { 
        position: absolute;
        right: 0;
        top: 12px;
        width: 5px; height: 5px; background: #52c41a; border-radius: 50%; box-shadow: 0 0 5px rgba(82,196,26,0.3); 
      }
      .model-info {
        display: flex; align-items: center; gap: 8px;
        margin-left: 22px; // 与图标对齐
        .model-name { font-size: 11px; color: #999; }
        .model-dim { font-size: 10px; color: @primary-blue; font-weight: 700; }
      }
    }
  }
}

.sidebar-footer {
  padding: 16px; border-top: 1px solid #f5f5f5;
  .footer-row {
    display: flex; justify-content: space-between; align-items: center;
    .driver-info {
      display: flex; align-items: center; gap: 8px;
      .s-avatars {
        display: flex;
        .s-av { width: 18px; height: 18px; border-radius: 50%; background: #e6f7ff; color: @primary-blue; 
                font-size: 9px; font-weight: 700; display: flex; align-items: center; justify-content: center;
                border: 1px solid #fff; margin-right: -4px; }
      }
      .s-text { font-size: 11px; color: #bfbfbf; }
    }
    .m-btn { color: #ddd; &:hover { color: @primary-blue; } }
  }
}
</style>