# RAG 嵌入与向量注册重构说明

## 目标

- **Starter**：编排与参数传递（`AstroChatParam`、`RagSetting`），通过已有工厂创建 **EmbeddingModel**；通过 **接口** 获取 **EmbeddingStore**。
- **astrsomn-vector-qdrant**：查 `AI_VEC_SOURCE` / `AI_VEC_STORE`，使用 `VecStoreBackend` 构建 LangChain4j **EmbeddingStore**，并注册 `RagEmbeddingStoreResolver`。

## 核心类型

| 位置 | 说明 |
|------|------|
| [`RagEmbeddingStoreResolver`](e:/project/Astrsomn/astrsomn-core/src/main/java/org/astrsomn/core/common/langchain/rag/RagEmbeddingStoreResolver.java)（core） | 解析向量存储并可选返回集合上的 `modelKey` 作为嵌入模型 key |
| [`DefaultRagEmbeddingStoreResolver`](e:/project/Astrsomn/astrsomn-vector/astrsomn-vector-qdrant/src/main/java/org/astrsomn/vector/qdrant/rag/DefaultRagEmbeddingStoreResolver.java) | 实现：首条 `knowledgeKeys` = `AI_VEC_STORE.ID` |
| [`QdrantVectorAutoConfiguration`](e:/project/Astrsomn/astrsomn-vector/astrsomn-vector-qdrant/src/main/java/org/astrsomn/vector/qdrant/config/QdrantVectorAutoConfiguration.java) | 注册 `VecStoreBackendRegistry`、`RagEmbeddingStoreResolver`（需已存在 `AiVecStoreMapper` Bean） |
| [`AiEmbeddingModelFactory`](e:/project/Astrsomn/astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/tool/rag/AiEmbeddingModelFactory.java) | 查 `AI_MODEL` / 账号，临时写入 `ModelSetting` 后调用 `AstroModelFactory.createModel(EmbeddingModel.class)` |
| [`VectorStoreRegistry`](e:/project/Astrsomn/astrsomn-spring-boot-starter/src/main/java/org/astrsomn/starter/langchain/tool/rag/VectorStoreRegistry.java) | 委托 `RagEmbeddingStoreResolver` |

## 配置约定

1. **knowledgeKeys**：首项为向量集合主键（数字字符串），对应 `AI_VEC_STORE.ID`。
2. **embeddingModelKey**（`RagSetting`）：显式指定嵌入模型；为空时尝试用集合的 `modelKey`，再回退为当前会话的 `param.modelKey`。
3. 应用需依赖 **astrsomn-vector-qdrant**（或后续其他向量实现模块），否则 `RagEmbeddingStoreResolver` 不存在会在运行时抛错。

## 已移除

- `astrsomn-spring-boot-starter/.../rag/impl` 下 `VectorStoreHandler` 及各厂商占位实现（由 `VecStoreBackend` 体系替代）。

## 相关设计

- [向量存储扩展设计规范](./向量存储扩展设计规范.md)
- [向量域抽象层设计](./向量域抽象层设计.md)
