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

Scripts resolve the repository root from their own location (`deploy/maven-deploy` → parent of `deploy`). Run them from any working directory after `cd` into this folder.

### 1. Deploy Providers Modules

Deploy selected provider modules (edit `MODULES` inside the script to match what you need):

```bash
cd <repo-root>/deploy/maven-deploy
.\deploy-providers.bat
```

### 2. Deploy Integrations Modules

Deploy selected integration modules (edit `MODULES` inside the script as needed):

```bash
cd <repo-root>/deploy/maven-deploy
.\deploy-integrations.bat
```

### 3. Deploy All Modules

Deploy the same set as the full manual pipeline (five providers plus internal-storage, runtime-starter, workflow-starter):

```bash
cd <repo-root>/deploy/maven-deploy
.\deploy-all.bat
```

## Formal release (maven-release-plugin)

For a versioned release with Git commits, tags, and upload to GitHub Packages—while **only deploying the same eight jar modules** as `deploy-all.bat`—use the root POM and the `maven-release-plugin`:

1. **Prerequisites**
   - The reactor version must be a **`-SNAPSHOT`** (for example `0.2.0-SNAPSHOT`).
   - Git repository with `origin` configured; `user.name` and `user.email` set.
   - `~/.m2/settings.xml` includes the `github-astrsomn` server (see below).
   - The plugin is already configured to pass **`-Pgithub-packages-release`** to the deploy forked by `release:perform`, so you do not need to add that profile on the command line.

2. **Prepare** (sets release version, commits, tags, then bumps to the next development version and commits):

```bash
cd <repo-root>
mvn release:prepare
```

Non-interactive example:

```bash
mvn -B release:prepare -DreleaseVersion=0.2.0 -DdevelopmentVersion=0.3.0-SNAPSHOT
```

To prepare without pushing (push tags and commits yourself later):

```bash
mvn release:prepare -DpushChanges=false
```

3. **Perform** (checks out the release tag, builds, deploys to GitHub Packages):

```bash
mvn release:perform
```

4. **Rollback a failed prepare** (removes SCM backup and release properties; does not undo Git changes already pushed):

```bash
mvn release:clean
```

**Note:** The default `release:perform` flow does **not** activate the `release` profile (GPG, Javadoc, sources). Publishing to GitHub Packages does not require GPG. Use `-Prelease` only when you intend to sign artifacts and have GPG configured.

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
