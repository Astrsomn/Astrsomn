Astrsomn Workflow 生产级数据库设计方案 (v2.0 架构演进版)

本方案适配 Server -> Workflow -> Starter 三层架构。所有表均继承自 astrsomn-core 中的 BaseEntity。

1. 模块分层说明

astrsomn-core: 定义 BaseEntity 审计基类。

astrsomn-workflow: 定义本文件所述的所有数据库实体（Entity）与业务逻辑引擎。

astrsomn-spring-boot-starter: 负责扫描并加载 astrsomn-workflow 的核心组件。

2. 流程定义域 (Design Time - 由 Server 写入)

2.1 流程主表 ast_flow_definition

name: 流程名称

flow_key: 业务唯一标识（Client 调用凭证）

category: 业务分类（如：生产、质检、通用）

draft_graph_json: 前端 Vue-flow 的 UI 布局草稿

2.2 节点快照表 ast_flow_node_config

flow_definition_id: 关联主表 ID

node_id: UI 节点 ID

node_type: START, LLM, TOOL, CONDITION, HUMAN, END

config_json: 核心配置。包含：

LLM: model_id, prompt_template, temperature

TOOL: bean_name, method_name, parameter_mapping

HUMAN: candidate_group, timeout_minutes

3. 发布与隔离域 (Deployment)

3.1 流程发布表 ast_flow_deployment

flow_definition_id: 关联定义 ID

version: 递增版本号（如 1, 2, 3）

deployed_graph_json: 发布时刻的完整逻辑拓扑快照

is_latest: 是否为当前环境的最新版（Boolean）

4. 实例执行域 (Execution - 由 Starter 写入/更新)

4.1 流程实例表 ast_flow_instance

deployment_id: 关联发布记录 ID

business_key: 业务系统主键（如：订单 ID、工单 ID）

execution_status: RUNNING, SUSPENDED, COMPLETED, FAILED

current_node_id: 当前执行位置

state_json: Context 变量池。存储全链路中间数据。

4.2 执行历史表 ast_flow_node_history

instance_id: 关联实例 ID

node_id: 节点 ID

input_data: 入参快照

output_data: 出参快照

execution_ms: 耗时（毫秒）

5. 交互与审计域 (Interaction)

5.1 人工任务表 ast_flow_human_task

instance_id: 实例 ID

node_id: 节点 ID

task_status: PENDING, APPROVED, REJECTED

payload: 展示给人看的上下文

action_data: 人提交的反馈结果（回写至 state_json）

6. 架构演进建议

实体解耦：将这些表对应的实体类放在 astrsomn-workflow 的 domain.entity 包下。

Starter 增强：在 astrsomn-spring-boot-starter 中提供一个 AstroWorkflowTemplate，业务端只需要：

@Autowired
private AstroWorkflowTemplate workflow;

// 一行代码启动工作流
workflow.start("order_audit_flow", "ORDER_001", variables);


DB 路由：确保 BaseEntity 里的 envCode 始终参与查询，这样 Server 端配置的“测试流”不会被生产环境的 Starter 拉取到。