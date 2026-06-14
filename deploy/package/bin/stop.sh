#!/usr/bin/env bash
set -euo pipefail

# Resolve script directory and app root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

PID_FILE="$APP_DIR/astrsomn-server.pid"
TIMEOUT=15

# ============================================================
# Check if PID file exists
# ============================================================
if [ ! -f "$PID_FILE" ]; then
    echo "No PID file found. Server may not be running."
    exit 0
fi

pid=$(cat "$PID_FILE")

# ============================================================
# Check if process exists
# ============================================================
if ! kill -0 "$pid" 2>/dev/null; then
    echo "Process $pid not found. Removing stale PID file."
    rm -f "$PID_FILE"
    exit 0
fi

# ============================================================
# Graceful shutdown
# ============================================================
echo "Stopping Astrsomn Server (PID: $pid)..."
echo "Waiting up to ${TIMEOUT}s for graceful shutdown..."

kill "$pid"

count=0
while kill -0 "$pid" 2>/dev/null && [ "$count" -lt "$TIMEOUT" ]; do
    sleep 1
    count=$((count + 1))
done

# ============================================================
# Force kill if still running
# ============================================================
if kill -0 "$pid" 2>/dev/null; then
    echo "Graceful shutdown timed out. Force killing..."
    kill -9 "$pid"
    sleep 1
fi

rm -f "$PID_FILE"
echo "Astrsomn Server stopped."
