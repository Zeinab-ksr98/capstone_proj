package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    @Id
    @Column(name = "accountRequest_Id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private Role Type;//all roles except patient
    private String companyName;
    private String publicName;
    @Email
    @Column(nullable = false, unique = true)
    private String email;


}
