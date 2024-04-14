package com.dgpad.thyme.service;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.repository.HospitalRepository;
import com.dgpad.thyme.service.UserComplements.BedsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Comparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Hospital> getAllHospitalsSortedByReservationSize(){
        return hospitalRepository.getAllHospitalsSortedByReservationSize();
    }

    public Hospital getHospitalById(UUID id){
        return hospitalRepository.findById(id).orElse(null);
    }
    public int getTotalBeds(List<Beds> beds){
        int sum=0;
        for ( Beds bed:beds) {
            sum+=bed.getNbBeds();
        }
        return sum;
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
    public List<AmbulanceRequest> getAllAmbulanceRequestsForCustomerWithin24hs(UUID userId){
        // Retrieve all reservations for the user
        List<AmbulanceRequest> allRequest = getHospitalById(userId).getAmbulanceRequest();

        // Filter reservations within the last 24 hours
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<AmbulanceRequest> requestsWithin24Hours = allRequest.stream()
                .filter(reservation -> reservation.getCreatedAt().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());
        // Add both lists to the model
        return requestsWithin24Hours;
    }


    public Hospital update(Hospital currentuser,Hospital user){
        if(user.getUsername()!=null)
            currentuser.setUsername(user.getUsername());
        if (user.getPublicName()!=null)
            currentuser.setPublicName(user.getPublicName());
        if (user.getPhone()!=null)
            currentuser.setPhone(user.getPhone());

        if (user.getManagerPhone()!=null)
            currentuser.setManagerPhone(user.getManagerPhone());
        if (user.getManagerName()!=null)
            currentuser.setManagerName(user.getManagerName());

        if (user.getSupervisingPhysicianName()!=null)
            currentuser.setSupervisingPhysicianName(user.getSupervisingPhysicianName());
        if (user.getSupervisingPhysicianPhone()!=null)
            currentuser.setSupervisingPhysicianPhone(user.getSupervisingPhysicianPhone());

        return save(currentuser);
    }

}