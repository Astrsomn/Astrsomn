# AI Workflow Builder 目录梳理与重构建议

## 1. 文档目标

- 梳理 `astrsomn-ui/src/views/admin/ai-workflow/definition/builder` 当前目录结构。
- 说明每个文件的职责，降低后续接手成本。
- 给出一套可演进的推荐目录架构，便于后期维护和扩展新节点。

---

## 2. 当前目录总览（50 files）

```text
builder/
├─ WorkflowDefinitionBuilder.vue
├─ core/
│  ├─ constants.ts
│  ├─ useNodeDnD.ts
│  ├─ types.ts
│  └─ useWorkflowGraph.ts
└─ components/
   ├─ center/
   │  ├─ Center.vue
   │  └─ components/
   │     ├─ Left-Bottom.vue
   │     ├─ Left-Center.vue
   │     ├─ Left-Top.vue
   │     ├─ Right-Bottom.vue
   │     └─ Right-Top.vue
   ├─ context-menu/
   │  ├─ ContextMenu.vue
   │  └─ types.ts
   ├─ left/
   │  ├─ Left.vue
   │  └─ component/
   │     ├─ LeftViewToggle.vue
   │     ├─ ModelChatPanel.vue
   │     └─ WorkflowListPanel.vue
   ├─ nodes/
   │  ├─ registry.ts
   │  ├─ ai/
   │  │  ├─ knowledge/{CanvasNode.vue, InspectorPanel.vue}
   │  │  ├─ llm/{CanvasNode.vue, InspectorPanel.vue}
   │  │  └─ vision/{CanvasNode.vue, InspectorPanel.vue}
   │  ├─ control/
   │  │  ├─ end/{CanvasNode.vue, InspectorPanel.vue}
   │  │  ├─ parallel/{CanvasNode.vue, InspectorPanel.vue}
   │  │  └─ start/{CanvasNode.vue, InspectorPanel.vue}
   │  ├─ interaction/
   │  │  ├─ human-audit/{CanvasNode.vue, InspectorPanel.vue}
   │  │  └─ input-form/{CanvasNode.vue, InspectorPanel.vue}
   │  ├─ logic/
   │  │  ├─ condition/{CanvasNode.vue, InspectorPanel.vue}
   │  │  ├─ iterator/{CanvasNode.vue, InspectorPanel.vue}
   │  │  └─ template/{CanvasNode.vue, InspectorPanel.vue}
   │  ├─ shared/
   │  │  ├─ BaseCanvasNode.vue
   │  │  ├─ BaseInspectorPanel.vue
   │  │  └─ node-props.ts
   │  └─ tool/
   │     ├─ code/{CanvasNode.vue, InspectorPanel.vue}
   │     ├─ http/{CanvasNode.vue, InspectorPanel.vue}
   │     └─ search/{CanvasNode.vue, InspectorPanel.vue}
   └─ right/
      └─ Right.vue
```

---

## 3. 文件职责清单（逐文件）

## 3.1 入口与编排层

- `builder/WorkflowDefinitionBuilder.vue`  
  页面主容器与总编排：组装左中右三栏、承接右键菜单、连接 API（列表/详情/保存）、调用图状态 composable。

## 3.2 core（领域模型与核心行为）

- `builder/core/types.ts`  
  全局类型契约中心：节点类型、端口、图结构、面板与交互 payload 等。
- `builder/core/constants.ts`  
  节点定义与静态配置中心：`nodeDefinitionMap`、默认图、画布节点菜单、节点默认数据构造。
- `builder/core/useNodeDnD.ts`  
  节点拖拽桥接：`startDrag` 写入 MIME，`parseDropType` 解析并校验落点类型。
- `builder/core/useWorkflowGraph.ts`  
  图状态与图行为核心：节点/连线增删改、连线校验规则、批量操作、图校验、序列化与反序列化。

## 3.3 左侧面板（流程导航 + 占位聊天）

- `builder/components/left/Left.vue`  
  左栏壳组件，在“流程列表/模型对话”之间切换。
- `builder/components/left/component/LeftViewToggle.vue`  
  左栏模式切换开关（workflow-list / model-chat）。
- `builder/components/left/component/WorkflowListPanel.vue`  
  流程列表展示与当前流程选择。
- `builder/components/left/component/ModelChatPanel.vue`  
  模型聊天区 UI（当前为占位交互，便于后续接真实会话）。

## 3.4 中央画布（VueFlow）

- `builder/components/center/Center.vue`  
  画布主组件：承载 VueFlow、拖拽落点、缩放、选择、右键菜单事件派发、交互模式切换。
- `builder/components/center/components/Left-Top.vue`  
  左上角工具条：框选/平移/清空/撤销重做占位。
- `builder/components/center/components/Left-Center.vue`  
  画布左侧节点浮动面板：按分组展示节点并支持拖拽。
- `builder/components/center/components/Left-Bottom.vue`  
  左侧面板收起/展开浮动按钮。
- `builder/components/center/components/Right-Top.vue`  
  右上角动作区：保存草稿、调试/发布占位。
- `builder/components/center/components/Right-Bottom.vue`  
  右下角视图控制：缩放百分比、适配视图、放大缩小。

## 3.5 右侧属性面板

- `builder/components/right/Right.vue`  
  属性面板主入口：流程元信息编辑、节点/连线配置容器、节点 Inspector 动态渲染。

## 3.6 右键菜单

- `builder/components/context-menu/ContextMenu.vue`  
  通用右键菜单弹层（纯展示和事件抛出，不承载业务规则）。
- `builder/components/context-menu/types.ts`  
  右键菜单项与坐标类型定义。

## 3.7 节点系统（渲染 + 配置）

- `builder/components/nodes/registry.ts`  
  节点注册表：节点类型 -> 画布节点组件 + Inspector 组件映射。
- `builder/components/nodes/shared/BaseCanvasNode.vue`  
  画布节点通用基座：头部样式、句柄渲染、主题变量、基础内容插槽。
- `builder/components/nodes/shared/BaseInspectorPanel.vue`  
  Inspector 通用基座：名称/说明/动态 schema 配置项/端口信息/变量引用提示。
- `builder/components/nodes/shared/node-props.ts`  
  节点展示层 props 类型。

### AI 类节点

- `builder/components/nodes/ai/llm/CanvasNode.vue`：LLM 节点画布外观。  
- `builder/components/nodes/ai/llm/InspectorPanel.vue`：LLM 节点配置面板（当前委托通用 BaseInspector）。  
- `builder/components/nodes/ai/knowledge/CanvasNode.vue`：知识库检索节点画布外观。  
- `builder/components/nodes/ai/knowledge/InspectorPanel.vue`：知识库检索配置面板（委托 BaseInspector）。  
- `builder/components/nodes/ai/vision/CanvasNode.vue`：视觉识别节点画布外观。  
- `builder/components/nodes/ai/vision/InspectorPanel.vue`：视觉识别配置面板（委托 BaseInspector）。

### Control 类节点

- `builder/components/nodes/control/start/CanvasNode.vue`：开始节点画布外观。  
- `builder/components/nodes/control/start/InspectorPanel.vue`：开始节点配置面板（委托 BaseInspector）。  
- `builder/components/nodes/control/end/CanvasNode.vue`：结束节点画布外观。  
- `builder/components/nodes/control/end/InspectorPanel.vue`：结束节点配置面板（委托 BaseInspector）。  
- `builder/components/nodes/control/parallel/CanvasNode.vue`：并行节点画布外观。  
- `builder/components/nodes/control/parallel/InspectorPanel.vue`：并行节点配置面板（委托 BaseInspector）。

### Interaction 类节点

- `builder/components/nodes/interaction/human-audit/CanvasNode.vue`：人工审核节点画布外观。  
- `builder/components/nodes/interaction/human-audit/InspectorPanel.vue`：人工审核配置面板（委托 BaseInspector）。  
- `builder/components/nodes/interaction/input-form/CanvasNode.vue`：输入增强节点画布外观。  
- `builder/components/nodes/interaction/input-form/InspectorPanel.vue`：输入增强配置面板（委托 BaseInspector）。

### Logic 类节点

- `builder/components/nodes/logic/condition/CanvasNode.vue`：条件分支节点画布外观（含 true/false 句柄）。  
- `builder/components/nodes/logic/condition/InspectorPanel.vue`：条件分支配置面板（委托 BaseInspector）。  
- `builder/components/nodes/logic/iterator/CanvasNode.vue`：迭代器节点画布外观。  
- `builder/components/nodes/logic/iterator/InspectorPanel.vue`：迭代器配置面板（委托 BaseInspector）。  
- `builder/components/nodes/logic/template/CanvasNode.vue`：变量聚合节点画布外观。  
- `builder/components/nodes/logic/template/InspectorPanel.vue`：变量聚合配置面板（委托 BaseInspector）。

### Tool 类节点

- `builder/components/nodes/tool/http/CanvasNode.vue`：HTTP 请求节点画布外观。  
- `builder/components/nodes/tool/http/InspectorPanel.vue`：HTTP 节点配置面板（委托 BaseInspector）。  
- `builder/components/nodes/tool/code/CanvasNode.vue`：代码执行节点画布外观。  
- `builder/components/nodes/tool/code/InspectorPanel.vue`：代码执行配置面板（委托 BaseInspector）。  
- `builder/components/nodes/tool/search/CanvasNode.vue`：搜索节点画布外观。  
- `builder/components/nodes/tool/search/InspectorPanel.vue`：搜索节点配置面板（委托 BaseInspector）。

---

## 4. 当前结构的可维护性观察

- `WorkflowDefinitionBuilder.vue` 偏“巨型入口”，UI 编排、业务动作、菜单策略、接口调用耦合在一处。
- `core/useNodeDnD.ts` 与命名风格不一致（其语义是 composable，建议统一 `useNodeDnD.ts`）。
- `components/left/component` 命名不统一（建议 `components` 复数）。
- 节点 `InspectorPanel.vue` 大量样板转发（几乎都只包一层 `BaseInspectorPanel`）。
- 业务规则分散：连线规则在 `useWorkflowGraph.ts`，右键动作分发在页面入口，后续扩展策略时容易变重。
- 若继续增加节点类型，`constants.ts + registry.ts + nodes/*` 的手工同步成本会持续上升。

---

## 5. 推荐目录架构设计（面向后期维护）

## 5.1 推荐目标结构

```text
builder/
├─ index.vue                         # 原 WorkflowDefinitionBuilder.vue
├─ app/
│  ├─ useBuilderPage.ts              # 页面编排、事件胶水、接口联动
│  ├─ useContextMenuActions.ts       # 右键动作策略
│  └─ useWorkflowPersistence.ts       # 查询列表/详情/保存
├─ domain/
│  ├─ types.ts                       # 领域类型（原 core/types.ts）
│  ├─ node-definitions.ts            # 节点定义（原 constants 的定义部分）
│  ├─ palette.ts                     # 左侧/画布节点面板数据
│  ├─ graph-default.ts               # 默认图
│  ├─ graph-rules.ts                 # 连线与图校验规则
│  └─ graph-serializer.ts            # graphJson 序列化/反序列化
├─ composables/
│  ├─ useWorkflowGraph.ts            # 图状态管理（保留）
│  ├─ useNodeDnD.ts                  # 原 useNodeDnD.ts
│  └─ useCanvasInteraction.ts        # 缩放/平移/选中等交互态
├─ components/
│  ├─ left/
│  │  ├─ LeftPanel.vue
│  │  └─ components/...
│  ├─ canvas/
│  │  ├─ CenterCanvas.vue
│  │  └─ tools/...
│  ├─ right/
│  │  └─ RightInspector.vue
│  ├─ context-menu/
│  │  ├─ ContextMenu.vue
│  │  └─ types.ts
│  └─ nodes/
│     ├─ registry.ts
│     ├─ shared/
│     ├─ manifests/                  # 节点元配置（可选）
│     └─ <type>/                     # 每个节点类型目录（Canvas + Inspector）
└─ README.md                         # builder 模块维护说明
```

## 5.2 设计要点

- **分层清晰**：`domain` 放纯数据/规则，`composables` 放状态，`components` 放视图。
- **入口瘦身**：`index.vue` 只做拼装，页面事件逻辑下沉到 `app/*`。
- **命名统一**：`use*` composable 命名一致，目录统一复数 `components`。
- **扩展友好**：新增节点时，遵循“定义 -> 画布组件 -> Inspector -> registry”最小路径。
- **易测性提升**：规则与序列化拆到 `domain` 后可独立单测，不依赖 Vue 组件。

---

## 6. 建议的落地步骤（低风险迁移）

1. **第一步：仅重命名和搬迁，不改行为**  
   `WorkflowDefinitionBuilder.vue -> index.vue`，`useNodeDnD.ts -> useNodeDnD.ts`，`component -> components`。
2. **第二步：抽离页面逻辑**  
   将接口请求与右键动作分发拆到 `app/` composable。
3. **第三步：抽离领域规则**  
   把 `validateGraph`、连接约束、graphJson 转换逻辑拆到 `domain/`。
4. **第四步：压缩节点样板文件**  
   对仅转发 `BaseInspectorPanel` 的 Inspector 可考虑统一生成/统一导出策略。
5. **第五步：补 README + 约定**  
   写清“新增节点改哪里”的 checklist，减少团队沟通成本。

---

## 7. 新增节点时建议 checklist

- 在 `domain/node-definitions.ts` 增加节点定义（标题、端口、配置 schema）。
- 新增对应 `CanvasNode.vue`（必要时定制句柄和视觉）。
- 新增对应 `InspectorPanel.vue`（若无特殊逻辑可复用 BaseInspector）。
- 在 `components/nodes/registry.ts` 注册 Canvas 与 Inspector。
- 如有新连线约束，补充 `domain/graph-rules.ts`。
- 更新模块 README 的节点清单。

