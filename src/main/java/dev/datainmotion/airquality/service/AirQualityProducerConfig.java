package dev.datainmotion.airquality.service;

import dev.datainmotion.airquality.model.Observation;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

// See: https://github.com/streamnative/examples/tree/master/spring-pulsar

@Configuration(proxyBeanMethods = false)
public class AirQualityProducerConfig {

    @Bean
    ProducerInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }

    static class LoggingInterceptor implements ProducerInterceptor {
        private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

        @Override
        public void close() {
            // no-op
        }

        @Override
        public boolean eligible(Message message) {
            return true;
        }

        @Override
        public Message beforeSend(Producer producer, Message message) {
            return message;
        }


        @Override
        public void onSendAcknowledgement(
                Producer producer, Message message, MessageId msgId, Throwable exception) {

            String schemaName = "";
            if ( message != null && message.getReaderSchema() != null) {
                try {
                    Optional<JSONSchema<Observation>> schema = message.getReaderSchema();

                    if ( schema.isPresent() && !schema.isEmpty()) {
                        JSONSchema<Observation>schemaValue = schema.get();
                        schemaName = schemaValue.getSchemaInfo().getSchemaDefinition().toString();
                    }
                } catch (Exception e) {
                   log.error("Schema Parsing Error", e);
                }
            }
            log.info("====Ack Producer: {}, MessageId: {}, Key: {}, Pub Time: {}, Schema: {}, Value: {}",
                    message.getProducerName(),
                    message.getMessageId(),
                    message.getKey(),
                    message.getPublishTime(),
                    schemaName,
                    message.getValue());
        }
    }
}