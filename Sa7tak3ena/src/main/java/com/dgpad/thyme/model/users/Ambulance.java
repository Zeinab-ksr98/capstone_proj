package com.dgpad.thyme.model.users;
import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ambulance extends User {
    //his address
    @ManyToOne
    private Address address;
    //agency name-
    private String publicName;
    @ManyToOne
    @JoinColumn(name = "ambulanceAgency_Id")
    private AmbulanceAgency Agency;

    @OneToMany(mappedBy = "ambulance")
    private List<Paramedic> ParamedicList;

    @OneToMany(mappedBy = "ambulance")
    public List<Equipments> equipmentsList ;

    @OneToMany(mappedBy = "ambulance", cascade = CascadeType.ALL)
    private List<AmbulanceCar> ambulanceCars;
    //there was an array of ambulanceAgency type but i do not know for what it is
    //reservations from hospital to be able to confirm any request he will take
    @OneToMany
    private List<Reservation> reservations;

    //his requests before reservation of the patient in the hospital
    @OneToMany(mappedBy = "ambulance", cascade = CascadeType.ALL)
    private List<AmbulanceRequest> ambulanceRequests;
    public Ambulance(String username, String publicName,String email, String password,String phone,boolean administrator) {
        super(username, email, password, phone, Role.AMBULANCE, administrator);
        this.publicName=publicName;
        this.address=null;
        this.Agency= null;
        this.ambulanceCars = new ArrayList<>();
        this.reservations=new ArrayList<>();
        this.ambulanceRequests=new ArrayList<>();
    }
}
