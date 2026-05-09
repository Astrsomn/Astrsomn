# 🌟 Astrsomn 星梦

<p align="center">
  <img src="astrsomn-ui/src/assets/Astrsomn-logo.png" alt="Astrsomn Logo" width="200"/>
</p>

> **一行注解，即刻开启 AI 进化**\
> 基于 LangChain4j 深度封装，面向 Java 的标准化、生产级 AI 集成底座

<p align="center">
  <a href="README-EN.md"><img src="https://img.shields.io/badge/Language-English-blue.svg" alt="English"></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg" alt="Apache 2.0 License"></a>
  <a href="https://github.com/langchain4j/langchain4j"><img src="https://img.shields.io/badge/Powered%20by-LangChain4j-orange.svg" alt="Powered by LangChain4j"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Build-Maven-blue.svg" alt="Maven Build"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg" alt="Spring Boot 3.2+"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/issues"><img src="https://img.shields.io/github/issues/Astrsomn/Astrsomn.svg" alt="GitHub Issues"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/stargazers"><img src="https://img.shields.io/github/stars/Astrsomn/Astrsomn.svg" alt="GitHub Stars"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/network/members"><img src="https://img.shields.io/github/forks/Astrsomn/Astrsomn.svg" alt="GitHub Forks"></a>
</p>

> [!WARNING]
> ## 当前为半成品（开发中）
> 这个项目目前**尚未完成**，仍在快速迭代阶段，功能、配置和 API 都可能发生变化。  
> **请勿用于生产环境**，仅建议用于学习、体验与反馈。

***

## 📖 项目介绍

**Astrsomn（星梦）** 致力于解决 Java 生态接入大模型时常见的工程问题：配置复杂、依赖耦合、能力扩展困难、运行治理不足。\
框架通过分层模块化与标准化封装，让开发者既能快速上手，也能在企业场景中长期演进。

***

## 🏗️ 项目施工中 🏗️

| 状态 | 功能模块           | 说明                 |
| -- | -------------- | ------------------ |
| ✅  | **Agent 生命周期** | 已完成创建、配置、管理能力      |
| ✅  | **环境初始化**      | 支持快速环境配置与初始化       |
| ✅  | **依赖快速引入**     | Maven Starter 一键集成 |
| ✅  | **基本配置功能**     | 提供核心配置管理能力         |
| ❌  | **工作流模块**      | 规划中，尚未开放可用版本      |
| ❌  | **向量库集成**      | 部分实现，不稳定           |
| ❌  | **安全与治理**      | 多租户、限流、监控等功能缺失     |
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

## ✨ 核心特性

| 特性           | 描述                                                                                    |
| ------------ | ------------------------------------------------------------------------------------- |
| 🔧 **深度封装**  | 完整封装 LangChain4j 能力（LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent） |
| 🚀 **零侵入接入** | SpringBoot Starter 自动配置，一行注解即可集成                                                      |
| 🔌 **可插拔设计** | Provider/Vector 可插拔架构，支持多模型与多向量库治理                                                    |
| 📡 **流式响应**  | 支持流式响应、工具调用、记忆、RAG、编排等完整链路                                                            |
| 📊 **可观测能力** | 面向生产的可观测与可维护能力（配置、日志、成本、缓存）                                                           |
| 🔒 **企业级特性** | 支持多模型、多租户、限流、监控、日志等企业级特性                                                              |
| 🔗 **原生兼容**  | 完全兼容原生 LangChain4j，无缝扩展                                                               |

***

## 🛠️ 技术栈

| 类别    | 技术                               |
| ----- | -------------------------------- |
| 后端框架  | Spring Boot 3.3.0                |
| 语言    | Java 17+                         |
| AI 集成 | LangChain4j                      |
| 前端框架  | Vue 3 + TypeScript               |
| 构建工具  | Maven                            |
| 数据库   | MySQL / H2                       |
| 向量数据库 | Qdrant / Milvus / Chroma / Redis |

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

### 一行注解，开启 Java AI 进化

```java
@Service
public class MyService {
    // 一行注解，注入 AI 能力
    @Astro(agentKey = "MY-AGENT", envCode = "PRO")
    private AstroChatAssistant assistant;

    public void demo() {
        String response = assistant.chat("你好，请介绍一下自己");
    }
}
```

### 环境要求

- JDK 21+
- Spring Boot 3.2+
- Maven 3.8+
- MySQL 8.0+（推荐）

### 步骤 1：准备数据库

1. 确保 MySQL 可访问（本机或远程均可）。
2. 创建数据库（示例）：

```sql
CREATE DATABASE astro_ai DEFAULT CHARACTER SET utf8mb4;
```

### 步骤 2：修改 MySQL 配置（必做）

编辑 `astrsomn-server/src/main/resources/application-mysql.yml`。

如果你是在自己的业务项目中直接接入 `astrsomn-runtime-starter`，请确保激活 `mysql` profile：

```yml
spring:
  profiles:
    active: mysql
```

**必须修改项**：
- `host`
- `port`
- `database-name`
- `username`
- `password`

**最新可启动参考（runtime-starter）**：

```yml
astrsomn:
  enabled: true
  env-code: PRO
  username: admin
  admin-users: admin
  mybatis-plus:
    additional-type-aliases-package: com.astrsomn.workflow.core.domain.entity
  data-base:
    database-type: mysql
    host: 127.0.0.1
    port: 3306
    database-name: astro_ai
    username: root
    password: your_password
    driver: com.mysql.cj.jdbc.Driver
    use-ssl: false
    charset: utf8
    timezone: Asia/Shanghai
    connection-timeout: 30000
    maximum-pool-size: 10
    minimum-idle: 5
    validation:
      enabled: true
      fail-fast: false
      required-tables:
        - SYS_ENV
```

### 步骤 3：启动服务

- 在 IDE 中运行 `astrsomn-server` 模块的 `AstrsomnServerApplication.java`。
- 首次启动会自动执行 Flyway 迁移（默认开启）。

### 步骤 4：确认启动成功

满足以下任一条件可判定后端已成功启动：
- 日志中出现 Spring Boot 启动完成信息（Started ...）。
- 控制台无数据库连接报错且应用持续运行。
- 监听端口为 `4481`（默认配置）。

### 步骤 5：在业务项目中引入依赖并使用 `@Astro`

完成服务启动后，在你的业务项目中引入下方 starter 依赖，即可通过 `@Astro` 注解接入 AI 能力。

### Maven 依赖（仅 runtime-starter）

**1) 引入 runtime-starter**：

```xml
<!-- 运行时 Starter（AI 模型、工具、MCP 等） -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-runtime-starter</artifactId>
    <version>0.1.0-alpha.1</version>
</dependency>
```

**2) 选择并引入可用 Provider（至少一个）**：

```xml
<!-- DeepSeek Provider -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-provider-deepseek</artifactId>
    <version>0.1.0-alpha.1</version>
</dependency>

<!-- Zhipu Provider -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-provider-zhipu</artifactId>
    <version>0.1.0-alpha.1</version>
</dependency>
```

> 当前“快速启动”仅验证并支持：`astrsomn-runtime-starter` + `astrsomn-provider-deepseek` / `astrsomn-provider-zhipu`。
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

- [Qdrant Vector](astrsomn-vector/astrsomn-vector-qdrant/)
- [Chroma Vector](astrsomn-vector/astrsomn-vector-chroma/)
- [Milvus Vector](astrsomn-vector/astrsomn-vector-milvus/)
- [Redis Vector](astrsomn-vector/astrsomn-vector-redis/)

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

## 📄 许可证

```
Copyright 2024 Astrsomn Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

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
