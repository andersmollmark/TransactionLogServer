#!/bin/bash

addr=$1

TEMP=`getopt -o s --long self -n 'asterixgui' -- "$@"`

if [ $? != 0 ] ; then echo "Terminating..." >&2 ; exit 1 ; fi

# Note the quotes around `$TEMP': they are essential!
eval set -- "$TEMP"

while true ; do
	case "$1" in
        -s|--self)
        addr="self"
		shift 1
        break ;;

        *) 
        break ;;
	esac
done

if [ "$addr" == "self" ] ; then
    if [ "$2" == "" ] ; then
        addr=localhost
    else
        addr=$2
    fi
fi


THIS_SCRIPT=$(/usr/bin/readlink -nf "$0")
DIR=${THIS_SCRIPT%/*}
pushd $DIR

if [ "$addr" == "" ] ; then
    echo Input Ip-address
    read addr
fi

echo Installing $addr
chmod 600 ../key/sparedisk-3.0.key
ssh -i ../key/sparedisk-3.0.key install@$addr 'sudo chmod a+w /tmp/; sudo rm /tmp/*utls*'
scp -i ../key/sparedisk-3.0.key ../target/utls-install.tar.gz install@$addr:/tmp/
ssh -i ../key/sparedisk-3.0.key install@$addr "sudo /bin/tar -xzf /tmp/utls-install.tar.gz -C /tmp;sudo /bin/bash /tmp/target.sh"
popd


