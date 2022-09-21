package dev.datainmotion.airquality;

import java.util.List;
import java.util.UUID;

import dev.datainmotion.airquality.model.Observation;
import dev.datainmotion.airquality.service.AirQualityService;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.autoconfigure.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.pulsar.core.PulsarTopic;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static org.apache.pulsar.client.api.SubscriptionType.*;

/**
 * example spring boot app to read rest feed send to Pulsar
 */

@EnableScheduling
@SpringBootApplication
public class AirQualityApp {

	private static final Logger log = LoggerFactory.getLogger(AirQualityApp.class);
	public static final String ERRORMSG1 = "No observations found or failed or outrun our 500 allotment.";

	@Autowired
    private AirQualityService airQualityService;

	@Autowired
	private PulsarTemplate<Observation> pulsarTemplate;

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AirQualityApp.class, args);
    }

	@Scheduled(initialDelay = 10, fixedRate = 10000)
    public void getRows() {
		this.pulsarTemplate.setSchema(Schema.JSON(Observation.class));
		List<Observation> observations = airQualityService.fetchCurrentObservation();
        if (observations == null || observations.size() <= 0) {
			log.debug(ERRORMSG1);
            return;
        }
        log.debug("Count: {}", observations.size());
		observations.forEach((observation) -> {
			log.debug("{}={} for {} {}",
					observation.getParameterName(),
					observation.getAqi(),
					observation.getStateCode(),
					observation.getReportingArea());
			try {
				UUID uuidKey = UUID.randomUUID();
				MessageId msgid = pulsarTemplate.newMessage(observation)
						.withMessageCustomizer((mb) -> mb.key(uuidKey.toString()))
						.send();
				log.debug("MSGID Sent: {}", msgid.toString());
			}
			catch (Throwable e) {
				log.error("Pulsar Error", e);
			}
		});
    }

	@PulsarListener(subscriptionName = "aq-spring-reader", subscriptionType = Shared, schemaType = SchemaType.JSON, topics = "persistent://public/default/aq-pm25")
	void echoObservation(Observation message) {
		this.log.info("PM2.5 Message received: {}", message);
	}

	@PulsarListener(subscriptionName = "pm10-spring-reader", subscriptionType = Shared, schemaType = SchemaType.JSON, topics = "persistent://public/default/aq-pm10")
	void echoObservation2(Observation message) {
		this.log.info("PM10 Message received: {}", message);
	}

	@PulsarListener(subscriptionName = "ozone-spring-reader", subscriptionType = Shared, schemaType = SchemaType.JSON, topics = "persistent://public/default/aq-ozone")
	void echoObservation3(Observation message) {
		this.log.info("Ozone Message received: {}", message);
	}
}
