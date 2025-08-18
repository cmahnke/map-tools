FROM ghcr.io/cmahnke/map-tools/osmium:latest AS osmium
FROM ghcr.io/cmahnke/map-tools/tilemaker:latest AS tilemaker
FROM ghcr.io/cmahnke/map-tools/osmctools:latest AS osmctools

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV BUILD_DEPS="git wget cmake make automake autoconf libtool clang-dev g++ zlib-dev sqlite-dev bzip2-dev expat-dev lz4-dev rapidjson-dev boost-dev lua5.1-dev gettext-dev protoc protobuf-dev libffi-dev postgresql-dev bzip2-dev proj-dev potrace-dev cimg" \
    RUN_DEPS="zlib sqlite bzip2 expat lz4-libs rapidjson boost1.84-program_options boost1.84-filesystem boost1.84-iostreams protobuf gdal gdal-tools lua5.1 gettext bash libcrypto3 proj postgresql-libs potrace" \
    TIPPECANOE_GIT_REPO="https://github.com/mapbox/tippecanoe.git" \
    TIPPECANOE_GIT_TAG="1.36.0" \
    NLOHMANN_JSON_GIT_REPO="https://github.com/nlohmann/json.git" \
    NLOHMANN_JSON_GIT_TAG="v3.12.0" \
    OSM2PGSQL_GIT_REPO="https://github.com/openstreetmap/osm2pgsql.git" \
    OSM2PGSQL_GIT_TAG="2.1.1" \
    BUILD_DIR=/tmp/build

COPY --from=osmium /opt/osmium /opt/osmium
COPY --from=tilemaker /opt/tilemaker /opt/tilemaker
COPY --from=osmctools /opt/osmctools /opt/osmctools

#RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories && \
RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories && \
    apk --update upgrade &&\
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
    export CC=clang CXX=clang++ && \
# Needed for newer Lua versions
#    ln -s /usr/bin/lua5.4 /usr/bin/lua && \
    cd $BUILD_DIR && \
# Tippecanoe
    git clone --depth 1 --branch $TIPPECANOE_GIT_TAG $TIPPECANOE_GIT_REPO && \
    cd tippecanoe && \
    PREFIX=/opt/tippecanoe make -j install && \
    cd .. && \
# nlohmann/json
    git clone --depth 1 --branch $NLOHMANN_JSON_GIT_TAG $NLOHMANN_JSON_GIT_REPO && \
    cd json && \
    mkdir build && cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=/opt/osm2pgsql -DBUILD_TESTING=OFF -DJSON_BuildTests=OFF .. && \
    make install && \
    export PKG_CONFIG_PATH=$PKG_CONFIG_PATH:/opt/osm2pgsql/share/pkgconfig && \
    cd ../.. && \
# osm2pgsql
    git clone --depth 1 --branch $OSM2PGSQL_GIT_TAG $OSM2PGSQL_GIT_REPO && \
    cd osm2pgsql && \
    mkdir build && cd build && \
    cmake -DNLOHMANN_INCLUDE_DIR=/opt/osm2pgsql/include -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTS=OFF -DCMAKE_INSTALL_PREFIX:PATH=/opt/osm2pgsql .. && \
    make install && \
    cd / && \
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
