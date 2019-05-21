FROM openjdk:11-jdk-alpine
VOLUME /tmp

ADD target/gpconnect-oauth2-smart.jar gpconnect-oauth2-smart.jar

# ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gpconnect-oauth2-smart.jar"]

