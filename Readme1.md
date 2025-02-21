import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class KafkaProducerExample {
    public static void main(String[] args) {
        // Kafka Properties
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "your.kafka.server:9093"); // Use 9093 for SSL

        // Security Properties for SSL
        kafkaProps.put("security.protocol", "SSL");
        kafkaProps.put("ssl.truststore.location", "/path/to/truststore.jks");
        kafkaProps.put("ssl.truststore.password", "your-truststore-password");
        kafkaProps.put("ssl.keystore.location", "/path/to/keystore.jks");
        kafkaProps.put("ssl.keystore.password", "your-keystore-password");
        kafkaProps.put("ssl.key.password", "your-key-password");

        // Kafka Producer Config
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("acks", "all"); // Ensures message is fully committed

        // Create Kafka Producer
        Producer<String, String> producer = new KafkaProducer<>(kafkaProps);

        try {
            String topic = "your_topic";
            String key = "test-key";
            String value = "Hello Kafka from JMeter Java!";

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            // Sending message synchronously
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Message sent to topic " + metadata.topic() + " at offset " + metadata.offset());

        } catch (Exception e) {
            System.err.println("Kafka Error: " + e.getMessage());
        } finally {
            producer.close();
        }
    }
}
