# Astrsomn Maven Deployment Scripts

## Directory Structure

```
deploy/
├── maven-deploy/              # Maven deployment scripts
│   ├── deploy-providers.bat   # Deploy providers modules
│   ├── deploy-integrations.bat # Deploy integrations modules
│   ├── deploy-all.bat         # Deploy all modules
│   └── README.md              # Usage documentation
└── windows/                   # Windows deployment scripts
    └── ...
```

## Usage

### 1. Deploy Providers Modules

Deploy all AI provider modules (OpenAI, Qianfan, Qwen, Zhipu, Deepseek):

```bash
cd D:\Project\astrsomn\Astrsomn\deploy\maven-deploy
.\deploy-providers.bat
```

### 2. Deploy Integrations Modules

Deploy all integration modules (internal-storage, runtime-starter, workflow-starter):

```bash
cd D:\Project\astrsomn\Astrsomn\deploy\maven-deploy
.\deploy-integrations.bat
```

### 3. Deploy All Modules

Deploy all modules at once:

```bash
cd D:\Project\astrsomn\Astrsomn\deploy\maven-deploy
.\deploy-all.bat
```

## Prerequisites

1. **Maven Environment**: Ensure Maven is installed and configured in your PATH
2. **Maven Configuration**: Ensure `~/.m2/settings.xml` contains GitHub Packages authentication:

```xml
<servers>
    <server>
        <id>github-astrsomn</id>
        <username>Your GitHub Username</username>
        <password>Your GitHub Personal Access Token</password>
    </server>
</servers>
```

3. **Token Permissions**: Ensure your token has the following permissions:
   - `write:packages` - Allow publishing packages
   - `read:packages` - Allow reading packages

## Notes

1. **First Deployment**: First-time deployment may take a few minutes to initialize the GitHub Packages repository
2. **Version Conflicts**: If the same version already exists, deployment will fail. Use a new version number or delete the old version
3. **Network Environment**: Ensure network connectivity to access GitHub Packages

## Deployment Process

Each script performs the following steps:

1. **Install Dependencies**: Install all dependencies to local Maven repository
2. **Deploy Modules**: Deploy specified modules to GitHub Packages one by one
3. **Error Handling**: Stop immediately and display error message if any module deployment fails

## View Deployment Results

After successful deployment, visit the following URL to view published packages:

```
https://github.com/Astrsomn/Astrsomn/packages
```

## Common Issues

### 401 Unauthorized
- Check if the token is correct
- Check if the token has `write:packages` permission
- Check if settings.xml configuration is correct

### 422 Unprocessable Entity
- GitHub Packages does not support pom-type packages
- Scripts automatically handle this issue by only deploying jar modules

### Connection Timeout
- Check network connection
- Check GitHub service status
