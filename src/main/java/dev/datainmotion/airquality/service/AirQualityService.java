package dev.datainmotion.airquality.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dev.datainmotion.airquality.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 
 */
@Service
@EnableConfigurationProperties(AirQualityProperties.class)
public class AirQualityService {
	private static final Logger log = LoggerFactory.getLogger(AirQualityService.class);

	private final WebClient webClient;

	private final AirQualityProperties apiProperties;

	public AirQualityService(WebClient.Builder webClientBuilder, AirQualityProperties apiProperties) {
		this.webClient = webClientBuilder
				.baseUrl(apiProperties.getBaseUrl())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
		this.apiProperties = apiProperties;
	}

    /**
     * 
     * @return list of observations from json
     */
    public List<Observation> fetchCurrentObservation() {
		/*
		 If you want to pull the web out of the equation use this hardcoded return below instead for testing.
		Observation o1 = new Observation();
		o1.setAqi(5);
		o1.setParameterName("thingy");
		o1.setStateCode("tx");
		o1.setReportingArea("aus");
		return Collections.singletonList(o1);
		*/
		try {
			return webClient
					.get()
					.uri(apiProperties.getAirqualityUri(), nextZipCodeRandomly(), apiProperties.getApiKey())
					.retrieve()
					.bodyToFlux(Observation.class)
					.timeout(Duration.ofSeconds(10))
					.collect(Collectors.toList())
					.block();
		} catch (Exception ex) {
			log.error("Failed to retrieve observations from AirNow due to: " + ex.getMessage(), ex);
			return Collections.emptyList();
		}
    }

	/**
	 *
	 * @return
	 */
	public String nextZipCodeRandomly() {
		Random rand = new Random();
		return apiProperties.getZipCodes().get(rand.nextInt(apiProperties.getZipCodes().size()));
	}
}