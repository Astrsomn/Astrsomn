# Astrsomn Starter 及扩展架构分析报告

## 1. 分析范围与目标

本报告聚焦以下模块及其协同关系：

- `astrsomn-spring-boot-starter`
- `astrsomn-core`
- `astrsomn-providers`
- `astrsomn-vector`
- `astrsomn-workflow-core`
- `astrsomn-workflow-spring-boot-starter`
- `astrsomn-server`（仅分析与 starter 的耦合点）

目标是为后续改建提供依据：梳理当前架构优点、缺点与风险，并给出分阶段可执行改造建议。

## 2. 模块职责总览

- `astrsomn-core`
  - 领域实体、枚举、DTO 基础、LangChain 参数模型与扩展契约（如 `ModelProviderHandler`、`AstroExtensionDescriptor`）。
  - Mapper 接口与 XML 也在 core 中定义。
- `astrsomn-spring-boot-starter`
  - 自动配置入口、数据源与 MyBatis-Plus 装配、模型工厂与助手工厂、工具/RAG 组装、插件加载、注解处理器。
  - 是系统“运行时集成层”。
- `astrsomn-workflow-core`
  - 纯工作流引擎能力（节点、图、执行器），不依赖 Spring 容器。
- `astrsomn-workflow-spring-boot-starter`
  - 将 workflow-core 以 Spring Bean 方式装配，桥接到 starter / server。
- `astrsomn-providers`
  - 各模型厂商适配实现（Qwen/DeepSeek/OpenAI 等）。
- `astrsomn-vector`
  - 向量能力聚合模块（当前与 starter 内向量处理存在职责重叠/脱节）。
- `astrsomn-server`
  - 业务 API 与业务服务。通过依赖 starter 提供能力，并在业务层调用。

## 3. 关键运行链路

### 3.1 启动链路

1. `astrsomn-server` 启动应用。
2. starter 的自动配置加载（数据源、属性、workflow 集成等）。
3. `AstroModelFactory` 初始化可用 provider handler（Spring Bean + SPI 缓存）。
4. `AstrsomnPluginManager` 启动时扫描 `./plugins`，按 SPI 加载外部 provider。
5. `SystemExtensionRegistry` 扫描 `AstroExtensionDescriptor` Bean 并写入 `SYSTEM_EXTENSION`。
6. workflow 侧注册 TASK 处理器，将 workflow 节点与 assistant 调用接通。

### 3.2 模型创建链路

1. 业务层构建 `AstroChatParam`。
2. `AstroAssistantFactory` 拉取并合并运行时配置（Agent + Instance + Tool + MCP + RAG）。
3. `AstroModelFactory` 按 provider 选择 handler 创建模型实例。
4. `AiServices` 装配完成并进入会话缓存。

### 3.3 插件安装 / 应用 / 卸载链路（当前实现）

- 安装：`SystemExtensionController.create` -> `SystemExtensionService.create`
  - 入库，默认 `status=INSTALLED`、`applied=N`。
- 应用：`SystemExtensionController.apply` -> `SystemExtensionService.apply`
  - 调 `AstrsomnPluginManager.applyPlugin(jarName)` 动态加载；
  - 成功后更新 `status=APPLIED`、`applied=Y`。
- 卸载：`SystemExtensionController.uninstall` -> `SystemExtensionService.uninstall`
  - 调 `AstrsomnPluginManager.unloadPlugin(jarName)`；
  - 更新 `status=UNINSTALLED`、`applied=N`。

### 3.4 工具注册链路

1. 运行时根据 `toolKeys` 查工具表。
2. 通过 `beanName + methodName` 在容器中解析并反射绑定。
3. 组装为 LangChain4j `ToolProvider`。

## 4. 扩展点盘点

- SPI 扩展：`ModelProviderHandler`
  - 支持 classpath SPI 与插件目录 SPI。
- Spring 扩展：
  - `ModelProviderHandler` Bean 注入；
  - 各类 `BeanPostProcessor`（如 `@Astro` 注入、工具注解处理）。
- 注解扩展：
  - `@Astro`、`@AstroToolGroup` 等。
- 配置扩展：
  - `astrsomn.*` 统一配置，支持环境维度。
- 描述扩展：
  - `AstroExtensionDescriptor` 及其入库同步机制。

## 5. 架构优点

- 模块边界总体清晰：core（契约）与 starter（运行时集成）分层明确。
- 自动配置组织较完整：可通过条件注解控制装配开关。
- Provider 扩展方式灵活：同时支持静态依赖和外部插件加载。
- 工作流与助手能力有可复用桥接点：TASK 节点可直接调 agent。
- 运行时参数可配置化：模型、工具、MCP、RAG 可由 DB 驱动。
- 工具调用模型具备动态性：无需每次改代码重编译即可调整工具集合。
- 已开始形成“安装-应用-卸载”的插件生命周期语义。

## 6. 缺点与风险（按优先级）

### 高优先级

- Provider 实际可发现性存在不确定性
  - 部分 provider 既非 Spring Bean，也缺 SPI 配置时，运行时可能“依赖已引入但不可用”。
- 插件卸载与助手缓存一致性缺失
  - 卸载后既有缓存 assistant 可能仍持有旧行为。
- RAG 向量注册链路未完全闭环
  - 向量 handler 与 registry 之间职责连接不够清晰，存在运行期失败风险。

### 中优先级

- 插件状态存在双轨来源
  - `SystemExtensionRegistry` 启动写状态与手动 apply/uninstall 存在并行语义，易不一致。
- 插件热更新能力弱
  - 同名 jar 重载策略较保守，运维替换 jar 体验不佳。
- handler 冲突策略未制度化
  - 同 provider 多实现时覆盖逻辑不可观测。
- providers 与聚合模块编排不完全一致
  - 仓库存在模块与聚合声明差异，可能引发构建/发布偏差。

### 低优先级

- 部分异常路径只记录日志，不易追踪告警。
- 代码中仍有若干“历史遗留 warning”，影响长期可维护性。

## 7. 改建建议（分阶段）

### 短期（1~2 周）

- 明确 provider 注册单一标准
  - 推荐：内置模块使用 Spring Bean + AutoConfiguration；外部插件使用 SPI。
- 为 apply/uninstall 补缓存失效策略
  - 在 `AssistantCacheManager` 增加按 provider / extension 定向失效。
- 统一扩展状态机
  - 规范 `INSTALLED/APPLIED/UNINSTALLED` 的状态迁移和触发事件。

### 中期（2~4 周）

- 插件生命周期治理
  - 加入版本校验、同名 jar 覆盖策略、失败回滚。
- 扩展管理能力增强
  - 增加“检测（discover）-安装（install）-应用（apply）-停用（disable）-卸载（uninstall）”完整接口。
- RAG 与 vector 能力归一
  - 将向量实现集中到 vector 模块，starter 只保留编排与路由。

### 长期（1~2 月）

- 构建统一扩展平台层
  - 把 DB 元数据、类加载状态、服务可用状态统一到可观测控制面。
- 建立兼容矩阵与契约测试
  - provider、workflow、tool、vector 各插件都应具备契约级自动化测试。

## 8. 对“安装后点击应用才生效”的评价

当前你要的模型方向是对的：把“元数据登记”和“运行时加载”解耦，可以显著提升安全性与运维可控性。  
建议下一步再补三件事：

- 应用前校验（jar 存在、SPI 完整、版本兼容）
- 应用后健康检查（handler 可发现、试调用）
- 卸载后一致性处理（缓存清理 + 状态审计）

## 9. 关键文件参考（核心）

- `astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/factory/AstroModelFactory.java`
- `astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/plugin/AstrsomnPluginManager.java`
- `astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/factory/AstroAssistantFactory.java`
- `astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/runtime/ToolProviderAssembler.java`
- `astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/tool/rag/RagComponentAssembler.java`
- `astrsomn-core/src/main/java/org/astrsomn/core/common/langchain/extension/ModelProviderHandler.java`
- `astrsomn-core/src/main/java/org/astrsomn/core/common/langchain/extension/AstroExtensionDescriptor.java`
- `astrsomn-server/src/main/java/org/astrsomn/server/service/impl/SystemExtensionServiceImpl.java`
- `astrsomn-server/src/main/java/org/astrsomn/server/api/SystemExtensionController.java`
- `astrsomn-server/src/main/java/org/astrsomn/server/plugin/SystemExtensionRegistry.java`

