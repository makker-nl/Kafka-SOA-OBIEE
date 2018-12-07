#!/bin/bash
SCRIPTPATH=$(dirname $0)
# 
. $SCRIPTPATH/kfk_env.sh
#
echo Zookeeper $KFK_ZOOKEEPER
echo Node $KFK_NODE
#
$KAFKA_HOME/bin/kafka-topics --list \
  --zookeeper $KFK_ZOOKEEPER 
