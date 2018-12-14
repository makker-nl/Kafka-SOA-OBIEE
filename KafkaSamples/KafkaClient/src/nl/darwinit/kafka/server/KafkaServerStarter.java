package nl.darwinit.kafka.server;

import java.io.IOException;

import java.util.Properties;

import kafka.metrics.KafkaMetricsReporter;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;

import nl.darwinit.kafka.properties.PropertiesFactory;

import org.apache.kafka.common.utils.SystemTime;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import scala.Option;

import scala.collection.Seq;
import scala.collection.mutable.ArraySeq;

/**
 * https://stackoverflow.com/questions/9286054/is-it-possible-to-start-a-zookeeper-server-instance-in-process-say-for-unit-tes
 * https://www.programcreek.com/java-api-examples/index.php?api=kafka.server.KafkaServer
 * https://grokonez.com/java-integration/distributed-system/start-apache-kafka#2_Start_a_Kafka_server
 */
public class KafkaServerStarter {
    protected Logger logger = LogManager.getLogger(this.getClass());

    public KafkaServerStarter() {
        super();
    }


    public void start() {
        Properties serverProperties;
        try {
            serverProperties = PropertiesFactory.getKSProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KafkaConfig config = KafkaConfig.fromProps(serverProperties);
        Seq<KafkaMetricsReporter> reporters =
            new ArraySeq<KafkaMetricsReporter>(0); //  KafkaMetricsReporter$.MODULE$.startReporters(new VerifiableProperties(serverProperties));
        KafkaServer server = new KafkaServer(config, new SystemTime(), Option.apply("prefix"), reporters);

        new Thread() {
            public void run() {
                server.startup();
            }
        }.start();
    }

    public static void main(String[] args) {
        KafkaServerStarter kafkaServerStarter = new KafkaServerStarter();
        kafkaServerStarter.start();
    }
}
