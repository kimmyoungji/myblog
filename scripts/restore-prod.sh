#!/bin/bash

# -E: 함수/서브셸에도 ERR trap 적용, -e: 명령 실패 시 즉시 종료
# -u: 정의 안 된 변수 사용 시 종료, pipefail: 파이프 중 하나라도 실패하면 실패 처리
set -Eeuo pipefail

HOME_DIR="/home/ubuntu/myblog"

# ${1:-}: 인자가 없으면 빈 문자열 (set -u로 인한 종료 방지)
DB_BACKUP="${1:-}"
UPLOAD_BACKUP="${2:-}"

# -z: 문자열이 비어 있으면 참
if [ -z "$DB_BACKUP" ] || [ -z "$UPLOAD_BACKUP" ]; then
  echo "Usage: $0 database.sql.gz upload.tar.gz"
  exit 1
fi

# 백업 파일 확인 (-f: 일반 파일로 존재하면 참)
[ -f "$DB_BACKUP" ] || {
  echo "DB backup not found: $DB_BACKUP"
  exit 1
}

[ -f "$UPLOAD_BACKUP" ] || {
  echo "Upload backup not found: $UPLOAD_BACKUP"
  exit 1
}

# cd 후에도 찾을 수 있도록 상대경로를 절대경로로 바꾼다
DB_BACKUP="$(realpath "$DB_BACKUP")"
UPLOAD_BACKUP="$(realpath "$UPLOAD_BACKUP")"

cd "$HOME_DIR"

# 압축 파일 검증
gzip -t "$DB_BACKUP"                   # -t: 압축 해제 없이 손상 여부만 검사
tar -tzf "$UPLOAD_BACKUP" > /dev/null  # -t: 목록 출력, -z: gzip, -f: 대상 파일

# 실패 시 app 재시작 (ERR: 명령이 실패할 때 실행)
trap '
  echo "Restore failed. Restarting app..."
  docker compose start app
' ERR

echo "Stopping app..."
docker compose stop app


# ========================================
# DB 복원
# ========================================

echo "Restoring database..."

# gzip -dc: 압축을 풀어 표준출력으로 내보냄 (원본 파일 유지)
# exec -T: TTY 없이 실행해 파이프 입력을 컨테이너로 전달
# 작은따옴표 안의 $MYSQL_* 변수는 컨테이너 안의 환경변수로 해석됨
gzip -dc "$DB_BACKUP" |
docker compose exec -T mysqldb sh -c '
  mysql \
    -u"$MYSQL_USER" \
    -p"$MYSQL_PASSWORD" \
    "$MYSQL_DATABASE"
'


# ========================================
# upload 복원
# ========================================

echo "Restoring upload files..."

rm -rf "$HOME_DIR/upload"

# -x: 압축 해제, -C: 해제할 디렉터리 지정 (백업 안의 upload/ 폴더가 그대로 복원됨)
tar -xzf "$UPLOAD_BACKUP" \
  -C "$HOME_DIR"


# ========================================
# app 재시작
# ========================================

docker compose start app

echo "Restore completed."