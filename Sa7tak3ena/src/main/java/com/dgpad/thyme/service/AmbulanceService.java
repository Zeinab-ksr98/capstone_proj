package com.dgpad.thyme.service;


import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.repository.AmbulanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AmbulanceService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    public Ambulance save(Ambulance user){
        return ambulanceRepository.save(user);
    }
    public Ambulance createUser(Ambulance user){
        Ambulance newUser = new Ambulance(user.getUsername(),user.getPublicName(), user.getEmail(),passwordEncoder.encode(user.getPassword()), user.getPhone(),user.isAdministrator());
        return save(newUser);
    }
    public List<Ambulance> getAllAmbulances(){
        return ambulanceRepository.findAll();
    }
    public List<Ambulance> getAllCompletedAmbulances(){
        return ambulanceRepository.findAllCompleted();
    }

    public Ambulance getAmbulanceById(UUID id){
        return ambulanceRepository.findById(id).orElse(null);
    }
    public List<Ambulance> getAmbulanceByAgency(AmbulanceAgency agency){
        return  ambulanceRepository.findAllAmbulanceForAgency(agency);
    }
    public List<Ambulance> getAmbulanceByAvailableCarTypeAndAgency(AmbulanceAgency agency,Ambulancetypes type){
        return  ambulanceRepository.getAmbulanceByAvailableCarTypeAndAgency(agency,type);
    }
    public Ambulance update(Ambulance currentuser, Ambulance user){
        if(user.getUsername()!=null)
            currentuser.setUsername(user.getUsername());
        if (user.getPhone()!=null)
            currentuser.setPhone(user.getPhone());
        if (user.getPublicName()!=null)
            currentuser.setPublicName(user.getPublicName());

        return save(currentuser);
    }
}