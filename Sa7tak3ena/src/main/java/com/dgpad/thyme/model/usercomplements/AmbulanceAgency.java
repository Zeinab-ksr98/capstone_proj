package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.users.Ambulance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceAgency {
    @Id
    @Column(name = "ambulanceAgency_Id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
}
