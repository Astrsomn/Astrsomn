# 🌟 Astrsomn 星梦

<p align="center">
  <img src="astrsomn-ui/src/assets/Astrsomn-logo.png" alt="Astrsomn Logo" width="200"/>
</p>

> **一行注解，即刻开启 AI 进化**  
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

**适用场景**：RAG 知识库、智能对话、Agent 编排、MCP 工具接入、企业 AI 中台等。

---

## ✨ 核心特性

| 特性 | 描述 |
|------|------|
| 🔧 **深度封装** | 完整封装 LangChain4j 能力（LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent） |
| 🚀 **零侵入接入** | SpringBoot Starter 自动配置，一行注解即可集成 |
| 🔌 **可插拔设计** | Provider/Vector 可插拔架构，支持多模型与多向量库治理 |
| 📡 **流式响应** | 支持流式响应、工具调用、记忆、RAG、编排等完整链路 |
| 📊 **可观测能力** | 面向生产的可观测与可维护能力（配置、日志、成本、缓存） |
| 🔒 **企业级特性** | 支持多模型、多租户、限流、监控、日志等企业级特性 |
| 🔗 **原生兼容** | 完全兼容原生 LangChain4j，无缝扩展 |

---

## 🧩 模块结构

```text
Astrsomn
├── astrsomn-core                    # 核心能力封装（基础接口、工具、模型统一层）
├── astrsomn-spring-boot-starter     # SpringBoot 快速启动器（自动配置、Starter 依赖）
├── astrsomn-workflow-core           # 工作流核心引擎
├── astrsomn-workflow-spring-boot-starter  # 工作流 Starter
├── astrsomn-providers               # 模型提供方实现
│   ├── astrsomn-provider-openai     # OpenAI 适配
│   ├── astrsomn-provider-deepseek   # DeepSeek 适配
│   ├── astrsomn-provider-qwen       # 通义千问适配
│   ├── astrsomn-provider-qianfan    # 百度千帆适配
│   └── astrsomn-provider-zhipu      # 智谱 AI 适配
├── astrsomn-vector                  # 向量存储实现
│   ├── astrsomn-vector-qdrant       # Qdrant 适配
│   ├── astrsomn-vector-chroma       # Chroma 适配
│   ├── astrsomn-vector-milvus       # Milvus 适配
│   └── astrsomn-vector-redis        # Redis 适配
├── astrsomn-server                  # 独立部署的 AI 服务（HTTP 接口、服务化运行）
├── astrsomn-ui                      # 可视化控制台（配置、调试、监控）
└── astrsomn-introduction            # 项目介绍站点源码
```

---

## 🔗 依赖分层

当前按以下方向演进（便于后续 SPI 与自定义 ClassLoader 增强）：

- `server` 依赖：`starter`、`providers`、`vector`
- `starter` 依赖：`core`（及编排相关核心）
- `providers` 依赖：`core`
- `vector` 依赖：`core`

> **说明**：为保证现阶段稳定性，`core` 中部分模型相关依赖暂时保留，后续会随代码拆分逐步迁移到对应子模块。

---

## 🚀 快速开始

### 环境要求
- JDK 21+
- Spring Boot 3.2+
- Maven 3.8+

### 步骤

1. **启动基础依赖**（数据库、向量库等）。
2. **配置参数**：修改 `astrsomn-server` 的数据源与模型参数。
3. **启动服务**：运行 `AstrsomnServerApplication.java`。
4. **可视化管理**：启动 `astrsomn-ui` 进行配置与调试。

### Maven 依赖

**核心依赖（推荐）**：

```xml
<!-- Spring Boot Starter（已包含 core） -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-springboot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 工作流 Starter（已包含 core 和 springboot-starter） -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-workflow-springboot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

> **说明**：`astrsomn-springboot-starter` 已包含 `astrsomn-core`，`astrsomn-workflow-springboot-starter` 已包含前两者。根据业务需求选择对应的依赖即可。

---

## 📚 官方网站与文档

| 类型 | 链接 |
|------|------|
| 🏠 官方网站 | [astrsomn.com](https://www.astrsomn.com/home.html) |
| 📖 官方文档 | [doc.astrsomn.com](https://doc.astrsomn.com) |
| 💻 GitHub 仓库 | [Astrsomn/Astrsomn](https://github.com/Astrsomn/Astrsomn) |

---

## 📚 文档与资源

| 文档类型 | 链接 |
|----------|------|
| 📖 系统设计文档 | [系统设计文档](document/系统设计文档.md) |
| 🌐 中文介绍站点 | [astrsomn-introduction](astrsomn-introduction/) |
| 📝 英文文档 | [README-EN.md](README-EN.md) |
| 📋 版本路线规划 | [版本路线规划-1x到2x](document/2026-04-17/版本路线规划-1x到2x.md) |
| 🗂️ 工作流设计 | [AI工作流标准化路线图](document/2026-04-26/ai-workflow-standardization-roadmap.md) |
| 📐 向量存储设计 | [向量存储扩展设计规范](document/2026-04-06/向量存储扩展设计规范.md) |
| 📊 扩展系统架构 | [扩展系统架构分析](document/2026-04-15/扩展系统架构分析.md) |

---

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

---

## 📦 扩展生态

### 模型提供方
- [OpenAI Provider](astrsomn-providers/astrsomn-provider-openai/)
- [DeepSeek Provider](astrsomn-providers/astrsomn-provider-deepseek/)
- [Qwen Provider](astrsomn-providers/astrsomn-provider-qwen/)
- [Qianfan Provider](astrsomn-providers/astrsomn-provider-qianfan/)
- [Zhipu Provider](astrsomn-providers/astrsomn-provider-zhipu/)

### 向量存储
- [Qdrant Vector](astrsomn-vector/astrsomn-vector-qdrant/)
- [Chroma Vector](astrsomn-vector/astrsomn-vector-chroma/)
- [Milvus Vector](astrsomn-vector/astrsomn-vector-milvus/)
- [Redis Vector](astrsomn-vector/astrsomn-vector-redis/)

---

## 📬 社区与支持

- 💬 [GitHub Discussions](https://github.com/Astrsomn/Astrsomn/discussions) - 社区讨论
- 🐛 [Issue Tracker](https://github.com/Astrsomn/Astrsomn/issues) - Bug 报告
- 🔧 [Pull Requests](https://github.com/Astrsomn/Astrsomn/pulls) - 代码贡献
- 📧 邮件列表：dev@astrsomn.io

---

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

---

<p align="center">
  Made with ❤️ by the Astrsomn Team
</p>

<p align="center">
  <a href="https://www.astrsomn.com/home.html"><img src="https://img.shields.io/badge/Website-astrsomn.com-blue.svg" alt="Astrsomn Website"></a>
  <a href="https://doc.astrsomn.com"><img src="https://img.shields.io/badge/Documentation-doc.astrsomn.com-green.svg" alt="Documentation"></a>
  <a href="https://github.com/Astrsomn/Astrsomn"><img src="https://img.shields.io/badge/GitHub-Astrsomn-blue.svg" alt="GitHub"></a>
  <a href="https://gitter.im/Astrsomn/community"><img src="https://img.shields.io/badge/Gitter-Join%20Chat-green.svg" alt="Gitter"></a>
</p>
