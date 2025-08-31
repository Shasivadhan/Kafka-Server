package com.ksv.producer.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.CRC32;

public class ReverseKeyPartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
        // No custom configuration required
    }

    @Override
    public int partition(String topic,
                         Object key,
                         byte[] keyBytes,
                         Object value,
                         byte[] valueBytes,
                         Cluster cluster) {

        // Total number of partitions for the topic
        int partitionCount = cluster.partitionsForTopic(topic).size();

        // If no key provided → default to partition 0
        if (key == null) {
            return 0;
        }

        // Step 1: Reverse the key string
        String reversedKey = new StringBuilder(key.toString()).reverse().toString();

        // Step 2: Use CRC32 hashing algorithm on reversed key
        CRC32 crc = new CRC32();
        crc.update(reversedKey.getBytes(StandardCharsets.UTF_8));

        // Step 3: Compute partition by modulus operation
        return (int) (Math.abs(crc.getValue()) % partitionCount);
    }

    @Override
    public void close() {
        // No resource cleanup required
    }
}
