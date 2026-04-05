# 控制台首页布局配置

## 概述

本目录包含控制台首页的布局配置文件，允许通过JSON文件自定义首页模块的排列和尺寸。

## 文件结构

```
config/
├── dashboard-layout.json  # 布局配置文件
├── types.ts              # 类型定义
├── loader.ts             # 配置加载器
└── README.md             # 说明文档
```

## 配置文件说明

### dashboard-layout.json

主配置文件，包含以下字段：

#### 顶层配置

```json
{
  "version": 1,                    // 配置版本号
  "description": "控制台首页布局配置文件",
  "layout": { ... },               // 布局全局配置
  "modules": [ ... ]               // 模块配置数组
}
```

#### layout 配置

```json
{
  "layout": {
    "cols": 12,          // 栅格列数
    "rowHeight": 100,    // 行高（像素）
    "gap": 16,           // 模块间距（像素）
    "maxRows": 20        // 最大行数
  }
}
```

#### modules 配置

每个模块包含以下字段：

```json
{
  "kind": "AiModel",     // 模块类型（必填）
  "x": 0,                // X坐标（必填）
  "y": 0,                // Y坐标（必填）
  "w": 2,                // 宽度（必填，最大3）
  "h": 2,                // 高度（必填，最大3）
  "minW": 1,             // 最小宽度（可选）
  "minH": 1,             // 最小高度（可选）
  "maxW": 3,             // 最大宽度（可选，最大3）
  "maxH": 3,             // 最大高度（可选，最大3）
  "route": "/admin/models"  // 路由路径（页面模块必填）
}
```

## 模块类型

### 内置模块

- `AiAgent` - AI智能体
- `AiInstance` - AI实例
- `ConsoleSystemLoad` - 系统负载

### 页面模块

- `AiModel` - 模型接入
- `AiAccount` - 凭证管理
- `AiMcp` - AI MCP
- `AiTool` - 工具插件
- `AiPrompt` - 提示词库
- `AiTemplate` - FTL模板
- `KnowledgeBase` - 知识库管理
- `AiDocument` - 文档管理
- `SecurityPolicy` - 安全治理
- `TraceInsight` - 链路追踪
- `AiWorkflow` - 工作流
- `AiWorkflowTest` - 工作流编排测试
- `SystemUser` - 用户管理
- `SystemEnv` - 环境管理
- `SystemConfig` - 系统配置
- `SystemExtension` - 系统扩展
- `ConsoleResourceLibrary` - 应用库

## 使用方法

### 1. 修改配置文件

编辑 `dashboard-layout.json` 文件，调整模块的位置和尺寸。

### 2. 在代码中使用

```typescript
import { loadLayoutFromConfig, getLayoutConfigMetadata } from './config/loader'

// 加载布局配置
const layoutItems = loadLayoutFromConfig()

// 获取配置元数据
const metadata = getLayoutConfigMetadata()
console.log('配置版本:', metadata.version)
console.log('模块数量:', metadata.moduleCount)
```

### 3. 验证配置

配置加载器会自动验证配置的合法性：
- 检查坐标和尺寸是否为正数
- 检查最小/最大尺寸约束
- 自动限制最大尺寸为3x3

## 配置示例

### 示例1：紧凑布局

```json
{
  "modules": [
    {
      "kind": "AiModel",
      "route": "/admin/models",
      "x": 0,
      "y": 0,
      "w": 2,
      "h": 2
    },
    {
      "kind": "AiAccount",
      "route": "/admin/ai-account",
      "x": 2,
      "y": 0,
      "w": 2,
      "h": 2
    }
  ]
}
```

### 示例2：大尺寸模块

```json
{
  "modules": [
    {
      "kind": "AiAgent",
      "x": 0,
      "y": 0,
      "w": 3,
      "h": 3,
      "minW": 2,
      "minH": 2
    }
  ]
}
```

## 注意事项

1. **尺寸限制**：
   - 普通模块最大尺寸为3x3，超过的尺寸会自动限制
   - **AiAgent模块**特殊处理，最大尺寸为6x4
2. **坐标系统**：使用12列栅格系统，x坐标范围0-11
3. **路由配置**：页面模块必须配置route字段
4. **版本管理**：修改配置后建议更新version字段
5. **备份**：修改前建议备份原配置文件

## 故障排查

### 模块不显示

- 检查kind字段是否正确
- 检查route字段是否配置（页面模块）
- 检查坐标是否超出栅格范围

### 布局错乱

- 检查模块是否有重叠
- 检查尺寸是否超出限制
- 检查栅格列数配置

### 配置不生效

- 清除浏览器缓存
- 检查JSON格式是否正确
- 查看控制台是否有错误信息
