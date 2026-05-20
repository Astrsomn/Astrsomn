# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build entire project (skip tests)
mvn clean install -DskipTests

# Build a single module with dependencies
mvn clean install -pl astrsomn-server -am -DskipTests

# Compile only (verify changes compile)
mvn compile -pl astrsomn-server -am -q -DskipTests

# Run all tests
mvn test -pl astrsomn-server

# Package the server JAR
mvn clean package -pl astrsomn-server -DskipTests

# Frontend
cd astrsomn-ui && npm run dev       # dev server
cd astrsomn-ui && npm run build     # production build
```

## Project Architecture

**Astrsomn** is an AI Agent runtime platform — a Spring Boot 3.3 / Java 17 multi-module Maven project with a Vue 3 frontend.

### Module Dependency (bottom-up)

```
astrsomn-common          → Base classes, IError, BusinessException, BaseResponse
astrsomn-api/*           → DTOs, Entities, Enums, Constants (shared across server & plugins)
astrsomn-integrations/*  → Spring Boot Starters (auto-config, MyBatis, LangChain4j wiring)
astrsomn-plugins/*       → AI model providers (DeepSeek, Zhipu, Qianfan, Qwen, OpenAI)
                        → Vector stores (Chroma, Milvus, Qdrant, Redis)
astrsomn-server          → Spring Boot executable, depends on integrations + plugins
```

### Server Layer Structure

```
api/         → @RestController (extends BaseController)
service/     → Interface + Impl (extends ServiceImpl<Mapper, Entity>)
mapper/      → MyBatis-Plus BaseMapper interfaces (+ XML in resources/mapper/)
config/      → Spring config (MybatisConfig, CorsConfig, FlywayConfig, etc.)
interceptor/ → Authentication, Authorization, RateLimiting, EnvCode, Logging
filter/      → ExceptionHandlingFilter, RequestWrapper, CORS
exception/   → GlobalExceptionHandler (@RestControllerAdvice)
```

## Key Conventions

### Controller Pattern

- Extend `BaseController` (provides `success()`, `fail()`, `parseLongIds()`)
- `@RequestMapping("/v1/astro/<resource-name>")` — kebab-case
- `@RequiredArgsConstructor` for constructor injection, never `@Autowired`
- CRUD endpoints (not strict REST):

| HTTP | Path | Request Body | Return |
|------|------|-------------|--------|
| POST | `/create` | `XxxCreateRequestDTO` | `BaseResponse<String>` |
| POST | `/saveOrUpdate` | `XxxCreateRequestDTO` | `BaseResponse<String>` |
| POST | `/update` | `XxxUpdateRequestDTO` | `BaseResponse<String>` |
| DELETE | `/delete/{ids}` | (comma-separated in path) | `BaseResponse<String>` |
| POST | `/queryPage` | `BasePageRequest<XxxQueryRequestDTO>` | `PageResponse<XxxResponseDTO>` |
| GET | `/detail` | `?id=` query param | `BaseResponse<XxxResponseDTO>` |

- Delete endpoints parse IDs: `parseLongIds(ids, ",")` with try-catch for `NumberFormatException`
- `saveOrUpdate` checks existence by business key (not id): query by key → if null, create; else update + replace children

### Service Pattern

- Interface extends `IService<Entity>`, impl extends `ServiceImpl<Mapper, Entity>`
- Interface + Impl in the **same package** (`com.astrsomn.server.service.<domain>`)
- Method signatures return `BaseResponse<T>` or `PageResponse<T>`
- Business errors thrown as `throw new BusinessException(XxxErrorEnum.ERROR_CODE)`
- DTO → Entity conversion via `BeanUtils.copyProperties()` (Spring)

### Entity / DTO Convention

- Entities extend `BaseEntity<Long>` (provides `id`, `createTime`, `updateTime`, `createUser`, `updateUser`, `deleted`, `envCode`)
- `@TableName("UPPER_CASE_TABLE")`, `@TableId(value = "ID", type = IdType.ASSIGN_ID)`, `@TableField("COLUMN_NAME")`
- ID field annotated with `@JsonSerialize(using = ToStringSerializer.class)` (prevents JS precision loss for Snowflake IDs)
- DTOs: `XxxCreateRequestDTO`, `XxxUpdateRequestDTO`, `XxxQueryRequestDTO`, `XxxResponseDTO`
- Request DTOs that carry nested children extend the entity and add `List<ChildRequestDTO>` fields
- API DTOs/Entities live in `astrsomn-api/astrsomn-api-runtime/src/main/java/com/astrsomn/api/runtime/common/`

### Error Handling

- Every business error **must** have a corresponding `XxxErrorEnum` entry — never throw `new BusinessException("ad-hoc message")`
- Domain error enums implement `IError`, placed in `astrsomn-api/astrsomn-api-runtime/src/main/java/com/astrsomn/api/runtime/exception/`
- Error messages in enums are in **English** (e.g., `"Agent not found"`, not `"智能体不存在"`)
- Throw pattern: `throw new BusinessException(AiAgentErrorEnum.AGENT_NOT_FOUND)`
- Global catch: `GlobalExceptionHandler` (`@RestControllerAdvice`) + `ExceptionHandlingFilter` (servlet filter fallback)

### Enum Usage

- Status fields, type fields, and other fixed-value columns **must** use enum constants, never raw string literals
- Enum pattern: define `StatusEnum` or similar inner enums within the domain constant class (e.g., `AiInstanceEnum.StatusEnum`, `AiPromptEnum.StatusEnum`)
- Assignment: `entity.setStatus(AiAgentEnum.StatusEnum.ENABLED.getCode())`, never `entity.setStatus("ENABLED")`
- Comparison: `AiAgentEnum.StatusEnum.ENABLED.getCode().equals(entity.getStatus())`, never `"ENABLED".equals(...)`

### Multi-tenancy

- `envCode` field on `BaseEntity` drives data isolation
- `EnvCodeRequestInterceptor` extracts envCode from request context into `UserContext` ThreadLocal

### Logging & Return Messages

- Log messages and `BaseResponse` return messages **must** be in **English**
- `BaseResponse.success("success")` — not `"操作成功"`
- Log: `log.info("Agent created, agentKey={}", agentKey)` — not `"智能体创建成功"`

### Database Query Rules

- **Single-table queries** → `LambdaQueryWrapper` with method references, never raw SQL or string-based wrappers
  ```java
  lambdaQuery().eq(AiAgentEntity::getAgentKey, agentKey).one();
  ```
- **Multi-table / complex joins / aggregations** → XML mapper with `@Param` annotations
- **Pagination** → `PageUtils.buildPage(request)` + `baseMapper.queryPage(page, param)`

### Null & Empty Checks

| Scenario | Tool |
|----------|------|
| Object is null | `Objects.isNull(obj)` |
| Object is not null | `Objects.nonNull(obj)` |
| Collection empty | `CollectionUtils.isEmpty(list)` (Hutool or common) |
| String blank | `StringUtils.isBlank(str)` (from `com.astrsomn.common.utils.StringUtils`) |

Never use `obj == null`, `list.size() == 0`, or `str == null || str.isEmpty()`.

### Stream & Optional

- Prefer `Stream` API for collection transformations, filtering, and mapping over imperative for-loops
- Use `Optional` to express nullable return values — never return null from a method that has a meaningful absence case
- `Optional.ofNullable(...).orElse(...)` / `.orElseGet(...)` / `.ifPresent(...)` over nested null checks
- Avoid `Optional.get()` without `isPresent()` — use `.orElseThrow()` instead

## Commit Style

```
[emoji] [type](scope): Chinese description
```

Emoji/type mapping: `✨ feat`, `🐛 fix`, `♻️ refactor`, `🗑️ chore`, `📝 docs`, `🎉 feat` (major)

Example: `♻️ [refactor](service-package): 重构服务层包结构，移除 envCode 依赖并统一响应消息`
