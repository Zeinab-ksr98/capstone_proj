package com.dgpad.thyme.service.UserComplements;

import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.repository.AmbulanceCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public AmbulanceCar getCarById(int id) {
        return ambulanceCarRepository.findById(id).orElse(null);
    }
    public void deleteCar(int id) {

        ambulanceCarRepository.delete(getCarById(id));
    }
}
