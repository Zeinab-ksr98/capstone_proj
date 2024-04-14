package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.enums.Distracts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    private String name;
    private String REGION;

    public Address(String name) {
        this.name = name;
    }
}
