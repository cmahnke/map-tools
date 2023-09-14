# syntax=docker/dockerfile:experimental

FROM ghcr.io/cmahnke/map-action/baremaps:latest AS baremaps

FROM ghcr.io/cmahnke/wenn-man-nicht-alles-selber-macht/postgis:15-3.3-alpine as postgis

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-action

ENV BUILD_DEPS="gpg dirmngr gpg-agent wget jq moreutils openjdk17-jre openjdk17-jre-headless" \
    BAREMAPS_DIR=/opt/baremaps \
    POSTGRES_DB=baremaps \
    POSTGRES_USER=baremaps \
    POSTGRES_PASSWORD=baremaps \
    BUILD_DIR=/tmp/build/ \
    GOSU_VERSION=1.16

ARG URL="https://download.geofabrik.de/europe/liechtenstein-latest.osm.pbf"
ARG TARGETARCH

COPY --from=baremaps $BAREMAPS_DIR $BAREMAPS_DIR

RUN --mount=target=/mnt/build-context \
    apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    wget -O /usr/local/bin/gosu https://github.com/tianon/gosu/releases/download/${GOSU_VERSION}/gosu-${TARGETARCH} && \
	  wget -O /usr/local/bin/gosu.asc https://github.com/tianon/gosu/releases/download/${GOSU_VERSION}/gosu-${TARGETARCH}.asc && \
    export GNUPGHOME="$(mktemp -d)" && \
    gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 && \
    gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu && \
    gpgconf --kill all && \
    chmod +x /usr/local/bin/gosu && \
    rm -rf "$GNUPGHOME" /usr/local/bin/gosu.asc && \
    cp -r /mnt/build-context/docker/database/templates $BUILD_DIR && \
    cp -r /mnt/build-context/docker/database/scripts $BUILD_DIR && \
    cp -r /mnt/build-context/docker/database/conf.sql /docker-entrypoint-initdb.d/01_conf.sql && \
    $BUILD_DIR/scripts/prepare-postgis.sh && \
    su postgres -c "pg_ctl start -D $PGDATA" && \
    cd $BUILD_DIR && \
    DSN="jdbc:postgresql://localhost:5432/${POSTGRES_DB}?&user=${POSTGRES_USER}&password=${POSTGRES_PASSWORD}" && \
    jq --arg URL "$URL" '.steps[0].tasks[0].url = $URL' workflow.json | sponge workflow.json && \
    jq --arg DSN "$DSN" 'walk(if type == "object" and has("database") then ( .database = $DSN ) else . end)' workflow.json | sponge workflow.json && \
    jq 'del(.steps[2])' workflow.json | sponge workflow.json && \
    $BAREMAPS_DIR/bin/baremaps workflow execute --file workflow.json && \
    psql -U $POSTGRES_USER -d $POSTGRES_DB -c "ALTER SYSTEM SET max_wal_size = '32MB'" && \
    psql -U $POSTGRES_USER -d $POSTGRES_DB -c "ALTER SYSTEM RESET shared_buffers" && \
    su postgres -c 'vacuumdb -U $POSTGRES_USER -f -a' && \
    su postgres -c "pg_ctl stop -D $PGDATA" && \
    #pg_archivecleanup -d /var/lib/postgresql/data/pg_wal $(pg_controldata | grep "Latest checkpoint's REDO WAL" | cut -d: -f2 | tr -d [:space:]) && \
# Cleanup
    rm -rf $BAREMAPS_DIR $BUILD_DIR && \
    apk del ${REQ_BUILD} && \
    rm -rf /var/cache/apk/*

# Start from an emty image to get rid of layers
#FROM scratch

#COPY --from=postgis / /

# TODO: Add index SQL to entrypoint
