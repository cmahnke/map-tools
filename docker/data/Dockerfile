# syntax=docker/dockerfile:experimental

FROM ghcr.io/cmahnke/map-action/osmium:latest AS osmium
FROM ghcr.io/cmahnke/map-action/tilemaker:latest AS tilemaker

FROM alpine:3.18 as builder

COPY --from=osmium /opt/osmium /opt/osmium
COPY --from=tilemaker /opt/tilemaker /opt/tilemaker

ARG COVERAGE=central-europe.json

ENV BUILD_DEPS="python3 py3-pip py3-yaml py3-requests gdal-tools expat gdal libbz2 zlib boost1.82-program_options lz4-libs geos boost1.82-filesystem boost1.82-iostreams protobuf lua5.1-libs" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data

RUN --mount=target=/mnt/build-context \
    apk --update upgrade &&\
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR && \
    cp -r /mnt/build-context/docker/data/* $BUILD_DIR && \
    pip install -r $BUILD_DIR/requirements.txt && \
    python $BUILD_DIR/scripts/mktiles.py -i -v -c $BUILD_DIR/scripts/mktiles.yaml -c $BUILD_DIR/scripts/mktiles-github.yaml -u $BUILD_DIR/coverages/${COVERAGE}

# Restart from an empty image to get rid of layers
FROM alpine:3.18

ENV DATA_DIR=/data

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

COPY --from=builder $DATA_DIR $DATA_DIR
