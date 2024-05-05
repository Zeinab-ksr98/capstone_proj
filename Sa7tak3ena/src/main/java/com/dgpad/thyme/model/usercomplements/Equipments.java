package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.users.Ambulance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Equipments {
    @Id
    @Column(name = "Equipment_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
    private  int price;
    private LocalDateTime CreatedAt;

    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;
}
