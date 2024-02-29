package com.dgpad.thyme.model.users;
import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.enums.Insurance;
import com.dgpad.thyme.model.enums.Nationality;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient extends User {
    private String FirstName;
    private String LastName;
    private String identityCardImage;//passport or id
    private String selfie;
    private int age;
    private boolean verified;
    private boolean verifiedPhone;
    @Enumerated(EnumType.STRING)
    private Gender gender;
// can enums be added by user as others?
    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @Enumerated(EnumType.STRING)
    private Insurance insurance;
    @OneToMany
    private List<Reservation> reservations;

    @OneToMany (mappedBy = "patient", cascade = CascadeType.ALL,orphanRemoval = true)
    public List<Request> requests;

    public Patient(String username,String firstName,String Lastname, String email, String password, String phone,Gender gender) {
        super(username, email, password, phone,Role.PATIENT);
        setVerified(false);
        this.FirstName=firstName;
        this.LastName=Lastname;
        this.identityCardImage=null;
        this.selfie=null;
        this.age=0;
        this.gender=gender;
        this.nationality=null;
        this.insurance=null;
        this.reservations=new ArrayList<>();
        this.requests=new ArrayList<>();
    }
    public Patient(String number, String password, boolean verified){
        setPhone(number);
        this.password = password;
        this.verifiedPhone = verified;
    }
}