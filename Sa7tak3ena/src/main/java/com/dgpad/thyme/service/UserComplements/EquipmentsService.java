package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.usercomplements.Equipments;
import com.dgpad.thyme.repository.BedsRepository;
import com.dgpad.thyme.repository.EquipmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class EquipmentsService {
    @Autowired
    private EquipmentsRepository equipmentsRepository;

    public Equipments save(Equipments equipment){
        return equipmentsRepository.save(equipment);
    }
    public void deleteEquipment(int id) {
        equipmentsRepository.deleteById(id);
    }
    public Equipments getEquipmentById(int id) {
        return equipmentsRepository.findById(id).orElse(null);
    }
    public List<Equipments> getAllEquipments(){
        return equipmentsRepository.findAll();
    }

}

