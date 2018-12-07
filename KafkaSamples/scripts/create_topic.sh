#!/bin/bash
SCRIPTPATH=$(dirname $0)
# 
$SCRIPTPATH/kfk_env.sh
#
export KFK_TOPIC=$1
#
$KAFKA_HOME/bin/kafka-topics --create \
  --zookeeper $KFK_ZOOKEEPER \
  --replication-factor 1 --partitions 13 \
  --topic $KFK_TOPIC
