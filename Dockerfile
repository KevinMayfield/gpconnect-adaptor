FROM openjdk:11-slim
VOLUME /tmp

COPY target/gpconnect-oauth2-smart.jar gpconnect-oauth2-smart.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gpconnect-oauth2-smart.jar"]

