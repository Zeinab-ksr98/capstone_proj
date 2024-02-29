package com.dgpad.thyme.service;


import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.HospitalRepository;
import com.dgpad.thyme.repository.PatientRepository;
import com.dgpad.thyme.repository.UserRepository;
import com.dgpad.thyme.security.UserInfoDetails;
import com.dgpad.thyme.service.UserComplements.BedsService;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BedsService bedsService;

    public Hospital save(Hospital user){
        return hospitalRepository.save(user);
    }
    public Hospital createUser(Hospital user){
        Hospital newUser = new Hospital(user.getUsername(),user.getPublicName(), user.getEmail(),passwordEncoder.encode(user.getPassword()), user.getPhone(), user.isAdministrator());
        return save(newUser);
    }
    public List<Hospital> getAllHospitals(){
        return hospitalRepository.findAll();
    }
    public List<Hospital> getAllEnabledHospitals(){
        return hospitalRepository.getAllEnabledHospitals();
    }

    public Hospital getHospitalById(UUID id){
        return hospitalRepository.findById(id).orElse(null);
    }
    public Hospital getHospitalByBedId(int id){
        List<Hospital> hospitals =getAllHospitals();
        for (int i = 0; i < hospitals.size(); i++) {
            List<Beds> beds =hospitals.get(i).getAvailableBeds();
            for (int h = 0; h < beds.size(); h++) {
                if (beds.get(i).getId()==id){
                    return hospitals.get(i);
                }
            }
        }
        return null;
    }
    public List<Hospital> findHospitalsWithAvailableEmergencyBeds(String categoryName) {
        return hospitalRepository.findHospitalsWithAvailableEmergencyBeds(categoryName);
    }

    public Hospital update(Hospital currentuser,Hospital user){
        if (user.getPublicName()!=null)
            currentuser.setPublicName(user.getPublicName());
        if (user.getAddress()!=null)
            currentuser.setAddress(user.getAddress());
        if (user.getManagerName()!=null)
            currentuser.setManagerName(user.getManagerName());
        if (user.getManagerPhone()!=null)
            currentuser.setManagerPhone(user.getManagerPhone());
        if (user.getSupervisingPhysicianName()!=null)
            currentuser.setSupervisingPhysicianName(user.getSupervisingPhysicianName());
        if (user.getUsername()!=null)
            currentuser.setUsername(user.getUsername());
        if (user.getEmail()!=null)
            currentuser.setEmail(user.getEmail());
        if (user.getPassword()!=null)
            currentuser.setPassword(user.getPassword());
        if (user.getPhone()!=null)
            currentuser.setPhone(user.getPhone());
        return save(currentuser);
    }

}