# syntax=docker/dockerfile:experimental

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ARG OSMOSIS_TAG=0.48.3

ENV BUILD_DEPS="git gradle curl libxml2-utils" \
    RUN_DEPS="openjdk17-jre-headless" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data \
    OSMOSIS_GIT_URL=https://github.com/openstreetmap/osmosis.git \
    MAP_FILE_NAME=map.osm

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    echo "http://dl-cdn.alpinelinux.org/alpine/v$(cat /etc/alpine-release | cut -d'.' -f1,2)/community" >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
# Install Osmosis
    cd $BUILD_DIR && \
    git clone $OSMOSIS_GIT_URL --branch $OSMOSIS_TAG --single-branch && \
    cd osmosis/ && \
    gradle build -x test && \
    mkdir -p /opt/osmosis && \
    cp osmosis/build/distributions/osmosis-*.zip /opt/osmosis && \
    cd /opt/osmosis && \
    unzip osmosis-*.zip && \
    mv osmosis-*/* . && \
    rm -r osmosis-*/ *.zip && \
# Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
