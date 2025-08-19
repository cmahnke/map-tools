# syntax=docker/dockerfile:experimental

FROM maven:3-eclipse-temurin-24 AS builder

ARG BASEMAPS_TAG=main

ENV BUILD_DEPS="git wget unzip" \
    BUILD_DIR=/tmp/build \
    BASEMAPS_GIT_URL=https://github.com/protomaps/basemaps.git \
    BASEMAPS_DIR=/opt/basemaps

RUN mkdir -p $BUILD_DIR $BASEMAPS_DIR && \
    apt-get update && apt-get upgrade -y && \
    apt-get install -y $BUILD_DEPS && \
# Install Basemaps
    cd $BUILD_DIR && \
    git clone $BASEMAPS_GIT_URL --branch $BASEMAPS_TAG --single-branch basemaps && \
    cd basemaps/tiles && \
    mvn clean package

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV RUN_DEPS="temurin-24-jre" \
    BASEMAPS_DIR=/opt/basemaps

COPY --from=builder $BASEMAPS_DIR/*.jar $BASEMAPS_DIR

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    touch /etc/apk/repositories && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BASEMAPS_DIR && \
## Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
