#!/bin/bash

set -euo pipefail

HOME_DIR="/home/ubuntu/myblog"
NOW="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="$HOME_DIR/backups/$NOW"

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
gzip -t "$BACKUP_DIR/database.sql.gz" # 파일이 깨지지 않았는지 검사
gzip -dc "$BACKUP_DIR/database.sql.gz" | grep -q "CREATE TABLE" # 쿼리문 있는지 검증
tar -tzf "$BACKUP_DIR/upload.tar.gz" > /dev/null # tar.gz 내부 목록을 읽을 수 있는지 검사

echo "Backup completed and verified: $BACKUP_DIR"