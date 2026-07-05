FROM debian AS base-img
RUN apt update && apt full-upgrade -y && apt install vim git 7zip curl -y
COPY --from=eclipse-temurin:25 /opt/java/openjdk /root/.jdks/25
COPY --from=eclipse-temurin:21 /opt/java/openjdk /root/.jdks/21
COPY --from=eclipse-temurin:17 /opt/java/openjdk /root/.jdks/17
COPY --from=eclipse-temurin:8 /opt/java/openjdk /root/.jdks/8
ENV JAVA_HOME=/root/.jdks/25
