
# 🌟 Astrsomn

<p align="center">
  <img src="astrsomn-ui/src/assets/Astrsomn-logo.png" alt="Astrsomn Logo" width="200"/>
</p>


<p align="center">
  <a href="README-EN.md"><img src="https://img.shields.io/badge/Language-English-blue.svg" alt="English"></a>
  <a href="README.md"><img src="https://img.shields.io/badge/Language-Chinese-red.svg" alt="中文"></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg" alt="Apache 2.0 License"></a>
  <a href="https://github.com/langchain4j/langchain4j"><img src="https://img.shields.io/badge/Powered%20by-LangChain4j-orange.svg" alt="Powered by LangChain4j"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Build-Maven-blue.svg" alt="Maven Build"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg" alt="Spring Boot 3.2+"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/issues"><img src="https://img.shields.io/github/issues/Astrsomn/Astrsomn.svg" alt="GitHub Issues"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/stargazers"><img src="https://img.shields.io/github/stars/Astrsomn/Astrsomn.svg" alt="GitHub Stars"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/network/members"><img src="https://img.shields.io/github/forks/Astrsomn/Astrsomn.svg" alt="GitHub Forks"></a>
</p>

## ⚡ 30 秒快速了解

- **这是什么**：一个基于 LangChain4j 封装的 Java AI Starter
- **你能得到什么**：一行注解 `@Astro` 快速发起模型调用，支持多 Provider 可插拔切换
- **它解决什么问题**：把 LangChain4j 的复杂配置收敛为标准化配置，并提供可视化管理能力
- **怎么开始**：先引入 `astrsomn-runtime-starter` 和 Provider，再按 `快速开始` 完成 YAML 配置与启动

> [!WARNING]
> ## 当前为半成品（开发中）
> 这个项目目前**尚未完成**，仍在快速迭代阶段，功能、配置和 API 都可能发生变化。  
> **请勿用于生产环境**，仅建议用于学习、体验与反馈。

***

## 📖 项目介绍

**Astrsomn** 是一个基于 LangChain4j 封装的 Java AI 集成框架：你只需引入一个 Starter 依赖，就能快速接入多模型能力。\
框架把 LangChain4j 的复杂工程配置（模型接入、Provider 选择、运行参数等）标准化并可视化，降低上手门槛。

你可以直接获得这些能力：

- 一行注解 `@Astro` 即可在业务代码中发起模型调用
- 通过 `runtime-starter + provider` 快速切换 DeepSeek、Zhipu 等模型
- 把原本分散的 LangChain4j 配置收敛为统一配置项，并支持可视化管理
- 保持对原生 LangChain4j 能力的兼容，便于渐进扩展到 Agent / RAG / Tools

***

## 🏗️ 项目施工中 🏗️

| 状态 | 功能模块           | 说明                 |
| ---- | -------------- |--------------------|
| ✅  | **Agent 生命周期** | 已完成创建、配置、管理能力      |
| ✅  | **环境初始化**      | 支持快速环境配置与初始化       |
| ✅  | **依赖快速引入**     | Maven Starter 一键集成 |
| ✅  | **基本配置功能**     | 提供核心配置管理能力         |
| ✅  | **RAG 能力**     | 已完成向量检索与知识库能力      |
| 🔄 | **工作流模块**      | 计划 2.0 版本发布        |
| ✅  | **向量库集成**      | 支持 Qdrant 向量数据库    |
| ❌  | **安全与治理**      | 限流、监控等功能待完善        |
| ⚠️ | **API 兼容性**    | 可能随时变更，不保证向后兼容     |

**⚠️ 请勿用于生产环境！⚠️**

本项目正在积极开发中，欢迎各位朋友一起参与完善！
详见 [贡献指南](#🤝-贡献指南)

***

你可以把 Astrsomn 理解为一套完整闭环：

- `core`：统一抽象与通用能力
- `starter`：Spring Boot 自动装配与接入层
- `providers`：模型提供方能力实现
- `vector`：向量存储能力实现
- `server`：服务化运行与治理
- `ui`：可视化配置与运维

**适用场景**：RAG 知识库、智能对话、MCP 工具接入、企业 AI 中台等。

***

> **一行注解，即刻开启 AI 进化**\
> 基于 LangChain4j 深度封装，面向 Java 的标准化、生产级 AI 集成底座

## ✨ 核心特性

| 特性            | 描述                                                                                    |
| ------------- | ------------------------------------------------------------------------------------- |
| 🔧 **深度封装**   | 完整封装 LangChain4j 能力（LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent） |
| 🚀 **零侵入接入**  | SpringBoot Starter 自动配置，一行注解即可集成                                                      |
| 🔌 **可插拔设计**  | Provider/Vector 可插拔架构，支持多模型与多向量库治理                                                    |
| 📡 **流式响应**   | 支持流式响应、工具调用、记忆、RAG、编排等完整链路                                                            |
| 📊 **可观测能力**  | 面向生产的可观测与可维护能力（配置、日志、成本、缓存）                                                           |
| 🔒 **企业级特性**  | 支持多模型、多租户、限流、监控、日志等企业级特性                                                              |
| 🛡️ **高可用保障** | 内置熔断、降级、故障转移机制，确保服务稳定性与可靠性                                                            |
| 🔗 **原生兼容**   | 完全兼容原生 LangChain4j，无缝扩展                                                               |

***

## 🛠️ 技术栈

| 类别    | 技术                               |
| ----- | -------------------------------- |
| 后端框架  | Spring Boot 3.3.0                |
| 语言    | Java 17+                         |
| AI 集成 | LangChain4j 1.11.x               |
| 前端框架  | Vue 3 + TypeScript               |
| 构建工具  | Maven                            |
| 数据库   | MySQL                            |
| 向量数据库 | Qdrant / Milvus / Chroma / Redis |

### 🔑 关键依赖与主版本（快速了解）

| 依赖坐标                                             | 主版本              | 用途                            |
| ------------------------------------------------ | ---------------- | ----------------------------- |
| `com.astrsomn:astrsomn-runtime-starter`          | `0.2.0-SNAPSHOT` | 一站式接入入口，提供注解注入与运行时能力          |
| `dev.langchain4j:langchain4j-core`               | `1.11.x`         | LangChain4j 核心抽象与调用能力         |
| `dev.langchain4j:langchain4j-open-ai`            | `1.11.x`         | OpenAI 协议模型接入（DeepSeek 等兼容场景） |
| `dev.langchain4j:langchain4j-community-zhipu-ai` | `1.11.0-beta19`  | 智谱模型接入能力                      |
| `org.springframework.boot:spring-boot-starter`   | `3.3.x`          | Spring Boot 运行与自动配置基础         |
| `com.baomidou:mybatis-plus-spring-boot3-starter` | `3.5.x`          | 数据访问与配置持久化基础能力                |

> 说明：文档使用"主版本"帮助快速判断兼容范围；精确补丁版本以各模块 `pom.xml` 为准。

***

## 🧩 模块结构

```text
Astrsomn
├── astrsomn-common                    # 通用基础模块（工具类、基础实体、异常定义）
├── astrsomn-api                       # API 接口定义层
│   ├── astrsomn-api-runtime           # 运行时 API（异常枚举、错误码）
│   ├── astrsomn-api-storage           # 存储 API（文件处理等）
│   └── astrsomn-api-workflow          # 工作流 API（规划中）
├── astrsomn-integrations              # 集成层（Spring Boot Starter）
│   ├── astrsomn-runtime-starter       # 运行时 Starter（AI 模型、工具、MCP 等）
│   │   └── route                      # 模型路由模块（高可用保障）
│   │       ├── ModelRouteCompositeFactory      # 路由组合工厂
│   │       ├── CompositeChatModel              # 组合聊天模型（故障转移）
│   │       ├── CompositeStreamingChatModel    # 组合流式模型
│   │       ├── EndpointSelectionStrategy       # 端点选择策略（负载均衡）
│   │       └── ResilienceDecorationStrategy    # 弹性装饰策略（熔断、降级）
│   ├── astrsomn-workflow-starter      # 工作流 Starter（规划中）
│   └── astrsomn-internal-storage      # 内部存储实现
├── astrsomn-plugins                   # 插件生态
│   ├── astrsomn-providers             # 模型提供方实现（当前可用：DeepSeek / Zhipu）
│   └── astrsomn-vector                # 向量存储实现（规划中）
├── astrsomn-server                    # 服务端应用（HTTP 接口、服务化运行）
└── astrsomn-ui                        # 前端控制台（Vue 3 + TypeScript）
```

***

## 🔗 依赖分层

当前按以下方向演进（便于后续 SPI 与自定义 ClassLoader 增强）：

- `server` 依赖：`integrations`、`plugins`
- `integrations` 依赖：`api`、`common`
- `plugins` 依赖：`api`
- `api` 依赖：`common`

***

## 🚀 快速开始

### 环境要求

| 环境      | 版本要求      |
| ------- | --------- |
| JDK     | 21+       |
| Maven   | 3.8+      |
| MySQL   | 8.0+（推荐）  |
| Node.js | 18+（前端开发） |

***

### 一、项目启动

克隆仓库后，在本地同时运行后端服务和前端控制台。

#### 1. 后端

##### 1.1 准备数据库

确保 MySQL 可访问（本机或远程均可），创建数据库：

```sql
CREATE DATABASE astro_ai DEFAULT CHARACTER SET utf8mb4;
```

##### 1.2 修改配置

编辑 `astrsomn-server/src/main/resources/application-mysql.yml`，修改数据库连接信息：

**必须修改项**：

- `datasource.url` 中的 `host`、`port`、数据库名
- `datasource.username`
- `datasource.password`

```yml
astrsomn:
  enabled: true
  datasource:
    # 启动前请改成你的实例：单行 jdbc:mysql URL
    url: jdbc:mysql://127.0.0.1:3306/astro_ai?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10
      minimum-idle: 5

```

##### 1.3 启动后端

在 IDE 中运行 `astrsomn-server` 模块的 `AstrsomnServerApplication.java`。

- 首次启动会自动执行 Flyway 迁移（默认开启）。
- 确认成功：日志中出现 `Started ...`，监听端口为 `4481`（默认）。

#### 2. 前端

前端分为基础组件包（`astrsomn-ui-packages`）和主应用（`astrsomn-ui`）。
组件包需先手动构建，主应用通过 `file:` 协议引用它们。

##### 2.1 构建基础组件包

```bash
cd astrsomn-ui-packages/astro-chat-core
npm install
npm run build
```

> `astro-chat-vue` 是纯源码导出（`exports` 直接指向 `./src/index.ts`），无需单独构建；但其源码中直接 import 了 `markdown-it`、`highlight.js` 等第三方包，因此需要先 `npm install` 安装依赖。

```bash
cd astrsomn-ui-packages/astro-chat-vue
npm install
```

##### 2.2 启动前端主应用

```bash
cd astrsomn-ui
npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`（默认端口），确保后端 `4481` 端口可访问。

***

### 二、作为依赖接入你的项目

如果你想在自己的业务项目中接入 Astrsomn 的 AI 能力，按以下步骤操作。

#### 1. 引入 Maven 依赖

**1) runtime-starter**：

```xml
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-runtime-starter</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```

**2) 选择并引入可用 Provider（至少一个）**：

```xml
<!-- DeepSeek Provider -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-provider-deepseek</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>

<!-- Zhipu Provider -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-provider-zhipu</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```

> 当前仅验证并支持：`astrsomn-runtime-starter` + `astrsomn-provider-deepseek` / `astrsomn-provider-zhipu`。
> 其他 Starter/Provider 将在后续版本逐步开放。

**3) 以下依赖需要由使用方项目自行提供（runtime-starter 中为 provided）**：

```xml
<!-- Spring Boot 基础 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>

<!-- JDBC + MyBatis-Plus -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>

<!-- 数据库驱动（至少一个；MySQL 示例） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- 配置校验 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- 响应式流能力（按需） -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
```

#### 2. 添加配置

确保激活 `mysql` profile，在你的 `application.yml` 中添加：

```yml
spring:
  profiles:
    active: mysql
```

然后在 `application-mysql.yml` 中配置数据库连接，格式参考上方「1.2 修改配置」。

#### 3. 在业务代码中使用 `@Astro`

```java
@Service
public class MyService {
    @Astro(agentKey = "MY-AGENT", envCode = "PRO")
    private AstroChatAssistant assistant;

    public void demo() {
        String response = assistant.chat("你好，请介绍一下自己");
    }
}
```

***

## 📚 官方网站与文档

| 类型           | 链接                                                        |
| ------------ | --------------------------------------------------------- |
| 🏠 官方网站      | [astrsomn.com](https://www.astrsomn.com/home.html)        |
| 📖 官方文档      | [doc.astrsomn.com](https://doc.astrsomn.com)              |
| 💻 GitHub 仓库 | [Astrsomn/Astrsomn](https://github.com/Astrsomn/Astrsomn) |

***

## 📚 文档与资源

| 文档类型      | 链接                                                                        |
| --------- | ------------------------------------------------------------------------- |
| 📖 系统设计文档 | [系统设计文档](document/系统设计文档.md)                                              |
| 🌐 中文介绍站点 | [astrsomn-introduction](astrsomn-introduction/)                           |
| 📝 英文文档   | [README-EN.md](README-EN.md)                                              |
| 📋 版本路线规划 | [版本路线规划-1x到2x](document/2026-04-17/版本路线规划-1x到2x.md)                       |
| 🗂️ 工作流设计 | [AI工作流标准化路线图](document/2026-04-26/ai-workflow-standardization-roadmap.md) |
| 📐 向量存储设计 | [向量存储扩展设计规范](document/2026-04-06/向量存储扩展设计规范.md)                           |
| 📊 扩展系统架构 | [扩展系统架构分析](document/2026-04-15/扩展系统架构分析.md)                               |

***

## 🤝 贡献指南

欢迎贡献代码！我们非常感谢任何形式的贡献，包括但不限于：

### 贡献方式

- 💡 [提交 Issue](https://github.com/Astrsomn/Astrsomn/issues) - 报告 bug 或提出功能建议
- 📝 [提交 Pull Request](https://github.com/Astrsomn/Astrsomn/pulls) - 贡献代码
- 📖 完善文档 - 帮助改进项目文档
- 🗣️ 社区交流 - 在讨论区分享使用经验

### 贡献流程

1. **Fork 本仓库** → [Astrsomn/Astrsomn](https://github.com/Astrsomn/Astrsomn)
2. **创建功能分支** (`git checkout -b feature/your-feature`)
3. **提交更改** (`git commit -m 'Add some feature'`)
4. **推送到分支** (`git push origin feature/your-feature`)
5. **创建 Pull Request** → [提交 PR](https://github.com/Astrsomn/Astrsomn/pulls)

### 贡献规范

- 请遵循 [代码风格指南](document/2026-04-02/开源完善总方案.md)
- 提交前请确保通过所有测试
- 提供清晰的 commit 信息和 PR 描述

***

## 📦 扩展生态

### 模型提供方

- [DeepSeek Provider](astrsomn-plugins/astrsomn-providers/astrsomn-provider-deepseek/)
- [Zhipu Provider](astrsomn-plugins/astrsomn-providers/astrsomn-provider-zhipu/)
- OpenAI Provider（待开放）
- Qwen Provider（待开放）
- Qianfan Provider（待开放）

### 向量存储

- [Chroma Vector](astrsomn-vector/astrsomn-vector-chroma/)

***

## 💬 社区与交流

<p align="center">
  <a href="https://github.com/Astrsomn/Astrsomn/discussions"><img src="https://img.shields.io/badge/Discussions-GitHub-blue?style=flat-square&logo=github" alt="GitHub Discussions"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/issues"><img src="https://img.shields.io/badge/Issues-GitHub-green?style=flat-square&logo=github" alt="GitHub Issues"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/pulls"><img src="https://img.shields.io/badge/PRs-Welcome-orange?style=flat-square&logo=github" alt="PRs Welcome"></a>
  <a href="mailto:astrsomn@outlook.com"><img src="https://img.shields.io/badge/Email-astrsomn@outlook.com-red?style=flat-square&logo=gmail" alt="Email"></a>
</p>

- 💬 **讨论区**：[GitHub Discussions](https://github.com/Astrsomn/Astrsomn/discussions) - 分享使用经验、交流想法
- 🐛 **问题反馈**：[GitHub Issues](https://github.com/Astrsomn/Astrsomn/issues) - 报告 Bug、提出功能建议
- 🔧 **贡献代码**：[Pull Requests](https://github.com/Astrsomn/Astrsomn/pulls) - 欢迎提交 PR
- 📧 **联系邮箱**：<astrsomn@outlook.com>

***

<p align="center">
  Made with ❤️ by the Astrsomn Team
</p>

<p align="center">
  <a href="https://www.astrsomn.com/home.html"><img src="https://img.shields.io/badge/Website-astrsomn.com-blue.svg" alt="Astrsomn Website"></a>
  <a href="https://doc.astrsomn.com"><img src="https://img.shields.io/badge/Documentation-doc.astrsomn.com-green.svg" alt="Documentation"></a>
  <a href="https://github.com/Astrsomn/Astrsomn"><img src="https://img.shields.io/badge/GitHub-Astrsomn-blue.svg" alt="GitHub"></a>
  <a href="https://gitter.im/Astrsomn/community"><img src="https://img.shields.io/badge/Gitter-Join%20Chat-green.svg" alt="Gitter"></a>
</p>
