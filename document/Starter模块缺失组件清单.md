# Astrsomn Starter 模块缺失组件清单

## 概述
本文档列出了 `astrsomn-spring-boot-starter` 模块中缺失或需要完善的组件，按优先级排序。

---

## 🔴 高优先级缺失组件

### 1. AiModelService
**位置**: `org.astrsomn.starter.langchain.factory.AiStreamModelFactory` 中被引用  
**状态**: ❌ 未定义  
**依赖**: `AiModelMapper`  
**职责**: 
- 根据 modelId 获取模型配置
- 提供模型信息的缓存机制
- 模型状态管理

**需要实现的方法**:
```java
public interface AiModelService {
    AiModelEntity getByModelId(Long modelId);
    List<AiModelEntity> getAllEnabledModels();
    void updateModelStatus(Long modelId, String status);
}
```

---

### 2. AiConversationFacade
**位置**: `DynamicMemoryProvider` 和 `AstroAssistantFactory` 中被引用  
**状态**: ❌ 未定义  
**依赖**: `AiConversationMapper`, `AiConversationEntity`  
**职责**:
- 对话历史管理
- 消息持久化
- 记忆恢复

**需要实现的方法**:
```java
public interface AiConversationFacade {
    List<ChatMessage> loadFromDatabase(String memoryId);
    void saveConversation(String memoryId, List<ChatMessage> messages);
    void clearConversation(String memoryId);
    int getMaxMessageOrder(String memoryId);
}
```

---

### 3. EmbeddingModelRegistry
**位置**: `AstroAssistantFactory` 中被引用  
**状态**: ❌ 未定义  
**依赖**: `EmbeddingModel`  
**职责**:
- 管理多个嵌入模型
- 根据向量维度匹配模型
- 模型缓存和复用

**需要实现的方法**:
```java
public interface EmbeddingModelRegistry {
    void registerModel(Integer dimension, EmbeddingModel model);
    EmbeddingModel getModelByDimension(Integer dimension);
    EmbeddingModel getDefaultModel();
    void clearCache();
}
```

---

### 4. AiChatModelFactory 完整实现
**位置**: `org.astrsomn.starter.langchain.factory.AiChatModelFactory`  
**状态**: ⚠️ 框架存在，但核心逻辑被注释  
**问题**: 
- `getLanguageModel` 方法中所有模型构建逻辑被注释
- 缺少对各个模型提供商的支持实现

**需要补充的模型支持**:
```java
public ChatModel getLanguageModel(AiChatBuildParam buildParam) {
    // 需要实现以下模型的构建逻辑：
    // - OpenAI
    // - Alibaba (通义千问)
    // - Zhipu AI
    // - Google Gemini
    // - Baidu Qianfan
    // - DeepSeek
    // - 其他模型提供商
}
```

---

### 5. DynamicRagProvider
**位置**: `AstroAssistantFactory` 中被注释引用  
**状态**: ❌ 未定义  
**依赖**: `QdrantUtil`, `EmbeddingModel`  
**职责**:
- 知识库检索
- 向量相似度搜索
- RAG 内容提供

**需要实现的方法**:
```java
public class DynamicRagProvider implements ContentRetriever {
    public void initialize(List<Long> ragIdList);
    public List<ContentSegment> retrieve(Query query);
}
```

---

### 6. QdrantUtil
**位置**: `AstroAssistantFactory` 中被注释引用  
**状态**: ❌ 未定义  
**依赖**: Qdrant Client  
**职责**:
- Qdrant 向量数据库操作
- 向量插入和查询
- 集合管理

**需要实现的方法**:
```java
public class QdrantUtil {
    public void insertVectors(String collectionName, List<Vector> vectors);
    public List<ScoredPoint> search(String collectionName, Vector queryVector, int topK);
    public void createCollection(String collectionName, int dimension);
    public void deleteCollection(String collectionName);
}
```

---

## 🟡 中优先级缺失组件

### 7. AiPromptMapper 方法实现
**位置**: `AstroAssistantFactory` 中调用 `aiPromptMapper.getByUUID()`  
**状态**: ⚠️ Mapper 接口存在，但方法未实现  
**需要补充**:
```java
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {
    @Select("SELECT * FROM AI_PROMPT WHERE PROMPT_KEY = #{promptUuid} AND ENABLE_FLAG = 'enabled' ORDER BY VERSION DESC LIMIT 1")
    AiPromptEntity getByUUID(@Param("promptUuid") String promptUuid);
}
```

---

### 8. AiConversationMapper 方法实现
**位置**: `DatabaseHistoryRecorder` 中调用 `mapper.getMaxMessageOrder()`  
**状态**: ⚠️ Mapper 接口存在，但方法未实现  
**需要补充**:
```java
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {
    @Select("SELECT COALESCE(MAX(MESSAGE_ORDER), 0) FROM AI_CONVERSATION WHERE CONVERSATION_ID = #{memoryId}")
    int getMaxMessageOrder(@Param("memoryId") String memoryId);
    
    @Select("SELECT * FROM AI_CONVERSATION WHERE CONVERSATION_ID = #{memoryId} ORDER BY MESSAGE_ORDER ASC")
    List<AiConversationEntity> getHistoryByMemoryId(@Param("memoryId") String memoryId);
}
```

---

### 9. ChatStreamEnum
**位置**: `ChatStreamUtil` 中被引用  
**状态**: ❌ 未找到定义  
**需要定义**:
```java
public interface ChatStreamEnum {
    enum AstroEventType {
        TEXT("text", "文本内容"),
        THOUGHT("thought", "思考过程"),
        HTML("html", "HTML 内容"),
        TOOL("tool", "工具调用"),
        DONE("done", "完成"),
        ERROR("error", "错误");
        
        private final String code;
        private final String desc;
    }
    
    enum AstroChatRole {
        USER("user", "用户"),
        ASSISTANT("assistant", "助手"),
        SYSTEM("system", "系统");
        
        private final String code;
        private final String desc;
    }
}
```

---

### 10. AstroHistoryRecorder 接口
**位置**: `DatabaseHistoryRecorder` 实现了该接口，但接口未找到定义  
**状态**: ❌ 接口未定义  
**需要定义**:
```java
public interface AstroHistoryRecorder {
    void savePair(AiChatBuildParam param, String content, TokenUsage usage);
    void saveMessage(String memoryId, String role, String content, int order, int tokenCount);
    List<AiConversationEntity> getHistory(String memoryId);
}
```

---

### 11. AiConversationEntity.createEntity 方法
**位置**: `DatabaseHistoryRecorder.createEntity()` 返回 null  
**状态**: ⚠️ 方法存在但未实现  
**需要实现**:
```java
private AiConversationEntity createEntity(AiChatBuildParam param, ChatStreamEnum.AstroChatRole role, 
                                         String message, int order, int tokenCount) {
    AiConversationEntity entity = new AiConversationEntity();
    entity.setConversationId(param.getMemoryId());
    entity.setRole(role.getCode());
    entity.setContent(message);
    entity.setMessageOrder(order);
    entity.setTokenCount(tokenCount);
    entity.setCreatedAt(new Date());
    entity.setCreatedBy(param.getMemoryId());
    return entity;
}
```

---

## 🟢 低优先级缺失组件

### 12. AstrsomnProperties 完善
**位置**: `org.astrsomn.starter.config.AstrsomnProperties`  
**状态**: ⚠️ 配置不完整  
**需要补充的配置项**:
```java
@Data
@ConfigurationProperties(prefix = "astrsomn")
public class AstrsomnProperties {
    private String envCode;
    
    private Database database;
    
    private Qdrant qdrant;
    
    private Redis redis;
    
    private Chat chat;
    
    @Data
    public static class Database {
        private String baseUrl;
        private String username;
        private String password;
        private String driver;
    }
    
    @Data
    public static class Qdrant {
        private String host;
        private Integer port;
        private String apiKey;
    }
    
    @Data
    public static class Redis {
        private String host;
        private Integer port;
        private String password;
        private Integer database;
    }
    
    @Data
    public static class Chat {
        private Integer defaultMaxMessages;
        private Boolean enableStream;
        private Integer maxToken;
    }
}
```

---

### 13. AiBuildParamGenerator 依赖修复
**位置**: `org.astrsomn.starter.langchain.param.AiBuildParamGenerator`  
**状态**: ⚠️ 引用了不存在的 domain 包  
**问题**: 
- 引用了 `domain.dto.request.chat.AiChatQueryRequest`
- 引用了 `domain.entity.AiAgentEntity`
- 这些类应该在 `org.astrsomn.core.common.entity` 中

**需要修复**:
```java
// 将 import 修改为正确的包路径
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
```

---

### 14. AiChatBuildParam 重构
**位置**: `org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam`  
**状态**: ⚠️ 被标记为 @Deprecated  
**建议**: 
- 使用新的 `AstroChatRequest` 替代
- `AstroChatRequest` 设计更合理，使用了组合模式
- 需要更新所有引用 `AiChatBuildParam` 的代码

---

### 15. AstroChatRequest 集成
**位置**: `org.astrsomn.core.common.langchain.buildParam.AstroChatRequest`  
**状态**: ⚠️ 已定义但未被使用  
**需要**:
- 更新 `AiBuildParamGenerator` 使用 `AstroChatRequest`
- 更新 `AstroAssistantFactory` 接受 `AstroChatRequest`
- 更新 `ChatStreamUtil` 接受 `AstroChatRequest`

---

## 📋 配置文件缺失

### 16. application.properties
**位置**: `astrsomn-spring-boot-starter/src/main/resources/application.properties`  
**状态**: ⚠️ 文件存在但内容为空  
**需要添加**:
```properties
# Astrsomn Starter 配置
astrsomn.env-code=dev

# 数据库配置
astrsomn.database.url=jdbc:mysql://localhost:3306/astrsomn
astrsomn.database.username=root
astrsomn.database.password=root
astrsomn.database.driver=com.mysql.cj.jdbc.Driver

# Qdrant 配置
astrsomn.qdrant.host=localhost
astrsomn.qdrant.port=6333
astrsomn.qdrant.api-key=

# Redis 配置
astrsomn.redis.host=localhost
astrsomn.redis.port=6379
astrsomn.redis.password=
astrsomn.redis.database=0

# Chat 配置
astrsomn.chat.default-max-messages=10
astrsomn.chat.enable-stream=true
astrsomn.chat.max-token=2048
```

---

## 🔧 自动配置完善

### 17. AstrsomnAutoConfiguration 增强
**位置**: `org.astrsomn.starter.config.AstrsomnAutoConfiguration`  
**状态**: ⚠️ 只配置了 Properties，缺少其他 Bean  
**需要添加的 Bean**:
```java
@AutoConfiguration
@EnableConfigurationProperties(AstrsomnProperties.class)
@ConditionalOnClass(name = "org.astrsomn.core.service.AiService")
public class AstrsomnAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public AstrsomnProperties astrsomnProperties() {
        return new AstrsomnProperties();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AiModelService aiModelService(AiModelMapper mapper) {
        return new AiModelServiceImpl(mapper);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AiConversationFacade aiConversationFacade(AiConversationMapper mapper) {
        return new AiConversationFacadeImpl(mapper);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModelRegistry embeddingModelRegistry(AstrsomnProperties properties) {
        return new EmbeddingModelRegistryImpl(properties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public QdrantUtil qdrantUtil(AstrsomnProperties properties) {
        return new QdrantUtil(properties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public McpManager mcpManager() {
        return new McpManager();
    }
    
    @Bean
    @ConditionalOnMissingBean
    @PreDestroy
    public McpManager mcpManagerWithCleanup(McpManager manager) {
        return manager;
    }
}
```

---

## 📝 总结

### 必须实现（阻塞开发）
1. ✅ AiModelService
2. ✅ AiConversationFacade
3. ✅ EmbeddingModelRegistry
4. ✅ AiChatModelFactory 完整实现
5. ✅ DynamicRagProvider
6. ✅ QdrantUtil

### 应该实现（影响功能）
7. ✅ AiPromptMapper 方法实现
8. ✅ AiConversationMapper 方法实现
9. ✅ ChatStreamEnum
10. ✅ AstroHistoryRecorder 接口
11. ✅ AiConversationEntity.createEntity 方法

### 建议实现（提升质量）
12. ✅ AstrsomnProperties 完善
13. ✅ AiBuildParamGenerator 依赖修复
14. ✅ AiChatBuildParam 重构
15. ✅ AstroChatRequest 集成
16. ✅ application.properties 配置
17. ✅ AstrsomnAutoConfiguration 增强

---

**文档版本**: V1.0  
**最后更新**: 2026-03-18  
**维护人员**: Astrsomn Team
