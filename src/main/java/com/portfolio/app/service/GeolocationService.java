package com.portfolio.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GeolocationService {

    private final RestTemplate restTemplate;

    public GeolocationService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, String> getLocationFromIP(String ipAddress) {
        Map<String, String> location = new HashMap<>();

        try {
            // Using ip-api.com (free tier)
            String url = "http://ip-api.com/json/" + ipAddress + "?fields=country,city,regionName";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "success".equals(response.get("status"))) {
                location.put("country", (String) response.get("country"));
                location.put("city", (String) response.get("city"));
                location.put("region", (String) response.get("regionName"));
            }
        } catch (Exception e) {
            log.warn("Failed to get geolocation for IP: {}", ipAddress, e);
        }

        return location;
    }
}
