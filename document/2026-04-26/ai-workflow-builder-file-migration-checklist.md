# AI Workflow Builder 文件迁移清单（手动执行版）

## 1. 使用说明

- 本文档只做一件事：明确“**哪个文件移动到哪里，重命名成什么**”。
- 你可以按阶段执行（推荐），每阶段做完后本地运行一次页面验证。
- 本清单以你当前目录为基准：  
  `astrsomn-ui/src/views/admin/ai-workflow/definition/builder`

---

## 2. 目标结构（简版）

```text
builder/
├─ index.vue
├─ app/
├─ domain/
├─ composables/
└─ components/
   ├─ left/
   ├─ canvas/
   ├─ right/
   ├─ context-menu/
   └─ nodes/
```

---

## 3. Phase 1（仅移动/重命名，不改逻辑）

## 3.1 入口文件

- `builder/WorkflowDefinitionBuilder.vue`  
  -> `builder/index.vue`  
  （重命名）

## 3.2 core -> domain / composables

- `builder/core/types.ts`  
  -> `builder/domain/types.ts`

- `builder/core/constants.ts`  
  -> `builder/domain/constants.ts`

- `builder/core/useWorkflowGraph.ts`  
  -> `builder/composables/useWorkflowGraph.ts`

- `builder/core/useNodeDnD.ts`  
  -> `builder/composables/useNodeDnD.ts`  
  （建议同步把导出函数名 `useNodeDnD` 改成 `useNodeDnD`）

## 3.3 center 命名规范化（Center -> canvas）

- `builder/components/center/Center.vue`  
  -> `builder/components/canvas/CenterCanvas.vue`

- `builder/components/center/components/Left-Top.vue`  
  -> `builder/components/canvas/tools/CanvasTopLeftTools.vue`

- `builder/components/center/components/Left-Center.vue`  
  -> `builder/components/canvas/tools/CanvasLeftPalette.vue`

- `builder/components/center/components/Left-Bottom.vue`  
  -> `builder/components/canvas/tools/CanvasLeftCollapseToggle.vue`

- `builder/components/center/components/Right-Top.vue`  
  -> `builder/components/canvas/tools/CanvasTopRightActions.vue`

- `builder/components/center/components/Right-Bottom.vue`  
  -> `builder/components/canvas/tools/CanvasBottomRightZoom.vue`

## 3.4 left 子目录 component -> components（复数）

- `builder/components/left/Left.vue`  
  -> `builder/components/left/LeftPanel.vue`

- `builder/components/left/component/LeftViewToggle.vue`  
  -> `builder/components/left/components/LeftViewToggle.vue`

- `builder/components/left/component/WorkflowListPanel.vue`  
  -> `builder/components/left/components/WorkflowListPanel.vue`

- `builder/components/left/component/ModelChatPanel.vue`  
  -> `builder/components/left/components/ModelChatPanel.vue`

## 3.5 right 重命名

- `builder/components/right/Right.vue`  
  -> `builder/components/right/RightInspector.vue`

## 3.6 context-menu 保持目录，仅类型可后续迁移

- `builder/components/context-menu/ContextMenu.vue`  
  -> `builder/components/context-menu/ContextMenu.vue`（不变）

- `builder/components/context-menu/types.ts`  
  -> `builder/components/context-menu/types.ts`（不变）

## 3.7 nodes shared 基座重命名（可读性提升）

- `builder/components/nodes/shared/BaseCanvasNode.vue`  
  -> `builder/components/nodes/shared/NodeCardBase.vue`

- `builder/components/nodes/shared/BaseInspectorPanel.vue`  
  -> `builder/components/nodes/shared/NodeInspectorBase.vue`

- `builder/components/nodes/shared/node-props.ts`  
  -> `builder/components/nodes/shared/node-view-props.ts`

## 3.8 nodes registry 暂时不动（或同阶段改名）

可选：

- `builder/components/nodes/registry.ts`  
  -> `builder/components/nodes/node-registry.ts`

---

## 4. Phase 2（按职责进一步拆分）

> 这一阶段可能需要少量代码改动（import 和导出），但仍是结构优化，不是功能改造。

## 4.1 从 `domain/constants.ts` 拆出更清晰模块

- `builder/domain/constants.ts`  
  拆分为：
  - `builder/domain/node-definitions.ts`（`nodeDefinitionMap`）
  - `builder/domain/palette.ts`（`paletteGroups`、`canvasPaletteIcons`）
  - `builder/domain/graph-default.ts`（`defaultGraph`）
  - `builder/domain/dnd-constants.ts`（`NODE_DRAG_MIME`）
  - `builder/domain/node-data-factory.ts`（`createNodeData`、`getNodeDefinition`）

## 4.2 页面胶水逻辑下沉到 app

从 `builder/index.vue` 抽出：

- `builder/app/useWorkflowPersistence.ts`  
  （流程列表查询、详情拉取、保存草稿）

- `builder/app/useContextMenuActions.ts`  
  （右键菜单 items 构建 + action 分发）

- `builder/app/useBuilderPage.ts`  
  （页面整体状态编排，可选）

---

## 5. 节点文件迁移建议（14 个节点，批量规则）

你当前节点目录已经是按类别分组，建议保留。  
为了统一命名，建议仅把 `InspectorPanel.vue` 统一改名为 `Inspector.vue`（可选）：

例如：

- `builder/components/nodes/ai/llm/InspectorPanel.vue`
  -> `builder/components/nodes/ai/llm/Inspector.vue`

- `builder/components/nodes/control/start/InspectorPanel.vue`
  -> `builder/components/nodes/control/start/Inspector.vue`

...其余节点同理。

`CanvasNode.vue` 建议先不动，避免引入大量 import 变更。

---

## 6. 手动执行顺序（推荐）

1. 先移动 `core/*` 到 `domain + composables`
2. 再移动 `center/*` 到 `components/canvas/*`
3. 再处理 `left/right` 重命名
4. 最后移动 `nodes/shared` 与（可选）`registry.ts`
5. 全量替换 import 后，运行页面验证
6. 再做 Phase 2 拆分

---

## 7. 一次性核对清单（迁移完成后）

- 是否还存在旧路径 `builder/core/*`
- 是否还存在旧路径 `builder/components/center/*`
- 是否还存在旧文件 `WorkflowDefinitionBuilder.vue`
- `index.vue` 能否正常加载 left/center/right
- 画布拖拽新增节点是否正常
- 节点右键菜单是否正常
- 右侧属性面板是否按节点类型切换
- 保存草稿接口是否仍可触发

---

## 8. 最小风险策略

- 每移动 3~5 个文件就修一次 import，不要一次性移动 50 个文件。
- 每个阶段保留“可运行状态”，避免最后集中排错。
- `nodes/*` 先只改 shared 与 registry，节点明细文件延后。

