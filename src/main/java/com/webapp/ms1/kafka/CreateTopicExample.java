package com.webapp.ms1.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CreateTopicExample {

    public static void main(String[] args) {
        String topicName = "sensor-readings";
        int numPartitions = 3;
        short replicationFactor = 1; // Use 1 for a local/development cluster

        // Set up the properties for connecting to Kafka
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Create the AdminClient using the properties
        try (AdminClient adminClient = AdminClient.create(config)) {
            // Define the new topic
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);

            // Create the topic (the call is asynchronous)
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            System.out.println("Topic '" + topicName + "' created successfully with " + numPartitions + " partitions.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
