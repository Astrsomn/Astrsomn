# Astrsomn Workflow 实例层：节点执行器规范

## 1. 目标
- 为全部前端节点提供统一后端执行规范。
- 解耦编排引擎与基础 AI/MCP/工具实现。

## 2. 执行器 SPI
```java
public interface NodeExecutor {
    String type();
    NodeResult execute(NodeExecutionContext ctx) throws Exception;
}
```

- `NodeExecutionContext`：包含 `instanceId/nodeId/inputVars/config/systemCtx`。
- `NodeResult`：`status/outputVars/routeSignals/metrics`。

## 3. 注册机制
- `NodeExecutorRegistry`：`type -> executor` 映射。
- Spring 自动注入 + 启动期唯一性校验（禁止重复 type）。
- 支持扩展包通过 starter 自动发现执行器。

## 4. 全节点映射（V1）
- `start`：初始化输入变量与系统上下文。
- `end`：构建最终响应结果。
- `llm`：调用 `AstroAssistantFactory`，输出文本与用量。
- `retrieval`：调用 RAG 检索组件，输出 chunks/references。
- `if-else`：表达式求值后输出路由信号。
- `intent-classifier`：LLM 分类并输出目标分支标签。
- `parallel`：拆分 branch token。
- `merge`：按策略聚合分支变量。
- `http`：执行 HTTP 请求（白名单 + 超时 + 重试）。
- `code`：执行受限脚本（沙箱、资源限额）。
- `tools`：调用 `ToolProviderAssembler` 聚合的工具/MCP能力。

## 5. 错误语义
- 业务错误：节点返回 `FAILED_BUSINESS`。
- 依赖错误：返回 `FAILED_DEPENDENCY`（可重试）。
- 超时错误：返回 `FAILED_TIMEOUT`（可重试）。
- 未知错误：返回 `FAILED_UNKNOWN`。

## 6. 安全与治理
- `http/code/tools` 节点必须启用安全策略：
  - 超时
  - 资源上限
  - 白名单/黑名单
  - 脱敏日志

## 7. 分级实现
- **V1 必做**：11 类节点统一 SPI + 基础执行与错误模型。
- **V1.5 可选**：节点级熔断、批处理执行器。
- **V2 增强**：多租户插件化节点市场。
