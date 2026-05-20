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

### Module Dependency Graph

```
astrsomn-common                           ← Leaf: base classes (IError, BusinessException, BaseResponse)
 │
 ├── astrsomn-api-storage                 ← Storage DTOs/Entities
 ├── astrsomn-api-workflow                ← Workflow DTOs/Entities
 ├── astrsomn-api-system                  ← System DTOs/Entities
 ├── astrsomn-api-vector                  ← Vector-store DTOs/Entities
 │
 ├── astrsomn-api-runtime                 ← Hub: core runtime DTOs/Entities/Enums
 │    ├── depends on: api-system, api-vector (transitive: common)
 │    │
 │    ├── astrsomn-internal-storage       ← Internal storage impl (depends on api-storage + common)
 │    │
 │    ├── astrsomn-system-starter         ← System auto-config (depends on api-runtime, api-system, api-vector)
 │    ├── astrsomn-vector-starter         ← Vector auto-config (depends on api-runtime, api-system, api-vector)
 │    │
 │    ├── astrsomn-runtime-starter        ← Runtime auto-config (depends on api-runtime, api-system,
 │    │    │                                  api-vector, vector-starter, system-starter)
 │    │    │
 │    │    └── astrsomn-workflow-starter  ← Workflow auto-config (depends on runtime-starter, api-workflow)
 │    │
 │    ├── astrsomn-plugins/astrsomn-providers/*
 │    │    ├── astrsomn-provider-deepseek
 │    │    ├── astrsomn-provider-openai
 │    │    ├── astrsomn-provider-qianfan
 │    │    ├── astrsomn-provider-qwen
 │    │    └── astrsomn-provider-zhipu
 │    │
 │    └── astrsomn-plugins/astrsomn-vector/*
 │         ├── astrsomn-vector-chroma
 │         ├── astrsomn-vector-milvus
 │         ├── astrsomn-vector-qdrant
 │         └── astrsomn-vector-redis
 │
 └── astrsomn-server                      ← Executable JAR: directly depends on 12 project modules
      (api-runtime, api-storage, internal-storage, workflow-starter,
       all 5 providers, all 4 vector stores)
```

**Dependency rules:**
- `astrsomn-common` is the leaf — no project-internal dependencies; all modules transitively depend on it
- `astrsomn-api-runtime` is the central hub — depended on by every starter, every provider, every vector plugin, and the server
- All provider/vector plugins depend **only** on `astrsomn-api-runtime` (inherited from parent POM)
- Starter modules wire everything together via Spring Boot auto-configuration
- The graph is strictly acyclic — dependencies flow `common → api → integrations → server` with plugins hanging off `api-runtime`

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
- Enum code values are **pure English** (e.g., `ENABLED`, `DISABLED`, `OPENAI`) — they serve as the contract between backend and frontend dictionaries
- Keep enum classes minimal: one line per constant, no Javadoc, no Chinese comments
- **Enum ↔ Dictionary contract:** every backend enum code must have a matching frontend dictionary entry (zh-CN + en-US) — see Frontend Conventions > i18n

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

## Frontend Conventions

**Stack:** Vue 3 (Composition API) + TypeScript + Vite 6 + Ant Design Vue 4

### Component Organization — Mandatory Directory Structure

Every domain module under `src/views/admin/<section>/<domain>/` **must** follow this layout:

```
<domain>/
  <Domain>List.vue            ← Page-level: AstPageShell → AstDataSection → AstDataView
  <Domain>Form.vue            ← Create/Edit form (AstDrawer wrapper, must be < 400 lines)
  component/                  ← Reusable sub-components (cards, table cells, form sections)
    <Domain>Card.vue          ← Card component for grid/list view
  selector/                   ← Entity-picker drawers/modals (used by other domains)
    <Domain>SelectorDrawer.vue
```

**File-size rule:** No single `.vue` file should exceed 400 lines. Split large files by extracting:
- **Card components** → `component/<Domain>Card.vue`
- **Form sections** → `component/<Domain>FormSection.vue`
- **Selector drawers** → `selector/<Domain>SelectorDrawer.vue`
- **Table column renders** → `component/<Domain>Columns.ts` or dedicated cell components

### Component Naming & Patterns

- Shared shell components use `Ast` prefix: `AstPageShell`, `AstDataView`, `AstDrawer`, `AstPagination`, `AstSearchInput`, `AstOverview`, `AstKeyGenerator`, `AstSegmentedButton`
- All components use `<script lang="ts" setup>` exclusively
- Props typed with `defineProps<{...}>()`, defaults via `withDefaults()`
- Emits typed with `defineEmits<{(e: 'event', payload: Type): void}>()`
- No Pinia/Vuex — state managed via `ref()` / `reactive()` / `computed()` in composables or local scope
- API calls wrapped per-domain in `src/api/<domain>.ts` using the shared Axios instance from `src/utils/request.ts`

### Internationalization (i18n)

**All user-visible text must be translatable.** Two-tier system in `src/locales/`:

1. **Enum dictionary** (`locales/dictionary/`) — maps backend enum codes to i18n labels:
   ```ts
   // Backend enum code (pure English): AiProviderEnum.OPENAI.getCode() → "OPENAI"
   // Frontend dictionary maps that code to locale-aware display text:
   const dict = useDictionary('ai-model.provider')
   dict.getLabel(record.provider)  // "OPENAI" → "OpenAI" (en-US) or "OpenAI" (zh-CN)
   ```
   - Dictionary IDs must match backend enum class names converted to kebab-case (e.g., `AiProviderEnum` → `ai-model.provider`)
   - Dictionary keys must exactly match backend enum code values (e.g., `OPENAI`, `ENABLED`)
   - Dictionaries registered in `locales/dictionary/registry.ts`
   - Each provides `zh-CN` (default) and `en-US` translations
   - When adding a new backend enum value, you **must** add the corresponding dictionary entry in the frontend

2. **Page translations** (`locales/pages.ts`) — for page titles, form labels, button text, placeholders:
   ```ts
   const { t } = usePageTranslation('aiAccount')
   t('create.title')  // returns "创建账号" or "Create Account"
   ```

**Rules:**
- Never hardcode Chinese or English strings in templates — always go through dictionary or page translation
- New domain → create its dictionary entries and page translation keys before writing UI
- Language persisted in `localStorage` key `lang`; switched via `useLanguage()` composable

### Theme / Dark Mode

**Every component must support both dark and light themes.** Theme is implemented via CSS custom properties defined in `src/styles/theme.css`.

- **Dark theme:** variables declared on `:root` (default)
- **Light theme:** variables declared on `:root.light` (toggled by adding `.light` class to `<html>`)
- Theme switched via `useTheme()` composable, persisted in `localStorage` key `theme`

**Rules for component styles:**
- Always use CSS custom properties, never hardcoded color values in `<style scoped>`:
  ```css
  /* CORRECT */
  .card { background: var(--bg-card); color: var(--text-primary); border: 1px solid var(--border-default); }
  .card:hover { border-color: var(--primary); box-shadow: var(--shadow-card); }

  /* WRONG */
  .card { background: #121e2d; color: #e1e9f5; border: 1px solid #1e2f46; }
  ```
- When a component needs a local shade, alias globals at the top of `<style scoped>`:
  ```css
  --card-bg: var(--bg-card);
  --card-border: var(--border-default);
  --text-main: var(--text-primary);
  ```
- Use `var(--transition-smooth)` or `var(--transition-pop)` for animated property changes
- Radius: use `var(--radius-sm)` / `var(--radius-md)` / `var(--radius-lg)` — never hardcoded px
- Test new components in both themes before marking complete

### API Layer

- Single Axios instance in `src/utils/request.ts` — no `baseURL` (same-origin proxied)
- Request interceptor attaches `Authorization: Bearer <token>` and workspace env header
- Response interceptor unwraps `{ code, message, data }` or `{ success, message, data }`
- Each domain exports an API object from `src/api/<domain>.ts`:
  ```ts
  export const aiAccountApi = {
    queryPage: (params: PageRequest) => request.post('/v1/astro/ai-account/queryPage', params),
    detail: (id: string) => request.get('/v1/astro/ai-account/detail', { params: { id } }),
    create: (data: AccountCreateDTO) => request.post('/v1/astro/ai-account/create', data),
    update: (data: AccountUpdateDTO) => request.post('/v1/astro/ai-account/update', data),
    delete: (ids: string) => request.delete(`/v1/astro/ai-account/delete/${ids}`),
  }
  ```

### Admin Layout Hierarchy

```
App.vue
  <router-view>
    AdminHome.vue
      <AppHeader>                    ← Fixed top bar (60px)
      <ActivityBar>                  ← Fixed left bar (56px)
      <main>
        <router-view>
          AdminModuleLayout.vue
            <AdminModuleShell>       ← Sidebar menu (260px)
              <router-view>
                <Domain>List.vue     ← Page content
                  <AstPageShell>
                    <AstDataSection>
                      <AstDataView mode="card|table" />
                      <AstPagination />
```

### Selector Components

Selectors are cross-domain entity pickers. All follow the same pattern:
- Wrap `AstDrawer` with `:open` / `@update:open`
- Contain search bar + filter controls + scrollable list + `AstPagination` in footer slot
- Emit `select` (selected entity) and `update:open` (close)
- Live in `selector/` subdirectory — never inline a selector inside another domain's form

## Commit Style

```
[emoji] [type](scope): Chinese description
```

Emoji/type mapping: `✨ feat`, `🐛 fix`, `♻️ refactor`, `🗑️ chore`, `📝 docs`, `🎉 feat` (major)

Example: `♻️ [refactor](service-package): 重构服务层包结构，移除 envCode 依赖并统一响应消息`
