# Astrsomn AI 网关平台

> For English, see [README.en.md](README.en.md)

---

## 环境要求

- **JDK 17** 或更高版本 → [下载](https://adoptium.net/)
- MySQL 8.0+（必需）

### 数据库初始化

首次使用前，需要先创建数据库并导入初始化脚本：

```bash
# 1. 登录 MySQL 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS astro_ai DEFAULT CHARACTER SET utf8mb4;"

# 2. 导入初始化脚本
mysql -u root -p astro_ai < database/V1__Initial.sql
```

### 快速开始

**1. 编辑配置文件**

打开 `config/application.yml`，修改数据库连接信息和 JWT 密钥：

```yaml
# 必改项：
#   astrsomn.datasource.url       — 数据库地址
#   astrsomn.datasource.username  — 数据库用户名
#   astrsomn.datasource.password  — 数据库密码
#   jwt.secret                     — JWT 签名密钥（改成随机字符串）
```

**2. 启动服务**

**Linux / macOS:**
```bash
chmod +x bin/start.sh bin/stop.sh
./bin/start.sh
```

**Windows:**
双击 `bin/start.bat`，会打开控制台窗口运行服务。关闭窗口即可停止。
```cmd
bin\start.bat
```

### 启动后

浏览器访问 **http://localhost:4481**，管理员账号密码在 `config/application.yml` 中配置（默认 `admin` / `admin`）。

```bash
# Linux / macOS
./bin/start.sh --logs     # 查看运行日志
./bin/stop.sh             # 停止服务

# Windows
bin\start.bat             # 前台运行，Ctrl+C 或关闭窗口停止
bin\stop.bat              # 或通过此脚本停止
```

### 目录结构

```
astrsomn/
├── VERSION                   # 版本信息
├── astrsomn-server.jar       # 主程序
├── config/
│   └── application.yml           # 配置文件（修改数据库、端口、JWT 等）
├── database/
│   └── V1__Initial.sql           # 数据库初始化脚本
├── bin/
│   ├── start.sh / start.bat  # 启动脚本
│   ├── stop.sh / stop.bat    # 停止脚本
│   └── env                    # 启动环境配置（Java 路径、JVM 参数）
├── log/                      # 运行日志
└── storage/                  # 文件存储
```

### 常见问题

| 问题 | 解决方法 |
|------|---------|
| 端口被占用 | 修改 `config/application.yml` 中的 `server.port` |
| 找不到 Java | 安装 JDK 17+ https://adoptium.net/ |
| 权限不足 | `chmod +x bin/start.sh bin/stop.sh` |
| 数据库表不存在 | 运行 `database/V1__Initial.sql` 初始化数据库 |
| MySQL 连接失败 | 检查 `config/application.yml` 中数据库主机/端口/用户名/密码 |
| 查看启动报错 | `start.sh --logs` 或查看 `log/astrsomn-server.log` |
