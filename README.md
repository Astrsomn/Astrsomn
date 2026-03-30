# 🌟 Astrsomn 星梦
## 企业级 LangChain4j 工程化框架
> 一行注解，即刻开启 AI 进化  
> 基于 LangChain4j 深度封装，面向 Java 的标准化、生产级 AI 集成底座

---

## 📖 项目介绍
**Astrsomn（星梦）** 致力于解决 Java 生态接入大模型时常见的工程问题：配置复杂、依赖耦合、能力扩展困难、运行治理不足。  
框架通过分层模块化与标准化封装，让开发者既能快速上手，也能在企业场景中长期演进。

你可以把 Astrsomn 理解为一套完整闭环：
- `core`：统一抽象与通用能力
- `starter`：Spring Boot 自动装配与接入层
- `providers`：模型提供方能力实现
- `vector`：向量存储能力实现
- `server`：服务化运行与治理
- `ui`：可视化配置与运维

适用场景：RAG 知识库、智能对话、Agent 编排、MCP 工具接入、企业 AI 中台等。

---

## ✨ 核心特性
- 深度封装 LangChain4j，保留原生扩展能力
- 声明式注入与编程式构建并存，兼顾易用与灵活
- Starter 零侵入接入，支持业务系统平滑升级
- Provider/Vector 可插拔，便于多模型与多向量库治理
- 支持流式响应、工具调用、记忆、RAG、编排等完整链路
- 面向生产的可观测与可维护能力（配置、日志、成本、缓存）

---

## 🧩 模块结构（当前）
```text
Astrsomn
├── astrsomn-core
├── astrsomn-spring-boot-starter
├── astrsomn-workflow-core
├── astrsomn-workflow-spring-boot-starter
├── astrsomn-providers
│   ├── astrsomn-provider-openai
│   ├── astrsomn-provider-deepseek
│   └── astrsomn-provider-qwen
├── astrsomn-vector
│   ├── astrsomn-vector-qdrant
│   ├── astrsomn-vector-chroma
│   ├── astrsomn-vector-milvus
│   └── astrsomn-vector-redis
├── astrsomn-server
├── astrsomn-ui
└── astrsomn-introduction
```

---

## 🔗 依赖分层（目标）
当前按以下方向演进（便于后续 SPI 与自定义 ClassLoader 增强）：

- `server` 依赖：`starter`、`providers`、`vector`
- `starter` 依赖：`core`（及编排相关核心）
- `providers` 依赖：`core`
- `vector` 依赖：`core`

> 说明：为保证现阶段稳定性，`core` 中部分模型相关依赖暂时保留，后续会随代码拆分逐步迁移到对应子模块。

---

## 🚀 快速开始
1. 启动基础依赖（数据库、向量库等）。
2. 配置 `astrsomn-server` 的数据源与模型参数。
3. 启动 `astrsomn-server`。
4. 启动 `astrsomn-ui` 进行可视化配置与调试。

---

## 📚 文档与说明
- 中文介绍站点源码：`astrsomn-introduction`
- 英文 README：`README-EN.md`
- 系统设计文档：`document/系统设计文档.md`