# syntax=docker/dockerfile:experimental

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

ENV BUILD_DEPS="git wget cmake make automake autoconf libtool clang-dev g++ zlib-dev expat-dev lz4-dev gettext-dev protoc protobuf-dev" \
    RUN_DEPS="zlib protobuf" \
    OSMCTOOLS_GIT_REPO="https://gitlab.com/osm-c-tools/osmctools.git" \
    OSMCTOOLS_GIT_TAG="0.9" \
    OSMFILTER=http://m.m.i24.cc/osmfilter.c \
    BUILD_DIR=/tmp/build

RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories && \
    echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories && \
    apk --update upgrade &&\
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
    export CC=clang CXX=clang++ && \
    cd $BUILD_DIR && \
    wget -O - $OSMFILTER |cc -x c - -O3 -o osmfilter && \
    mkdir -p /opt/osmfilter/bin && \
    mv osmfilter /opt/osmfilter/bin/ && \
## OSM C Tools
    git clone --depth 1 --branch $OSMCTOOLS_GIT_TAG $OSMCTOOLS_GIT_REPO && \
    cd osmctools && \
    autoreconf --install && \
    ./configure --prefix=/opt/osmctools && \
    make && make install && \
    cd .. && \
# Create links
    for filepath in /opt/**/bin/* ; do \
      filename=$(basename $filepath) ; \
      if ! test -e /usr/local/bin/$filename ; then \
        ln -s $filepath /usr/local/bin/$filename ; \
      fi ; \
    done && \
# Cleanup
    rm -rf $BUILD_DIR  && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/* /root/.cache
