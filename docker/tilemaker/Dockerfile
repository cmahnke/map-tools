# syntax=docker/dockerfile:experimental

FROM alpine:3.18

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

ENV BUILD_DEPS="git wget cmake make libtool autoconf automake alpine-sdk zlib-dev sqlite-dev bzip2-dev lz4-dev boost-dev lua5.1-dev gettext-dev protoc protobuf-dev bzip2-dev rapidjson-dev" \
    RUN_DEPS="zlib sqlite-libs bzip2 lz4-libs rapidjson boost1.82-program_options boost1.82-filesystem boost1.82-iostreams protobuf lua5.1 bash rapidjson" \
    SHAPELIB_GIT_REPO="https://github.com/OSGeo/shapelib.git" \
    SHAPELIB_GIT_TAG="v1.5.0" \
    TILEMAKER_GIT_REPO="https://github.com/systemed/tilemaker.git" \
    TILEMAKER_GIT_TAG="v2.4.0" \
    BUILD_DIR=/tmp/build

RUN --mount=target=/mnt/build-context \
    apk --update upgrade && \
    apk add --no-cache $BUILD_DEPS $RUN_DEPS && \
    mkdir -p $BUILD_DIR && \
    cd $BUILD_DIR && \
# ShapeLib
    git clone --depth 1 --branch $SHAPELIB_GIT_TAG $SHAPELIB_GIT_REPO && \
    cd shapelib && \
    ./autogen.sh --prefix=/opt/tilemaker && \
    make && make install && \
    cd .. && \
# Tilemaker
    git clone --depth 1 --branch $TILEMAKER_GIT_TAG $TILEMAKER_GIT_REPO && \
    cd tilemaker && \
    mkdir build && \
    cd build && \
# TODO: Check how to enable -DTILEMAKER_BUILD_STATIC=ON 
    cmake -DLIBSHP_INCLUDE_DIR=/opt/tilemaker/include -DLIBSHP_LIBRARIES=/opt/tilemaker/lib/libshp.a -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=/opt/tilemaker .. && \
    make install && \
    cd / && \

# Create links
    for filepath in /opt/**/bin/* ; do \
        filename=$(basename $filepath) ; \
        ln -s $filepath /usr/local/bin/$filename ; \
    done && \
# Cleanup
    rm -rf /opt/tilemaker/share /opt/tilemaker/include $BUILD_DIR  && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/* /root/.cache
