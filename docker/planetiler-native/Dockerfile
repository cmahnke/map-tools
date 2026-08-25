ARG PLANETILER_GIT_URL=https://github.com/onthegomap/planetiler.git
ARG PLANETILER_GIT_TAG=main
ARG DOWNLOAD_DATA=""

FROM ghcr.io/graalvm/native-image-community:25i2 AS builder

ARG PLANETILER_GIT_URL
ARG PLANETILER_GIT_TAG

ENV BUILD_DEPS="wget maven git" \
    BUILD_DIR=/tmp/build \
    PLANETILER_DIR=/opt/planetiler \
    PLANETILER_DATA_DIR=/usr/share/planetiler \
    PLANETILER_BIN=/opt/planetiler/planetiler

# OS of base image is Oracle Linux
RUN --mount=target=/mnt/build-context \
    microdnf install $BUILD_DEPS -y && \
    mkdir -p $BUILD_DIR $PLANETILER_DIR && \
    cp /mnt/build-context/docker/planetiler/scripts/* $BUILD_DIR && \
# Configure Maven
    export MAVEN_CONFIG_DIR=`getent passwd root | cut -d: -f6`"/.m2/" && \
    mkdir -p $MAVEN_CONFIG_DIR && \
    cp /mnt/build-context/docker/planetiler-native/config/settings.xml ${MAVEN_CONFIG_DIR}settings.xml && \
# Build Planetiler
    cd $BUILD_DIR && \
    git clone --recurse-submodules $PLANETILER_GIT_URL --branch $PLANETILER_GIT_TAG --single-branch --depth 1

COPY docker/planetiler-native/src/planetiler-native.patch $BUILD_DIR/

RUN cd $BUILD_DIR/planetiler && \
    git apply --whitespace=nowarn ../planetiler-native.patch && \
    mvn -DskipTests=true -Pnative clean package && \
# Install Planetiler
    cp ./planetiler-dist/target/native-image/planetiler $PLANETILER_BIN && \
    rm -rf ./planetiler-dist/target bin data/output* data/*.gz data/tmp

# the native image is dynamically linked against glibc, so a glibc based runtime is required
FROM debian:12-slim

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source=https://github.com/cmahnke/map-tools

ARG DOWNLOAD_DATA

ENV RUN_DEPS="" \
    PLANETILER_DIR=/opt/planetiler \
    PLANETILER_DATA_DIR=/usr/share/planetiler \
    PLANETILER_BIN=/opt/planetiler/planetiler

COPY --from=builder $PLANETILER_BIN $PLANETILER_BIN
COPY --chmod=755 docker/planetiler/scripts/get-data.sh /usr/local/bin/get-data

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends zlib1g $RUN_DEPS && \
    ln -s $PLANETILER_BIN /usr/local/bin/planetiler && \
# Cleanup
    rm -rf /var/lib/apt/lists/* /var/cache/apt/* /root/.cache
