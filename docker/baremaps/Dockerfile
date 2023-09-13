# syntax=docker/dockerfile:experimental

FROM maven:3-eclipse-temurin-17-focal AS builder

ENV BUILD_DEPS="git maven gcompat libc6-compat" \
    BAREMAPS_REPO=https://github.com/apache/incubator-baremaps.git \
    BAREMAPS_TAG="v0.7.1" \
    BUILD_DIR=/tmp/build

RUN mkdir -p $BUILD_DIR && \
    cd $BUILD_DIR && \
    git clone $BAREMAPS_REPO --branch $BAREMAPS_TAG --single-branch baremaps && \
    cd baremaps && \
    mvn package -Dmaven.test.skip=true

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source=https://github.com/cmahnke/map-action

ENV RUN_DEPS="openjdk17-jre openjdk17-jre-headless" \
    BUILD_DIR=/tmp/build \
    BAREMAPS_DIR=/opt/baremaps

COPY --from=builder $BUILD_DIR/baremaps/baremaps-cli/target/baremaps-0.7.1-incubating-bin.tar.gz $BAREMAPS_DIR/..

RUN apk --update upgrade && \
    apk add --no-cache $RUN_DEPS && \
    cd $BAREMAPS_DIR/.. && \
    tar xzf *-bin.tar.gz && \
    rm -rf *-bin.tar.gz && \
    ln -s baremaps-* baremaps && \
    export PATH=$PATH:$BAREMAPS/bin
