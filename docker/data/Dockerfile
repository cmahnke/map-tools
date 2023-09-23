# syntax=docker/dockerfile:experimental

FROM ghcr.io/cmahnke/map-action/planetiler:latest-data AS planetiler
FROM ghcr.io/cmahnke/map-action/osmium:latest AS osmium

FROM alpine:3.18 as builder

ARG COVERAGE=central-europe.json
#tile_compression=gzip (the tile compression, one of [none, gzip])
ARG TILE_COMPRESSION=gzip
# Either 'files' otherwise mbtiles
ARG FORMAT=files
# Use this to reduce maximum zoom level
ARG MAX_ZOOM=14
#Exclude layers
ARG EXCLUDE_LAYERS=housenumber,aeroway,aerodrome_label
# Filter buildings
ARG FILTER_BUILDINGS=true

ENV BUILD_DEPS="python3 py3-pip py3-yaml py3-requests openjdk17-jre-headless gdal-tools expat libbz2 zlib boost1.82-program_options lz4-libs geos zip jq" \
    BUILD_DIR=/tmp/build \
    DATA_DIR=/data

COPY --from=planetiler /opt/planetiler /opt/planetiler
COPY --from=planetiler /tmp/build /tmp/build
COPY --from=osmium /opt/osmium /opt/osmium

RUN --mount=target=/mnt/build-context \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR $DATA_DIR $BUILD_DIRdata/output/ && \
    cp -r /mnt/build-context/docker/data/* $BUILD_DIR && \
    pip install -r $BUILD_DIR/requirements.txt && \
    cd $BUILD_DIR/scripts && mkdir -p data/sources/ && \
    ./mktiles.py --print-cmd -v -m -c mktiles-docker.yaml -u ../coverages/${COVERAGE} && \
    ##mv ./coastline ./water-polygons-split-3857 && \
    ##zip -r water-polygons-split-3857.zip water-polygons-split-3857  && \
    ##mv water-polygons-split-3857.zip data/sources/  && \
    ##mv coastline.tmp/water-polygons-split-3857.zip data/sources/ && \
    #rm -rf coastline coastline.tmp water-polygons-split-3857* landcover  && \
    BBOX=`jq -r .bbox ../coverages/${COVERAGE} | tr ' ' ','` && \
    if test "${BBOX}" != '' && test "${BBOX}" != 'null' ; then \
      PLANETILER_OPTS="--bounds=${BBOX}" ; \
      /opt/osmium/bin/osmium extract --bbox="$BBOX" ./tmp/merged.osm.pbf --output=./tmp/merged-bb.osm.pbf && \
      rm -f ./tmp/merged.osm.pbf && \
      echo "[`echo $BBOX |tr ' ' ','`]" > $DATA_DIR/bbox.json && \
      mv ./tmp/merged-bb.osm.pbf ./tmp/merged.osm.pbf ; \
    else \
      PLANETILER_OPTS="" ; \
    fi && \
    if test "$FILTER_BUILDINGS" == 'true' ; then \
      /opt/osmium/bin/osmium tags-filter -i -o data/sources/${COVERAGE}.osm.pbf ./tmp/merged.osm.pbf a/building && \
      rm -f ./tmp/merged.osm.pbf ; \
    else \
      mv ./tmp/merged.osm.pbf data/sources/${COVERAGE}.osm.pbf ; \
    fi && \
    mv $BUILD_DIR/scripts/data/sources/${COVERAGE}.osm.pbf $BUILD_DIR/data/sources/${COVERAGE}.osm.pbf . && \
    rm -rf $BUILD_DIR/scripts && \
    cd $BUILD_DIR/ && \
    #mv $BUILD_DIR/scripts/data . && \
    java -Xmx4g -jar /opt/planetiler/planetiler-*-with-deps.jar --download=false --languages=de,en --use_wikidata=false --osm-path=data/sources/${COVERAGE}.osm.pbf --free_water_polygons_after_read=true --free_natural_earth_after_read=true --free_osm_after_read=true --free_lake_centerlines_after_read=true --gzip_temp=true --tile_compression=${TILE_COMPRESSION} --maxzoom=${MAX_ZOOM} --render_maxzoom=${MAX_ZOOM} --exclude-layers=${EXCLUDE_LAYERS} $PLANETILER_OPTS  && \
    rm -rf /opt && \
    if test "${FORMAT}" = 'files' ; then \
      /usr/bin/mb-util --silent --image_format=pbf $BUILD_DIR/data/output.mbtiles $DATA_DIR/tiles ; \
    else \
      mv data/output.mbtiles $DATA_DIR/tiles.mbtiles ; \
    fi && \
# Cleanup
    cd / && rm -rf $BUILD_DIR /tmp/GeoTools && \
    apk del $BUILD_DEPS

# Restart from an empty image to get rid of layers
#FROM scratch
FROM alpine:3.18

ENV DATA_DIR=/data

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

COPY --from=builder $DATA_DIR $DATA_DIR
