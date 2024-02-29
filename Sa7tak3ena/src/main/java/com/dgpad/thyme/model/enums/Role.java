package com.dgpad.thyme.model.enums;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    HOSPITAL,AMBULANCE,PATIENT, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
