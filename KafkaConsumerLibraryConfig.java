To create a Kafka client library that can dynamically listen to multiple topics with different group.id configurations—without knowing the exact number of topics in advance—you need to make your library flexible and configurable. The key is to allow the application that imports your library to configure the topics and consumer group IDs at runtime (e.g., through properties, configuration files, or environment variables).

Here’s a plan to implement such a library:

Key Points for Flexibility:

	1.	Dynamic Configuration: Allow the application to provide a list of topics and corresponding group.id values at runtime.
	2.	Kafka Consumer Creation: Dynamically create Kafka listeners based on the provided configuration.
	3.	Spring Boot Integration: Use Spring Boot’s property-based configuration to inject topics and consumer group IDs.

Step-by-Step Approach:

1. Kafka Consumer Library - Dynamic Configuration Support

In your client library, you can configure the Kafka consumers dynamically. You’ll provide a method to read the topics and group IDs from the application’s configuration and set up consumers accordingly.

Here’s how to create this dynamic consumer configuration in your library:

KafkaConsumerLibraryConfig.java (in the library)

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerLibraryConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory(null)); // Generic factory, set groupId later dynamically
        factory.setConcurrency(3); // Optional, configure concurrency

        return factory;
    }

    // Dynamically set group.id from configuration
    public ConsumerFactory<String, String> consumerFactory(String groupId) {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }
}

This class sets up a generic kafkaListenerContainerFactory that will be reused for multiple listeners. The group.id is dynamically set when the listener is registered.

2. KafkaListenerConfigurer - Dynamic Topic and Group ID Handling

Your library will need a mechanism to dynamically create listeners for the topics and group IDs based on the application’s configuration.

KafkaListenerConfigurer.java (in the library)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class KafkaListenerConfigurer {

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, String> factory;

    @Autowired
    private KafkaConsumerLibraryConfig kafkaConsumerLibraryConfig;

    // This method is called at application startup to register listeners dynamically
    public void configureListeners(Map<String, List<String>> topicsWithGroupIds) {
        topicsWithGroupIds.forEach((groupId, topics) -> {
            topics.forEach(topic -> {
                createListenerForTopic(topic, groupId);
            });
        });
    }

    // Create Kafka Listener dynamically for each topic and groupId
    public void createListenerForTopic(String topic, String groupId) {
        factory.setConsumerFactory(kafkaConsumerLibraryConfig.consumerFactory(groupId));

        ConcurrentMessageListenerContainer<String, String> container =
                factory.createContainer(topic);
        
        container.setupMessageListener((MessageListener<String, String>) record -> {
            System.out.printf("Consumed message from topic: %s, groupId: %s -> Key: %s, Value: %s%n",
                    record.topic(), groupId, record.key(), record.value());
        });

        container.start();  // Start the listener
    }
}

Explanation:

	•	configureListeners(): This method accepts a Map<String, List<String>> where each key is a group.id, and the value is a list of topics associated with that group. It creates a listener for each topic and group ID dynamically.
	•	createListenerForTopic(): This method creates a Kafka listener for a given topic and group ID at runtime, setting up the necessary consumer factory and message listener.

3. Configuring the Application Using the Library

Now, in your application, you need to provide the configuration dynamically (e.g., from application.yml or programmatically) and call the configureListeners method from your library.

ApplicationConfig.java (in the application)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Autowired
    private KafkaListenerConfigurer kafkaListenerConfigurer;

    @PostConstruct
    public void setupKafkaListeners() {
        // Example: Dynamically providing group IDs and topics from configuration
        Map<String, List<String>> topicsWithGroupIds = new HashMap<>();
        
        topicsWithGroupIds.put("t1", Arrays.asList("topic1"));
        topicsWithGroupIds.put("t2", Arrays.asList("topic2"));
        topicsWithGroupIds.put("t3", Arrays.asList("topic3"));

        // Call the method to configure listeners dynamically
        kafkaListenerConfigurer.configureListeners(topicsWithGroupIds);
    }
}

In this configuration:

	•	setupKafkaListeners(): You dynamically build the map of topics and group IDs based on your application’s configuration. This can be done from the application.yml, environment variables, or other sources as needed.

4. Using External Configuration

You can make this even more dynamic by externalizing the configuration into application.yml (in the application using the library):

kafka:
  topics-with-groupids:
    t1:
      - topic1
    t2:
      - topic2
    t3:
      - topic3

Then, in your ApplicationConfig.java, you can inject this configuration using @ConfigurationProperties or @Value and pass it to KafkaListenerConfigurer.

5. Using @ConfigurationProperties

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicsConfig {

    private Map<String, List<String>> topicsWithGroupids;

    public Map<String, List<String>> getTopicsWithGroupids() {
        return topicsWithGroupids;
    }

    public void setTopicsWithGroupids(Map<String, List<String>> topicsWithGroupids) {
        this.topicsWithGroupids = topicsWithGroupids;
    }
}

Then inject this KafkaTopicsConfig into ApplicationConfig:

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ApplicationConfig {

    @Autowired
    private KafkaListenerConfigurer kafkaListenerConfigurer;

    @Autowired
    private KafkaTopicsConfig kafkaTopicsConfig;

    @PostConstruct
    public void setupKafkaListeners() {
        // Pass the topics with group IDs to the library
        kafkaListenerConfigurer.configureListeners(kafkaTopicsConfig.getTopicsWithGroupids());
    }
}

Summary:

	•	Flexible Listener Configuration: The library dynamically sets up Kafka listeners based on a map of topics and group IDs provided at runtime.
	•	Spring Boot Integration: The application using the library can provide configuration (through properties or application.yml).
	•	Dynamic Listener Creation: Kafka consumers are created dynamically based on the provided configuration at startup.

This allows the application to specify an arbitrary number of topics and consumer group IDs without modifying the library code.
