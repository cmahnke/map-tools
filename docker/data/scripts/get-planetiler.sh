#!/bin/sh

NAME=`date |md5`

docker pull ghcr.io/cmahnke/map-tools/planetiler:latest
docker create --name $NAME ghcr.io/cmahnke/map-tools/planetiler:latest
docker cp $NAME:/opt/planetiler .
mv planetiler/planetiler-*-with-deps.jar .
rm -rf planetiler
docker rm -f $NAME
