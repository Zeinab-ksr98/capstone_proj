package com.dgpad.thyme.model.users;

import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.usercomplements.Block;
import com.dgpad.thyme.model.usercomplements.HSections;
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
public class Hospital extends User {
    @ManyToOne
    private Address address;
    private String publicName;
    private String Fax;

    @OneToMany(mappedBy = "hospital")
    private List<HSections> hospitalSections;

    private String managerName;
    private String managerPhone;
    private String SupervisingPhysicianName;
    private String SupervisingPhysicianPhone;
    private String CONSTRUCTION_AUTH_NB; //Construction Authorization Nb
    @OneToOne
    private Block block;
    @OneToMany
    private List<AmbulanceRequest> ambulanceRequest;

    @OneToMany
    public List<Reservation> reservations;

    @OneToMany
    public List<Request> requests;
    @OneToMany
    private List<Beds> availableBeds;

    public Hospital(String username,String publicName, String email, String password, String phone,boolean administrator) {
        super(username, email, password, phone, Role.HOSPITAL,administrator);
        this.address= null;
        this.hospitalSections=new ArrayList<>();
        this.publicName=publicName;
        this.managerName=null;
        this.managerPhone=null;
        this.SupervisingPhysicianName=null;
        this.SupervisingPhysicianPhone=null;
        this.ambulanceRequest=new ArrayList<>();
        this.reservations=new ArrayList<>();
        this.requests=new ArrayList<>();
        this.availableBeds=new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "address=" + address +
                "Phone="+getPhone()+
                ", publicName='" + publicName + '\'' +
                ", Fax='" + Fax + '\'' +
                ", hospitalSections=" + hospitalSections +
                ", managerName='" + managerName + '\'' +
                ", managerPhone='" + managerPhone + '\'' +
                ", SupervisingPhysicianName='" + SupervisingPhysicianName + '\'' +
                ", SupervisingPhysicianPhone='" + SupervisingPhysicianPhone + '\'' +
                ", CONSTRUCTION_AUTH_NB='" + CONSTRUCTION_AUTH_NB + '\'' +
                ", block=" + block +
                ", ambulanceRequest=" + ambulanceRequest +
                ", reservations=" + reservations +
                ", requests=" + requests +
                ", availableBeds=" + availableBeds +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
