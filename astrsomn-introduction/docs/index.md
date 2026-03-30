---
layout: home

hero:
  name: "Astrsomn 星梦"
  text: "一行注解，即刻开启 AI 进化"
  tagline: >-
    基于 LangChain4j 深度封装，将复杂的 AI 链路转化为一行注解的极致体验。
    为 Java 开发者打造标准化、生产级可用的 AI 集成底座。
  actions:
    - theme: brand
      text: 开始构建 →
      link: /guide/getting-started
    - theme: alt
      text: 设计哲学
      link: /guide/project-overview
    - theme: alt
      text: GitHub
      link: https://github.com/astrsomn/astrsomn

features:
  - title: 🚀 声明式开发
    details: 像注入 Service 一样注入 Agent，彻底屏蔽 LangChain4j 复杂的初始化与低级 API 调用。
  - title: 🔌 MCP 全兼容
    details: 深度适配模型上下文协议（MCP），支持企业内部 WMS/MES 系统插件化接入与动态发现。
  - title: ⚡ 极致响应
    details: 原生支持思维链（Thinking）流式渲染与 Tool 执行全链路追踪，让 AI 决策过程清晰可见。
  - title: 🏗️ 工业级治理
    details: 提供多环境（EnvCode）隔离、Token 成本审计与实例缓存，支撑 AI 从 PoC 稳步迈向生产。
  - title: 🎨 玻璃拟态美学
    details: 采用 ToC 级设计语言，赋予 AI 管理端丝滑的交互体验与极具现代感的视觉质感。
  - title: 🧩 零侵入集成
    details: 基于 Spring Boot Starter 自动装配机制，无需改动现有业务代码即可快速赋能智能化。
---

## 💡 愿景：让 AI 落地回归“工程”

在企业级生产环境下，AI 的挑战不在于“模型本身”，而在于**如何稳定、安全、可控地连接业务**。

> **Astrsomn** 是一套完整的 AI 生态闭环：
> 从 **`astrsomn-core`** 的底层抽象，到 **`astrsomn-starter`** 的无感集成，再到 **`astrsomn-server`** 的中心化治理，我们为 Java 开发者提供了一套开箱即用的“AI 工业底座”。

---

## 🏗️ 核心架构视图

Astrsomn 采用分层解耦架构，确保系统在高并发业务场景下的高可用与易扩展。

| 层级 | 核心组件 | 技术特性 |
| :--- | :--- | :--- |
| **接入层 (Access)** | `@Astro` Annotation / Starter | 零侵入式 Bean 注入，支持多 Agent 实例隔离 |
| **编排层 (Orchestration)** | `AstroAssistantFactory` | 动态构建 AiServices，支持 LLM/Streaming 模式切换 |
| **能力层 (Capabilities)** | `DynamicToolProvider` / MCP | 跨协议工具调用，支持 Spring Bean 方法实时转化为 Tool |
| **治理层 (Governance)** | `AssistantCacheManager` | 内存态管理、Prompt 版本快照、多环境配置下发 |

---

### 方式一：声明式注入 (推荐)
**最简单的接入方式**。通过 `@Astro` 注解，框架会自动从配置中心检索参数，并完成 Agent 的生命周期管理与模型装配。

```java
@Service
public class OrderService {

    // 自动装配：框架根据 agentKey 自动检索配置并注入缓存实例
    @Astro(agentKey = "order-handler", envCode = "prod", temperature = 0.3)
    private OrderCreateAssistant orderAssistant;

    public String createOrder() {
        // 像调用普通方法一样执行 AI 任务
        return orderAssistant.chat("帮我买一个 iPhone17", UUID.randomUUID().toString());
    }
}
```

### 方式二：编程式构建
**更灵活的管控方式**。当你的业务需要根据运行时上下文（如动态调整历史消息数、切换工具策略）来构建 Assistant 时，可以使用 AstroAssistantFactory。

```java
@Service
@RequiredArgsConstructor
public class TaskService {

    private final AstroAssistantFactory assistantFactory;

    public String createTask() {
        // 1. 动态构建请求参数
        var param = AstroChatRequest.of(TaskCreateAssistant.class, "deepseek-sensor");
        param.setMaxHistoryMessages(5);        // 运行时调整上下文长度
        param.setToolStrategy(new ToolStrategy()); // 动态指定工具策略

        // 2. 通过工厂获取/创建 Assistant（内置 AssistantCacheManager 性能优化）
        TaskCreateAssistant assistant = assistantFactory.createAssistant(param);
        
        return assistant.chat("帮我创建一个任务，执行人是刘慧鹏", UUID.fastUUID().toString());
    }
}