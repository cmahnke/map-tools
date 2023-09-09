#!/usr/bin/env bash

OPENMAPTILES_GIT=https://github.com/openmaptiles/openmaptiles.git
OPENMAPTILES_TAG="v3.12.2"

git clone $OPENMAPTILES_GIT --branch $OPENMAPTILES_TAG --single-branch
cd openmaptiles
docker run -v $(pwd):/tileset openmaptiles/openmaptiles-tools make
docker-compose up -d postgres
docker-compose run import-water
docker-compose run import-natural-earth
docker-compose run import-lakelines
docker-compose run import-osmborder
