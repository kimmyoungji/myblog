# AI 최대한 안쓰고 만든 김명지 포트폴리오/블로그

본 프로젝트는 springframework4.3 / JSP / Mybatis / MySQL DB 를 사용하여 구현한 포트폴리오 전시용 게시판 프로젝트입니다.

배포 URL: [https://www.kmjoyit.xyz](https://www.kmjoyit.xyz)

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

./script/restore-local.sh
```

---

## 자동 배포 구현하기
1. 배포 정책 정하기
git push -> test -> Docker image build -> GHCR upload
-> Lightsali SSH 접속 -> 새 이미지 pull -> app container 교체 -> 서비스 정상 확인
2. docker-compose.yml의 app을 build 방식에서 image 방식으로 변경한다.
3. GitHub Container Registry에 이미지를 올린다.
4. GitHub Actions에서 Docker 이미지를 빌드하는 Workflow를 만든다.
5. Lightsali이 GHCR 이미지를 실행할 수 있게 준비한다. 
6. GitHub Actions가 Lightsali에 ssh로 접속할 수 있게 하낟.
7. GitHub Actions에 실제 배포 단계를 추가한다.
8. 배포 성공 여부를 자동 검증한다.
9. 배포 실패 시 rollback을 추가한다.
10. 현재 만들어둔 백업 자동화와 배포 자동화를 연결하되, 역할은 분리한다.

---

## GHCR(Git Hub Container Repository)에 이미지 업로드하기
```bash 
# 1. 이미지 빌드 및 컴퓨터 아키텍쳐 확인 -> lightsali가 amd64아키이므로 linux/amd64에 맞게 빌드
docker buildx build --platform linux/amd64 -t ghcr.io/kimmyoungji/kmjblog:latest .
# 1-2. 이미지 아키텍쳐 확인
docker image inspect ghcr.io/kimmyoungji/kmjblog:latest --format '{{.Os}}/{{.Architecture}}'

# 2. CR_PAT 발급
# Git Hub -> Settings -> Developer Settings -> Personal Access Token -> Tokens(classic) -> generate token with write:package auth scope
# export CR_PAT=(pat token)

# 3. GHCR 로그인
echo $CR_PAT | docker login ghcr.io -u myname --password-stdin

# 4. 업로드
docker docker push ghcr.io/kimmyoungji/kmjblog:latest
```