# 🌟 Astrsomn
## Enterprise-Grade LangChain4j One-Stop Encapsulation Framework
> Encapsulate complex AI, unleash Java creativity
> Built on LangChain4j, providing production-grade AI application development solutions for Java developers

---

## 📖 Project Introduction
**Astrsomn** is a lightweight, service-oriented, and visualized enhanced encapsulation framework for LangChain4j, designed for enterprises and developers.
It aims to solve problems such as **cumbersome configuration, insufficient engineering, deployment difficulties, and lack of management and control** when integrating large model capabilities into the Java ecosystem.

Through the **core + starter + server + ui** four-module design, Java developers can:
- No need to deeply understand LangChain4j's underlying implementation
- No need to build AI services from scratch
- No need to write a lot of repetitive code
- Support SpringBoot one-click integration
- Support independent deployment as a service
- Support visualized management and debugging

Truly achieve **out-of-the-box, low-code access, and production-ready**.

Application scenarios: RAG knowledge base, intelligent dialogue, enterprise AI assistant, tool calling, Agent, AI gateway, etc.

---

## ✨ Core Features
✅ Deeply encapsulates all LangChain4j capabilities (LLM / Embedding / Vector Database / Memory / RAG / Tools / Agent)
✅ SpringBoot Starter zero-invasion automatic configuration
✅ Independently deployable Server service (HTTP + interface specification)
✅ Visual management console (model debugging / knowledge base / dialogue logs)
✅ Unified core package, no redundant dependencies
✅ Supports enterprise-level features such as multi-model, multi-tenant, rate limiting, monitoring, and logging
✅ Production-grade high availability, scalability, and maintainability
✅ Fully compatible with native LangChain4j, seamless extension

---

## 🧩 Module Architecture
```plaintext
astrsomn
├── astrsomn-core          # Core capability encapsulation (basic interfaces, tools, model unified layer)
├── astrsomn-starter       # SpringBoot quick starter (automatic configuration, starter dependencies)
├── astrsomn-server        # Independently deployable AI service (HTTP interface, service-oriented operation)
└── astrsomn-ui            # Visual console (configuration, debugging, monitoring)
```