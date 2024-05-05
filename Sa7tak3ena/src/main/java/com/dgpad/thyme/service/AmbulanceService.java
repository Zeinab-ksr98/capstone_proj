package com.dgpad.thyme.service;


import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.Equipments;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.repository.AmbulanceRepository;
import com.dgpad.thyme.repository.AmbulanceRequestRepository;
import com.dgpad.thyme.service.UserComplements.AmbulanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<Equipments> getEquipmentsWithinMonthForAmbulance(Ambulance ambulance) {
        // Get the current date and time
        LocalDateTime currentDate = LocalDateTime.now();
        // Subtract one month from the current date
        LocalDateTime oneMonthAgo = currentDate.minusMonths(1);
        // Query the repository to retrieve equipments created within the past month for the specified ambulance
        return ambulanceRepository.getEquipmentsWithinMonthForAmbulance(ambulance, oneMonthAgo);
    }

    public Map<Month, Integer> getTotalEquipmentPricesWithinMonthForAmbulance(Ambulance ambulance) {
        List<Equipments> equipments = ambulance.getEquipmentsList();

        // Map to store total prices for each month
        Map<Month, Integer> totalPriceMap = new HashMap<>();

        // Calculate total prices for each month
        for (Equipments equipment : equipments) {
            LocalDateTime createdAt = equipment.getCreatedAt();
            int price = equipment.getPrice();

            // Get the month part of the creation date
            Month month = createdAt.getMonth();

            // Update total price for the month
            totalPriceMap.put(month, totalPriceMap.getOrDefault(month, 0) + price);
        }

        return totalPriceMap;
    }




    public List<Ambulance> getAllAmbulances(){
        return ambulanceRepository.findAll();
    }
    public List<Ambulance> getAllCompletedAmbulances(){
        return ambulanceRepository.findAllCompleted();
    }
    public List<Ambulance> getAllCompletedAmbulancesinRegion(String region){
        return ambulanceRepository.getAllCompletedAmbulancesinRegion(region);
    }

    public Ambulance getAmbulanceById(UUID id){
        return ambulanceRepository.findById(id).orElse(null);
    }
    public List<Ambulance> getAmbulanceByAgency(AmbulanceAgency agency){
        return  ambulanceRepository.findAllAmbulanceForAgency(agency);
    }
    public List<Ambulance> getcompletedAmbulanceByAgency(AmbulanceAgency agency){
        return  ambulanceRepository.findAllCompletedAmbulanceForAgency(agency);
    }
    public List<Ambulance> getAmbulanceByAvailableCarType(Ambulancetypes type){
        return  ambulanceRepository.getAmbulanceByAvailableCarType(type);
    }
    public List<Ambulance> getAmbulanceByAvailableCarTypeAndRegion(Ambulancetypes type,String regin){
        return  ambulanceRepository.getAmbulanceByAvailableCarTypeInRegion(type, regin);
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