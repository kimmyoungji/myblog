#!/bin/bash

set -euo pipefail

# prod 환경 변수 설정
HOME_DIR="/home/ubuntu/myblog"
NOW="$(date +%Y%m%d_%H%M%S)"
BACKUP_ROOT="$HOME_DIR/backups"
BACKUP_DIR="$BACKUP_ROOT/$NOW"
KEEP_COUNT=10 # 유지할 백업 폴더 개수

mkdir -p "$BACKUP_DIR"
cd "$HOME_DIR"

# 종료 시 app 재시작
trap 'docker compose start app' EXIT

# app 중지
docker compose stop app

# DB 백업
docker compose exec -T mysqldb sh -c '
  mysqldump \
    --single-transaction \
    --quick \
    --no-tablespaces \
    -u"$MYSQL_USER" \
    -p"$MYSQL_PASSWORD" \
    "$MYSQL_DATABASE"
' | gzip > "$BACKUP_DIR/database_$NOW.sql.gz"

# 업로드 파일 백업
tar -czf "$BACKUP_DIR/upload_$NOW.tar.gz" \
  -C "$HOME_DIR" upload

# 백업 검증
gzip -t "$BACKUP_DIR/database_$NOW.sql.gz" # 파일이 깨지지 않았는지 검사
gzip -dc "$BACKUP_DIR/database_$NOW.sql.gz" | grep "CREATE TABLE" > /dev/null # 쿼리문 있는지 검증 (grep -q는 일찍 종료해 pipefail에서 SIGPIPE 실패가 나므로 끝까지 읽는다)
tar -tzf "$BACKUP_DIR/upload_$NOW.tar.gz" > /dev/null # tar.gz 내부 목록을 읽을 수 있는지 검사

echo "Backup completed and verified: $BACKUP_DIR"

# 오래된 백업 정리 (검증까지 끝난 뒤에만 실행, 최신 KEEP_COUNT개만 유지)
# 백업 폴더명이 날짜_시간(예: 20261005_192225)이므로 그 형식만 대상으로 한다
# ls -1dt: 폴더 자체를 최신순으로 한 줄씩, tail -n +N: N번째 줄부터 출력(= 초과분)
ls -1dt "$BACKUP_ROOT"/[0-9]*_[0-9]* 2>/dev/null \
    | tail -n +$((KEEP_COUNT + 1)) \
    | while IFS= read -r OLD_BACKUP
      do
          echo "오래된 백업 삭제: $OLD_BACKUP"
          rm -rf -- "$OLD_BACKUP"
      done