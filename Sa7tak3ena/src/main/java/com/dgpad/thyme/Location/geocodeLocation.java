package com.dgpad.thyme.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class geocodeLocation {
    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;

}
