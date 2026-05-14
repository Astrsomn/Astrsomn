# Vector 和 System 模块拆分方案

## 一、拆分背景

当前 `astrsomn-runtime-starter` 模块包含了向量数据库管理和系统配置管理两类功能，随着业务发展，这两类功能的职责边界越来越清晰，需要进行模块拆分以实现：

- **职责单一化**：向量模块专注于向量存储、检索、RAG 能力；系统模块专注于系统配置、环境管理、用户管理
- **按需依赖**：允许应用只引入需要的模块
- **独立演进**：两个模块可以独立迭代和发布

---

## 二、实体分类

### 2.1 Vector 相关实体

| Entity 文件 | 说明 | 所属模块 |
|------------|------|----------|
| `AiVecDocEntity.java` | 向量文档实体 | vector |
| `AiVecDriverEntity.java` | 向量驱动实体 | vector |
| `AiVecSegmentEntity.java` | 向量片段实体 | vector |
| `AiVecSourceEntity.java` | 向量源实体 | vector |
| `AiVecStoreEntity.java` | 向量存储实体 | vector |

### 2.2 System 相关实体

| Entity 文件 | 说明 | 所属模块 |
|------------|------|----------|
| `SystemConfigEntity.java` | 系统配置实体 | system |
| `SystemEnvEntity.java` | 系统环境实体 | system |
| `SystemExtensionEntity.java` | 系统扩展实体 | system |
| `SystemMessageEntity.java` | 系统消息实体 | system |
| `SystemUserEntity.java` | 系统用户实体 | system |

---

## 三、模块拆分方案

### 3.1 新模块结构

```
Astrsomn/
├── astrsomn-api/
│   ├── astrsomn-api-runtime/           # 公共 API 层（保持不变）
│   │   └── src/main/java/com/astrsomn/api/runtime/common/entity/
│   │       ├── vector/                 # 新增：Vector 实体子包
│   │       │   ├── AiVecDocEntity.java
│   │       │   ├── AiVecDriverEntity.java
│   │       │   ├── AiVecSegmentEntity.java
│   │       │   ├── AiVecSourceEntity.java
│   │       │   └── AiVecStoreEntity.java
│   │       └── system/                 # 新增：System 实体子包
│   │           ├── SystemConfigEntity.java
│   │           ├── SystemEnvEntity.java
│   │           ├── SystemExtensionEntity.java
│   │           ├── SystemMessageEntity.java
│   │           └── SystemUserEntity.java
│   ├── astrsomn-api-vector/            # 新增：Vector API 模块
│   └── astrsomn-api-system/            # 新增：System API 模块
└── astrsomn-integrations/
    ├── astrsomn-runtime-starter/       # 保留：核心启动器（基础配置）
    ├── astrsomn-vector-starter/        # 新增：Vector 启动器
    └── astrsomn-system-starter/        # 新增：System 启动器
```

### 3.2 Starter 文件拆分映射

#### 3.2.1 归入 `astrsomn-vector-starter` 的文件

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `mapper/AstAiVecStoreMapper.java` | `astrsomn-vector-starter/src/main/java/.../mapper/` | 向量存储 Mapper |
| `mapper/AstAiVecSourceMapper.java` | `astrsomn-vector-starter/src/main/java/.../mapper/` | 向量源 Mapper |
| `mapper/AstAiVecDocMapper.java` | `astrsomn-vector-starter/src/main/java/.../mapper/` | 向量文档 Mapper |
| `mapper/AstAiVecSegmentMapper.java` | `astrsomn-vector-starter/src/main/java/.../mapper/` | 向量片段 Mapper |
| `langchain/vector/AstroVecSourceFactory.java` | `astrsomn-vector-starter/src/main/java/.../langchain/vector/` | 向量源工厂 |
| `langchain/tool/rag/VectorStoreRegistry.java` | `astrsomn-vector-starter/src/main/java/.../langchain/tool/rag/` | 向量存储注册器 |
| `langchain/tool/rag/DynamicRagProvider.java` | `astrsomn-vector-starter/src/main/java/.../langchain/tool/rag/` | 动态 RAG 提供者 |

#### 3.2.2 归入 `astrsomn-system-starter` 的文件

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `mapper/AstSystemUserMapper.java` | `astrsomn-system-starter/src/main/java/.../mapper/` | 系统用户 Mapper |
| `mapper/AstSystemMessageMapper.java` | `astrsomn-system-starter/src/main/java/.../mapper/` | 系统消息 Mapper |
| `mapper/AstSystemExtensionMapper.java` | `astrsomn-system-starter/src/main/java/.../mapper/` | 系统扩展 Mapper |
| `mapper/AstSystemEnvMapper.java` | `astrsomn-system-starter/src/main/java/.../mapper/` | 系统环境 Mapper |
| `mapper/AstSystemConfigMapper.java` | `astrsomn-system-starter/src/main/java/.../mapper/` | 系统配置 Mapper |
| `message/SystemMessageRecorder.java` | `astrsomn-system-starter/src/main/java/.../message/` | 系统消息记录器 |

#### 3.2.3 保留在 `astrsomn-runtime-starter` 的文件

| 文件 | 说明 |
|------|------|
| `config/AstrsomnAutoConfiguration.java` | 核心自动配置 |
| `config/AstrsomnDataSourceConfiguration.java` | 数据源配置 |
| `config/AstrsomnProperties.java` | 核心属性配置 |
| `config/AstrsomnMetaObjectHandler.java` | 元对象处理器 |
| `context/EnvRuntime.java` | 环境运行时 |
| `context/UserContext.java` | 用户上下文 |
| `langchain/factory/AstroAssistantFactory.java` | 助手工厂 |
| `langchain/factory/AstroModelFactory.java` | 模型工厂 |
| `langchain/route/*` | 路由策略相关 |
| `langchain/stream/*` | 流式处理相关 |
| `plugin/AstrsomnPluginManager.java` | 插件管理器 |
| `schema/SchemaInitializer.java` | Schema 初始化器 |

---

## 四、依赖关系设计

### 4.1 模块依赖图

```
                    ┌─────────────────────────┐
                    │    astrsomn-api-runtime │
                    │  (公共实体、枚举、异常)    │
                    └───────────┬─────────────┘
                                │
           ┌────────────────────┼────────────────────┐
           ▼                    ▼                    ▼
   ┌──────────────┐    ┌─────────────────┐    ┌─────────────────┐
   │astrsomn-api- │    │astrsomn-api-    │    │astrsomn-api-    │
   │vector        │    │system           │    │ai-core         │
   │(向量API接口)  │    │(系统API接口)     │    │(AI核心API)      │
   └──────┬───────┘    └────────┬────────┘    └────────┬────────┘
          │                     │                      │
          ▼                     ▼                      ▼
   ┌──────────────┐    ┌─────────────────┐    ┌─────────────────┐
   │astrsomn-     │    │astrsomn-        │    │astrsomn-        │
   │vector-       │    │system-          │    │runtime-         │
   │starter       │────│starter          │────│starter          │
   │(向量启动器)   │    │(系统启动器)      │    │(核心启动器)      │
   └──────────────┘    └─────────────────┘    └─────────────────┘
```

### 4.2 pom.xml 依赖声明

#### astrsomn-vector-starter/pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>com.astrsomn</groupId>
        <artifactId>astrsomn-api-runtime</artifactId>
    </dependency>
    <dependency>
        <groupId>com.astrsomn</groupId>
        <artifactId>astrsomn-runtime-starter</artifactId>
    </dependency>
    <!-- 向量数据库驱动依赖（可选）-->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### astrsomn-system-starter/pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>com.astrsomn</groupId>
        <artifactId>astrsomn-api-runtime</artifactId>
    </dependency>
    <dependency>
        <groupId>com.astrsomn</groupId>
        <artifactId>astrsomn-runtime-starter</artifactId>
    </dependency>
</dependencies>
```

---

## 五、AutoConfiguration 拆分

### 5.1 astrsomn-vector-starter 配置类

```java
// AstrsomnVectorAutoConfiguration.java
@Configuration
@ConditionalOnClass({AiVecSourceEntity.class, AstroVecSourceFactory.class})
@EnableConfigurationProperties(AstrsomnVectorProperties.class)
public class AstrsomnVectorAutoConfiguration {
    
    @Bean
    public AstroVecSourceFactory astroVecSourceFactory() {
        return new AstroVecSourceFactory();
    }
    
    @Bean
    public VectorStoreRegistry vectorStoreRegistry(ObjectProvider<RagEmbeddingStoreResolver> resolver) {
        return new VectorStoreRegistry(resolver);
    }
    
    @Bean
    public DynamicRagProvider dynamicRagProvider(AstroModelFactory modelFactory, 
                                                  VectorStoreRegistry registry) {
        return new DynamicRagProvider(modelFactory, registry);
    }
}
```

### 5.2 astrsomn-system-starter 配置类

```java
// AstrsomnSystemAutoConfiguration.java
@Configuration
@ConditionalOnClass({SystemConfigEntity.class, SystemMessageRecorder.class})
public class AstrsomnSystemAutoConfiguration {
    
    @Bean
    public SystemMessageRecorder systemMessageRecorder(AstSystemMessageMapper mapper) {
        return new SystemMessageRecorder(mapper);
    }
}
```

---

## 六、迁移步骤

| 步骤 | 操作 | 负责人 | 预估时间 |
|------|------|--------|----------|
| 1 | 创建新模块目录结构 | 架构师 | 0.5d |
| 2 | 迁移 Entity 到新的子包 | 开发 | 0.5d |
| 3 | 迁移 Mapper 到对应模块 | 开发 | 0.5d |
| 4 | 迁移业务类到对应模块 | 开发 | 1d |
| 5 | 编写新模块的 AutoConfiguration | 开发 | 0.5d |
| 6 | 更新 pom.xml 依赖关系 | 开发 | 0.5d |
| 7 | 编译验证 | 开发 | 0.5d |
| 8 | 单元测试验证 | 测试 | 1d |
| 9 | 集成测试验证 | 测试 | 1d |

---

## 七、兼容性保证

### 7.1 向后兼容策略

1. **保留旧包路径别名**：在原位置保留空类并继承新位置的类（Deprecated）
2. **提供迁移指南**：文档说明如何从旧依赖迁移到新依赖
3. **过渡期双支持**：在一个版本周期内同时支持新旧依赖方式

### 7.2 旧依赖声明（兼容模式）

```xml
<!-- 旧方式（兼容）-->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-runtime-starter</artifactId>
</dependency>

<!-- 新方式（推荐）-->
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-runtime-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-vector-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.astrsomn</groupId>
    <artifactId>astrsomn-system-starter</artifactId>
</dependency>
```

---

## 八、注意事项

1. **事务边界**：确保跨模块调用时事务一致性
2. **循环依赖**：避免 vector 和 system 模块之间的循环依赖
3. **配置优先级**：明确多模块配置的加载顺序和优先级
4. **日志追踪**：确保跨模块调用时日志链路完整
5. **插件兼容性**：检查现有插件是否需要适配新的模块结构