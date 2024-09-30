Consider below points for my question

1) I have two kafka providers(Say Fusion or KaaS) and from old provider(Fusion) to new provider(KaaS) we are migrating
2) I have multiple services having kafka configuration configured for old kafka service provider in application.yml using spring boot auto configurer
3) Since we have multiple services , we are migrating in multiple phases
4) It could be the case in one service it could be consuming message from any provider and producing message to any provider
5) Since we are doing migration, I want to create configuration in common library(Say citigate-commons) and in all services I want it flag based  which kafka service provider I will be using(either / or Fusion/Kaas or nothing). If flag is on lets say kaasKafkaEnabled = true then it should use kaas one and if flag is on lets say fusionKafkaEnabled = true then it should use fusion and if both are enabled then using qualifier I will be accessing kafkaTemplate
6) If a service has not migrated to the new Kafka provider (KaaS), the existing configuration (old provider - Fusion) should work as is, and citigate-commons Kafka config should not be applied.
7) If a service has migrated (by setting the flag in application.yml), it will use the citigate-commons library to configure Kafka with KaaS, or Fusion, or both.
8) in service application.yml if nothing has been configured, it should not use any thing.

To address the scenario where you have two Kafka providers (Fusion and KaaS) and need to manage Kafka configuration for multiple services based on flags (`kaasKafkaEnabled`, `fusionKafkaEnabled`), you can design the solution by creating a shared configuration in a common library (`citigate-commons`). This configuration will handle which Kafka service to use based on flags in the `application.yml`.

Here’s how you can implement this:

### Steps to Implement:

1. **Define Flags in `application.yml`**
   Each service can define its Kafka provider configuration based on flags. For example:
   ```yaml
   kafka:
     kaasKafkaEnabled: true
     fusionKafkaEnabled: false
   ```

2. **Create Kafka Configuration in `citigate-commons` Library**
   In your common library, define a Spring configuration class that will check the flags and configure Kafka templates accordingly.

   ```java
   import org.apache.kafka.clients.producer.ProducerConfig;
   import org.apache.kafka.common.serialization.StringSerializer;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.context.annotation.Primary;
   import org.springframework.kafka.core.DefaultKafkaProducerFactory;
   import org.springframework.kafka.core.KafkaTemplate;
   import org.springframework.kafka.core.ProducerFactory;

   import java.util.HashMap;
   import java.util.Map;

   @Configuration
   public class KafkaConfig {

       @Value("${kafka.kaasKafkaEnabled}")
       private boolean kaasKafkaEnabled;

       @Value("${kafka.fusionKafkaEnabled}")
       private boolean fusionKafkaEnabled;

       private final KafkaProperties kafkaProperties;

       public KafkaConfig(KafkaProperties kafkaProperties) {
           this.kafkaProperties = kafkaProperties;
       }

       // Common Kafka producer properties
       private Map<String, Object> commonProducerConfigs() {
           Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());
           configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
           configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
           return configs;
       }

       // Configuration for KaaS Kafka Template
       @Bean(name = "kaasKafkaTemplate")
       @Primary
       public KafkaTemplate<String, String> kaasKafkaTemplate() {
           if (kaasKafkaEnabled) {
               Map<String, Object> configs = new HashMap<>(commonProducerConfigs());
               configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kaas.kafka.bootstrap.servers");
               ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
               return new KafkaTemplate<>(producerFactory);
           }
           return null;
       }

       // Configuration for Fusion Kafka Template
       @Bean(name = "fusionKafkaTemplate")
       public KafkaTemplate<String, String> fusionKafkaTemplate() {
           if (fusionKafkaEnabled) {
               Map<String, Object> configs = new HashMap<>(commonProducerConfigs());
               configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "fusion.kafka.bootstrap.servers");
               ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
               return new KafkaTemplate<>(producerFactory);
           }
           return null;
       }
   }
   ```

3. **Inject and Use KafkaTemplate Based on Flags**
   In your services, you can inject the `KafkaTemplate` and decide based on the flags which provider to use.

   ```java
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.beans.factory.annotation.Qualifier;
   import org.springframework.kafka.core.KafkaTemplate;
   import org.springframework.stereotype.Service;

   @Service
   public class KafkaMessageService {

       private final KafkaTemplate<String, String> kafkaTemplate;

       @Autowired
       public KafkaMessageService(@Qualifier("kaasKafkaTemplate") KafkaTemplate<String, String> kaasKafkaTemplate,
                                  @Qualifier("fusionKafkaTemplate") KafkaTemplate<String, String> fusionKafkaTemplate,
                                  @Value("${kafka.kaasKafkaEnabled}") boolean kaasKafkaEnabled,
                                  @Value("${kafka.fusionKafkaEnabled}") boolean fusionKafkaEnabled) {

           // Choose the appropriate KafkaTemplate based on flags
           if (kaasKafkaEnabled) {
               this.kafkaTemplate = kaasKafkaTemplate;
           } else if (fusionKafkaEnabled) {
               this.kafkaTemplate = fusionKafkaTemplate;
           } else {
               throw new IllegalArgumentException("No Kafka provider enabled!");
           }
       }

       public void sendMessage(String topic, String message) {
           kafkaTemplate.send(topic, message);
       }
   }
   ```

4. **Use Qualifier for Multiple Kafka Providers**
   If both flags are enabled (`kaasKafkaEnabled` and `fusionKafkaEnabled` are `true`), you can use the `@Qualifier` annotation to specify which `KafkaTemplate` to use at the method level.

   ```java
   @Autowired
   @Qualifier("kaasKafkaTemplate")
   private KafkaTemplate<String, String> kaasKafkaTemplate;

   @Autowired
   @Qualifier("fusionKafkaTemplate")
   private KafkaTemplate<String, String> fusionKafkaTemplate;
   ```

### Key Points:
- **Flag-Based Configuration**: The choice between Fusion and KaaS Kafka providers is controlled through flags (`kaasKafkaEnabled` and `fusionKafkaEnabled`) in the `application.yml` file.
- **Common Kafka Configuration**: The Kafka configuration is moved to a common library (`citigate-commons`), ensuring consistent configurations across all services.
- **Spring Boot Autoconfiguration**: Spring Boot’s autoconfiguration capabilities are leveraged to automatically configure Kafka based on the active flags.
- **Qualifier for Multiple Providers**: When both providers are enabled, the `@Qualifier` annotation ensures you can choose the appropriate `KafkaTemplate`.

This solution allows you to migrate services to the new Kafka provider (KaaS) in phases while maintaining support for both Kafka providers based on configuration flags.


---------
Coldnt understand , explain more
For the services I have not picked migration and that is already using citigate-commons should not impacted...if I provide flag in application.yml then only it should use this configuration otherwise not


Got it! You want to ensure that services which haven't been migrated yet should **not** be impacted by the new configuration (citigate-commons) unless the flag is explicitly set in `application.yml`. Here’s how you can design this:

### Objective:
1. If a service **has not migrated** to the new Kafka provider (KaaS), the existing configuration (old provider - Fusion) should work as is, and **citigate-commons** Kafka config should not be applied.
2. If a service **has migrated** (by setting the flag in `application.yml`), it will use the **citigate-commons** library to configure Kafka with KaaS, or Fusion, or both.

### Design Steps:

#### 1. Flags in `application.yml` (Service-Specific)
Each service can configure whether it wants to use KaaS, Fusion, or neither by adding flags to its `application.yml`:
```yaml
# In services' application.yml file
kafka:
  kaasKafkaEnabled: true   # If migrating to KaaS
  fusionKafkaEnabled: false # If still using Fusion
```

If neither flag is present, then the existing Kafka configuration will remain unaffected.

#### 2. Conditional Kafka Configuration in citigate-commons
In the common library (`citigate-commons`), you will define a configuration that is **enabled only if** a flag (`kaasKafkaEnabled` or `fusionKafkaEnabled`) is set to `true`. If no flag is set, the default configuration (old Kafka provider) will remain active.

Here’s how to do it:

- Use Spring’s `@ConditionalOnProperty` annotation to check if a flag is set before applying the Kafka configuration.

```java
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    // Common Kafka producer properties
    private Map<String, Object> commonProducerConfigs() {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return configs;
    }

    // Conditional Configuration for KaaS Kafka Template
    @Bean(name = "kaasKafkaTemplate")
    @ConditionalOnProperty(name = "kafka.kaasKafkaEnabled", havingValue = "true", matchIfMissing = false)
    public KafkaTemplate<String, String> kaasKafkaTemplate() {
        Map<String, Object> configs = new HashMap<>(commonProducerConfigs());
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kaas.kafka.bootstrap.servers");
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        return new KafkaTemplate<>(producerFactory);
    }

    // Conditional Configuration for Fusion Kafka Template
    @Bean(name = "fusionKafkaTemplate")
    @ConditionalOnProperty(name = "kafka.fusionKafkaEnabled", havingValue = "true", matchIfMissing = false)
    public KafkaTemplate<String, String> fusionKafkaTemplate() {
        Map<String, Object> configs = new HashMap<>(commonProducerConfigs());
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "fusion.kafka.bootstrap.servers");
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        return new KafkaTemplate<>(producerFactory);
    }
}
```

#### Explanation of the Code:
1. **Conditional Configuration**:
   - `@ConditionalOnProperty(name = "kafka.kaasKafkaEnabled", havingValue = "true")` ensures that the KaaS configuration is only created if `kaasKafkaEnabled: true` is explicitly set in `application.yml`.
   - Similarly, `@ConditionalOnProperty(name = "kafka.fusionKafkaEnabled", havingValue = "true")` ensures the Fusion Kafka configuration is created only if `fusionKafkaEnabled: true` is set.
   - If neither flag is set, **no configuration from `citigate-commons` will be applied**, and the existing setup will remain intact.

2. **No Impact on Unmigrated Services**:
   - Services that don’t add the flags (`kaasKafkaEnabled` or `fusionKafkaEnabled`) will continue to use their old Kafka configurations.
   - Only services that explicitly set the flags in their `application.yml` will use the new configuration from `citigate-commons`.

#### 3. Configuring Multiple Providers:
If a service needs to use both providers (Fusion and KaaS) simultaneously, you can control this with the flags and inject the appropriate `KafkaTemplate` using `@Qualifier`.

For example:
```java
@Autowired
@Qualifier("kaasKafkaTemplate")
private KafkaTemplate<String, String> kaasKafkaTemplate;

@Autowired
@Qualifier("fusionKafkaTemplate")
private KafkaTemplate<String, String> fusionKafkaTemplate;
```

In this way, based on which flags are enabled, you can choose which Kafka provider to use for producing and consuming messages.

### Conclusion:
- **No Flags Set**: If no flags are set in `application.yml`, the services will continue to use their old Kafka configurations without any impact.
- **Flags Set**: When a flag like `kaasKafkaEnabled` or `fusionKafkaEnabled` is set to `true`, the respective Kafka provider (KaaS or Fusion) will be used.
- **Phased Migration**: This allows a phased migration of services, where only the services with the flags enabled will use the new Kafka configuration, while others remain unaffected.

This approach ensures backward compatibility for services that haven’t yet migrated to the new Kafka provider.

