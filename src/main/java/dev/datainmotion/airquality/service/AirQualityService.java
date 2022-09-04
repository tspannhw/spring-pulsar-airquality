package dev.datainmotion.airquality.service;

import dev.datainmotion.airquality.model.Observation;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 
 */
@Service
public class AirQualityService {
    private static final Logger log = LoggerFactory.getLogger(AirQualityService.class);

    private static final String DEFAULT_ZIPCODE = "08520";

    @Value("${zipcodes}")
    String[] arrayOfStrings;

    @Value("${airnowapi.url:http://localhost:8080}")
    String airnowapi;

    @Value("${airnowapi.uri}")
    String airnowapiuri;

    @Value("${airnowapi.api.key}")
    String apikey;

    @Value("${airnowapi.key.name}")
    String airnowapikeyname;

    /**
     *
     * @return
     */
    public String getURI() {
        Random rand = new Random();
        String zipCode = null;

        try {
            zipCode= arrayOfStrings[rand.nextInt(arrayOfStrings.length)];
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( zipCode == null || zipCode.trim().length() <= 0) {
            zipCode = arrayOfStrings[0];
        }

        if ( zipCode == null || zipCode.trim().length() <= 0) {
            zipCode = DEFAULT_ZIPCODE;
        }

        return new StringJoiner("", "", "")
                .add(airnowapi)
                .add(airnowapiuri)
                .add(zipCode)
                .add("&API_KEY=")
                .add(apikey)
                .toString();
    }

    @Autowired
    WebClient webClient;

    /**
     * 
     * @return list of observations from json
     */
    public List<Observation> fetchCurrentObservation() {
        List<Observation> obs = null;

        try {
            Flux<Observation> observationFlux = webClient
                    .get()
                    .uri(getURI())
                    .retrieve()
                    .bodyToFlux(Observation.class)
                    .timeout(Duration.ofMillis(10_000));

            obs = observationFlux
                    .collect(Collectors.toList())
                    .share().block();
        } catch (Throwable t) {
            log.error("fetch failed", t);
        }

        return obs;
    }
}