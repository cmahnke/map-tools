# syntax=docker/dockerfile:experimental

FROM maven:3-eclipse-temurin-22 AS builder

ARG OSMOSIS_TAG=0.49.2

ENV BUILD_DEPS="git wget unzip" \
    BUILD_DIR=/tmp/build \
    SPLITMAP_GIT_URL=https://github.com/simonpoole/mapsplit.git \
    SPLITMAP_TAG=0.4.0 \
    SPLITMAP_DIR=/opt/splitmap \
    GRADLE_VERSION=8.8 \
    GRADLE_HOME=/opt/gradle

RUN mkdir -p $BUILD_DIR $OSMOSIS_DIR && \
    apt-get update && apt-get upgrade -y && \
    apt-get install -y $BUILD_DEPS && \
    wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp && \
    unzip -d /opt /tmp/gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle && \
# Install Mapsplit
    cd $BUILD_DIR && \
    git clone $SPLITMAP_GIT_URL --branch $SPLITMAP_TAG --single-branch && \
    cd mapsplit && \
    rm build.gradle && \
    wget https://raw.githubusercontent.com/simonpoole/mapsplit/ba73a7cb128f76fce8b974ef38ad1e6c54debc53/build.gradle && \
    sed -i 's/configurations.implementation.collect/configurations.runtimeClasspath.collect/g' build.gradle && \
    sed -i '36 i duplicatesStrategy = DuplicatesStrategy.EXCLUDE' build.gradle && \
    /opt/gradle/bin/gradle build -x test && \
    /opt/gradle/bin/gradle fatJar && \
    mkdir -p $SPLITMAP_DIR && \
    mv build/libs/mapsplit-all-*.jar $SPLITMAP_DIR

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV RUN_DEPS="temurin-22-jre" \
    SPLITMAP_GIT_URL=https://github.com/simonpoole/mapsplit.git \
    SPLITMAP_TAG=0.4.0 \
    SPLITMAP_DIR=/opt/splitmap

COPY --from=builder $SPLITMAP_DIR/*.jar $SPLITMAP_DIR/

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    touch /etc/apk/repositories && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $SPLITMAP_DIR && \
# Install Splitmap
#    cd $BUILD_DIR && \
#    git clone $SPLITMAP_GIT_URL --branch $SPLITMAP_TAG --single-branch && \
#    cd mapsplit && \
#    rm build.gradle && \
#    wget https://raw.githubusercontent.com/simonpoole/mapsplit/ba73a7cb128f76fce8b974ef38ad1e6c54debc53/build.gradle && \
#    sed -i 's/configurations.implementation.collect/configurations.runtimeClasspath.collect/g' build.gradle && \
#    sed -i '36 i duplicatesStrategy = DuplicatesStrategy.EXCLUDE' build.gradle && \
#    gradle build -x test && \
#    gradle fatJar && \
#    mkdir -p $SPLITMAP_DIR && \
#    mv build/libs/mapsplit-all-*.jar $SPLITMAP_DIR && \
## Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
