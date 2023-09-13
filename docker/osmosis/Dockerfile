# syntax=docker/dockerfile:experimental

# Use v3.12, since higher versions include Gradle > 6.0 which doesn't ship the jdepended plugin
FROM alpine:3.12

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

#ARG BBOX=20.4200,54.8700,21.3500,55.7500
ARG BBOX=""

ENV BUILD_DEPS="git gradle curl libxml2-utils" \
    RUN_DEPS="openjdk8-jre" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data \
    OSMOSIS_GIT_URL=https://github.com/openstreetmap/osmosis.git \
    OSMOSIS_TAG=0.48.3 \
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
# Cleanup Osmosis build
    cd / && rm -rf $BUILD_DIR/* && \
# Get XML from bounding box
    if [ -n "$BBOX" ] ; then \
        curl -g -o $BUILD_DIR/$MAP_FILE_NAME "https://overpass-api.de/api/map?bbox=$BBOX" && \
# Convert Data
        /opt/osmosis/bin/osmosis --read-xml $BUILD_DIR/$MAP_FILE_NAME --write-pbf $BUILD_DIR/$MAP_FILE_NAME.pbf && \
        rm $BUILD_DIR/$MAP_FILE_NAME && \
        mv $BUILD_DIR/$MAP_FILE_NAME.pbf $DATA_DIR/ && \
        cd / && rm -rf /opt/osmosis ; \
    fi && \
# Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS
