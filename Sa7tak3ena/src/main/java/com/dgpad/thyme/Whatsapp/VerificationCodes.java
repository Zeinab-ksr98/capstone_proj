package com.dgpad.thyme.Whatsapp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity()
@Table(name = "verification_codes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    public String number;
    public String code;

    public VerificationCodes(String number, String code){
        this.number = number;
        this.code = code;
    }
}
