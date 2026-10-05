#!/bin/zsh

# -e: 명령 실패 시 즉시 종료, -u: 정의 안 된 변수 사용 시 종료, pipefail: 파이프 중 하나라도 실패하면 실패 처리
set -euo pipefail

# local 환경 변수 설정 (Homebrew MySQL + 프로젝트 루트의 upload 폴더)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
HOME_DIR="$(cd "$SCRIPT_DIR/.." && pwd)" # scripts/의 상위 = 프로젝트 루트
DB_NAME="kmjblog"
DB_USER="kmjblog_admin"

# ${1:-}: 인자가 없으면 빈 문자열 (set -u로 인한 종료 방지)
DB_BACKUP="${1:-}"
UPLOAD_BACKUP="${2:-}"

if [[ -z "$DB_BACKUP" || -z "$UPLOAD_BACKUP" ]]; then
  echo "Usage: $0 database.sql.gz upload.tar.gz" >&2
  exit 1
fi

# 백업 파일 확인 (-f: 일반 파일로 존재하면 참)
[[ -f "$DB_BACKUP" ]] || { echo "DB backup not found: $DB_BACKUP" >&2; exit 1; }
[[ -f "$UPLOAD_BACKUP" ]] || { echo "Upload backup not found: $UPLOAD_BACKUP" >&2; exit 1; }

# cd 후에도 찾을 수 있도록 상대경로를 절대경로로 바꾼다
DB_BACKUP="$(realpath "$DB_BACKUP")"
UPLOAD_BACKUP="$(realpath "$UPLOAD_BACKUP")"

cd "$HOME_DIR"

# 압축 파일 검증
gzip -t "$DB_BACKUP"                   # -t: 압축 해제 없이 손상 여부만 검사
tar -tzf "$UPLOAD_BACKUP" > /dev/null  # -t: 목록 출력, -z: gzip, -f: 대상 파일

# 로컬 Tomcat이 실행 중이면 중단 (run-local.sh는 포그라운드 실행이라 스크립트에서 재시작할 수 없음)
if lsof -nP -iTCP:8080 -sTCP:LISTEN > /dev/null; then
  echo "8080 포트에서 앱이 실행 중입니다. '\$CATALINA_HOME/bin/shutdown.sh'로 종료한 뒤 다시 실행하세요." >&2
  exit 1
fi

# 로컬 DB 비밀번호: LOCAL_DB_PASSWORD가 있으면 우선 사용, 없으면 루트 .env의 MYSQL_PASSWORD
# (~/.mylogin.cnf가 MYSQL_PWD보다 우선하므로 명령줄 인자 -p로 넘긴다. backup-local.sh 참고)
set -a; source ./.env; set +a
DB_PASSWORD="${LOCAL_DB_PASSWORD:-$MYSQL_PASSWORD}"

# 복원 전 현재 상태를 백업 (복원이 잘못되면 이 백업으로 되돌린다)
echo "Backing up current state..."
"$SCRIPT_DIR/backup-local.sh"

# 실패 시 안내 (ERR: 명령이 실패할 때 실행)
trap 'echo "Restore failed. 위의 복원 전 백업(backups/local_*)으로 다시 복원하세요." >&2' ERR


# ========================================
# DB 복원
# ========================================

echo "Restoring database..."

# 덤프에 DROP TABLE IF EXISTS가 들어 있어 기존 테이블은 백업 시점으로 덮어써진다
gzip -dc "$DB_BACKUP" |
mysql \
  -h 127.0.0.1 \
  -u"$DB_USER" \
  -p"$DB_PASSWORD" \
  "$DB_NAME"


# ========================================
# upload 복원
# ========================================

echo "Restoring upload files..."

rm -rf "$HOME_DIR/upload"

# -x: 압축 해제, -C: 해제할 디렉터리 지정 (백업 안의 upload/ 폴더가 그대로 복원됨)
tar -xzf "$UPLOAD_BACKUP" \
  -C "$HOME_DIR"

echo "Restore completed. ./scripts/run-local.sh로 앱을 다시 실행하세요."
