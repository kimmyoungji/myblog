# AI 최대한 안쓰고 만든 김명지 포트폴리오/블로그

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

## 더 쉽게 실행하는 법
```zsh
#tomcat 실행
./run-local.sh

#tomcat 종료
$CATALINA_HOME/bin/shutdown.sh
```

## vscode에서 디버깅 거는 방법
1. extention pack for java 설치 (https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
2. 왼쪽 메뉴 > 실행 및 디버그 > Attach to Tomcat 클릭
3. 원하는 코드 위치에 디버깅 위치 표시 (코드행 왼쪽 클릭 시 빨간 점이 나타남)

## 도커로 실행하는 방법
```zsh
#컨테이너 띄우기
docker compose up -d app

#컨테이너 내리기
docker compose up -d app

#오류로그보기
docker compose logs app | grep -nE "SEVERE|Caused by|Exception:" | tail -20

```

### 로컬 이미지 데이터를 AWS Lightsali 에 업로드하기

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