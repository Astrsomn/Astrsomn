# 🌟 Astrsomn

<p align="center">
  <img src="astrsomn-ui/src/assets/Astrsomn-logo.png" alt="Astrsomn Logo" width="200"/>
</p>

> **One annotation to unleash AI evolution**  
> Built on LangChain4j, providing standardized, production-grade AI integration for Java

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

## ⚡ 30 Second Quick Overview

- **What is it**: A Java AI Starter built on LangChain4j
- **What you get**: One annotation `@Astro` to quickly invoke AI models, supporting pluggable Provider switching
- **What problem it solves**: Converges complex LangChain4j configurations into standardized configurations with visual management capabilities
- **How to start**: First add `astrsomn-runtime-starter` and Provider dependencies, then complete YAML configuration and startup following "Quick Start"

> [!WARNING]
> ## Work in Progress (Not Production Ready)
> This project is currently **incomplete** and under active development. Features, configuration, and APIs may change frequently.  
> **Do not use in production**. It is currently intended for learning, evaluation, and feedback only.

---

## 📖 Project Introduction

**Astrsomn** is a Java AI integration framework built on LangChain4j: simply add one Starter dependency to quickly access multi-model capabilities.  
The framework standardizes and visualizes complex LangChain4j engineering configurations (model access, Provider selection, runtime parameters, etc.), lowering the barrier to entry.

You get these capabilities out of the box:

- One annotation `@Astro` to invoke AI models in your business code
- Quickly switch between DeepSeek, Zhipu and other models via `runtime-starter + provider`
- Converge scattered LangChain4j configurations into unified configuration items with visual management support
- Maintain compatibility with native LangChain4j capabilities for progressive expansion to Agent / RAG / Tools

---

## 🏗️ Under Construction 🏗️

| Status | Feature Module | Description |
|--------|----------------|-------------|
| ✅ | **Agent Lifecycle** | Creation, configuration, and management capabilities completed |
| ✅ | **Environment Initialization** | Supports quick environment configuration and initialization |
| ✅ | **Dependency Quick Import** | Maven Starter one-click integration |
| ✅ | **Basic Configuration** | Core configuration management capabilities provided |
| ✅ | **RAG Capabilities** | Vector retrieval and knowledge base capabilities completed |
| 🔄 | **Workflow Module** | Planned for version 2.0 |
| ✅ | **Vector Database Integration** | Qdrant vector database supported |
| ❌ | **Security & Governance** | Rate limiting, monitoring and other features pending |
| ⚠️ | **API Compatibility** | May change at any time, no backward compatibility guaranteed |

**⚠️ NOT FOR PRODUCTION USE! ⚠️**

This project is under active development. Welcome to join us and contribute!
See [Contributing](#🤝-Contributing)

---

You can think of Astrsomn as a complete ecosystem:

- `core`: Unified abstraction and common capabilities
- `starter`: Spring Boot auto-configuration and access layer
- `providers`: Model provider implementations
- `vector`: Vector storage implementations
- `server`: Service-oriented runtime and governance
- `ui`: Visual configuration and operations

**Application scenarios**: RAG knowledge base, intelligent dialogue, MCP tool integration, enterprise AI platform, etc.

---

> **One annotation to unleash AI evolution**  
> Built on LangChain4j, providing standardized, production-grade AI integration for Java

## ✨ Core Features

| Feature | Description |
|---------|-------------|
| 🔧 **Deep Encapsulation** | Full encapsulation of LangChain4j capabilities (LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent) |
| 🚀 **Zero-Invasion Integration** | SpringBoot Starter auto-configuration, integrate with one annotation |
| 🔌 **Plugable Design** | Provider/Vector pluggable architecture, supports multi-model and multi-vector database governance |
| 📡 **Streaming Response** | Supports streaming response, tool calling, memory, RAG, orchestration and other complete workflows |
| 📊 **Observability** | Production-ready observability and maintainability (configuration, logging, cost, caching) |
| 🔒 **Enterprise Features** | Multi-model, multi-tenant, rate limiting, monitoring, logging and other enterprise-grade features |
| 🛡️ **High Availability** | Built-in circuit breaker, fallback, and failover mechanisms for service stability |
| 🔗 **Native Compatibility** | Fully compatible with native LangChain4j, seamless extension |

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| Backend Framework | Spring Boot 3.3.0 |
| Language | Java 17+ |
| AI Integration | LangChain4j 1.11.x |
| Frontend Framework | Vue 3 + TypeScript |
| Build Tool | Maven |
| Database | MySQL / H2 |
| Vector Database | Qdrant / Milvus / Chroma / Redis |

### 🔑 Key Dependencies and Versions (Quick Reference)

| Dependency | Main Version | Purpose |
|------------|--------------|---------|
| `com.astrsomn:astrsomn-runtime-starter` | `0.2.0-SNAPSHOT` | One-stop access entry, provides annotation injection and runtime capabilities |
| `dev.langchain4j:langchain4j-core` | `1.11.x` | LangChain4j core abstraction and invocation capabilities |
| `dev.langchain4j:langchain4j-open-ai` | `1.11.x` | OpenAI protocol model access (DeepSeek and other compatible scenarios) |
| `dev.langchain4j:langchain4j-community-zhipu-ai` | `1.11.0-beta19` | Zhipu AI model access |
| `org.springframework.boot:spring-boot-starter` | `3.3.x` | Spring Boot runtime and auto-configuration foundation |
| `com.baomidou:mybatis-plus-spring-boot3-starter` | `3.5.x` | Data access and configuration persistence capabilities |

> Note: Documentation uses "main version" to help quickly determine compatibility range; exact patch versions are in each module's `pom.xml`.

---

## 🧩 Module Architecture

```text
Astrsomn
├── astrsomn-common                    # Common base module (utilities, base entities, exception definitions)
├── astrsomn-api                       # API interface definition layer
│   ├── astrsomn-api-runtime           # Runtime API (exception enums, error codes)
│   ├── astrsomn-api-storage           # Storage API (file handling, etc.)
│   └── astrsomn-api-workflow          # Workflow API (planned)
├── astrsomn-integrations              # Integration layer (Spring Boot Starter)
│   ├── astrsomn-runtime-starter       # Runtime Starter (AI models, tools, MCP, etc.)
│   │   └── route                      # Model routing module (high availability)
│   │       ├── ModelRouteCompositeFactory      # Route composite factory
│   │       ├── CompositeChatModel              # Composite chat model (failover)
│   │       ├── CompositeStreamingChatModel    # Composite streaming model
│   │       ├── EndpointSelectionStrategy       # Endpoint selection strategy (load balancing)
│   │       └── ResilienceDecorationStrategy    # Resilience decoration strategy (circuit breaker, fallback)
│   ├── astrsomn-workflow-starter      # Workflow Starter (planned)
│   └── astrsomn-internal-storage      # Internal storage implementation
├── astrsomn-plugins                   # Plugin ecosystem
│   ├── astrsomn-providers             # Model provider implementations (currently available: DeepSeek / Zhipu)
│   └── astrsomn-vector                # Vector storage implementations (planned)
├── astrsomn-server                    # Server application (HTTP interface, service-oriented operation)
└── astrsomn-ui                        # Frontend console (Vue 3 + TypeScript)
```

---

## 🔗 Dependency Hierarchy

The framework evolves in the following direction (facilitating future SPI and custom ClassLoader enhancements):

- `server` depends on: `integrations`, `plugins`
- `integrations` depends on: `api`, `common`
- `plugins` depends on: `api`
- `api` depends on: `common`

---

## 🚀 Quick Start

### Requirements

| Environment | Version |
|-------------|---------|
| JDK | 21+ |
| Maven | 3.8+ |
| MySQL | 8.0+ (recommended) |
| Node.js | 18+ (for frontend development) |

---

### Part 1: Start the Project

After cloning the repository, run both the backend service and frontend console locally.

#### 1. Backend

##### 1.1 Prepare Database

Ensure MySQL is accessible (local or remote), create database:

```sql
CREATE DATABASE astro_ai DEFAULT CHARACTER SET utf8mb4;
```

##### 1.2 Update Configuration

Edit `astrsomn-server/src/main/resources/application-mysql.yml`, modify database connection:

**Required changes**:

- `host`, `port`, database name in `datasource.url`
- `datasource.username`
- `datasource.password`

```yml
astrsomn:
  enabled: true
  datasource:
    # Change to your instance before startup: single-line jdbc:mysql URL
    url: jdbc:mysql://127.0.0.1:3306/astro_ai?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10
      minimum-idle: 5
```

##### 1.3 Start Backend

Run `AstrsomnServerApplication.java` in the `astrsomn-server` module from your IDE.

- Flyway migration runs automatically on first startup (enabled by default).
- Confirm success: Log shows `Started ...`, listening on port `4481` (default).

#### 2. Frontend

The frontend consists of base component packages (`astrsomn-ui-packages`) and the main application (`astrsomn-ui`).
Component packages need to be built manually first, then the main application references them via `file:` protocol.

##### 2.1 Build Base Component Package

```bash
cd astrsomn-ui-packages/astro-chat-core
npm install
npm run build
```

> `astro-chat-vue` is pure source export (`exports` points directly to `./src/index.ts`), no separate build needed.

##### 2.2 Start Frontend Application

```bash
cd astrsomn-ui
npm install
npm run dev
```

Frontend runs at `http://localhost:3000` (default), ensure backend port `4481` is accessible.

---

### Part 2: Integrate into Your Project

If you want to integrate Astrsomn's AI capabilities into your own project, follow these steps.

#### 1. Add Maven Dependencies

**1) runtime-starter**:

```xml
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-runtime-starter</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```

**2) Select and add available Providers (at least one)**:

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

> Currently verified and supported: `astrsomn-runtime-starter` + `astrsomn-provider-deepseek` / `astrsomn-provider-zhipu`.
> Other Starters/Providers will be gradually released in subsequent versions.

**3) The following dependencies need to be provided by the user project (marked as provided in runtime-starter)**:

```xml
<!-- Spring Boot Basics -->
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

<!-- Database Driver (at least one; MySQL example) -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- Configuration Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Reactive Stream Capabilities (optional) -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
```

#### 2. Add Configuration

Ensure `mysql` profile is activated, add to your `application.yml`:

```yml
spring:
  profiles:
    active: mysql
```

Then configure database connection in `application-mysql.yml`, format reference "1.2 Update Configuration" above.

#### 3. Use `@Astro` in Business Code

```java
@Service
public class MyService {
    @Astro(agentKey = "MY-AGENT", envCode = "PRO")
    private AstroChatAssistant assistant;

    public void demo() {
        String response = assistant.chat("Hello, please introduce yourself");
    }
}
```

---

## 📚 Official Website & Documentation

| Type | Link |
|------|------|
| 🏠 Official Website | [astrsomn.com](https://www.astrsomn.com/home.html) |
| 📖 Documentation | [doc.astrsomn.com](https://doc.astrsomn.com) |
| 💻 GitHub Repository | [Astrsomn/Astrsomn](https://github.com/Astrsomn/Astrsomn) |

---

## 📚 Documentation & Resources

| Document Type | Link |
|---------------|------|
| 📖 System Design | [System Design Document](document/系统设计文档.md) |
| 🌐 Introduction Site | [astrsomn-introduction](astrsomn-introduction/) |
| 📝 Chinese Documentation | [README-CN.md](README.md) |
| 📋 Version Roadmap | [Version Roadmap 1.x to 2.x](document/2026-04-17/版本路线规划-1x到2x.md) |
| 🗂️ Workflow Design | [AI Workflow Standardization Roadmap](document/2026-04-26/ai-workflow-standardization-roadmap.md) |
| 📐 Vector Storage Design | [Vector Storage Extension Specification](document/2026-04-06/向量存储扩展设计规范.md) |
| 📊 Extension Architecture | [Extension System Architecture Analysis](document/2026-04-15/扩展系统架构分析.md) |

---

## 🤝 Contributing

We welcome contributions! We appreciate any form of contribution including but not limited to:

### Ways to Contribute

- 💡 [Submit Issues](https://github.com/Astrsomn/Astrsomn/issues) - Report bugs or suggest features
- 📝 [Submit Pull Requests](https://github.com/Astrsomn/Astrsomn/pulls) - Contribute code
- 📖 Improve Documentation - Help improve project documentation
- 🗣️ Community Engagement - Share experiences in discussions

### Contribution Process

1. **Fork the repository** → [Astrsomn/Astrsomn](https://github.com/Astrsomn/Astrsomn)
2. **Create a feature branch** (`git checkout -b feature/your-feature`)
3. **Commit changes** (`git commit -m 'Add some feature'`)
4. **Push to branch** (`git push origin feature/your-feature`)
5. **Create Pull Request** → [Submit PR](https://github.com/Astrsomn/Astrsomn/pulls)

### Contribution Guidelines

- Follow the [Code Style Guide](document/2026-04-02/开源完善总方案.md)
- Ensure all tests pass before submitting
- Provide clear commit messages and PR descriptions

---

## 📦 Extension Ecosystem

### Model Providers

- [DeepSeek Provider](astrsomn-plugins/astrsomn-providers/astrsomn-provider-deepseek/)
- [Zhipu Provider](astrsomn-plugins/astrsomn-providers/astrsomn-provider-zhipu/)
- OpenAI Provider (coming soon)
- Qwen Provider (coming soon)
- Qianfan Provider (coming soon)

### Vector Storage

- [Chroma Vector](astrsomn-vector/astrsomn-vector-chroma/)

---

## 💬 Community & Support

<p align="center">
  <a href="https://github.com/Astrsomn/Astrsomn/discussions"><img src="https://img.shields.io/badge/Discussions-GitHub-blue?style=flat-square&logo=github" alt="GitHub Discussions"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/issues"><img src="https://img.shields.io/badge/Issues-GitHub-green?style=flat-square&logo=github" alt="GitHub Issues"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/pulls"><img src="https://img.shields.io/badge/PRs-Welcome-orange?style=flat-square&logo=github" alt="PRs Welcome"></a>
  <a href="mailto:astrsomn@outlook.com"><img src="https://img.shields.io/badge/Email-astrsomn@outlook.com-red?style=flat-square&logo=gmail" alt="Email"></a>
</p>

- 💬 **Discussions**: [GitHub Discussions](https://github.com/Astrsomn/Astrsomn/discussions) - Share experiences and ideas
- 🐛 **Issues**: [GitHub Issues](https://github.com/Astrsomn/Astrsomn/issues) - Report bugs and suggest features
- 🔧 **Contributions**: [Pull Requests](https://github.com/Astrsomn/Astrsomn/pulls) - Welcome to submit PRs
- 📧 **Email**: astrsomn@outlook.com

---

## 📄 License

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
