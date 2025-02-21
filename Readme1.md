import org.apache.kafka.clients.producer.*
import java.util.*

val kafkaProps = Properties()

// Kafka Bootstrap Server
kafkaProps["bootstrap.servers"] = "your.kafka.server:9093" // Use 9093 for SSL

// Security Properties for SSL
kafkaProps["security.protocol"] = "SSL"
kafkaProps["ssl.truststore.location"] = "/path/to/truststore.jks"
kafkaProps["ssl.truststore.password"] = "your-truststore-password"
kafkaProps["ssl.keystore.location"] = "/path/to/keystore.jks"
kafkaProps["ssl.keystore.password"] = "your-keystore-password"
kafkaProps["ssl.key.password"] = "your-key-password"

// Kafka Producer Config
kafkaProps["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
kafkaProps["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
kafkaProps["acks"] = "all" // Ensures message is fully committed

// Create Producer
val producer = KafkaProducer<String, String>(kafkaProps)

try {
    val topic = "your_topic"
    val key = "test-key"
    val value = "Hello Kafka from JMeter Kotlin!"

    val record = ProducerRecord(topic, key, value)
    producer.send(record) { metadata, exception ->
        if (exception == null) {
            println("Message sent to topic ${metadata.topic()} at offset ${metadata.offset()}")
        } else {
            println("Error sending message: ${exception.message}")
        }
    }
} catch (e: Exception) {
    println("Kafka Error: ${e.message}")
} finally {
    producer.close()
}
