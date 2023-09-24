# syntax=docker/dockerfile:experimental

FROM ghcr.io/cmahnke/map-tools/planetiler:latest-data AS planetiler
FROM ghcr.io/cmahnke/map-tools/osmium:latest AS osmium
FROM ghcr.io/cmahnke/map-tools/osmosis:latest AS osmosis
FROM ghcr.io/cmahnke/map-tools/osmctools:latest AS osmctools

FROM alpine:3.18 as builder

ENV BUILD_DEPS="python3 py3-pip py3-yaml py3-requests openjdk17-jre-headless expat libbz2 zlib boost1.82-program_options lz4-libs geos zip jq" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data

COPY --from=planetiler /opt/planetiler /opt/planetiler
COPY --from=planetiler /tmp/build /tmp/build
COPY --from=osmium /opt/osmium /opt/osmium
COPY --from=osmosis /opt/osmosis /opt/osmosis
COPY --from=osmctools /opt/osmfilter /opt/osmfilter
COPY --from=osmctools /opt/osmconvert /opt/osmconvert

RUN --mount=target=/mnt/build-context \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR $BUILD_DIRdata/output/ && \
    cp -r /mnt/build-context/docker/data/* $BUILD_DIR && \
    pip install -r $BUILD_DIR/requirements.txt && \
    cd $BUILD_DIR/scripts && mkdir -p data/sources/

WORKDIR $BUILD_DIR

FROM alpine:3.18

ENV BUILD_DIR=/tmp/build \
    DATA_DIR=/data \
    PLANETILER_DIR=/opt/planetiler

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

COPY --from=builder / /
