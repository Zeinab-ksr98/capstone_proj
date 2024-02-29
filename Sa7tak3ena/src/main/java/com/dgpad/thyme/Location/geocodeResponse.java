package com.dgpad.thyme.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class geocodeResponse {
    @JsonProperty("results")
    private List<geocodeResult> results;

    @JsonProperty("status")
    private String status;

    // Getters and Setters
}

