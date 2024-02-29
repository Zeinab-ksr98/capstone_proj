package com.dgpad.thyme.model.usercomplements;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Beds {
    @Id
    @Column(name = "bed_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int nbBeds;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category is required")
    private BedCategory category;

}
