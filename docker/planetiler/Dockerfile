# syntax=docker/dockerfile:experimental

ARG PLANETILER_TAG=v0.8.0
ARG DOWNLOAD_DATA=""

FROM maven:3-eclipse-temurin-22 AS builder

ARG PLANETILER_TAG
ARG DOWNLOAD_DATA

ENV BUILD_DEPS="git wget" \
    BUILD_DIR=/tmp/build \
    PLANETILER_GIT_URL=https://github.com/onthegomap/planetiler.git \
    PLANETILER_DIR=/opt/planetiler \
    PLANETILER_DATA_DIR=/usr/share/planetiler

RUN --mount=target=/mnt/build-context \
    mkdir -p $BUILD_DIR $PLANETILER_DIR $PLANETILER_DATA_DIR && \
    cp /mnt/build-context/docker/planetiler/scripts/* $BUILD_DIR && \
# Install Planetiler
    cd $BUILD_DIR && \
    git clone --recurse-submodules $PLANETILER_GIT_URL --branch $PLANETILER_TAG --single-branch && \
    cd planetiler && \
    mvn -Dmaven.javadoc.skip=true -DskipTests=true clean install  && \
    mvn -Dmaven.javadoc.skip=true -Dmaven.source.skip -DskipTests=true package && \
    mv planetiler-dist/target/*with-deps.jar $PLANETILER_DIR && \
# Download data
    if test -n "$DOWNLOAD_DATA" ; then \
      cd $BUILD_DIR && \
      ./get-data.sh ; \
    fi

FROM alpine:3.20

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ARG DOWNLOAD_DATA

ENV RUN_DEPS="temurin-22-jre" \
    PLANETILER_DIR=/opt/planetiler \
    PLANETILER_DATA_DIR=/usr/share/planetiler

COPY --from=builder $PLANETILER_DIR/*with-deps.jar $PLANETILER_DIR/
COPY --from=builder $PLANETILER_DATA_DIR $PLANETILER_DATA_DIR

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS && \
    mkdir -p $PLANETILER_DIR/bin && \
    printf "#!/bin/sh\njava -Xmx2g -jar /opt/planetiler/planetiler*-with-deps.jar" > /opt/planetiler/bin/planetiler && \
    chmod +x /opt/planetiler/bin/planetiler && \
    export PATH=$PATH:$PLANETILER_DIR/bin && \
    if test -z "$DOWNLOAD_DATA" ; then \
      echo "Removing existing data directory" && \
      rm -rf $PLANETILER_DATA_DIR ; \
    else \
      echo "Relinking data directory" && \
      ln -s $PLANETILER_DATA_DIR/data $PLANETILER_DIR/data ; \
    fi && \
# Cleanup
    rm -rf /var/cache/apk/* /root/.cache
