# syntax=docker/dockerfile:experimental

FROM alpine:3.22

LABEL maintainer="cmahnke@gmail.com"
LABEL org.opencontainers.image.source https://github.com/cmahnke/map-tools

ENV BUILD_DEPS="py3-pip" \
    RUN_DEPS="python3"

RUN apk --update upgrade && \
    apk add --no-cache $RUN_DEPS $BUILD_DEPS && \
    mkdir -p $BUILD_DIR ~/.config/pip/ && \
    printf "\n[global]\nbreak-system-packages = true" >> ~/.config/pip/pip.conf && \
    pip install mbutil && \
## Cleanup
    cd / && rm -rf $BUILD_DIR /root/.gradle && \
    apk del $BUILD_DEPS && \
    rm -rf /var/cache/apk/*
