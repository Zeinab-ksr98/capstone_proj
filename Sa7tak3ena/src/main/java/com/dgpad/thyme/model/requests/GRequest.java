package com.dgpad.thyme.model.requests;

import com.dgpad.thyme.model.users.Hospital;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GRequest {
    private Request request;
    private String preferedhospitals;
    private String preferedagencies;

}
