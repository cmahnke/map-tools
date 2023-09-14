Experimental Docker image for map creation
==========================================

# Building

```
DOCKER_BUILDKIT=1 docker build -t ghcr.io/cmahnke/map-action:latest .
```

## Conversion tools

```
docker buildx build -f docker/conversion-tools/Dockerfile .
```

## Osmosis

```
docker buildx build -f docker/osmosis/Dockerfile .
```

## Baremaps

```
docker buildx build -f docker/baremaps/Dockerfile .
```

## Database

```
docker buildx build -f docker/database/Dockerfile .
```

or

```
docker buildx build --progress=plain --build-arg="URL=https://download.geofabrik.de/europe/luxembourg-latest.osm.pbf" -f docker/database/Dockerfile .
```

# TODO
* Generate Image with map data
