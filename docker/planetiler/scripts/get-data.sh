#/bin/sh

cd "$PLANETILER_DATA_DIR/data/sources/"
wget https://osmdata.openstreetmap.de/download/water-polygons-split-3857.zip
wget https://dev.maptiler.download/geodata/omt/lake_centerline.shp.zip
wget https://dev.maptiler.download/geodata/omt/natural_earth_vector.sqlite.zip
ln -s $PLANETILER_DATA_DIR/data $PLANETILER_DIR/data
