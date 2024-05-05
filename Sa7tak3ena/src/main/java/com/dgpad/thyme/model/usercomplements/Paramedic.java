package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.model.users.Ambulance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private String phone;
    @ManyToOne
    public Image image;
    boolean inactive ;
    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;

}
