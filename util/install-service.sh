#!/bin/bash

[ "$(id -u)" != 0 ] && echo "The service installation requires root privileges." && exit 1

FILE=/etc/systemd/system/gw2-event-bot.service
if [ -f "$FILE" ]; then
    read -p "A service file already exists. Do you want to overwrite it? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]
    then
        echo "The old service file will be overwritten."
    else
        echo "Aborting service installation." && exit 0
    fi
fi

SCRIPTPATH=$(dirname $(readlink -f "$0"))
PROJPATH=$(dirname $SCRIPTPATH)

sed -e "s|\${dir}|$PROJPATH|" $SCRIPTPATH/gw2-event-bot.service > $FILE|| exit 1

echo "The service file has been installed successfully. If you want to enable and/or run the service, you can do so now."
