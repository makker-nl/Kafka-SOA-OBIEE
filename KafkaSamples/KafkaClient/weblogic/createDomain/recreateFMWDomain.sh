#!/bin/bash
SCRIPTPATH=$(dirname $0)
#
. $SCRIPTPATH/fmw12c_env.sh
echo
echo Recreate domain: $DOMAIN_HOME
rm -rf $DOMAIN_HOME
rm -rf $APPLICATIONS_HOME
wlst.sh $SCRIPTPATH/createFMWDomain.py -loadProperties $SCRIPTPATH/fmw.properties
echo
echo Copy setUserOverrides.sh to: $DOMAIN_HOME/bin
cp $SCRIPTPATH/setUserOverrides.sh $DOMAIN_HOME/bin