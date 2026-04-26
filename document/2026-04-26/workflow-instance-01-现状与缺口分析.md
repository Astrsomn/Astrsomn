# Astrsomn Workflow 实例层：现状与缺口分析

## 1. 背景
- 前端编排器已稳定，核心定义由 `graphJson` 承载，结构来自 `nodes/edges/meta`。
- 后端已有工作流基础表与 Mapper，但实例运行引擎尚未形成闭环。
- 目标是构建接近 Dify 的“可发布、可运行、可追踪”的实例层。

## 2. 当前能力盘点

### 2.1 前端 DSL 能力
- 节点类型：`start/end/parallel/llm/retrieval/if-else/intent-classifier/merge/http/code/tools`。
- 节点结构：`id/type/position/data(config,inputs,outputs)`。
- 连线结构：`source/target/sourceHandle/targetHandle/label/type`。
- 持久化接口：`/create /update /detail /queryPage /delete`，并预留 `publish/test-run`。

### 2.2 后端 workflow 模块能力
- 已有数据实体：
  - `AST_FLOW_DEFINITION`（草稿定义）
  - `AST_FLOW_DEPLOYMENT`（发布版本）
  - `AST_FLOW_INSTANCE`（实例）
  - `AST_FLOW_NODE_HISTORY`（节点历史）
  - `AST_FLOW_HUMAN_TASK`（人工任务）
- `astrsomn-workflow-springboot-starter` 已具备 Mapper 与 AutoConfiguration。
- `astrsomn-server` 当前 `AiWorkflowController` 仅实现定义 CRUD。

### 2.3 基础 AI 能力
- `astrsomn-springboot-starter` 已可复用：
  - LLM 组装（`AstroAssistantFactory`）
  - Tool/MCP 组装（`ToolProviderAssembler`、`DynamicMcpToolProvider`）
  - RAG/Memory/Stream 等运行时能力

## 3. 关键缺口

### 3.1 控制面缺口（Control Plane）
- `publish` 未落地：缺少草稿校验、版本冻结、部署写入流程。
- 缺少 DSL 编译步骤：未将 `graphJson` 转为运行期可执行计划。
- 缺少发布一致性策略：定义版本、节点配置、部署快照未形成原子语义。

### 3.2 运行面缺口（Runtime Plane）
- `test-run` 未落地：没有同步执行通道。
- 生产实例执行未落地：没有异步调度、Worker、任务恢复机制。
- 节点执行器体系缺失：暂无 `NodeExecutor` 注册与路由。
- 分支并行语义未定义：`parallel/merge/if-else/intent-classifier` 执行规则未后端化。

### 3.3 可观测性与治理缺口
- 实例级 TraceId、NodeSpan、重试轨迹、错误分类尚未统一。
- 没有实例重放、超时中断、幂等去重、限流配额治理。
- 人工节点虽有表结构，但缺少挂起/恢复 API 与审批动作语义。

## 4. 与 Dify 对标差距（V1 视角）

| 能力域 | Dify 典型能力 | 当前状态 | 缺口等级 |
|---|---|---|---|
| DSL 发布 | 草稿与发布分离、版本冻结 | 仅草稿 CRUD | 高 |
| 测试运行 | 同步试跑 + 节点轨迹 | 接口预留，后端未实现 | 高 |
| 生产运行 | 异步实例、任务恢复 | 实体表有，执行器无 | 高 |
| 节点生态 | 丰富节点与统一执行器 | 前端节点齐全，后端无执行器 | 高 |
| 运行可观测 | 节点级日志/指标/重试轨迹 | 仅历史表基础 | 中高 |
| 人工节点 | 待办、审批、恢复 | 仅数据骨架 | 中高 |

## 5. 优先级结论
- **P0**：发布 + 编译 + test-run 同步闭环。
- **P0**：异步实例执行主链路（调度、执行、状态落库、恢复）。
- **P1**：全节点执行器实现与统一 I/O 契约。
- **P1**：可观测与治理（重试、超时、幂等、限流）。
- **P2**：高级特性（子流程、补偿事务、回放调试）。

## 6. 范围边界
- 基础模型调用、MCP、工具能力留在 `astrsomn-springboot-starter`。
- 编排/流程控制/实例状态机留在 `astrsomn-workflow-springboot-starter`。
- `astrsomn-server` 负责对外 API 门面与鉴权上下文透传。
