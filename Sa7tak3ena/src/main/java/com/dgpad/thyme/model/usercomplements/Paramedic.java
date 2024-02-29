package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.users.Ambulance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Paramedic {
    @Id
    @Column(name = "Paramedic_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;

}
