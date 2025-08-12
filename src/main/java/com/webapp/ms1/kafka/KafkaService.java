package com.webapp.ms1.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.random.RandomGenerator;

@Slf4j
@Service
public class KafkaService {

    public static String KAFKA_URL = "localhost:9092";

    // This method can be used to create a topic programmatically if needed.
    public void createTopic(String topicName, int numPartitions, short replicationFactor) throws ExecutionException, InterruptedException {
        // Set up the properties for connecting to Kafka
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_URL);

        // Create the AdminClient using the properties
        try (AdminClient adminClient = AdminClient.create(config)) {
            // Define the new topic
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);

            // Create the topic (the call is asynchronous)
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            System.out.println("Topic '" + topicName + "' created successfully with " + numPartitions + " partitions.");
        } catch (Exception e) {
            log.error("Unexpected error while creating topic: {}", e.getMessage());
            throw new RuntimeException("Failed to create topic", e);
        }
    }

    static void consume(String topicName, String groupId) {
        // Set up consumer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_URL);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");  // Start from the beginning if no offset is present

        // Create the consumer
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("groupId: {}", groupId);
                    log.info("Received record: key = {}, value = {}, partition = {}, offset = {}",
                            record.key(), record.value(), record.partition(), record.offset());
                }
            }
        } finally {
            consumer.close();
        }
    }

    public void produce(String topicName, String key, String value) {
        // Set up producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_URL);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        // Create the producer
        Producer<String, String> producer = new KafkaProducer<>(props);
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            // Asynchronous send with callback
            producer.send(record, (RecordMetadata metadata, Exception exception) -> {
                if (exception != null) {
                    log.error("Error sending record: ", exception);
                } else {
                    log.info("Sent record(key={} value={}) to partition {} with offset {}",
                            key, value, metadata.partition(), metadata.offset());
                }
            });
        } finally {
            producer.close();
        }
    }
}
