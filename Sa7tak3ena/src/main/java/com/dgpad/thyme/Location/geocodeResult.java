package com.dgpad.thyme.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class geocodeResult {
    @JsonProperty("formatted_address")
    private String formattedAddress;

    @JsonProperty("geometry")
    private geocodeGeometry geometry;

}
