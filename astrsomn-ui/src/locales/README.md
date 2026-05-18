# 国际化字典使用指南

## 📁 目录结构

```
src/locales/
├── dictionary/
│   ├── core.ts          # 枚举字典核心实现
│   ├── registry.ts      # 字典注册表（所有字典在这里注册）
│   └── index.ts         # 导出入口
├── zh-CN/
│   └── dictionary/
│       ├── common.ts                    # 通用枚举（状态、是/否等）
│       ├── chat.ts                      # 聊天相关枚举
│       ├── ai-config/
│       │   ├── ai-account.ts           # AI账户枚举
│       │   ├── ai-agent.ts             # AI代理枚举
│       │   ├── ai-instance.ts          # AI实例枚举
│       │   └── ai-model.ts             # AI模型枚举
│       ├── ai-vector/
│       │   ├── vec-driver.ts            # 向量驱动枚举
│       │   ├── vec-doc.ts               # 向量文档枚举
│       │   ├── vec-store.ts             # 向量存储枚举
│       │   ├── vec-source.ts            # 向量源枚举
│       │   └── vec-chunk-strategy.ts   # 向量切片策略枚举
│       ├── ai-workflow/
│       │   ├── workflow-node.ts         # 工作流节点枚举
│       │   └── workflow-instance.ts     # 工作流实例枚举
│       └── system-config/
│           ├── system-extension.ts     # 系统扩展枚举
│           ├── system-message.ts       # 系统消息枚举
│           └── system-user.ts          # 系统用户枚举
└── en-US/
    └── dictionary/
        └── [同上结构，英文文案]
```

## 🚀 快速开始

### 1. 使用已有字典

```typescript
import { getDictionary } from '@/locales/dictionary'

// 获取枚举字典
const modelProviderDict = getDictionary('ai-model.provider')
const label = modelProviderDict.getLabel('openai') // 返回 "OpenAI" 或 "OpenAI (GPT)"

// 使用 options() 方法获取下拉选项
const providers = modelProviderDict.options()
// 返回: [{ value: 'openai', label: 'OpenAI' }, ...]

// 使用 Vue Composition API
import { useDictionary } from '@/locales/dictionary'

const dict = useDictionary('ai-model.provider')
console.log(dict.value.getLabel('openai'))
```

### 2. 创建新的枚举字典

#### 中文字典文件：`zh-CN/dictionary/xxx.ts`

```typescript
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

// 定义状态标签（key需与后端枚举code一致）
const statusLabels = {
    enabled: '启用',
    disabled: '禁用',
    pending: '待处理'
} as const

// 创建字典
export const myStatusDictionary = createEnumDictionary({
    id: 'my.status',           // 全局唯一ID
    labels: statusLabels,      // 标签映射
    order: ['enabled', 'disabled', 'pending'], // 显示顺序
    caseInsensitive: true      // 可选：是否大小写不敏感
})
```

#### 英文字典文件：`en-US/dictionary/xxx.ts`

```typescript
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const statusLabels = {
    enabled: 'Enabled',
    disabled: 'Disabled',
    pending: 'Pending'
} as const

export const myStatusDictionary = createEnumDictionary({
    id: 'my.status',
    labels: statusLabels,
    order: ['enabled', 'disabled', 'pending'],
    caseInsensitive: true
})
```

#### 注册到 registry.ts

```typescript
// 在 registry.ts 中导入
import { myStatusDictionary as myStatusEn } from '../en-US/dictionary/xxx.ts'
import { myStatusDictionary as myStatusZh } from '../zh-CN/dictionary/xxx.ts'

// 在 DictionaryBundle 类型中添加
export type DictionaryBundle = {
    // ... 已有字典
    'my.status': EnumDictionary<Record<string, string>>
}

// 在 bundles 中注册
const zhCNDictionaryBundle = {
    // ... 其他字典
    'my.status': myStatusZh
}

const enUSDictionaryBundle = {
    // ... 其他字典
    'my.status': myStatusEn
}
```

## 📖 可用字典列表

### AI配置相关

| 字典ID | 说明 | 示例 |
|--------|------|------|
| `ai-model.provider` | 模型提供商 | OpenAI, Anthropic, Google等 |
| `ai-model.status` | 模型状态 | enabled, disabled |
| `ai-model.capabilities` | 模型能力 | streaming, tools, vision等 |
| `ai-model.sourceType` | 模型来源 | user_custom, plugin |
| `ai-account.status` | 账户状态 | enabled, disabled |
| `ai-instance.status` | 实例状态 | enabled, disabled |
| `ai-agent.status` | 代理状态 | enabled, disabled |
| `ai-agent.memoryMode` | 记忆模式 | shortTerm, longTerm, hybrid |
| `ai-agent.isDefault` | 是否默认 | Y, N |

### 向量库相关

| 字典ID | 说明 |
|--------|------|
| `ai-vec.driver` | 向量数据库驱动 |
| `ai-vec.driver.status` | 向量驱动状态 |
| `ai-vec.driver.param` | 向量驱动参数 |
| `ai-vec.doc.syncStatus` | 文档同步状态 |
| `ai-vec.store.status` | 向量存储状态 |
| `ai-vec.source.status` | 向量源状态 |
| `ai-vec.chunkStrategy` | 切片策略 |

### 工作流相关

| 字典ID | 说明 |
|--------|------|
| `ai-workflow.nodeType` | 节点类型 |
| `ai-workflow.executionStatus` | 执行状态 |
| `ai-workflow.humanTaskStatus` | 人工任务状态 |
| `ai-workflow.instance.state` | 实例状态 |
| `ai-workflow.node.state` | 节点状态 |

### 系统配置相关

| 字典ID | 说明 |
|--------|------|
| `system.extension.type` | 扩展类型 |
| `system.extension.installStatus` | 安装状态 |
| `system.extension.applyStatus` | 应用状态 |
| `system.extension.discoveryMechanism` | 发现机制 |
| `system.extension.installSource` | 安装来源 |
| `system.message.type` | 消息类型 |
| `system.message.level` | 消息级别 |
| `system.message.readStatus` | 阅读状态 |
| `system.message.refType` | 引用类型 |
| `system.user.role` | 用户角色 |
| `system.user.admin` | 是否管理员 |

### 通用

| 字典ID | 说明 |
|--------|------|
| `astro.chat.role` | 聊天角色 |
| `astro.chat.eventType` | 聊天事件类型 |
| `common.status` | 通用状态 |
| `common.boolean` | 布尔值 |
| `common.yesNo` | 是否选项 |

## 🎯 在组件中使用

### Vue 3 Composition API

```vue
<template>
  <div>
    <el-select v-model="form.status">
      <el-option
        v-for="option in statusOptions"
        :key="option.value"
        :label="option.label"
        :value="option.value"
      />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useDictionary } from '@/locales/dictionary'

const dict = useDictionary('common.status')
const statusOptions = computed(() => dict.value.options())
</script>
```

### 在表格中使用

```vue
<template>
  <el-table :data="tableData">
    <el-table-column prop="status" label="状态">
      <template #default="{ row }">
        {{ getStatusLabel(row.status) }}
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { getDictionary } from '@/locales/dictionary'

function getStatusLabel(code: string) {
  const dict = getDictionary('common.status')
  return dict.getLabel(code) || code
}
</script>
```

## ⚠️ 注意事项

1. **后端对齐**：字典的 key 必须与后端枚举的 code 完全一致
2. **大小写**：如果后端返回的大小写不一致，设置 `caseInsensitive: true`
3. **新增字典**：每次新增字典后，必须在 `registry.ts` 中注册
4. **顺序一致**：中文和英文的 `order` 数组应该保持一致

## 🔧 调试技巧

```typescript
// 查看当前语言
import { dictionaryLocale, getDictionaryLocale } from '@/locales/dictionary'
console.log('当前语言:', dictionaryLocale.value)

// 切换语言
import { setDictionaryLocale } from '@/locales/dictionary'
setDictionaryLocale('en-US')

// 打印所有字典
import { getDictionaryBundle } from '@/locales/dictionary'
console.log('中文字典:', getDictionaryBundle('zh-CN'))
console.log('英文字典:', getDictionaryBundle('en-US'))
```
