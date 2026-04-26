# Astrsomn Workflow 实例层：数据模型与存储策略

## 1. 目标
- 在现有 5 张核心表基础上，支持发布冻结、实例恢复、节点审计、人工任务闭环。

## 2. 现有表扩展建议

### 2.1 `AST_FLOW_DEFINITION`
- 新增：`VALIDATION_STATUS`、`DSL_SCHEMA_VERSION`。
- 说明：草稿有效性与 DSL 版本追踪。

### 2.2 `AST_FLOW_DEPLOYMENT`
- 新增：`COMPILED_PLAN_JSON`、`PLAN_HASH`、`PUBLISHED_BY`、`PUBLISHED_AT`。
- 说明：发布即冻结执行计划，避免运行时重复编译。

### 2.3 `AST_FLOW_INSTANCE`
- 新增：`REQUEST_ID`、`STARTED_AT`、`FINISHED_AT`、`ERROR_CODE`、`ERROR_MESSAGE`、`TRACE_ID`。
- 说明：增强运行追踪和幂等控制。

### 2.4 `AST_FLOW_NODE_HISTORY`
- 新增：`ATTEMPT_NO`、`STATUS`、`ERROR_CODE`、`ERROR_MESSAGE`、`START_TIME`、`END_TIME`、`TRACE_SPAN_ID`。
- 说明：支持重试、时序分析、根因定位。

### 2.5 `AST_FLOW_HUMAN_TASK`
- 新增：`ASSIGNEE`、`DEADLINE`、`APPROVAL_COMMENT`、`RESOLVED_AT`。
- 说明：支持审批流程和超时治理。

## 3. 建议新增表（V1.5）
- `AST_FLOW_INSTANCE_EVENT`：实例事件流（状态迁移、外部回调）。
- `AST_FLOW_INSTANCE_LOCK`：分布式执行锁（或改为 Redis 锁）。
- `AST_FLOW_DLQ`：死信任务记录。

## 4. 索引策略
- 实例表：`(ENV_CODE, EXECUTION_STATUS, UPDATE_TIME)`。
- 历史表：`(INSTANCE_ID, NODE_ID, ATTEMPT_NO)`。
- 人工表：`(TASK_STATUS, ASSIGNEE, UPDATE_TIME)`。
- 部署表：`(FLOW_DEFINITION_ID, IS_LATEST)`。

## 5. 存储策略
- 热数据：实例、人工任务、近 7 天节点历史。
- 冷数据：长期历史归档到对象存储或历史库。
- 大字段（input/output/state）建议压缩与可选脱敏。

## 6. 一致性策略
- 发布操作采用事务：写 deployment + 切 latest + 记录审计。
- 节点执行采用“状态机更新 + 历史落库”同事务边界（或可靠事件补偿）。

## 7. 多环境隔离
- 复用 `env_code` 过滤策略，不信任前端传值。
- 所有实例查询与写入均由服务端注入生效环境。
