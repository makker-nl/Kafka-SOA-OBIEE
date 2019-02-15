#!/bin/bash
SCRIPTPATH=$(dirname $0)
#
. $SCRIPTPATH/fmw12c_env.sh
echo
echo Start domain: $DOMAIN_HOME
wlst.sh $SCRIPTPATH/startDomain.py -loadProperties $SCRIPTPATH/fmw.properties
