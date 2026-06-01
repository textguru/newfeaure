#!/usr/bin/env bash
# 폐쇄망 배포용 zip 묶음 생성 스크립트.
# 사용법: bash tools/make_bundle.sh
# 결과: dist/절차서포털.zip  (압축 해제 후 index.html 더블클릭)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/procedure-site"
DIST="$ROOT/dist"
NAME="절차서포털"

mkdir -p "$DIST"
rm -f "$DIST/$NAME.zip"

cd "$ROOT"
# procedure-site 폴더째로 묶어 풀었을 때 폴더가 생기도록 함
zip -r -q "$DIST/$NAME.zip" "procedure-site" \
  -x "*/.DS_Store" -x "*/Thumbs.db"

echo "생성 완료: dist/$NAME.zip"
unzip -l "$DIST/$NAME.zip" | tail -n +2 | head -n 30
