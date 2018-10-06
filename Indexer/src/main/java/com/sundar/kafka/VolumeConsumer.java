/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sundar.kafka;

import com.sundar.es.utils.ESConnection;
import com.sundar.es.utils.ESUtils;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

/**
 *
 * @author sundar
 */
public class VolumeConsumer {

    private final static Logger LOG = Logger.getLogger(VolumeConsumer.class);
    private static String TOPIC = null;
    private static String BOOTSTRAP_SERVERS = null;
    private static String CONSUMER_GROUP = null;
    private static String MAX_RECORD = null;
    private static long CONSUMER_POLL = 0;
    private static String HOST = null;
    private static int PORT = 0;
    private static String CLUSTER_NAME = null;
    private BulkRequestBuilder bulkRequest;

    public VolumeConsumer() {
        try {
            Properties pro = new Properties();
            pro.load(VolumeConsumer.class.getResourceAsStream("/conf/consumerconf.properties"));
            VolumeConsumer.BOOTSTRAP_SERVERS = pro.getProperty("BOOTSTRAP_SERVERS");
            VolumeConsumer.TOPIC = pro.getProperty("TOPIC");
            VolumeConsumer.MAX_RECORD = pro.getProperty("MAX_RECORD");
            VolumeConsumer.CONSUMER_GROUP = pro.getProperty("CONSUMER_GROUP");
            VolumeConsumer.CONSUMER_POLL = Integer.valueOf(pro.getProperty("CONSUMER_POLL", "5"));
            VolumeConsumer.HOST = pro.getProperty("HOST");
            VolumeConsumer.PORT = Integer.valueOf(pro.getProperty("PORT", "6300"));
            VolumeConsumer.CLUSTER_NAME = pro.getProperty("CLUSTER_NAME");
        } catch (IOException ex) {
            LOG.error("Exception occurred while load Properties : " + ex, ex);
        }
    }

    private static Consumer<Long, Object> createConsumer() {

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_RECORD);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        final Consumer<Long, Object> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(TOPIC));

        return consumer;
    }

    private void runConsumer() throws InterruptedException, UnknownHostException, IOException {
        try (Consumer<Long, Object> consumer = createConsumer()) {
            Client client = ESConnection.getClient(HOST, PORT, CLUSTER_NAME);
            while (true) {
                final ConsumerRecords<Long, Object> consumerRecords = consumer.poll(CONSUMER_POLL);
                ESUtils.createByIndexTemplate("syslog", client);
                final String index = "syslog";
                for (ConsumerRecord<Long, Object> record : consumerRecords) {
                    String receivedByteArr = record.value().toString();
                    String[] datas = receivedByteArr.split("\t");

                    IndexRequestBuilder prepareIndex = client.prepareIndex(index, "syslog");
                    prepareIndex.setSource(ESUtils.convertJson(datas));
                    bulkRequest = client.prepareBulk();
                    bulkRequest.add(prepareIndex);
                    if (bulkRequest.numberOfActions() >= 5) {
                        bulkRequest.execute();
                    }
                }
            }
        }
    }

    public static void main(String... args) throws Exception {
        new VolumeConsumer().runConsumer();
    }
}
