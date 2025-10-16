# syntax=docker/dockerfile:experimental

ARG OSM2WORLD_TAG=master

FROM maven:3-eclipse-temurin-22 AS builder

ARG OSM2WORLD_TAG
ARG DOWNLOAD_DATA

ENV BUILD_DEPS="git wget" \
    BUILD_DIR=/tmp/build \
    OSM2WORLD_GIT_URL=https://github.com/tordanik/OSM2World \
    OSM2WORLD_DIR=/opt/osm2world \
    OSM2WORLD_DATA_DIR=/usr/share/osm2world

RUN --mount=target=/mnt/build-context \
    mkdir -p $BUILD_DIR $OSM2WORLD_DIR $OSM2WORLD_DATA_DIR && \
# Install OSW2World
    cd $BUILD_DIR && \
    git clone --recurse-submodules $OSM2WORLD_GIT_URL --branch $OSM2WORLD_TAG --single-branch && \
    cd OSM2World && \
    mvn -Dmaven.javadoc.skip=true -DskipTests=true clean install  && \
    mvn -Dmaven.javadoc.skip=true -Dmaven.source.skip -DskipTests=true --update-snapshots package && \
    mv desktop/target/osm2world-*.jar $OSM2WORLD_DIR && \
    mv desktop/target/lib $OSM2WORLD_DIR && \
    mv build-adds/osm2world.sh $OSM2WORLD_DIR

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV RUN_DEPS="temurin-23-jre" \
    OSM2WORLD_DIR=/opt/osm2world

COPY --from=builder $OSM2WORLD_DIR/* $OSM2WORLD_DIR/

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    sed -i -e 's/http:/https:/' /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS && \
    mkdir -p $OSM2WORLD_DIR && \
    #printf "#!/bin/sh\njava -Xmx2g -jar /opt/osm2world/osm2world*-with-deps.jar" > /opt/osm2world/bin/osm2world && \
    chmod +x /opt/osm2world/osm2world.sh && \
    export PATH=$PATH:$OSM2WORLD_DIR && \
# Cleanup
    rm -rf /var/cache/apk/* /root/.cache
