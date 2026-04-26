# Astrsomn Workflow 实例层：运行时领域模型与状态机

## 1. 目标
- 定义实例层统一运行语义，支撑同步 `test-run` 与异步生产执行。
- 将“流程状态 + 节点状态 + 人工任务状态”纳入可恢复状态机。

## 2. 领域对象
- `FlowDefinition`：草稿流程定义。
- `FlowDeployment`：发布后的不可变快照（版本化）。
- `FlowInstance`：一次运行实例（关联 deployment）。
- `NodeExecution`：实例内单节点执行记录（映射 node history）。
- `HumanTask`：人工节点产生的待办任务。
- `RuntimeContext`：实例变量、系统变量、追踪信息容器。

## 3. 实例状态机
- `CREATED`：实例创建，未执行。
- `RUNNING`：执行中。
- `WAITING_HUMAN`：卡在人工节点。
- `SUSPENDED`：人工暂停或系统暂停。
- `COMPLETED`：正常完成。
- `FAILED`：失败终止。
- `CANCELLED`：外部取消。

状态迁移规则：
- `CREATED -> RUNNING`
- `RUNNING -> WAITING_HUMAN | SUSPENDED | COMPLETED | FAILED | CANCELLED`
- `WAITING_HUMAN -> RUNNING | CANCELLED`
- `SUSPENDED -> RUNNING | CANCELLED`

## 4. 节点状态机
- `PENDING`：待执行。
- `READY`：依赖满足可执行。
- `RUNNING`：执行中。
- `SUCCEEDED`：执行成功。
- `FAILED`：执行失败（可重试）。
- `SKIPPED`：条件不满足被跳过。
- `WAITING_HUMAN`：等待人工动作。

## 5. 并行与分支语义
- `parallel`：生成多个子执行分支；分支拥有独立 node token。
- `merge`：默认等待所有上游分支完成（可扩展 quorum 策略）。
- `if-else`：表达式返回布尔并只激活一个下游分支。
- `intent-classifier`：路由到匹配标签分支；未命中走默认分支。

## 6. 失败与恢复语义
- 节点重试：按节点配置 `retryPolicy(maxAttempts, backoffMs)`。
- 实例恢复：从最新可恢复 node token 继续，不重复成功节点。
- 幂等键：`instanceId + nodeId + attemptNo`。
- 超时：节点超时置 `FAILED_TIMEOUT` 并触发重试或失败升级。

## 7. V1/V1.5/V2
- **V1 必做**：实例/节点双状态机、人工挂起恢复、基础重试。
- **V1.5 可选**：分支级补偿、失败分类与熔断策略。
- **V2 增强**：跨流程 saga、子流程嵌套状态机。
