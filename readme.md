# AI 최대한 안쓰고 만든 김명지 포트폴리오/블로그

본 프로젝트는 springframework4.3 / JSP / Mybatis / MySQL DB 를 사용하여 구현한 포트폴리오 전시용 게시판 프로젝트입니다.

---

## 로컬 빌트 및 톰캣 실행 방법
```zsh
#프로젝트빌드
mvn package -DskipTests

#프로젝트 소스를 톰캣으로 복제
cp /Users/myoungjikim/dev/kbc_std/myblog/blog/target/kmjblog-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war

#WAR 압축 해제
cd $CATALINA_HOME/webapps
unzip ROOT.war -d ./ROOT

#tomcat실행준비1-CATALINA_HOME 변수 확인 [필요시에만 수행]
echo $CATALINA_HOME #CATALINA_HOME 설정확인. 없으면 변수 생성
#$CATALINA_HOME 생성하기
export CATALINA_HOME='tomcat 폴더 경로'
vim ~/.zshrc #쉘 설정파일 열어서 시스템 환경변수 추가

#tomcat실행준비2-실행권한활성화 [필요시에만 수행]
chmod +x $CATALINA_HOME/bin/*.sh

#tomcat실행 (DB 비밀번호는 JDBC_PASSWORD 환경변수로 전달)
JDBC_PASSWORD='로컬 DB 비밀번호' \
CATALINA_OPTS="-Dspring.profiles.active=local" \
JPDA_ADDRESS=localhost:8000 \
$CATALINA_HOME/bin/catalina.sh jpda run

#tomcat종료
$CATALINA_HOME/bin/shutdown.sh

#tomcat오류로그확인
grep -n SEVERE logs/catalina.out | tail
```

---

## 더 쉽게 실행하는 법
```zsh
#tomcat 실행 (빌드 → ROOT.war 배포 → .env의 DB 비밀번호로 실행, 디버깅 포트 8000)
./scripts/run-local.sh

#tomcat 종료
$CATALINA_HOME/bin/shutdown.sh
```

---

## vscode에서 디버깅 거는 방법
1. extention pack for java 설치 (https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
2. 왼쪽 메뉴 > 실행 및 디버그 > Attach to Tomcat 클릭
3. 원하는 코드 위치에 디버깅 위치 표시 (코드행 왼쪽 클릭 시 빨간 점이 나타남)

---

## 도커로 실행하는 방법
```zsh
#컨테이너 띄우기
docker compose up -d app

#컨테이너 내리기
docker compose up -d app

#오류로그보기
docker compose logs app | grep -nE "SEVERE|Caused by|Exception:" | tail -20

```

---

## 백업 / 복원
DB(mysqldump)와 `upload/` 폴더를 함께 백업하고, 결과는 `backups/` 아래에 저장된다. (`backups/`는 git 제외)

| 스크립트 | 환경 | 설명 |
|---|---|---|
| `scripts/backup-prod.sh` | Lightsail | app 중지 → 백업 → 검증 → app 재시작. `backups/<날짜_시간>/` |
| `scripts/restore-prod.sh` | Lightsail | app 중지 → DB/upload 복원 → app 재시작 |
| `scripts/backup-local.sh` | 로컬 | Homebrew MySQL + `upload/` 백업. `backups/local_<날짜_시간>/` |
| `scripts/restore-local.sh` | 로컬 | 복원 전 현재 상태를 자동 백업한 뒤 복원. Tomcat을 먼저 종료해야 함 |

```zsh
#로컬 백업 (DB 비밀번호는 .env의 MYSQL_PASSWORD, 다르면 LOCAL_DB_PASSWORD로 지정)
./scripts/backup-local.sh

#로컬 복원 (Tomcat 종료 후)
$CATALINA_HOME/bin/shutdown.sh
./scripts/restore-local.sh backups/<폴더>/database_<시간>.sql.gz backups/<폴더>/upload_<시간>.tar.gz

#운영 백업 / 복원 (Lightsail, /home/ubuntu/myblog에서)
./scripts/backup-prod.sh
./scripts/restore-prod.sh backups/<시간>/database_<시간>.sql.gz backups/<시간>/upload_<시간>.tar.gz
```
- 복원하면 덤프에 있는 테이블은 백업 시점으로 덮어써진다 (`DROP TABLE IF EXISTS` 포함). 덤프에 없는 테이블은 남는다.
- 로컬 `~/.mylogin.cnf`에 root 접속 정보가 있어 `MYSQL_PWD`가 무시되므로, 로컬 스크립트는 `-p`로 비밀번호를 넘긴다. (`Using a password...` 경고는 무시해도 됨)

---

## 로컬 이미지 데이터를 AWS Lightsali 에 업로드하기

먼저 아래 두가지를 전제한다.
- 네트워크 환경이 22번 포트를 통해 데이터를 외부로 전송할 수 있다.
- kmjblog 라는 라벨의 호스트가 ~/.ssh/Config에 등록되어 있다.
- .ssh/Config 내용 예시
  ```bash
    Host kmjblog
    HostName ***.***.***.***
    User ubuntu
    IdentityFile ~/(.pem 파일 경로)
    IdentitiesOnly yes
  ```

그 다음 아래 명령어를 프로젝트 루트(.../blog/*)에서 실행한다.
```
rsync -avz --exclude 'temp/' --exclude '.DS_Store' upload/ kmjblog:~/myblog/upload
```

---

## AWS의 백업 파일을 로컬에 다운로드 해서 적용하기
```bash
cd (프로젝트 루트 경로)
scp -i ~/(.ssh에 위치한 lightsali 연결용 pem 경로) \ 
  -r \
  ubuntu@52.79.229.201:/home/ubuntu/myblog/backups/(백업하고자하는 폴더명) \
  (프로젝트 루트 경로)/backups/
```