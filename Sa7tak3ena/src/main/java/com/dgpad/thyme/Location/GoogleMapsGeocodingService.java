//package com.dgpad.thyme.Location;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class GoogleMapsGeocodingService {
//
//    @Value("${google.maps.apiKey}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate;
//
//    public GoogleMapsGeocodingService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public geocodeResponse geocode(String address) {
//        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json"
//                + "?address=" + address
//                + "&key=" + apiKey;
//
//        return restTemplate.getForObject(apiUrl, geocodeResponse.class);
//    }
//}
//
//
