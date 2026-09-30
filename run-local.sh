#!/bin/zsh
set -e
cd "$(dirname "$0")"

# CATALINA_HOME이 비어 있으면 rm -rf/cp가 /webapps 같은 엉뚱한 경로를 대상으로 하므로 즉시 중단
if [[ -z "$CATALINA_HOME" || ! -x "$CATALINA_HOME/bin/catalina.sh" ]]; then
  echo "CATALINA_HOME이 설정되지 않았거나 Tomcat 경로가 아닙니다: '$CATALINA_HOME'" >&2
  echo "예) export CATALINA_HOME=/Users/myoungjikim/dev/kbc_std/servers/apache-tomcat-9.0.109" >&2
  exit 1
fi

mvn -q package -DskipTests
rm -rf "$CATALINA_HOME/webapps/ROOT" "$CATALINA_HOME/webapps/ROOT.war"
cp target/kmjblog-0.0.1-SNAPSHOT.war "$CATALINA_HOME/webapps/ROOT.war"

# 루트 .env의 MYSQL_PASSWORD를 JDBC_PASSWORD로 사용
set -a; source ../.env; set +a
export JDBC_PASSWORD="$MYSQL_PASSWORD"

CATALINA_OPTS="-Dspring.profiles.active=local" JPDA_ADDRESS=localhost:8000 \
  exec "$CATALINA_HOME/bin/catalina.sh" jpda run