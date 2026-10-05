#!/bin/zsh

set -euo pipefail

# local 환경 변수 설정 (Homebrew MySQL + 프로젝트 루트의 upload 폴더)
HOME_DIR="$(cd "$(dirname "$0")" && pwd)"
NOW="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="$HOME_DIR/backups/local_$NOW"
DB_NAME="kmjblog"
DB_USER="kmjblog_admin"

cd "$HOME_DIR"

# 로컬 DB 비밀번호: LOCAL_DB_PASSWORD가 있으면 우선 사용, 없으면 루트 .env의 MYSQL_PASSWORD (run-local.sh와 동일)
set -a; source ./.env; set +a
export MYSQL_PWD="${LOCAL_DB_PASSWORD:-$MYSQL_PASSWORD}"

# 로컬 MySQL 접속 확인 (mysqladmin ping은 인증 실패에도 성공하므로 실제 쿼리로 확인)
if ! mysql -h 127.0.0.1 -u"$DB_USER" -e "SELECT 1" "$DB_NAME" > /dev/null; then
  echo "로컬 MySQL에 접속할 수 없습니다. MySQL 실행 여부('brew services list')와 비밀번호를 확인하세요." >&2
  echo "예) LOCAL_DB_PASSWORD='로컬 DB 비밀번호' ./backup-local.sh" >&2
  exit 1
fi

mkdir -p "$BACKUP_DIR"

# 중간에 실패하면 불완전한 백업 폴더를 지운다
trap 'echo "Backup failed: $BACKUP_DIR 삭제" >&2; rm -rf "$BACKUP_DIR"' ERR

# DB 백업 (--single-transaction으로 Tomcat 실행 중에도 일관된 스냅샷을 뜬다)
mysqldump \
  -h 127.0.0.1 \
  --single-transaction \
  --quick \
  --no-tablespaces \
  -u"$DB_USER" \
  "$DB_NAME" \
  | gzip > "$BACKUP_DIR/database_$NOW.sql.gz"

# 업로드 파일 백업
tar -czf "$BACKUP_DIR/upload_$NOW.tar.gz" \
  -C "$HOME_DIR" upload

# 백업 검증
gzip -t "$BACKUP_DIR/database_$NOW.sql.gz" # 파일이 깨지지 않았는지 검사
gzip -dc "$BACKUP_DIR/database_$NOW.sql.gz" | grep -q "CREATE TABLE" # 쿼리문 있는지 검증
tar -tzf "$BACKUP_DIR/upload_$NOW.tar.gz" > /dev/null # tar.gz 내부 목록을 읽을 수 있는지 검사

echo "Backup completed and verified: $BACKUP_DIR"
