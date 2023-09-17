# syntax=docker/dockerfile:experimental

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

ENV BUILD_DEPS="git make boost-dev bzip2-dev cmake expat-dev g++ gdal-dev protoc protobuf-dev lz4-dev geos-dev" \
    RUN_DEPS="expat gdal libbz2 zlib boost1.82-program_options lz4-libs geos" \
    PROTOZERO_REPO=https://github.com/mapbox/protozero.git \
    PROTOZERO_TAG="v1.7.1" \
    LIBOSMIUM_REPO=https://github.com/osmcode/libosmium.git \
    LIBOSMIUM_TAG="v2.19.0" \
    OSMIUM_TOOL_REPO=https://github.com/osmcode/osmium-tool \
    OSMIUM_TOOL_TAG="v1.15.0" \
    BUILD_DIR=/tmp/build

RUN --mount=target=/mnt/build-context \
    apk --update upgrade && \
    apk add --no-cache $BUILD_DEPS $RUN_DEPS && \
    mkdir -p $BUILD_DIR && \
    cd $BUILD_DIR && \
# Protozero
    git clone $PROTOZERO_REPO --branch $PROTOZERO_TAG --single-branch && \
    cd protozero && \
    mkdir build && cd build && \
    cmake -DCMAKE_INSTALL_PREFIX:PATH=/opt/osmium -DBUILD_TESTING=OFF .. && \
    make && \
    cd ../.. && \
# LibOsmium
# TODO: Check `proj`: See https://github.com/osmcode/osmium-proj
    cd $BUILD_DIR && \
    git clone $LIBOSMIUM_REPO --branch $LIBOSMIUM_TAG --single-branch && \
    cd libosmium && \
    mkdir build && cd build && \
    cmake -DCMAKE_INSTALL_PREFIX:PATH=/opt/osmium -DCMAKE_BUILD_TYPE=MinSizeRel -DBUILD_EXAMPLES=OFF -DBUILD_TESTING=OFF -DBUILD_DATA_TESTS=OFF .. && \
    make && \
    cd ../.. && \
# Osmium tool
    git clone $OSMIUM_TOOL_REPO --branch $OSMIUM_TOOL_TAG --single-branch && \
    cd osmium-tool && \
    mkdir build && cd build && \
    cmake -DCMAKE_INSTALL_PREFIX:PATH=/opt/osmium -DCMAKE_BUILD_TYPE=MinSizeRel -DBUILD_TESTING=OFF .. && \
    make install && \
    cd / && \
    ln -s /opt/osmium/bin/osmium /usr/local/bin/osmium && \
# Cleanup
    rm -rf $BUILD_DIR && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
