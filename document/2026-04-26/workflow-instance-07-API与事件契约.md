# Astrsomn Workflow 实例层：API 与事件契约

## 1. 目标
- 补全从“定义管理”到“实例运行”所需 API。
- 定义运行时事件模型，支持 UI 可观测与异步协同。

## 2. API 清单（V1）

### 2.1 定义与发布
- `POST /v1/astro/ai-workflow/publish?id=...`
  - 行为：校验草稿 -> 编译 -> 生成 deployment version -> 切 latest。

### 2.2 测试运行
- `POST /v1/astro/ai-workflow/test-run`
  - 入参：`id? | workflowKey?`, `userMessage`, `memoryKey?`。
  - 出参：`status`, `lastNodeId`, `message`, `variables`, `traceId`。

### 2.3 生产实例
- `POST /v1/astro/ai-workflow/start-instance`
- `GET /v1/astro/ai-workflow/instance/detail?id=...`
- `POST /v1/astro/ai-workflow/instance/cancel`
- `POST /v1/astro/ai-workflow/instance/resume`

### 2.4 人工任务
- `GET /v1/astro/ai-workflow/human-task/queryPage`
- `POST /v1/astro/ai-workflow/human-task/approve`
- `POST /v1/astro/ai-workflow/human-task/reject`

## 3. 事件契约（领域事件）
- `FlowPublishedEvent`
- `InstanceStartedEvent`
  - `instanceId/workflowKey/deploymentVersion/traceId`
- `NodeStartedEvent`
- `NodeCompletedEvent`
- `NodeFailedEvent`
- `HumanTaskCreatedEvent`
- `InstanceCompletedEvent`
- `InstanceFailedEvent`

## 4. 事件字段建议
- 公共字段：`eventId/eventType/eventTime/envCode/traceId/instanceId`。
- 节点事件：`nodeId/nodeType/attemptNo/status/durationMs`。
- 错误事件：`errorCode/errorMessage/retryable`。

## 5. 向前兼容原则
- API 返回统一包裹字段，不移除既有字段，仅新增可选字段。
- 事件采用 schemaVersion，消费者按版本解码。
