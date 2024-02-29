package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.users.Ambulance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AmbulanceCar {
    @Id
    @Column(name = "ambulanceTypeId")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private Ambulancetypes type;
    private AmbulanceStatus status;

    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;
}
