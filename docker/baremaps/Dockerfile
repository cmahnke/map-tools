# syntax=docker/dockerfile:experimental

FROM maven:3-eclipse-temurin-22 AS builder

ENV BAREMAPS_REPO=https://github.com/apache/incubator-baremaps.git \
    BAREMAPS_TAG="main" \
    BUILD_DIR=/tmp/build

RUN mkdir -p $BUILD_DIR && \
    cd $BUILD_DIR && \
    git clone $BAREMAPS_REPO --branch $BAREMAPS_TAG --single-branch baremaps && \
    cd baremaps && \
    mvn package -Dmaven.test.skip=true

FROM alpine:3.19

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV RUN_DEPS="temurin-22-jre" \
    BUILD_DIR=/tmp/build \
    BAREMAPS_DIR=/opt/baremaps

COPY --from=builder $BUILD_DIR/baremaps/baremaps-cli/target/*-bin.tar.gz $BAREMAPS_DIR/..

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS && \
    cd $BAREMAPS_DIR/.. && \
    tar xzf *-bin.tar.gz && \
    rm -rf *.tar.gz && \
    ln -s *baremaps-*-bin baremaps && \
    export PATH=$PATH:$BAREMAPS/bin && \
# Cleanup
    rm -rf /var/cache/apk/* /root/.cache
