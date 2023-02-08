# syntax=docker/dockerfile:experimental

FROM alpine:3.17

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source=https://github.com/cmahnke/map-action

ENV BUILD_DEPS="git wget cmake make automake autoconf libtool clang-dev g++ zlib-dev protozero-dev sqlite-dev bzip2-dev expat-dev lz4-dev rapidjson-dev boost-dev libosmium-dev lua5.1-dev gettext-dev protoc protobuf-dev libffi-dev" \
    RUN_DEPS="zlib protozero sqlite bzip2 expat lz4 rapidjson boost libosmium gdal-tools lua5.1 gettext bash protobuf libffi libcrypto3" \
    OSMIUM_GIT_REPO="https://github.com/osmcode/osmium-tool.git" \
    OSMIUM_GIT_TAG="v1.14.0" \
    TIPPECANOE_GIT_REPO="https://github.com/mapbox/tippecanoe.git" \
    TIPPECANOE_GIT_TAG="1.36.0" \
    OSMCTOOLS_GIT_REPO="https://github.com/ramunasd/osmctools.git" \
    OSMCTOOLS_GIT_TAG="0.4" \
    SHAPELIB_GIT_REPO="https://github.com/OSGeo/shapelib.git" \
    SHAPELIB_GIT_TAG="v1.5.0" \
    TILEMAKER_GIT_REPO="https://github.com/systemed/tilemaker.git" \
    TILEMAKER_GIT_TAG="v2.2.0" \
    BUILD_DIR=/tmp/build

RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories && \
    echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories && \
    apk --update upgrade &&\
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
    export CC=clang CXX=clang++ && \
# Needed for newer Lua versions
#    ln -s /usr/bin/lua5.4 /usr/bin/lua && \
    cd $BUILD_DIR && \
# Osmium Tool
    git clone --depth 1 --branch $OSMIUM_GIT_TAG $OSMIUM_GIT_REPO && \
    cd osmium-tool && \
    mkdir build && \
    cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=/usr .. && \
    make install && \
    cd ../.. && \
# Tippecanoe
    git clone --depth 1 --branch $TIPPECANOE_GIT_TAG $TIPPECANOE_GIT_REPO && \
    cd tippecanoe && \
    PREFIX=/usr make -j install && \
    cd .. && \
## OSM C Tools
    git clone --depth 1 --branch $OSMCTOOLS_GIT_TAG $OSMCTOOLS_GIT_REPO && \
    cd osmctools && \
    autoreconf --install && \
    ./configure --prefix=/usr && \
    make && make install && \
    cd .. && \
# ShapeLib
    git clone --depth 1 --branch $SHAPELIB_GIT_TAG $SHAPELIB_GIT_REPO && \
    cd shapelib && \
    ./autogen.sh --prefix=/usr && \
    make && make install && \
    cd .. && \
# Tilemaker
    git clone --depth 1 --branch $TILEMAKER_GIT_TAG $TILEMAKER_GIT_REPO && \
    cd tilemaker && \
    mkdir build && \
    cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=/usr .. && \
    make install && \
    cd ../.. && \

# Cleanup
    cd / && rm -rf $BUILD_DIR  && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/* /root/.cache
