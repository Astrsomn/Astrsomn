#!/usr/bin/env bash
# ============================================================================
# Astrsomn Server — Startup
# ============================================================================
# Usage:  edit config/application.yml then run bin/start.sh
# ============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load env config
[ -f "$SCRIPT_DIR/env" ] && source "$SCRIPT_DIR/env"

# Resolve Java command
if [ -n "${JAVA_HOME:-}" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="${JAVA_CMD:-java}"
fi

JAR_FILE="$APP_DIR/astrsomn-server.jar"
PID_FILE="$APP_DIR/astrsomn-server.pid"
LOG_DIR="$APP_DIR/log"
LOG_FILE="$LOG_DIR/astrsomn-server.log"
CONFIG_FILE="$APP_DIR/config/application.yml"
VERSION_FILE="$APP_DIR/VERSION"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx1024m -Dfile.encoding=UTF-8}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

# ── Banner ───────────────────────────────────────────────────────
banner() {
    echo -e "${CYAN}"
    echo "    ╔══════════════════════════════════════════╗"
    echo "    ║         Astrsomn AI Gateway             ║"
    if [ -f "$VERSION_FILE" ]; then
        printf "    ║         %-32s ║\n" "Version: $(cat "$VERSION_FILE")"
    fi
    echo -e "    ╚══════════════════════════════════════════╝${NC}"
    echo -e "    ${BOLD}Config:${NC} config/application.yml"
    echo -e "    ${BOLD}Log:${NC}    log/astrsomn-server.log"
    echo ""
}

# ── Check Java 17+ ──────────────────────────────────────────────
check_java() {
    if ! command -v "$JAVA_CMD" >/dev/null 2>&1 && ! test -x "$JAVA_CMD" 2>/dev/null; then
        echo -e "${RED}  ✗ Java not found: $JAVA_CMD${NC}"
        echo -e "${RED}    Install JDK 17+ or set JAVA_CMD.${NC}"
        echo -e "${RED}    https://adoptium.net/${NC}"
        exit 1
    fi
    local v
    v=$("$JAVA_CMD" -version 2>&1 | grep -oP '(?<=version ")\d+' | head -1)
    [ -z "$v" ] && v=$("$JAVA_CMD" -version 2>&1 | grep -oP '(?<=version "1\.)\d+' | head -1)
    if [ -z "$v" ] || [ "$v" -lt 17 ]; then
        echo -e "${RED}  ✗ Java ${v:-?} too old. JDK 17+ required. https://adoptium.net/${NC}"
        exit 1
    fi
}

# ── Check not already running ───────────────────────────────────
check_running() {
    if [ -f "$PID_FILE" ]; then
        local pid; pid=$(cat "$PID_FILE")
        if kill -0 "$pid" 2>/dev/null; then
            echo -e "${YELLOW}  ! Already running (PID: ${pid}). Use bin/stop.sh first.${NC}"
            exit 1
        fi
        rm -f "$PID_FILE"
    fi
}

# ── Show live log for $1 seconds, return 0 if process still alive
watch_startup() {
    local pid="$1" watch_secs="${2:-10}"
    local elapsed=0

    echo -e "  ${CYAN}┌─ Startup log (${watch_secs}s) ─ Ctrl+C stops watching, server keeps running ─┐${NC}"

    tail -n 5 -f "$LOG_FILE" 2>/dev/null &
    local tp=$!

    while [ "$elapsed" -lt "$watch_secs" ]; do
        sleep 1
        elapsed=$((elapsed + 1))
        if ! kill -0 "$pid" 2>/dev/null; then
            sleep 0.5
            kill "$tp" 2>/dev/null || true
            wait "$tp" 2>/dev/null || true
            echo -e "  ${CYAN}└──────────────────────────────────────────────┘${NC}"
            return 1
        fi
    done

    kill "$tp" 2>/dev/null || true
    wait "$tp" 2>/dev/null || true
    echo -e "  ${CYAN}└──────────────────────────────────────────────┘${NC}"
    return 0
}

# ============================================================================
# Start server
# ============================================================================
start_server() {
    check_java
    check_running

    if [ ! -f "$JAR_FILE" ]; then
        echo -e "${RED}  ✗ JAR not found: ${JAR_FILE}${NC}"
        exit 1
    fi

    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}  ✗ Config not found: ${CONFIG_FILE}${NC}"
        echo -e "${RED}    Please create config/application.yml first.${NC}"
        echo -e "${RED}    An example config is included in the package.${NC}"
        exit 1
    fi

    mkdir -p "$LOG_DIR"
    : > "$LOG_FILE"

    echo -e "${GREEN}  Starting...${NC}"
    echo "    JAR:  $JAR_FILE"
    echo "    Log:  $LOG_FILE"
    echo ""

    cd "$APP_DIR"
    nohup "$JAVA_CMD" $JAVA_OPTS -jar "$JAR_FILE" >> "$LOG_FILE" 2>&1 &
    local pid=$!
    echo "$pid" > "$PID_FILE"

    if watch_startup "$pid" 10; then
        local port; port=$(grep -oP 'port:\s*\K\d+' "$CONFIG_FILE" 2>/dev/null || echo "4481")
        echo ""
        echo -e "  ${GREEN}╔════════════════════════════════════════════╗${NC}"
        echo -e "  ${GREEN}║   Server started successfully!            ║${NC}"
        echo -e "  ${GREEN}╠════════════════════════════════════════════╣${NC}"
        printf "  ${GREEN}║${NC}   PID:    %-32s${GREEN}║${NC}\n" "$pid"
        printf "  ${GREEN}║${NC}   Access: %-32s${GREEN}║${NC}\n" "http://localhost:${port}"
        printf "  ${GREEN}║${NC}   Log:    %-32s${GREEN}║${NC}\n" "$LOG_FILE"
        echo -e "  ${GREEN}╚════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "  View logs:  ${BOLD}bin/start.sh --logs${NC}"
        echo -e "  Stop:       ${BOLD}bin/stop.sh${NC}"
    else
        echo ""
        echo -e "${RED}╔════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║   Server exited during startup!           ║${NC}"
        echo -e "${RED}╠════════════════════════════════════════════╣${NC}"
        echo -e "${RED}║${NC}  Full log: $LOG_FILE"
        echo -e "${RED}╚════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "  ${BOLD}Troubleshooting:${NC}"
        echo "  1. Check database connection in config/application.yml"
        echo "  2. Is port already in use?"
        echo "  3. Run: bin/start.sh --logs"
        rm -f "$PID_FILE"
        exit 1
    fi
}

# ============================================================================
# Show logs
# ============================================================================
show_logs() {
    if [ -f "$LOG_FILE" ]; then
        echo -e "${CYAN}  Full log: $LOG_FILE${NC}"
        echo "  ─────────────────────────────────────────────"
        tail -200 "$LOG_FILE"
    else
        echo -e "${YELLOW}  No log file yet. Start the server first.${NC}"
    fi
}

# ============================================================================
# Main
# ============================================================================
banner

case "${1:-}" in
    --logs|-l)
        show_logs
        ;;
    --help|-h)
        echo ""
        echo "Usage: bin/start.sh [OPTION]"
        echo ""
        echo "  (no args)    Start server"
        echo "  --logs, -l   View recent server logs"
        echo "  --help, -h   This help"
        echo ""
        echo "Before first run, edit config/application.yml to set your"
        echo "database credentials and JWT secret, then run:"
        echo ""
        echo "  bin/start.sh"
        echo ""
        ;;
    *)
        start_server
        ;;
esac
