# syntax=docker/dockerfile:experimental

# Use v3.12, since higher versions include Gradle > 6.0 which doesn't ship the jdepended plugin
FROM alpine:3.12

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

ARG OSMOSIS_TAG=0.48.3

ENV BUILD_DEPS="git gradle curl libxml2-utils" \
    RUN_DEPS="openjdk8-jre" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data \
    OSMOSIS_GIT_URL=https://github.com/openstreetmap/osmosis.git \
    MAP_FILE_NAME=map.osm

RUN echo "http://dl-cdn.alpinelinux.org/alpine/v$(cat /etc/alpine-release | cut -d'.' -f1,2)/community" >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
# Install Osmosis
    cd $BUILD_DIR && \
    git clone $OSMOSIS_GIT_URL --branch $OSMOSIS_TAG --single-branch && \
    cd osmosis/package && \
    gradle syncLibs && \
#    rm -rf build/distibution && \
    cd .. && mkdir -p /opt/osmosis && \
    mv package/* /opt/osmosis && \
# Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
