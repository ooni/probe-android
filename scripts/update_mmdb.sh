#!/bin/sh
set -e
RAWDIR="app/src/main/res/raw/"

download_geoip() {
    echo "* Fetching geoip databases"
    base=https://geolite.maxmind.com/download/geoip/database
    if [ ! -f "country.mmdb" ]; then
        curl -fsSLO $base/GeoLite2-Country.tar.gz
        tar -xf GeoLite2-Country.tar.gz
        mv GeoLite2-Country_20??????/GeoLite2-Country.mmdb country.mmdb
        rm -rf GeoLite2-Country_20?????? GeoLite2-Country.tar.gz
    fi
    if [ ! -f "asn.mmdb" ]; then
        curl -fsSLO $base/GeoLite2-ASN.tar.gz
        tar -xf GeoLite2-ASN.tar.gz
        mv GeoLite2-ASN_20??????/GeoLite2-ASN.mmdb asn.mmdb
        rm -rf GeoLite2-ASN_20?????? GeoLite2-ASN.tar.gz
    fi
}

download_geoip
mv asn.mmdb $RAWDIR
mv country.mmdb $RAWDIR
