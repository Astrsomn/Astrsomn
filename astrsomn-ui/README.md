# Astrsomn UI

基于 Vue3 + Ant Design Vue + TypeScript 的前端项目

## 项目结构

```
astrsomn-ui/
├── src/
│   ├── api/              # API接口层（与页面分离）
│   │   └── auth.ts       # 认证相关接口
│   ├── types/            # TypeScript类型定义
│   │   └── index.ts      # 通用类型定义
│   ├── utils/            # 工具函数
│   │   └── request.ts    # Axios封装
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页面
│   │   └── Home.vue      # 首页
│   ├── router/           # 路由配置
│   │   └── index.ts      # 路由定义
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── public/               # 静态资源
├── index.html            # HTML模板
├── vite.config.ts        # Vite配置
├── tsconfig.json         # TypeScript配置
└── package.json          # 项目依赖
```

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Ant Design Vue** - 企业级UI组件库
- **TypeScript** - JavaScript的超集
- **Vue Router** - Vue.js官方路由
- **Axios** - HTTP客户端
- **Vite** - 下一代前端构建工具

## 功能特性

### 已实现
- ✅ 用户登录功能
- ✅ API接口层与页面分离
- ✅ 路由守卫（登录验证）
- ✅ Token管理
- ✅ 响应式布局
- ✅ 表单验证
- ✅ 错误处理

### 接口说明

#### 登录接口
- **URL**: `/api/auth/login`
- **方法**: POST
- **请求参数**:
  ```typescript
  {
    username: string,
    password: string
  }
  ```
- **响应**:
  ```typescript
  {
    code: 200,
    message: "success",
    data: {
      token: string,
      userInfo: {
        id: string,
        username: string,
        email?: string
      }
    }
  }
  ```

#### 退出登录接口
- **URL**: `/api/auth/logout`
- **方法**: POST

#### 获取用户信息接口
- **URL**: `/api/auth/user-info`
- **方法**: GET

## 开发指南

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产构建
```bash
npm run preview
```

## 配置说明

### 环境变量

开发环境 (`.env.development`):
```
VITE_API_BASE_URL=http://localhost:8080/api
```

生产环境 (`.env.production`):
```
VITE_API_BASE_URL=/api
```

### 代理配置

在 `vite.config.ts` 中配置了API代理，开发环境下 `/api` 请求会被代理到 `http://localhost:8080`。

## 路由配置

- `/login` - 登录页面（无需认证）
- `/` - 首页（需要认证）

路由守卫会自动检查用户是否已登录，未登录用户访问需要认证的页面会被重定向到登录页。

## API层设计

API层与页面完全分离，遵循以下原则：

1. **统一封装**: 使用 `request.ts` 统一封装Axios，处理请求/响应拦截
2. **类型安全**: 使用TypeScript定义接口类型
3. **错误处理**: 统一的错误处理机制
4. **Token管理**: 自动添加Token到请求头

## 后续开发建议

1. 在 `src/api/` 目录下添加更多业务模块的API接口
2. 在 `src/views/` 目录下添加更多页面组件
3. 在 `src/router/index.ts` 中配置新路由
4. 在 `src/types/index.ts` 中添加新的类型定义
5. 考虑添加状态管理（Pinia/Vuex）
6. 添加单元测试
7. 优化构建配置和性能

## 注意事项

- 确保后端API接口地址正确配置
- Token默认存储在localStorage中，可根据需求改为Cookie
- 密码长度验证设置为最少6位，可在Login.vue中修改
- 所有API请求都经过统一的错误处理
