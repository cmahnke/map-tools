#/bin/sh

DOWNLOAD_DIR="$PLANETILER_DATA_DIR/data/sources/"

mkdir -p "$DOWNLOAD_DIR"
cd "$DOWNLOAD_DIR"
echo "Downloading to `pwd`"
wget https://osmdata.openstreetmap.de/download/water-polygons-split-3857.zip
wget https://dev.maptiler.download/geodata/omt/lake_centerline.shp.zip
wget https://dev.maptiler.download/geodata/omt/natural_earth_vector.sqlite.zip
ln -s $PLANETILER_DATA_DIR/data $PLANETILER_DIR/data
