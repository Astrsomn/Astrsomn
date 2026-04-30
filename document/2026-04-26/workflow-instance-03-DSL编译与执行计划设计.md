# Astrsomn Workflow 实例层：DSL 编译与执行计划设计

## 1. 目标
- 将前端 `graphJson` 编译为后端可执行计划 `ExecutablePlan`。
- 在 `publish` 时完成严格校验，在 `test-run` 时支持快速编译。

## 2. 编译流程
1. `Parse`：解析 `nodes/edges/meta`。
2. `Normalize`：补齐默认值、标准化节点类型编码。
3. `Validate`：图合法性与节点配置校验。
4. `TopologyBuild`：构建 DAG/分支图与入度索引。
5. `PlanEmit`：输出 `ExecutablePlan`（JSON + version hash）。

## 3. 校验规则
- 必须且仅允许一个 `start`。
- 至少一个 `end` 且可达。
- 无孤立节点、无非法环（V1 不支持循环）。
- 每个 node 的 `config` 通过对应 `NodeSpec` 校验。
- 每条边必须存在合法 `sourceHandle/targetHandle`。
- 值类型检查：`outputs.valueType` 到下游 `inputs.valueType` 可赋值。

## 4. ExecutablePlan 建议结构
- `planId`：`deploymentId + version` 派生。
- `version`：发布版本号。
- `startNodeId`：入口节点。
- `nodeSpecs`：节点静态定义（type/config/inputPorts/outputPorts）。
- `transitions`：路由定义（条件表达式/标签路由/并行聚合策略）。
- `runtimePolicies`：超时、重试、限流、并发上限。

## 5. 发布冻结策略
- `publish` 时将 `draftGraphJson` 编译并落地 `deployedGraphJson + compiledPlanJson`。
- 实例运行只读 `deployment`，不读取草稿定义。
- 发布失败时不更新 latest 标识，保持旧版本可运行。

## 6. test-run 策略
- 若传 `id`：编译草稿后执行（不入 deployment）。
- 若传 `workflowKey`：读取 latest deployment 的 compiled plan 执行。
- test-run 默认轻量持久化（仅可选记录调试日志）。

## 7. 版本与兼容
- 节点规范版本字段：`nodeSpecVersion`。
- 编译器版本字段：`compilerVersion`。
- 执行器需声明可支持的 spec 版本范围。
