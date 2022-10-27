package dev.datainmotion.airquality.service;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("airnowapi")
public class AirQualityProperties {

	private final String baseUrl;
	private final String airqualityUri;
	private final String apiKey;
	private final List<String> zipCodes;

	public AirQualityProperties(
			@DefaultValue("http://localhost:${server.port}") String baseUrl,
			String airqualityUri,
			String apiKey,
			@DefaultValue("08520") List<String> zipCodes) {
		this.baseUrl = baseUrl;
		this.airqualityUri = airqualityUri;
		this.apiKey = apiKey;
		this.zipCodes = zipCodes;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getAirqualityUri() {
		return airqualityUri;
	}

	public String getApiKey() {
		return apiKey;
	}

	public List<String> getZipCodes() {
		return zipCodes;
	}
}
