#!/usr/bin/env bash
set -euo pipefail

# ============================================================================
# Astrsomn Multi-Platform Packaging Build Script
# ============================================================================
# Produces three platform packages:
#   - astrsomn-<version>-linux.tar.gz
#   - astrsomn-<version>-macos.tar.gz
#   - astrsomn-<version>-windows.zip
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

UI_DIR="$PROJECT_ROOT/astrsomn-ui"
SERVER_DIR="$PROJECT_ROOT/astrsomn-server"
SERVER_TARGET="$SERVER_DIR/target"
OUTPUT_DIR="$SCRIPT_DIR/output"

# Extract version from root pom.xml
VERSION=$(grep -m1 '<version>' "$PROJECT_ROOT/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
ARTIFACT="astrsomn-${VERSION}"

echo "============================================"
echo " Astrsomn Multi-Platform Build"
echo " Version: $VERSION"
echo "============================================"
echo ""

# ============================================================
# Step 1: Check prerequisites
# ============================================================
echo "[1/5] Checking prerequisites..."

command -v node >/dev/null 2>&1 || { echo "ERROR: node not found in PATH. Install Node.js."; exit 1; }
command -v npm  >/dev/null 2>&1 || { echo "ERROR: npm not found in PATH. Install Node.js."; exit 1; }
command -v mvn  >/dev/null 2>&1 || { echo "ERROR: mvn not found in PATH. Install Maven."; exit 1; }

echo "  node: $(node --version)"
echo "  npm:  $(npm --version)"
echo "  mvn:  $(mvn --version 2>&1 | head -1)"
echo ""

# ============================================================
# Step 2: Build frontend
# ============================================================
echo "[2/5] Building frontend UI..."

cd "$UI_DIR"
npm install --silent
npm run build

# Ensure static resources exist — if Vite outDir is misconfigured, copy from dist/ as fallback
STATIC_DIR="$SERVER_DIR/src/main/resources/static"
if [ ! -f "$STATIC_DIR/index.html" ]; then
    echo "  Static dir empty, copying from dist/ fallback..."
    mkdir -p "$STATIC_DIR"
    cp -r "$UI_DIR/dist/"* "$STATIC_DIR/"
fi

echo "  Frontend build complete."
echo ""

# ============================================================
# Step 3: Build server fat JAR
# ============================================================
echo "[3/5] Building server JAR..."

cd "$PROJECT_ROOT"
mvn clean package -pl astrsomn-server -am -DskipTests -q

echo "  Server build complete."
echo ""

# ============================================================
# Step 4: Verify artifacts
# ============================================================
echo "[4/5] Verifying artifacts..."

# Find the built JAR (supports both with and without version in name)
JAR_FILE=$(ls "$SERVER_TARGET"/astrsomn-server*.jar 2>/dev/null | grep -v 'sources\|javadoc' | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: No server JAR found in $SERVER_TARGET"
    exit 1
fi

# Verify JAR contains frontend static files
if jar tf "$JAR_FILE" 2>/dev/null | grep -q 'BOOT-INF/classes/static/index.html'; then
    echo "  JAR:  $JAR_FILE"
    echo "  UI:   embedded (verified)"
else
    echo "WARNING: JAR does not contain frontend static files."
    echo "  The Vite build may not have run correctly."
    echo "  Continuing anyway..."
fi

mkdir -p "$OUTPUT_DIR"
echo ""

# ============================================================
# Step 5: Assemble platform packages
# ============================================================
echo "[5/5] Assembling platform packages..."

STAGE_DIR="$OUTPUT_DIR/astrsomn"
rm -rf "$STAGE_DIR"
mkdir -p "$STAGE_DIR/config" "$STAGE_DIR/bin" "$STAGE_DIR/database"

# --- Common files (same for all platforms) ---
cp "$JAR_FILE" "$STAGE_DIR/astrsomn-server.jar"
cp "$SCRIPT_DIR/config/application.yml" "$STAGE_DIR/config/"
cp "$SCRIPT_DIR/README.md" "$STAGE_DIR/"
[ -f "$SCRIPT_DIR/README.en.md" ] && cp "$SCRIPT_DIR/README.en.md" "$STAGE_DIR/"
cp "$SERVER_DIR/src/main/resources/db/migration/"*.sql "$STAGE_DIR/database/" 2>/dev/null || true
echo "$VERSION" > "$STAGE_DIR/VERSION"

# --- assemble_package ---
# $1 = platform name (linux, macos, windows)
# $2 = archive extension (tar.gz, zip)
assemble_package() {
    local platform="$1"; local ext="$2"
    local archive="astrsomn-${platform}.${ext}"

    # Clean + copy platform scripts
    rm -rf "$STAGE_DIR/bin"
    mkdir -p "$STAGE_DIR/bin"
        cp "$SCRIPT_DIR/bin/.env" "$STAGE_DIR/bin/"
    if [ "$platform" = "windows" ]; then
        cp "$SCRIPT_DIR/bin/start.bat" "$STAGE_DIR/bin/"
        cp "$SCRIPT_DIR/bin/stop.bat"  "$STAGE_DIR/bin/"
    else
        cp "$SCRIPT_DIR/bin/start.sh" "$STAGE_DIR/bin/"
        cp "$SCRIPT_DIR/bin/stop.sh"  "$STAGE_DIR/bin/"
        chmod +x "$STAGE_DIR/bin/start.sh" "$STAGE_DIR/bin/stop.sh"
    fi

    cd "$OUTPUT_DIR"
    rm -f "$archive"

    if [ "$ext" = "zip" ]; then
        if command -v zip >/dev/null 2>&1; then
            zip -qr "$archive" "astrsomn"
        else
            jar cf "$archive" -C "$OUTPUT_DIR" "astrsomn"
        fi
    else
        tar -czf "$archive" "astrsomn"
    fi

    local size; size=$(du -h "$archive" 2>/dev/null | cut -f1 || echo "?")
    echo "  $archive  ($size)"
}

assemble_package "linux"   "tar.gz"
assemble_package "macos"   "tar.gz"
assemble_package "windows" "zip"

rm -rf "$STAGE_DIR"

echo ""
echo "============================================"
echo " Build completed successfully!"
echo "============================================"
echo " Output: $OUTPUT_DIR"
ls -lh "$OUTPUT_DIR"/*.tar.gz "$OUTPUT_DIR"/*.zip 2>/dev/null || true
