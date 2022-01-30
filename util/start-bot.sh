#!/bin/bash

PROJPATH=$(dirname $(dirname $(readlink -f "$0")))

java -jar $PROJPATH/target/GW2-Raid-Bot-1.0-SNAPSHOT.jar
