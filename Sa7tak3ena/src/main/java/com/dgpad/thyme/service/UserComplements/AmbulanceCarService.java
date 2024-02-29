package com.dgpad.thyme.service.UserComplements;

import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.repository.AmbulanceCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AmbulanceCarService {
    @Autowired
    private AmbulanceCarRepository ambulanceCarRepository;

    public AmbulanceCar save(AmbulanceCar car){
        return ambulanceCarRepository.save(car);
    }
    public List<AmbulanceCar> getAllCars(){
        return ambulanceCarRepository.findAll();
    }

    public List<AmbulanceCar> findAllUserCarByStatus(AmbulanceStatus status, UUID ambulance_id){
        return ambulanceCarRepository.findAllUserCarByStatus(status,ambulance_id);
    }

    public List<AmbulanceCar> findAllUserTypedCarsByStatus(AmbulanceStatus status, UUID ambulance_id, Ambulancetypes type){
        return ambulanceCarRepository.findAllUserTypedCarsByStatus(status,ambulance_id,type);
    }
    public AmbulanceCar getCarById(int id) {
        return ambulanceCarRepository.findById(id).orElse(null);
    }
    public void deleteCar(int id) {

        ambulanceCarRepository.delete(getCarById(id));
    }
    public AmbulanceCar updateStatus(int id, AmbulanceStatus status){
        AmbulanceCar car = getCarById(id);
        car.setStatus(status);
        return save(car);
    }

}
