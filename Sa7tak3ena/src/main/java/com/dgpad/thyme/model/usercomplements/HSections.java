package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.users.Hospital;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HSections {
    @Id
    @Column(name = "section_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

}
