# 🌟 Astrsomn

> **One annotation to unleash AI evolution**  
> Built on LangChain4j, providing standardized, production-grade AI integration for Java

<p align="center">
  <a href="README.md"><img src="https://img.shields.io/badge/Language-Chinese-red.svg" alt="Chinese"></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg" alt="Apache 2.0 License"></a>
  <a href="https://github.com/langchain4j/langchain4j"><img src="https://img.shields.io/badge/Powered%20by-LangChain4j-orange.svg" alt="Powered by LangChain4j"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Build-Maven-blue.svg" alt="Maven Build"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg" alt="Spring Boot 3.2+"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/issues"><img src="https://img.shields.io/github/issues/Astrsomn/Astrsomn.svg" alt="GitHub Issues"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/stargazers"><img src="https://img.shields.io/github/stars/Astrsomn/Astrsomn.svg" alt="GitHub Stars"></a>
  <a href="https://github.com/Astrsomn/Astrsomn/network/members"><img src="https://img.shields.io/github/forks/Astrsomn/Astrsomn.svg" alt="GitHub Forks"></a>
</p>

---

## 📖 Project Introduction

**Astrsomn** is designed to solve common engineering challenges when integrating large models into the Java ecosystem: complex configuration, dependency coupling, difficult capability extension, and insufficient runtime governance.

Through layered modularization and standardized encapsulation, Astrsomn enables developers to quickly get started while supporting long-term evolution in enterprise scenarios.

Astrsomn consists of a complete ecosystem:
- `core`: Unified abstraction and common capabilities
- `starter`: Spring Boot auto-configuration and access layer
- `providers`: Model provider implementations
- `vector`: Vector storage implementations
- `server`: Service-oriented runtime and governance
- `ui`: Visual configuration and operations

**Application scenarios**: RAG knowledge base, intelligent dialogue, Agent orchestration, MCP tool integration, enterprise AI platform, etc.

---

## ✨ Core Features

| Feature | Description |
|---------|-------------|
| 🔧 **Deep Encapsulation** | Full encapsulation of LangChain4j capabilities (LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent) |
| 🚀 **Zero-Invasion Integration** | SpringBoot Starter auto-configuration, integrate with one annotation |
| 🔌 **Plugable Design** | Provider/Vector pluggable architecture, supports multi-model and multi-vector database governance |
| 📡 **Streaming Response** | Supports streaming response, tool calling, memory, RAG, orchestration and other complete workflows |
| 📊 **Observability** | Production-ready observability and maintainability (configuration, logging, cost, caching) |
| 🔒 **Enterprise Features** | Multi-model, multi-tenant, rate limiting, monitoring, logging and other enterprise-grade features |
| 🔗 **Native Compatibility** | Fully compatible with native LangChain4j, seamless extension |

---

## 🧩 Module Architecture

```text
Astrsomn
├── astrsomn-core                    # Core capability encapsulation (basic interfaces, tools, model unified layer)
├── astrsomn-spring-boot-starter     # SpringBoot quick starter (auto-configuration, starter dependencies)
├── astrsomn-workflow-core           # Workflow core engine
├── astrsomn-workflow-spring-boot-starter  # Workflow Starter
├── astrsomn-providers               # Model provider implementations
│   ├── astrsomn-provider-openai     # OpenAI adapter
│   ├── astrsomn-provider-deepseek   # DeepSeek adapter
│   ├── astrsomn-provider-qwen       # Qwen adapter
│   ├── astrsomn-provider-qianfan    # Qianfan adapter
│   └── astrsomn-provider-zhipu      # Zhipu AI adapter
├── astrsomn-vector                  # Vector storage implementations
│   ├── astrsomn-vector-qdrant       # Qdrant adapter
│   ├── astrsomn-vector-chroma       # Chroma adapter
│   ├── astrsomn-vector-milvus       # Milvus adapter
│   └── astrsomn-vector-redis        # Redis adapter
├── astrsomn-server                  # Standalone deployable AI service (HTTP interface, service-oriented operation)
├── astrsomn-ui                      # Visual console (configuration, debugging, monitoring)
└── astrsomn-introduction            # Project introduction site source
```

---

## 🔗 Dependency Hierarchy

The framework evolves in the following direction (facilitating future SPI and custom ClassLoader enhancements):

- `server` depends on: `starter`, `providers`, `vector`
- `starter` depends on: `core` (and workflow-related core)
- `providers` depends on: `core`
- `vector` depends on: `core`

> **Note**: To ensure stability at this stage, some model-related dependencies in `core` are temporarily retained and will be gradually migrated to corresponding sub-modules as code is split.

---

## 🚀 Quick Start

### Requirements
- JDK 21+
- Spring Boot 3.2+
- Maven 3.8+

### Steps

1. **Start basic dependencies** (database, vector database, etc.).
2. **Configure parameters**: Modify data source and model parameters in `astrsomn-server`.
3. **Start the service**: Run `AstrsomnServerApplication.java`.
4. **Visual management**: Start `astrsomn-ui` for configuration and debugging.

### Maven Dependencies

```xml
<!-- Core dependency -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Spring Boot Starter -->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 📚 Documentation & Resources

| Document Type | Link |
|---------------|------|
| 📖 System Design | [System Design Document](document/系统设计文档.md) |
| 🌐 Introduction Site | [astrsomn-introduction](astrsomn-introduction/) |
| 📝 Chinese Documentation | [README.md](README.md) |
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
- [OpenAI Provider](astrsomn-providers/astrsomn-provider-openai/)
- [DeepSeek Provider](astrsomn-providers/astrsomn-provider-deepseek/)
- [Qwen Provider](astrsomn-providers/astrsomn-provider-qwen/)
- [Qianfan Provider](astrsomn-providers/astrsomn-provider-qianfan/)
- [Zhipu Provider](astrsomn-providers/astrsomn-provider-zhipu/)

### Vector Storage
- [Qdrant Vector](astrsomn-vector/astrsomn-vector-qdrant/)
- [Chroma Vector](astrsomn-vector/astrsomn-vector-chroma/)
- [Milvus Vector](astrsomn-vector/astrsomn-vector-milvus/)
- [Redis Vector](astrsomn-vector/astrsomn-vector-redis/)

---

## 📬 Community & Support

- 💬 [GitHub Discussions](https://github.com/Astrsomn/Astrsomn/discussions) - Community discussions
- 🐛 [Issue Tracker](https://github.com/Astrsomn/Astrsomn/issues) - Bug reports
- 🔧 [Pull Requests](https://github.com/Astrsomn/Astrsomn/pulls) - Code contributions
- 📧 Mailing List: dev@astrsomn.io

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
  <a href="https://github.com/Astrsomn/Astrsomn"><img src="https://img.shields.io/badge/GitHub-Astrsomn-blue.svg" alt="GitHub"></a>
  <a href="https://gitter.im/Astrsomn/community"><img src="https://img.shields.io/badge/Gitter-Join%20Chat-green.svg" alt="Gitter"></a>
</p>
