<template>
  <div class="sidebar-container">
    <div class="sidebar-header">
      <div class="header-left">
        <div class="header-icon-box">
          <DatabaseFilled class="header-icon" />
        </div>
        <span class="header-title">资产目录</span>
      </div>
      <PlusOutlined class="add-icon" @click="handleAddSource" />
    </div>

    <div class="sidebar-content">
      <div
        v-for="source in sources"
        :key="source.id"
        class="source-section"
      >
        <a-dropdown :trigger="['contextmenu']">
          <SourceCard
            :ip="source.ip"
            :port="source.port"
            :tag="source.tag"
            :is-open="openKeys.includes(source.id)"
            @toggle="toggleSource(source.id)"
          />
          <template #overlay>
            <a-menu @click="({ key }) => handleSourceMenuClick(key, source)">
              <a-menu-item key="addDb">
                <template #icon><PlusOutlined /></template>
                新增数据库
              </a-menu-item>
              <a-menu-item key="edit">
                <template #icon><EditOutlined /></template>
                编辑数据源
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="delete" class="danger-item">
                <template #icon><DeleteOutlined /></template>
                删除数据源
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <transition name="expand">
          <div v-if="openKeys.includes(source.id)" class="db-container">
            <a-dropdown
              v-for="db in source.dbs"
              :key="db.id"
              :trigger="['contextmenu']"
            >
              <DbNode
                :db-name="db.dbName"
                :model-name="db.modelName"
                :dim="db.dim"
                :active="db.active"
                :is-selected="selectedKeys.includes(db.id)"
                @select="selectDb(db.id)"
              />
              <template #overlay>
                <a-menu @click="({ key }) => handleDbMenuClick(key, source.id, db)">
                  <a-menu-item key="edit">
                    <template #icon><EditOutlined /></template>
                    编辑数据库
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="delete" class="danger-item">
                    <template #icon><DeleteOutlined /></template>
                    删除数据库
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </transition>
      </div>
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
        <AppstoreOutlined class="m-btn" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  DatabaseFilled,
  AppstoreOutlined
} from '@ant-design/icons-vue'
import SourceCard from './SourceCard.vue'
import DbNode from './DbNode.vue'

const selectedKeys = ref<string[]>(['db-1'])
const openKeys = ref<string[]>(['src-1'])

type Db = {
  id: string
  dbName: string
  type: string
  modelName: string
  dim: number
  active: boolean
}

type Source = {
  id: string
  ip: string
  port: string
  user: string
  tag: string
  dbs: Db[]
}

const sources = ref<Source[]>([
  {
    id: 'src-1',
    ip: '10.0.4.12',
    port: '19530',
    user: 'admin',
    tag: '生产',
    dbs: [
      { id: 'db-1', dbName: '用户行为分析库', type: 'milvus', modelName: '语义增强·V3', dim: 1536, active: true },
      { id: 'db-2', dbName: '全局日志特征库', type: 'milvus', modelName: '多语言·Large', dim: 1024, active: false }
    ]
  },
  {
    id: 'src-2',
    ip: '127.0.0.1',
    port: '6333',
    user: 'default',
    tag: '测试',
    dbs: [
      { id: 'db-3', dbName: 'QA验证临时库', type: 'qdrant', modelName: '轻量嵌入', dim: 768, active: false }
    ]
  }
])

const toggleSource = (id: string) => {
  const index = openKeys.value.indexOf(id)
  if (index > -1) {
    openKeys.value.splice(index, 1)
  } else {
    openKeys.value.push(id)
  }
}

const selectDb = (id: string) => {
  selectedKeys.value = [id]
}

const handleAddSource = () => {
  message.info('新增数据源 - 待对接接口')
}

const handleSourceMenuClick = (key: string, source: Source) => {
  switch (key) {
    case 'addDb':
      message.info(`新增数据库到数据源 ${source.ip}:${source.port} - 待对接接口`)
      break
    case 'edit':
      message.info(`编辑数据源 ${source.ip}:${source.port} - 待对接接口`)
      break
    case 'delete':
      message.warning(`删除数据源 ${source.id} - 待对接接口`)
      break
  }
}

const handleDbMenuClick = (key: string, sourceId: string, db: Db) => {
  switch (key) {
    case 'edit':
      message.info(`编辑数据库 ${db.dbName} - 待对接接口`)
      break
    case 'delete':
      message.warning(`从数据源 ${sourceId} 删除数据库 ${db.id} - 待对接接口`)
      break
  }
}
</script>

<style lang="less" scoped>
@primary-blue: #2563eb;
@bg-main: #fcfdfe;
@border-subtle: rgba(226, 232, 240, 0.6);
@text-main: #1e293b;
@text-sub: #64748b;
@text-muted: #94a3b8;

.sidebar-container {
  height: calc(100vh - 74px);
  width: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  border: 1px solid @border-subtle;
  border-radius: 12px;
  margin: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to bottom, #ffffff, #fafafa);
  border-bottom: 1px solid #f1f5f9;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;

    .header-icon-box {
      width: 32px;
      height: 32px;
      background: @primary-blue;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);

      .header-icon {
        color: #fff;
        font-size: 14px;
      }
    }

    .header-title {
      font-size: 15px;
      font-weight: 700;
      color: @text-main;
      letter-spacing: -0.01em;
    }
  }

  .add-icon {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: @text-muted;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #f1f5f9;
      color: @primary-blue;
    }
  }
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.source-section {
  margin: 0 8px;
}

.db-container {
  margin: 0 8px 8px 20px;
  padding-left: 16px;
  border-left: 1.5px solid #f1f5f9;
  overflow: hidden;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  max-height: 500px;
  opacity: 1;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
  margin-bottom: 0;
}

.sidebar-footer {
  padding: 14px 16px;
  border-top: 1px solid #f5f5f5;

  .footer-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .driver-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .s-avatars {
        display: flex;

        .s-av {
          width: 18px;
          height: 18px;
          border-radius: 50%;
          background: #e6f7ff;
          color: @primary-blue;
          font-size: 9px;
          font-weight: 700;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid #fff;
          margin-right: -4px;
        }
      }

      .s-text {
        font-size: 11px;
        color: @text-muted;
      }
    }

    .m-btn {
      color: #ddd;
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: @primary-blue;
      }
    }
  }
}

:deep(.danger-item) {
  color: #ff4d4f;
  
  &:hover {
    color: #ff4d4f !important;
    background: #fff1f0 !important;
  }
}
</style>
