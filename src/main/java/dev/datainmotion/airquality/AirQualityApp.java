package dev.datainmotion.airquality;

import dev.datainmotion.airquality.model.Observation;
import dev.datainmotion.airquality.service.AirQualityService;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.core.PulsarProducerFactory;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.pulsar.config.*;
import org.springframework.pulsar.listener.Acknowledgement;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.UUID;

/**
 * example spring boot app to read rest feed send to Pulsar
 */

@EnableScheduling
@SpringBootApplication
public class AirQualityApp implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AirQualityApp.class);

    @Autowired
    private AirQualityService airQualityService;

    @Value("${spring.pulsar.producer.topic-name:airquality}")
    String topicName;

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AirQualityApp.class, args);
    }

    @Autowired
    private PulsarTemplate<Observation> pulsarTemplate;

    /**
     * get rows
     */
    private void getRows() {
        pulsarTemplate.setSchema(Schema.JSON(Observation.class));
        List<Observation> obsList = airQualityService.fetchCurrentObservation();

        if (obsList == null || obsList.size() <= 0) {
            return;
        }
        log.debug("Count: {}", obsList.size());

        for (Observation observation2 : obsList) {
            log.info("{}={} for {} {}",
                    observation2.getParameterName(),
                    observation2.getAqi(),
                    observation2.getStateCode(),
                    observation2.getReportingArea());
            try {
                UUID uuidKey = UUID.randomUUID();
                MessageId msgid = pulsarTemplate.newMessage(observation2)
                        .withMessageCustomizer((mb) -> mb.key(uuidKey.toString()))
                        .withTopic(topicName)
                        .send();

                log.info("Sent {}", observation2.toString());
                log.debug("PULSAR MSGID {}", msgid.toString());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Pulsar Error", e);
            }
        }
    }

    @Scheduled(initialDelay = 0, fixedRate = 2000)
    public void repeatRun() {
        getRows();
    }

    @Override
    public void run(String... args) {
        getRows();
    }
}