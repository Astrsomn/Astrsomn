# Astrsomn AI Gateway

> 中文文档请参阅 [README.md](README.md)

---

## Requirements

- **JDK 17** or later → [Download](https://adoptium.net/)
- MySQL 8.0+ (required)

### Database Setup

Before first launch, create the database and import the schema:

```bash
# 1. Create the database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS astro_ai DEFAULT CHARACTER SET utf8mb4;"

# 2. Import the initialization script
mysql -u root -p astro_ai < database/V1__Initial.sql
```

### Quick Start

**1. Edit the config file**

Open `config/application.yml` and update the database connection and JWT secret:

```yaml
# Required changes:
#   astrsomn.datasource.url       — database URL
#   astrsomn.datasource.username  — database username
#   astrsomn.datasource.password  — database password
#   jwt.secret                     — JWT signing key (use a random string)
```

**2. Start the server**

**Linux / macOS:**
```bash
chmod +x bin/start.sh bin/stop.sh
./bin/start.sh
```

**Windows:**
Double-click `bin/start.bat` — a console window will open. Close the window to stop the server.
```cmd
bin\start.bat
```

### After Startup

Open **http://localhost:4481** and log in with the credentials configured in `config/application.yml` (default: `admin` / `admin`).

```bash
# Linux / macOS
./bin/start.sh --logs     # View server logs
./bin/stop.sh             # Stop server

# Windows
bin\start.bat             # Foreground, Ctrl+C or close window to stop
bin\stop.bat              # Or use this script to stop
```

### Directory Layout

```
astrsomn/
├── VERSION                   # Version info
├── astrsomn-server.jar       # Application
├── config/
│   └── application.yml           # Configuration (database, port, JWT, etc.)
├── database/
│   └── V1__Initial.sql           # Database schema script
├── bin/
│   ├── start.sh / start.bat  # Start scripts
│   ├── stop.sh / stop.bat    # Stop scripts
│   └── .env                   # Launcher config (Java path, JVM options)
├── log/                      # Runtime logs
└── storage/                  # File uploads
```

### Troubleshooting

| Problem | Solution |
|---------|----------|
| Port in use | Change `server.port` in `config/application.yml` |
| Java not found | Install JDK 17+ from https://adoptium.net/ |
| Permission denied | `chmod +x bin/start.sh bin/stop.sh` |
| Tables not found | Run `database/V1__Initial.sql` to initialize the database |
| MySQL connection refused | Check host/port/credentials in `config/application.yml` |
| View error logs | `start.sh --logs` or check `log/astrsomn-server.log` |
