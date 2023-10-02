# syntax=docker/dockerfile:experimental

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV BUILD_DEPS="git gradle wget sed" \
    RUN_DEPS="openjdk17-jre-headless" \
    BUILD_DIR=/tmp/build \
    SPLITMAP_GIT_URL=https://github.com/simonpoole/mapsplit.git \
    SPLITMAP_TAG=0.4.0

RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk && \
    echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories && \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
# Install Splitmap
    cd $BUILD_DIR && \
    git clone $SPLITMAP_GIT_URL --branch $SPLITMAP_TAG --single-branch && \
    cd mapsplit && \
    rm build.gradle && \
    wget https://raw.githubusercontent.com/simonpoole/mapsplit/ba73a7cb128f76fce8b974ef38ad1e6c54debc53/build.gradle && \
    sed -i 's/configurations.implementation.collect/configurations.runtimeClasspath.collect/g' build.gradle && \
    sed -i '36 i duplicatesStrategy = DuplicatesStrategy.EXCLUDE' build.gradle && \
    gradle build -x test && \
    gradle fatJar && \
    mkdir -p /opt/splitmap && \
    mv build/libs/mapsplit-all-*.jar /opt/splitmap && \
## Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
