package com.dgpad.thyme.model;

import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.enums.ReservationType;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private ReservationType reservationType;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private String MedicalRecord;
    //entered by the hospital after reservation
    private int bednum;
    private int floornum;

    @ManyToMany
    private List<AmbulanceRequest> ambulanceRequests=new ArrayList<>();

//    filled when ambulance accept the request
    @OneToOne
    private AmbulanceCar ambulanceCar;
    private LocalDateTime CreatedAt;

    public Reservation(int bednum) {
        this.bednum = bednum;
    }
}
