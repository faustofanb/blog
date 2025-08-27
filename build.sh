#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUT_DIR="$ROOT_DIR/out"
mkdir -p "$OUT_DIR"
find "$ROOT_DIR/src/main/java" -name "*.java" > "$OUT_DIR/sources.list"
javac -source 1.8 -target 1.8 -encoding UTF-8 -d "$OUT_DIR" @"$OUT_DIR/sources.list"
echo "Compiled to $OUT_DIR"

