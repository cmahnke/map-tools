#!/bin/sh

NAME=`date |md5`

docker pull ghcr.io/cmahnke/map-tools/osmosis:latest
docker create --name $NAME ghcr.io/cmahnke/map-tools/osmosis:latest
docker cp $NAME:/opt/osmosis .
docker rm -f $NAME
