#!/bin/sh

#Customizations to the default setDomainEnv.sh-script. This file is sourced from there

##### GENERIC - ALL SERVERS #####
# Set base mem args.
USER_MEM_ARGS="-Xms2g -Xmx2g"

# add Kafka libraries to the WebLogic Server system claspath
export KAFKA_HOME=/app/opensource/confluent
export KAFKA_JAVA_HOME=$KAFKA_HOME/share/java/kafka
export CONFLUENT_JAVA_HOME=$KAFKA_HOME/share/java/confluent-common
export KAFKA_CLIENT_HOME=/home/oracle/projects/Kafka-SOA-OBIEE/KafkaSamples/KafkaClient
#
if [ "${POST_CLASSPATH}" != "" ] ; then
  POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/kafka.jar"
  export POST_CLASSPATH
else
  POST_CLASSPATH="${KAFKA_JAVA_HOME}/kafka.jar"
  export POST_CLASSPATH
fi
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/kafka-clients-2.0.0-cp1.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/log4j-1.2.17.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/slf4j-log4j12-1.7.25.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/slf4j-api-1.7.25.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/kafka-log4j-appender-2.0.0-cp1.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/zookeeper-3.4.13.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/scala-library-2.11.12.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${CONFLUENT_JAVA_HOME}/common-metrics-5.0.0.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/scala-logging_2.11-3.9.0.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/metrics-core-2.2.0.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/jackson-core-2.9.6.jar"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/jackson-databind-2.9.6.jar"
export POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_JAVA_HOME}/jackson-annotations-2.9.6.jar"
#
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_HOME}/etc/kafka"
POST_CLASSPATH="${POST_CLASSPATH}${CLASSPATHSEP}${KAFKA_CLIENT_HOME}/config"
#
logdir=${DOMAIN_HOME}/servers/${SERVER_NAME}/logs

#As per bug 17358032, Derby is currently enabled by default. Disable Derby, we are using Oracle.
export DERBY_FLAG="false"

#Signal the JVM that there is no display available:
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -Djava.awt.headless=true"

#Enables the use of compressed pointers (object references represented as 32 bit offsets instead of 64-bit pointers) for optimized 64-bit performance with Java heap sizes less than 32gb.
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -XX:+UseCompressedOops"

#Size the Young Genration to 1/4 th of the heap, that is ratio 1:3:
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -XX:NewRatio=3"

#Reduce signal usage (by using this a shutdown will not gracefully shutdown the weblogic
#servers therefore the nodemanager will start them after failure)
#Note: Restart after failure needs to be configured at weblogic level
#Note2: Nodemanager needs to be registered as deamon in order to be started after (re-)boot
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -Xrs"

#Enable GC log:
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -Xloggc:${logdir}/gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=100 -XX:GCLogFileSize=10M"

#Enable heap dump on out of memory:
_HEAP_DUMP_PATH=${logdir}/heap_dumps
#Make sure this directory exists:
mkdir -p ${_HEAP_DUMP_PATH}
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${_HEAP_DUMP_PATH}"

#Enable flight recordings
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

#Enable default recording
_JFR_DUMP_PATH=${logdir}/jfr_dumps
#Make sure this directory exists:
mkdir -p ${_JFR_DUMP_PATH}
export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true,dumponexitpath=${_JFR_DUMP_PATH},repository=${logdir}/jfr_recordings,maxage=1h,maxsize=100MB,disk=true"

##### GENERIC - ALL SERVERS #####

##### AdminServer only #####
if [ "${STARTUP_GROUP}" = "AdminServerStartupGroup" ] ; then
   USER_MEM_ARGS="-Xms2g -Xmx2g"
fi
##### AdminServer only #####

##### SOA Servers only #####
if [ "${STARTUP_GROUP}" = "SOA-MGD-SVRS" ] ; then
#   USER_MEM_ARGS="-Xms4g -Xmx4g"
   USER_MEM_ARGS="-Xms2g -Xmx2g"
fi
##### SOA Servers only #####

##### OSB Servers with integrated OWSM only #####
if [ "${STARTUP_GROUP}" = "OSB-MGD-SVRS-COMBINED" ] ; then
#   USER_MEM_ARGS="-Xms3g -Xmx3g"
   USER_MEM_ARGS="-Xms2g -Xmx2g"
   export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -D_Offline_FileDataArchive=true -Dweblogic.connector.ConnectionPoolProfilingEnabled=false -Dcom.bea.wlw.netui.disableInstrumentation=true"
fi
##### OSB Servers with integrated OWSM only #####

##### OSB Servers without integrated OWSM only #####
if [ "${STARTUP_GROUP}" = "OSB-MGD-SVRS-ONLY" ] ; then
   USER_MEM_ARGS="-Xms2g -Xmx2g"
   export EXTRA_JAVA_PROPERTIES="${EXTRA_JAVA_PROPERTIES} -D_Offline_FileDataArchive=true -Dweblogic.connector.ConnectionPoolProfilingEnabled=false -Dcom.bea.wlw.netui.disableInstrumentation=true"
fi
##### OSB Servers without integrated OWSM only #####

##### Separate WSM-PM Servers only #####
if [[ "${STARTUP_GROUP}" =~ .*WSMPM-MAN-SVR.* ]] ; then
   USER_MEM_ARGS="-Xms1536m -Xmx1536m"
fi
##### Separate WSM-PM Servers only #####

##### BI Servers only ######
if [ "${STARTUP_GROUP}" = "BISUITE-MAN-SVR" ] ; then
   USER_MEM_ARGS="-Xms2g -Xmx2g"
fi
##### BI Servers only ######

##### WCC Servers only #####
if [ "${STARTUP_GROUP}" = "UCM-MGD-SVR" ] ; then
   USER_MEM_ARGS="-Xms4g -Xmx4g"
fi
##### WCC Servers only #####

##### IBR Servers only #####
if [ "${STARTUP_GROUP}" = "IBR-MGD-SVR" ] ; then
   USER_MEM_ARGS="-Xms4g -Xmx4g"         
fi
##### IBR Servers only #####

##### ADF Servers only #####
if [ "${STARTUP_GROUP}" = "UCM_ADF-MGD-SVR" ] ; then
   USER_MEM_ARGS="-Xms4g -Xmx4g"
fi
##### ADF Servers only #####
