#!/bin/bash
SCRIPTPATH=$(dirname $0)
# 
$SCRIPTPATH/kfk_env.sh
#
$KAFKA_HOME/bin/confluent start
