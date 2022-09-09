package dev.datainmotion.airquality.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datainmotion.airquality.model.Observation;
import dev.datainmotion.airquality.service.AirQualityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 *
 */
public class DataUtility {
    private static final Logger log = LoggerFactory.getLogger(DataUtility.class);
    /**
     * build json output
     * @param  observation   observation as an object
     * @return String device as JSON String
     */
    public static String serializeToJSON(Observation observation) {
        String jsonValue = "";
        try {
            if (observation != null) {
                ObjectMapper mapper = new ObjectMapper();
                jsonValue = mapper.writeValueAsString(observation);
            }
        } catch (Throwable t) {
            log.error("serializer error", t);
        }
        return jsonValue;
    }

    /**
     * device to json
     * @param observation   observation
     * @return byte[] json String
     */
    public static byte[] serialize(Observation observation) {
        if ( observation == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(observation);
        } catch (JsonProcessingException e) {
            log.error("json error", e);
        }
        if ( json == null) {
            json = "{}";
        }

        return String.format(Locale.US, "%s", json).getBytes();
    }
}