# Use the official maven/Java 8 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
WORKDIR /xingcan
COPY xingcan-wx-api/pom.xml ./xingcan-wx-api/pom.xml
COPY xingcan-admin-api/pom.xml ./xingcan-admin-api/pom.xml
COPY xingcan-all/pom.xml ./xingcan-all/pom.xml
COPY xingcan-core/pom.xml ./xingcan-core/pom.xml
COPY xingcan-db/pom.xml ./xingcan-db/pom.xml
COPY pom.xml .

COPY xingcan-wx-api/src ./xingcan-wx-api/src
COPY xingcan-admin-api/src ./xingcan-admin-api/src
COPY xingcan-all/src ./xingcan-all/src
COPY xingcan-core/src ./xingcan-core/src
COPY xingcan-db/src ./xingcan-db/src
COPY src ./src

COPY settings.xml /usr/share/maven/conf/settings.xml
# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=8081","-jar","./xingcan-wx-api/target/xingcan-wx-api-0.0.1.jar"]

