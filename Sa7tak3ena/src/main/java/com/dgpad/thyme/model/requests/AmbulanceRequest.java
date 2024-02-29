package com.dgpad.thyme.model.requests;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    in case of transfer it is called from in case of house service it is location
    @Column(name = "transport_from")
    public String from;
    //    in case of transfer
    @Column(name = "transport_to")
    public String to;
    public AmbulanceRequestStatus status;
    @Column(name = "service_type")
    public Ambulanceservice service;
    @Column(name = "details")
    public String description;
    @Column(name = "car_used")
    public Ambulancetypes car_type;
    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;
    @OneToOne
    private Hospital hospital;
//    sender can be either a hospital or a patient
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    private LocalDateTime createdAt;
}
