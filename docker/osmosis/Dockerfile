# syntax=docker/dockerfile:experimental

FROM maven:3-eclipse-temurin-24 AS builder

ARG OSMOSIS_TAG=0.49.2

ENV BUILD_DEPS="git wget unzip" \
    BUILD_DIR=/tmp/build \
    OSMOSIS_GIT_URL=https://github.com/openstreetmap/osmosis.git \
    OSMOSIS_DIR=/opt/osmosis \
    GRADLE_VERSION=8.14.3 \
    GRADLE_HOME=/opt/gradle

RUN mkdir -p $BUILD_DIR $OSMOSIS_DIR && \
    apt-get update && apt-get upgrade -y && \
    apt-get install -y $BUILD_DEPS && \
    wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp && \
    unzip -d /opt /tmp/gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle && \
# Install Osmosis
    cd $BUILD_DIR && \
    git clone $OSMOSIS_GIT_URL --branch $OSMOSIS_TAG --single-branch osmosis && \
    cd osmosis && \
    /opt/gradle/bin/gradle build -x test && \
    cp osmosis/build/distributions/osmosis-*.zip /opt/osmosis

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV RUN_DEPS="temurin-23-jre" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data \
    OSMOSIS_DIR=/opt/osmosis \
    MAP_FILE_NAME=map.osm

COPY --from=builder $OSMOSIS_DIR/osmosis-*.zip $OSMOSIS_DIR/

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    mkdir -p /etc/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $BUILD_DEPS $RUN_DEPS && \
    mkdir -p $DATA_DIR $OSMOSIS_DIR && \
    cd $OSMOSIS_DIR && \
    unzip osmosis-*.zip && \
    mv osmosis-*/* . && \
    rm -r osmosis-*/ *.zip && \
# Cleanup
    cd / && rm -rf $BUILD_DIR && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
