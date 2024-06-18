package com.dgpad.thyme.service;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
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
    private UserService userService;

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
    public List<Hospital> getAllHospitalsOrderbyactive(){
        return hospitalRepository.getAllHospitalsOrderByEnabled();
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
    public List<Hospital> findHospitalsWithAvailableBedCategory(String categoryName) {
        return hospitalRepository.findHospitalsWithAvailableCategoryBeds(categoryName);
    }
    public int getBedCountForCategoryinHospital(UUID hospitalId, String categoryName) {
        Integer bedCount = hospitalRepository.getBedCountForCategoryinHospital(hospitalId, categoryName);
        return bedCount != null ? bedCount : 0;
    }


    public List<Patient> findPatientsByHospital(){
            return hospitalRepository.findPatientsByHospital(userService.getCurrentUser().id);
    }
    public int[] calculateAgeDistribution() {
        List<Patient> patientList=findPatientsByHospital();
        if (patientList == null)
                return null;
        int[] ageCounts = new int[4];

        for (Patient patient : patientList) {
            if (patient.getAge() < 18) {
                ageCounts[0]++;
            } else if (patient.getAge() >= 18 && patient.getAge() <= 30) {
                ageCounts[1]++;
            } else if (patient.getAge() > 30 && patient.getAge() <= 50) {
                ageCounts[2]++;
            } else {
                ageCounts[3]++;
            }
        }
        return ageCounts;
    }

    public int[] calculateGenderDistribution() {
        List<Patient> patientList=findPatientsByHospital();
        if (patientList == null)
            return null;
        int maleCount = 0, femaleCount = 0;
        for (Patient patient : patientList) {
            if (patient.getGender()== Gender.Male) {
                maleCount++;
            } else if (patient.getGender() == Gender.Female) {
                femaleCount++;
            }
        }
        return new int[]{maleCount, femaleCount};
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

    public List<Hospital> filterByDistrict(String district) {
        return hospitalRepository.findByAddressRegion(district);
    }
}