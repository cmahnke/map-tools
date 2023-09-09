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
