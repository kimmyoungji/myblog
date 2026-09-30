# 1단계: Maven 빌드 (WAR 생성)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q dependency:resolve
COPY src ./src
RUN mvn -q package -DskipTests

# 2단계: Tomcat 런타임에 WAR만 올리기
FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /workspace/target/kmjblog-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080