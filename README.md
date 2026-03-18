# 🌟 Astrsomn 星梦
## 企业级 LangChain4j 一站式封装框架
> 封装复杂 AI，释放 Java 创造力
> 基于 LangChain4j 构建，为 Java 开发者提供生产级 AI 应用开发解决方案

---

## 📖 项目介绍
**Astrsomn（星梦）** 是一款面向企业与开发者的轻量化、服务化、可视化的 LangChain4j 增强封装框架。
致力于解决 Java 生态接入大模型能力时**配置繁琐、工程化不足、难以部署、缺少管控**等问题。

通过 **core + starter + server + ui** 四大模块化设计，让 Java 开发者：
- 无需深入理解 LangChain4j 底层
- 无需从零搭建 AI 服务
- 无需编写大量重复代码
- 支持 SpringBoot 一键集成
- 支持独立部署服务化
- 支持可视化管理与调试

真正实现 **开箱即用、低代码接入、生产级可用**。

适用场景：RAG 知识库、智能对话、企业 AI 助手、工具调用、Agent 智能体、AI 网关等。

---

## ✨ 核心特性
✅ 深度封装 LangChain4j 全能力（LLM / Embedding / 向量库 / 记忆 / RAG / Tools / Agent）
✅ SpringBoot Starter 零侵入自动配置
✅ 独立可部署 Server 服务（HTTP + 接口规范）
✅ 可视化管理控制台（模型调试 / 知识库 / 对话日志）
✅ 统一核心包，无冗余依赖
✅ 支持多模型、多租户、限流、监控、日志等企业级特性
✅ 生产级高可用、可扩展、易维护
✅ 完全兼容原生 LangChain4j，无缝扩展

---

## 🧩 模块架构
```plaintext
astrsomn
├── astrsomn-core          # 核心能力封装（基础接口、工具、模型统一层）
├── astrsomn-starter       # SpringBoot 快速启动器（自动配置、 starter 依赖）
├── astrsomn-server        # 独立部署 AI 服务（HTTP 接口、服务化运行）
└── astrsomn-ui            # 可视化控制台（配置、调试、监控）