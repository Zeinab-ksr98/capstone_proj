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
public class BedCategory {
    @Id
    @Column(name = "BedCategory_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
}
